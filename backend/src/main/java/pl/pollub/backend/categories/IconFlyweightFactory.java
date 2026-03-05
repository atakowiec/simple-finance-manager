package pl.pollub.backend.categories;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// start flyweight
/**
 * Flyweight factory for icon byte[] instances. Returns a shared byte[] for identical icon content.
 */
@Component
public class IconFlyweightFactory {
    private final ConcurrentMap<String, byte[]> cache = new ConcurrentHashMap<>();

    public byte[] getOrAdd(byte[] icon) {
        if (icon == null) return null;
        String key = sha256Hex(icon);
        // computeIfAbsent will store the given byte[] as the canonical instance
        return cache.computeIfAbsent(key, k -> icon);
    }

    private String sha256Hex(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(data);
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // Fallback: use identity-based key (not expected)
            return String.valueOf(java.util.Arrays.hashCode(data));
        }
    }
}

