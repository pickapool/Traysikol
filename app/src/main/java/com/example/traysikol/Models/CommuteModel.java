package com.example.traysikol.Models;

import com.example.traysikol.Enums.CommuteStatus;
import com.example.traysikol.Models.OSRDirectionModels.ORSGeometry;
import com.google.firebase.database.Exclude;

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
    public String time;
    public String address1;
    public String address2;
    public float rating;
    @Exclude
    public UserAccountModel DriverAccount;
    @Exclude
    public UserAccountModel PassengerAccount;
    private boolean isRated;

    public CommuteModel (){}

    public CommuteModel(String key, String passengerUid, String driverUid, double passengerLatitude,
                        double passengerLongitude, double passengerDestinationLatitude, double passengerDestinationLongitude,
                        PassengerRoute passengerRoute, boolean isOccupied, CommuteStatus commuteStatus, Date commuteDate,
                        String distance, String fare, String time, String address1, String address2, float rating,
                        UserAccountModel driverAccount, UserAccountModel passengerAccount,
                        Boolean isRated) {
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
        this.time = time;
        this.address1 = address1;
        this.address2 = address2;
        this.rating = rating;
        this.isRated = isRated;
        DriverAccount = driverAccount;
        PassengerAccount = passengerAccount;
    }

    public boolean isRated() {
        return isRated;
    }

    public void setRated(boolean rated) {
        isRated = rated;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public UserAccountModel getDriverAccount() {
        return DriverAccount;
    }

    public void setDriverAccount(UserAccountModel driverAccount) {
        DriverAccount = driverAccount;
    }

    public UserAccountModel getPassengerAccount() {
        return PassengerAccount;
    }

    public void setPassengerAccount(UserAccountModel passengerAccount) {
        PassengerAccount = passengerAccount;
    }
}
