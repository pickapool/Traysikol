package com.example.traysikol.Drivers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.traysikol.Adapter.AdapterDriversHistory;
import com.example.traysikol.Enums.CommuteStatus;
import com.example.traysikol.Models.CommuteModel;
import com.example.traysikol.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DriversHistory extends AppCompatActivity {
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    AdapterDriversHistory adapterDriversHistory;
    DatabaseReference reference;
    List<CommuteModel> commuteModelList;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers_history);

        recyclerView = findViewById(R.id.recycleView);
        reference = FirebaseDatabase.getInstance().getReference();
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii = new Intent(DriversHistory.this, DriversHome.class);
                startActivity(ii);
                finish();
            }
        });

        reference.child("Commutes").orderByChild("driverUid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commuteModelList = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    CommuteModel model = snapshot1.getValue(CommuteModel.class);
                    if(model.getCommuteStatus() == CommuteStatus.Done) {
                        commuteModelList.add(model);
                    }
                }
                linearLayoutManager = new LinearLayoutManager(DriversHistory.this);
                adapterDriversHistory = new AdapterDriversHistory(commuteModelList, DriversHistory.this);
                recyclerView.setAdapter(adapterDriversHistory);
                recyclerView.setLayoutManager(linearLayoutManager);
                adapterDriversHistory.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}