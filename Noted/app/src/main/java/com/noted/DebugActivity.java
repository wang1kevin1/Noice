package com.noted;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DebugActivity extends AppCompatActivity {

    // Menu buttons
    private Button dButtonCreateAccount;
    private Button dButtonLogin;
    private Button dButtonDashboard;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        // Initialize debug buttons
        dButtonCreateAccount = findViewById(R.id.debugButtonCreateAccount);
        dButtonLogin = findViewById(R.id.debugButtonLogin);
        dButtonDashboard = findViewById(R.id.debugButtonDashboard);

        dButtonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent =
                        new Intent(DebugActivity.this, CreateAccountActivity.class);
                startActivity(myIntent);
            }
        });

        dButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent =
                        new Intent(DebugActivity.this, LoginActivity.class);
                startActivity(myIntent);
            }
        });

        dButtonDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent =
                        new Intent(DebugActivity.this, DashboardActivity.class);
                startActivity(myIntent);
            }
        });
    }
}
