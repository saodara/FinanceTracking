package com.dara.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        TextInputEditText emailInput = findViewById(R.id.email_input_forgot);
        MaterialButton sendOtpButton = findViewById(R.id.send_otp_button);

        sendOtpButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            if (email.isEmpty()) {
                emailInput.setError("Email is required");
            } else {
                sendResetOtp(email);
            }
        });

        findViewById(R.id.back_to_login_forgot).setOnClickListener(v -> finish());
    }

    private void sendResetOtp(String email) {
        // Generate random 6-digit OTP using OtpManager
        String otp = OtpManager.generateOtp(this);
        
        // Show message
        Toast.makeText(this, "Sending reset code to: " + email, Toast.LENGTH_SHORT).show();

        // Send OTP via EmailJS
        OtpManager.sendOtp(email, otp, new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, java.io.IOException e) {
                android.util.Log.e("ForgotPassword", "Failed to send OTP: " + e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {
                if (response.isSuccessful()) {
                    android.util.Log.d("ForgotPassword", "OTP sent successfully");
                } else {
                    android.util.Log.e("ForgotPassword", "OTP send failed: " + response.body().string());
                }
            }
        });

        Intent intent = new Intent(ForgotPasswordActivity.this, OtpVerificationActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("otp", otp);
        intent.putExtra("mode", "reset"); // Tell OTP activity we are resetting password
        startActivity(intent);
    }
}