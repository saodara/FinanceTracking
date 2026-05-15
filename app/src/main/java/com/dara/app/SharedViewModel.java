package com.dara.app;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class SharedViewModel extends AndroidViewModel {
    private TransactionRepository repository;
    private LiveData<List<Transaction>> allTransactions;
    private LiveData<List<Budget>> allBudgets;
    private LiveData<List<SavingsGoal>> allSavingsGoals;

    public SharedViewModel(Application application) {
        super(application);
        repository = new TransactionRepository(application);
        allTransactions = repository.getAllTransactions();
        allBudgets = repository.getAllBudgets();
        allSavingsGoals = repository.getAllSavingsGoals();
    }

    public LiveData<List<Transaction>> getTransactionList() {
        return allTransactions;
    }

    public LiveData<List<Budget>> getBudgetList() {
        return allBudgets;
    }

    public LiveData<List<SavingsGoal>> getSavingsGoalList() {
        return allSavingsGoals;
    }

    public void addTransaction(Transaction transaction) {
        repository.insert(transaction);
    }

    public void addBudget(Budget budget) {
        repository.insertBudget(budget);
    }

    public void updateBudget(Budget budget) {
        repository.updateBudget(budget);
    }

    public void addSavingsGoal(SavingsGoal goal) {
        repository.insertSavingsGoal(goal);
    }

    public void updateSavingsGoal(SavingsGoal goal) {
        repository.updateSavingsGoal(goal);
    }

    public void clearTransactions() {
        repository.deleteAll();
    }

    public void clearTransactionsAboveAmount(double amount) {
        repository.deleteTransactionsAboveAmount(amount);
    }

    public void clearLocalData() {
        repository.clearLocalData();
    }

    public double getCurrentBalanceSync() {
        return repository.getTotalBalanceSync();
    }
}