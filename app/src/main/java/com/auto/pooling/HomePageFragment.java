package com.auto.pooling;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.auto.adapter.PoolingDataAdapter;
import com.auto.pooling.databinding.FragmentHomePageBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.auto.response_models.PoolingResponseModel;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HomePageFragment extends Fragment {

    private FragmentHomePageBinding binding;

    ArrayList<PoolingResponseModel> newArrayList = new ArrayList<>();


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private PoolingDataAdapter poolingDataAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding =  FragmentHomePageBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchData();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        poolingDataAdapter = new PoolingDataAdapter(requireContext(),true,new ArrayList<>(),onlongClick -> {

        });
        binding.poolingListView.setAdapter(poolingDataAdapter);
        binding.userName.setText(mAuth.getCurrentUser().getDisplayName());
        Glide.with(requireContext())
                .load(mAuth.getCurrentUser().getPhotoUrl()) // Load the profile image URL
                .placeholder(R.drawable.example_image) // Optional placeholder while loading
                .error(R.drawable.example_image) // Optional error image if loading fails
                .into(binding.userImage);
    }

    private void fetchData() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.poolingListView.setVisibility(View.GONE);
        newArrayList.clear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

// Convert to Date
        Date startOfToday = calendar.getTime();
        db.collection("poolings").whereGreaterThanOrEqualTo("timestamp", startOfToday).orderBy("timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.poolingListView.setVisibility(View.VISIBLE);
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String docId = document.getId();
                        String driverName = document.getString("driverName");
                        String imageUrl = document.getString("imageUrl");
                        String rating = document.getString("rating");
                        Date date = document.getDate("date");
                        String leavingFrom = document.getString("leavingFrom");
                        String goingTo = document.getString("goingTo");
                        ArrayList<Double> bookedSeats = (ArrayList<Double>) document.get("bookedSeats");
                        PoolingResponseModel poolingModel = new PoolingResponseModel(docId,imageUrl,driverName,rating,date,leavingFrom,goingTo,bookedSeats);
                        newArrayList.add(poolingModel);
                    }
                    poolingDataAdapter.newData(newArrayList);
                } else {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.poolingListView.setVisibility(View.VISIBLE);
                    Toast.makeText(requireContext(),"Error getting Documents",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}