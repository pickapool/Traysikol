package com.example.traysikol.Drivers;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.traysikol.Adapter.GoogleMarkerAdapter;
import com.example.traysikol.CurrentRequest;
import com.example.traysikol.Enums.AccountType;
import com.example.traysikol.Enums.CommuteStatus;
import com.example.traysikol.Enums.OnlineStatus;
import com.example.traysikol.GlobalClass;
import com.example.traysikol.MainActivity;
import com.example.traysikol.Models.CommuteModel;
import com.example.traysikol.Models.OnlineDriverModel;
import com.example.traysikol.Models.UserAccountModel;
import com.example.traysikol.Passenger.PassengerHomeScreen;
import com.example.traysikol.Passenger.PassengerProfile;
import com.example.traysikol.Passenger.PassengerProfileDetails;
import com.example.traysikol.R;
import com.example.traysikol.Services.RequestADriverService;
import com.example.traysikol.UniqueRandomGenerator;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;

public class DriversHome extends AppCompatActivity implements OnMapReadyCallback {
    DatabaseReference reference;
    FusedLocationProviderClient fusedLocationProviderClient;
    GoogleMap googleMaps;
    ImageView home, myRequest, myProfile;
    LatLng myLocation = null;
    List<CommuteModel> CommuteModels;
    ImageView commute;
    CommuteModel CurrentCommute = null;
    ImageView toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drviers_home);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        reference = FirebaseDatabase.getInstance().getReference();
        myRequest = findViewById(R.id.currentRequest);
        myProfile = findViewById(R.id.myProfile);
        commute = findViewById(R.id.commute);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        user = FirebaseAuth.getInstance().getCurrentUser();

        View headerView = navigationView.getHeaderView(0);
        CircleImageView pp = headerView.findViewById(R.id.profilePicture);
        TextView fullname = headerView.findViewById(R.id.fullName);
        TextView address = headerView.findViewById(R.id.address);

        fullname.setText(GlobalClass.UserAccount.getFullName());
        address.setText(TextUtils.isEmpty(GlobalClass.UserAccount.getAddress()) ? "Address not found" : GlobalClass.UserAccount.getAddress());
        if(!TextUtils.isEmpty(GlobalClass.UserAccount.getProfilePicture())) {
            Picasso.get().load(GlobalClass.UserAccount.getProfilePicture()).into(pp);
        }

        Intent serviceIntent = new Intent(DriversHome.this, RequestADriverService.class);
        toolbar.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.drivers) {

                } else if(itemId == R.id.history)
                {
                    Intent ii = new Intent(DriversHome.this, DriversHistory.class);
                    startActivity(ii);
                    finish();
                }
                else if(itemId == R.id.logout)
                {
                    stopService(serviceIntent);
                    SignOut();
                } else if (itemId == R.id.myRide)
                {
                    int count = 0;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        List<CommuteModel>  list = CommuteModels.stream().filter(e ->
                                e.commuteStatus == CommuteStatus.InProgress && e.isOccupied()
                        ).collect(Collectors.toList());
                        count = list.size();
                        if (count > 0) {
                            CommuteModel currentRequest =  list.get(0);
                            CurrentRequest current = new CurrentRequest(currentRequest);
                            current.show(getSupportFragmentManager(), "DriversHome");
                        } else {
                            Toast.makeText(DriversHome.this, "There are no current trips.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if(itemId == R.id.contactUs) {
                    showDialog("Got questions, feedback, or concerns? We’re here to help! Reach out to us through the following channels:  \n" +
                            "- Email TRICYRIDE@gmail.com  \n" +
                            "- Phone: (+63) 965-955-2562  \n" +
                            "\n" +
                            "\n" +
                            "You can also follow us on social media for updates and announcements:  \n" +
                            "- *Facebook:* [TricyRide Official\n" +
                            "- Instagram: [@TricyRidePH](#)  \n", "Contact Us");
                } else if (itemId == R.id.aboutUs) {
                    showDialog("Welcome to TricyRide, your trusted partner in enhancing transportation convenience for communities.\n" +
                            "\n" +
                            "At TricyRide, we aim to:  \n" +
                            "- Increase Transportation Availability:*Connecting passengers with nearby drivers for hassle-free trips.  \n" +
                            "- Reduce Waiting Times: Save time by booking your ride instantly through our app.  \n" +
                            "- Streamline Bookings: Enjoy a user-friendly platform for efficient ride reservations.  \n" +
                            "- Promote Safety and Reliability:Work with verified tricycle drivers for peace of mind.  \n" +
                            "- Foster Local Empowerment:Support our hardworking local tricycle drivers by providing a reliable income source.  \n" +
                            "\n" +
                            "We are committed to transforming your travel into a seamless, enjoyable experience while supporting the local economy.  \n",

                            "About Us");
                } else {
                    /*SignOut();
                    stopService(serviceIntent);
                    Intent ii = new Intent(DriversHome.this, MainActivity.class);
                    startActivity(ii);
                    finish();*/
                }
                return  true;
            }
        });
        commute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!user.isEmailVerified()) {
                    Toast.makeText(DriversHome.this, "Account is not verified, go to profile and get the verification.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(CurrentCommute == null)
                {
                    Toast.makeText(DriversHome.this, "No selected trip!", Toast.LENGTH_SHORT).show();
                    return;
                }
                ShowCommuteInfo(CurrentCommute);
            }
        });
        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii =new Intent(DriversHome.this, PassengerProfile.class);
                startActivity(ii);
                finish();
            }
        });

        myRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    List<CommuteModel>  list = CommuteModels.stream().filter(e ->
                            e.commuteStatus == CommuteStatus.InProgress && e.isOccupied()
                    ).collect(Collectors.toList());
                    count = list.size();
                    if (count > 0) {
                        CommuteModel currentRequest =  list.get(0);
                        CurrentRequest current = new CurrentRequest(currentRequest);
                        current.show(getSupportFragmentManager(), "DriversHome");
                    } else {
                        Toast.makeText(DriversHome.this, "There are no current trips.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMapDrivers);
        mapFragment.getMapAsync(this);
        try {
            stopService(serviceIntent);
            startService(serviceIntent);
        } catch (Exception ee) {

        }
        home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetLocation(true);
            }
        });

        //remove driver then set again
        reference.child("OnlineDrivers")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (ActivityCompat.checkSelfPermission(DriversHome.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                    && ActivityCompat.checkSelfPermission(DriversHome.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(DriversHome.this, "Location not granted", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Location location = task.getResult();
                                    if (location != null) {
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("latitude", location.getLatitude());
                                        hashMap.put("longitude", location.getLongitude());
                                        hashMap.put("onlineStatus", OnlineStatus.Online);
                                        myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                        reference.child("OnlineDrivers").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .updateChildren(hashMap);
                                        SetLocation(true);
                                        GetCommutes();
                                    } else {
                                        //progressBar.dismiss();
                                        Toast.makeText(DriversHome.this, "Please toggle your Location and restart the app.", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    //progressBar.dismiss();
                                    System.out.println("errore21312" + task.getException().getMessage());
                                    Toast.makeText(DriversHome.this, "Sorry, there was a problem getting you location.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            CheckLocationIsOn();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        GetMyCommutes();

    }
    private void showDialog(String message, String title) {
        // Create an AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the dialog title
        builder.setTitle(title);

        // Set the dialog message (this is the text displayed in the dialog)
        builder.setMessage(message);

        // Set a positive button (OK button) and its click listener
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the click event for the OK button
                dialog.dismiss();
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        // Show the dialog
        dialog.show();
    }
    private void SignOut()
    {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("latitude", 0);
        hashMap.put("longitude", 0);
        hashMap.put("onlineStatus", OnlineStatus.Offline);
        reference.child("OnlineDrivers").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .updateChildren(hashMap);
        FirebaseAuth.getInstance().signOut();
        Intent ii = new Intent(DriversHome.this, MainActivity.class);
        startActivity(ii);
        finish();
    }

    private void CheckLocationIsOn() {
        ProgressDialog progressBar = new ProgressDialog(DriversHome.this);
        progressBar.setTitle("Getting Location");
        progressBar.setMessage("Please wait...");
        progressBar.setCancelable(false);
        progressBar.show();
        //if (ActivityCompat.checkSelfPermission(DriversHome.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location not granted", Toast.LENGTH_SHORT).show();
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location location = task.getResult();
                if (location != null) {
                    OnlineDriverModel model = new OnlineDriverModel();
                    model.setDriverUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    model.setLatitude(location.getLatitude());
                    model.setLongitude(location.getLongitude());
                    model.setOnlineStatus(OnlineStatus.Online);
                    model.setDriverName(GlobalClass.UserAccount.getFullName());
                    myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    reference.child("OnlineDrivers").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(model);
                    SetLocation(true);
                    GetCommutes();
                    progressBar.dismiss();
                } else {
                    //progressBar.dismiss();
                    Toast.makeText(DriversHome.this, "Please toggle your Location and restart the app.", Toast.LENGTH_LONG).show();
                }
            } else {
                //progressBar.dismiss();
                System.out.println("errore21312" + task.getException().getMessage());
                Toast.makeText(DriversHome.this, "Sorry, there was a problem getting your location.", Toast.LENGTH_SHORT).show();
            }
        });
        /*} else {
            //progressBar.dismiss();
            Toast.makeText(DriversHome.this, "No permission granted. Please reinstall the app.", Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (GlobalClass.UserAccount.getAccountType() == AccountType.Driver) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("latitude", 0);
            hashMap.put("longitude", 0);
            hashMap.put("onlineStatus", OnlineStatus.Offline);
            reference.child("OnlineDrivers").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .updateChildren(hashMap);
        }
        super.onPause();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMaps = googleMap;
        googleMaps.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                CommuteModel model = (CommuteModel) marker.getTag();
                if (model != null) {
                    CurrentCommute = model;
                    marker.showInfoWindow();
                }
                return true;
            }
        });
    }

    private void SetLocation(boolean moveCamera) {
        // Update the marker icon
        if(myLocation != null) {
            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.traysikol_icon);
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 60, 60, false);
            BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
            if (moveCamera)
                googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
            googleMaps.addMarker(new MarkerOptions()
                    .position(myLocation)
                    .title("You are here")
                    .icon(smallMarkerIcon));
        } else {
            Toast.makeText(this, "Please toggle your location.", Toast.LENGTH_SHORT).show();
        }
    }

    private void GetCommutes() {
        reference.child("Commutes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Bitmap b = GlobalClass.UserAccount.getAccountType() == AccountType.Commuter ? BitmapFactory.decodeResource(getResources(), R.drawable.marker1) : BitmapFactory.decodeResource(getResources(), R.drawable.mylocation);
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, 60, 60, false);
                googleMaps.clear();
                SetLocation(false);
                BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    CommuteModel model = snapshot1.getValue(CommuteModel.class);
                    if (!model.isOccupied()) {
                        LatLng passengerLatlng = new LatLng(model.getPassengerLatitude(), model.getPassengerLongitude());
                        Marker marker = googleMaps.addMarker(new MarkerOptions()
                                .position(passengerLatlng)
                                .icon(smallMarkerIcon));
                        googleMaps.setInfoWindowAdapter(new GoogleMarkerAdapter(DriversHome.this));
                        marker.setTag(model);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ShowCommuteInfo(CommuteModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DriversHome.this);
        View customLayout = getLayoutInflater().inflate(R.layout.layout_commute_dialog, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0);

        WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
        lWindowParams.copyFrom(dialog.getWindow().getAttributes());
        lWindowParams.width = 700;
        lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        TextView fullName = customLayout.findViewById(R.id.fullNames);
        CircleImageView pp = customLayout.findViewById(R.id.profilePicture);
        TextView time = customLayout.findViewById(R.id.durations);
        TextView fare = customLayout.findViewById(R.id.fare);
        TextView distance = customLayout.findViewById(R.id.distance);
        TextView address1 = customLayout.findViewById(R.id.address1);
        TextView address2 = customLayout.findViewById(R.id.address2);
        Button accept = customLayout.findViewById(R.id.accept);
        Button close = customLayout.findViewById(R.id.decline);

        fare.setText("₱ " + model.getFare());
        distance.setText(model.getDistance());
        address1.setText(model.getAddress1());
        address2.setText(model.getAddress2());
        time.setText(String.valueOf(model.getTime()));

        accept.setOnClickListener(view -> {
            int count = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                count = CommuteModels.stream().filter(e ->
                        e.commuteStatus == CommuteStatus.InProgress && e.isOccupied()
                ).collect(Collectors.toList()).size();
                if (count > 0) {
                    Toast.makeText(DriversHome.this, "You are currently occupied!", Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("driverUid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("isOccupied", true);
                    hashMap.put("occupied", true);
                    reference.child("Commutes").child(model.getKey()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                List<CommuteModel>  list = CommuteModels.stream().filter(e ->
                                        e.commuteStatus == CommuteStatus.InProgress && e.isOccupied()
                                ).collect(Collectors.toList());
                                if (list.size() > 0) {
                                    CommuteModel currentRequest = list.get(0);
                                    CurrentRequest current = new CurrentRequest(currentRequest);
                                    current.show(getSupportFragmentManager(), "DriversHome");
                                }
                                Toast.makeText(DriversHome.this, "Passenger will be notify in there request.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DriversHome.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        close.setOnClickListener(view -> dialog.dismiss());

        // Inflate SupportMapFragment programmatically and add it to the FrameLayout
        MapView mapView = customLayout.findViewById(R.id.googleMap1);
        mapView.onCreate(null);
        mapView.onResume();
        final GoogleMap[] googleMapDialog = new GoogleMap[1];
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMapDialog[0] = googleMap;
                googleMapDialog[0].setInfoWindowAdapter(
                        new GoogleMarkerAdapter(DriversHome.this)
                );
                Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.mylocation);
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, 60, 60, false);
                BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

                Bitmap b1 = BitmapFactory.decodeResource(getResources(), R.drawable.destination);
                Bitmap smallMarker1 = Bitmap.createScaledBitmap(b1, 60, 60, false);
                BitmapDescriptor smallMarkerIcon1 = BitmapDescriptorFactory.fromBitmap(smallMarker1);

                LatLng passLocation = new LatLng(model.getPassengerLatitude(), model.getPassengerLongitude());
                LatLng passDestination = new LatLng(model.getPassengerDestinationLatitude(), model.getPassengerDestinationLongitude());

                googleMapDialog[0].moveCamera(CameraUpdateFactory.newLatLngZoom(passLocation, 11));
                googleMapDialog[0].addMarker(new MarkerOptions()
                        .position(passLocation)
                        .title(model.getAddress1())
                        .icon(smallMarkerIcon));
                googleMapDialog[0].addMarker(new MarkerOptions()
                        .position(passDestination)
                        .title(model.getAddress2())
                        .icon(smallMarkerIcon1));

                PolylineOptions polylineOptions = new PolylineOptions();
                for (List<Double> coordinates : model.getPassengerRoute().getPassengerRoute()) {
                    double latitude = coordinates.get(1);
                    double longitude = coordinates.get(0);
                    LatLng latLng = new LatLng(latitude, longitude);
                    polylineOptions.add(latLng);
                }

                //Set disantance and time
                Polyline polyline = googleMapDialog[0].addPolyline(polylineOptions);

            }
        });


        reference.child("Accounts").child(model.getPassengerUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserAccountModel user = snapshot.getValue(UserAccountModel.class);
                fullName.setText(user.getFullName());
                if (!TextUtils.isEmpty(user.getProfilePicture())) {
                    Picasso.get().load(user.getProfilePicture()).into(pp);
                } else {
                    UniqueRandomGenerator uniqueRandomGenerator = new UniqueRandomGenerator();
                    int number = uniqueRandomGenerator.generateUniqueRandom();
                    if (number == 1) {
                        Picasso.get().load(R.drawable.person1).into(pp);
                    } else if (number == 2) {
                        Picasso.get().load(R.drawable.person2).into(pp);
                    } else if (number == 3) {
                        Picasso.get().load(R.drawable.person3).into(pp);
                    } else {
                        Picasso.get().load(R.drawable.person4).into(pp);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


        dialog.show();
        dialog.getWindow().setAttributes(lWindowParams);
    }

    private void GetMyCommutes() {
        reference.child("Commutes").orderByChild("driverUid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CommuteModels = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    CommuteModel model = snapshot1.getValue(CommuteModel.class);
                    reference.child("Accounts").child(model.getPassengerUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserAccountModel passenger = snapshot.getValue(UserAccountModel.class);
                            model.PassengerAccount = passenger;
                            model.DriverAccount = GlobalClass.UserAccount;
                            CommuteModels.add(model);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
