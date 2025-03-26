package com.example.traysikol;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Rating;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.traysikol.Enums.AccountType;
import com.example.traysikol.Enums.CommuteStatus;
import com.example.traysikol.Models.CommuteModel;
import com.example.traysikol.Models.UserAccountModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CurrentRequest extends DialogFragment {

    DatabaseReference reference;
    CommuteModel commuteModel;

    public CurrentRequest(CommuteModel model)
    {
        this.commuteModel = model;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.FullScreenDialogTheme);

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_drivers_current_request, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Initialize all here

        reference = FirebaseDatabase.getInstance().getReference();

        TextView driverName = view.findViewById(R.id.driverFullName);
        CircleImageView driverPP = view.findViewById(R.id.driverProfilePicture);

        TextView pName = view.findViewById(R.id.passengerFullName);
        CircleImageView pPP = view.findViewById(R.id.passengerProfilePicture);

        TextView address1 = view.findViewById(R.id.address1);
        TextView address2 = view.findViewById(R.id.address2);
        TextView fare = view.findViewById(R.id.fare);
        TextView distance = view.findViewById(R.id.distance);
        TextView time = view.findViewById(R.id.duration);

        ImageView back = view.findViewById(R.id.back);
        Button cancel = view.findViewById(R.id.cancel);
        Button endTrip = view.findViewById(R.id.endTrip);
        reference.child("Accounts").child(commuteModel.getDriverUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserAccountModel driverModel = snapshot.getValue(UserAccountModel.class);
                commuteModel.setDriverAccount(driverModel);

                Extensions.SetProfilePicture(driverModel.getProfilePicture(), driverPP);
                driverName.setText(driverModel.getFirstname() == null ? "No Driver" : commuteModel.DriverAccount.getFullName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.child("Accounts").child(commuteModel.getPassengerUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserAccountModel passenger = snapshot.getValue(UserAccountModel.class);
                commuteModel.setPassengerAccount(passenger);
                Extensions.SetProfilePicture(passenger.getProfilePicture(), pPP);
                pName.setText(passenger.getFullName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        address1.setText(commuteModel.getAddress1());
        address2.setText(commuteModel.getAddress2());
        fare.setText(commuteModel.getFare());
        distance.setText(commuteModel.getDistance());
        time.setText(commuteModel.getTime());

        RelativeLayout rel1 = view.findViewById(R.id.rel1);
        RelativeLayout rel2 = view.findViewById(R.id.rel2);

        if(GlobalClass.UserAccount.getAccountType() == AccountType.Commuter)
        {
            rel2.setVisibility(View.INVISIBLE);
            if(!TextUtils.isEmpty(commuteModel.DriverAccount.getUid()))
            {
                endTrip.setVisibility(View.INVISIBLE);
                cancel.setVisibility(View.INVISIBLE);
            }
        } else {
            rel1.setVisibility(View.INVISIBLE);
        }

        ImageView callDriver = view.findViewById(R.id.driverCall);
        ImageView messageDriver = view.findViewById(R.id.driverMessage);

        ImageView callPassenger = view.findViewById(R.id.passengerCall);
        ImageView messagePassenger = view.findViewById(R.id.passengerMessage);

        callDriver.setOnClickListener(view14 -> makePhoneCall(commuteModel.DriverAccount.getPhoneNumber()));
        messageDriver.setOnClickListener(view14 -> sendSms(commuteModel.DriverAccount.getPhoneNumber()));

        callPassenger.setOnClickListener(view14 -> makePhoneCall(commuteModel.PassengerAccount.getPhoneNumber()));
        messagePassenger.setOnClickListener(view14 -> sendSms(commuteModel.PassengerAccount.getPhoneNumber()));

        back.setOnClickListener(view1 -> dismiss());
        cancel.setOnClickListener(view12 -> ConfirmDialog.showDialog(getContext(), "Confirmation", "Are you sure you want to cancel this trip?", new ConfirmDialog.ConfirmDialogListener() {
            @Override
            public void onYesClicked() {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("commuteStatus", CommuteStatus.Cancelled);
                reference.child("Commutes").child(commuteModel.getKey()).updateChildren(hashMap).addOnCompleteListener(task -> {
                    dismiss();
                    Toast.makeText(getActivity(), "Trip has been cancelled.", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onNoClicked() {

            }
        }));

        endTrip.setOnClickListener(view13 -> ConfirmDialog.showDialog(getContext(), "Confirmation", "Are you sure you want to end this trip?", new ConfirmDialog.ConfirmDialogListener() {
            @Override
            public void onYesClicked() {
               /* if(commuteModel.DriverAccount.getFirstname() == null) {*/
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("commuteStatus", CommuteStatus.Done);
                    reference.child("Commutes").child(commuteModel.getKey()).updateChildren(hashMap).addOnCompleteListener(task -> {
                        dismiss();
                        Toast.makeText(getActivity(), "Trip has been ended.", Toast.LENGTH_SHORT).show();
                    });
               /* } else {
                    showCustomDialog(commuteModel);
                }*/
            }

            @Override
            public void onNoClicked() {

            }
        }));
    }
    private void showCustomDialog(CommuteModel model) {
        LayoutInflater inflater = getLayoutInflater();
        android.view.View customView = inflater.inflate(R.layout.template_history, null);

        TextView date = customView.findViewById(R.id.time_text);
        TextView address1 = customView.findViewById(R.id.address1);
        TextView address2 = customView.findViewById(R.id.address2);
        RatingBar rate = customView.findViewById(R.id.ratingBar);

        SimpleDateFormat formats = new SimpleDateFormat("MMMM dd, yyyy hh:mm a");
        date.setText(formats.format(model.getCommuteDate()));
        address1.setText(model.getAddress1());
        address2.setText(model.getAddress2());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(customView);

        builder.setTitle("Driver Rating")
                .setCancelable(true)
                .setPositiveButton("DONE", (dialog, which) -> {
                    SetRating(model, rate.getRating());
                });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }
    private void SetRating(CommuteModel model, float v) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("rating", v);
        hashMap.put("commuteStatus", CommuteStatus.Done);
        reference.child("Commutes").child(model.getKey()).updateChildren(hashMap).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Toast.makeText(getActivity(), "Thank you for your rating.", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
    private void makePhoneCall(String phoneNumber) {
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CALL_PHONE}, 1993);
        } else {
            String dial = "tel:" + phoneNumber;
            getActivity().startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

    private void sendSms(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
        intent.putExtra("sms_body", "");

        try {
            getActivity().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "SMS app not found", Toast.LENGTH_SHORT).show();
        }
    }
}