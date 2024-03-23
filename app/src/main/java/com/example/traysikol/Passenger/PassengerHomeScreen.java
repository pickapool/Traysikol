package com.example.traysikol.Passenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.traysikol.Adapter.PlacesAdapter.PlaceAdapter;
import com.example.traysikol.ChooseAccount;
import com.example.traysikol.Enums.CommuteStatus;
import com.example.traysikol.Enums.OnlineStatus;
import com.example.traysikol.Extensions;
import com.example.traysikol.GlobalClass;
import com.example.traysikol.Models.OSRDirectionModels.ORSFeature;
import com.example.traysikol.Models.OSRDirectionModels.ORSGeometry;
import com.example.traysikol.Models.OSRDirectionModels.ORSResponse;
import com.example.traysikol.Models.OSRPlacesModels.FeatureCollection;
import com.example.traysikol.Models.OSRPlacesModels.Features;
import com.example.traysikol.Models.OnlineDriverModel;
import com.example.traysikol.Models.PassengerRoute;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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
    ImageView home, commute, driversNear, myProfile;
    String myAddress, myDestinationAddress, fare, distance, time = "";
    private View customLayout;
    int lastIcon = 1;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    PassengerRoute passengerRoute = new PassengerRoute();
    DatabaseReference reference;
    List<Marker> driversMarker = new ArrayList<>();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_home_screen);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        toolbar = findViewById(R.id.toolbar);
        autoCompleteTextView = findViewById(R.id.searchPlaces);
        home = findViewById(R.id.home);
        commute = findViewById(R.id.commute);
        driversNear = findViewById(R.id.driversNear);
        myProfile = findViewById(R.id.myProfile);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        reference = FirebaseDatabase.getInstance().getReference();

        ListOfFeatures = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);
        CheckLocationIsOn();

        home.setOnClickListener(view -> ChangeIcons(1));
        commute.setOnClickListener(view -> ChangeIcons(2));
        driversNear.setOnClickListener(view -> {
            reference.child("OnlineDrivers").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    googleMaps.clear();
                    SetLocation(false);
                    for(DataSnapshot snapshot1 : snapshot.getChildren())
                    {
                        OnlineDriverModel oDriver = snapshot1.getValue(OnlineDriverModel.class);
                        if(oDriver.getOnlineStatus() == OnlineStatus.Online) {
                            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.traysikol_icon);
                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);
                            BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                            LatLng lng = new LatLng(oDriver.getLatitude(), oDriver.getLongitude());
                            Marker newMarker = googleMaps.addMarker(new MarkerOptions().position(lng).title(oDriver.getDriverName()).icon(smallMarkerIcon));
                        }
                    }
                    //reference.removeEventListener(this);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            ChangeIcons(3);
        });
        myProfile.setOnClickListener(view ->
        {
            ChangeIcons(4);
            Intent ii = new Intent(PassengerHomeScreen.this, PassengerProfile.class);
            startActivity(ii);
            finish();
        });
        toolbar.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });
        autoCompleteTextView.setOnTouchListener((view, motionEvent) -> {
            final int DRAWABLE_RIGHT = 2;
            if (autoCompleteTextView.getText().toString().length() > 2) {
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
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            // Get the selected item from the adapter
            Features selectedPlace = (Features) parent.getItemAtPosition(position);
            if (selectedPlace != null) {
                autoCompleteTextView.setText(selectedPlace.properties.label);

                LatLng latlng = new LatLng(selectedPlace.geometry.coordinates[1], selectedPlace.geometry.coordinates[0]);
                googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 200));
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
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.drivers) {
                    Toast.makeText(PassengerHomeScreen.this, "asdasd", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.history) {
                    // Handle History menu item click
                } else if (itemId == R.id.myRide) {
                    // Handle reuqest menu item click
                    //Check if existed and
                } else if (itemId == R.id.profile) {
                    // Handle Profile menu item click
                } else if (itemId == R.id.aboutUs) {
                    // Handle About Us menu item click
                } else if (itemId == R.id.logout) {
                    FirebaseAuth.getInstance().signOut();
                    Intent ii = new Intent(PassengerHomeScreen.this, ChooseAccount.class);
                    startActivity(ii);
                    finish();
                }
                return true;
            }
        });
        ChangeIcons(1);
    }

    private void ChangeIcons(int position) {
        lastIcon = position;
        if (position == 2) {
            if(TextUtils.isEmpty(myAddress) ||
                    TextUtils.isEmpty(myDestinationAddress) ||
                    TextUtils.isEmpty("20") ||
                    TextUtils.isEmpty(distance) ||
                    TextUtils.isEmpty(time))
            {
                Toast.makeText(this, "Please select a destination", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        home.setImageResource(R.drawable.home_icon);
        commute.setImageResource(R.drawable.commute_icon);
        driversNear.setImageResource(R.drawable.drivers_icon);
        myProfile.setImageResource(R.drawable.edit_profile_icon);
        switch (position) {
            case 1:
                home.setImageResource(R.drawable.home_icon_selected);
                break;
            case 2:
                commute.setImageResource(R.drawable.commute_icon_selected);
                ShowDialogConfirm(myAddress, myDestinationAddress, "20", distance, time);
                break;
            case 3:
                driversNear.setImageResource(R.drawable.drivers_icon_selected);
                break;
            case 4:
                myProfile.setImageResource(R.drawable.profile_icon_selected);
                break;
            default:
                home.setImageResource(R.drawable.home_icon);
                commute.setImageResource(R.drawable.commute_icon);
                driversNear.setImageResource(R.drawable.drivers_icon);
                myProfile.setImageResource(R.drawable.edit_profile_icon);
                break;
        }
    }

    private void SetDestination(LatLng latLng) {
        GlobalClass.CommuteModel.setPassengerDestinationLatitude(latLng.latitude);
        GlobalClass.CommuteModel.setPassengerDestinationLongitude(latLng.longitude);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.taxi_destination);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 60, 60, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
        Marker newMarker = googleMaps.addMarker(new MarkerOptions().position(latLng).title("Destination").icon(smallMarkerIcon));
        lastMarker = newMarker;
    }

    private void SetLocation(boolean move) {
        myLocation = new LatLng(GlobalClass.currentLocation.get(0).getLatitude(), GlobalClass.currentLocation.get(0).getLongitude());
        // Update the marker icon
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.mylocation);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 60, 60, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
        if(move) {
            googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
        }
        googleMaps.addMarker(new MarkerOptions()
                .position(myLocation)
                .title("Location")
                .icon(smallMarkerIcon));
        GlobalClass.CommuteModel.setPassengerLatitude(myLocation.latitude);
        GlobalClass.CommuteModel.setPassengerLongitude(myLocation.longitude);
    }

    private void CheckLocationIsOn() {
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
                            if (GlobalClass.IsUseLocation) {
                                GlobalClass.currentLocation = geocoder
                                        .getFromLocation(GlobalClass.currentLocation.get(0).getLatitude(),
                                                GlobalClass.currentLocation.get(0).getLongitude(), 1);
                            } else {
                                GlobalClass.currentLocation = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                myAddress = GlobalClass.currentLocation.get(0).getAddressLine(0);
                            }
                            GlobalClass.IsUseLocation = false;
                            SetLocation(true);
                        } catch (IOException e) {
                            //progressBar.dismiss();
                            Toast.makeText(PassengerHomeScreen.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        //progressBar.dismiss();
                        Toast.makeText(PassengerHomeScreen.this, "Please toggle your Location and restart the app.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    //progressBar.dismiss();
                    System.out.println("error" + task.getException().getMessage());
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

    private void DefaultIcons() {
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
            } catch (Exception ee) {
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
                ChangeIcons(1);
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
                String end = String.format("%s,%s", myDestination.longitude, myDestination.latitude);
                //Toast.makeText(PassengerCommute.this, start, Toast.LENGTH_SHORT).show();
                RequestQueue queue = Volley.newRequestQueue(PassengerHomeScreen.this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://api.openrouteservice.org/v2/directions/driving-car?api_key=5b3ce3597851110001cf6248865cba12d4c44f36b368b2b065a07c00&start=" + start + "&end=" + end, new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            googleMaps.clear();

                            SetDestination(myDestination);
                            SetLocation(true);

                            Gson gson = new Gson();
                            ORSResponse orsResponse = gson.fromJson(response, ORSResponse.class);
                            ORSFeature feature = orsResponse.features.get(0);

                            ORSGeometry geometry = feature.geometry;

                            if (geometry != null && "LineString".equals(geometry.type)) {
                                passengerRoute.setPassengerRoute(Extensions.convertToList(geometry.coordinates));
                                GlobalClass.CommuteModel.setPassengerRoute(passengerRoute);
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

                                //Get address to
                                Geocoder geocoder = new Geocoder(PassengerHomeScreen.this, Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(orsResponse.metadata.query.coordinates[1][1], orsResponse.metadata.query.coordinates[1][0], 1);
                                myDestinationAddress = addresses.get(0).getAddressLine(0);
                                distance = GlobalClass.convertDistance(GlobalClass.GetDistance(orsResponse));
                                time = GlobalClass.GetTime(orsResponse);
                                GlobalClass.CommuteModel.setDistance(distance);
                                GlobalClass.CommuteModel.setFare("20");

                            }
                            dialog.dismiss();

                        } catch (Exception ee) {
                            dialog.dismiss();
                            System.out.println("error1 - " + ee.getMessage());
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
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

    public static DisplayMetrics getDeviceMetrics(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(metrics);
        return metrics;
    }

    //Address-> Address address = GlobalClass.currentLocation.get(0); -> String addressLine = address.getAddressLine(0);
    private void ShowDialogConfirm(String address1, String address2, String fare, String distance, String time) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(PassengerHomeScreen.this);
            View customLayout = getLayoutInflater().inflate(R.layout.layout_confirm_dialog, null);
            builder.setView(customLayout);
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setDimAmount(0);

            WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
            lWindowParams.copyFrom(dialog.getWindow().getAttributes());
            lWindowParams.width = 510;
            lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;


            TextView add1 = customLayout.findViewById(R.id.address1);
            TextView add2 = customLayout.findViewById(R.id.address2);
            TextView fares = customLayout.findViewById(R.id.fare);
            TextView dis = customLayout.findViewById(R.id.distance);
            TextView tim = customLayout.findViewById(R.id.times);
            ImageView close = customLayout.findViewById(R.id.closeDialog);
            Button confirmBtn = customLayout.findViewById(R.id.confirmCommute);
            LinearLayout btns = customLayout.findViewById(R.id.buttonsRide);
            Button rideNow = customLayout.findViewById(R.id.rideNow);
            Button ridelater = customLayout.findViewById(R.id.rideLater);

            add1.setText(address1);
            add2.setText(address2);
            fares.setText("₱ " + fare);
            dis.setText(distance);
            tim.setText("Estimated duration: " + time);

            close.setOnClickListener(view -> {
                dialog.dismiss();
                ChangeIcons(1);
            });
            rideNow.setOnClickListener(view -> {
                RideNow();
            });
            ridelater.setOnClickListener(view -> {
                dialog.dismiss();
                PassengerCountDown countDown = new PassengerCountDown(myAddress, myDestinationAddress, fare, distance, time);
                countDown.show(getSupportFragmentManager(), "PassengerHomeScreen");
            });
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right);
                    view.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            confirmBtn.setVisibility(View.GONE);
                            btns.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            });

            dialog.show();
            dialog.getWindow().setAttributes(lWindowParams);
        } catch (Exception ee) {
            System.out.println("66213213" + ee.getMessage());
        }
    }
    private void RideNow()
    {
        String key = reference.push().getKey();

        GlobalClass.CommuteModel.setDriverUid("");
        GlobalClass.CommuteModel.setCommuteStatus(CommuteStatus.InProgress);
        GlobalClass.CommuteModel.setOccupied(false);
        GlobalClass.CommuteModel.setKey(key);
        GlobalClass.CommuteModel.setPassengerUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        GlobalClass.CommuteModel.setCommuteDate(Calendar.getInstance().getTime());

        reference.child("Commutes").child(key).setValue(GlobalClass.CommuteModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ProgressDialog progressDialog = new ProgressDialog(PassengerHomeScreen.this);
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Sending to drivers");
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                if(task.isSuccessful())
                {
                    Intent ii = new Intent(PassengerHomeScreen.this, PassengerDoneRequest.class);
                    startActivity(ii);
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(PassengerHomeScreen.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}