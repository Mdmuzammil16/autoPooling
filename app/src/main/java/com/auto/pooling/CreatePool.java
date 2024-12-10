package com.auto.pooling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.auto.adapter.PoolingDataAdapter;
import com.auto.pooling.databinding.ActivityCreatePoolBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreatePool extends AppCompatActivity {
    private ActivityCreatePoolBinding _binding;
    private Date selectedDate;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _binding = ActivityCreatePoolBinding.inflate(getLayoutInflater());

        setContentView(_binding.getRoot());

        String[] options = {"1 Passenger", "2 Passengers", "3 Passengers"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,  // Custom layout for spinner items
                options
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Use a system dropdown layout
        _binding.passengerCountSpinner.setAdapter(adapter);


        _binding.btnSelectDate.setOnClickListener(v -> {
            // Get the current date and time
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            // Open DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                this,
                                (TimePicker timeView, int selectedHour, int selectedMinute) -> {
                                    Calendar selectedCalendar = Calendar.getInstance();
                                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute, 0);
                                    selectedDate = selectedCalendar.getTime(); // Get the Date object
                                    _binding.tvSelectedDate.setText("Selected Date and Time:"+selectedDay+"/"+selectedMonth+"/"+selectedYear + " " +selectedHour+":"+selectedMinute);
                                },
                                hour,
                                minute,
                                true // true for 24-hour format, false for 12-hour format
                        );
                        timePickerDialog.show();
                    },
                    year,
                    month,
                    day
            );
            datePickerDialog.show();
        });


        _binding.createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_binding.leavingFromTxt.getText().toString().trim().isEmpty()  || _binding.goingToTxt.getText().toString().trim().isEmpty()){
                    Toast.makeText(CreatePool.this,"Missing Leaving From Or Going To Address",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(selectedDate == null){
                    Toast.makeText(CreatePool.this,"Date is Missing",Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> poolingData = new HashMap<>();
                poolingData.put("driverName", "Taxi Driver");
                poolingData.put("rating", "3.0");
                poolingData.put("date", selectedDate);
                poolingData.put("leavingFrom", _binding.leavingFromTxt.getText().toString().trim());
                poolingData.put("goingTo", _binding.goingToTxt.getText().toString().trim());
                poolingData.put("passenger",_binding.passengerCountSpinner.getSelectedItemPosition()+1);
                poolingData.put("bookedSeats",new ArrayList<>());

                db.collection("poolings")
                        .add(poolingData)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(CreatePool.this,"Data Added Successfully",Toast.LENGTH_SHORT).show();
                                Intent resultIntent = new Intent();
                                setResult(RESULT_OK, resultIntent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreatePool.this,e.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        _binding.testTap.setOnClickListener(view -> {
            Intent intent = new Intent(CreatePool.this, LocationActivity.class);
            startActivityForResult(intent, 200);

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            String location = data.getStringExtra("location");
            Toast.makeText(this, ""+location, Toast.LENGTH_SHORT).show();
        }
    }
}
