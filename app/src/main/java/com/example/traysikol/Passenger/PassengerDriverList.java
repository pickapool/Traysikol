package com.example.traysikol.Passenger;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.traysikol.Adapter.AdapterDrivers;
import com.example.traysikol.Adapter.AdapterDriversNearBy;
import com.example.traysikol.GlobalClass;
import com.example.traysikol.Models.CommuteModel;
import com.example.traysikol.Models.OSRDirectionModels.ORSResponse;
import com.example.traysikol.Models.OnlineDriverModel;
import com.example.traysikol.Models.UserAccountModel;
import com.example.traysikol.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

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
    List<OnlineDriverModel> nearByList;
    List<OnlineDriverModel> longDistance;
    EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_driver_list);

        linearLayoutManager = new LinearLayoutManager(PassengerDriverList.this, LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManager1 = new LinearLayoutManager(PassengerDriverList.this);
        topDriver = findViewById(R.id.driversNearby);
        bottomDriver = findViewById(R.id.listDriverContainer);
        back = findViewById(R.id.back);
        search = findViewById(R.id.searchDriver);

        reference = FirebaseDatabase.getInstance().getReference();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii = new Intent(PassengerDriverList.this, PassengerHomeScreen.class);
                startActivity(ii);
                finish();
            }
        });
        reference.child("OnlineDrivers").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                onlineDriverModelList = new ArrayList<>();
                nearByList = new ArrayList<>();
                longDistance = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    OnlineDriverModel model = snapshot1.getValue(OnlineDriverModel.class);
                    String startLatitude = String.valueOf(GlobalClass.currentLocation.get(0).getLatitude());
                    String startLongitude = String.valueOf(GlobalClass.currentLocation.get(0).getLongitude());
                    String endLatitude = String.valueOf(model.getLatitude());
                    String endLongitude = String.valueOf(model.getLongitude());

                    String start = String.format("%s,%s", startLongitude, startLatitude);
                    String end = String.format("%s,%s", endLongitude, endLatitude);

                    RequestQueue queue = Volley.newRequestQueue(PassengerDriverList.this);
                    StringRequest stringRequest = new StringRequest(Request.Method.GET,
                            "https://api.openrouteservice.org/v2/directions/driving-car?api_key=5b3ce3597851110001cf6248865cba12d4c44f36b368b2b065a07c00&start="
                                    + start + "&end="
                                    + end, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Gson gson = new Gson();
                                ORSResponse orsResponse = gson.fromJson(response, ORSResponse.class);
                                double distance = GlobalClass.GetDistance(orsResponse);
                                model.setDistance(distance);
                                reference.child("Accounts").child(model.getDriverUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        UserAccountModel model1 = snapshot.getValue(UserAccountModel.class);
                                        model.setUserAccountModel(model1);
                                        onlineDriverModelList.add(model);

                                        nearByList = onlineDriverModelList.stream().filter(e -> e.getDistance() <= 1000).collect(Collectors.toList());
                                        adapterNear = new AdapterDriversNearBy(nearByList, PassengerDriverList.this);
                                        topDriver.setLayoutManager(linearLayoutManager);
                                        topDriver.setAdapter(adapterNear);
                                        adapterNear.notifyDataSetChanged();

                                        longDistance = onlineDriverModelList.stream().filter(e -> e.getDistance() > 1000).collect(Collectors.toList());
                                        adapter = new AdapterDrivers(longDistance, PassengerDriverList.this);
                                        bottomDriver.setLayoutManager(linearLayoutManager1);
                                        bottomDriver.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            } catch (Exception ee) {
                                System.out.println("error2312cc - " + ee.getMessage());
                                Toast.makeText(PassengerDriverList.this, ee.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("error2333 - " + error);
                            model.setDistance(9999999);
                            Toast.makeText(PassengerDriverList.this, "Some of other locations is exceeded the API distance limit.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    queue.add(stringRequest);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void afterTextChanged(Editable editable) {
                String chars = editable.toString();
                if (chars.length() == 0) {
                    adapter = new AdapterDrivers(longDistance, PassengerDriverList.this);
                } else {
                    List<OnlineDriverModel> current = null;
                    if (isNumeric(chars)) {
                        current = longDistance.stream().filter(e -> e.getDistance() <= Double.parseDouble(chars)
                        ).collect(Collectors.toList());
                    } else {
                        current = longDistance.stream().filter(e ->
                                e.getUserAccountModel().getFullName().toLowerCase().contains(chars.toLowerCase()) ||
                                        e.getUserAccountModel().getFullName().toLowerCase().contains(chars.toLowerCase()) ||
                                        e.getUserAccountModel().getAddress().toLowerCase().contains(chars.toLowerCase()) ||
                                        e.getUserAccountModel().getFirstname().toLowerCase().contains(chars.toLowerCase()) ||
                                        e.getUserAccountModel().getLastname().toLowerCase().contains(chars.toLowerCase()) ||
                                        e.getOnlineStatus().toString().toLowerCase().contains(chars.toLowerCase())
                        ).collect(Collectors.toList());
                    }
                    adapter = new AdapterDrivers(current, PassengerDriverList.this);
                }
                bottomDriver.setLayoutManager(linearLayoutManager1);
                bottomDriver.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}