package pl.pollub.backend.mail.decorators;

import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pollub.backend.mail.interfaces.MailRequest;
import pl.pollub.backend.mail.interfaces.MailSenderImplementation;

/**
 * Decorator that logs outgoing mail attempts and results.
 */
public class LoggingMailSenderDecorator implements MailSenderImplementation {
    private static final Logger logger = LoggerFactory.getLogger(LoggingMailSenderDecorator.class);

    private final MailSenderImplementation delegate;

    public LoggingMailSenderDecorator(MailSenderImplementation delegate) {
        this.delegate = delegate;
    }

    @Override
    public void send(MailRequest mailRequest) throws MessagingException {
        logger.info("Sending mail to={} subject='{}'", mailRequest.to(), mailRequest.subject());
        try {
            delegate.send(mailRequest);
            logger.info("Mail sent to={} subject='{}'", mailRequest.to(), mailRequest.subject());
        } catch (MessagingException ex) {
            logger.warn("Mail failed to send to={} subject='{}'", mailRequest.to(), mailRequest.subject(), ex);
            throw ex;
        }
    }
}
