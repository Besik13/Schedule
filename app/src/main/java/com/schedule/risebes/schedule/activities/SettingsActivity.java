package com.schedule.risebes.schedule.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.schedule.risebes.schedule.R;
import com.schedule.risebes.schedule.rest.Groups;
import com.schedule.risebes.schedule.server.ServerCommand;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class SettingsActivity extends AppCompatActivity {

    private String[] listOfGroups;
    Spinner AutoCompleteGroup;

    private class HttpRequestTask extends AsyncTask<Void, Void, Groups>
    {
        @Override
        protected Groups doInBackground(Void... params) {
            try {
                final String url = ServerCommand.getGroupsString();
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
            AutoCompleteGroup = (Spinner) findViewById(R.id.autoCompleteGroup);
            AutoCompleteGroup.setAdapter(new ArrayAdapter(SettingsActivity.this,
                    android.R.layout.simple_dropdown_item_1line, listOfGroups));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        new HttpRequestTask().execute();
    }
}
