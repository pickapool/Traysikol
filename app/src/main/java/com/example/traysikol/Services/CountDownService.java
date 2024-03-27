package com.example.traysikol.Services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.traysikol.Enums.CommuteStatus;
import com.example.traysikol.Extensions;
import com.example.traysikol.GlobalClass;
import com.example.traysikol.Models.CommuteModel;
import com.example.traysikol.Passenger.PassengerDoneRequest;
import com.example.traysikol.Passenger.PassengerHomeScreen;
import com.example.traysikol.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.Calendar;

public class CountDownService extends Service {
    private CountDownTimer countDownTimer;
    DatabaseReference reference;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Retrieve countdown timer data from SharedPreferences

        SharedPreferences sharedPreferences = getSharedPreferences("Countdown", Context.MODE_PRIVATE);
        String hour = sharedPreferences.getString("hour", "0");
        String minute = sharedPreferences.getString("minute", "0");
        String second = sharedPreferences.getString("second", "0");
        // Start countdown timer
        startCountdownTimer(1000, hour, minute, second);
        return START_STICKY;
    }

    private void startCountdownTimer(long milliseconds, String hr, String min, String sec) {
        int hours = Integer.parseInt(hr);
        int minutes = Integer.parseInt(min);
        int seconds = Integer.parseInt(sec);

        long totalDurationInMillis = (hours * 3600 + minutes * 60 + seconds) * 1000;
        countDownTimer = new CountDownTimer(totalDurationInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                // Update SharedPreferences with remaining time
                updateSharedPreferences(millisUntilFinished);
            }

            public void onFinish() {
                SharedPreferences sharedPreferences = getSharedPreferences("Countdown", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("IsPlaying", "false");
                editor.apply();
                //send to firebase

                Gson gson = new Gson();
                String json = sharedPreferences.getString("CommuteModel", "");
                CommuteModel obj = gson.fromJson(json, CommuteModel.class);
                RideNow(obj);
                //Toast.makeText(CountDownService.this, json, Toast.LENGTH_SHORT).show();
                // Toast.makeText(CountDownService.this, "finish", Toast.LENGTH_SHORT).show();
                stopSelf();
            }
        }.start();
    }

    private void RideNow(CommuteModel model) {
        try {
            reference = FirebaseDatabase.getInstance().getReference();
            String key = reference.push().getKey();

            model.setDriverUid("");
            model.setCommuteStatus(CommuteStatus.InProgress);
            model.setOccupied(false);
            model.setKey(key);
            model.setPassengerUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
            model.setCommuteDate(Calendar.getInstance().getTime());
            model.setRating(0);
            reference.child("Commutes").child(key).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        //notify
                        //
                        Extensions.CreateNotification(getApplicationContext(), PassengerHomeScreen.class, "Your request has been sent to drivers!");
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CountDownService.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception ee) {
            System.out.println("123c" + ee.getMessage());
        }
    }
    private void updateSharedPreferences(long millisUntilFinished) {
        // Calculate remaining time in hours, minutes, and seconds
        int remainingHours = (int) (millisUntilFinished / 3600000);
        int remainingMinutes = (int) ((millisUntilFinished % 3600000) / 60000);
        int remainingSeconds = (int) ((millisUntilFinished % 60000) / 1000);

        // Save remaining time to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("Countdown", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("hour", String.format("%02d", remainingHours));
        editor.putString("minute", String.format("%02d", remainingMinutes));
        editor.putString("second", String.format("%02d", remainingSeconds));
        editor.apply();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}