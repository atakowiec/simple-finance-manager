package pl.pollub.backend.util;

import pl.pollub.backend.util.currency.CurrencyDescriptor;
import pl.pollub.backend.util.currency.CurrencyDescriptorFlyweightFactory;

import java.text.NumberFormat;

// start singleton
/**
 * Singleton responsible for consistent currency formatting across the application.
 */
public final class CurrencyFormatter {
    private static final CurrencyFormatter INSTANCE = new CurrencyFormatter();
    private static final CurrencyDescriptor DEFAULT_DESCRIPTOR =
            CurrencyDescriptorFlyweightFactory.getDefault();

    private CurrencyFormatter() {
    }

    public static CurrencyFormatter getInstance() {
        return INSTANCE;
    }

    public String format(Double amount) {
        return format(amount, DEFAULT_DESCRIPTOR);
    }

    public String format(Double amount, CurrencyDescriptor descriptor) {
        CurrencyDescriptor resolved = descriptor != null ? descriptor : DEFAULT_DESCRIPTOR;
        double value = amount != null ? amount.doubleValue() : 0.0;
        NumberFormat formatter = NumberFormat.getCurrencyInstance(resolved.locale());
        formatter.setCurrency(resolved.currency());
        formatter.setMinimumFractionDigits(resolved.fractionDigits());
        formatter.setMaximumFractionDigits(resolved.fractionDigits());
        return formatter.format(value);
    }
}
