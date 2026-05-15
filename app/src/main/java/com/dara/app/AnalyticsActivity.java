package com.dara.app;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyticsActivity extends AppCompatActivity {

    private PieChart pieChart;
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        pieChart = findViewById(R.id.pie_chart);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.getTransactionList().observe(this, new Observer<List<Transaction>>() {
            @Override
            public void onChanged(List<Transaction> transactions) {
                updateChart(transactions);
            }
        });
    }

    private void updateChart(List<Transaction> transactionList) {
        Map<String, Float> categorySpending = calculateCategorySpending(transactionList);

        if (categorySpending.isEmpty()) {
            pieChart.clear();
            pieChart.setNoDataText("No expense data available");
            return;
        }

        List<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : categorySpending.entrySet()) {
            pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Spending by Category");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Spending");
        pieChart.animateXY(1000, 1000);
        pieChart.invalidate();
    }

    private Map<String, Float> calculateCategorySpending(List<Transaction> transactionList) {
        Map<String, Float> categorySpending = new HashMap<>();
        if (transactionList == null) return categorySpending;
        
        for (Transaction transaction : transactionList) {
            if ("Expense".equalsIgnoreCase(transaction.getType())) {
                String category = transaction.getCategory();
                float amount = (float) Math.abs(transaction.getAmount());
                if (categorySpending.containsKey(category)) {
                    categorySpending.put(category, categorySpending.get(category) + amount);
                } else {
                    categorySpending.put(category, amount);
                }
            }
        }
        return categorySpending;
    }
}