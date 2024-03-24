package com.example.traysikol.Passenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.traysikol.DialogHelper;
import com.example.traysikol.Enums.CommuteStatus;
import com.example.traysikol.GlobalClass;
import com.example.traysikol.Models.CommuteModel;
import com.example.traysikol.Models.SaveDestinationModel;
import com.example.traysikol.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class PassengerDoneRequest extends AppCompatActivity implements DialogHelper.ConfirmationListener{
    Button retn;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_done_request);

        retn = findViewById(R.id.goBack);
        reference = FirebaseDatabase.getInstance().getReference();

        DialogHelper.showConfirmationDialog(this, this);
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
    public void onConfirmation(boolean confirmed) {
        if (confirmed) {
            // Yes button clicked
            SaveDestinationModel destinationModel = new SaveDestinationModel();
            String key = reference.push().getKey();
            destinationModel.setKey(key);
            destinationModel.setPassengerUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
            destinationModel.setLatitude(GlobalClass.currentLocation.get(0).getLatitude());
            destinationModel.setLongitude(GlobalClass.currentLocation.get(0).getLongitude());
            destinationModel.setAddress(GlobalClass.currentLocation.get(0).getAddressLine(0));
            reference.child("SaveDestinations").child(key).setValue(destinationModel)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(PassengerDoneRequest.this, "Destination has been saved.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PassengerDoneRequest.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // No button clicked
        }
    }

    @Override
    public void onBackPressed() {
        Intent ii = new Intent(PassengerDoneRequest.this , PassengerHomeScreen.class);
        startActivity(ii);
        finish();
    }
}