package com.example.traysikol.Models;

public class SaveDestinationModel {
    public String key;
    public String passengerUid;
    public double latitude;
    public double longitude;
    public String address;
    public SaveDestinationModel(){}

    public SaveDestinationModel(String key, String passengerUid, double latitude, double longitude, String address) {
        this.key = key;
        this.passengerUid = passengerUid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPassengerUid() {
        return passengerUid;
    }

    public void setPassengerUid(String passengerUid) {
        this.passengerUid = passengerUid;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
