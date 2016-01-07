package com.schedule.risebes.schedule.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.schedule.risebes.schedule.R;
import com.schedule.risebes.schedule.rest.Groups;
import com.schedule.risebes.schedule.rest.NewUser;
import com.schedule.risebes.schedule.server.ServerCommand;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class RegistrationActivity extends AppCompatActivity implements TextWatcher {
    private Groups group;
    private View mProgressView;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mName;
    private EditText mSurname;
    private Spinner mGroup;
    private Button buttonReg;

    private UserRegistration userRegistration = new UserRegistration();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initFields();
        regListeners();
        mProgressView = findViewById(R.id.reg_progress);
        showProgress(true);
        new GetGroups().execute();
    }

    private void initFields()
    {
        buttonReg= (Button) findViewById(R.id.button_reg);
        mEmail = (EditText) findViewById(R.id.reg_email);
        mPassword = (EditText) findViewById(R.id.reg_password);
        mName = (EditText) findViewById(R.id.reg_name);
        mSurname = (EditText) findViewById(R.id.reg_surname);
        mGroup = (Spinner) findViewById(R.id.reg_group);
    }

    private void regListeners()
    {
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(testFieldsOfValid()) {
                    showProgress(true);
                    userRegistration.execute();
                }
            }
        });
    }
    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private boolean testFieldsOfValid()
    {
        View focusView = null;
        boolean cancel = false;
        if (TextUtils.isEmpty(mEmail.getText())) {
            mEmail.setError(getString(R.string.error_field_required));
            focusView = mEmail;
            cancel = true;
        }
        if (TextUtils.isEmpty(mPassword.getText())) {
            mPassword.setError(getString(R.string.error_field_required));
            focusView = mPassword;
            cancel = true;
        }
        if (TextUtils.isEmpty(mName.getText())) {
            mName.setError(getString(R.string.error_field_required));
            focusView = mName;
            cancel = true;
        }
        if (TextUtils.isEmpty(mSurname.getText())) {
            mSurname.setError(getString(R.string.error_field_required));
            focusView = mSurname;
            cancel = true;
        }
        if (mGroup.getSelectedItem()==null) {
            focusView = mGroup;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return false;
        }
        return true;
    }
    private NewUser createUserFromForm()
    {
        NewUser user = new NewUser();
        user.setEmail(mEmail.getText().toString());
        user.setPassword(mPassword.getText().toString());
        user.setName(mName.getText().toString());
        user.setSurename(mSurname.getText().toString());
        user.setGroup(getSelectedGroupId());
        return user;
    }
    private Integer getSelectedGroupId()
    {
        String groupName = mGroup.getSelectedItem().toString();
        for(Map.Entry<Integer,String> entry:group.getGroups().entrySet())
        {
            if(entry.getValue().trim().equalsIgnoreCase(groupName))
            {
                return entry.getKey();
            }
        }
        return null;
    }

    private class GetGroups extends AsyncTask<Void, Void, Groups>
    {
        @Override
        protected Groups doInBackground(Void... params) {
            try {
                final String url = ServerCommand.getGroupsString();
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                return restTemplate.getForObject(url, Groups.class);
            } catch (HttpClientErrorException e) {

            } catch (ResourceAccessException e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Groups groups) {
            if(groups!=null) {
                group = groups;
                Spinner autoCompleteGroup;
                String[] listOfGroups = new String[groups.getGroups().size()];
                groups.getGroups().values().toArray(listOfGroups);
                autoCompleteGroup = (Spinner) findViewById(R.id.reg_group);
                autoCompleteGroup.setAdapter(new ArrayAdapter<>(RegistrationActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, listOfGroups));
            }else
            {
                //TODO Make some info message
            }
            showProgress(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private class UserRegistration extends AsyncTask<Void, Void, NewUser>
    {
        @Override
        protected NewUser doInBackground(Void... params) {
            try {
                NewUser user = createUserFromForm();
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setContentType(new MediaType("application", "json"));
                HttpEntity<NewUser> requestEntity = new HttpEntity<>(user, requestHeaders);

                final String url = ServerCommand.getAddUserString();
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                restTemplate.exchange(url, HttpMethod.POST, requestEntity, NewUser.class);
                return user;
            } catch (Exception e) {
                Log.e("RegistrationActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(NewUser groups) {
            showProgress(false);
            finish();
            Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_LONG).show();
        }
    }
}
