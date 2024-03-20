package com.example.traysikol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.traysikol.Passenger.PassengerHomeScreen;
import com.example.traysikol.Passenger.PassengerRegister;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class Login extends AppCompatActivity {
    TextView signUp;
    Button login;
    ImageView back;
    BottomSheetDialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signUp = findViewById(R.id.signup);
        login = findViewById(R.id.loginButton);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii = new Intent(Login.this, ChooseAccount.class);
                startActivity(ii);
                finish();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PassengerRegister dialog = new PassengerRegister();
                dialog.show(getSupportFragmentManager(), dialog.getTag());
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii = new Intent(Login.this , PassengerHomeScreen.class);
                startActivity(ii);
            }
        });

    }
}