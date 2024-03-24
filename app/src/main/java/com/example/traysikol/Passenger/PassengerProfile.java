package com.example.traysikol.Passenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.traysikol.GlobalClass;
import com.example.traysikol.R;
import com.squareup.picasso.Picasso;

public class PassengerProfile extends AppCompatActivity {
    ImageView back, pp;
    RelativeLayout privacy, profDetail, saveDestination;
    TextView fullName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_profile);
        back = findViewById(R.id.back);
        profDetail = findViewById(R.id.profile_details_btn);
        saveDestination = findViewById(R.id.save_destination_btn);
        fullName = findViewById(R.id.fullName);
        pp = findViewById(R.id.profilePicture);

        fullName.setText(GlobalClass.UserAccount.getFullName());
        if(!TextUtils.isEmpty(GlobalClass.UserAccount.getProfilePicture()))
            Picasso.get().load(GlobalClass.UserAccount.getProfilePicture()).into(pp);
        saveDestination.setOnClickListener(view -> {
            Intent ii = new Intent(PassengerProfile.this, PassengerSaveDestination.class);
            startActivity(ii);
            finish();
        });
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