package pl.pollub.backend.mail.interfaces;

import jakarta.mail.MessagingException;

/**
 * Interface that can be implemented by refined abstractions that send HTML emails.
 */
public interface FullHtmlMail {
    void send() throws MessagingException;

    String getHtml();

    String getSubject();

    String getTo();
}
