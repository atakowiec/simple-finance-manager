package pl.pollub.backend.mail.decorators;

import jakarta.mail.MessagingException;
import pl.pollub.backend.mail.interfaces.MailRequest;
import pl.pollub.backend.mail.interfaces.MailSenderImplementation;

/**
 * Decorator that retries mail sending on failure.
 */
public class RetryMailSenderDecorator implements MailSenderImplementation {
    private final MailSenderImplementation delegate;
    private final int maxAttempts;
    private final long delayMillis;

    public RetryMailSenderDecorator(MailSenderImplementation delegate, int maxAttempts, long delayMillis) {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be >= 1");
        }
        if (delayMillis < 0) {
            throw new IllegalArgumentException("delayMillis must be >= 0");
        }
        this.delegate = delegate;
        this.maxAttempts = maxAttempts;
        this.delayMillis = delayMillis;
    }

    @Override
    public void send(MailRequest mailRequest) throws MessagingException {
        MessagingException lastException = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                delegate.send(mailRequest);
                return;
            } catch (MessagingException ex) {
                lastException = ex;
                if (attempt == maxAttempts) {
                    throw ex;
                }
                sleepBeforeRetry();
            }
        }

        if (lastException != null) {
            throw lastException;
        }
    }

    private void sleepBeforeRetry() throws MessagingException {
        if (delayMillis <= 0) {
            return;
        }
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MessagingException("Mail retry interrupted", e);
        }
    }
}
