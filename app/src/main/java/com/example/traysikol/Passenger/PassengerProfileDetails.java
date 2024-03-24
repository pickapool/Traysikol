package com.example.traysikol.Passenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.traysikol.BuildConfig;
import com.example.traysikol.Drivers.DriversHome;
import com.example.traysikol.Enums.AccountType;
import com.example.traysikol.Extensions;
import com.example.traysikol.GlobalClass;
import com.example.traysikol.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class PassengerProfileDetails extends AppCompatActivity {
    ImageView back, pp;
    TextView name;
    EditText address ,mNumber , dob, email;
    TextInputEditText fName = null;
    TextInputEditText lastname = null;;
    TextInputEditText others = null;;
    TextView head = null;;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_profile_details);

        reference = FirebaseDatabase.getInstance().getReference();

        back = findViewById(R.id.back);
        address = findViewById(R.id.address);
        mNumber = findViewById(R.id.phoneNumber);
        dob = findViewById(R.id.dateOfBirth);
        name = findViewById(R.id.fullName);
        pp = findViewById(R.id.profilePicture);
        email = findViewById(R.id.email);

        email.setText(GlobalClass.UserAccount.getEmail());

        if(!TextUtils.isEmpty(GlobalClass.UserAccount.getProfilePicture()))
            Picasso.get().load(GlobalClass.UserAccount.getProfilePicture()).into(pp);
        name.setText(GlobalClass.UserAccount.getFullName());
        address.setText(GlobalClass.UserAccount.getAddress());
        mNumber.setText(GlobalClass.UserAccount.getPhoneNumber());
        dob.setText(GlobalClass.UserAccount.getDateofBirth());

        pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Extensions.ChooseActions(PassengerProfileDetails.this);
            }
        });

        address.setOnClickListener(view -> EditDialog(false,"Address",GlobalClass.UserAccount.getAddress(),"address").show());
        mNumber.setOnClickListener(view -> EditDialog(false,"Mobile Number",GlobalClass.UserAccount.getPhoneNumber(),"phoneNumber").show());
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDateOfBirth(view);
            }
        });
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditDialog(true,"","","").show();
            }
        });

        back.setOnClickListener(view -> {
            Intent ii;
            if(GlobalClass.UserAccount.getAccountType() == AccountType.Commuter) {
                ii = new Intent(PassengerProfileDetails.this, PassengerProfile.class);
            } else {
                ii = new Intent(PassengerProfileDetails.this, DriversHome.class);
            }
            startActivity(ii);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri resultUri = result.getUri();
                    //String filePath = resultUri.getPath();
                    //String filename = new File(filePath).getName();
                    Extensions.UploadProfilePicture(PassengerProfileDetails.this, resultUri);
                    Picasso.get().load(resultUri).into(pp);
                } catch (Exception e) {
                    System.out.println("error41" + e.getMessage());
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        } else if(requestCode == 1223 && resultCode == RESULT_OK) {
            try {
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"capture.jpg");
                Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                        BuildConfig.APPLICATION_ID + ".provider", file);
                if(uri != null)
                {
                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(PassengerProfileDetails.this);
                }
            } catch (Exception ee)
            {
                System.out.println("33kkk"+ee.getMessage());
            }

        }
    }
    private AlertDialog EditDialog(boolean IsName, String textViewTitle, String value, String props)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(PassengerProfileDetails.this);
        View customLayout = null;
        if(IsName) {
            customLayout = getLayoutInflater().inflate(R.layout.layout_edit_name, null);
        } else {
            customLayout = getLayoutInflater().inflate(R.layout.layout_edit_others, null);
        }
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setDimAmount(0);
        if(IsName) {
            fName = customLayout.findViewById(R.id.firstname);
            lastname = customLayout.findViewById(R.id.lastname);
            fName.setText(GlobalClass.UserAccount.getFirstname());
            lastname.setText(GlobalClass.UserAccount.getLastname());
        } else {
            head = customLayout.findViewById(R.id.textViewEdit);
            others = customLayout.findViewById(R.id.editOther);
            head.setText(textViewTitle);
            others.setText(value);
        }

        Button close = customLayout.findViewById(R.id.close);
        Button update = customLayout.findViewById(R.id.update);

        close.setOnClickListener(view -> dialog.dismiss());
        update.setOnClickListener(view -> {
            HashMap<String, Object> hashMap = new HashMap<>();
            if(IsName)
            {
                hashMap.put("firstname", fName.getText().toString());
                hashMap.put("lastname", lastname.getText().toString());
            } else {
                hashMap.put(props, others.getText().toString());
            }
            reference
                    .child("Accounts")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(PassengerProfileDetails.this, "Profile has been save", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    if(IsName)
                    {
                        GlobalClass.UserAccount.setFirstname(fName.getText().toString());
                        GlobalClass.UserAccount.setLastname(lastname.getText().toString());
                        name.setText(GlobalClass.UserAccount.getFullName());
                    } else{
                        if(props == "address")
                        {
                            address.setText(others.getText().toString());
                        } else if(props == "phoneNumber")
                        {
                            mNumber.setText(others.getText().toString());
                        }
                    }
                }
            });

        });
        return dialog;
    }
    public void pickDateOfBirth(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Do something with the selected date
                        String selectedDate = (month + 1) + "/" +dayOfMonth + "/" + year;
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("dateofbirth", selectedDate);
                        reference
                                .child("Accounts")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dob.setText(selectedDate);
                                        Toast.makeText(PassengerProfileDetails.this, "Profile has been save", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                },
                year, month, dayOfMonth);

        // Set maximum date (optional)
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        // Show date picker dialog
        datePickerDialog.show();
    }
}