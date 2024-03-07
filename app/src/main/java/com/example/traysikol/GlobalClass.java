package com.example.traysikol;

import android.location.Address;

import java.text.DecimalFormat;
import java.util.List;

public class GlobalClass {

    public static List<Address> currentLocation = null;
    private static DecimalFormat df = new DecimalFormat("#.##");

    public static String convertDistance(double distance) {
        if (distance >= 1000) {
            double kilometers = distance / 1000.0;
            return "("+df.format(kilometers) + " km)";
        } else {
            return "("+df.format(distance) + " m)";
        }
    }
    public static String convertMinutesToHoursAndMinutes(double minutes) {
        double hours = minutes / 60;
        double remainingMinutes = minutes % 60;

        if (hours > 0 && remainingMinutes > 0) {
            return df.format(hours) + " hours" + remainingMinutes + " mins";
        } else if (hours > 0) {
            return hours + " hours";
        } else {
            return remainingMinutes + " mins";
        }
    }
}
