package com.dara.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private TextView currentLanguage, currentCurrency;
    private SwitchMaterial switchNotifications;
    private SwitchMaterial switchDarkMode;
    private FirebaseAuth mAuth;
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // UI References
        currentLanguage = findViewById(R.id.current_language);
        currentCurrency = findViewById(R.id.current_currency);
        LinearLayout btnLanguage = findViewById(R.id.settings_language);
        LinearLayout btnCurrency = findViewById(R.id.settings_currency);
        LinearLayout btnChangePassword = findViewById(R.id.settings_change_password);
        View btnLogout = findViewById(R.id.btn_settings_logout);
        View btnReset = findViewById(R.id.btn_settings_reset);
        switchNotifications = findViewById(R.id.switch_notifications);
        switchDarkMode = findViewById(R.id.switch_dark_mode);

        // Load Preferences
        loadPreferences();

        // Listeners
        btnLanguage.setOnClickListener(v -> showLanguageDialog());
        
        btnCurrency.setOnClickListener(v -> showCurrencyDialog());

        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnReset.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Reset Data")
                .setMessage("This will delete all your local transactions, budgets, and savings goals. Are you sure?")
                .setPositiveButton("Reset", (dialog, which) -> {
                    sharedViewModel.clearTransactions();
                    Toast.makeText(this, "All Data Cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
        });

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
            editor.putBoolean("Notifications", isChecked);
            editor.apply();
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
             SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
             editor.putBoolean("DarkMode", isChecked);
             editor.apply();
             
             if (isChecked) {
                 AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
             } else {
                 AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
             }
        });
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        
        // Language
        String lang = prefs.getString("My_Lang", "en");
        switch (lang) {
            case "km": currentLanguage.setText("Khmer"); break;
            case "zh": currentLanguage.setText("Chinese"); break;
            case "fr": currentLanguage.setText("French"); break;
            default: currentLanguage.setText("English"); break;
        }

        // Currency
        String currency = CurrencyHelper.getCurrencyCode(this);
        String symbol = CurrencyHelper.getCurrencySymbol(this);
        currentCurrency.setText(currency + " (" + symbol + ")");

        // Notifications
        boolean notif = prefs.getBoolean("Notifications", true);
        switchNotifications.setChecked(notif);
        
        // Dark Mode
        boolean darkMode = prefs.getBoolean("DarkMode", false);
        switchDarkMode.setChecked(darkMode);
    }

    private void showLanguageDialog() {
        final String[] languages = {"English", "Khmer", "Chinese", "French"};
        final String[] langCodes = {"en", "km", "zh", "fr"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Language");
        builder.setItems(languages, (dialog, which) -> {
            setLocale(langCodes[which]);
        });
        builder.show();
    }

    private void showCurrencyDialog() {
        final String[] currencies = {"USD ($)", "KHR (៛)", "CNY (¥)", "EUR (€)"};
        final String[] currencyCodes = {"USD", "KHR", "CNY", "EUR"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Currency");
        builder.setItems(currencies, (dialog, which) -> {
            CurrencyHelper.setCurrency(this, currencyCodes[which]);
            loadPreferences();
            Toast.makeText(this, "Currency Updated", Toast.LENGTH_SHORT).show();
            
            // Optional: Restart MainActivity to refresh all amounts
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        builder.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Password");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        final EditText oldPassInput = new EditText(this);
        oldPassInput.setHint("Current Password");
        oldPassInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(oldPassInput);

        final EditText newPassInput = new EditText(this);
        newPassInput.setHint("New Password");
        newPassInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(newPassInput);
        
        final EditText confirmPassInput = new EditText(this);
        confirmPassInput.setHint("Confirm New Password");
        confirmPassInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(confirmPassInput);

        builder.setView(layout);

        builder.setPositiveButton("Change", (dialog, which) -> {
            String oldPass = oldPassInput.getText().toString();
            String newPass = newPassInput.getText().toString();
            String confirmPass = confirmPassInput.getText().toString();

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(SettingsActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(SettingsActivity.this, "New passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            changeFirebasePassword(oldPass, newPass);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void changeFirebasePassword(String oldPass, String newPass) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(newPass).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(SettingsActivity.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SettingsActivity.this, "Error updating password", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(SettingsActivity.this, "Authentication Failed. Wrong old password.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
