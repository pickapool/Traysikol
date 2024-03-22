package com.example.traysikol.Passenger;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.traysikol.BuildConfig;
import com.example.traysikol.Extensions;
import com.example.traysikol.GlobalClass;
import com.example.traysikol.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.Objects;

public class PassengerProfileDetails extends AppCompatActivity {
    ImageView back, pp;
    TextView name;
    EditText address ,mNumber , dob;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_profile_details);

        back = findViewById(R.id.back);
        address = findViewById(R.id.address);
        mNumber = findViewById(R.id.phoneNumber);
        dob = findViewById(R.id.dateOfBirth);
        name = findViewById(R.id.fullName);
        pp = findViewById(R.id.profilePicture);

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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii = new Intent(PassengerProfileDetails.this, PassengerProfile.class);
                startActivity(ii);
                finish();
            }
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
}