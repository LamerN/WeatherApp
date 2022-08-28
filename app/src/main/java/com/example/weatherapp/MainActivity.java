package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private EditText editTextCity;
    private Button buttonSearch;
    private TextView textViewWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextCity = findViewById(R.id.editTextCity);
        buttonSearch = findViewById(R.id.buttonSearch);
        textViewWeather = findViewById(R.id.textViewWeather);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cityName = editTextCity.getText().toString();
                if ((cityName.trim().equals(""))) {                                                             //проверка заполнения поля
                    Toast.makeText(MainActivity.this,R.string.toast_text, Toast.LENGTH_SHORT).show();
                } else {

                    String city = editTextCity.getText().toString();
                    String key = "d413d0e786af04c647891498baac4b66";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric&lang=ru";

                    new GetURLData().execute(url);
                }
            }
        });

    }

    private class GetURLData extends AsyncTask<String, String, String>{
        HttpsURLConnection connection = null;
        BufferedReader reader = null;

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;                    //возвращает если была ошибка
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                String weatherDescription = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                double temp = jsonObject.getJSONObject("main").getDouble("temp");
                double tempFeelsLike = jsonObject.getJSONObject("main").getDouble("feels_like");
                int pressure = jsonObject.getJSONObject("main").getInt("pressure");
                double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
                textViewWeather.setText(weatherDescription+"\n температура: " + temp + "°С\n ощущается как: " + tempFeelsLike + "°С\n давление: " + pressure + " мм.рт.ст\n скорость ветра: " + windSpeed +" м/с");
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    }
}