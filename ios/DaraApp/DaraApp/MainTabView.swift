import SwiftUI

struct MainTabView: View {
    var body: some View {
        TabView {
            NavigationStack {
                DashboardView()
            }
            .tabItem {
                Label("Home", systemImage: "house")
            }

            NavigationStack {
                TransactionHistoryView()
            }
            .tabItem {
                Label("History", systemImage: "list.bullet.rectangle")
            }

            NavigationStack {
                AnalyticsView()
            }
            .tabItem {
                Label("Analytics", systemImage: "chart.pie")
            }

            NavigationStack {
                ProfileView()
            }
            .tabItem {
                Label("Profile", systemImage: "person.crop.circle")
            }
        }
        .tint(DaraTheme.primary)
    }
}
