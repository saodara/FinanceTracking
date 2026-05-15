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

public class TransfersActivity extends AppCompatActivity {

    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfers);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        Button transferButton = findViewById(R.id.transfer_button);
        if (transferButton != null) {
            transferButton.setOnClickListener(v -> {
                EditText toAccount = findViewById(R.id.to_account);
                EditText amountInput = findViewById(R.id.amount);
                
                String accountText = toAccount != null ? toAccount.getText().toString() : "";
                String amountText = amountInput != null ? amountInput.getText().toString() : "";

                if (accountText.isEmpty() || amountText.isEmpty()) {
                    Toast.makeText(this, "Please fill in all details", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        double amount = Double.parseDouble(amountText);
                        
                        // Check balance before proceeding
                        double currentBalance = sharedViewModel.getCurrentBalanceSync();
                        if (amount > currentBalance) {
                            Toast.makeText(this, "Balance not enough. Transfer failed.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
                        
                        // Create transaction (Negative amount for expense/transfer out)
                        Transaction transaction = new Transaction(
                                "Transfer to " + accountText,
                                date,
                                -amount, 
                                "Expense"
                        );
                        
                        sharedViewModel.addTransaction(transaction);
                        
                        Toast.makeText(this, "Transfer Successful!", Toast.LENGTH_LONG).show();
                        finish();
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
