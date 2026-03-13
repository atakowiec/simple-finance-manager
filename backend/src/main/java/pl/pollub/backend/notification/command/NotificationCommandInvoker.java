package pl.pollub.backend.notification.command;

/**
 * Invoker for notification commands.
 */
public class NotificationCommandInvoker {
    public void execute(NotificationCommand command) {
        command.execute();
    }
}
