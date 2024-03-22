package com.example.traysikol.Models;

import java.util.ArrayList;
import java.util.List;

public class PassengerRoute {
    List<List<Double>> passengerRoute;
    public  PassengerRoute(){}

    public PassengerRoute(List<List<Double>> passengerRoute) {
        this.passengerRoute = passengerRoute;
    }

    public List<List<Double>> getPassengerRoute() {
        return passengerRoute;
    }

    public void setPassengerRoute(List<List<Double>> passengerRoute) {
        this.passengerRoute = passengerRoute;
    }
}
