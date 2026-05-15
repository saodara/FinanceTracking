import SwiftUI

struct SavingsGoalsView: View {
    @EnvironmentObject private var store: FinanceStore
    @State private var name = ""
    @State private var targetAmountText = ""
    @State private var savedAmountText = ""

    var body: some View {
        List {
            Section("Add Goal") {
                TextField("Goal Name", text: $name)
                TextField("Target Amount", text: $targetAmountText)
                    .keyboardType(.decimalPad)
                TextField("Saved Amount", text: $savedAmountText)
                    .keyboardType(.decimalPad)

                Button("Add Goal") {
                    let target = Double(targetAmountText) ?? 0
                    let saved = Double(savedAmountText) ?? 0
                    store.addSavingsGoal(name: name, targetAmount: target, savedAmount: saved)
                    name = ""
                    targetAmountText = ""
                    savedAmountText = ""
                }
            }

            Section("Goals") {
                if store.savingsGoals.isEmpty {
                    Text("No savings goals yet.")
                        .foregroundStyle(DaraTheme.textSecondary)
                } else {
                    ForEach(store.savingsGoals) { goal in
                        VStack(alignment: .leading, spacing: 8) {
                            Text(goal.name)
                                .font(.headline)
                            ProgressView(value: goal.progress)
                                .tint(DaraTheme.primary)
                            Text("\(goal.savedAmount.moneyText) / \(goal.targetAmount.moneyText)")
                                .font(.caption)
                                .foregroundStyle(DaraTheme.textSecondary)
                        }
                        .padding(.vertical, 4)
                    }
                }
            }
        }
        .navigationTitle("Savings Goals")
    }
}
