package com.dara.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddMoneyActivity extends AppCompatActivity {

    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        EditText amountInput = findViewById(R.id.amount_to_add);
        Button confirmButton = findViewById(R.id.btn_confirm_add_money);

        confirmButton.setOnClickListener(v -> {
            String amountText = amountInput.getText().toString();
            if (amountText.isEmpty()) {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if user is logged in
            if (com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(this, "Please log in first", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                double amount = Double.parseDouble(amountText);
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

                // Create transaction (Positive amount for income/deposit)
                Transaction transaction = new Transaction(
                        "Account Deposit",
                        date,
                        amount,
                        "Income"
                );

                sharedViewModel.addTransaction(transaction);
                
                // Show a helpful message
                Toast.makeText(this, "Processing Deposit...", Toast.LENGTH_SHORT).show();
                
                // Return to main screen (Firestore will sync in background)
                finish();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
            }
        });
    }
}