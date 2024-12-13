package com.auto.pooling;

import static com.auto.extensions.extension.generateRandomName;
import static com.auto.extensions.extension.getImageUrl;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.auto.adapter.SelectingSeatAdapter;
import com.auto.pooling.databinding.FragmentCreatePoolingBinding;
import com.auto.response_models.SeatDataModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class CreatePoolingFragment extends Fragment {


     private FragmentCreatePoolingBinding _binding;
    private Date selectedDate;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    List<Double> bookingPassenger = new ArrayList<>();

    private ActivityResultLauncher<Intent> locationActivityLauncher;
    private SelectingSeatAdapter selectingSeatAdapter;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = FragmentCreatePoolingBinding.inflate(getLayoutInflater());
        return _binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        locationActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == 200 || result.getResultCode() == 201) {
                Intent data = result.getData();
                if (data != null) {
                    String location = data.getStringExtra("location");
                    if (result.getResultCode() == 200) {
                        _binding.leavingFromTxt.setText(location);
                    } else {
                        _binding.goingToTxt.setText(location);
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Service Not Available", Toast.LENGTH_SHORT).show();
            }
        });

        ArrayList<SeatDataModel> list = new ArrayList<>();
        list.clear();
        list.add(new SeatDataModel(false,false,"1"));
        list.add(new SeatDataModel(false,false,"2"));
        list.add(new SeatDataModel(false,false,"3"));

        selectingSeatAdapter = new SelectingSeatAdapter(requireContext(),list,onlongClick ->{});
        _binding.seatsRecycler.setAdapter(selectingSeatAdapter);

//        String[] options = {"1 Passenger", "2 Passengers", "3 Passengers"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),R.layout.spinner_item, options);
//
//        adapter.setDropDownViewResource(R.layout.spinner_item); // Use a system dropdown layout
//        _binding.passengerCountSpinner.setAdapter(adapter);



//        _binding.goingToTxt.setOnTouchListener((v, event) -> {
//            v.performClick();
//            return true;
//        });
        _binding.btnSelectDate.setOnClickListener(v -> {
            // Get the current date and time
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            // Open DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (DatePicker view2, int selectedYear, int selectedMonth, int selectedDay) -> {
                // Open TimePickerDialog once date is selected
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        requireContext(),
                        (TimePicker timeView, int selectedHour, int selectedMinute) -> {
                            Calendar selectedCalendar = Calendar.getInstance();
                            selectedCalendar.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute, 0);
                            if (selectedCalendar.before(calendar)) {
                                Toast.makeText(requireContext(), "Past dates and times are not allowed.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            selectedDate = selectedCalendar.getTime(); // Get the Date object
                            _binding.tvSelectedDate.setText(selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear + " " + selectedHour + ":" + selectedMinute);
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
            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

            datePickerDialog.show();
        });


        _binding.createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _binding.progressBar.setVisibility(View.VISIBLE);
                _binding.createBtn.setVisibility(View.GONE);
                if(_binding.leavingFromTxt.getText().toString().trim().isEmpty()  || _binding.goingToTxt.getText().toString().trim().isEmpty()){
                    Toast.makeText(requireContext(),"Missing Leaving From Or Going To Address",Toast.LENGTH_SHORT).show();
                    _binding.progressBar.setVisibility(View.GONE);
                    _binding.createBtn.setVisibility(View.VISIBLE);
                    return;
                }
                else if(selectedDate == null){
                    Toast.makeText(requireContext(),"Date is Missing",Toast.LENGTH_SHORT).show();
                    _binding.progressBar.setVisibility(View.GONE);
                    _binding.createBtn.setVisibility(View.VISIBLE);
                    return;
                }
                bookingPassenger.clear();
                selectingSeatAdapter.seatList.forEach((data) ->{
                    if(data.isSeatSelected()){
                        bookingPassenger.add(Double.parseDouble(data.getSeatName()));
                    }
                });
                if(bookingPassenger.size() == 0) {
                    Toast.makeText(requireContext(),"Select Any Seat",Toast.LENGTH_SHORT).show();
                    _binding.progressBar.setVisibility(View.GONE);
                    _binding.createBtn.setVisibility(View.VISIBLE);
                    return;
                }

                Map<String, Object> poolingData = new HashMap<>();
                poolingData.put("timestamp", FieldValue.serverTimestamp());
                poolingData.put("driverName",generateRandomName());
                poolingData.put("imageUrl",getImageUrl());
                poolingData.put("rating", "3.0");
                poolingData.put("date", selectedDate);
                poolingData.put("leavingFrom", _binding.leavingFromTxt.getText().toString().trim());
                poolingData.put("goingTo", _binding.goingToTxt.getText().toString().trim());
                poolingData.put("bookedSeats",bookingPassenger);
                poolingData.put("uid",mAuth.getCurrentUser().getUid());

                db.collection("poolings")
                        .add(poolingData)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Map<String, Object> orderData = new HashMap<>();
                                orderData.put("timestamp", FieldValue.serverTimestamp());
                                orderData.put("poolingId", documentReference.getId());
                                orderData.put("imageUrl",poolingData.get("imageUrl"));
                                orderData.put("driverName",poolingData.get("driverName"));
                                orderData.put("rating", "3.0");
                                orderData.put("date",selectedDate);
                                orderData.put("leavingFrom", _binding.leavingFromTxt.getText().toString().trim());
                                orderData.put("goingTo", _binding.goingToTxt.getText().toString().trim());
                                orderData.put("seats", bookingPassenger);
                                orderData.put("price",500);
                                orderData.put("uid",mAuth.getCurrentUser().getUid());

                                db.collection("orders")
                                        .add(orderData)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                _binding.progressBar.setVisibility(View.GONE);
                                                _binding.createBtn.setVisibility(View.VISIBLE);
                                                Toast.makeText(requireContext(),"Data Added Successfully",Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(requireContext(), SuccessPage.class);
                                                intent.putExtra("leavingFrom",_binding.leavingFromTxt.getText().toString().trim());
                                                intent.putExtra("goingTo",_binding.goingToTxt.getText().toString().trim());
                                                startActivity(intent);
//                                                Intent resultIntent = new Intent();
//                                                setResult(RESULT_OK, resultIntent);
//                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                _binding.progressBar.setVisibility(View.GONE);
                                                _binding.createBtn.setVisibility(View.VISIBLE);
                                                Toast.makeText(requireContext(),e.toString(),Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                _binding.progressBar.setVisibility(View.GONE);
                                _binding.createBtn.setVisibility(View.VISIBLE);
                                Toast.makeText(requireContext(),e.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        _binding.leavingFromTxt.setOnClickListener(view1 -> {
            Intent intent = new Intent(requireContext(), LocationActivity.class);
            intent.putExtra("requestCode",200);
            locationActivityLauncher.launch(intent);
        });
        _binding.goingToTxt.setOnClickListener(view2 -> {
            Intent intent = new Intent(requireContext(), LocationActivity.class);
            intent.putExtra("requestCode",201);
            locationActivityLauncher.launch(intent);
        });
    }



}