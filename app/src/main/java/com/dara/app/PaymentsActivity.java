package com.dara.app;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentsActivity extends AppCompatActivity {

    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        GridLayout grid = findViewById(R.id.grid_layout);
        if (grid != null) {
            for (int i = 0; i < grid.getChildCount(); i++) {
                if (grid.getChildAt(i) instanceof Button) {
                    Button btn = (Button) grid.getChildAt(i);
                    btn.setOnClickListener(v -> showPaymentDialog(btn.getText().toString()));
                }
            }
        }
    }

    private void showPaymentDialog(String serviceName) {
        if ("More".equalsIgnoreCase(serviceName.trim())) {
            showMoreOptionsDialog();
        } else {
            showServicePaymentDialog(serviceName);
        }
    }

    private void showMoreOptionsDialog() {
        String[] options = {"Food", "Sport", "Shopping", "Other"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Category");
        builder.setItems(options, (dialog, which) -> {
            showServicePaymentDialog(options[which]);
        });
        builder.show();
    }

    private void showServicePaymentDialog(String serviceName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pay " + serviceName.replace("\n", " "));

        // Create a container layout
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        // Account Number / ID Input (Top)
        final EditText idInput = new EditText(this);

        // Use phone keyboard for Top-up, general text for others
        if (serviceName.toLowerCase().contains("top-up")) {
            idInput.setInputType(InputType.TYPE_CLASS_PHONE);
            idInput.setHint("Phone Number ");
        } else {
            idInput.setInputType(InputType.TYPE_CLASS_TEXT);
            idInput.setHint("Account Number / Consumer ID");
        }
        layout.addView(idInput);

        // Amount Input (Down/Bottom)
        final EditText amountInput = new EditText(this);
        amountInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        amountInput.setHint("Enter Amount ($)");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 30, 0, 0);
        amountInput.setLayoutParams(params);
        layout.addView(amountInput);

        builder.setView(layout);

        builder.setPositiveButton("Confirm Payment", (dialog, which) -> {
            String amountText = amountInput.getText().toString();
            String idText = idInput.getText().toString();

            if (amountText.isEmpty() || idText.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    double amount = Double.parseDouble(amountText);
                    String description = serviceName.replace("\n", " ") + ": " + idText;
                    processPayment(description, amount);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid Amount", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void processPayment(String description, double amount) {
        double currentBalance = sharedViewModel.getCurrentBalanceSync();
        if (amount > currentBalance) {
            Toast.makeText(this, "Balance not enough. Transaction failed.", Toast.LENGTH_LONG).show();
            return;
        }

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

        // Create transaction (Negative amount for expense)
        Transaction transaction = new Transaction(
                description,
                date,
                -amount,
                "Expense"
        );

        sharedViewModel.addTransaction(transaction);
        Toast.makeText(this, "Successful!", Toast.LENGTH_SHORT).show();
    }
}
