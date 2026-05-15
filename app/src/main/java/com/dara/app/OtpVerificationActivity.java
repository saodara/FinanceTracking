package com.dara.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OtpVerificationActivity extends AppCompatActivity {

    private static final String TAG = "OtpVerificationActivity";
    private String name, email, password, correctOtp, mode;
    private TextInputEditText otpInput;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get data from intent
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        correctOtp = getIntent().getStringExtra("otp");
        mode = getIntent().getStringExtra("mode"); // 'register' or 'reset'

        otpInput = findViewById(R.id.otp_input);
        MaterialButton verifyButton = findViewById(R.id.verify_button);
        TextView instruction = findViewById(R.id.otp_instruction);

        instruction.setText("We sent a 6-digit code to " + email);

        verifyButton.setOnClickListener(v -> {
            String enteredOtp = otpInput.getText().toString().trim();
            // Verify OTP using OtpManager or fallback to Intent extra
            boolean isValid = OtpManager.verifyOtp(this, enteredOtp);
            if (!isValid && correctOtp != null && !correctOtp.isEmpty()) {
                isValid = correctOtp.equals(enteredOtp);
            }

            if (isValid) {
                if ("reset".equals(mode)) {
                    // Go to Set New Password screen
                    Intent intent = new Intent(OtpVerificationActivity.this, NewPasswordActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                } else {
                    // Regular registration flow
                    registerUser();
                }
            } else {
                otpInput.setError("Invalid OTP");
                Toast.makeText(this, "The code you entered is incorrect.", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.resend_otp).setOnClickListener(v -> {
            // Generate a new 6-digit OTP using OtpManager
            String newOtp = OtpManager.generateOtp(this);
            
            // Send new OTP via EmailJS
            OtpManager.sendOtp(email, newOtp, new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, java.io.IOException e) {
                    Log.e(TAG, "Failed to resend OTP: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"));
                    runOnUiThread(() ->
                            Toast.makeText(OtpVerificationActivity.this, "Failed to send OTP", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "OTP resent successfully");
                        runOnUiThread(() ->
                                Toast.makeText(OtpVerificationActivity.this, "OTP sent to your email!", Toast.LENGTH_SHORT).show()
                        );
                    } else {
                        String errorMsg = response.body() != null ? response.body().string() : "No body";
                        Log.e(TAG, "OTP resend failed: " + errorMsg);
                    }
                }
            });
        });
    }

    private void registerUser() {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Set display name
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            user.updateProfile(profileUpdates);

                            // Save user profile data to Realtime Database
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("uid", user.getUid());
                            userData.put("displayName", name);
                            userData.put("email", email);
                            userData.put("createdAt", System.currentTimeMillis());

                            mDatabase.child("users").child(user.getUid()).child("profile")
                                    .setValue(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, String.format(Locale.US, "Welcome %s!", name), Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(OtpVerificationActivity.this, MainActivity.class));
                                        finishAffinity();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Database save failed", e);
                                        Toast.makeText(this, "Profile Save Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        }
                    } else {
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(this, "Registration Failed: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
