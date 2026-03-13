package pl.pollub.backend.util.currency;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Flyweight factory for CurrencyDescriptor instances.
 * Returns shared descriptors for the same currency code + locale.
 */
public final class CurrencyDescriptorFlyweightFactory {
    private static final Locale DEFAULT_LOCALE = new Locale("pl", "PL");
    private static final String DEFAULT_CODE = "PLN";
    private static final ConcurrentMap<String, CurrencyDescriptor> CACHE = new ConcurrentHashMap<>();

    private CurrencyDescriptorFlyweightFactory() {
    }

    public static CurrencyDescriptor getDefault() {
        return get(DEFAULT_CODE, DEFAULT_LOCALE);
    }

    public static CurrencyDescriptor get(String code) {
        return get(code, DEFAULT_LOCALE);
    }

    public static CurrencyDescriptor get(String code, Locale locale) {
        String key = code + "|" + locale.toLanguageTag();
        return CACHE.computeIfAbsent(key, k -> CurrencyDescriptor.of(code, locale));
    }
}
