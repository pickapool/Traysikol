package com.example.traysikol.Models;

import com.example.traysikol.Enums.OnlineStatus;

public class OnlineDriverModel {
    public String driverUid;
    public double latitude;
    public double longitude;
    public OnlineStatus onlineStatus;
    public String driverName;

    public OnlineDriverModel(){}

    public OnlineDriverModel(String driverUid, double latitude, double longitude, OnlineStatus onlineStatus, String driverName) {
        this.driverUid = driverUid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.onlineStatus = onlineStatus;
        this.driverName = driverName;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverUid() {
        return driverUid;
    }

    public void setDriverUid(String driverUid) {
        this.driverUid = driverUid;
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

    public OnlineStatus getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(OnlineStatus onlineStatus) {
        this.onlineStatus = onlineStatus;
    }
}
