package com.auto.pooling;

import static com.auto.extensions.extension.getDateTimeFromDate;
import static com.auto.extensions.extension.getDateTimeWithExtraHour;
import static com.auto.extensions.extension.getTimeFromDate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.auto.adapter.SelectingSeatAdapter;
import com.auto.pooling.databinding.ActivityCreateBookingBinding;
import com.auto.response_models.PoolingResponseModel;
import com.auto.response_models.SeatDataModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateBookingActivity extends AppCompatActivity {
    private ActivityCreateBookingBinding binding;
    List<Double> bookingPassenger = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private SelectingSeatAdapter selectingSeatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Gson gson = new Gson();
        Intent intent = getIntent();
        String poolingModel = intent.getStringExtra("poolingModel");
        String position = intent.getStringExtra("position");
        boolean fromHome = intent.getBooleanExtra("fromHome",true);
        PoolingResponseModel poolingData = gson.fromJson(poolingModel, PoolingResponseModel.class);

        String imageUrl = "https://api.dicebear.com/9.x/avataaars/png?seed="+position;
        if(poolingData.getImageUrl() !=null){
            imageUrl = poolingData.getImageUrl();
        }
        Glide.with(this)
                .load(imageUrl) // Load the profile image URL
                .placeholder(R.drawable.example_image) // Optional placeholder while loading
                .error(R.drawable.example_image) // Optional error image if loading fails
                .into(binding.autoDriverImage);

        List<Double> bookedSeats = poolingData.getBookedSeats();


        ArrayList<SeatDataModel> list = new ArrayList<>();
        if(fromHome){
            list.clear();
            list.add(new SeatDataModel(bookedSeats.contains(1.0),false,"1"));
            list.add(new SeatDataModel(bookedSeats.contains(2.0),false,"2"));
            list.add(new SeatDataModel(bookedSeats.contains(3.0),false,"3"));
            binding.confirmBookingBtn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
        else{
            list.clear();
            bookedSeats.forEach((seatNumber) -> {
                list.add(new SeatDataModel(true, false, String.valueOf(((Number) seatNumber).intValue())));
            });
            binding.confirmBookingBtn.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
        }
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
        binding.autoDriverName.setText(poolingData.getDriverName());
        binding.rating.setText(poolingData.getRating());
        binding.leavingFromTxt.setText(poolingData.getLeavingFrom());
        binding.goingToTxt.setText(poolingData.getGoingTo());
        binding.bookingDateAndTimeTxt.setText(getDateTimeFromDate(poolingData.getDate()));
        binding.startTime.setText(getTimeFromDate(poolingData.getDate()));
        binding.endTime.setText(getDateTimeWithExtraHour(poolingData.getDate()));


        selectingSeatAdapter = new SelectingSeatAdapter(CreateBookingActivity.this,list,onlongClick ->{});
        binding.seatRecyclerList.setAdapter(selectingSeatAdapter);
        binding.confirmBookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.confirmBookingBtn.setVisibility(View.GONE);
                bookingPassenger.clear();
                selectingSeatAdapter.seatList.forEach((data) ->{
                    if(data.isSeatSelected()){
                        bookingPassenger.add(Double.parseDouble(data.getSeatName()));
                    }
                });
                if(bookingPassenger.size() == 0) {
                    Toast.makeText(CreateBookingActivity.this,"Select Any Seat",Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
                    binding.confirmBookingBtn.setVisibility(View.VISIBLE);
                    return;
                }
                Map<String, Object> orderData = new HashMap<>();
                orderData.put("timestamp", FieldValue.serverTimestamp());
                orderData.put("poolingId", poolingData.getDocId());
                orderData.put("imageUrl", poolingData.getImageUrl());
                orderData.put("driverName", poolingData.getDriverName());
                orderData.put("rating", "3.0");
                orderData.put("date", poolingData.getDate());
                orderData.put("leavingFrom", poolingData.getLeavingFrom());
                orderData.put("goingTo", poolingData.getGoingTo());
                orderData.put("seats", bookingPassenger);
                orderData.put("price",500);
                orderData.put("uid",mAuth.getCurrentUser().getUid());

                db.collection("orders")
                        .add(orderData)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                bookingPassenger.addAll(bookedSeats);
                                db.collection("poolings").document(poolingData.getDocId())
                                        .update("bookedSeats",bookingPassenger)
                                        .addOnSuccessListener(aVoid -> {
                                            binding.progressBar.setVisibility(View.GONE);
                                            binding.confirmBookingBtn.setVisibility(View.VISIBLE);
                                            Toast.makeText(CreateBookingActivity.this,"Data Added Successfully",Toast.LENGTH_SHORT).show();
                                            Intent resultIntent = new Intent();
                                            setResult(RESULT_OK, resultIntent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            binding.progressBar.setVisibility(View.GONE);
                                            binding.confirmBookingBtn.setVisibility(View.VISIBLE);
                                            Toast.makeText(CreateBookingActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                binding.progressBar.setVisibility(View.GONE);
                                binding.confirmBookingBtn.setVisibility(View.VISIBLE);
                                Toast.makeText(CreateBookingActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}