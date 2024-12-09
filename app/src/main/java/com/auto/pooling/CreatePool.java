package com.auto.pooling;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Toast;

import com.auto.pooling.databinding.ActivityCreatePoolBinding;

public class CreatePool extends AppCompatActivity {
    private ActivityCreatePoolBinding _binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pool);
        _binding = ActivityCreatePoolBinding.inflate(getLayoutInflater());
        int selectedId = _binding.radioGroup.getCheckedRadioButtonId();

        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            String selectedOption = selectedRadioButton.getText().toString();
            Toast.makeText(this, "Selected: " + selectedOption, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No option selected", Toast.LENGTH_SHORT).show();
        }

        String[] options = {"1 Passenger", "2 Passengers", "3 Passengers", "4 Passengers"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item,options);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        _binding.passengerCountSpinner.setAdapter(adapter);
        _binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


}