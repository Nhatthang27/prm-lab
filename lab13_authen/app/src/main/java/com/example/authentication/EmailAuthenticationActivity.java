package com.example.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailAuthenticationActivity extends AppCompatActivity {

    private AppCompatButton btnRegister;
    private EditText editTextTextEmailAddress, editTextPassword;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_email_authentication);

        init();
        btnRegister.setOnClickListener(v -> {
            signUpUser(editTextTextEmailAddress.getText().toString(), editTextPassword.getText().toString());
        });
    }

    private void signUpUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(emailTask -> {
                                        if (emailTask.isSuccessful()) {
                                            Toast.makeText(EmailAuthenticationActivity.this, "Gửi xác thực thành công, vui lòng xác thực email để đăng nhập", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(EmailAuthenticationActivity.this, LoginEmailActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(EmailAuthenticationActivity.this, "Đã có lỗi xảy ra. Gửi xác thực thất bại.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(EmailAuthenticationActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        editTextTextEmailAddress = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnRegister = findViewById(R.id.btnConfirm);
    }

}