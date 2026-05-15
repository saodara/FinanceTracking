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

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    private List<Budget> budgetList;
    private OnBudgetListener onBudgetListener;

    public interface OnBudgetListener {
        void onBudgetClick(int position);
    }

    public BudgetAdapter(List<Budget> budgetList, OnBudgetListener onBudgetListener) {
        this.budgetList = budgetList;
        this.onBudgetListener = onBudgetListener;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.budget_item, parent, false);
        return new BudgetViewHolder(itemView, onBudgetListener);
    }
@Override
public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
    Budget budget = budgetList.get(position);
    holder.budgetCategoryName.setText(budget.getCategory());

    android.content.Context context = holder.itemView.getContext();
    holder.budgetAmountText.setText(CurrencyHelper.formatCurrency(context, budget.getBudgetAmount()));
    holder.budgetSpendingText.setText(CurrencyHelper.formatCurrency(context, budget.getSpentAmount()) 
            + " spent of " + CurrencyHelper.formatCurrency(context, budget.getBudgetAmount()));

    int progress = (int) ((budget.getSpentAmount() / budget.getBudgetAmount()) * 100);
    holder.budgetProgressBar.setProgress(progress);
}


    @Override
    public int getItemCount() {
        return budgetList.size();
    }

    public static class BudgetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView budgetCategoryName, budgetSpendingText, budgetAmountText;
        public ProgressBar budgetProgressBar;
        OnBudgetListener onBudgetListener;

        public BudgetViewHolder(View view, OnBudgetListener onBudgetListener) {
            super(view);
            budgetCategoryName = view.findViewById(R.id.budget_category_name);
            budgetSpendingText = view.findViewById(R.id.budget_spending_text);
            budgetAmountText = view.findViewById(R.id.budget_amount_text);
            budgetProgressBar = view.findViewById(R.id.budget_progress_bar);
            this.onBudgetListener = onBudgetListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onBudgetListener.onBudgetClick(getAdapterPosition());
        }
    }
}