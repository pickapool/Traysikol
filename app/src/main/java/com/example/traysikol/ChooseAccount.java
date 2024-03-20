package com.example.traysikol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.traysikol.Enums.AccountType;

public class ChooseAccount extends AppCompatActivity {
    LinearLayout d, c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_account);
        d = findViewById(R.id.chooseDriver);
        c = findViewById(R.id.chooseCommuter);

        d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalClass.AccountType = AccountType.Driver;
                Intent ii = new Intent(ChooseAccount.this, Login.class);
                startActivity(ii);
                finish();
            }
        });
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalClass.AccountType = AccountType.Commuter;
                Intent ii = new Intent(ChooseAccount.this, Login.class);
                startActivity(ii);
                finish();
            }
        });
    }
}