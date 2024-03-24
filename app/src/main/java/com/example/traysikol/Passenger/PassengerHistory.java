package com.example.traysikol.Passenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.traysikol.Adapter.AdapterHistory;
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

public class PassengerHistory extends AppCompatActivity {
    List<CommuteModel> commuteModelList;
    DatabaseReference reference;
    LinearLayoutManager linearLayoutManager;
    LinearLayoutManager linearLayoutManager1;
    AdapterHistory adapterHistory;
    AdapterHistory adapterHistory1;
    RecyclerView todayContainer;
    RecyclerView previousContainer;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_history);

        reference = FirebaseDatabase.getInstance().getReference();
        todayContainer = findViewById(R.id.aboveContainer);
        previousContainer = findViewById(R.id.bottomContainer);
        linearLayoutManager = new LinearLayoutManager(PassengerHistory.this);
        linearLayoutManager1 = new LinearLayoutManager(PassengerHistory.this);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii = new Intent(PassengerHistory.this, PassengerHomeScreen.class);
                startActivity(ii);
                finish();
            }
        });

        reference.child("Commutes").orderByChild("passengerUid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        commuteModelList = new ArrayList<>();
                        for (DataSnapshot snapshot1 : snapshot.getChildren())
                        {
                            CommuteModel commuteModel = snapshot1.getValue(CommuteModel.class);
                            if(commuteModel.getCommuteStatus() == CommuteStatus.Done)
                            {
                                commuteModelList.add(commuteModel);
                            }
                        }

                        adapterHistory = new AdapterHistory(commuteModelList, PassengerHistory.this);
                        todayContainer.setLayoutManager(linearLayoutManager);
                        todayContainer.setAdapter(adapterHistory);
                        adapterHistory.notifyDataSetChanged();

                        adapterHistory1 = new AdapterHistory(commuteModelList, PassengerHistory.this);
                        previousContainer.setLayoutManager(linearLayoutManager1);
                        previousContainer.setAdapter(adapterHistory1);
                        adapterHistory1.notifyDataSetChanged();

                        //set adapter here;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}