package com.example.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button btnSignInEmail, btnSignUpEmail, btnSignInPhone, btnSignUpPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnSignInEmail = findViewById(R.id.btnSignInEmail);
        btnSignUpEmail = findViewById(R.id.btnSignUpEmail);
        btnSignInPhone = findViewById(R.id.btnSignInPhone);
        btnSignUpPhone = findViewById(R.id.btnSignUpPhone);

        btnSignInEmail.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
        });

        btnSignUpEmail.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
        });

        btnSignInPhone.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PhoneAuthActivity.class);
            intent.putExtra("isSignUp", false);
            startActivity(intent);
        });

        btnSignUpPhone.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PhoneAuthActivity.class);
            intent.putExtra("isSignUp", true);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}