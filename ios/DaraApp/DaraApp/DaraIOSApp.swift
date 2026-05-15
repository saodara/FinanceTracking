import SwiftUI

@main
struct DaraIOSApp: App {
    @StateObject private var store = FinanceStore()

    var body: some Scene {
        WindowGroup {
            RootView()
                .environmentObject(store)
        }
    }
}
