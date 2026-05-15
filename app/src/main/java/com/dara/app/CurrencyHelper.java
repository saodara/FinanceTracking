package com.dara.app;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class CurrencyHelper {

    private static final String PREFS_NAME = "Settings";
    private static final String KEY_CURRENCY = "My_Currency";

    public static String formatCurrency(Context context, double amount) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String currencyCode = prefs.getString(KEY_CURRENCY, "USD");
        
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        
        switch (currencyCode) {
            case "KHR":
                format.setCurrency(Currency.getInstance("KHR"));
                return "៛" + String.format(Locale.US, "%,.0f", amount);
            case "CNY":
                return "¥" + String.format(Locale.US, "%,.2f", amount);
            case "EUR":
                return "€" + String.format(Locale.US, "%,.2f", amount);
            default:
                return "$" + String.format(Locale.US, "%,.2f", amount);
        }
    }

    public static String getCurrencySymbol(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String currencyCode = prefs.getString(KEY_CURRENCY, "USD");
        switch (currencyCode) {
            case "KHR": return "៛";
            case "CNY": return "¥";
            case "EUR": return "€";
            default: return "$";
        }
    }

    public static void setCurrency(Context context, String currencyCode) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_CURRENCY, currencyCode);
        editor.apply();
    }
    
    public static String getCurrencyCode(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_CURRENCY, "USD");
    }
}
