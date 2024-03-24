package com.example.traysikol.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.traysikol.Drivers.DriversHome;
import com.example.traysikol.Extensions;
import com.example.traysikol.Models.CommuteModel;
import com.example.traysikol.Passenger.PassengerHomeScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverAcceptService extends Service {

    DatabaseReference reference;
    boolean IsLoadedCommutes = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Commutes").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                IsLoadedCommutes = true;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                CommuteModel commuteModel = snapshot.getValue(CommuteModel.class);
                //Toast.makeText(DriverAcceptService.this, String.valueOf(commuteModel.getPassengerUid()), Toast.LENGTH_SHORT).show();
                if(commuteModel.getPassengerUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                {
                    if(commuteModel.isOccupied())
                    {
                        Extensions.CreateNotification(getApplicationContext(), PassengerHomeScreen.class, "Driver is on its way.");
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
