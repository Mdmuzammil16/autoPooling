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
import com.auto.adapter.LocationAdapter;
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
        arrayList.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(response);
                        JSONArray predictions = jsonObject.getJSONArray("predictions");
                        for (int i = 0; i < predictions.length(); i++) {
                            JSONObject prediction = predictions.getJSONObject(i);
                            arrayList.add(prediction); // Add to the ArrayList
                        }
                        binding.locationRv.setAdapter(new LocationAdapter(LocationActivity.this, arrayList));
                    } catch (JSONException e) {
                        Toast.makeText(LocationActivity.this, "Error: " + e, Toast.LENGTH_LONG).show();

                    }
                },
                error -> Toast.makeText(LocationActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show());
        requestQueue.add(stringRequest);
    }

}