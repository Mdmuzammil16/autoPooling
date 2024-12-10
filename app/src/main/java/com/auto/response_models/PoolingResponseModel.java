package com.auto.response_models;

import java.util.ArrayList;
import java.util.Date;

public class PoolingResponseModel {

    public PoolingResponseModel(String docId, String driverName, String rating, Date date, String leavingFrom, String goingTo,Long passenger,ArrayList<Double> bookedSeats) {
        this.docId = docId;
        this.driverName = driverName;
        this.rating = rating;
        this.date = date;
        this.leavingFrom = leavingFrom;
        this.goingTo = goingTo;
        this.passenger = passenger;
        this.bookedSeats = bookedSeats;
    }

    private final String docId;

    private ArrayList<Double> bookedSeats;
    private String driverName;

    private String rating;

    private Date date;

    private String leavingFrom;

    private String goingTo;

    private Long passenger;

    public String getDocId() {
        return docId;
    }

    public Long getPassenger() {
        return passenger;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public void setPassenger(Long passenger) {
        this.passenger = passenger;
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

    // Constructor







}

