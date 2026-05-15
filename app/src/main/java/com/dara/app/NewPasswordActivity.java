package com.dara.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class NewPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        String email = getIntent().getStringExtra("email");
        TextView emailText = findViewById(R.id.reset_email_text);
        if (emailText != null) {
            emailText.setText(email);
        }

        TextInputEditText newPasswordInput = findViewById(R.id.new_password_input);
        TextInputEditText confirmPasswordInput = findViewById(R.id.confirm_password_input);
        MaterialButton updatePasswordButton = findViewById(R.id.update_password_button);

        updatePasswordButton.setOnClickListener(v -> {
            String newPassword = newPasswordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (newPassword.length() < 6) {
                newPasswordInput.setError("Password too short");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                confirmPasswordInput.setError("Passwords do not match");
                return;
            }

            // Since Firebase doesn't allow changing password without current password 
            // or an 'oobCode' from an email link, we MUST send the reset link.
            if (email != null) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Reset link sent! Please click it in your email, then login with your new password.", Toast.LENGTH_LONG).show();
                                
                                // Go back to Login screen so user is ready
                                Intent intent = new Intent(NewPasswordActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}