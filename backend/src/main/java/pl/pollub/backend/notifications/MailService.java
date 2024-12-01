package pl.pollub.backend.notifications;

import jakarta.mail.MessagingException;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;

/**
 * Service for sending e-mails. It uses JavaMailSender to send e-mails.
 */
public interface MailService {
    /**
     * Sends an e-mail to the specified address.
     *
     * @param to            target e-mail address
     * @param subject       e-mail subject
     * @param text          e-mail content
     * @param isHtmlContent whether the content is HTML
     * @throws MessagingException if an error occurs while sending the e-mail
     */
    void sendMail(String to, String subject, String text, boolean isHtmlContent) throws MessagingException;

    /**
     * Checks all the conditions, decides whether to send a warning e-mail and sends it if necessary.
     *
     * @param user  user to whom the e-mail will be sent
     * @param group group for which the e-mail will be sent
     */
    void trySendLimitWarningMail(User user, Group group);
}
