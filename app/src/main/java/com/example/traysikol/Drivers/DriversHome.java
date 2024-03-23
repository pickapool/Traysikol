package com.example.traysikol.Drivers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.example.traysikol.Enums.AccountType;
import com.example.traysikol.Enums.OnlineStatus;
import com.example.traysikol.GlobalClass;
import com.example.traysikol.Models.OnlineDriverModel;
import com.example.traysikol.Passenger.PassengerHomeScreen;
import com.example.traysikol.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class DriversHome extends AppCompatActivity {
    DatabaseReference reference;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drviers_home);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        reference = FirebaseDatabase.getInstance().getReference();
        //remove driver then set again
        reference.child("OnlineDrivers")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Location location = task.getResult();
                                    if (location != null) {
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("latitude", location.getLatitude());
                                        hashMap.put("longitude", location.getLongitude());
                                        hashMap.put("onlineStatus", OnlineStatus.Online);
                                        reference.child("OnlineDrivers").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .updateChildren(hashMap);
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

    }

    private void CheckLocationIsOn() {
        ProgressDialog progressBar = new ProgressDialog(DriversHome.this);
        progressBar.setTitle("Getting Location");
        progressBar.setMessage("Please wait...");
        progressBar.setCancelable(false);
        progressBar.show();
        //if (ActivityCompat.checkSelfPermission(DriversHome.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
                        reference.child("OnlineDrivers").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(model);
                        progressBar.dismiss();
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
        if(GlobalClass.UserAccount.getAccountType() == AccountType.Driver) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("latitude", 0);
            hashMap.put("longitude", 0);
            hashMap.put("onlineStatus", OnlineStatus.Offline);
            reference.child("OnlineDrivers").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .updateChildren(hashMap);
        }
        super.onPause();
    }
}
