package com.example.traysikol.Passenger;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.traysikol.Adapter.AdapterDrivers;
import com.example.traysikol.Adapter.PlacesAdapter.PlaceAdapter;
import com.example.traysikol.ChooseAccount;
import com.example.traysikol.CurrentRequest;
import com.example.traysikol.Drivers.DriversHome;
import com.example.traysikol.Enums.CommuteStatus;
import com.example.traysikol.Enums.OnlineStatus;
import com.example.traysikol.Extensions;
import com.example.traysikol.GlobalClass;
import com.example.traysikol.Models.CommuteModel;
import com.example.traysikol.Models.FareModel;
import com.example.traysikol.Models.OSRDirectionModels.ORSFeature;
import com.example.traysikol.Models.OSRDirectionModels.ORSGeometry;
import com.example.traysikol.Models.OSRDirectionModels.ORSResponse;
import com.example.traysikol.Models.OSRPlacesModels.FeatureCollection;
import com.example.traysikol.Models.OSRPlacesModels.Features;
import com.example.traysikol.Models.OnlineDriverModel;
import com.example.traysikol.Models.PassengerRoute;
import com.example.traysikol.Models.UserAccountModel;
import com.example.traysikol.R;
import com.example.traysikol.RotatingDrawable;
import com.example.traysikol.Services.DriverAcceptService;
import com.example.traysikol.Services.RequestADriverService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;

public class PassengerHomeScreen extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap googleMaps;
    Marker lastMarker = null;
    LatLng myLocation = null;
    LatLng myDestination = null;
    Runnable runnable;
    FusedLocationProviderClient fusedLocationProviderClient;
    private AutoCompleteTextView autoCompleteTextView;
    List<Features> ListOfFeatures;
    Handler handler = new Handler();
    private PlaceAdapter adapter;
    private RotatingDrawable rotatingDrawable;
    ImageView home, commute, driversNear, myProfile;
    String myAddress, myDestinationAddress, fare, distance, time = "";
    private View customLayout;
    int lastIcon = 1;
    ImageView toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    PassengerRoute passengerRoute = new PassengerRoute();
    DatabaseReference reference;
    List<Marker> driversMarker = new ArrayList<>();
    List<CommuteModel> CommuteModels = new ArrayList<>();
    FirebaseUser user;
    PassengerHomeScreen.UniqueRandomGenerator generator = new PassengerHomeScreen.UniqueRandomGenerator();
    boolean rated = false;
    int countStudent = 0, countRegular = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_home_screen);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        autoCompleteTextView = findViewById(R.id.searchPlaces);
        home = findViewById(R.id.home);
        commute = findViewById(R.id.commute);
        driversNear = findViewById(R.id.driversNear);
        myProfile = findViewById(R.id.myProfile);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        reference = FirebaseDatabase.getInstance().getReference();
        user  = FirebaseAuth.getInstance().getCurrentUser();

        ListOfFeatures = new ArrayList<>();

        Intent serviceIntent = new Intent(PassengerHomeScreen.this, DriverAcceptService.class);
        stopService(serviceIntent);
        startService(serviceIntent);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);
        CheckLocationIsOn();
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        CircleImageView pp = headerView.findViewById(R.id.profilePicture);
        TextView fullname = headerView.findViewById(R.id.fullName);
        TextView address = headerView.findViewById(R.id.address);

        fullname.setText(GlobalClass.UserAccount.getFullName());
        address.setText(TextUtils.isEmpty(GlobalClass.UserAccount.getAddress()) ? "Address not found" : GlobalClass.UserAccount.getAddress());
        if(!TextUtils.isEmpty(GlobalClass.UserAccount.getProfilePicture())) {
            Picasso.get().load(GlobalClass.UserAccount.getProfilePicture()).into(pp);
        }
        home.setOnClickListener(view -> ChangeIcons(1));
        commute.setOnClickListener(view ->
        {
            int count = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                count = CommuteModels.stream().filter(e ->
                        e.commuteStatus == CommuteStatus.InProgress && e.isOccupied()
                ).collect(Collectors.toList()).size();
                if (count > 0) {
                    Toast.makeText(PassengerHomeScreen.this, "You are currently occupied!", Toast.LENGTH_SHORT).show();
                } else {
                    ChangeIcons(2);
                }
            }
        });
        driversNear.setOnClickListener(view -> {
            reference.child("OnlineDrivers").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    googleMaps.clear();
                    SetLocation(false);
                    for(DataSnapshot snapshot1 : snapshot.getChildren())
                    {
                        OnlineDriverModel oDriver = snapshot1.getValue(OnlineDriverModel.class);
                        if(oDriver.getOnlineStatus() == OnlineStatus.Online) {
                            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.traysikol_icon);
                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);
                            BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                            LatLng lng = new LatLng(oDriver.getLatitude(), oDriver.getLongitude());
                            Marker newMarker = googleMaps.addMarker(new MarkerOptions().position(lng).title(oDriver.getDriverName()).icon(smallMarkerIcon));
                        }
                    }
                    //reference.removeEventListener(this);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            ChangeIcons(3);
        });
        myProfile.setOnClickListener(view ->
        {
            ChangeIcons(4);
            Intent ii = new Intent(PassengerHomeScreen.this, PassengerProfile.class);
            startActivity(ii);
            finish();
        });
        toolbar.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });
        autoCompleteTextView.setOnTouchListener((view, motionEvent) -> {
            final int DRAWABLE_RIGHT = 2;
            if (autoCompleteTextView.getText().toString().length() > 2) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= (autoCompleteTextView.getRight() - autoCompleteTextView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        setCompoundDrawable(R.drawable.search_animation);
                        SearchPlaces(autoCompleteTextView.getText().toString());
                        return true;
                    }
                }
            }
            return false;
        });
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            // Get the selected item from the adapter
            Features selectedPlace = (Features) parent.getItemAtPosition(position);
            if (selectedPlace != null) {
                autoCompleteTextView.setText(selectedPlace.properties.label);

                LatLng latlng = new LatLng(selectedPlace.geometry.coordinates[1], selectedPlace.geometry.coordinates[0]);
                googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 200));
            }
        });
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                final String newText = editable.toString().toLowerCase(Locale.getDefault());

                if (newText.length() >= 3) { // Adjust the minimum text length as needed
                    handler.removeCallbacksAndMessages(null); // Cancel previous delayed tasks
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            filterFeatures(newText);
                        }
                    }, 400);
                }
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.drivers) {
                   Intent ii = new Intent(PassengerHomeScreen.this , PassengerDriverList.class);
                   startActivity(ii);
                   finish();
                } else if (itemId == R.id.history) {
                    Intent ii = new Intent(PassengerHomeScreen.this, PassengerHistory.class);
                    startActivity(ii);
                    finish();
                } else if (itemId == R.id.myRide) {
                    int count = 0;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        List<CommuteModel>  list = CommuteModels.stream().filter(e ->
                                e.commuteStatus == CommuteStatus.InProgress
                        ).collect(Collectors.toList());
                        count = list.size();
                        if (count > 0) {
                            CommuteModel currentRequest =  list.get(0);
                            CurrentRequest current = new CurrentRequest(currentRequest);
                            current.show(getSupportFragmentManager(), "PassengerHomeScreen");
                        } else {
                            Toast.makeText(PassengerHomeScreen.this, "There are no current trips.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (itemId == R.id.contactUs) {
                    showDialog("Got questions, feedback, or concerns? We’re here to help! Reach out to us through the following channels:  \n" +
                            "- Email TRICYRIDE@gmail.com  \n" +
                            "- Phone: (+63) 965-955-2562  \n" +
                            "\n" +
                            "\n" +
                            "You can also follow us on social media for updates and announcements:  \n" +
                            "- *Facebook:* [TricyRide Official\n" +
                            "- Instagram: [@TricyRidePH](#)  \n", "Contact Us");
                    // Handle Profile menu item click
                } else if (itemId == R.id.aboutUs) {
                    showDialog("Welcome to TricyRide, your trusted partner in enhancing transportation convenience for communities.\n" +
                            "\n" +
                            "At TricyRide, we aim to:  \n" +
                            "- Increase Transportation Availability:*Connecting passengers with nearby drivers for hassle-free trips.  \n" +
                            "- Reduce Waiting Times: Save time by booking your ride instantly through our app.  \n" +
                            "- Streamline Bookings: Enjoy a user-friendly platform for efficient ride reservations.  \n" +
                            "- Promote Safety and Reliability:Work with verified tricycle drivers for peace of mind.  \n" +
                            "- Foster Local Empowerment:Support our hardworking local tricycle drivers by providing a reliable income source.  \n" +
                            "\n" +
                            "We are committed to transforming your travel into a seamless, enjoyable experience while supporting the local economy.  \n", "About Us");
                    // Handle About Us menu item click
                } else if (itemId == R.id.logout) {
                    FirebaseAuth.getInstance().signOut();
                    stopService(serviceIntent);
                    Intent ii = new Intent(PassengerHomeScreen.this, ChooseAccount.class);
                    startActivity(ii);
                    finish();
                }
                return true;
            }
        });
        ChangeIcons(1);
        GetMyCommutes();
        reference.child("Commutes").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                CommuteModel commute = snapshot.getValue(CommuteModel.class);
                if(commute.getPassengerUid().equals(GlobalClass.UserAccount.getUid())) {
                    if(commute.getCommuteStatus().equals(CommuteStatus.Done) && !snapshot.child("isRated").getValue(Boolean.class)) {
                        Intent ii = new Intent(PassengerHomeScreen.this, PassengerHistory.class);
                        startActivity(ii);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void showCustomDialog(CommuteModel model) {
        if(!TextUtils.isEmpty(model.getDriverUid())) {
            reference.child("Accounts").child(model.getDriverUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserAccountModel driverModel = snapshot.getValue(UserAccountModel.class);
                    model.setDriverAccount(driverModel);

                    LayoutInflater inflater = getLayoutInflater();
                    View customView = inflater.inflate(R.layout.template_history_rate, null);

                    TextView date = customView.findViewById(R.id.time_text);
                    TextView address1 = customView.findViewById(R.id.address1);
                    TextView address2 = customView.findViewById(R.id.address2);
                    TextView driverName = customView.findViewById(R.id.driverName);
                    RatingBar rate = customView.findViewById(R.id.ratingBar);
                    CircleImageView c = customView.findViewById(R.id.profilePicture);

                    int number = 0;
                    number = generator.generateUniqueRandom();

                    if (!TextUtils.isEmpty(model.getDriverAccount().getProfilePicture())) {
                        Picasso.get().load(model.getDriverAccount().getProfilePicture()).into(c);
                    } else {
                        if (number == 1) {
                            Picasso.get().load(R.drawable.person1).into(c);
                        } else if (number == 2) {
                            Picasso.get().load(R.drawable.person2).into(c);
                        } else if (number == 3) {
                            Picasso.get().load(R.drawable.person3).into(c);
                        } else {
                            Picasso.get().load(R.drawable.person4).into(c);
                        }
                    }

                    SimpleDateFormat formats = new SimpleDateFormat("MMMM dd, yyyy hh:mm a");
                    date.setText(formats.format(model.getCommuteDate()));
                    address1.setText(model.getAddress1());
                    address2.setText(model.getAddress2());
                    driverName.setText(model.getDriverAccount().getFullName());


                    AlertDialog.Builder builder = new AlertDialog.Builder(PassengerHomeScreen.this);
                    builder.setView(customView);

                    builder.setTitle("Driver Rating")
                            .setCancelable(true)
                            .setPositiveButton("DONE", (dialog, which) -> {
                                SetRating(model, rate.getRating(), dialog);
                            });
                    AlertDialog dialog = builder.create();
                    dialog.setCancelable(false);
                    dialog.show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
    private void SetRating(CommuteModel model, float v, DialogInterface dialog) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("rating", v);
        hashMap.put("commuteStatus", CommuteStatus.Done);
        hashMap.put("isRated", true);
        reference.child("Commutes").child(model.getKey()).updateChildren(hashMap).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Toast.makeText(PassengerHomeScreen.this, "Thank you for your rating.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
    public class UniqueRandomGenerator {
        private int previousNumber;
        private Random random;

        public UniqueRandomGenerator() {
            random = new Random();
            previousNumber = -1; // Initialize to an invalid number
        }

        public int generateUniqueRandom() {
            int currentNumber;
            do {
                currentNumber = random.nextInt(4) + 1; // Generates random number between 1 and 4
            } while (currentNumber == previousNumber); // Loop until a different number is generated

            previousNumber = currentNumber; // Update the previous number
            return currentNumber;
        }
    }
    private void showDialog(String message, String title) {
        // Create an AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the dialog title
        builder.setTitle(title);

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

    private void ChangeIcons(int position) {

        lastIcon = position;
        if (position == 2) {
            int count = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                count = CommuteModels.stream().filter(e ->
                        e.commuteStatus == CommuteStatus.InProgress
                ).collect(Collectors.toList()).size();
                if (count > 0) {
                    Toast.makeText(PassengerHomeScreen.this, "You have current trip in progress!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if(TextUtils.isEmpty(myAddress) ||
                    TextUtils.isEmpty(myDestinationAddress) ||
                    TextUtils.isEmpty("20") ||
                    TextUtils.isEmpty(distance) ||
                    TextUtils.isEmpty(time))
            {
                Toast.makeText(this, "Please select a destination", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        home.setImageResource(R.drawable.home_icon);
        commute.setImageResource(R.drawable.commute_icon);
        driversNear.setImageResource(R.drawable.drivers_icon);
        myProfile.setImageResource(R.drawable.edit_profile_icon);
        switch (position) {
            case 1:
                home.setImageResource(R.drawable.home_icon_selected);
                break;
            case 2:
                if(!user.isEmailVerified()) {
                    Toast.makeText(this, "Account is not verified, go to profile and get the verification.", Toast.LENGTH_SHORT).show();
                    return;
                }
                commute.setImageResource(R.drawable.commute_icon_selected);
                ShowPassengersList();
                //ShowDialogConfirm(myAddress, myDestinationAddress, GlobalClass.CommuteModel.getFare(), distance, time);
                break;
            case 3:
                driversNear.setImageResource(R.drawable.drivers_icon_selected);
                break;
            case 4:
                myProfile.setImageResource(R.drawable.profile_icon_selected);
                break;
            default:
                home.setImageResource(R.drawable.home_icon);
                commute.setImageResource(R.drawable.commute_icon);
                driversNear.setImageResource(R.drawable.drivers_icon);
                myProfile.setImageResource(R.drawable.edit_profile_icon);
                break;
        }
    }

    private void SetDestination(LatLng latLng) {
        GlobalClass.CommuteModel.setPassengerDestinationLatitude(latLng.latitude);
        GlobalClass.CommuteModel.setPassengerDestinationLongitude(latLng.longitude);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.destination);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 60, 60, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
        Marker newMarker = googleMaps.addMarker(new MarkerOptions().position(latLng).title("Destination").icon(smallMarkerIcon));
        lastMarker = newMarker;
    }

    private void SetLocation(boolean move) {
        myLocation = new LatLng(GlobalClass.currentLocation.get(0).getLatitude(), GlobalClass.currentLocation.get(0).getLongitude());
        // Update the marker icon
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.marker1);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 60, 60, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
        if(move) {
            googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
        }
        googleMaps.addMarker(new MarkerOptions()
                .position(myLocation)
                .title("Location")
                .icon(smallMarkerIcon));
        GlobalClass.CommuteModel.setPassengerLatitude(myLocation.latitude);
        GlobalClass.CommuteModel.setPassengerLongitude(myLocation.longitude);
    }

    private void CheckLocationIsOn() {
        ProgressDialog progressBar = new ProgressDialog(PassengerHomeScreen.this);
        progressBar.setTitle("Getting Location");
        progressBar.setMessage("Please wait...");
        progressBar.setCancelable(false);
        progressBar.show();
        if (ActivityCompat.checkSelfPermission(PassengerHomeScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(PassengerHomeScreen.this, Locale.getDefault());
                        try {
                            progressBar.dismiss();
                            if (GlobalClass.IsUseLocation) {
                                GlobalClass.currentLocation = geocoder
                                        .getFromLocation(GlobalClass.currentLocation.get(0).getLatitude(),
                                                GlobalClass.currentLocation.get(0).getLongitude(), 1);
                            } else {
                                GlobalClass.currentLocation = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                myAddress = GlobalClass.currentLocation.get(0).getAddressLine(0);
                            }
                            GlobalClass.IsUseLocation = false;
                            SetLocation(true);
                        } catch (IOException e) {
                            //progressBar.dismiss();
                            Toast.makeText(PassengerHomeScreen.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        //progressBar.dismiss();
                        Toast.makeText(PassengerHomeScreen.this, "Please toggle your Location and restart the app.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    //progressBar.dismiss();
                    System.out.println("error" + task.getException().getMessage());
                    Toast.makeText(PassengerHomeScreen.this, "Sorry, there was a problem getting you location.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            //progressBar.dismiss();
            Toast.makeText(PassengerHomeScreen.this, "No permission granted. Please reisntall the app.", Toast.LENGTH_SHORT).show();
        }
    }

    private void filterFeatures(String newText) {
        List<Features> filteredFeatures = new ArrayList<>();
        for (Features feature : ListOfFeatures) {
            if (feature.properties.label.toLowerCase(Locale.getDefault()).contains(newText)) {
                filteredFeatures.add(feature);
            }
        }
        updateAdapter(filteredFeatures);
    }

    private void updateAdapter(List<Features> filteredFeatures) {
        adapter = new PlaceAdapter(PassengerHomeScreen.this, filteredFeatures);
        autoCompleteTextView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void setCompoundDrawable(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        if (drawable != null) {
            rotatingDrawable = new RotatingDrawable(this, R.drawable.search_animation);
            autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, rotatingDrawable, null);
            // Start the rotation animation
            rotatingDrawable.start();
        } else {
            DefaultIcons();
        }
    }

    private void DefaultIcons() {
        Drawable drawable1 = getResources().getDrawable(R.drawable.search_alt_2_svgrepo_com);
        autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable1, null);
    }

    private void SearchPlaces(String query) {
        ListOfFeatures = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(PassengerHomeScreen.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                GlobalClass.PlacesAPI(query), response -> {
            try {
                Gson gson = new Gson();
                FeatureCollection featureCollection = gson.fromJson(response, FeatureCollection.class);
                for (Features features : featureCollection.features) {
                    ListOfFeatures.add(features);
                }
                //Toast.makeText(PassengerCommute.this, String.valueOf(ListOfFeatures.size()), Toast.LENGTH_SHORT).show();
                updateAdapter(ListOfFeatures);
                DefaultIcons();
            } catch (Exception ee) {
                DefaultIcons();
                System.out.println("error4 - " + ee.getMessage());
            }
        }, error -> {
            System.out.println("error5 - " + error);
            DefaultIcons();
            Toast.makeText(PassengerHomeScreen.this, String.valueOf(error), Toast.LENGTH_SHORT).show();
        });
        queue.add(stringRequest);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMaps = googleMap;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        googleMaps.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                ChangeIcons(1);
                ProgressDialog dialog = new ProgressDialog(PassengerHomeScreen.this);
                dialog.setTitle("Getting route");
                dialog.setMessage("Loading...");
                dialog.show();

                myDestination = latLng;
                if (lastMarker != null) {
                    lastMarker.remove();
                }

                SetDestination(latLng);
                String start = String.format("%s,%s", myLocation.longitude, myLocation.latitude);
                String end = String.format("%s,%s", myDestination.longitude, myDestination.latitude);
                //Toast.makeText(PassengerCommute.this, start, Toast.LENGTH_SHORT).show();
                RequestQueue queue = Volley.newRequestQueue(PassengerHomeScreen.this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://api.openrouteservice.org/v2/directions/driving-car?api_key=5b3ce3597851110001cf6248865cba12d4c44f36b368b2b065a07c00&start=" + start + "&end=" + end, new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            googleMaps.clear();

                            SetDestination(myDestination);
                            SetLocation(true);

                            Gson gson = new Gson();
                            ORSResponse orsResponse = gson.fromJson(response, ORSResponse.class);
                            ORSFeature feature = orsResponse.features.get(0);

                            ORSGeometry geometry = feature.geometry;

                            if (geometry != null && "LineString".equals(geometry.type)) {
                                passengerRoute.setPassengerRoute(Extensions.convertToList(geometry.coordinates));
                                GlobalClass.CommuteModel.setPassengerRoute(passengerRoute);
                                double[][] coordinates = geometry.coordinates;

                                PolylineOptions polylineOptions = new PolylineOptions();
                                for (double[] coordinate : coordinates) {
                                    double latitude = coordinate[1];
                                    double longitude = coordinate[0];
                                    LatLng latLng = new LatLng(latitude, longitude);
                                    polylineOptions.add(latLng);
                                }

                                //Set disantance and time
                                Polyline polyline = googleMap.addPolyline(polylineOptions);

                                //Get address to
                                Geocoder geocoder = new Geocoder(PassengerHomeScreen.this, Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(orsResponse.metadata.query.coordinates[1][1], orsResponse.metadata.query.coordinates[1][0], 1);
                                myDestinationAddress = addresses.get(0).getAddressLine(0);
                                GlobalClass.DistanceValue = GlobalClass.GetDistance(orsResponse);
                                distance = GlobalClass.convertDistance(GlobalClass.GetDistance(orsResponse));
                                time = GlobalClass.GetTime(orsResponse);
                                GlobalClass.CommuteModel.setDistance(distance);
                                reference.child("Fare").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        GlobalClass.Fares = dataSnapshot.getValue(FareModel.class);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                /*double currentFare = 0.0;
                                if(GlobalClass.DistanceValue <= 4000) {
                                    currentFare = 15;
                                } else if(GlobalClass.DistanceValue > 4000 && GlobalClass.DistanceValue <= 5000) {
                                    currentFare = 20;
                                } else if(GlobalClass.DistanceValue > 5000 && GlobalClass.DistanceValue <= 7000) {
                                    currentFare = 25;
                                } else if(GlobalClass.DistanceValue > 7000 && GlobalClass.DistanceValue <= 9000) {
                                    currentFare = 30;
                                } else {
                                    double perKM = 2.5;
                                    double toKm = GlobalClass.DistanceValue / 1000;
                                    currentFare = 30 + (toKm * perKM);
                                }

                                //GlobalClass.CurrentFare = currentFare;
                                DecimalFormat df = new DecimalFormat("#.##");
                                df.setRoundingMode(RoundingMode.HALF_UP);
                                GlobalClass.CommuteModel.setFare(df.format(currentFare));*/

                            }
                            dialog.dismiss();

                        } catch (Exception ee) {
                            dialog.dismiss();
                            System.out.println("error1 - " + ee.getMessage());
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("error2 - " + error);
                        Toast.makeText(PassengerHomeScreen.this, String.valueOf(error), Toast.LENGTH_SHORT).show();
                    }
                });
                queue.add(stringRequest);
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    public static DisplayMetrics getDeviceMetrics(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(metrics);
        return metrics;
    }

    //Address-> Address address = GlobalClass.currentLocation.get(0); -> String addressLine = address.getAddressLine(0);
    private void ShowPassengersList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PassengerHomeScreen.this);
        View customLayout = getLayoutInflater().inflate(R.layout.layout_passengers_count, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0);

        WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
        lWindowParams.copyFrom(dialog.getWindow().getAttributes());
        lWindowParams.width = 700;
        lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ImageView close = customLayout.findViewById(R.id.closeDialog);
        close.setOnClickListener(view -> {
            dialog.dismiss();
            countStudent = countRegular = 0;
            ChangeIcons(1);
        });
        ImageView minusS = customLayout.findViewById(R.id.minusStudent);
        ImageView plusS = customLayout.findViewById(R.id.plusStudent);
        TextView countS = customLayout.findViewById(R.id.countStudent);

        ImageView minusR = customLayout.findViewById(R.id.minusRegular);
        ImageView plusR = customLayout.findViewById(R.id.plusRegular);
        TextView countR = customLayout.findViewById(R.id.countRegular);

        countS.setText(String.valueOf(countStudent));
        countR.setText(String.valueOf(countRegular));

        Button proceed = customLayout.findViewById(R.id.confirmCommute);

        minusS.setOnClickListener(view -> {
            if(countStudent == 0)
                return;
            countStudent -= 1;
            countS.setText(String.valueOf(countStudent));
        });
        plusS.setOnClickListener(view -> {
            countStudent += 1;
            countS.setText(String.valueOf(countStudent));
        });
        minusR.setOnClickListener(view -> {
            if(countRegular == 0)
                return;
            countRegular -= 1;
            countR.setText(String.valueOf(countRegular));
        });
        plusR.setOnClickListener(view -> {
            countRegular += 1;
            countR.setText(String.valueOf(countRegular));
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(countStudent == 0 && countRegular == 0) {
                    Toast.makeText(PassengerHomeScreen.this, "No passengers.", Toast.LENGTH_SHORT).show();
                    return;
                }
                double countStudentsTotal = 0;
                double countRegularTotal = 0;
                if(countStudent > 0)
                {
                    double discountStudents = countStudent * 2;
                    countStudentsTotal = (GlobalClass.calculateStudentFare(GlobalClass.DistanceValue, GlobalClass.Fares) * countStudent) - discountStudents;
                }
                if(countRegular > 0) {
                    countRegularTotal = (GlobalClass.calculateRegularFare(GlobalClass.DistanceValue, GlobalClass.Fares) * countRegular);
                }
                int totalPass = countStudent + countRegular;
                if(totalPass > 5) {
                    Toast.makeText(PassengerHomeScreen.this, "Maximum passenger is 5.", Toast.LENGTH_SHORT).show();
                    return;
                }

                double total = countRegularTotal + countStudentsTotal;
                GlobalClass.CommuteModel.setRegularCount(countRegular);
                GlobalClass.CommuteModel.setStudentCount(countStudent);
                ShowDialogConfirm(myAddress, myDestinationAddress, total , distance, time);
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lWindowParams);

    }
    private void ShowDialogConfirm(String address1, String address2, double fare, String distance, String time) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(PassengerHomeScreen.this);
            View customLayout = getLayoutInflater().inflate(R.layout.layout_confirm_dialog, null);
            builder.setView(customLayout);
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setDimAmount(0);

            WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
            lWindowParams.copyFrom(dialog.getWindow().getAttributes());
            lWindowParams.width = 700;
            lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;


            TextView add1 = customLayout.findViewById(R.id.address1);
            TextView add2 = customLayout.findViewById(R.id.address2);
            TextView fares = customLayout.findViewById(R.id.fare);
            TextView dis = customLayout.findViewById(R.id.distance);
            TextView tim = customLayout.findViewById(R.id.times);
            ImageView close = customLayout.findViewById(R.id.closeDialog);
            Button confirmBtn = customLayout.findViewById(R.id.confirmCommute);
            LinearLayout btns = customLayout.findViewById(R.id.buttonsRide);
            Button rideNow = customLayout.findViewById(R.id.rideNow);
            Button ridelater = customLayout.findViewById(R.id.rideLater);
            CheckBox check = customLayout.findViewById(R.id.isStudent);

            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.HALF_UP);
            double newFare = 0.0;
            /*check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    double newFare = GlobalClass.CurrentFare;
                    //Toast.makeText(PassengerHomeScreen.this, String.valueOf(newFare), Toast.LENGTH_SHORT).show();
                    if(b) {
                        if(newFare == 15)
                            newFare = newFare - 2;
                        else
                            newFare = newFare - 5;
                    } else {
                        newFare = GlobalClass.CurrentFare;
                    }
                    fares.setText("₱ " + (df.format(fare)));
                    GlobalClass.CommuteModel.setFare(String.valueOf(fare));
                }
            });*/


            add1.setText(address1);
            add2.setText(address2);
            fares.setText("₱ " + (df.format(fare)));
            dis.setText(distance);
            tim.setText("Estimated duration: " + time);
            GlobalClass.CommuteModel.setFare(String.valueOf(fare));

            close.setOnClickListener(view -> {
                dialog.dismiss();
                ChangeIcons(1);
            });
            rideNow.setOnClickListener(view -> {
                RideNow();
            });
            ridelater.setOnClickListener(view -> {
                dialog.dismiss();
                PassengerCountDown countDown = new PassengerCountDown(myAddress, myDestinationAddress, (df.format(newFare)), distance, time);
                countDown.show(getSupportFragmentManager(), "PassengerHomeScreen");
            });
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right);
                    view.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            confirmBtn.setVisibility(View.GONE);
                            btns.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            });

            dialog.show();
            dialog.getWindow().setAttributes(lWindowParams);
        } catch (Exception ee) {
            System.out.println("66213213" + ee.getMessage());
        }
    }
    private void RideNow()
    {
        String key = reference.push().getKey();

        GlobalClass.CommuteModel.setDriverUid("");
        GlobalClass.CommuteModel.setCommuteStatus(CommuteStatus.InProgress);
        GlobalClass.CommuteModel.setOccupied(false);
        GlobalClass.CommuteModel.setKey(key);
        GlobalClass.CommuteModel.setPassengerUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        GlobalClass.CommuteModel.setCommuteDate(Calendar.getInstance().getTime());
        GlobalClass.CommuteModel.setTime(time);
        GlobalClass.CommuteModel.setAddress1(myAddress);
        GlobalClass.CommuteModel.setAddress2(myDestinationAddress);
        GlobalClass.CommuteModel.setRated(false);

        reference.child("Commutes").child(key).setValue(GlobalClass.CommuteModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ProgressDialog progressDialog = new ProgressDialog(PassengerHomeScreen.this);
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Sending to drivers");
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                if(task.isSuccessful())
                {
                    Intent ii = new Intent(PassengerHomeScreen.this, PassengerDoneRequest.class);
                    startActivity(ii);
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(PassengerHomeScreen.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void GetMyCommutes() {
        reference.child("Commutes").orderByChild("passengerUid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CommuteModels = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    CommuteModel model = snapshot1.getValue(CommuteModel.class);
                    reference.child("Accounts").child(model.getDriverUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserAccountModel driver = snapshot.getValue(UserAccountModel.class);
                            model.DriverAccount = driver;
                            model.PassengerAccount = GlobalClass.UserAccount;
                            CommuteModels.add(model);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}