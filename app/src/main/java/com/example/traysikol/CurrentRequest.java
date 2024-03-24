package com.example.traysikol;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.traysikol.Enums.CommuteStatus;
import com.example.traysikol.Models.CommuteModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        Extensions.SetProfilePicture(commuteModel.DriverAccount.getProfilePicture(), driverPP);
        Extensions.SetProfilePicture(commuteModel.PassengerAccount.getProfilePicture(), pPP);

        driverName.setText(commuteModel.DriverAccount.getFullName());
        pName.setText(commuteModel.PassengerAccount.getFullName());
        address1.setText(commuteModel.getAddress1());
        address2.setText(commuteModel.getAddress2());
        fare.setText(commuteModel.getFare());
        distance.setText(commuteModel.getDistance());
        time.setText(commuteModel.getTime());

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
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("commuteStatus", CommuteStatus.Done);
                reference.child("Commutes").child(commuteModel.getKey()).updateChildren(hashMap).addOnCompleteListener(task -> {
                    dismiss();
                    Toast.makeText(getActivity(), "Trip has been cancelled.", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onNoClicked() {

            }
        }));

    }
}