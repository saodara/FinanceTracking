package com.dara.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CardsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        Button btnAdd = findViewById(R.id.btn_add_card);
        btnAdd.setOnClickListener(v -> 
            Toast.makeText(this, "Card application submitted", Toast.LENGTH_SHORT).show()
        );
    }
}