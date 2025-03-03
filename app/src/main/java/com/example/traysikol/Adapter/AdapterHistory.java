package com.example.traysikol.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traysikol.CurrentRequest;
import com.example.traysikol.Extensions;
import com.example.traysikol.GlobalClass;
import com.example.traysikol.Models.CommuteModel;
import com.example.traysikol.Models.UserAccountModel;
import com.example.traysikol.Passenger.PassengerHistory;
import com.example.traysikol.Passenger.PassengerHomeScreen;
import com.example.traysikol.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.ViewHolder>{

    List<CommuteModel> commuteModelList;
    Activity activity;
    DatabaseReference reference;
    AdapterHistory.UniqueRandomGenerator generator = new AdapterHistory.UniqueRandomGenerator();

    public AdapterHistory(List<CommuteModel> commuteModelList, Activity activity) {
        this.commuteModelList = commuteModelList;
        this.activity = activity;
        reference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public AdapterHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.template_history_rate , null);
        return new AdapterHistory.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHistory.ViewHolder holder, int position) {
        CommuteModel model = commuteModelList.get(position);

        SimpleDateFormat formats = new SimpleDateFormat("MMMM dd, yyyy hh:mm a");
        reference.child("Accounts").child(model.getDriverUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserAccountModel driverModel = snapshot.getValue(UserAccountModel.class);
                model.setDriverAccount(driverModel);
                holder.ratingBar.setRating(model.getRating());
                holder.driverName.setText(model.getDriverAccount().getFullName());
                int number = 0;
                number = generator.generateUniqueRandom();

                if (!TextUtils.isEmpty(model.getDriverAccount().getProfilePicture())) {
                    Picasso.get().load(model.getDriverAccount().getProfilePicture()).into(holder.profile);
                } else {
                    if (number == 1) {
                        Picasso.get().load(R.drawable.person1).into(holder.profile);
                    } else if (number == 2) {
                        Picasso.get().load(R.drawable.person2).into(holder.profile);
                    } else if (number == 3) {
                        Picasso.get().load(R.drawable.person3).into(holder.profile);
                    } else {
                        Picasso.get().load(R.drawable.person4).into(holder.profile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.time.setText(formats.format(model.getCommuteDate()));
        holder.address1.setText(model.getAddress1());
        holder.address2.setText(model.getAddress2());

        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if(b) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("rating", v);
                    hashMap.put("isRated", true);

                    reference.child("Commutes").child(model.getKey()).updateChildren(hashMap);
                    Extensions.CreateNotification(activity, PassengerHistory.class, "Thank you for your rating.");
                }
            }
        });
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View dialogView = inflater.inflate(R.layout.activity_drivers_current_request, null);

                    TextView driverName = dialogView.findViewById(R.id.driverFullName);
                    CircleImageView driverPP = dialogView.findViewById(R.id.driverProfilePicture);
                    TextView pName = dialogView.findViewById(R.id.passengerFullName);
                    CircleImageView pPP = dialogView.findViewById(R.id.passengerProfilePicture);
                    TextView address1 = dialogView.findViewById(R.id.address1);
                    TextView address2 = dialogView.findViewById(R.id.address2);
                    TextView fare = dialogView.findViewById(R.id.fare);
                    TextView distance = dialogView.findViewById(R.id.distance);
                    TextView time = dialogView.findViewById(R.id.duration);

                    Button cancel = dialogView.findViewById(R.id.cancel);
                    Button endTrip = dialogView.findViewById(R.id.endTrip);


                    cancel.setVisibility(View.INVISIBLE);
                    endTrip.setVisibility(View.INVISIBLE);

                    ImageView back = dialogView.findViewById(R.id.back);
                    Extensions.SetProfilePicture(model.DriverAccount.getProfilePicture(), driverPP);
                    Extensions.SetProfilePicture(GlobalClass.UserAccount.getProfilePicture(), pPP);

                    driverName.setText(model.DriverAccount.getFullName());
                    pName.setText(GlobalClass.UserAccount.getFullName());
                    address1.setText(model.getAddress1());
                    address2.setText(model.getAddress2());
                    fare.setText(model.getFare());
                    distance.setText(model.getDistance());
                    time.setText(model.getTime());

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setView(dialogView);
                    builder.setCancelable(false);





                    AlertDialog dialog = builder.create();

                    WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
                    lWindowParams.copyFrom(dialog.getWindow().getAttributes());
                    lWindowParams.width = 700;
                    lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

                    dialog.show(); // Show the dialog
                    dialog.getWindow().setAttributes(lWindowParams);
                    back.setOnClickListener(view1 -> dialog.dismiss());
                } catch (Exception ee) {
                    System.out.println("sad22" +ee.getMessage());
                }
            }
        });
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

    @Override
    public int getItemCount() {
        return commuteModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView time, address1, address2, driverName;
        RatingBar ratingBar;
        CircleImageView profile;
        ImageView view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.time_text);
            address1 = itemView.findViewById(R.id.address1);
            address2 = itemView.findViewById(R.id.address2);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            driverName = itemView.findViewById(R.id.driverName);
            profile = itemView.findViewById(R.id.profilePicture);
            view = itemView.findViewById(R.id.iconView);
        }
    }
}
