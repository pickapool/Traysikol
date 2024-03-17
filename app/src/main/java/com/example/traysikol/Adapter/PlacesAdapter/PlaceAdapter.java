package com.example.traysikol.Adapter.PlacesAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.traysikol.Models.OSRPlacesModels.Features;

import java.util.List;

public class PlaceAdapter extends ArrayAdapter<Features> {
    private LayoutInflater inflater;

    public PlaceAdapter(Context context, List<Features> places) {
        super(context, 0, places);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        Features place = getItem(position);
        if (place != null) {
            textView.setText(place.properties.label);
        }

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        // Return a unique identifier for each item
        return getItem(position).hashCode();
    }
}