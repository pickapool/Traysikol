package com.example.traysikol.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class CountDownService extends Service {
    private CountDownTimer countDownTimer;

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
                // Stop the service when the countdown is finished
                SharedPreferences sharedPreferences = getSharedPreferences("Countdown", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("IsPlaying", "false");
                editor.apply();
                //send to firebase

                stopSelf();
            }
        }.start();
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