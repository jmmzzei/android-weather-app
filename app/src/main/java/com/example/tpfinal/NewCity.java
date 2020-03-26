package com.example.tpfinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class NewCity extends AppCompatActivity {
    EditText txtAddCity;
    Button btnAddCity, btnBack;
    ArrayList<String> bufferCities;
    final String saved = "¡Ciudad guardada!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_ciudad);
        txtAddCity = findViewById(R.id.txtAddCity);
        btnAddCity = findViewById(R.id.btnAddCity);
        btnBack = findViewById(R.id.btnBack);
        txtAddCity.requestFocus();
        final InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);

        bufferCities = new ArrayList<>();

        btnAddCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addedCity = txtAddCity.getText().toString();
                if (!addedCity.isEmpty()) {
                    bufferCities.add(addedCity);
                    txtAddCity.setText("");
                    Toast.makeText(NewCity.this, saved, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(txtAddCity.getWindowToken(), 0);
                Intent intent = new Intent();

                if (!bufferCities.isEmpty()){
                    for (int i = 0; i<bufferCities.size(); i++){
                        intent.putExtra(Integer.toString(i), bufferCities.get(i));
                    }
                    setResult(RESULT_OK, intent);
                    bufferCities.clear();
                    finish();
                } else {
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}
