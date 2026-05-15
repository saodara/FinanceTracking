import SwiftUI

enum DaraTheme {
    static let primary = Color(red: 0.0, green: 0.424, blue: 0.537)
    static let primaryDark = Color(red: 0.0, green: 0.310, blue: 0.388)
    static let secondary = Color(red: 0.0, green: 0.173, blue: 0.243)
    static let accent = Color(red: 0.933, green: 0.243, blue: 0.263)
    static let background = Color(red: 0.961, green: 0.969, blue: 0.980)
    static let textSecondary = Color(red: 0.424, green: 0.459, blue: 0.490)
}

extension Double {
    var moneyText: String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.currencyCode = "USD"
        return formatter.string(from: NSNumber(value: self)) ?? "$0.00"
    }
}

extension Date {
    var shortText: String {
        formatted(date: .abbreviated, time: .omitted)
    }
}
