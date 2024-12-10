package com.auto.pooling;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateBookingActivity extends AppCompatActivity {
    private ActivityCreateBookingBinding binding;
    List<Double> bookingPassenger = new ArrayList<>();

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
        PoolingResponseModel poolingData = gson.fromJson(poolingModel, PoolingResponseModel.class);


        binding.autoDriverName.setText(poolingData.getDriverName());
        binding.rating.setText(poolingData.getRating());
        binding.leavingFromTxt.setText(poolingData.getLeavingFrom());
        binding.goingToTxt.setText(poolingData.getGoingTo());
        binding.bookingDateAndTimeTxt.setText(poolingData.getDate().toString());
        List<Double> bookedSeats = poolingData.getBookedSeats();


        ArrayList<SeatDataModel> list = new ArrayList<>();
        list.add(new SeatDataModel(bookedSeats.contains(1.0),false,"1"));
        list.add(new SeatDataModel(bookedSeats.contains(2.0),false,"2"));
        list.add(new SeatDataModel(bookedSeats.contains(3.0),false,"3"));


        selectingSeatAdapter = new SelectingSeatAdapter(CreateBookingActivity.this,list,onlongClick ->{});
        binding.seatRecyclerList.setAdapter(selectingSeatAdapter);
        binding.confirmBookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookingPassenger.clear();
                bookingPassenger.addAll(bookedSeats);
                selectingSeatAdapter.seatList.forEach((data) ->{
                    if(data.isSeatSelected()){
                        bookingPassenger.add(Double.parseDouble(data.getSeatName()));
                    }
                });
                if(bookingPassenger.size() == 0) {
                    Toast.makeText(CreateBookingActivity.this,"Select Any Seat",Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, Object> orderData = new HashMap<>();
                orderData.put("poolingId", poolingData.getDocId());
                orderData.put("driverName", "Taxi Driver");
                orderData.put("rating", "3.0");
                orderData.put("date", poolingData.getDate());
                orderData.put("leavingFrom", poolingData.getLeavingFrom());
                orderData.put("goingTo", poolingData.getGoingTo());
                orderData.put("seats", bookingPassenger);
                orderData.put("price",500);

                db.collection("orders")
                        .add(orderData)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                db.collection("poolings").document(poolingData.getDocId())
                                        .update("bookedSeats",bookingPassenger)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(CreateBookingActivity.this,"Data Added Successfully",Toast.LENGTH_SHORT).show();
                                            Intent resultIntent = new Intent();
                                            setResult(RESULT_OK, resultIntent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(CreateBookingActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreateBookingActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}