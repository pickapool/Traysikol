package com.example.traysikol.Passenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.traysikol.Drivers.DriversHome;
import com.example.traysikol.Enums.AccountType;
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
    RelativeLayout privacy_btn;
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
        privacy_btn = findViewById(R.id.privacy_btn);

        privacy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog("Privacy Policy\n" +
                        "Your trust is important to us, and we are committed to safeguarding your personal information. Our Privacy Policy outlines how we collect, use, and protect your data:  \n" +
                        "\n" +
                        " 1. Information We Collect\n" +
                        "- Personal Information: Name, contact details, and address for account creation.  \n" +
                        "- Ride Details: Pickup/drop-off locations and trip history for service improvement.  \n" +
                        "\n" +
                        "2. How We Use Your Information\n" +
                        "- To provide and improve our booking services.  \n" +
                        "- To communicate important updates or promotional offers.  \n" +
                        "- To ensure safety and accountability during trips.  \n" +
                        "\n" +
                        "3. Data Security \n" +
                        "We implement industry-standard measures to protect your information from unauthorized access, alteration, or disclosure.  \n" +
                        "\n" +
                        "4. Third-Party Sharing\n" +
                        "We do not share your personal data with third parties.\n" +
                        "\n" +
                        "\n" +
                        "By using TricyRide, you agree to the terms outlined in this Privacy Policy. For detailed terms.");
            }
        });

        if(GlobalClass.UserAccount.getAccountType() == AccountType.Driver)
            saveDestination.setVisibility(View.GONE);

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
            Intent ii;
            if(GlobalClass.UserAccount.getAccountType() == AccountType.Driver)
            {
                ii = new Intent(PassengerProfile.this, DriversHome.class);
            } else {
                ii = new Intent(PassengerProfile.this, PassengerHomeScreen.class);
            }
            startActivity(ii);

            finish();
        });
        profDetail.setOnClickListener(view -> {
            Intent ii = new Intent(PassengerProfile.this, PassengerProfileDetails.class);
            startActivity(ii);
            finish();
        });
    }
    private void showDialog(String message) {
        // Create an AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the dialog title
        builder.setTitle("Privacy");

        // Set the dialog message (this is the text displayed in the dialog)
        builder.setMessage(message);

        // Set a positive button (OK button) and its click listener
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the click event for the OK button
                dialog.dismiss();
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        // Show the dialog
        dialog.show();
    }
}