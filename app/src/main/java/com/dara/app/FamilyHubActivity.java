package com.dara.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FamilyHubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_hub);

        RecyclerView membersList = findViewById(R.id.family_members_list);
        membersList.setLayoutManager(new LinearLayoutManager(this));
        
        // Placeholder for members
        // In a real implementation, this would come from Firestore 'groups' collection
        
        findViewById(R.id.btn_invite_member).setOnClickListener(v -> {
            startActivity(new Intent(this, InviteMemberActivity.class));
        });

        findViewById(R.id.btn_contribution_breakdown).setOnClickListener(v -> {
            showContributionDialog();
        });

        findViewById(R.id.btn_permissions_manager).setOnClickListener(v -> {
            startActivity(new Intent(this, PermissionsActivity.class));
        });

        findViewById(R.id.btn_recurring_manager).setOnClickListener(v -> {
             startActivity(new Intent(this, RecurringTransactionsActivity.class));
        });
    }

    private void showContributionDialog() {
        // In a real app, this would aggregate data from the repository
        String[] members = {"You", "Alice", "Bob"};
        String[] contributions = {"$1,200.00", "$850.00", "$400.00"};
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Contribution Breakdown");
        
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < members.length; i++) {
            message.append(members[i]).append(": ").append(contributions[i]).append("\n");
        }
        
        builder.setMessage(message.toString());
        builder.setPositiveButton("Close", null);
        builder.show();
    }
}
