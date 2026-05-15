package com.dara.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class SavingsGoalAdapter extends RecyclerView.Adapter<SavingsGoalAdapter.SavingsGoalViewHolder> {

    private List<SavingsGoal> savingsGoalList;
    private OnSavingsGoalListener onSavingsGoalListener;

    public interface OnSavingsGoalListener {
        void onSavingsGoalClick(int position);
    }

    public SavingsGoalAdapter(List<SavingsGoal> savingsGoalList, OnSavingsGoalListener onSavingsGoalListener) {
        this.savingsGoalList = savingsGoalList;
        this.onSavingsGoalListener = onSavingsGoalListener;
    }

    @NonNull
    @Override
    public SavingsGoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.savings_goal_item, parent, false);
        return new SavingsGoalViewHolder(itemView, onSavingsGoalListener);
    }
@Override
public void onBindViewHolder(@NonNull SavingsGoalViewHolder holder, int position) {
    SavingsGoal savingsGoal = savingsGoalList.get(position);
    holder.goalNameText.setText(savingsGoal.getName());

    android.content.Context context = holder.itemView.getContext();
    holder.goalAmountText.setText(CurrencyHelper.formatCurrency(context, savingsGoal.getTargetAmount()));
    holder.goalProgressText.setText(CurrencyHelper.formatCurrency(context, savingsGoal.getSavedAmount()) 
            + " saved of " + CurrencyHelper.formatCurrency(context, savingsGoal.getTargetAmount()));

    int progress = (int) ((savingsGoal.getSavedAmount() / savingsGoal.getTargetAmount()) * 100);
    holder.goalProgressBar.setProgress(progress);
}


    @Override
    public int getItemCount() {
        return savingsGoalList.size();
    }

    public static class SavingsGoalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView goalNameText, goalProgressText, goalAmountText;
        public ProgressBar goalProgressBar;
        OnSavingsGoalListener onSavingsGoalListener;

        public SavingsGoalViewHolder(View view, OnSavingsGoalListener onSavingsGoalListener) {
            super(view);
            goalNameText = view.findViewById(R.id.goal_name_text);
            goalProgressText = view.findViewById(R.id.goal_progress_text);
            goalAmountText = view.findViewById(R.id.goal_amount_text);
            goalProgressBar = view.findViewById(R.id.goal_progress_bar);
            this.onSavingsGoalListener = onSavingsGoalListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onSavingsGoalListener.onSavingsGoalClick(getAdapterPosition());
        }
    }
}