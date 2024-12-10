package com.auto.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.auto.pooling.CreateBookingActivity;
import com.auto.pooling.databinding.AutoDetailsAdapterBinding;
import com.auto.response_models.PoolingResponseModel;
import com.google.gson.Gson;

import java.util.ArrayList;


public class PoolingDataAdapter extends RecyclerView.Adapter<PoolingDataAdapter.Holder> {

    private Context context;
    private ArrayList<PoolingResponseModel> poolingList;
    private OnLongClickListener onLongClick;



    public interface OnLongClickListener {
        void onLongClick(PoolingResponseModel commentModel);
    }

    public PoolingDataAdapter(Context context, ArrayList<PoolingResponseModel> poolingList, OnLongClickListener onLongClick) {
        this.context = context;
        this.poolingList = poolingList;
        this.onLongClick = onLongClick;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        AutoDetailsAdapterBinding binding;

        public Holder(@NonNull AutoDetailsAdapterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AutoDetailsAdapterBinding binding = AutoDetailsAdapterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        PoolingResponseModel poolingData = poolingList.get(position);

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CreateBookingActivity.class);
                Gson gson = new Gson();
                String jsonString = gson.toJson(poolingData);
                intent.putExtra("poolingModel",jsonString);
                context.startActivity(intent);
            }
        });

        holder.binding.autoDriverName.setText(poolingData.getDriverName());
        holder.binding.rating.setText(poolingData.getRating());
        holder.binding.leavingFromTxt.setText(poolingData.getLeavingFrom());
        holder.binding.goingToTxt.setText(poolingData.getGoingTo());
    }

    @Override
    public int getItemCount() {
        return poolingList != null ? poolingList.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void newData(ArrayList<PoolingResponseModel> newData) {
        this.poolingList = newData;
        notifyDataSetChanged();
    }

//    @SuppressLint("NotifyDataSetChanged")
//    public void addData(CommentModel comment) {
//        this.commentsList.add(comment);
//        notifyDataSetChanged();
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    public void removeData(CommentModel comment) {
//        this.commentsList.remove(comment);
//        notifyDataSetChanged();
//    }
}
