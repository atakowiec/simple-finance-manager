package pl.pollub.backend.group.deletion;

import lombok.Data;

/**
 * Context object shared by group deletion handlers.
 */
@Data
public class GroupDeletionContext {
    private int cleanupStepsExecuted;
}

