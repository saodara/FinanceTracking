package com.dara.app;

import android.graphics.Color;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList;

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_item, parent, false);
        return new TransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.categoryText.setText(transaction.getCategory());
        holder.dateText.setText(transaction.getDate());

        Context context = holder.itemView.getContext();
        String type = transaction.getType();
        if (type != null && type.equalsIgnoreCase("Income")) {
            holder.amountText.setTextColor(Color.GREEN);
            holder.amountText.setText("+ " + CurrencyHelper.formatCurrency(context, Math.abs(transaction.getAmount())));
        } else {
            holder.amountText.setTextColor(Color.RED);
            holder.amountText.setText("- " + CurrencyHelper.formatCurrency(context, Math.abs(transaction.getAmount())));
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryText, dateText, amountText;

        public TransactionViewHolder(View view) {
            super(view);
            categoryText = view.findViewById(R.id.category_text);
            dateText = view.findViewById(R.id.date_text);
            amountText = view.findViewById(R.id.amount_text);
        }
    }
}
