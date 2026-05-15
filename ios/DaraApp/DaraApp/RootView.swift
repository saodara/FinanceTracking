import SwiftUI

struct RootView: View {
    @EnvironmentObject private var store: FinanceStore

    var body: some View {
        if store.isSignedIn {
            MainTabView()
        } else {
            LoginView()
        }
    }
}
