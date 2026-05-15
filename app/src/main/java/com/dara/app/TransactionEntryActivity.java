package com.dara.app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TransactionEntryActivity extends AppCompatActivity {

    private EditText datePicker, amountInput;
    private Calendar myCalendar;
    private Spinner categorySpinner, currencySpinner;
    private RadioGroup transactionTypeGroup;
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_entry);

        datePicker = findViewById(R.id.date_picker);
        amountInput = findViewById(R.id.amount_input);
        amountInput.setShowSoftInputOnFocus(false);
        myCalendar = Calendar.getInstance();
        categorySpinner = findViewById(R.id.category_selector);
        currencySpinner = findViewById(R.id.currency_selector);
        transactionTypeGroup = findViewById(R.id.transaction_type_group);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(TransactionEntryActivity.this,
                        date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Category Spinner
        List<String> categories = Arrays.asList("Food", "Transport", "Shopping", "Bills", "Entertainment", "Salary", "Freelance");
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // Currency Spinner
        List<String> currencies = Arrays.asList("USD", "EUR", "GBP", "JPY");
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(currencyAdapter);

        // Save Button
        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTransaction();
            }
        });

        // Keypad logic
        android.widget.GridLayout keypadGrid = findViewById(R.id.keypad_grid);
        for (int i = 0; i < keypadGrid.getChildCount(); i++) {
            View child = keypadGrid.getChildAt(i);
            if (child instanceof Button) {
                Button button = (Button) child;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleKeypadClick(button.getText().toString());
                    }
                });
            }
        }
    }

    private void handleKeypadClick(String key) {
        String currentAmount = amountInput.getText().toString();

        if (key.equals("C")) {
            amountInput.setText("");
        } else if (key.equals("OK")) {
            saveTransaction();
        } else if (key.equals(".")) {
            if (!currentAmount.contains(".")) {
                amountInput.setText(currentAmount + ".");
            }
        } else if (key.equals("00")) {
            if (!currentAmount.isEmpty()) {
                amountInput.setText(currentAmount + "00");
            }
        } else if (key.equals("+") || key.equals("-") || key.equals("*") || key.equals("/")) {
            // Basic operator support could be added here
        } else {
            // Numbers 0-9
            amountInput.setText(currentAmount + key);
        }
    }

    private void saveTransaction() {
        String amountStr = amountInput.getText().toString();
        String date = datePicker.getText().toString();

        if (amountStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill in all details", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedTypeId = transactionTypeGroup.getCheckedRadioButtonId();
        if (selectedTypeId == -1) {
            Toast.makeText(this, "Please select a transaction type", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount <= 0) {
            Toast.makeText(this, "Amount must be greater than zero", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedTypeId);
        String transactionType = selectedRadioButton.getText().toString();
        String category = (String) categorySpinner.getSelectedItem();

        if (transactionType.equalsIgnoreCase("Expense")) {
            double currentBalance = sharedViewModel.getCurrentBalanceSync();
            if (amount > currentBalance) {
                Toast.makeText(this, "Balance not enough.", Toast.LENGTH_LONG).show();
                return;
            }
            amount = -Math.abs(amount); // Ensure expense is negative
        } else {
            amount = Math.abs(amount); // Ensure income is positive
        }

        Transaction transaction = new Transaction(category, date, amount, transactionType);
        sharedViewModel.addTransaction(transaction);
        finish();
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        datePicker.setText(sdf.format(myCalendar.getTime()));
    }
}
