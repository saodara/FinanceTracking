package com.dara.app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SavingsGoalsActivity extends AppCompatActivity implements SavingsGoalAdapter.OnSavingsGoalListener {

    private RecyclerView savingsGoalsRecyclerView;
    private SavingsGoalAdapter savingsGoalAdapter;
    private List<SavingsGoal> savingsGoalList;
    private Button addGoalButton;
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings_goals);

        savingsGoalsRecyclerView = findViewById(R.id.savings_goals_list);
        addGoalButton = findViewById(R.id.add_goal_button);

        savingsGoalList = new ArrayList<>();
        savingsGoalAdapter = new SavingsGoalAdapter(savingsGoalList, this);
        savingsGoalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        savingsGoalsRecyclerView.setAdapter(savingsGoalAdapter);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        
        sharedViewModel.getSavingsGoalList().observe(this, goals -> {
            if (goals != null) {
                savingsGoalList.clear();
                savingsGoalList.addAll(goals);
                savingsGoalAdapter.notifyDataSetChanged();
            }
        });

        addGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddGoalDialog();
            }
        });
    }

    private void showAddGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Savings Goal");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_savings_goal, null);
        final EditText goalNameEditText = view.findViewById(R.id.dialog_goal_name_edittext);
        final EditText targetAmountEditText = view.findViewById(R.id.dialog_target_amount_edittext);

        builder.setView(view);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String goalName = goalNameEditText.getText().toString();
                String targetAmount = targetAmountEditText.getText().toString();
                if (!goalName.isEmpty() && !targetAmount.isEmpty()) {
                    sharedViewModel.addSavingsGoal(new SavingsGoal(goalName, Double.parseDouble(targetAmount), 0));
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
    public void onSavingsGoalClick(int position) {
        showEditGoalDialog(position);
    }

    private void showEditGoalDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Savings Goal");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_savings_goal, null);
        final EditText savedAmountEditText = view.findViewById(R.id.dialog_saved_amount_edittext);

        SavingsGoal savingsGoal = savingsGoalList.get(position);
        savedAmountEditText.setText(String.valueOf(savingsGoal.getSavedAmount()));

        builder.setView(view);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String savedAmount = savedAmountEditText.getText().toString();
                if (!savedAmount.isEmpty()) {
                    savingsGoal.setSavedAmount(Double.parseDouble(savedAmount));
                    sharedViewModel.updateSavingsGoal(savingsGoal);
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