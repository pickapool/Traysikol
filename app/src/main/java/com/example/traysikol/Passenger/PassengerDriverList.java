package com.example.traysikol.Passenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.traysikol.Adapter.AdapterDrivers;
import com.example.traysikol.Models.OnlineDriverModel;
import com.example.traysikol.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PassengerDriverList extends AppCompatActivity {
    RecyclerView topDriver;
    RecyclerView bottomDriver;
    AdapterDrivers adapter;
    LinearLayoutManager linearLayoutManager;

    DatabaseReference reference;
    List<OnlineDriverModel> onlineDriverModelList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_driver_list);

        linearLayoutManager = new LinearLayoutManager(PassengerDriverList.this);
        topDriver = findViewById(R.id.driversNearby);
        bottomDriver = findViewById(R.id.listDriverContainer);

        reference = FirebaseDatabase.getInstance().getReference();

        reference.child("OnlineDrivers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                onlineDriverModelList = new ArrayList<>();
                for(DataSnapshot snapshot1: snapshot.getChildren())
                {
                    OnlineDriverModel model = snapshot1.getValue(OnlineDriverModel.class);
                    onlineDriverModelList.add(model);
                }
                for (OnlineDriverModel online: onlineDriverModelList) {

                }
                adapter = new AdapterDrivers(onlineDriverModelList,PassengerDriverList.this);
                bottomDriver.setLayoutManager(linearLayoutManager);
                bottomDriver.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}