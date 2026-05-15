package com.dara.app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExchangeActivity extends AppCompatActivity {

    private Spinner fromSpinner, toSpinner;
    private EditText amountInput;
    private TextView resultText;
    private SharedViewModel sharedViewModel;

    // Static rates for demo (1 USD = 4100 KHR)
    private static final double USD_TO_KHR = 4100.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        fromSpinner = findViewById(R.id.spinner_from_currency);
        toSpinner = findViewById(R.id.spinner_to_currency);
        amountInput = findViewById(R.id.exchange_amount_input);
        resultText = findViewById(R.id.exchange_result_text);
        Button confirmButton = findViewById(R.id.btn_confirm_exchange);

        String[] currencies = {"USD", "KHR"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);
        toSpinner.setSelection(1); // Default to KHR

        amountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateExchange();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        confirmButton.setOnClickListener(v -> {
            String amountStr = amountInput.getText().toString();
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Enter an amount", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            String from = fromSpinner.getSelectedItem().toString();
            String to = toSpinner.getSelectedItem().toString();

            if (from.equals(to)) {
                Toast.makeText(this, "Currencies must be different", Toast.LENGTH_SHORT).show();
                return;
            }

            performExchange(from, to, amount);
        });
    }

    private void calculateExchange() {
        String s = amountInput.getText().toString();
        if (s.isEmpty()) {
            resultText.setText("You will receive: $0.00");
            return;
        }

        try {
            double amount = Double.parseDouble(s);
            String from = fromSpinner.getSelectedItem().toString();
            String to = toSpinner.getSelectedItem().toString();
            double result;

            if (from.equals("USD") && to.equals("KHR")) {
                result = amount * USD_TO_KHR;
                resultText.setText(String.format(Locale.US, "You will receive: ៛%,.0f", result));
            } else if (from.equals("KHR") && to.equals("USD")) {
                result = amount / USD_TO_KHR;
                resultText.setText(String.format(Locale.US, "You will receive: $%,.2f", result));
            } else {
                resultText.setText("You will receive: " + amount + " " + to);
            }
        } catch (Exception e) {
            resultText.setText("Invalid amount");
        }
    }

    private void performExchange(String from, String to, double amount) {
        double currentBalance = sharedViewModel.getCurrentBalanceSync();
        if (amount > currentBalance) {
            Toast.makeText(this, "Balance not enough. Exchange failed.", Toast.LENGTH_LONG).show();
            return;
        }

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        
        // In a real banking app, this would deduct from one balance and add to another.
        // Here we simulate it as a swap transaction.
        
        Transaction deduction = new Transaction(
                "Exchange " + from + " to " + to,
                date,
                -amount, // Sell (Expense in current simplified model)
                "Expense"
        );
        
        // We calculate the value in USD for the dashboard if needed, 
        // but our simple model uses one primary balance ($).
        
        sharedViewModel.addTransaction(deduction);
        
        Toast.makeText(this, "Exchange Successful!", Toast.LENGTH_LONG).show();
        finish();
    }
}