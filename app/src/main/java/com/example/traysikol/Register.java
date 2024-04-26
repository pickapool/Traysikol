package com.example.traysikol;

import android.app.ProgressDialog;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.traysikol.Models.UserAccountModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        terms.setOnClickListener(view13 -> ShowTerms("This page is used to inform visitors regarding my policies with the collection, use, and disclosure of Personal Information if anyone decided to use my Service. " +
                "If you choose to use my Service, then you agree to the collection and use of information in relation to this policy. The Personal Information that I collect is used for providing and improving the Service. I will not use or share your information with anyone except as described in this Privacy Policy."));
        privacy.setOnClickListener(view13 -> ShowTerms("This page is used to inform visitors regarding my policies with the collection, use, and disclosure of Personal Information if anyone decided to use my Service. " +
                "If you choose to use my Service, then you agree to the collection and use of information in relation to this policy. The Personal Information that I collect is used for providing and improving the Service. I will not use or share your information with anyone except as described in this Privacy Policy."));
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

    private void ShowTerms(String terms) {
        AlertDialog innerDialog = new AlertDialog.Builder(getContext())
                .setTitle("Inner Dialog")
                .setMessage("This is an inner dialog inside the outer dialog.")
                .setPositiveButton("OK", (innerDialogInterface, i) -> {
                    // Handle inner dialog button click if needed
                })
                .create();
        innerDialog.show();
    }

}
