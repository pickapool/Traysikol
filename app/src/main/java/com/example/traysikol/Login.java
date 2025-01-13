package com.example.traysikol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.traysikol.Drivers.DriversHome;
import com.example.traysikol.Enums.AccountType;
import com.example.traysikol.Models.UserAccountModel;
import com.example.traysikol.Passenger.PassengerHomeScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    TextView signUp, forgotPassword;
    Button login;
    ImageView back;
    BottomSheetDialogFragment dialogFragment;
    TextInputEditText username, password;
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signUp = findViewById(R.id.signup);
        login = findViewById(R.id.loginButton);
        back = findViewById(R.id.back);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        forgotPassword = findViewById(R.id.forgotPassword);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setTitle("Forgot Password");

                final EditText input = new EditText(Login.this);
                input.setHint("Type your email");
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String userInput = input.getText().toString();
                                if(TextUtils.isEmpty(userInput)) {
                                    Toast.makeText(Login.this, "Please enter an email!" + userInput, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                FirebaseAuth.getInstance().sendPasswordResetEmail(userInput)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(Login.this, "Please check your email for password reset.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Handle cancel button click
                            }
                        });
                builder.show();
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

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
                Register dialog = new Register();
                dialog.show(getSupportFragmentManager(), dialog.getTag());
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(username.getText()) || TextUtils.isEmpty(password.getText())) {
                    Toast.makeText(Login.this, "All fields are required.", Toast.LENGTH_SHORT).show();
                    return;
                }
                ProgressDialog dialog = new ProgressDialog(Login.this);
                dialog.setCancelable(false);
                dialog.setTitle("Signing in");
                dialog.setMessage("Loading...");
                dialog.show();
                reference.child("Accounts").orderByChild("username").equalTo(username.getText().toString())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                        UserAccountModel acc = snapshot1.getValue(UserAccountModel.class);
                                        if(acc.getAccountType() == AccountType.Driver) {
                                            if(!acc.getIsApproved()) {
                                                Toast.makeText(Login.this, "Your account was still on pending.", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                return;
                                            }
                                        }
                                        if (acc.getAccountType() != GlobalClass.AccountType) {
                                            dialog.dismiss();
                                            Toast.makeText(Login.this, "Sorry we can't find your account as a " + acc.getAccountType().toString(), Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        firebaseAuth.signInWithEmailAndPassword(acc.getEmail(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    GlobalClass.UserAccount = acc;
                                                    Intent ii;
                                                    if (acc.accountType == AccountType.Commuter) {
                                                        //Passenger home
                                                        ii = new Intent(Login.this, PassengerHomeScreen.class);
                                                    } else {
                                                        //Driver home
                                                        ii = new Intent(Login.this, DriversHome.class);
                                                    }
                                                    startActivity(ii);
                                                } else {
                                                    dialog.dismiss();
                                                    Toast.makeText(Login.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(Login.this, "Sorry we can't find your account.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });

    }
}