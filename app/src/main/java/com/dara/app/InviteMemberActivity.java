package com.dara.app;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class InviteMemberActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_member);

        EditText emailInput = findViewById(R.id.invite_email);
        Spinner roleSpinner = findViewById(R.id.role_spinner);
        Button sendBtn = findViewById(R.id.btn_send_invite);

        String[] roles = {"Viewer", "Member", "Editor", "Admin"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        sendBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Invitation sent to " + email, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}