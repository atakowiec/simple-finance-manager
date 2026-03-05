package pl.pollub.backend.mail;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pollub.backend.mail.interfaces.Mail;

/**
 * Service for sending e-mails. It uses the Bridge design pattern to send e-mails.
 */
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    @Override
    public void sendMail(Mail mailToSend) throws MessagingException {
        mailToSend.send();
    }
}
