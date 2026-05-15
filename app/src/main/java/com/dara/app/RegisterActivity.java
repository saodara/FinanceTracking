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

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private FirebaseAuth mAuth;

    private TextInputEditText nameInput, emailInput, passwordInput;
    private MaterialButton signUpButton;
    private TextView backToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signUpButton = findViewById(R.id.signUpButton);
        backToLogin = findViewById(R.id.backToLogin);

        signUpButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (name.isEmpty()) {
                nameInput.setError("Name is required");
                return;
            }
            if (email.isEmpty()) {
                emailInput.setError("Email is required");
                return;
            }
            if (password.length() < 6) {
                passwordInput.setError("Password must be at least 6 characters");
                return;
            }

            registerUser(name, email, password);
        });

        backToLogin.setOnClickListener(v -> finish());
    }

    private void registerUser(String name, String email, String password) {
        // Check if email already exists in Firebase Auth
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().getSignInMethods().isEmpty()) {
                    // Email not in use, proceed with OTP
                    sendOtpAndNavigate(name, email, password);
                } else {
                    // Email already exists
                    Toast.makeText(RegisterActivity.this, "This email is already registered. Please login.", Toast.LENGTH_LONG).show();
                    emailInput.setError("Email already in use");
                }
            } else {
                Log.e(TAG, "Error checking email: " + task.getException().getMessage());
                sendOtpAndNavigate(name, email, password); // Fallback to original flow
            }
        });
    }

    private void sendOtpAndNavigate(String name, String email, String password) {
        // Generate random 6-digit OTP using OtpManager
        String otp = OtpManager.generateOtp(this);
        
        Toast.makeText(this, "Sending OTP to: " + email, Toast.LENGTH_SHORT).show();

        // Send OTP via EmailJS
        OtpManager.sendOtp(email, otp, new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, java.io.IOException e) {
                Log.e(TAG, "Failed to send OTP: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Network Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "OTP sent successfully");
                } else {
                    String error = response.body().string();
                    Log.e(TAG, "OTP send failed: " + error);
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "EmailJS Error: " + error, Toast.LENGTH_LONG).show());
                }
            }
        });

        Intent intent = new Intent(RegisterActivity.this, OtpVerificationActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        intent.putExtra("otp", otp);
        startActivity(intent);
    }
}
