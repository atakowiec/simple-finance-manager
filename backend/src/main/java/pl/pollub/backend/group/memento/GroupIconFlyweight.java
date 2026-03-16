package pl.pollub.backend.group.memento;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

// start flyweight
/**
 * Flyweight that stores canonical group icon snapshot data shared by multiple mementos.
 */
public class GroupIconFlyweight {
	private final byte[] icon;
	private final String contentType;
	private final String checksum;
	private final AtomicInteger referenceCount = new AtomicInteger(1);

	public GroupIconFlyweight(byte[] icon, String contentType, String checksum) {
		this.icon = Arrays.copyOf(icon, icon.length);
		this.contentType = contentType;
		this.checksum = checksum;
	}

	public byte[] getIcon() {
		return Arrays.copyOf(icon, icon.length);
	}

	public String getContentType() {
		return contentType;
	}

	public String getChecksum() {
		return checksum;
	}

	public int retain() {
		return referenceCount.incrementAndGet();
	}

	public int release() {
		return referenceCount.decrementAndGet();
	}

	public int getReferenceCount() {
		return referenceCount.get();
	}
}

