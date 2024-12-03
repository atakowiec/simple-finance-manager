package pl.pollub.backend.mail.interfaces;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public interface Mail<T extends Mail<T>> {
    void applyTo(MimeMessage mimeMessage) throws MessagingException;

    String getTo();
}
