package com.auto.adapter;

import static com.auto.extensions.extension.getDateTimeWithExtraHour;
import static com.auto.extensions.extension.getTimeFromDate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.auto.pooling.CreateBookingActivity;
import com.auto.pooling.R;
import com.auto.pooling.databinding.AutoDetailsAdapterBinding;
import com.auto.response_models.PoolingResponseModel;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class PoolingDataAdapter extends RecyclerView.Adapter<PoolingDataAdapter.Holder> {

    private Context context;
    private boolean fromHome;
    private ArrayList<PoolingResponseModel> poolingList;
    private OnLongClickListener onLongClick;



    public interface OnLongClickListener {
        void onLongClick(PoolingResponseModel commentModel);
    }

    public PoolingDataAdapter(Context context,boolean fromHome,ArrayList<PoolingResponseModel> poolingList, OnLongClickListener onLongClick) {
        this.context = context;
        this.fromHome = fromHome;
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
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {
        PoolingResponseModel poolingData = poolingList.get(position);

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CreateBookingActivity.class);
                Gson gson = new Gson();
                String jsonString = gson.toJson(poolingData);
                intent.putExtra("poolingModel",jsonString);
                intent.putExtra("fromHome",fromHome);
                intent.putExtra("position",String.valueOf(position));
                context.startActivity(intent);
            }
        });
        holder.binding.autoDriverName.setText(poolingData.getDriverName());
        holder.binding.rating.setText(poolingData.getRating());
        holder.binding.leavingFromTxt.setText(poolingData.getLeavingFrom());
        holder.binding.goingToTxt.setText(poolingData.getGoingTo());
        holder.binding.startTime.setText(getTimeFromDate(poolingData.getDate()));
        holder.binding.endTime.setText(getDateTimeWithExtraHour(poolingData.getDate()));
        String imageUrl = "https://api.dicebear.com/9.x/avataaars/png?seed="+position;
        if(poolingData.getImageUrl() !=null){
            imageUrl = poolingData.getImageUrl();
        }
        Glide.with(context)
                .load(imageUrl) // Load the profile image URL
                .placeholder(R.drawable.example_image) // Optional placeholder while loading
                .error(R.drawable.example_image) // Optional error image if loading fails
                .into(holder.binding.autoDriverImage);
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
