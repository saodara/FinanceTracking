import SwiftUI

struct BudgetPlannerView: View {
    @EnvironmentObject private var store: FinanceStore
    @State private var category = "Food"
    @State private var amountText = ""
    @State private var currency = "USD"

    private let categories = ["Food", "Transport", "Shopping", "Bills", "Entertainment"]
    private let currencies = ["USD", "EUR", "GBP", "JPY"]

    var body: some View {
        List {
            Section("Add Budget") {
                Picker("Category", selection: $category) {
                    ForEach(categories, id: \.self) { Text($0) }
                }

                Picker("Currency", selection: $currency) {
                    ForEach(currencies, id: \.self) { Text($0) }
                }

                TextField("Budget Amount", text: $amountText)
                    .keyboardType(.decimalPad)

                Button("Add Budget") {
                    if let amount = Double(amountText) {
                        store.addBudget(category: category, amount: amount, currency: currency)
                        amountText = ""
                    }
                }
            }

            Section("Budgets") {
                if store.budgets.isEmpty {
                    Text("No budgets yet.")
                        .foregroundStyle(DaraTheme.textSecondary)
                } else {
                    ForEach(store.budgets) { budget in
                        VStack(alignment: .leading, spacing: 8) {
                            HStack {
                                Text(budget.category)
                                    .font(.headline)
                                Spacer()
                                Text("\(budget.currency) \(budget.budgetAmount, specifier: "%.2f")")
                            }

                            ProgressView(value: budget.progress)
                                .tint(DaraTheme.primary)

                            Text("Spent \(budget.spentAmount, specifier: "%.2f") of \(budget.budgetAmount, specifier: "%.2f")")
                                .font(.caption)
                                .foregroundStyle(DaraTheme.textSecondary)
                        }
                        .padding(.vertical, 4)
                    }
                }
            }
        }
        .navigationTitle("Budget Planner")
    }
}
