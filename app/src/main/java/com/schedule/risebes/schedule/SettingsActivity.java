package com.schedule.risebes.schedule;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.schedule.risebes.schedule.rest.Groups;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class SettingsActivity extends AppCompatActivity implements TextWatcher {

    private  String group = "";
    private String[] listOfGroups;
    private class HttpRequestTask extends AsyncTask<Void, Void, Groups>
    {
        @Override
        protected Groups doInBackground(Void... params) {
            try {
                final String url = "http://192.168.0.100:8080/groups";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Groups groups = restTemplate.getForObject(url, Groups.class);
                return groups;
            } catch (Exception e) {
                Log.e("SettingsActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Groups groups) {
            listOfGroups = new String[groups.getGroups().size()];
            groups.getGroups().values().toArray(listOfGroups);
            AutoCompleteGroup = (AutoCompleteTextView) findViewById(R.id.autoCompleteGroup);
            AutoCompleteGroup.addTextChangedListener(SettingsActivity.this);
            AutoCompleteGroup.setAdapter(new ArrayAdapter(SettingsActivity.this,
                    android.R.layout.simple_dropdown_item_1line, listOfGroups));
            AutoCompleteGroup.setText(group);
        }
    }
    AutoCompleteTextView AutoCompleteGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        new HttpRequestTask().execute();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        group = AutoCompleteGroup.getText().toString();
    }
}
