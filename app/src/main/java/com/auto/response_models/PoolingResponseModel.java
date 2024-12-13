package com.auto.response_models;

import java.util.ArrayList;
import java.util.Date;

public class PoolingResponseModel {

    public PoolingResponseModel(String docId,String imageUrl,String driverName, String rating, Date date, String leavingFrom, String goingTo,ArrayList<Double> bookedSeats) {
        this.docId = docId;
        this.imageUrl = imageUrl;
        this.driverName = driverName;
        this.rating = rating;
        this.date = date;
        this.leavingFrom = leavingFrom;
        this.goingTo = goingTo;
        this.bookedSeats = bookedSeats;
    }

    private final String docId;

    private ArrayList<Double> bookedSeats;
    private String driverName;

    private String imageUrl;

    private String rating;

    private Date date;

    private String leavingFrom;

    private String goingTo;


    public String getDocId() {
        return docId;
    }


    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }


    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLeavingFrom() {
        return leavingFrom;
    }

    public void setLeavingFrom(String leavingFrom) {
        this.leavingFrom = leavingFrom;
    }

    public String getGoingTo() {
        return goingTo;
    }

    public void setGoingTo(String goingTo) {
        this.goingTo = goingTo;
    }

    public ArrayList<Double> getBookedSeats() {
        return bookedSeats;
    }

    public void setBookedSeats(ArrayList<Double> bookedSeats) {
        this.bookedSeats = bookedSeats;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Constructor







}

