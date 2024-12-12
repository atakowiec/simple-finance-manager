package pl.pollub.backend.mail;

import jakarta.mail.MessagingException;
import pl.pollub.backend.mail.interfaces.Mail;

/**
 * Service for sending e-mails. It uses JavaMailSender to send e-mails.
 */
public interface MailService {
    /**
     * Sends an e-mail to the specified address.
     *
     * @param mailToSend the mail object to send
     * @throws MessagingException if an error occurs while sending the e-mail
     */
    void sendMail(Mail mailToSend) throws MessagingException;
}
