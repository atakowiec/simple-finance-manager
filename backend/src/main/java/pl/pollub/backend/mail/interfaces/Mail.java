package pl.pollub.backend.mail.interfaces;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public interface Mail {
    void applyTo(MimeMessage mimeMessage) throws MessagingException;

    String getTo();
}
