package com.example.sqliteproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class WelcomePage extends AppCompatActivity {

    TextView tvUserName, tvWelcome;
    Button btnLogout;
    DatabaseAccessModifier db;
    ImageView backArrow, profileImage;

    private LoginInfo loggedInUser;  // ✅ Store the currently logged-in user

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                        // ✅ Convert Bitmap to Byte[]
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();

                        // ✅ Save image for this specific user
                        if (loggedInUser != null) {
                            db.updateUserImage(loggedInUser.getId(), imageBytes);

                            // ✅ Refresh user info after updating image
                            loggedInUser = db.getUserById(loggedInUser.getId());

                            profileImage.setImageBitmap(bitmap);
                            Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "No user found! Please log in again.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        tvUserName = findViewById(R.id.tvUserName);
        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogout = findViewById(R.id.btnCreateAccount);
        backArrow = findViewById(R.id.backArrow);
        profileImage = findViewById(R.id.userAvatar);

        db = new DatabaseAccessModifier(this);

        // ✅ Get user ID from intent
        Intent intent = getIntent();
        int userId = intent.getIntExtra("USER_ID", -1);

        if (userId != -1) {
            loggedInUser = db.getUserById(userId);
            if (loggedInUser != null) {
                tvUserName.setText(loggedInUser.getUsername());
                tvWelcome.setText("Welcome Back");



                // ✅ Load saved profile image if exists
                byte[] imageBytes = loggedInUser.getImage();
                if (imageBytes != null && imageBytes.length > 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    profileImage.setImageBitmap(bitmap);
                }
            } else {
                Toast.makeText(this, "User not found in database!", Toast.LENGTH_SHORT).show();
            }
        } else {
            String username = getIntent().getStringExtra("username");
            tvUserName.setText(username);
            Toast.makeText(this, "Invalid User ID received!", Toast.LENGTH_SHORT).show();
        }

        // ✅ Profile image click → open gallery (handle permissions correctly)
        profileImage.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Intent imageIntent = new Intent(MediaStore.ACTION_PICK_IMAGES);
                imagePickerLauncher.launch(imageIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery();
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
            } else {
                pickImageFromGallery();
            }
        });

        // ✅ Logout button
        btnLogout.setOnClickListener(v -> {
            Intent backToLogin = new Intent(WelcomePage.this, MainActivity.class);
            backToLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(backToLogin);
            finish();
        });

        // ✅ Back button
        backArrow.setOnClickListener(view -> {
            Intent backIntent = new Intent(WelcomePage.this, MainActivity.class);
            startActivity(backIntent);
        });
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
}
