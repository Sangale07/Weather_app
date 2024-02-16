package com.example.weatherapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText cityNameEditText;
    private static TextView cityNameTextView;
    private static TextView temperatureTextView;
    private static TextView humidityTextView;
    private static TextView windSpeedTextView;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityNameEditText = findViewById(R.id.cityNameEditText);
        cityNameTextView = findViewById(R.id.cityNameTextView);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        windSpeedTextView = findViewById(R.id.windSpeedTextView);
        searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cityName = cityNameEditText.getText().toString();
                if (cityName.isEmpty()) {
                    return;
                }

                new GetWeatherTask().execute(cityName);
            }
        });
    }

    private static class GetWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String cityName = params[0];
            String apiKey = "92c3d2eb830d0258c7092f4ae0c62eb2"; // Replace with your API key
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey + "&units=metric";

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    throw new RuntimeException("Failed to fetch weather data: HTTP error code " + responseCode);
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                return stringBuilder.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return ""; // Return an empty string instead of null
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.isEmpty()) {
                return;
            }

            try {
                JSONObject weatherData = new JSONObject(result);

                String cityName = weatherData.getString("name");
                double temperature = weatherData.getJSONObject("main").getDouble("temp");
                int humidity = weatherData.getJSONObject("main").getInt("humidity");
                double windSpeed = weatherData.getJSONObject("wind").getDouble("speed");

                cityNameTextView.setText(cityName);
                temperatureTextView.setText(String.format("%.2f", temperature) + " °C");
                humidityTextView.setText(String.valueOf(humidity) + " %");
                windSpeedTextView.setText(String.format("%.2f", windSpeed) + " m/s");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}