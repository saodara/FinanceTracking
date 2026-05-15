package com.dara.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;
import java.util.Locale;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private TextView balanceAmount, incomeAmount, expensesAmount;
    private SharedViewModel sharedViewModel;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLocale();
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        setupBottomNavigation();
        
        balanceAmount = findViewById(R.id.balance_amount);
        incomeAmount = findViewById(R.id.income_amount);
        expensesAmount = findViewById(R.id.expenses_amount);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.getTransactionList().observe(this, new Observer<List<Transaction>>() {
            @Override
            public void onChanged(List<Transaction> transactions) {
                updateDashboard(transactions);
            }
        });

        // Setup Main Grid Buttons
        setupClick(R.id.btn_transfers, TransfersActivity.class);
        setupClick(R.id.btn_payments, PaymentsActivity.class);
        setupClick(R.id.btn_scan_qr, ScanQrActivity.class);
        setupClick(R.id.btn_accounts, AddMoneyActivity.class);
        setupClick(R.id.btn_cards, CardsActivity.class);
        setupClick(R.id.btn_services, ServicesActivity.class);
        setupClick(R.id.btn_exchange, ExchangeActivity.class);
        setupClick(R.id.btn_loans, LoansActivity.class);
        setupClick(R.id.btn_more, FamilyHubActivity.class); // Using More button for Family Hub

        // Setup Tool List Buttons
        setupClick(R.id.budget_planner_button, BudgetPlannerActivity.class);
        setupClick(R.id.savings_goals_button, SavingsGoalsActivity.class);
        setupClick(R.id.analytics_button, AnalyticsActivity.class);

        // Profile Clicks
        View profileIcon = findViewById(R.id.profile_icon);
        TextView profileInitials = findViewById(R.id.profile_initials);
        if (currentUser != null && profileInitials != null) {
            String displayName = currentUser.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                String[] parts = displayName.split(" ");
                StringBuilder initials = new StringBuilder();
                for (String part : parts) {
                    if (!part.isEmpty()) initials.append(part.charAt(0));
                }
                profileInitials.setText(initials.toString().toUpperCase());
            } else {
                profileInitials.setText("U"); // Default for User
            }
        }
        
        if (profileIcon != null) {
            profileIcon.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));
        }

    }

    private void setupClick(int viewId, Class<?> activityClass) {
        View view = findViewById(viewId);
        if (view != null) {
            view.setOnClickListener(v -> {
                if (activityClass != null) {
                    startActivity(new Intent(MainActivity.this, activityClass));
                } else {
                    showComingSoonDialog();
                }
            });
        }
    }

    private void showComingSoonDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Coming Soon")
                .setMessage("This feature is currently under development. Stay tuned for updates!")
                .setPositiveButton("OK", null)
                .show();
    }

    private void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "en");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_history) {
                startActivity(new Intent(this, TransactionHistoryActivity.class));
                return true;
            } else if (itemId == R.id.nav_analytics) {
                startActivity(new Intent(this, AnalyticsActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return itemId == R.id.nav_home;
        });
    }

    private void updateDashboard(List<Transaction> transactionList) {
        if (transactionList == null) return;
        double totalIncome = 0;
        double totalExpenses = 0;

        for (Transaction transaction : transactionList) {
            String type = transaction.getType();
            if (type != null && type.equalsIgnoreCase("Income")) {
                totalIncome += Math.abs(transaction.getAmount());
            } else if (type != null) {
                totalExpenses += Math.abs(transaction.getAmount());
            }
        }

        double currentBalance = totalIncome - totalExpenses;

        if (balanceAmount != null) {
            balanceAmount.setText(CurrencyHelper.formatCurrency(this, currentBalance));
            if (currentBalance <= 0) {
                balanceAmount.setTextColor(Color.RED);
            } else {
                balanceAmount.setTextColor(Color.parseColor("#006C89")); // ABA Primary
            }
        }

        if (incomeAmount != null) incomeAmount.setText("+ " + CurrencyHelper.formatCurrency(this, totalIncome));
        if (expensesAmount != null) expensesAmount.setText("- " + CurrencyHelper.formatCurrency(this, totalExpenses));
    }
}
