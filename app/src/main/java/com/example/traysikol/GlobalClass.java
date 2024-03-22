package com.example.traysikol;

import android.accounts.Account;
import android.location.Address;
import android.location.Location;

import com.example.traysikol.Enums.AccountType;
import com.example.traysikol.Models.CommuteModel;
import com.example.traysikol.Models.OSRDirectionModels.ORSFeature;
import com.example.traysikol.Models.OSRDirectionModels.ORSGeometry;
import com.example.traysikol.Models.OSRDirectionModels.ORSProperties;
import com.example.traysikol.Models.OSRDirectionModels.ORSResponse;
import com.example.traysikol.Models.OSRDirectionModels.ORSSegment;
import com.example.traysikol.Models.UserAccountModel;

import java.text.DecimalFormat;
import java.util.List;

public class GlobalClass {

    public static List<Address> currentLocation = null;
    public static AccountType AccountType;
    public static UserAccountModel UserAccount = new UserAccountModel();
    public static CommuteModel CommuteModel = new CommuteModel();
    private static DecimalFormat df = new DecimalFormat("#.##");

    public static String convertDistance(double distance) {
        if (distance >= 1000) {
            double kilometers = distance / 1000.0;
            return "("+df.format(kilometers) + " km)";
        } else {
            return "("+df.format(distance) + " m)";
        }
    }
    public static double GetDistance(ORSResponse orsResponse)
    {
        float totalDistance = 0.0F;
        ORSGeometry geometry = orsResponse.features.get(0).geometry;
        double[][] coordinates = geometry.coordinates;

        for (int i = 1; i < coordinates.length; i++) {
            double[] start = coordinates[i - 1];
            double[] end = coordinates[i];

            float[] results = new float[1];
            Location.distanceBetween(start[1], start[0], end[1], end[0], results);

            // Add the distance between consecutive points to the total distance
            totalDistance += results[0];
        }
        return  totalDistance;
    }
    public static String GetTime(ORSResponse orsResponse) {
        double totalTimeSeconds = 0.0; // Total time in seconds

        for (ORSFeature feature : orsResponse.features) {
            ORSProperties properties = feature.properties;
            for (ORSSegment segment : properties.segments) {
                double durationSeconds = segment.duration;
                totalTimeSeconds += durationSeconds; // Add to total duration
            }
        }

        // Convert total duration to hours and minutes
        int hours = (int) (totalTimeSeconds / 3600);
        int minutes = (int) ((totalTimeSeconds % 3600) / 60);

        if (hours > 0 && minutes > 0) {
            // Return hours and minutes format
            return hours + " hour" + (hours > 1 ? "s" : "") + " " + minutes + " minute" + (minutes > 1 ? "s" : "");
        } else if (hours > 0) {
            // Return hours only format
            return hours + " hour" + (hours > 1 ? "s" : "");
        } else {
            // Return minutes only format
            return minutes + " minute" + (minutes > 1 ? "s" : "");
        }
    }
    public static String PlacesAPI(String query)
    {
        return "https://api.openrouteservice.org/geocode/autocomplete?api_key=5b3ce3597851110001cf6248865cba12d4c44f36b368b2b065a07c00&text="+query;
    }
}
