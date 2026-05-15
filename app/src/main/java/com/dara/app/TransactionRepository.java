package com.dara.app;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionRepository {
    private static final String TAG = "TransactionRepository";
    private final MutableLiveData<List<Transaction>> allTransactions = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Budget>> allBudgets = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<SavingsGoal>> allSavingsGoals = new MutableLiveData<>(new ArrayList<>());
    
    private final FirebaseDatabase mDatabase;
    private final FirebaseAuth mAuth;
    private final ExecutorService executorService;

    private ValueEventListener transactionListener;
    private ValueEventListener budgetListener;
    private ValueEventListener savingsListener;

    public TransactionRepository(Application application) {
        // Explicitly set the database URL to match App.java configuration
        mDatabase = FirebaseDatabase.getInstance("https://apps-b9f24-default-rtdb.firebaseio.com");
        
        mAuth = FirebaseAuth.getInstance();
        executorService = Executors.newSingleThreadExecutor();

        // Listen for Auth changes to setup listeners
        mAuth.addAuthStateListener(firebaseAuth -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                setupDatabaseListeners();
            } else {
                removeDatabaseListeners();
                clearLocalData();
            }
        });
    }

    private void removeDatabaseListeners() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;
        
        DatabaseReference userRef = mDatabase.getReference("users").child(user.getUid());
        if (transactionListener != null) {
            userRef.child("transactions").removeEventListener(transactionListener);
            transactionListener = null;
        }
        if (budgetListener != null) {
            userRef.child("budgets").removeEventListener(budgetListener);
            budgetListener = null;
        }
        if (savingsListener != null) {
            userRef.child("savings_goals").removeEventListener(savingsListener);
            savingsListener = null;
        }
    }

    private void setupDatabaseListeners() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        // Ensure we don't add duplicate listeners
        if (transactionListener != null) return;

        DatabaseReference userRef = mDatabase.getReference("users").child(user.getUid());

        // Transactions Listener
        transactionListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Transaction> list = new ArrayList<>();
                for (DataSnapshot doc : snapshot.getChildren()) {
                    try {
                        Transaction t = doc.getValue(Transaction.class);
                        if (t != null) list.add(t);
                    } catch (Exception e) {
                        Log.e(TAG, "Error mapping transaction", e);
                    }
                }
                allTransactions.postValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Transactions listen failed.", error.toException());
            }
        };
        userRef.child("transactions").addValueEventListener(transactionListener);

        // Budgets Listener
        budgetListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Budget> list = new ArrayList<>();
                for (DataSnapshot doc : snapshot.getChildren()) {
                    try {
                        Budget b = doc.getValue(Budget.class);
                        if (b != null) list.add(b);
                    } catch (Exception e) {
                        Log.e(TAG, "Error mapping budget", e);
                    }
                }
                allBudgets.postValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Budgets listen failed.", error.toException());
            }
        };
        userRef.child("budgets").addValueEventListener(budgetListener);

        // Savings Goals Listener
        savingsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<SavingsGoal> list = new ArrayList<>();
                for (DataSnapshot doc : snapshot.getChildren()) {
                    try {
                        SavingsGoal s = doc.getValue(SavingsGoal.class);
                        if (s != null) list.add(s);
                    } catch (Exception e) {
                        Log.e(TAG, "Error mapping savings goal", e);
                    }
                }
                allSavingsGoals.postValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Savings goals listen failed.", error.toException());
            }
        };
        userRef.child("savings_goals").addValueEventListener(savingsListener);
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }

    public LiveData<List<Budget>> getAllBudgets() {
        return allBudgets;
    }

    public LiveData<List<SavingsGoal>> getAllSavingsGoals() {
        return allSavingsGoals;
    }

    public void insertBudget(Budget budget) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mDatabase.getReference("users")
                    .child(user.getUid())
                    .child("budgets")
                    .push()
                    .setValue(budget);
        }
    }

    public void updateBudget(Budget budget) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mDatabase.getReference("users")
                    .child(user.getUid())
                    .child("budgets")
                    .orderByChild("category")
                    .equalTo(budget.getCategory())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot doc : snapshot.getChildren()) {
                                doc.getRef().setValue(budget);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
        }
    }

    public void insertSavingsGoal(SavingsGoal goal) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mDatabase.getReference("users")
                    .child(user.getUid())
                    .child("savings_goals")
                    .push()
                    .setValue(goal);
        }
    }

    public void updateSavingsGoal(SavingsGoal goal) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mDatabase.getReference("users")
                    .child(user.getUid())
                    .child("savings_goals")
                    .orderByChild("name")
                    .equalTo(goal.getName())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot doc : snapshot.getChildren()) {
                                doc.getRef().setValue(goal);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
        }
    }

    public void insert(Transaction transaction) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mDatabase.getReference("users")
                    .child(user.getUid())
                    .child("transactions")
                    .push()
                    .setValue(transaction)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Transaction inserted successfully"))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to insert transaction", e));
        }
    }

    public void deleteAll() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mDatabase.getReference("users")
                    .child(user.getUid())
                    .child("transactions")
                    .removeValue();
        }
    }

    public void deleteTransactionsAboveAmount(double amount) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mDatabase.getReference("users")
                    .child(user.getUid())
                    .child("transactions")
                    .orderByChild("amount")
                    .startAt(amount)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot doc : snapshot.getChildren()) {
                                doc.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
        }
    }

    public void clearLocalData() {
        allTransactions.setValue(new ArrayList<>());
        allBudgets.setValue(new ArrayList<>());
        allSavingsGoals.setValue(new ArrayList<>());
    }

    public double getTotalBalanceSync() {
        List<Transaction> transactions = allTransactions.getValue();
        double balance = 0.0;
        if (transactions != null) {
            for (Transaction t : transactions) {
                balance += t.getAmount();
            }
        }
        return balance;
    }
}
