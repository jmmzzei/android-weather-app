package com.example.tpfinal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Toolbar myToolbar;
    ListView listView;
    ArrayList<String> citiesList;
    ArrayList<String> keyCollector;
    SharedPreferences prefs;
    final String celsius = " °C";
    final String namePrefs = "MyCities";
    final String startURL = "https://api.openweathermap.org/data/2.5/weather?q=";
    final String endURL = "&appid=";

    final String instruction = "Agrega una ciudad desde el menú.";
    final String notFounded = "Ciudad no encontrada.";
    final String temperature = "Temperatura: ";
    ArrayAdapter<String> adapter;
    int lastItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.list);
        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        citiesList = new ArrayList<>();
        keyCollector = new ArrayList<>();
        prefs = getSharedPreferences(namePrefs, Context.MODE_PRIVATE);

        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            citiesList.add(entry.getValue().toString());
        }

        adapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1, citiesList);
        listView.setAdapter(adapter);

        if (adapter.getCount() == 0){
            setInstructions();
        } else {
            setNormal();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == 23) && (resultCode == RESULT_OK) && (data.hasExtra("0"))) {
            prefs = getSharedPreferences(namePrefs, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            if (citiesList.get(0).equals(instruction)){
                citiesList.remove(0);
            }

            Map<String, ?> allEntries2 = prefs.getAll();
            for (Map.Entry<String, ?> entry : allEntries2.entrySet()) {
                keyCollector.add(entry.getKey());
            }

            if (keyCollector.size() > 0){
                String lastItemStr = keyCollector.get(keyCollector.size()-1);
                lastItem = Integer.parseInt(lastItemStr);
            } else {
                lastItem = 0;
            }

            Bundle bundle = data.getExtras();
            for (int i = 0; i<bundle.size(); i++){
                String bundleStr = bundle.getString(Integer.toString(i));
                citiesList.add(bundleStr);
                lastItem++;
                editor.putString(Integer.toString(lastItem), bundleStr);
                editor.commit();
            }

            adapter.notifyDataSetChanged();

            if (adapter.getCount() == 0){
                setInstructions();
            } else {
                setNormal();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setInstructions() {
        citiesList.add(0, instruction);
        listView.setEnabled(false);
        listView.setOnItemClickListener(null);
    }

    public void setNormal() {
        listView.setEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StringBuilder urlBuilder = new StringBuilder(startURL);
                urlBuilder.append(citiesList.get(position));
                urlBuilder.append(endURL);
				urlBuilder.append(API_KEY);
                new DownloadJSON().execute(urlBuilder.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.menuAdd){
            Intent intent = new Intent(MainActivity.this, NewCity.class);
            startActivityForResult(intent, 23);
        }
        return super.onOptionsItemSelected(item);
    }

    public class DownloadJSON extends AsyncTask<String, Void, String> {
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        protected void onPreExecute(){
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            InputStream inputStream = null;
            String result = "";
            try {
                inputStream = new URL(urls[0]).openStream();
                if(inputStream != null) {
                    BufferedReader buffer = new BufferedReader( new InputStreamReader(inputStream));
                    String line = "";
                    while ((line = buffer.readLine()) != null) {
                        result += line;
                    }
                    inputStream.close();
                }
            } catch (Exception e) {
                Log.d("TAGs", "doInBGError: " + e);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String resultado) {
            dialog.dismiss();
            try {
                JSONObject firstJSON = new JSONObject(resultado);
                JSONObject mainJSON = firstJSON.getJSONObject("main");
                Double temp = mainJSON.getDouble("temp");

                Double tempInCelsius = temp - 273.15;
                DecimalFormat df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.CEILING);
                String tempShow = df.format(tempInCelsius);

                StringBuilder stringBuilder = new StringBuilder(temperature);
                stringBuilder.append(tempShow);
                stringBuilder.append(celsius);
                Toast.makeText(MainActivity.this, stringBuilder, Toast.LENGTH_SHORT).show();

            } catch (Exception e){
                Log.d("TAGs", "onPostExecuteSuccess: " + e);
                Toast.makeText(MainActivity.this, notFounded, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
