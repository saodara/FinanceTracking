import SwiftUI

struct DashboardView: View {
    @EnvironmentObject private var store: FinanceStore

    var body: some View {
        ScrollView {
            VStack(spacing: 18) {
                balanceCard
                quickActions
                toolList
            }
            .padding()
        }
        .background(DaraTheme.background)
        .navigationTitle("Home")
    }

    private var balanceCard: some View {
        VStack(alignment: .leading, spacing: 18) {
            Text("Current Balance")
                .font(.subheadline)
                .foregroundStyle(.white.opacity(0.8))

            Text(store.balance.moneyText)
                .font(.system(size: 34, weight: .bold))
                .foregroundStyle(.white)

            HStack(spacing: 12) {
                SummaryPill(title: "Income", value: store.totalIncome.moneyText, color: .green)
                SummaryPill(title: "Expenses", value: store.totalExpenses.moneyText, color: DaraTheme.accent)
            }
        }
        .padding(20)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(DaraTheme.primary)
        .clipShape(RoundedRectangle(cornerRadius: 8))
    }

    private var quickActions: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Quick Actions")
                .font(.headline)

            LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: 12) {
                NavigationLink {
                    TransactionEntryView()
                } label: {
                    ActionTile(title: "New Transaction", icon: "plus.circle")
                }

                NavigationLink {
                    BudgetPlannerView()
                } label: {
                    ActionTile(title: "Budget Planner", icon: "chart.bar")
                }

                NavigationLink {
                    SavingsGoalsView()
                } label: {
                    ActionTile(title: "Savings Goals", icon: "target")
                }

                NavigationLink {
                    TransactionHistoryView()
                } label: {
                    ActionTile(title: "View History", icon: "clock")
                }
            }
        }
    }

    private var toolList: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Tools")
                .font(.headline)

            NavigationLink("Analytics", destination: AnalyticsView())
            NavigationLink("Budget Planner", destination: BudgetPlannerView())
            NavigationLink("Savings Goals", destination: SavingsGoalsView())
        }
        .buttonStyle(.borderless)
        .foregroundStyle(DaraTheme.primary)
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(16)
        .background(.white)
        .clipShape(RoundedRectangle(cornerRadius: 8))
    }
}

private struct SummaryPill: View {
    var title: String
    var value: String
    var color: Color

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title)
                .font(.caption)
                .foregroundStyle(.white.opacity(0.75))
            Text(value)
                .font(.subheadline.bold())
                .foregroundStyle(color)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

private struct ActionTile: View {
    var title: String
    var icon: String

    var body: some View {
        VStack(spacing: 10) {
            Image(systemName: icon)
                .font(.title2)
            Text(title)
                .font(.subheadline.weight(.semibold))
                .multilineTextAlignment(.center)
        }
        .foregroundStyle(DaraTheme.primary)
        .frame(maxWidth: .infinity, minHeight: 96)
        .background(.white)
        .clipShape(RoundedRectangle(cornerRadius: 8))
    }
}
