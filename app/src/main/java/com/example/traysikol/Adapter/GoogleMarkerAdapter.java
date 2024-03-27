package com.example.traysikol.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.traysikol.Models.CommuteModel;
import com.example.traysikol.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class GoogleMarkerAdapter implements GoogleMap.InfoWindowAdapter {
    private View popup=null;
    private LayoutInflater inflater=null;
    private Context ctxt= null;
    private Marker lastMarker=null;

    public GoogleMarkerAdapter(){}
    public GoogleMarkerAdapter(Context context) {
        this.ctxt = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
       return null;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        CommuteModel model = (CommuteModel) marker.getTag();
        if (popup == null) {
            popup=inflater.inflate(R.layout.layout_markerinfo, null);
        }
        if (lastMarker == null || !lastMarker.getId().equals(marker.getId())) {
            lastMarker = marker;
        }

        TextView fa = (TextView)popup.findViewById(R.id.fare);
        TextView dis = (TextView)popup.findViewById(R.id.distance);
        fa.setText(model.getFare());
        dis.setText(model.getDistance());
        return(popup);
    }
}
