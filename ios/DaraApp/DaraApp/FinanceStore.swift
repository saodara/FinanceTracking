import Foundation

@MainActor
final class FinanceStore: ObservableObject {
    @Published var isSignedIn = false
    @Published var userName = "New User"
    @Published var email = ""
    @Published var transactions: [FinanceTransaction] = []
    @Published var budgets: [Budget] = []
    @Published var savingsGoals: [SavingsGoal] = []

    private let defaults = UserDefaults.standard
    private let transactionsKey = "dara.transactions"
    private let budgetsKey = "dara.budgets"
    private let savingsGoalsKey = "dara.savingsGoals"
    private let sessionKey = "dara.session"

    init() {
        load()
    }

    var totalIncome: Double {
        transactions
            .filter { $0.type == .income }
            .reduce(0) { $0 + abs($1.amount) }
    }

    var totalExpenses: Double {
        transactions
            .filter { $0.type == .expense }
            .reduce(0) { $0 + abs($1.amount) }
    }

    var balance: Double {
        totalIncome - totalExpenses
    }

    func signIn(email: String, password: String) -> Bool {
        guard !email.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty,
              !password.isEmpty else {
            return false
        }
        self.email = email
        self.userName = email.components(separatedBy: "@").first ?? "New User"
        self.isSignedIn = true
        saveSession()
        return true
    }

    func register(name: String, email: String, password: String) -> Bool {
        guard !name.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty,
              !email.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty,
              password.count >= 6 else {
            return false
        }
        self.userName = name
        self.email = email
        self.isSignedIn = true
        saveSession()
        return true
    }

    func logout() {
        isSignedIn = false
        defaults.removeObject(forKey: sessionKey)
    }

    func addTransaction(category: String, date: Date, amount: Double, type: TransactionType) throws {
        guard amount > 0 else { throw FinanceValidationError.invalidAmount }
        if type == .expense && amount > balance {
            throw FinanceValidationError.insufficientBalance
        }

        let signedAmount = type == .expense ? -abs(amount) : abs(amount)
        transactions.insert(
            FinanceTransaction(category: category, date: date, amount: signedAmount, type: type),
            at: 0
        )
        saveTransactions()
    }

    func addBudget(category: String, amount: Double, currency: String) {
        guard amount > 0 else { return }
        budgets.append(Budget(category: category, budgetAmount: amount, spentAmount: 0, currency: currency))
        saveBudgets()
    }

    func addSavingsGoal(name: String, targetAmount: Double, savedAmount: Double) {
        guard !name.isEmpty, targetAmount > 0, savedAmount >= 0 else { return }
        savingsGoals.append(SavingsGoal(name: name, targetAmount: targetAmount, savedAmount: savedAmount))
        saveSavingsGoals()
    }

    func clearTransactions() {
        transactions.removeAll()
        saveTransactions()
    }

    private func load() {
        transactions = decode([FinanceTransaction].self, forKey: transactionsKey) ?? []
        budgets = decode([Budget].self, forKey: budgetsKey) ?? []
        savingsGoals = decode([SavingsGoal].self, forKey: savingsGoalsKey) ?? []

        if let session = decode(Session.self, forKey: sessionKey) {
            userName = session.userName
            email = session.email
            isSignedIn = true
        }
    }

    private func saveSession() {
        encode(Session(userName: userName, email: email), forKey: sessionKey)
    }

    private func saveTransactions() {
        encode(transactions, forKey: transactionsKey)
    }

    private func saveBudgets() {
        encode(budgets, forKey: budgetsKey)
    }

    private func saveSavingsGoals() {
        encode(savingsGoals, forKey: savingsGoalsKey)
    }

    private func encode<T: Encodable>(_ value: T, forKey key: String) {
        if let data = try? JSONEncoder().encode(value) {
            defaults.set(data, forKey: key)
        }
    }

    private func decode<T: Decodable>(_ type: T.Type, forKey key: String) -> T? {
        guard let data = defaults.data(forKey: key) else { return nil }
        return try? JSONDecoder().decode(type, from: data)
    }
}

private struct Session: Codable {
    var userName: String
    var email: String
}
