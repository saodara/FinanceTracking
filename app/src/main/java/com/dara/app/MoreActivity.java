package com.dara.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MoreActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        setupButton(R.id.btn_locations, "Opening Maps...");
        setupButton(R.id.btn_contact, "Calling Support...");
        setupButton(R.id.btn_terms, "Loading Terms...");
        setupButton(R.id.btn_privacy, "Loading Policy...");
    }

    private void setupButton(int id, String message) {
        Button btn = findViewById(id);
        if (btn != null) {
            btn.setOnClickListener(v -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
        }
    }
}