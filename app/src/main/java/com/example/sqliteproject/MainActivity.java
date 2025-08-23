package com.example.sqliteproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
//import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    EditText email;
    Button signup_btn;
    LoginInfo loginInfo;;

    DatabaseAccessModifier databaseAccessModifier;

    ArrayAdapter arrayAdapter;
    TextView signInLink;

//    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.nameInput);
        password = findViewById(R.id.passwordInput);
        email = findViewById(R.id.emailInput);
        signup_btn = findViewById(R.id.signUpButton);
        signInLink = findViewById(R.id.signInLink);

        databaseAccessModifier = new DatabaseAccessModifier(this);

//        arrayAdapter
//                = new ArrayAdapter<LoginInfo>(MainActivity.this,
//                android.R.layout.simple_list_item_1,
//                databaseAccessModifier.getResultList());
//
//        listView.setAdapter(arrayAdapter);

        signInLink.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginPage.class);
            startActivity(intent);
        });

        signup_btn.setOnClickListener(view -> {
            try {
                loginInfo = new LoginInfo(
                        -1,
                        username.getText().toString(),
                        email.getText().toString(),
                        password.getText().toString()
                );

                DatabaseAccessModifier databaseAccessModifier = new DatabaseAccessModifier(MainActivity.this);

                boolean success = databaseAccessModifier.addOne(loginInfo);

                if (success) {
                    Toast.makeText(MainActivity.this, "Data Saved!!!", Toast.LENGTH_SHORT).show();

                    // 👉 Pass username to WelcomePage
                    Intent intent = new Intent(MainActivity.this, WelcomePage.class);
                    intent.putExtra("username", username.getText().toString());
                    startActivity(intent);

                    // Optional: close signup page so user can’t go back
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to save user", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception ex) {
                Toast.makeText(this, "Missing Inputs", Toast.LENGTH_SHORT).show();
            }
        });


//        btnAdd.setOnClickListener(view -> {
//            databaseAccessModifier = new DatabaseAccessModifier(MainActivity.this);
//            List<LoginInfo> allMembers = databaseAccessModifier.getResultList();
//
//            arrayAdapter = new ArrayAdapter<LoginInfo>(MainActivity.this, android.R.layout.simple_list_item_1, databaseAccessModifier.getResultList());
//            listView.setAdapter(arrayAdapter);
//
//            Toast.makeText(MainActivity.this, allMembers.toString(), Toast.LENGTH_SHORT).show();
//        });
//
//        listView.setOnItemClickListener((adapterView, view, i, l) -> {
//            LoginInfo clickedMember = (LoginInfo) adapterView.getItemAtPosition(i);
//            databaseAccessModifier.deleteOne(clickedMember);
//            arrayAdapter = new ArrayAdapter<LoginInfo>(MainActivity.this, android.R.layout.simple_list_item_1, databaseAccessModifier.getResultList());
//            listView.setAdapter(arrayAdapter);
//            Toast.makeText(MainActivity.this, "Deleted " + clickedMember.toString(), Toast.LENGTH_SHORT).show();
//        });

    }
}