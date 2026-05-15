package com.dara.app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BudgetPlannerActivity extends AppCompatActivity implements BudgetAdapter.OnBudgetListener {

    private RecyclerView budgetRecyclerView;
    private BudgetAdapter budgetAdapter;
    private List<Budget> budgetList;
    private Button addBudgetButton;
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_planner);

        budgetRecyclerView = findViewById(R.id.budget_list);
        addBudgetButton = findViewById(R.id.add_budget_button);

        budgetList = new ArrayList<>();
        budgetAdapter = new BudgetAdapter(budgetList, this);
        budgetRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        budgetRecyclerView.setAdapter(budgetAdapter);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        
        sharedViewModel.getTransactionList().observe(this, transactions -> {
            updateSpentAmounts(transactions);
        });

        sharedViewModel.getBudgetList().observe(this, budgets -> {
            if (budgets != null) {
                budgetList.clear();
                budgetList.addAll(budgets);
                updateSpentAmounts(sharedViewModel.getTransactionList().getValue());
                budgetAdapter.notifyDataSetChanged();
            }
        });

        addBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddBudgetDialog();
            }
        });
    }

    private void updateSpentAmounts(List<Transaction> transactions) {
        if (transactions == null || budgetList == null) return;
        
        for (Budget budget : budgetList) {
            double totalSpent = 0;
            for (Transaction t : transactions) {
                if (t.getCategory().equalsIgnoreCase(budget.getCategory()) && t.getAmount() < 0) {
                    totalSpent += Math.abs(t.getAmount());
                }
            }
            budget.setSpentAmount(totalSpent);
        }
        budgetAdapter.notifyDataSetChanged();
    }

    private void showAddBudgetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Budget");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_budget, null);
        final Spinner categorySpinner = view.findViewById(R.id.dialog_category_spinner);
        final EditText amountEditText = view.findViewById(R.id.dialog_amount_edittext);
        final Spinner currencySpinner = view.findViewById(R.id.dialog_currency_spinner);

        List<String> categories = Arrays.asList("Food", "Transport", "Shopping", "Bills", "Entertainment");
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        List<String> currencies = Arrays.asList("USD", "KHR", "EUR");
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(currencyAdapter);

        builder.setView(view);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String category = (String) categorySpinner.getSelectedItem();
                String amount = amountEditText.getText().toString();
                String currency = (String) currencySpinner.getSelectedItem();
                
                if (!amount.isEmpty()) {
                    sharedViewModel.addBudget(new Budget(category, Double.parseDouble(amount), 0, currency));
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

    @Override
    public void onBudgetClick(int position) {
        showEditBudgetDialog(position);
    }

    private void showEditBudgetDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Budget");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_budget, null);
        final TextView categoryTextView = view.findViewById(R.id.dialog_edit_category_text);
        final EditText amountEditText = view.findViewById(R.id.dialog_edit_amount_edittext);

        Budget budget = budgetList.get(position);
        categoryTextView.setText(budget.getCategory());
        amountEditText.setText(String.valueOf(budget.getBudgetAmount()));

        builder.setView(view);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String amount = amountEditText.getText().toString();
                if (!amount.isEmpty()) {
                    budget.setBudgetAmount(Double.parseDouble(amount));
                    sharedViewModel.updateBudget(budget);
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