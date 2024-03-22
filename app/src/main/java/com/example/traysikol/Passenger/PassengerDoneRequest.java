package com.example.traysikol.Passenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.traysikol.Enums.CommuteStatus;
import com.example.traysikol.GlobalClass;
import com.example.traysikol.Models.CommuteModel;
import com.example.traysikol.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class PassengerDoneRequest extends AppCompatActivity {
    Button retn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_done_request);

        retn = findViewById(R.id.goBack);
        retn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii = new Intent(PassengerDoneRequest.this , PassengerHomeScreen.class);
                startActivity(ii);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent ii = new Intent(PassengerDoneRequest.this , PassengerHomeScreen.class);
        startActivity(ii);
        finish();
    }
}