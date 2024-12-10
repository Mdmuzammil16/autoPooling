package com.auto.pooling;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.auto.adapter.PoolingDataAdapter;
import com.auto.pooling.databinding.FragmentBookingsBinding;
import com.auto.response_models.PoolingResponseModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;


public class BookingsFragment extends Fragment {


    private FragmentBookingsBinding binding;

    ArrayList<PoolingResponseModel> newArrayList = new ArrayList<>();


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private PoolingDataAdapter poolingDataAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBookingsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        poolingDataAdapter = new PoolingDataAdapter(requireContext(), new ArrayList<>(), onlongClick -> {

        });
        binding.bookingListView.setAdapter(poolingDataAdapter);
        fetchData();
    }

    private void fetchData() {
        newArrayList.clear();
        db.collection("orders").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        ArrayList<Double> bookedSeats = (ArrayList<Double>) document.get("seats");
                        PoolingResponseModel poolingModel = new PoolingResponseModel(docId,driverName,rating,date,leavingFrom,goingTo,bookedSeats);
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