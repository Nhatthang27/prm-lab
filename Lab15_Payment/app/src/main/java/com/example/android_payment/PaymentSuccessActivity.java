package com.example.android_payment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class PaymentSuccessActivity extends AppCompatActivity {

    TextView tvOrderInfo, tvName, tvPhone, tvCard, successTitle, successSubtitle;
    Button btnBackHome;
    CardView iconCard, detailsCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        initViews();
        displayPaymentInfo();
        setupClickListeners();
        animateViews();
    }

    private void initViews() {
        tvOrderInfo = findViewById(R.id.tvOrderInfo);
        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);
        tvCard = findViewById(R.id.tvCard);
        btnBackHome = findViewById(R.id.btnBackHome);
        iconCard = findViewById(R.id.iconCard);
        detailsCard = findViewById(R.id.detailsCard);
        successTitle = findViewById(R.id.successTitle);
        successSubtitle = findViewById(R.id.successSubtitle);
    }

    private void animateViews() {
        Animation scaleBounce = AnimationUtils.loadAnimation(this, R.anim.scale_bounce);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideInUp = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);

        iconCard.startAnimation(scaleBounce);

        successTitle.postDelayed(() -> successTitle.startAnimation(fadeIn), 150);
        successSubtitle.postDelayed(() -> successSubtitle.startAnimation(fadeIn), 250);

        detailsCard.postDelayed(() -> detailsCard.startAnimation(slideInUp), 350);
        btnBackHome.postDelayed(() -> btnBackHome.startAnimation(fadeIn), 500);
    }

    private void displayPaymentInfo() {
        String phone = "1234567890";
        String card = "9704 **** **** 0018";
        String name = "NGUYEN VAN A";
        String orderId = "ORD123456789";

        tvOrderInfo.setText("Order ID: " + orderId);
        tvName.setText("Name: " + name);
        tvPhone.setText("Phone: " + phone);
        tvCard.setText("Card: " + card);
    }

    private void setupClickListeners() {
        btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentSuccessActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
