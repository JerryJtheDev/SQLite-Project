package com.example.sqliteproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class LoginPage extends AppCompatActivity {

    EditText etUsername, etEmail, etPassword;
    Button btnLogin;
    TextView signInLink, passwordStrength;
    ImageButton backButton;
    ImageView passwordToggle;
    boolean isPasswordVisible = false;

    DatabaseAccessModifier databaseAccessModifier;

    // ✅ Regex Patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z ]{3,30}$");
    private static final Pattern EMAIL_PATTERN = Patterns.EMAIL_ADDRESS;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +
                    "(?=.*[a-z])" +
                    "(?=.*[A-Z])" +
                    "(?=.*[@#$%^&+=!])" +
                    "(?=\\S+$)" +
                    ".{8,}" +
                    "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        etUsername = findViewById(R.id.nameInput);
        etEmail = findViewById(R.id.emailInput);
        etPassword = findViewById(R.id.passwordInput);
        btnLogin = findViewById(R.id.signUpButton);
        signInLink = findViewById(R.id.signInLink);
        backButton = findViewById(R.id.backButton);
        passwordToggle = findViewById(R.id.passwordToggle);

        // 👁 Password Strength Indicator
        passwordStrength = new TextView(this);
        ((android.widget.RelativeLayout) findViewById(R.id.passwordContainer))
                .addView(passwordStrength);
        passwordStrength.setTextSize(12);
        passwordStrength.setPadding(16, 55, 0, 0);

        databaseAccessModifier = new DatabaseAccessModifier(this);

        // 👁 Toggle Password Visibility
        passwordToggle.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_eye_closed);
                isPasswordVisible = false;
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_eye_open);
                isPasswordVisible = true;
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        // 🟢 Live Password Strength Checker
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePasswordStrength(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Handle Login
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (!NAME_PATTERN.matcher(username).matches()) {
                Toast.makeText(this, "Invalid name format!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email format!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!PASSWORD_PATTERN.matcher(password).matches()) {
                Toast.makeText(this, "Password must have 8+ chars, upper, lower, number & special char!", Toast.LENGTH_LONG).show();
                return;
            }

            if (!validateName(username) | !validateEmail(email) | !validatePassword(password)) {
                return;
            }

            // ✅ Check if user exists in database
            boolean userExists = databaseAccessModifier.checkUser(username, email, password);

            if (userExists) {
                // ✅ Get the user's ID
                int userId = databaseAccessModifier.getUserId(username, email, password);

                if (userId != -1) {
                    Toast.makeText(LoginPage.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                    // ✅ Pass username + ID to WelcomePage
                    Intent intent = new Intent(LoginPage.this, WelcomePage.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginPage.this, "Error fetching user ID", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginPage.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });


        signInLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, MainActivity.class);
            startActivity(intent);
        });

        backButton.setOnClickListener(view -> {
            Intent backintent = new Intent(LoginPage.this, MainActivity.class);
            startActivity(backintent);
        });
    }

    // ✅ Password Strength Indicator
    private void updatePasswordStrength(String passwordText) {
        if (passwordText.isEmpty()) {
            passwordStrength.setText("");
            return;
        }

        if (passwordText.length() < 6) {
            passwordStrength.setText("Weak Password 😟");
            passwordStrength.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        } else if (passwordText.length() < 8) {
            passwordStrength.setText("Medium Strength 🙂");
            passwordStrength.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark));
        }else if (!PASSWORD_PATTERN.matcher(passwordText).matches()) {
            passwordStrength.setText("Medium Strength 🙂");
            passwordStrength.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark));
        } else {
            passwordStrength.setText("Strong Password 💪");
            passwordStrength.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        }
    }

    // ✅ Name Validation
    private boolean validateName(String name) {
        if (name.isEmpty()) {
            etUsername.setError("Name is required");
            etUsername.requestFocus();
            return false;
        } else if (!NAME_PATTERN.matcher(name).matches()) {
            etUsername.setError("Only letters & spaces, 3-30 chars");
            etUsername.requestFocus();
            return false;
        }
        etUsername.setError(null);
        return true;
    }

    // ✅ Email Validation
    private boolean validateEmail(String emailInput) {
        if (emailInput.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        } else if (!EMAIL_PATTERN.matcher(emailInput).matches()) {
            etEmail.setError("Invalid email address");
            etEmail.requestFocus();
            return false;
        }
        etEmail.setError(null);
        return true;
    }

    // ✅ Password Validation
    private boolean validatePassword(String pass) {
        if (pass.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        } else if (!PASSWORD_PATTERN.matcher(pass).matches()) {
            etPassword.setError("Must be 8+ chars, upper, lower, digit & special char");
            etPassword.requestFocus();
            return false;
        }
        etPassword.setError(null);
        return true;
    }
}
