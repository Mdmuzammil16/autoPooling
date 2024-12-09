package com.auto.pooling;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Toast;
import com.auto.pooling.databinding.ActivityCreatePoolBinding;

import java.util.Calendar;

public class CreatePool extends AppCompatActivity {
    private ActivityCreatePoolBinding _binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _binding = ActivityCreatePoolBinding.inflate(getLayoutInflater());

        setContentView(_binding.getRoot());

        int selectedId = _binding.radioGroup.getCheckedRadioButtonId();

        // Check if any radio button is selected
        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            String selectedOption = selectedRadioButton.getText().toString();
            Toast.makeText(this, "Selected: " + selectedOption, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No option selected", Toast.LENGTH_SHORT).show();
        }

        // Set up the spinner
        String[] options = {"1 Passenger", "2 Passengers", "3 Passengers", "4 Passengers"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,  // Custom layout for spinner items
                options
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Use a system dropdown layout
        _binding.passengerCountSpinner.setAdapter(adapter);

        _binding.btnSelectDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        _binding.tvSelectedDate.setText("Selected Date: " + date);
                    },
                    year,
                    month,
                    day
            );
            datePickerDialog.show();
        });

    }
}
