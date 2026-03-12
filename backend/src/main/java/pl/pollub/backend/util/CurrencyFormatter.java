package pl.pollub.backend.util;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

// start singleton
/**
 * Singleton responsible for consistent currency formatting across the application.
 */
public final class CurrencyFormatter {
    private static final CurrencyFormatter INSTANCE = new CurrencyFormatter();
    private static final Locale DEFAULT_LOCALE = new Locale("pl", "PL");
    private static final Currency DEFAULT_CURRENCY = Currency.getInstance("PLN");

    private CurrencyFormatter() {
    }

    public static CurrencyFormatter getInstance() {
        return INSTANCE;
    }

    public String format(Double amount) {
        if (amount == null) {
            return format(0.0);
        }
        return format(amount.doubleValue());
    }

    private String format(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(DEFAULT_LOCALE);
        formatter.setCurrency(DEFAULT_CURRENCY);
        return formatter.format(amount);
    }
}
