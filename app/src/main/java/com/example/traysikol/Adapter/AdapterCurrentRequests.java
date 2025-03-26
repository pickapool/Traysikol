package com.example.traysikol.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traysikol.CurrentRequest;
import com.example.traysikol.Models.CommuteModel;
import com.example.traysikol.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class AdapterCurrentRequests extends RecyclerView.Adapter<AdapterCurrentRequests.ViewHolder> {
    List<CommuteModel> commuteModelList;
    Activity activity;
    FragmentManager fragmentManager;

    public AdapterCurrentRequests(List<CommuteModel> commuteModelList, Activity activity, FragmentManager fragmentManager) {
        this.commuteModelList = commuteModelList;
        this.activity = activity;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public AdapterCurrentRequests.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.template_drivers_current_request, null);
        return new AdapterCurrentRequests.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCurrentRequests.ViewHolder holder, int position) {
        CommuteModel model = commuteModelList.get(position);
        holder.address1.setText(model.getAddress1());
        holder.address2.setText(model.getAddress2());

        holder.mapView.onCreate(null);
        holder.mapView.onResume();
        holder.mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                holder.map = googleMap;
                // Set location on the map
                SetLocation(holder.map, model);
            }
        });
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentRequest current = new CurrentRequest(model);
                current.show(fragmentManager, "DriversHome");
            }
        });
    }
    private void SetLocation(GoogleMap map,CommuteModel model) {
        LatLng passLocation = new LatLng(model.getPassengerLatitude(), model.getPassengerLongitude());
        LatLng passDesti = new LatLng(model.getPassengerDestinationLatitude(), model.getPassengerDestinationLongitude());
        // Update the marker icon
        Bitmap b = BitmapFactory.decodeResource(activity.getResources(), R.drawable.mylocation);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 60, 60, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

        Bitmap b1 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.taxi_destination);
        Bitmap smallMarker1 = Bitmap.createScaledBitmap(b1, 60, 60, false);
        BitmapDescriptor smallMarkerIcon1 = BitmapDescriptorFactory.fromBitmap(smallMarker1);


        map.moveCamera(CameraUpdateFactory.newLatLngZoom(passLocation, 11));
        map.addMarker(new MarkerOptions()
                .position(passLocation)
                .title(model.getAddress1())
                .icon(smallMarkerIcon));
        map.addMarker(new MarkerOptions()
                .position(passDesti)
                .title(model.getAddress2())
                .icon(smallMarkerIcon1));

        PolylineOptions polylineOptions = new PolylineOptions();
        for (List<Double> coordinates : model.getPassengerRoute().getPassengerRoute()) {
            double latitude = coordinates.get(1);
            double longitude = coordinates.get(0);
            LatLng latLng = new LatLng(latitude, longitude);
            polylineOptions.add(latLng);
        }
        Polyline polyline = map.addPolyline(polylineOptions);
    }
    @Override
    public int getItemCount() {
        return commuteModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MapView mapView;
        GoogleMap map;
        TextView address1, address2;
        ImageView view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mapView = itemView.findViewById(R.id.googleMap);
            address1 = itemView.findViewById(R.id.address1);
            address2 = itemView.findViewById(R.id.address2);
            view = itemView.findViewById(R.id.viewRequest);
        }
    }
}
