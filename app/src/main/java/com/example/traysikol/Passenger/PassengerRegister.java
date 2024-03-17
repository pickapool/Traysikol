package com.example.traysikol.Passenger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.traysikol.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class PassengerRegister extends DialogFragment {


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

        ImageView closeBtn = view.findViewById(R.id.closeButton);
        TextInputEditText firstName = view.findViewById(R.id.firstname);
        TextInputEditText lastName = view.findViewById(R.id.firstname);
        TextInputEditText email = view.findViewById(R.id.firstname);
        TextInputEditText phoneNumber = view.findViewById(R.id.firstname);
        TextInputEditText username = view.findViewById(R.id.firstname);
        TextInputEditText password = view.findViewById(R.id.firstname);
        TextInputEditText confirmPassword = view.findViewById(R.id.firstname);

        TextView terms = view.findViewById(R.id.terms);
        TextView privacy = view.findViewById(R.id.privacy);
        TextView login = view.findViewById(R.id.login);
        CheckBox agree = view.findViewById(R.id.agree);

        Button signUpBtn = view.findViewById(R.id.signupButton);


        login.setOnClickListener(view12 -> dismissWithAnimation());
        closeBtn.setOnClickListener(view1 -> dismissWithAnimation());
        terms.setOnClickListener(view13 -> ShowTerms("Hello"));
        privacy.setOnClickListener(view13 -> ShowTerms("Hello"));
        // Initialize views and set up dialog behavior
        // Apply animation when dialog is shown
        getDialog().getWindow().getDecorView().setPadding(0, 100, 0, 0);
        Animation slideInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_bottom);
        view.startAnimation(slideInAnimation);
    }

    // Dismiss dialog with animation
    private void dismissWithAnimation() {
        Animation slideOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_bottom);
        slideOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        getView().startAnimation(slideOutAnimation);
    }
    private void ShowTerms(String terms){
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
