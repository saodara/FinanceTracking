import SwiftUI

struct AnalyticsView: View {
    @EnvironmentObject private var store: FinanceStore

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 18) {
                StatRow(title: "Income", value: store.totalIncome, color: .green, maxValue: maxValue)
                StatRow(title: "Expenses", value: store.totalExpenses, color: DaraTheme.accent, maxValue: maxValue)
                StatRow(title: "Balance", value: max(store.balance, 0), color: DaraTheme.primary, maxValue: maxValue)

                VStack(alignment: .leading, spacing: 8) {
                    Text("Recent Categories")
                        .font(.headline)

                    ForEach(categoryTotals, id: \.name) { item in
                        HStack {
                            Text(item.name)
                            Spacer()
                            Text(item.amount.moneyText)
                                .foregroundStyle(DaraTheme.textSecondary)
                        }
                        Divider()
                    }
                }
                .padding(16)
                .background(.white)
                .clipShape(RoundedRectangle(cornerRadius: 8))
            }
            .padding()
        }
        .background(DaraTheme.background)
        .navigationTitle("Analytics")
    }

    private var maxValue: Double {
        max(store.totalIncome, store.totalExpenses, store.balance, 1)
    }

    private var categoryTotals: [(name: String, amount: Double)] {
        let grouped = Dictionary(grouping: store.transactions, by: { $0.category })
        return grouped.map { key, transactions in
            (key, transactions.reduce(0) { $0 + abs($1.amount) })
        }
        .sorted { $0.amount > $1.amount }
    }
}

private struct StatRow: View {
    var title: String
    var value: Double
    var color: Color
    var maxValue: Double

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text(title)
                    .font(.headline)
                Spacer()
                Text(value.moneyText)
                    .font(.subheadline.bold())
            }

            GeometryReader { proxy in
                RoundedRectangle(cornerRadius: 4)
                    .fill(color)
                    .frame(width: proxy.size.width * min(value / maxValue, 1))
            }
            .frame(height: 10)
            .background(Color.black.opacity(0.08))
            .clipShape(RoundedRectangle(cornerRadius: 4))
        }
        .padding(16)
        .background(.white)
        .clipShape(RoundedRectangle(cornerRadius: 8))
    }
}
