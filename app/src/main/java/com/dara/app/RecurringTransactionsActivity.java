package com.dara.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class RecurringTransactionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recurring_transactions);

        Button btnAdd = findViewById(R.id.btn_add_recurring);
        btnAdd.setOnClickListener(v -> showAddDialog());
    }

    private void showAddDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Add Recurring Payment")
                .setMessage("Feature under development. This will allow setting up automatic payments.")
                .setPositiveButton("OK", null)
                .show();
    }
}