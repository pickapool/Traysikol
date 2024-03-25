package com.example.traysikol.Adapter;

import android.Manifest;
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
import com.example.traysikol.Models.OnlineDriverModel;
import com.example.traysikol.Models.UserAccountModel;
import com.example.traysikol.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterDrivers extends RecyclerView.Adapter<AdapterDrivers.ViewHolder> {
    private static final int OFFLINE = 1;
    private static final int ONLINE = 2;
    DatabaseReference reference;
    List<OnlineDriverModel> onlineDriverModelList;
    Activity activity;
    BottomSheetDialog bottomSheetTeachersDialog;

    UniqueRandomGenerator generator = new UniqueRandomGenerator();

    public AdapterDrivers(List<OnlineDriverModel> onlineDriverModelList, Activity activity) {
        this.onlineDriverModelList = onlineDriverModelList;
        this.activity = activity;
        reference = FirebaseDatabase.getInstance().getReference();
        bottomSheetTeachersDialog = new BottomSheetDialog(activity);
    }

    @NonNull
    @Override
    public AdapterDrivers.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ONLINE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_profile_driver_online, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_profile_driver_offline, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        OnlineDriverModel dr = onlineDriverModelList.get(position);
        return dr.getOnlineStatus() == OnlineStatus.Online ? ONLINE : OFFLINE;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDrivers.ViewHolder holder, int position) {
        OnlineDriverModel model = onlineDriverModelList.get(position);
        holder.driverName.setText(model.getDriverName());
        int number;
        if (!TextUtils.isEmpty(model.getUserAccountModel().getProfilePicture())) {
            number = 0;
            Picasso.get().load(model.getUserAccountModel().getProfilePicture()).into(holder.profilePicture);
        } else {
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
        }
        holder.itemView.setOnClickListener(view -> ShowInformation(model.getUserAccountModel(), number));
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
        TextView driverName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.profilePicture);
            driverName = itemView.findViewById(R.id.driverName);
        }
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
}
