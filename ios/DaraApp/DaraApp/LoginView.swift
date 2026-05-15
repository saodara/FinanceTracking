import SwiftUI

struct LoginView: View {
    @EnvironmentObject private var store: FinanceStore
    @State private var email = ""
    @State private var password = ""
    @State private var name = ""
    @State private var isRegistering = false
    @State private var errorMessage = ""

    var body: some View {
        VStack(spacing: 24) {
            VStack(alignment: .leading, spacing: 8) {
                Text("Dara")
                    .font(.largeTitle.bold())
                    .foregroundStyle(DaraTheme.primary)
                Text(isRegistering ? "Create your account" : "Sign in to continue")
                    .foregroundStyle(DaraTheme.textSecondary)
            }
            .frame(maxWidth: .infinity, alignment: .leading)

            VStack(spacing: 14) {
                if isRegistering {
                    TextField("Name", text: $name)
                        .textContentType(.name)
                        .textFieldStyle(.roundedBorder)
                }

                TextField("Email", text: $email)
                    .textContentType(.emailAddress)
                    .keyboardType(.emailAddress)
                    .textInputAutocapitalization(.never)
                    .textFieldStyle(.roundedBorder)

                SecureField("Password", text: $password)
                    .textContentType(isRegistering ? .newPassword : .password)
                    .textFieldStyle(.roundedBorder)
            }

            if !errorMessage.isEmpty {
                Text(errorMessage)
                    .font(.footnote)
                    .foregroundStyle(DaraTheme.accent)
                    .frame(maxWidth: .infinity, alignment: .leading)
            }

            Button(action: submit) {
                Text(isRegistering ? "Create Account" : "Login")
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderedProminent)
            .tint(DaraTheme.primary)

            Button(isRegistering ? "Back to Login" : "Create a New Account") {
                errorMessage = ""
                isRegistering.toggle()
            }
            .foregroundStyle(DaraTheme.primary)

            Spacer()
        }
        .padding(24)
        .background(DaraTheme.background)
    }

    private func submit() {
        let success = isRegistering
            ? store.register(name: name, email: email, password: password)
            : store.signIn(email: email, password: password)

        if !success {
            errorMessage = isRegistering
                ? "Enter a name, email, and password of at least 6 characters."
                : "Email and password are required."
        }
    }
}
