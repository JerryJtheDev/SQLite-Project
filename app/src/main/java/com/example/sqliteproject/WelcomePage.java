package com.example.sqliteproject;


import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomePage extends AppCompatActivity {

    TextView tvUserName, tvWelcome;
    Button btnLogout;
    DatabaseAccessModifier db;
    ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        // Initialize views
        tvUserName = findViewById(R.id.tvUserName);
        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogout = findViewById(R.id.btnCreateAccount);
        backArrow = findViewById(R.id.backArrow);

        db = new DatabaseAccessModifier(this);

        // ✅ Option 1: If you passed user info from Login/Register Activity
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        if (username != null) {
            tvUserName.setText(username);
            tvWelcome.setText("Welcome back!");
        }

        // ✅ Option 2: Fetch latest user from DB (if no intent data is passed)
        else {
            LoginInfo latestUser = db.getLatestUser();
            if (latestUser != null) {
                tvUserName.setText(latestUser.getUsername());
                tvWelcome.setText("Welcome");
            }
        }

        // Logout button click
        btnLogout.setOnClickListener(v -> {
            Intent backToLogin = new Intent(WelcomePage.this, MainActivity.class);
            backToLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(backToLogin);
            finish();
        });

    }
}
