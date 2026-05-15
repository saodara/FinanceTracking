package com.dara.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ServicesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        setupButton(R.id.btn_open_account, "Feature coming soon");
        setupButton(R.id.btn_statements, "Statement requested");
        setupButton(R.id.btn_chequebook, "Chequebook requested");
        setupButton(R.id.btn_freeze_card, "Card frozen");
    }

    private void setupButton(int id, String message) {
        Button btn = findViewById(id);
        if (btn != null) {
            btn.setOnClickListener(v -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
        }
    }
}