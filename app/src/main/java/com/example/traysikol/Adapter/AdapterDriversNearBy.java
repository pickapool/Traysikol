package com.example.traysikol.Adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traysikol.Enums.OnlineStatus;

import android.Manifest;

import com.example.traysikol.Models.OnlineDriverModel;
import com.example.traysikol.Models.UserAccountModel;
import com.example.traysikol.R;
import com.example.traysikol.UniqueRandomGenerator;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterDriversNearBy extends RecyclerView.Adapter<AdapterDriversNearBy.ViewHolder> {
    private static final int OFFLINE = 1;
    private static final int ONLINE = 2;
    DatabaseReference reference;
    List<OnlineDriverModel> onlineDriverModelList;
    Activity activity;
    BottomSheetDialog bottomSheetTeachersDialog;

    UniqueRandomGenerator generator = new UniqueRandomGenerator();

    public AdapterDriversNearBy(List<OnlineDriverModel> onlineDriverModelList, Activity activity) {
        this.onlineDriverModelList = onlineDriverModelList;
        this.activity = activity;
        reference = FirebaseDatabase.getInstance().getReference();
        bottomSheetTeachersDialog = new BottomSheetDialog(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ONLINE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_profile_driver_above_online, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_profile_driver_above_offline, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        OnlineDriverModel dr = onlineDriverModelList.get(position);
        return dr.getOnlineStatus() == OnlineStatus.Online ? ONLINE : OFFLINE;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OnlineDriverModel model = onlineDriverModelList.get(position);
        holder.name.setText(model.getUserAccountModel().getFirstname().length() > 7 ? model.getUserAccountModel().getFirstname().substring(0, 7).trim() +"..": model.getUserAccountModel().getFirstname());
        int number = 0;
        if(model.getUserAccountModel() == null) {
            number = generator.generateUniqueRandom();
            if (number == 1) {
                Picasso.get().load(R.drawable.person1).into(holder.profilePicture);
            } else if (number == 2) {
                Picasso.get().load(R.drawable.person2).into(holder.profilePicture);
            } else if (number == 3) {
                Picasso.get().load(R.drawable.person3).into(holder.profilePicture);
            } else {
                Picasso.get().load(R.drawable.person4).into(holder.profilePicture);
            }
        } else {
            if (!TextUtils.isEmpty(model.getUserAccountModel().getProfilePicture())) {
                number = 0;
                Picasso.get().load(model.getUserAccountModel().getProfilePicture()).into(holder.profilePicture);
            } else {
                if (number == 1) {
                    Picasso.get().load(R.drawable.person1).into(holder.profilePicture);
                } else if (number == 2) {
                    Picasso.get().load(R.drawable.person2).into(holder.profilePicture);
                } else if (number == 3) {
                    Picasso.get().load(R.drawable.person3).into(holder.profilePicture);
                } else {
                    Picasso.get().load(R.drawable.person4).into(holder.profilePicture);
                }
            }
        }
        int finalNumber = number;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowInformation(model.getUserAccountModel(), finalNumber);
            }
        });

    }

    private void makePhoneCall(String phoneNumber) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CALL_PHONE}, 1993);
        } else {
            String dial = "tel:" + phoneNumber;
            activity.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

    private void sendSms(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
        intent.putExtra("sms_body", "");

        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "SMS app not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void ShowInformation(UserAccountModel user, int number) {
        bottomSheetTeachersDialog.cancel();
        View layout = LayoutInflater.from(activity).inflate(R.layout.bottom_nav_call_driver, null);
        bottomSheetTeachersDialog.setContentView(layout);
        bottomSheetTeachersDialog.setCancelable(false);
        bottomSheetTeachersDialog.setCanceledOnTouchOutside(true);
        bottomSheetTeachersDialog.show();

        TextView fullName = layout.findViewById(R.id.fullName);
        TextView address = layout.findViewById(R.id.address);
        TextView phoneNumber = layout.findViewById(R.id.phoneNumber);
        TextView email = layout.findViewById(R.id.email);

        ImageView message = layout.findViewById(R.id.message);
        ImageView call = layout.findViewById(R.id.call);
        CircleImageView pp = layout.findViewById(R.id.profilePicture);

        if (number == 0) {
            Picasso.get().load(user.getProfilePicture()).into(pp);
        } else if (number == 1) {
            Picasso.get().load(R.drawable.person1).into(pp);
        } else if (number == 2) {
            Picasso.get().load(R.drawable.person2).into(pp);
        } else if (number == 3) {
            Picasso.get().load(R.drawable.person3).into(pp);
        } else {
            Picasso.get().load(R.drawable.person4).into(pp);
        }
        call.setOnClickListener(view -> makePhoneCall(user.getPhoneNumber()));
        message.setOnClickListener(view -> sendSms(user.getPhoneNumber()));
        fullName.setText(user.getFullName());
        address.setText(TextUtils.isEmpty(user.getAddress()) ? "Address not found" : user.getAddress());
        phoneNumber.setText(TextUtils.isEmpty(user.getPhoneNumber()) ? "Phone number not found" : user.getPhoneNumber());
        email.setText(user.getEmail());

    }

    @Override
    public int getItemCount() {
        return onlineDriverModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profilePicture;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.profilePicture);
            name = itemView.findViewById(R.id.firstname);
        }
    }
}
