package com.example.android_payment;
import static androidx.fragment.app.FragmentManager.TAG;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    Button btnMinus, btnPlus, btnPayMoMo, btnPayZaloPay;
    TextView tvQuantity, tvSubtotal, tvTotal, tvResult;
    CardView productCard, summaryCard, resultCard;

    private int quantity = 1;
    private final int PRICE_PER_ITEM = 10000;
    public static final String TAG = "FragmentManager";
    
    // MoMo constants
    private final String MOMO_ENDPOINT = "https://test-payment.momo.vn/v2/gateway/api/create";
    private final String MOMO_PARTNER_CODE = "MOMO";
    private final String MOMO_ACCESS_KEY = "F8BBA842ECF85";
    private final String MOMO_SECRET_KEY = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
    
    // ZaloPay constants
    private final String ZALOPAY_ENDPOINT = "https://sb-openapi.zalopay.vn/v2/create";
    private final String ZALOPAY_APP_ID = "2553";
    private final String ZALOPAY_KEY1 = "PcY4iZIKFCIdgZvA6ueMcMHHUbRLYjPL";
    private final String ZALOPAY_KEY2 = "kLtgPl8HHhfvMuDHPwKfgfsY4Ydm9eIz";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setupClickListeners();
        updateCartSummary();
        handlePaymentCallback();
        animateViews();
    }

    private void initViews() {
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnPayMoMo = findViewById(R.id.btnPayMoMo);
        btnPayZaloPay = findViewById(R.id.btnPayZaloPay);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTotal = findViewById(R.id.tvTotal);
        tvResult = findViewById(R.id.tvResult);
        productCard = findViewById(R.id.productCard);
        summaryCard = findViewById(R.id.summaryCard);
        resultCard = findViewById(R.id.resultCard);
    }

    private void animateViews() {
        Animation slideInUp = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        productCard.startAnimation(slideInUp);

        summaryCard.postDelayed(() -> summaryCard.startAnimation(slideInUp), 100);

        View paymentButtonsLayout = findViewById(R.id.paymentButtonsLayout);
        paymentButtonsLayout.postDelayed(() -> paymentButtonsLayout.startAnimation(fadeIn), 200);
    }

    private void handlePaymentCallback() {
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            Uri data = intent.getData();
            String scheme = data.getScheme();
            
            if ("myapp".equals(scheme) && "payment-success".equals(data.getHost())) {
                // Payment successful, redirect to success activity
                Intent successIntent = new Intent(this, PaymentSuccessActivity.class);
                startActivity(successIntent);
                finish();
            }
        }
    }


    private void setupClickListeners() {
        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateCartSummary();
            }
        });

        btnPlus.setOnClickListener(v -> {
            quantity++;
            updateCartSummary();
        });

        btnPayMoMo.setOnClickListener(v -> {
            int amount = quantity * PRICE_PER_ITEM;
            createMoMoOrder(String.valueOf(amount));
        });

        btnPayZaloPay.setOnClickListener(v -> {
            int amount = quantity * PRICE_PER_ITEM;
            createZaloPayOrder(amount);
        });
    }

    private void updateCartSummary() {
        tvQuantity.setText(String.valueOf(quantity));
        int subtotal = quantity * PRICE_PER_ITEM;
        tvSubtotal.setText(formatCurrency(subtotal));
        tvTotal.setText(formatCurrency(subtotal));
    }

    private String formatCurrency(int amount) {
        return String.format("%,d VNĐ", amount);
    }

    private void createMoMoOrder(String amount) {
        resultCard.setVisibility(View.VISIBLE);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        resultCard.startAnimation(fadeIn);
        tvResult.setText("Đang xử lý thanh toán MoMo...");
        setButtonsEnabled(false);
        new Thread(() -> {
            try {
                String orderId = MOMO_PARTNER_CODE + System.currentTimeMillis();
                String requestId = orderId;
                String orderInfo = "Thanh toán MoMo - " + quantity + " sản phẩm";
                String redirectUrl = "myapp://payment-success";
                String ipnUrl = "https://webhook.site/123abc";
                String requestType = "payWithMethod";
                String extraData = "";
                
                String rawSignature = "accessKey=" + MOMO_ACCESS_KEY +
                        "&amount=" + amount +
                        "&extraData=" + extraData +
                        "&ipnUrl=" + ipnUrl +
                        "&orderId=" + orderId +
                        "&orderInfo=" + orderInfo +
                        "&partnerCode=" + MOMO_PARTNER_CODE +
                        "&redirectUrl=" + redirectUrl +
                        "&requestId=" + requestId +
                        "&requestType=" + requestType;
                        
                String signature = hmacSHA256(rawSignature, MOMO_SECRET_KEY);
                
                JSONObject requestBody = new JSONObject();
                requestBody.put("partnerCode", MOMO_PARTNER_CODE);
                requestBody.put("partnerName", "test");
                requestBody.put("storeId", "MomotestStore");
                requestBody.put("accessKey", MOMO_ACCESS_KEY);
                requestBody.put("requestId", requestId);
                requestBody.put("amount", Integer.parseInt(amount));
                requestBody.put("orderId", orderId);
                requestBody.put("orderInfo", orderInfo);
                requestBody.put("redirectUrl", redirectUrl);
                requestBody.put("ipnUrl", ipnUrl);
                requestBody.put("extraData", extraData);
                requestBody.put("requestType", requestType);
                requestBody.put("signature", signature);
                requestBody.put("lang", "vi");
                
                URL url = new URL(MOMO_ENDPOINT);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);
                
                OutputStream os = conn.getOutputStream();
                os.write(requestBody.toString().getBytes("UTF-8"));
                os.flush();
                os.close();
                
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A");
                    String responseBody = scanner.hasNext() ? scanner.next() : "";
                    JSONObject response = new JSONObject(responseBody);
                    int resultCode = response.getInt("resultCode");
                    String message = response.getString("message");
                    
                    if (resultCode == 0) {
                        String payUrl = response.getString("payUrl");
                        runOnUiThread(() -> {
                            tvResult.setText("Tạo đơn hàng MoMo thành công!\nNhấn để thanh toán.");
                            Log.d(TAG, "createMoMoOrder: " + payUrl);
                            openMoMoPaymentUrl(payUrl);
                            setButtonsEnabled(true);
                        });
                    } else {
                        runOnUiThread(() -> {
                            tvResult.setText("Lỗi từ MoMo: " + message);
                            setButtonsEnabled(true);
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        tvResult.setText("Lỗi kết nối MoMo: " + responseCode);
                        setButtonsEnabled(true);
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    tvResult.setText("Lỗi MoMo: " + e.getMessage());
                    setButtonsEnabled(true);
                });
            }
        }).start();
    }

     private void createZaloPayOrder(int amount) {
         resultCard.setVisibility(View.VISIBLE);
         Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
         resultCard.startAnimation(fadeIn);
         tvResult.setText("Đang xử lý thanh toán ZaloPay...");
         setButtonsEnabled(false);
        
         new Thread(() -> {
             try {
                 String appTransId = getVietnamDatePrefix() + "_" + (System.currentTimeMillis() % 1000000);
                 String appUser = "ZaloPayDemo";
                 long appTime = System.currentTimeMillis();
                 String description = "Android Payment - Thanh toán đơn hàng #" + appTransId;
                 String callbackUrl = "myapp://payment-success";

                 JSONObject embedDataObj = new JSONObject();
                 embedDataObj.put("redirecturl", callbackUrl);
                 String embedDataStr = embedDataObj.toString();

                 JSONArray itemsArr = new JSONArray();
                 JSONObject itemObj = new JSONObject();
                 itemObj.put("itemid", "knb");
                 itemObj.put("itemname", "kim nguyen bao");
                 itemObj.put("itemprice", amount);
                 itemObj.put("itemquantity", quantity);
                 itemsArr.put(itemObj);
                 String itemStr = itemsArr.toString();

                 String data = ZALOPAY_APP_ID + "|" + appTransId + "|" + appUser + "|" +
                         amount + "|" + appTime + "|" + embedDataStr + "|" + itemStr;
                 String mac = hmacSHA256(data, ZALOPAY_KEY1);

                 JSONObject requestBody = new JSONObject();
                 requestBody.put("app_id", Integer.parseInt(ZALOPAY_APP_ID));
                 requestBody.put("app_user", appUser);
                 requestBody.put("app_time", appTime);
                 requestBody.put("amount", amount);
                 requestBody.put("app_trans_id", appTransId);
                 requestBody.put("bank_code", "");
                 requestBody.put("embed_data", embedDataStr);
                 requestBody.put("item", itemStr);
                 requestBody.put("callback_url", callbackUrl);
                 requestBody.put("description", description);
                 requestBody.put("mac", mac);

                 URL url = new URL(ZALOPAY_ENDPOINT);
                 HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                 conn.setRequestMethod("POST");
                 conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                 conn.setDoOutput(true);
                
                 OutputStream os = conn.getOutputStream();
                 os.write(requestBody.toString().getBytes("UTF-8"));
                 os.flush();
                 os.close();
                
                 int responseCode = conn.getResponseCode();
                 if (responseCode == 200) {
                     Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A");
                     String responseBody = scanner.hasNext() ? scanner.next() : "";
                     JSONObject response = new JSONObject(responseBody);
                     int returnCode = response.getInt("return_code");
                     String returnMessage = response.getString("return_message");
                    
                     if (returnCode == 1) {
                         String orderUrl = response.getString("order_url");
                         Log.d(TAG, "createZaloPayOrder: " + orderUrl);
                         runOnUiThread(() -> {
                             tvResult.setText("Tạo đơn hàng ZaloPay thành công!\nNhấn để thanh toán.");
                             openZaloPayUrl(orderUrl);
                             setButtonsEnabled(true);
                         });
                     } else {
                         runOnUiThread(() -> {
                             tvResult.setText("Lỗi từ ZaloPay: " + returnMessage);
                             setButtonsEnabled(true);
                         });
                     }
                 } else {
                     runOnUiThread(() -> {
                         tvResult.setText("Lỗi kết nối ZaloPay: " + responseCode);
                         setButtonsEnabled(true);
                     });
                 }
             } catch (Exception e) {
                 runOnUiThread(() -> {
                     tvResult.setText("Lỗi ZaloPay: " + e.getMessage());
                     setButtonsEnabled(true);
                 });
             }
         }).start();
     }

    private String getVietnamDatePrefix() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyMMdd");
        java.util.TimeZone tz = java.util.TimeZone.getTimeZone("GMT+7");
        sdf.setTimeZone(tz);
        return sdf.format(new java.util.Date());
    }

    private void openMoMoPaymentUrl(String payUrl) {
        try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(payUrl));
                startActivity(browserIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi mở trang thanh toán MoMo", Toast.LENGTH_SHORT).show();
        }
    }

    private void openZaloPayUrl(String orderUrl) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(orderUrl));
            startActivity(browserIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi mở trang thanh toán ZaloPay", Toast.LENGTH_SHORT).show();
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        btnPayMoMo.setEnabled(enabled);
        btnPayZaloPay.setEnabled(enabled);
        btnMinus.setEnabled(enabled);
        btnPlus.setEnabled(enabled);
    }

    private String hmacSHA256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        mac.init(secretKey);
        byte[] bytes = mac.doFinal(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}

