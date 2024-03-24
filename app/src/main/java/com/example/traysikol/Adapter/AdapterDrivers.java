package com.example.traysikol.Adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traysikol.Enums.OnlineStatus;
import com.example.traysikol.Models.OnlineDriverModel;
import com.example.traysikol.Models.UserAccountModel;
import com.example.traysikol.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterDrivers extends RecyclerView.Adapter<AdapterDrivers.ViewHolder>{
    private static final int OFFLINE = 1;
    private static final int ONLINE = 2;
    DatabaseReference reference;
    List<OnlineDriverModel> onlineDriverModelList;
    Activity activity;

    UniqueRandomGenerator generator = new UniqueRandomGenerator();

    public AdapterDrivers(List<OnlineDriverModel> onlineDriverModelList, Activity activity) {
        this.onlineDriverModelList = onlineDriverModelList;
        this.activity = activity;
        reference = FirebaseDatabase.getInstance().getReference();
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
        return  dr.getOnlineStatus() == OnlineStatus.Online ? ONLINE : OFFLINE;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDrivers.ViewHolder holder, int position) {
            OnlineDriverModel model = onlineDriverModelList.get(position);
            holder.driverName.setText(model.getDriverName());
            reference.child("Accounts").child(model.getDriverUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserAccountModel user = snapshot.getValue(UserAccountModel.class);
                    model.setUserAccountModel(user);
                    if(!TextUtils.isEmpty(user.getProfilePicture())) {
                        Picasso.get().load(user.getProfilePicture()).into(holder.profilePicture);
                    } else {
                        int number = generator.generateUniqueRandom();
                        if(number == 1)
                        {
                            Picasso.get().load(R.drawable.person1).into(holder.profilePicture);
                        } else if (number == 2) {
                            Picasso.get().load(R.drawable.person2).into(holder.profilePicture);
                        } else if(number == 3) {
                            Picasso.get().load(R.drawable.person3).into(holder.profilePicture);
                        } else {
                            Picasso.get().load(R.drawable.person4).into(holder.profilePicture);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
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
