package com.example.traysikol.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traysikol.Models.CommuteModel;
import com.example.traysikol.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.ViewHolder>{

    List<CommuteModel> commuteModelList;
    Activity activity;
    DatabaseReference reference;

    public AdapterHistory(List<CommuteModel> commuteModelList, Activity activity) {
        this.commuteModelList = commuteModelList;
        this.activity = activity;
        reference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public AdapterHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.template_history , null);
        return new AdapterHistory.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHistory.ViewHolder holder, int position) {
        CommuteModel model = commuteModelList.get(position);

        SimpleDateFormat formats = new SimpleDateFormat("MMMM dd, yyyy hh:mm a");

        holder.time.setText(formats.format(model.getCommuteDate()));
        holder.address1.setText(model.getAddress1());
        holder.address2.setText(model.getAddress2());
        holder.ratingBar.setRating(model.getRating());

        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("rating", v);
                reference.child("Commutes").child(model.getKey()).updateChildren(hashMap);
            }
        });
    }

    @Override
    public int getItemCount() {
        return commuteModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView time, address1, address2;
        RatingBar ratingBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.time_text);
            address1 = itemView.findViewById(R.id.address1);
            address2 = itemView.findViewById(R.id.address2);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
