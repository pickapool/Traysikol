package com.example.traysikol.Passenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.traysikol.Adapter.AdapterDrivers;
import com.example.traysikol.Adapter.AdapterDriversNearBy;
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
    ImageView back;
    RecyclerView topDriver;
    RecyclerView bottomDriver;
    AdapterDrivers adapter;
    AdapterDriversNearBy adapterNear;
    LinearLayoutManager linearLayoutManager;
    LinearLayoutManager linearLayoutManager1;
    DatabaseReference reference;
    List<OnlineDriverModel> onlineDriverModelList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_driver_list);

        linearLayoutManager = new LinearLayoutManager(PassengerDriverList.this);
        linearLayoutManager1 = new LinearLayoutManager(PassengerDriverList.this, LinearLayoutManager.HORIZONTAL , false);
        topDriver = findViewById(R.id.driversNearby);
        bottomDriver = findViewById(R.id.listDriverContainer);
        back = findViewById(R.id.back);

        reference = FirebaseDatabase.getInstance().getReference();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii = new Intent(PassengerDriverList.this , PassengerHomeScreen.class);
                startActivity(ii);
                finish();
            }
        });
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

                adapterNear = new AdapterDriversNearBy(onlineDriverModelList,PassengerDriverList.this);
                topDriver.setLayoutManager(linearLayoutManager1);
                topDriver.setAdapter(adapterNear);
                adapterNear.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}