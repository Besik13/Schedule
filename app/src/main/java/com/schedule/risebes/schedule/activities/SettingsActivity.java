package com.schedule.risebes.schedule.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.schedule.risebes.schedule.R;
import com.schedule.risebes.schedule.rest.InfoFromTable;
import com.schedule.risebes.schedule.server.ServerCommand;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private String[] list;
    private Spinner spinnerDepartment;
    private Spinner spinnerSpecialty;
    private Spinner spinnerGroup;

    private Integer currentId;

    private InfoFromTable infoDep;
    private InfoFromTable infoSpec;
    private InfoFromTable infoGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        spinnerDepartment = (Spinner) findViewById(R.id.spinnerDepartment);
        spinnerSpecialty = (Spinner) findViewById(R.id.spinnerSpecialty);
        spinnerGroup = (Spinner) findViewById(R.id.spinnerGroup);
        createListeners();
        new HttpRequestTask("name", "departments", "1", spinnerDepartment).execute();

    }

    private void createListeners() {
        spinnerDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    currentId = getSelectedId(spinnerDepartment);
                    new HttpRequestTask("name", "specialty", "departament" ,currentId.toString(), spinnerSpecialty).execute();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
        spinnerSpecialty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                currentId = getSelectedId(spinnerSpecialty);
                new HttpRequestTask("group", "groups", "specialty", currentId.toString(), spinnerGroup).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
    }

    private Integer getSelectedId(Spinner spinner) {
        InfoFromTable currentInfo=null;
        switch (spinner.getId())
        {
            case R.id.spinnerDepartment: currentInfo=infoDep; break;
            case R.id.spinnerSpecialty: currentInfo=infoSpec; break;
            case R.id.spinnerGroup: currentInfo=infoGroup; break;
        }
        String name = spinner.getSelectedItem().toString();
        for (Map.Entry<Integer, String> entry : currentInfo.getInfo().entrySet()) {
            if (entry.getValue().trim().equalsIgnoreCase(name)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, InfoFromTable> {
        private final String what;
        private final String from;
        private final String where;
        private  String what2=null;
        private final Spinner spinner;

        HttpRequestTask(String what, String from, String where,String what2, Spinner spinner) {
            this.what = what;
            this.from = from;
            this.where = where;
            this.what2 = what2;
            this.spinner = spinner;
        }

        HttpRequestTask(String what, String from, String where, Spinner spinner) {
            this.what = what;
            this.from = from;
            this.where = where;
            this.spinner = spinner;
        }

        @Override
        protected InfoFromTable doInBackground(Void... params) {
            try {
                String url;
                if(what2==null)
                {
                    url = ServerCommand.getInfo(what, from, where);
                }
                    else
                {
                    url = ServerCommand.getInfo(what, from, where, what2);
                }
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                InfoFromTable infoFromTable = restTemplate.getForObject(url, InfoFromTable.class);
                return infoFromTable;
            } catch (Exception e) {
                Log.e("SettingsActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(InfoFromTable info) {
            switch (spinner.getId())
            {
                case R.id.spinnerDepartment: infoDep=info; break;
                case R.id.spinnerSpecialty: infoSpec=info; break;
                case R.id.spinnerGroup: infoGroup=info; break;
            }
            list = new String[info.getInfo().size()];
            info.getInfo().values().toArray(list);
            spinner.setAdapter(new ArrayAdapter(SettingsActivity.this,
                    android.R.layout.simple_dropdown_item_1line, list));
        }
    }
}
