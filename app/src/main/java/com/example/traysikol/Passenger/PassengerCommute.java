package com.example.traysikol.Passenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.traysikol.GlobalClass;
import com.example.traysikol.Models.OSRDirectionModels.ORSFeature;
import com.example.traysikol.Models.OSRDirectionModels.ORSGeometry;
import com.example.traysikol.Models.OSRDirectionModels.ORSProperties;
import com.example.traysikol.Models.OSRDirectionModels.ORSResponse;
import com.example.traysikol.Models.OSRDirectionModels.ORSSegment;
import com.example.traysikol.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
public class PassengerCommute extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap googleMaps;
    Handler handler = new Handler();
    Runnable runnable;
    Marker lastMarker = null;
    LatLng myLocation = null;
    LatLng myDestination = null;
    TextView distance, time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_commute);

        time = findViewById(R.id.minutes);
        distance = findViewById(R.id.distance);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMaps = googleMap;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        googleMaps.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                ProgressDialog dialog = new ProgressDialog(PassengerCommute.this);
                dialog.setTitle("Getting route");
                dialog.setMessage("Loading...");
                dialog.show();

                myDestination = latLng;
                if (lastMarker != null) {
                    lastMarker.remove();
                }

                SetDestination(latLng);
                String start = String.format("%s,%s", myLocation.longitude, myLocation.latitude);
                String end = String.format("%s,%s", myDestination.longitude,myDestination.latitude);
                //Toast.makeText(PassengerCommute.this, start, Toast.LENGTH_SHORT).show();
                RequestQueue queue = Volley.newRequestQueue(PassengerCommute.this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET,"https://api.openrouteservice.org/v2/directions/driving-car?api_key=5b3ce3597851110001cf6248865cba12d4c44f36b368b2b065a07c00&start="+start+"&end="+end, new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            googleMaps.clear();

                            SetDestination(myDestination);
                            SetLocation();

                            Gson gson = new Gson();
                            ORSResponse orsResponse = gson.fromJson(response, ORSResponse.class);
                            ORSFeature feature = orsResponse.features.get(0);

                            ORSGeometry geometry = feature.geometry;

                            if (geometry != null && "LineString".equals(geometry.type)) {
                                double[][] coordinates = geometry.coordinates;

                                PolylineOptions polylineOptions = new PolylineOptions();
                                for (double[] coordinate : coordinates) {
                                    double latitude = coordinate[1];
                                    double longitude = coordinate[0];
                                    LatLng latLng = new LatLng(latitude, longitude);
                                    polylineOptions.add(latLng);
                                }

                                //Set disantance and time
                                Polyline polyline = googleMap.addPolyline(polylineOptions);
                                String distances = String.format("%.2f",GetDistance(orsResponse));
                                distance.setText(GlobalClass.convertDistance(GetDistance(orsResponse)));
                                time.setText(String.valueOf(GetTime(orsResponse)));

                            }
                            dialog.dismiss();

                        } catch (Exception ee)
                        {
                            dialog.dismiss();
                            System.out.println("error1 - " + ee.getMessage());
                        }
                    }
                },new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("error2 - " + error);
                        Toast.makeText(PassengerCommute.this, String.valueOf(error), Toast.LENGTH_SHORT).show();
                    }
                });
                queue.add(stringRequest);
            }
        });
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
    @Override
    protected void onResume() {
        handler.postDelayed(runnable = () -> {
            //Tibiao zoom in map
            //LatLng move = new LatLng(11.156106,122.054483);

            googleMaps.clear();
            googleMaps.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            if(googleMaps !=null)
            {
                SetLocation();
                googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation , 15));
            }
            handler.postDelayed(runnable,2000);
            handler.removeCallbacks(runnable);
        }, 2000);
        super.onResume();
    }
    private void SetDestination(LatLng latLng)
    {
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.taxi_destination);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 60, 60, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
        Marker newMarker = googleMaps.addMarker(new MarkerOptions().position(latLng).title("Destination").icon(smallMarkerIcon));
        lastMarker = newMarker;
    }
    private void SetLocation()
    {
        myLocation = new LatLng(GlobalClass.currentLocation.get(0).getLatitude(),GlobalClass.currentLocation.get(0).getLongitude());
        // Update the marker icon
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.mylocation);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 60, 60, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

        googleMaps.addMarker(new MarkerOptions()
                .position(myLocation)
                .title("Your here")
                .icon(smallMarkerIcon));
    }
    private double GetDistance(ORSResponse orsResponse)
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
    private String GetTime(ORSResponse orsResponse) {
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
    private String PlacesAPI(String query)
    {
        return "\n" +
                "https://api.openrouteservice.org/geocode/autocomplete?api_key=5b3ce3597851110001cf6248865cba12d4c44f36b368b2b065a07c00&text="+query;
    }
}