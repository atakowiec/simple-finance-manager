package pl.pollub.backend.group.memento;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Flyweight factory responsible for sharing immutable icon snapshots across group mementos.
 */
public final class GroupIconFlyweightFactory {
	private static final Map<String, GroupIconFlyweight> CACHE = new ConcurrentHashMap<>();

	private GroupIconFlyweightFactory() {
	}

	public static GroupIconFlyweight acquire(byte[] icon, String contentType) {
		if (icon == null || icon.length == 0) {
			return null;
		}

		String normalizedContentType = normalizeContentType(contentType);
		String checksum = checksum(icon);
		String key = buildKey(checksum, normalizedContentType);

		return CACHE.compute(key, (ignored, existing) -> {
			if (existing != null) {
				existing.retain();
				return existing;
			}
			return new GroupIconFlyweight(Arrays.copyOf(icon, icon.length), normalizedContentType, checksum);
		});
	}

	public static void release(GroupIconFlyweight flyweight) {
		if (flyweight == null) {
			return;
		}

		String key = buildKey(flyweight.getChecksum(), flyweight.getContentType());
		CACHE.computeIfPresent(key, (ignored, existing) -> existing.release() <= 0 ? null : existing);
	}

	public static int getCacheSize() {
		return CACHE.size();
	}

	public static int getRefCount(String checksum, String contentType) {
		GroupIconFlyweight flyweight = CACHE.get(buildKey(checksum, normalizeContentType(contentType)));
		return flyweight == null ? 0 : flyweight.getReferenceCount();
	}

	public static void clear() {
		CACHE.clear();
	}

	private static String buildKey(String checksum, String contentType) {
		return contentType + ':' + checksum;
	}

	private static String normalizeContentType(String contentType) {
		if (contentType == null || contentType.isBlank()) {
			return "application/octet-stream";
		}
		return contentType.trim().toLowerCase();
	}

	private static String checksum(byte[] icon) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(icon);
			StringBuilder builder = new StringBuilder(hash.length * 2);
			for (byte value : hash) {
				builder.append(String.format("%02x", value));
			}
			return builder.toString();
		} catch (NoSuchAlgorithmException exception) {
			return String.valueOf(Arrays.hashCode(icon));
		}
	}
}

