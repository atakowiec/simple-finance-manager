package pl.pollub.backend.mail.interfaces;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

/**
 * Abstraction in the Bridge design pattern.
 */
@RequiredArgsConstructor
public abstract class Mail {
    protected final MailSenderImplementation sender;

    public abstract void send() throws MessagingException;

    public abstract String getTo();
}
