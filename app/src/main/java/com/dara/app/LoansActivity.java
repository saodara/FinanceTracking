package com.dara.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class LoansActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loans);

        EditText amountInput = findViewById(R.id.loan_amount);
        EditText rateInput = findViewById(R.id.loan_rate);
        EditText termInput = findViewById(R.id.loan_term);
        TextView resultText = findViewById(R.id.loan_result);
        Button calculateBtn = findViewById(R.id.btn_calculate_loan);

        calculateBtn.setOnClickListener(v -> {
            String amountStr = amountInput.getText().toString();
            String rateStr = rateInput.getText().toString();
            String termStr = termInput.getText().toString();

            if (amountStr.isEmpty() || rateStr.isEmpty() || termStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double principal = Double.parseDouble(amountStr);
                double annualRate = Double.parseDouble(rateStr);
                int months = Integer.parseInt(termStr);

                double monthlyRate = (annualRate / 100) / 12;
                double monthlyPayment = (principal * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -months));

                resultText.setText(String.format(Locale.US, "Monthly Payment: $%.2f", monthlyPayment));
            } catch (Exception e) {
                Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
            }
        });
    }
}