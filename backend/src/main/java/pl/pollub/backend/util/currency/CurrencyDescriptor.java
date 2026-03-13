package pl.pollub.backend.util.currency;

import java.util.Currency;
import java.util.Locale;

/**
 * Flyweight value object for currency metadata.
 * Intrinsic state: code, symbol, fraction digits, locale, and Currency instance.
 */
public record CurrencyDescriptor(
        String code,
        String symbol,
        int fractionDigits,
        Locale locale,
        Currency currency
) {
    public static CurrencyDescriptor of(String code, Locale locale) {
        Currency currency = Currency.getInstance(code);
        String symbol = currency.getSymbol(locale);
        int fractionDigits = currency.getDefaultFractionDigits();
        return new CurrencyDescriptor(code, symbol, fractionDigits, locale, currency);
    }
}
