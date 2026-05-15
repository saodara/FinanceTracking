import SwiftUI

struct TransactionEntryView: View {
    @EnvironmentObject private var store: FinanceStore
    @Environment(\.dismiss) private var dismiss
    @State private var type: TransactionType = .expense
    @State private var category = "Food"
    @State private var date = Date()
    @State private var amountText = ""
    @State private var errorMessage = ""

    private let categories = ["Food", "Transport", "Shopping", "Bills", "Entertainment", "Salary", "Freelance"]

    var body: some View {
        Form {
            Picker("Type", selection: $type) {
                ForEach(TransactionType.allCases) { item in
                    Text(item.rawValue).tag(item)
                }
            }
            .pickerStyle(.segmented)

            Picker("Category", selection: $category) {
                ForEach(categories, id: \.self) { item in
                    Text(item)
                }
            }

            DatePicker("Date", selection: $date, displayedComponents: .date)

            TextField("Amount", text: $amountText)
                .keyboardType(.decimalPad)

            if !errorMessage.isEmpty {
                Text(errorMessage)
                    .foregroundStyle(DaraTheme.accent)
            }

            Button("Save Transaction") {
                save()
            }
            .frame(maxWidth: .infinity)
        }
        .navigationTitle("New Transaction")
    }

    private func save() {
        guard let amount = Double(amountText) else {
            errorMessage = "Please enter a valid amount."
            return
        }

        do {
            try store.addTransaction(category: category, date: date, amount: amount, type: type)
            dismiss()
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}
