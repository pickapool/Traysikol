package com.example.traysikol.Adapter.SaveDestinationsAdapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traysikol.DialogHelper;
import com.example.traysikol.GlobalClass;
import com.example.traysikol.Models.SaveDestinationModel;
import com.example.traysikol.Passenger.PassengerCountDown;
import com.example.traysikol.Passenger.PassengerHomeScreen;
import com.example.traysikol.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SaveDestinationAdapter extends RecyclerView.Adapter<SaveDestinationAdapter.ViewHolder> implements DialogHelper.ConfirmationListener {
    List<SaveDestinationModel> listDestination;
    Activity activity;
    DatabaseReference reference;

    public SaveDestinationAdapter(List<SaveDestinationModel> listDestination, Activity activity) {
        this.listDestination = listDestination;
        this.activity = activity;
        reference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public SaveDestinationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.template_save_destination, null);
        return new SaveDestinationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SaveDestinationAdapter.ViewHolder holder, int position) {
        SaveDestinationModel model = listDestination.get(position);
        holder.address.setText(model.getAddress());

        // Initialize Google Maps
        holder.mapView.onCreate(null);
        holder.mapView.onResume();
        holder.mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                holder.map = googleMap;
                // Set location on the map
                SetLocation(holder.map, model.getLatitude(), model.getLongitude());
            }
        });
        holder.use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalClass.IsUseLocation = true;
                GlobalClass.currentLocation.get(0).setLatitude(model.getLongitude());
                GlobalClass.currentLocation.get(0).setLatitude(model.getLatitude());
                //Close and get destination
                Intent ii = new Intent(activity, PassengerHomeScreen.class);
                activity.startActivity(ii);
                activity.finish();
            }
        });
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("SaveDestinations").child(model.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(activity, "Destination has been removed.", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return listDestination.size();
    }

    private void SetLocation(GoogleMap map,double latitude, double longitude) {
        LatLng myLocation = new LatLng(latitude, longitude);
        // Update the marker icon
        Bitmap b = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            b = BitmapFactory.decodeResource(activity.getResources(), R.drawable.mylocation);
        }
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 60, 60, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
        map.addMarker(new MarkerOptions()
                .position(myLocation)
                .title("Location")
                .icon(smallMarkerIcon));
    }

    @Override
    public void onConfirmation(boolean confirmed) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView address;
        Button remove, use;
        MapView mapView;
        GoogleMap map;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            address = itemView.findViewById(R.id.address);
            remove = itemView.findViewById(R.id.remove);
            use = itemView.findViewById(R.id.use);
            mapView = itemView.findViewById(R.id.googleMap);
        }
    }
}
