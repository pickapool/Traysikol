package com.example.traysikol.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traysikol.Models.CommuteModel;
import com.example.traysikol.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.ViewHolder>{

    List<CommuteModel> commuteModelList;
    Activity activity;

    public AdapterHistory(List<CommuteModel> commuteModelList, Activity activity) {
        this.commuteModelList = commuteModelList;
        this.activity = activity;
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

    }

    @Override
    public int getItemCount() {
        return commuteModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView time, address1, address2;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.time_text);
            address1 = itemView.findViewById(R.id.address1);
            address2 = itemView.findViewById(R.id.address2);
        }
    }
}
