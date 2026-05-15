import Foundation

enum TransactionType: String, CaseIterable, Codable, Identifiable {
    case income = "Income"
    case expense = "Expense"

    var id: String { rawValue }
}

struct FinanceTransaction: Identifiable, Codable {
    var id = UUID()
    var category: String
    var date: Date
    var amount: Double
    var type: TransactionType
}

struct Budget: Identifiable, Codable {
    var id = UUID()
    var category: String
    var budgetAmount: Double
    var spentAmount: Double
    var currency: String

    var progress: Double {
        guard budgetAmount > 0 else { return 0 }
        return min(spentAmount / budgetAmount, 1)
    }
}

struct SavingsGoal: Identifiable, Codable {
    var id = UUID()
    var name: String
    var targetAmount: Double
    var savedAmount: Double

    var progress: Double {
        guard targetAmount > 0 else { return 0 }
        return min(savedAmount / targetAmount, 1)
    }
}

enum FinanceValidationError: LocalizedError {
    case invalidAmount
    case insufficientBalance

    var errorDescription: String? {
        switch self {
        case .invalidAmount:
            return "Amount must be greater than zero."
        case .insufficientBalance:
            return "Balance not enough."
        }
    }
}
