import SwiftUI

struct TransactionHistoryView: View {
    @EnvironmentObject private var store: FinanceStore

    var body: some View {
        List {
            if store.transactions.isEmpty {
                VStack(spacing: 8) {
                    Image(systemName: "tray")
                        .font(.largeTitle)
                        .foregroundStyle(DaraTheme.textSecondary)
                    Text("No Transactions")
                        .font(.headline)
                    Text("Transactions you add will appear here.")
                        .font(.subheadline)
                        .foregroundStyle(DaraTheme.textSecondary)
                }
                .frame(maxWidth: .infinity)
                .padding(.vertical, 40)
            } else {
                ForEach(store.transactions) { transaction in
                    HStack {
                        VStack(alignment: .leading, spacing: 4) {
                            Text(transaction.category)
                                .font(.headline)
                            Text(transaction.date.shortText)
                                .font(.caption)
                                .foregroundStyle(DaraTheme.textSecondary)
                        }

                        Spacer()

                        Text(transaction.amount.moneyText)
                            .font(.subheadline.bold())
                            .foregroundStyle(transaction.type == .income ? .green : DaraTheme.accent)
                    }
                    .padding(.vertical, 4)
                }
            }
        }
        .navigationTitle("History")
        .toolbar {
            if !store.transactions.isEmpty {
                Button("Clean") {
                    store.clearTransactions()
                }
            }
        }
    }
}
