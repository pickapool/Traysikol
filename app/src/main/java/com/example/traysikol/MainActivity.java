package com.example.traysikol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.traysikol.Enums.AccountType;
import com.example.traysikol.Models.UserAccountModel;
import com.example.traysikol.Passenger.PassengerHomeScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Dialog dialog;
    private int PERMISSION_CODE = 1;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    private void CheckPermissionLocation() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            GoToChoose();
        } else {
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) &&
                ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) &&
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS) &&
                ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("Please allow permission to use this app")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                    android.Manifest.permission.CAMERA, Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_CODE);
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            System.exit(0);
                        }
                    }).create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.CAMERA,
                    Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_CODE);
        }
    }

    private void GoToChoose() {
        reference = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            //Get account and set data to globalClass;
            reference.child("Accounts").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserAccountModel userAccount = snapshot.getValue(UserAccountModel.class);
                        GlobalClass.UserAccount = userAccount;
                        if (userAccount.getAccountType() == AccountType.Commuter) {
                            Intent ii = new Intent(MainActivity.this, PassengerHomeScreen.class);
                            startActivity(ii);
                        }
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Intent ii = new Intent(MainActivity.this, ChooseAccount.class);
                    startActivity(ii);
                    finish();
                }
            }, 500);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //GO to Login
                GoToChoose();

            } else {
                CheckPermissionLocation();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckPermissionLocation();
    }
}