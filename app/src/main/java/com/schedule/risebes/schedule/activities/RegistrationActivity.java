package com.schedule.risebes.schedule.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.schedule.risebes.schedule.R;
import com.schedule.risebes.schedule.rest.NewUser;
import com.schedule.risebes.schedule.server.ServerCommand;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class RegistrationActivity extends AppCompatActivity {
    private View mProgressView;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mName;
    private EditText mSurname;
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
    }

    private void initFields()
    {
        buttonReg= (Button) findViewById(R.id.button_reg);
        mEmail = (EditText) findViewById(R.id.reg_email);
        mPassword = (EditText) findViewById(R.id.reg_password);
        mName = (EditText) findViewById(R.id.reg_name);
        mSurname = (EditText) findViewById(R.id.reg_surname);
    }

    private void regListeners()
    {
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (testFieldsOfValid()) {
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
        return user;
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
