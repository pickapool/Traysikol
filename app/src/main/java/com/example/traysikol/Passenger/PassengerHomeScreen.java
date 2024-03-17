package com.example.traysikol.Passenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.PathSegment;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.traysikol.Adapter.PlacesAdapter.PlaceAdapter;
import com.example.traysikol.GlobalClass;
import com.example.traysikol.Models.OSRDirectionModels.ORSFeature;
import com.example.traysikol.Models.OSRDirectionModels.ORSGeometry;
import com.example.traysikol.Models.OSRDirectionModels.ORSResponse;
import com.example.traysikol.Models.OSRPlacesModels.FeatureCollection;
import com.example.traysikol.Models.OSRPlacesModels.Features;
import com.example.traysikol.R;
import com.example.traysikol.RotatingDrawable;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PassengerHomeScreen extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap googleMaps;
    Marker lastMarker = null;
    LatLng myLocation = null;
    LatLng myDestination = null;
    Runnable runnable;
    FusedLocationProviderClient fusedLocationProviderClient;
    ImageView toolbar;
    private AutoCompleteTextView autoCompleteTextView;
    List<Features> ListOfFeatures;
    Handler handler = new Handler();
    private PlaceAdapter adapter;
    private RotatingDrawable rotatingDrawable;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_home_screen);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        toolbar = findViewById(R.id.toolbar);
        autoCompleteTextView = findViewById(R.id.searchPlaces);

        ListOfFeatures = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);
        CheckLocationIsOn();

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        autoCompleteTextView.setOnTouchListener((view, motionEvent) -> {
            final int DRAWABLE_RIGHT = 2;
            if(autoCompleteTextView.getText().toString().length() > 2) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= (autoCompleteTextView.getRight() - autoCompleteTextView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        setCompoundDrawable(R.drawable.search_animation);
                        SearchPlaces(autoCompleteTextView.getText().toString());
                        return true;
                    }
                }
            }
            return false;
        });
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item from the adapter
                Features selectedPlace = (Features) parent.getItemAtPosition(position);
                if (selectedPlace != null) {
                    autoCompleteTextView.setText(selectedPlace.properties.label);

                    LatLng latlng = new LatLng(selectedPlace.geometry.coordinates[1], selectedPlace.geometry.coordinates[0]);
                    googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng , 200));
                }
            }
        });
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                final String newText = editable.toString().toLowerCase(Locale.getDefault());

                if (newText.length() >= 3) { // Adjust the minimum text length as needed
                    handler.removeCallbacksAndMessages(null); // Cancel previous delayed tasks
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            filterFeatures(newText);
                        }
                    }, 400);
                }
            }
        });
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
        googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation , 15));
        googleMaps.addMarker(new MarkerOptions()
                .position(myLocation)
                .title("Your here")
                .icon(smallMarkerIcon));
    }
    private void CheckLocationIsOn()
    {
        ProgressDialog progressBar = new ProgressDialog(PassengerHomeScreen.this);
        progressBar.setTitle("Getting Location");
        progressBar.setMessage("Please wait...");
        progressBar.setCancelable(false);
        progressBar.show();
        if (ActivityCompat.checkSelfPermission(PassengerHomeScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(PassengerHomeScreen.this, Locale.getDefault());
                        try {
                            progressBar.dismiss();
                            GlobalClass.currentLocation = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            SetLocation();
                        } catch (IOException e) {
                            //progressBar.dismiss();
                            Toast.makeText(PassengerHomeScreen.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        //progressBar.dismiss();
                        Toast.makeText(PassengerHomeScreen.this, "Please toggle your Location and restart the app.", Toast.LENGTH_LONG).show();
                    }
                } else{
                    //progressBar.dismiss();
                    System.out.println("error"+ task.getException().getMessage());
                    Toast.makeText(PassengerHomeScreen.this, "Sorry, there was a problem getting you location.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            //progressBar.dismiss();
            Toast.makeText(PassengerHomeScreen.this, "No permission granted. Please reisntall the app.", Toast.LENGTH_SHORT).show();
        }
    }
    private void filterFeatures(String newText) {
        List<Features> filteredFeatures = new ArrayList<>();
        for (Features feature : ListOfFeatures) {
            if (feature.properties.label.toLowerCase(Locale.getDefault()).contains(newText)) {
                filteredFeatures.add(feature);
            }
        }
        updateAdapter(filteredFeatures);
    }
    private void updateAdapter(List<Features> filteredFeatures) {
        adapter = new PlaceAdapter(PassengerHomeScreen.this, filteredFeatures);
        autoCompleteTextView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    private void setCompoundDrawable(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        if (drawable != null) {
            rotatingDrawable = new RotatingDrawable(this, R.drawable.search_animation);
            autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, rotatingDrawable, null);
            // Start the rotation animation
            rotatingDrawable.start();
        } else {
            DefaultIcons();
        }
    }
    private void DefaultIcons()
    {
        Drawable drawable1 = getResources().getDrawable(R.drawable.search_alt_2_svgrepo_com);
        autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable1, null);
    }
    private void SearchPlaces(String query) {
        ListOfFeatures = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(PassengerHomeScreen.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                GlobalClass.PlacesAPI(query), response -> {
            try {
                Gson gson = new Gson();
                FeatureCollection featureCollection = gson.fromJson(response, FeatureCollection.class);
                for (Features features : featureCollection.features) {
                    ListOfFeatures.add(features);
                }
                //Toast.makeText(PassengerCommute.this, String.valueOf(ListOfFeatures.size()), Toast.LENGTH_SHORT).show();
                updateAdapter(ListOfFeatures);
                DefaultIcons();
            } catch (Exception ee)
            {
                DefaultIcons();
                System.out.println("error4 - " + ee.getMessage());
            }
        }, error -> {
            System.out.println("error5 - " + error);
            DefaultIcons();
            Toast.makeText(PassengerHomeScreen.this, String.valueOf(error), Toast.LENGTH_SHORT).show();
        });
        queue.add(stringRequest);
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

                ProgressDialog dialog = new ProgressDialog(PassengerHomeScreen.this);
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
                RequestQueue queue = Volley.newRequestQueue(PassengerHomeScreen.this);
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
                                //istance.setText(GlobalClass.convertDistance(GlobalClass.GetDistance(orsResponse)));
                                //time.setText(String.valueOf(GlobalClass.GetTime(orsResponse)));

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
                        Toast.makeText(PassengerHomeScreen.this, String.valueOf(error), Toast.LENGTH_SHORT).show();
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
}