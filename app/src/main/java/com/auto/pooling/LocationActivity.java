package com.auto.pooling;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.auto.pooling.databinding.ActivityLocationBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LocationActivity extends AppCompatActivity {

    ActivityLocationBinding binding;
    String apiKey = "AIzaSyB24Hrv9znLj4PB1Aryc7a3kd3EzwJ9dZk";
    ArrayList<JSONObject> arrayList = new ArrayList<>();

    private static final long DEBOUNCE_DELAY = 900;
    private final Handler handler = new Handler();
    private Runnable searchRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.locationEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);
                searchRunnable = () -> performSearch(s.toString());
                handler.postDelayed(searchRunnable, DEBOUNCE_DELAY);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void performSearch(String query) {
        int radius = 10;
        String url = makeLocationUrl(query, apiKey, radius);
        getLocation(url);
    }

    String makeLocationUrl(
            String searchText,
            String apiKey,
            int radius
    ) {
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + searchText;
        url += "&key=" + apiKey;
        url += "&radius=" + radius;
        url += "&components=country:IN";
        return url;
    }

    private void getLocation(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response);
                        JSONArray predictions = jsonObject.getJSONArray("predictions");
                        for (int i = 0; i < predictions.length(); i++) {
                            JSONObject prediction = predictions.getJSONObject(i);
                            arrayList.add(prediction); // Add to the ArrayList
                        }
                    } catch (JSONException e) {
                        Toast.makeText(LocationActivity.this, "Error: " + e, Toast.LENGTH_LONG).show();

                    }
                },
                error -> Toast.makeText(LocationActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show());
        requestQueue.add(stringRequest);
    }

    private void getPlaceDetails(String placeId) {
        String url = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + placeId + "&key=" + apiKey;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject result = jsonObject.getJSONObject("result");
                        JSONObject geometry = result.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");
                        double latitude = location.getDouble("lat");
                        double longitude = location.getDouble("lng");
                        Log.d("fazilApp", "Latitude: " + latitude + ", Longitude: " + longitude);
                        Toast.makeText(LocationActivity.this, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.d("fazilApp", "Error parsing Place Details: " + e.getMessage());
                    }
                },
                error -> {
                    Log.d("fazilApp", "Error fetching Place Details: " + error.getMessage());
                });

        requestQueue.add(stringRequest);
    }
}