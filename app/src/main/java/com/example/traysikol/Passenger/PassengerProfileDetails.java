package com.example.traysikol.Passenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.traysikol.R;

public class PassengerProfileDetails extends AppCompatActivity {
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_profile_details);

        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii = new Intent(PassengerProfileDetails.this, PassengerProfile.class);
                startActivity(ii);
                finish();
            }
        });
    }
}