package com.example.traysikol.Drivers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traysikol.Adapter.AdapterCurrentRequests;
import com.example.traysikol.Adapter.AdapterHistory;
import com.example.traysikol.Enums.CommuteStatus;
import com.example.traysikol.Models.CommuteModel;
import com.example.traysikol.Passenger.PassengerHomeScreen;
import com.example.traysikol.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DriverCurrentRequests extends AppCompatActivity {
    List<CommuteModel> commuteModelList;
    DatabaseReference reference;
    LinearLayoutManager linearLayoutManager;
    AdapterCurrentRequests adapterHistory;
    RecyclerView todayContainer;
    ImageView back;
    TextView noRecord2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_current_requests);

        reference = FirebaseDatabase.getInstance().getReference();
        todayContainer = findViewById(R.id.container);
        linearLayoutManager = new LinearLayoutManager(DriverCurrentRequests.this);
        back = findViewById(R.id.back);
        noRecord2 = findViewById(R.id.noRecords2);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii = new Intent(DriverCurrentRequests.this, DriversHome.class);
                startActivity(ii);
                finish();
            }
        });

        reference.child("Commutes").orderByChild("driverUid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        commuteModelList = new ArrayList<>();
                        for (DataSnapshot snapshot1 : snapshot.getChildren())
                        {
                            CommuteModel commuteModel = snapshot1.getValue(CommuteModel.class);
                            commuteModelList.add(commuteModel);
                        }
                        List<CommuteModel> currents = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            currents = commuteModelList.stream()
                                    .filter(e -> e.commuteStatus == CommuteStatus.InProgress && e.isOccupied())
                                    .sorted((e1, e2) -> e2.getCommuteDate().compareTo(e1.getCommuteDate()))
                                    .collect(Collectors.toList());
                        }
                        if(currents.size() > 0) {
                            adapterHistory = new AdapterCurrentRequests(currents, DriverCurrentRequests.this, getSupportFragmentManager());
                            todayContainer.setLayoutManager(linearLayoutManager);
                            todayContainer.setAdapter(adapterHistory);
                            adapterHistory.notifyDataSetChanged();
                        } else {
                            todayContainer.setVisibility(View.INVISIBLE);
                            noRecord2.setVisibility(View.GONE);
                        }

                        //set adapter here;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}