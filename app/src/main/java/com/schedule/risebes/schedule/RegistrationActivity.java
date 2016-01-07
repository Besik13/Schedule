package com.schedule.risebes.schedule;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.schedule.risebes.schedule.rest.Groups;
import com.schedule.risebes.schedule.rest.NewUser;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class RegistrationActivity extends AppCompatActivity implements TextWatcher {
    Groups group;

    EditText mEmail;
    EditText mPassword;
    EditText mName;
    EditText mSurname;
    Spinner mGroup;
    Button buttonReg;

    private UserRegistration userRegistration = new UserRegistration();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initFields();
        regListeners();
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
                userRegistration.execute();
            }
        });
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
                final String url = "http://192.168.0.100:8080/groups";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                return restTemplate.getForObject(url, Groups.class);
            } catch (Exception e) {
                Log.e("SettingsActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Groups groups) {
            group = groups;
            Spinner autoCompleteGroup;
            String[] listOfGroups = new String[groups.getGroups().size()];
            groups.getGroups().values().toArray(listOfGroups);
            autoCompleteGroup = (Spinner) findViewById(R.id.reg_group);
            autoCompleteGroup.setAdapter(new ArrayAdapter<>(RegistrationActivity.this,
                    android.R.layout.simple_spinner_dropdown_item, listOfGroups));
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
                // Set the Content-Type header
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setContentType(new MediaType("application", "json"));
                HttpEntity<NewUser> requestEntity = new HttpEntity<>(user, requestHeaders);

                final String url = "http://192.168.0.100:8080/add_user";
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
            finish();
            Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_LONG).show();
        }
    }
}
