package com.example.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {

    private LinearLayout layoutPhoneInput, layoutOtpInput;
    private EditText editTextPhone, editTextCode;
    private Button btnSendCode, btnVerify, btnResendCode;
    private FirebaseAuth mAuth;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private boolean isSignUp = false;
    private String phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        init();

        // Lấy thông tin từ Intent
        isSignUp = getIntent().getBooleanExtra("isSignUp", false);

        btnSendCode.setOnClickListener(v -> {
            phoneNumber = editTextPhone.getText().toString().trim();
            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số điện thoại!", Toast.LENGTH_SHORT).show();
                return;
            }
            sendVerificationCode(phoneNumber);
        });

        btnVerify.setOnClickListener(v -> {
            String code = editTextCode.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mã xác minh!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (code.length() != 6) {
                Toast.makeText(this, "Mã xác minh phải có 6 chữ số!", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyCode(code);
        });

        btnResendCode.setOnClickListener(v -> {
            if (!phoneNumber.isEmpty() && resendToken != null) {
                resendVerificationCode(phoneNumber);
            }
        });
    }

    private void sendVerificationCode(String phone) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(callbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void resendVerificationCode(String phone) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(callbacks)
                        .setForceResendingToken(resendToken)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                    String code = credential.getSmsCode();
                    if (code != null) {
                        editTextCode.setText(code);
                        verifyCode(code);
                    } else {
                        signInWithCredential(credential);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(PhoneAuthActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCodeSent(@NonNull String verificationId,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    PhoneAuthActivity.this.verificationId = verificationId;
                    PhoneAuthActivity.this.resendToken = token;
                    Toast.makeText(PhoneAuthActivity.this, "Mã xác minh đã được gửi!", Toast.LENGTH_SHORT).show();
                    layoutPhoneInput.setVisibility(View.GONE);
                    layoutOtpInput.setVisibility(View.VISIBLE);
                }
            };

    private void verifyCode(String code) {
        if (verificationId == null) {
            Toast.makeText(this, "Bạn chưa gửi mã xác minh!", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (isSignUp) {
                            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        }
                        Intent intent = new Intent(PhoneAuthActivity.this, LoginSuccessActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else {
                        Toast.makeText(this, "Sai mã xác minh!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        layoutPhoneInput = findViewById(R.id.layoutPhoneInput);
        layoutOtpInput = findViewById(R.id.layoutOtpInput);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextCode = findViewById(R.id.editTextCode);
        btnSendCode = findViewById(R.id.btnSendCode);
        btnVerify = findViewById(R.id.btnVerify);
        btnResendCode = findViewById(R.id.btnResendCode);
    }
}
