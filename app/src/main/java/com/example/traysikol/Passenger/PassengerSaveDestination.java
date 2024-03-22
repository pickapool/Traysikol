package com.example.traysikol.Passenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.traysikol.Adapter.SaveDestinationsAdapter.SaveDestinationAdapter;
import com.example.traysikol.Models.SaveDestinationModel;
import com.example.traysikol.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PassengerSaveDestination extends AppCompatActivity {
    DatabaseReference reference;
    List<SaveDestinationModel> destinationModelList;
    RecyclerView recyclerView;
    SaveDestinationAdapter adapterDrivers;
    LinearLayoutManager linearLayoutManager;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_save_destination);

        reference = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recycleView);
        back = findViewById(R.id.back);
        linearLayoutManager = new LinearLayoutManager(PassengerSaveDestination.this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii = new Intent(PassengerSaveDestination.this, PassengerProfile.class);
                startActivity(ii);
                finish();
            }
        });
        reference.child("SaveDestinations")
                .orderByChild("passengerUid")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        destinationModelList = new ArrayList<>();
                        for(DataSnapshot snapshot1 : snapshot.getChildren())
                        {
                            SaveDestinationModel model = snapshot1.getValue(SaveDestinationModel.class);
                            destinationModelList.add(model);
                        }
                        adapterDrivers = new SaveDestinationAdapter(destinationModelList, PassengerSaveDestination.this);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(adapterDrivers);
                        adapterDrivers.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}