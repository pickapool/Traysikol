package com.example.traysikol.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.traysikol.Enums.CommuteStatus;
import com.example.traysikol.Extensions;
import com.example.traysikol.Models.CommuteModel;
import com.example.traysikol.Passenger.PassengerHomeScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.Calendar;

public class RequestService extends Service {
    DatabaseReference reference;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Retrieve countdown timer data from SharedPreferences
        return START_STICKY;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}