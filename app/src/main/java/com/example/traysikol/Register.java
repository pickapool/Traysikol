package com.example.traysikol;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.traysikol.Models.UserAccountModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends DialogFragment {
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.FullScreenDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_register_user, container, false);
    }
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        // Minimum 8 characters, at least 1 special character, and at least 1 uppercase letter
        String regex = "^(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=.*[0-9]).{8,}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        ImageView closeBtn = view.findViewById(R.id.closeButton);
        TextInputEditText firstName = view.findViewById(R.id.firstname);
        TextInputEditText lastName = view.findViewById(R.id.lastname);
        TextInputEditText email = view.findViewById(R.id.email);
        TextInputEditText phoneNumber = view.findViewById(R.id.phoneNumber);
        TextInputEditText username = view.findViewById(R.id.username);
        TextInputEditText password = view.findViewById(R.id.password);
        TextInputEditText confirmPassword = view.findViewById(R.id.confirmPassword);

        TextView terms = view.findViewById(R.id.terms);
        TextView privacy = view.findViewById(R.id.privacy);
        TextView login = view.findViewById(R.id.login);
        CheckBox agree = view.findViewById(R.id.agree);

        Button signUpBtn = view.findViewById(R.id.signupButton);


        login.setOnClickListener(view12 -> dismissWithAnimation());
        closeBtn.setOnClickListener(view1 -> dismissWithAnimation());
        terms.setOnClickListener(view13 -> ShowTerms("Here’s a simple and basic Terms of Service for the TricyRide app. You can adjust this template as necessary for your specific needs, keeping in mind legal requirements and the services you offer.\n" +
                "Terms of Service for TricyRide App\n" +
                "\n" +
                "Effective Date: [Insert Date]\n" +
                "\n" +
                "Welcome to TricyRide! By accessing or using the TricyRide mobile app (the \"App\"), you agree to comply with and be bound by the following Terms of Service (\"Terms\"). Please read these Terms carefully before using the App. If you do not agree with any of these Terms, please do not use the App.\n" +
                "1. Acceptance of Terms\n" +
                "\n" +
                "By downloading, installing, or using the TricyRide App, you agree to these Terms. We may update these Terms from time to time, and any changes will be effective immediately upon posting. It is your responsibility to review these Terms periodically.\n" +
                "2. Use of the App\n" +
                "\n" +
                "The TricyRide app is intended for personal, non-commercial use. You agree to use the App in compliance with applicable laws, regulations, and these Terms. You may not use the App for any illegal, unauthorized, or prohibited activities.\n" +
                "3. Account Registration\n" +
                "\n" +
                "To access certain features of the App, you may be required to create an account. You agree to provide accurate, up-to-date information during the registration process and to maintain the security of your account. You are responsible for all activities under your account, whether or not authorized by you.", ""));
        privacy.setOnClickListener(view13 -> ShowTerms("Your trust is important to us, and we are committed to safeguarding your personal information. Our Privacy Policy outlines how we collect, use, and protect your data:  \n" +
                "\n" +
                " 1. Information We Collect\n" +
                "- Personal Information: Name, contact details, and address for account creation.  \n" +
                "- Ride Details: Pickup/drop-off locations and trip history for service improvement.  \n" +
                "\n" +
                "2. How We Use Your Information\n" +
                "- To provide and improve our booking services.  \n" +
                "- To communicate important updates or promotional offers.  \n" +
                "- To ensure safety and accountability during trips.  \n" +
                "\n" +
                "3. Data Security \n" +
                "We implement industry-standard measures to protect your information from unauthorized access, alteration, or disclosure.  \n" +
                "\n" +
                "4. Third-Party Sharing\n" +
                "We do not share your personal data with third parties.\n" +
                "\n" +
                " \n" +
                "By using TricyRide, you agree to the terms outlined in this Privacy Policy. For detailed terms.", "Privacy and Policy"));
        // Initialize views and set up dialog behavior
        // Apply animation when dialog is shown
        getDialog().getWindow().getDecorView().setPadding(0, 100, 0, 0);
        Animation slideInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_bottom);
        view.startAnimation(slideInAnimation);

        signUpBtn.setOnClickListener(view14 -> {
            if (!agree.isChecked()) {
                Toast.makeText(getContext(), "Please check if you are confirming with our terms and policy.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(firstName.getText()) ||
                TextUtils.isEmpty(lastName.getText()) ||
                TextUtils.isEmpty(email.getText()) ||
                TextUtils.isEmpty(phoneNumber.getText()) ||
                TextUtils.isEmpty(username.getText()) ||
                TextUtils.isEmpty(password.getText()) ||
                TextUtils.isEmpty(confirmPassword.getText()))
            {
                Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!isValidPassword(password.getText().toString())){
                Toast.makeText(getContext(), "Password must be 8 characters, at least 1 special character, and at least 1 uppercase letter", Toast.LENGTH_SHORT).show();
                return;
            }
            ProgressDialog dialog = new ProgressDialog(getContext());
            dialog.setCancelable(false);
            dialog.setTitle("Creating account");
            dialog.setMessage("Loading...");
            dialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        UserAccountModel userAccountModel = new UserAccountModel();
                        userAccountModel.setFirstname(firstName.getText().toString());
                        userAccountModel.setLastname(lastName.getText().toString());
                        userAccountModel.setPhoneNumber(phoneNumber.getText().toString());
                        userAccountModel.setEmail(email.getText().toString());
                        userAccountModel.setPassword(password.getText().toString());
                        userAccountModel.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        userAccountModel.setUsername(username.getText().toString());
                        userAccountModel.setAccountType(GlobalClass.AccountType);
                        userAccountModel.setProfilePicture("");
                        userAccountModel.setDateofBirth("");
                        userAccountModel.setAddress("");
                        userAccountModel.setIsApproved(false);
                        reference.child("Accounts").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(userAccountModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            dialog.dismiss();
                                            Toast.makeText(getContext(), "Account has been registered!", Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().signOut();
                                            dismissWithAnimation();
                                        } else {
                                            dialog.dismiss();
                                            Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });


    }

    private boolean isEmpty(TextInputEditText textInputEditText) {
        CharSequence input = textInputEditText.getText();
        return input == null || input.toString().trim().isEmpty();
    }

    // Dismiss dialog with animation
    private void dismissWithAnimation() {
        Animation slideOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_bottom);
        slideOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        getView().startAnimation(slideOutAnimation);
    }

    private void ShowTerms(String terms, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(terms);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
