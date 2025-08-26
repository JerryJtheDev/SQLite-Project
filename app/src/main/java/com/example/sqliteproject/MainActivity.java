package com.example.sqliteproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    EditText username, password, email;
    Button signup_btn;
    LoginInfo loginInfo;
    DatabaseAccessModifier databaseAccessModifier;
    ArrayAdapter arrayAdapter;
    TextView signInLink, passwordStrength;

    ImageView passwordToggle;
    boolean isPasswordVisible = false;

    // ✅ Regex Patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z ]{3,30}$");
    private static final Pattern EMAIL_PATTERN = Patterns.EMAIL_ADDRESS;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +           // At least one digit
                    "(?=.*[a-z])" +           // At least one lowercase
                    "(?=.*[A-Z])" +           // At least one uppercase
                    "(?=.*[@#$%^&+=!])" +     // At least one special character
                    "(?=\\S+$)" +             // No whitespaces
                    ".{8,}" +                 // Minimum 8 characters
                    "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.nameInput);
        password = findViewById(R.id.passwordInput);
        email = findViewById(R.id.emailInput);
        signup_btn = findViewById(R.id.signUpButton);
        signInLink = findViewById(R.id.signInLink);
        passwordToggle = findViewById(R.id.passwordToggle);

        // 👁 Add password strength indicator dynamically
        passwordStrength = new TextView(this);
        ((android.widget.RelativeLayout) findViewById(R.id.passwordContainer))
                .addView(passwordStrength);
        passwordStrength.setTextSize(12);
        passwordStrength.setPadding(16, 55, 0, 0);

        databaseAccessModifier = new DatabaseAccessModifier(this);

        // 👁 Toggle Password Visibility
        passwordToggle.setOnClickListener(v -> {
            if (isPasswordVisible) {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_eye_closed);
                isPasswordVisible = false;
            } else {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_eye_open);
                isPasswordVisible = true;
            }
            password.setSelection(password.getText().length());
        });

        // 🟢 Live Password Strength Checker
        password.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePasswordStrength(s.toString());
            }
        });

        // 🔗 Go to Login Page
        signInLink.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginPage.class);
            startActivity(intent);
        });

        // 🔐 Signup Button Click
        signup_btn.setOnClickListener(view -> {
            String user = username.getText().toString().trim();
            String e_mail = email.getText().toString().trim();
            String pass = password.getText().toString().trim();

            // ✅ Validate Inputs
            if (!validateName(user) | !validateEmail(e_mail) | !validatePassword(pass)) {
                return;
            }

            try {
                loginInfo = new LoginInfo(-1, user, e_mail, pass);
                boolean success = databaseAccessModifier.addOne(loginInfo);

                if (success) {
                    Toast.makeText(MainActivity.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();

                    // Fetch the newly created user to obtain its ID
                    LoginInfo savedUser = databaseAccessModifier.getUserByEmailAndPassword(e_mail, pass);
                    Intent intent = new Intent(MainActivity.this, WelcomePage.class);
                    intent.putExtra("username", user);

                    if (savedUser != null) {
                        // Pass the user ID (preferred)
                        intent.putExtra("USER_ID", savedUser.getId());
                    } // if savedUser is null we still pass username for backward compatibility

                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to create account", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                Toast.makeText(this, "Unexpected error occurred", Toast.LENGTH_SHORT).show();
            }
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
        } else if (!PASSWORD_PATTERN.matcher(passwordText).matches()) {
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
            username.setError("Name is required");
            username.requestFocus();
            return false;
        } else if (!NAME_PATTERN.matcher(name).matches()) {
            username.setError("Only letters & spaces, 3-30 chars");
            username.requestFocus();
            return false;
        }
        username.setError(null);
        return true;
    }

    // ✅ Email Validation
    private boolean validateEmail(String emailInput) {
        if (emailInput.isEmpty()) {
            email.setError("Email is required");
            email.requestFocus();
            return false;
        } else if (!EMAIL_PATTERN.matcher(emailInput).matches()) {
            email.setError("Invalid email address");
            email.requestFocus();
            return false;
        }
        email.setError(null);
        return true;
    }

    // ✅ Password Validation
    private boolean validatePassword(String pass) {
        if (pass.isEmpty()) {
            password.setError("Password is required");
            password.requestFocus();
            return false;
        } else if (!PASSWORD_PATTERN.matcher(pass).matches()) {
            password.setError("Must be 8+ chars, upper, lower, digit & special char");
            password.requestFocus();
            return false;
        }
        password.setError(null);
        return true;
    }
}
