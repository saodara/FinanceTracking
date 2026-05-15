import SwiftUI

struct ProfileView: View {
    @EnvironmentObject private var store: FinanceStore

    var body: some View {
        VStack(spacing: 18) {
            Circle()
                .fill(DaraTheme.primary)
                .frame(width: 96, height: 96)
                .overlay {
                    Text(initials)
                        .font(.title.bold())
                        .foregroundStyle(.white)
                }

            Text(store.userName)
                .font(.title2.bold())

            Text(store.email)
                .foregroundStyle(DaraTheme.textSecondary)

            Text("UID: local-ios")
                .font(.caption.bold())
                .padding(.horizontal, 12)
                .padding(.vertical, 6)
                .background(DaraTheme.primary.opacity(0.12))
                .foregroundStyle(DaraTheme.primaryDark)
                .clipShape(Capsule())

            Button("Log Out", role: .destructive) {
                store.logout()
            }
            .buttonStyle(.bordered)
            .padding(.top, 16)

            Spacer()
        }
        .frame(maxWidth: .infinity)
        .padding(24)
        .background(DaraTheme.background)
        .navigationTitle("Profile")
    }

    private var initials: String {
        let parts = store.userName.split(separator: " ")
        let letters = parts.prefix(2).compactMap { $0.first }
        return letters.isEmpty ? "U" : String(letters).uppercased()
    }
}
