package com.example.traysikol.Passenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.PathSegment;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.traysikol.GlobalClass;
import com.example.traysikol.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PassengerHomeScreen extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    Toolbar toolbar;
    ImageView routeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_home_screen);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        toolbar = findViewById(R.id.toolbar);
        routeBtn = findViewById(R.id.rightIcon);

        routeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog progressBar = new ProgressDialog(PassengerHomeScreen.this);
                progressBar.setTitle("Getting Location");
                progressBar.setMessage("Please wait...");
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
                                    Intent ii = new Intent(PassengerHomeScreen.this, PassengerCommute.class);
                                    startActivity(ii);
                                } catch (IOException e) {
                                    progressBar.dismiss();
                                    Toast.makeText(PassengerHomeScreen.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                progressBar.dismiss();
                                Toast.makeText(PassengerHomeScreen.this, "Please toggle your Location", Toast.LENGTH_LONG).show();
                            }
                        } else{
                            progressBar.dismiss();
                            System.out.println("error"+ task.getException().getMessage());
                            Toast.makeText(PassengerHomeScreen.this, "Sorry, there was a problem getting you location.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    progressBar.dismiss();
                    Toast.makeText(PassengerHomeScreen.this, "No permission granted.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }
}