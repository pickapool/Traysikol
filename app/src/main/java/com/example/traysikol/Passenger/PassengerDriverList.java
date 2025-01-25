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
import com.example.traysikol.Enums.AccountType;
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
import com.google.gson.JsonObject;

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
                                reference.child("Accounts").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot snapshot2: snapshot.getChildren())
                                        {
                                            Gson gson = new Gson();
                                            String jsonLog = gson.toJson(snapshot2.getValue());
                                            JsonObject jsonObject = gson.fromJson(jsonLog, JsonObject.class);

                                            UserAccountModel model1 = new UserAccountModel();
                                            model1.setFirstname(jsonObject.has("Firstname") ? jsonObject.get("Firstname").getAsString() : "");
                                            if(jsonObject.has("Uid")) {
                                                model1.setUid(jsonObject.has("Uid") ? jsonObject.get("Uid").getAsString() : "");
                                            } else {
                                                model1.setUid(jsonObject.has("uid") ? jsonObject.get("uid").getAsString() : "");
                                            }

                                            // Handle 'Firstname' and 'firstname' case sensitivity
                                            model1.setFirstname(jsonObject.has("Firstname") ? jsonObject.get("Firstname").getAsString() : "");
                                            if (jsonObject.has("firstname") && !jsonObject.has("Firstname")) {
                                                model1.setFirstname(jsonObject.get("firstname").getAsString());
                                            }

                                            // Handle 'Lastname' and 'lastname' case sensitivity
                                            model1.setLastname(jsonObject.has("Lastname") ? jsonObject.get("Lastname").getAsString() : "");
                                            if (jsonObject.has("lastname") && !jsonObject.has("Lastname")) {
                                                model1.setLastname(jsonObject.get("lastname").getAsString());
                                            }

                                            // Handle 'Email' and 'email' case sensitivity
                                            model1.setEmail(jsonObject.has("Email") ? jsonObject.get("Email").getAsString() : "");
                                            if (jsonObject.has("email") && !jsonObject.has("Email")) {
                                                model1.setEmail(jsonObject.get("email").getAsString());
                                            }

                                            // Handle 'PhoneNumber' and 'phonenumber' case sensitivity
                                            model1.setPhoneNumber(jsonObject.has("PhoneNumber") ? jsonObject.get("PhoneNumber").getAsString() : "");
                                            if (jsonObject.has("phoneNumber") && !jsonObject.has("PhoneNumber")) {
                                                model1.setPhoneNumber(jsonObject.get("phoneNumber").getAsString());
                                            }

                                            // Handle 'Username' and 'username' case sensitivity
                                            model1.setUsername(jsonObject.has("Username") ? jsonObject.get("Username").getAsString() : "");
                                            if (jsonObject.has("username") && !jsonObject.has("Username")) {
                                                model1.setUsername(jsonObject.get("username").getAsString());
                                            }

                                            // Handle 'Password' and 'password' case sensitivity
                                            model1.setPassword(jsonObject.has("Password") ? jsonObject.get("Password").getAsString() : "");
                                            if (jsonObject.has("password") && !jsonObject.has("Password")) {
                                                model1.setPassword(jsonObject.get("password").getAsString());
                                            }

                                            // Handle 'ProfilePicture' and 'profilepicture' case sensitivity
                                            model1.setProfilePicture(jsonObject.has("ProfilePicture") ? jsonObject.get("ProfilePicture").getAsString() : "");
                                            if (jsonObject.has("profilepicture") && !jsonObject.has("ProfilePicture")) {
                                                model1.setProfilePicture(jsonObject.get("profilepicture").getAsString());
                                            }

                                            // Handle 'DateOfBirth' and 'dateofbirth' case sensitivity
                                            model1.setDateofBirth(jsonObject.has("DateOfBirth") ? jsonObject.get("DateOfBirth").getAsString() : "");
                                            if (jsonObject.has("dateofbirth") && !jsonObject.has("DateOfBirth")) {
                                                model1.setDateofBirth(jsonObject.get("dateofbirth").getAsString());
                                            }

                                            // Handle 'Address' and 'address' case sensitivity
                                            model1.setAddress(jsonObject.has("Address") ? jsonObject.get("Address").getAsString() : "");
                                            if (jsonObject.has("address") && !jsonObject.has("Address")) {
                                                model1.setAddress(jsonObject.get("address").getAsString());
                                            }

                                            System.out.println(model1.getUid() +"-"+model.getDriverUid());

                                            if(model1 != null) {
                                                if(model1.getUid() != null) {
                                                    if (model1.getUid().equals(model.getDriverUid())) {
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
                                                }
                                            }
                                        }
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
                            reference.child("Accounts").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot snapshot2: snapshot.getChildren())
                                    {
                                        Gson gson = new Gson();
                                        String jsonLog = gson.toJson(snapshot2.getValue());
                                        JsonObject jsonObject = gson.fromJson(jsonLog, JsonObject.class);

                                        UserAccountModel model1 = new UserAccountModel();
                                        model1.setFirstname(jsonObject.has("Firstname") ? jsonObject.get("Firstname").getAsString() : "");
                                        if(jsonObject.has("Uid")) {
                                            model1.setUid(jsonObject.has("Uid") ? jsonObject.get("Uid").getAsString() : "");
                                        } else {
                                            model1.setUid(jsonObject.has("uid") ? jsonObject.get("uid").getAsString() : "");
                                        }

                                        // Handle 'Firstname' and 'firstname' case sensitivity
                                        model1.setFirstname(jsonObject.has("Firstname") ? jsonObject.get("Firstname").getAsString() : "");
                                        if (jsonObject.has("firstname") && !jsonObject.has("Firstname")) {
                                            model1.setFirstname(jsonObject.get("firstname").getAsString());
                                        }

                                        // Handle 'Lastname' and 'lastname' case sensitivity
                                        model1.setLastname(jsonObject.has("Lastname") ? jsonObject.get("Lastname").getAsString() : "");
                                        if (jsonObject.has("lastname") && !jsonObject.has("Lastname")) {
                                            model1.setLastname(jsonObject.get("lastname").getAsString());
                                        }

                                        // Handle 'Email' and 'email' case sensitivity
                                        model1.setEmail(jsonObject.has("Email") ? jsonObject.get("Email").getAsString() : "");
                                        if (jsonObject.has("email") && !jsonObject.has("Email")) {
                                            model1.setEmail(jsonObject.get("email").getAsString());
                                        }

                                        // Handle 'PhoneNumber' and 'phonenumber' case sensitivity
                                        model1.setPhoneNumber(jsonObject.has("PhoneNumber") ? jsonObject.get("PhoneNumber").getAsString() : "");
                                        if (jsonObject.has("phoneNumber") && !jsonObject.has("PhoneNumber")) {
                                            model1.setPhoneNumber(jsonObject.get("phoneNumber").getAsString());
                                        }

                                        // Handle 'Username' and 'username' case sensitivity
                                        model1.setUsername(jsonObject.has("Username") ? jsonObject.get("Username").getAsString() : "");
                                        if (jsonObject.has("username") && !jsonObject.has("Username")) {
                                            model1.setUsername(jsonObject.get("username").getAsString());
                                        }

                                        // Handle 'Password' and 'password' case sensitivity
                                        model1.setPassword(jsonObject.has("Password") ? jsonObject.get("Password").getAsString() : "");
                                        if (jsonObject.has("password") && !jsonObject.has("Password")) {
                                            model1.setPassword(jsonObject.get("password").getAsString());
                                        }

                                        // Handle 'ProfilePicture' and 'profilepicture' case sensitivity
                                        model1.setProfilePicture(jsonObject.has("ProfilePicture") ? jsonObject.get("ProfilePicture").getAsString() : "");
                                        if (jsonObject.has("profilepicture") && !jsonObject.has("ProfilePicture")) {
                                            model1.setProfilePicture(jsonObject.get("profilepicture").getAsString());
                                        }

                                        // Handle 'DateOfBirth' and 'dateofbirth' case sensitivity
                                        model1.setDateofBirth(jsonObject.has("DateOfBirth") ? jsonObject.get("DateOfBirth").getAsString() : "");
                                        if (jsonObject.has("dateofbirth") && !jsonObject.has("DateOfBirth")) {
                                            model1.setDateofBirth(jsonObject.get("dateofbirth").getAsString());
                                        }

                                        // Handle 'Address' and 'address' case sensitivity
                                        model1.setAddress(jsonObject.has("Address") ? jsonObject.get("Address").getAsString() : "");
                                        if (jsonObject.has("address") && !jsonObject.has("Address")) {
                                            model1.setAddress(jsonObject.get("address").getAsString());
                                        }

                                        System.out.println(model1.getUid() +"-"+model.getDriverUid());

                                        if(model1 != null) {
                                            if(model1.getUid() != null) {
                                                if (model1.getUid().equals(model.getDriverUid())) {
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
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            //Toast.makeText(PassengerDriverList.this, "Some of other locations is exceeded the API distance limit.", Toast.LENGTH_SHORT).show();
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