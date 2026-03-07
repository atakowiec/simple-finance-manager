package pl.pollub.backend.mail.interfaces;

import jakarta.mail.MessagingException;

// start dependency inversion principle
// start interface segregation principle
/**
 * Start bridge
 * Implementor interface for the Bridge design pattern.
 * It defines the method to send mail independently of the mail content abstraction.
 */
public interface MailSenderImplementation {
    void send(String to, String subject, String body, boolean isHtml) throws MessagingException;
}
