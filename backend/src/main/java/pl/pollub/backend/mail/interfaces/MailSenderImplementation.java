package pl.pollub.backend.mail.interfaces;

import jakarta.mail.MessagingException;

// start bridge
// start decorator
/**
 * Implementor interface for the Bridge design pattern.
 * It defines the method to send mail independently of the mail content abstraction.
 */
public interface MailSenderImplementation {
    void send(MailRequest mailRequest) throws MessagingException;
}
