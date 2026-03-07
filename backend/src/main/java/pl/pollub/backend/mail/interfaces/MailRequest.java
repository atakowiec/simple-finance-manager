package pl.pollub.backend.mail.interfaces;

/**
 * Value object describing one outbound mail message.
 */
public record MailRequest(
        String to,
        String subject,
        String body,
        boolean html
) {
}

