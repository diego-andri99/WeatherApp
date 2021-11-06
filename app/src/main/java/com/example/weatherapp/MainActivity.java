package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.VoiceInteractor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.service.voice.VoiceInteractionService;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

     String risposta = "";
     String citta = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.button);
        EditText editText = findViewById(R.id.editText);
        TextView textView = findViewById(R.id.textView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                citta = editText.getText().toString();
                String URL = "https://api.openweathermap.org/data/2.5/weather?q="+citta+"&appid=c8078074a9c2ce2368a9d39d7bb67bba&lang=it&units=metric";
                RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
                JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        risposta = response.toString();
                        try {
                            textView.setText("DATI METEO A: " + citta.toUpperCase(Locale.ROOT) + "\n" + parse(risposta).toUpperCase(Locale.ROOT) + " " + "\n");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplication(), "Questa citta' non esiste, riprova!", Toast.LENGTH_SHORT).show();
                    }
                });
                requestQueue.add(objectRequest);

                InputMethodManager inputManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }

    public static String parse (String stringa) throws JSONException {
        String tempo = "";
        String temperatura = "";
        String meteo = "";
        int sign = 0;
        for(int i = 0; i < stringa.length()-1; i++){
            if(stringa.substring(i,i+1).equals("[")) {
                tempo = stringa.substring(i, stringa.length());
                sign = i+2;
            }
        }
        for(int i = 0; i < tempo.length()-1; i++){
            if(tempo.substring(i,i+1).equals("]")) {
                tempo = tempo.substring(0, i + 1);
            }
        }
        temperatura = stringa.substring(sign, stringa.length());
        for(int i = 0; i < temperatura.length()-1; i++){
            if(temperatura.substring(i,i+1).equals("{")) {
                temperatura = temperatura.substring(i, temperatura.length());
                break;
            }
        }
        for(int i = 0; i < temperatura.length()-1; i++){
            if(temperatura.substring(i,i+1).equals("}")) {
                temperatura = temperatura.substring(0, i + 1);
                break;
            }
        }
        temperatura = "[" + temperatura + "]";
        System.out.println("DATI: ");
        JSONArray jsonArray = new JSONArray(tempo);
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int id = jsonObject.getInt("id");
            String description = jsonObject.getString(("description"));
            meteo = meteo + "- " + description + "\n";
        }
        JSONArray jsonArray1 = new JSONArray(temperatura);
        for(int i = 0; i < jsonArray1.length(); i++){
            JSONObject jsonObject = jsonArray1.getJSONObject(i);
            double temp = jsonObject.getDouble("temp");
            double temp_min = jsonObject.getDouble("temp_min");
            double temp_max = jsonObject.getDouble("temp_max");
            meteo = meteo + "- TEMPERATURA: " + temp + "° \n"+  "- TEMPERATURA MIN: " + temp_min + "° \n" + "- TEMPERATURA MAX: " + temp_max + "°";
        }
        return meteo;
    }




}