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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.auto.response_models.PoolingResponseModel;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HomePageFragment extends Fragment {

    private FragmentHomePageBinding binding;

    ArrayList<PoolingResponseModel> newArrayList = new ArrayList<>();


    FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        poolingDataAdapter = new PoolingDataAdapter(requireContext(), new ArrayList<PoolingResponseModel>(),onlongClick -> {

        });
        binding.poolingListView.setAdapter(poolingDataAdapter);
        fetchData();

        binding.addPooling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),CreatePool.class );
                startActivity(intent);
             }
        });


    }

    private void fetchData() {
        newArrayList.clear();
        db.collection("poolings").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String docId = document.getId();
                        String driverName = document.getString("driverName");
                        String rating = document.getString("rating");
                        Date date = document.getDate("date");
                        String leavingFrom = document.getString("leavingFrom");
                        String goingTo = document.getString("goingTo");
                        Long passenger = document.getLong("passenger");
                        ArrayList<Double> bookedSeats = (ArrayList<Double>) document.get("bookedSeats");
                        PoolingResponseModel poolingModel = new PoolingResponseModel(docId,driverName,rating,date,leavingFrom,goingTo,passenger,bookedSeats);
                        newArrayList.add(poolingModel);
                    }
                    poolingDataAdapter.newData(newArrayList);
                } else {
                    Toast.makeText(requireContext(),"Error getting Documents",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}