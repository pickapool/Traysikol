package com.example.traysikol.Passenger;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.traysikol.R;
import com.example.traysikol.Services.CountDownService;

import java.util.Calendar;

public class PassengerCountDown extends DialogFragment {

    TextView hourTxt, minuteTxt, secondTxt;
    ImageView play, close;
    final Calendar calendar = Calendar.getInstance();
    boolean IsPlaying = false;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.FullScreenDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_countdown, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        hourTxt = view.findViewById(R.id.hour);
        minuteTxt = view.findViewById(R.id.minutes);
        secondTxt = view.findViewById(R.id.seconds);
        play = view.findViewById(R.id.play);
        close = view.findViewById(R.id.closeDialog);

        hourTxt.setOnClickListener(view1 -> SelectTime());
        minuteTxt.setOnClickListener(view1 -> SelectTime());
        secondTxt.setOnClickListener(view1 -> SelectTime());

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Countdown", Context.MODE_PRIVATE);
        String data = sharedPreferences.getString("IsPlaying", "true");
        Intent serviceIntent = new Intent(getContext(), CountDownService.class);
        getContext().stopService(serviceIntent);
        if(data.toLowerCase().contains("true")) {
            IsPlaying = true;
            String hour = sharedPreferences.getString("hour", "0");
            String minute = sharedPreferences.getString("minute", "0");
            String second = sharedPreferences.getString("second", "0");
            startCountdownTimer(1000, hour, minute, second);
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!IsPlaying) {
                        startCountdownTimer(1000, hourTxt.getText().toString(),
                                minuteTxt.getText().toString(),
                                secondTxt.getText().toString());
                    }
                }
            });
        } else {

        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void SelectTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), TimePickerDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Update the TextView with the selected hour
                hourTxt.setText(String.format("%02d", hourOfDay));
                minuteTxt.setText(String.format("%02d", minute));
            }
        }, 0, 0, true) {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                super.onTimeChanged(view, hourOfDay, 0); // Set minute to 0 to prevent minute changes
            }
        };

        timePickerDialog.setTitle("Select time");
        timePickerDialog.show();
    }

    private void startCountdownTimer(long milliseconds, String hr, String min, String sec) {
        int hours = Integer.parseInt(hr);
        int minutes = Integer.parseInt(min);
        int seconds = Integer.parseInt(sec);

        long totalDurationInMillis = (hours * 3600 + minutes * 60 + seconds) * 1000;

        new CountDownTimer(totalDurationInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                // Calculate remaining time in hours, minutes, and seconds
                int remainingHours = (int) (millisUntilFinished / 3600000);
                int remainingMinutes = (int) ((millisUntilFinished % 3600000) / 60000);
                int remainingSeconds = (int) ((millisUntilFinished % 60000) / 1000);

                // Update TextViews with remaining time
                hourTxt.setText(String.format("%02d", remainingHours));
                minuteTxt.setText(String.format("%02d", remainingMinutes));
                secondTxt.setText(String.format("%02d", remainingSeconds));
            }

            public void onFinish() {
                // Do something when the countdown is finished
                hourTxt.setText("00");
                minuteTxt.setText("00");
                secondTxt.setText("00");
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("Countdown", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("IsPlaying", "false");
                editor.apply();
            }
        }.start();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Countdown", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("IsPlaying", "true");
        editor.apply();
    }
    private void saveToPreferences(String hr, String min, String sec) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Countdown", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("hour", hr);
        editor.putString("minute", min);
        editor.putString("second", sec);
        editor.putString("IsPlaying", "true");
        editor.apply();
    }

    @Override
    public void onPause() {
        saveToPreferences(hourTxt.getText().toString(),
                minuteTxt.getText().toString(), secondTxt.getText().toString());
        Intent serviceIntent = new Intent(getContext(), CountDownService.class);
        getContext().startService(serviceIntent);
        super.onPause();
    }
}