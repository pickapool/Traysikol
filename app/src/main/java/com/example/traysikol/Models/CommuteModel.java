package com.example.traysikol.Models;

import com.example.traysikol.Enums.CommuteStatus;
import com.example.traysikol.Models.OSRDirectionModels.ORSGeometry;

import java.util.Date;

public class CommuteModel {
    public String key;
    public String passengerUid;
    public String driverUid;
    public double passengerLatitude;
    public double passengerLongitude;
    public double passengerDestinationLatitude;
    public double passengerDestinationLongitude;
    public PassengerRoute passengerRoute;
    public boolean isOccupied;
    public CommuteStatus commuteStatus;
    public Date commuteDate;
    public String distance;
    public String fare;

    public CommuteModel (){}

    public CommuteModel(String key, String passengerUid, String driverUid, double passengerLatitude, double passengerLongitude, double passengerDestinationLatitude, double passengerDestinationLongitude, PassengerRoute passengerRoute, boolean isOccupied, CommuteStatus commuteStatus, Date commuteDate, String distance, String fare) {
        this.key = key;
        this.passengerUid = passengerUid;
        this.driverUid = driverUid;
        this.passengerLatitude = passengerLatitude;
        this.passengerLongitude = passengerLongitude;
        this.passengerDestinationLatitude = passengerDestinationLatitude;
        this.passengerDestinationLongitude = passengerDestinationLongitude;
        this.passengerRoute = passengerRoute;
        this.isOccupied = isOccupied;
        this.commuteStatus = commuteStatus;
        this.commuteDate = commuteDate;
        this.distance = distance;
        this.fare = fare;
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

    public String getDriverUid() {
        return driverUid;
    }

    public void setDriverUid(String driverUid) {
        this.driverUid = driverUid;
    }

    public double getPassengerLatitude() {
        return passengerLatitude;
    }

    public void setPassengerLatitude(double passengerLatitude) {
        this.passengerLatitude = passengerLatitude;
    }

    public double getPassengerLongitude() {
        return passengerLongitude;
    }

    public void setPassengerLongitude(double passengerLongitude) {
        this.passengerLongitude = passengerLongitude;
    }

    public double getPassengerDestinationLatitude() {
        return passengerDestinationLatitude;
    }

    public void setPassengerDestinationLatitude(double passengerDestinationLatitude) {
        this.passengerDestinationLatitude = passengerDestinationLatitude;
    }

    public double getPassengerDestinationLongitude() {
        return passengerDestinationLongitude;
    }

    public void setPassengerDestinationLongitude(double passengerDestinationLongitude) {
        this.passengerDestinationLongitude = passengerDestinationLongitude;
    }

    public PassengerRoute getPassengerRoute() {
        return passengerRoute;
    }

    public void setPassengerRoute(PassengerRoute passengerRoute) {
        this.passengerRoute = passengerRoute;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public CommuteStatus getCommuteStatus() {
        return commuteStatus;
    }

    public void setCommuteStatus(CommuteStatus commuteStatus) {
        this.commuteStatus = commuteStatus;
    }

    public Date getCommuteDate() {
        return commuteDate;
    }

    public void setCommuteDate(Date commuteDate) {
        this.commuteDate = commuteDate;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }
}
