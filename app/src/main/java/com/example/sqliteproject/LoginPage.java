package com.example.sqliteproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class LoginPage extends AppCompatActivity {

    EditText etUsername, etEmail, etPassword;
    Button btnLogin;
    TextView signInLink;
    ImageButton backButton;

    DatabaseAccessModifier databaseAccessModifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        etUsername = findViewById(R.id.nameInput);
        etEmail = findViewById(R.id.emailInput);
        etPassword = findViewById(R.id.passwordInput);
        btnLogin = findViewById(R.id.signUpButton); // login button
        signInLink = findViewById(R.id.signInLink);
        backButton = findViewById(R.id.backButton);

        databaseAccessModifier = new DatabaseAccessModifier(this);

        // Handle login
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginPage.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ Check database
            boolean userExists = databaseAccessModifier.checkUser(username, email, password);

            if (userExists) {
                Toast.makeText(LoginPage.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                // Go to WelcomePage with username
                Intent intent = new Intent(LoginPage.this, WelcomePage.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginPage.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        signInLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, MainActivity.class);
            startActivity(intent);
        });

        // Navigate back
    }
}
