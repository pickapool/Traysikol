package com.example.traysikol.Passenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.traysikol.GlobalClass;
import com.example.traysikol.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class PassengerProfile extends AppCompatActivity {
    ImageView back, pp;
    RelativeLayout privacy, profDetail, saveDestination;
    TextView fullName, verify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_profile);
        back = findViewById(R.id.back);
        profDetail = findViewById(R.id.profile_details_btn);
        saveDestination = findViewById(R.id.save_destination_btn);
        fullName = findViewById(R.id.fullName);
        pp = findViewById(R.id.profilePicture);
        verify = findViewById(R.id.verify);

        verify.setOnClickListener(view -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(PassengerProfile.this, "We have sent an email verification, please check your inbox.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PassengerProfile.this, "Sorry, there was a problem while sending the verification.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

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