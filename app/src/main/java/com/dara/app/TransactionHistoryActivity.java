package com.dara.app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        recyclerView = findViewById(R.id.transaction_history_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.getTransactionList().observe(this, new Observer<List<Transaction>>() {
            @Override
            public void onChanged(List<Transaction> transactions) {
                adapter = new TransactionAdapter(transactions);
                recyclerView.setAdapter(adapter);
            }
        });

        Button cleanButton = findViewById(R.id.clean_button);
        cleanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCleanOptionsDialog();
            }
        });
    }

    private void showCleanOptionsDialog() {
        final CharSequence[] options = {"Clean All", "Clean by Amount", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(TransactionHistoryActivity.this);
        builder.setTitle("Clean Transactions");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Clean All")) {
                    sharedViewModel.clearTransactions();
                } else if (options[item].equals("Clean by Amount")) {
                    showCleanByAmountDialog();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void showCleanByAmountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TransactionHistoryActivity.this);
        builder.setTitle("Clean by Amount");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_clean_options, null);
        final EditText amountEditText = view.findViewById(R.id.clean_amount_edit_text);

        builder.setView(view);

        builder.setPositiveButton("Clean", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String amount = amountEditText.getText().toString();
                if (!amount.isEmpty()) {
                    sharedViewModel.clearTransactionsAboveAmount(Double.parseDouble(amount));
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}