package com.example.traysikol.Passenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.traysikol.R;

public class PassengerProfile extends AppCompatActivity {
    ImageView back;
    RelativeLayout privacy, profDetail, saveDestination;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_profile);
        back = findViewById(R.id.back);
        profDetail = findViewById(R.id.profile_details_btn);

        back.setOnClickListener(view -> {
            Intent ii = new Intent(PassengerProfile.this, PassengerHomeScreen.class);
            startActivity(ii);
            finish();
        });
        profDetail.setOnClickListener(view -> {
            Intent ii = new Intent(PassengerProfile.this, PassengerProfileDetails.class);
            startActivity(ii);
            finish();
        });
    }
}