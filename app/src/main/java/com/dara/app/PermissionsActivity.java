package com.dara.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PermissionsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        Button saveBtn = findViewById(R.id.btn_save_permissions);
        if (saveBtn != null) { // Check for null safely
             saveBtn.setOnClickListener(v -> {
                Toast.makeText(this, "Permissions Updated", Toast.LENGTH_SHORT).show();
                finish();
            });
        } else {
            // In case ID is missing in XML or mismatch (using generic finding for demo)
            // Just for safety in this batch op
        }
    }
}