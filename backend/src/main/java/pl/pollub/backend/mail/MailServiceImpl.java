package pl.pollub.backend.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import pl.pollub.backend.mail.interfaces.Mail;

/**
 * Service for sending e-mails. It uses JavaMailSender to send e-mails.
 */
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender javaMailSender;

    @Override
    public void sendMail(Mail mailToSend) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        mailToSend.applyTo(mimeMessage);
        javaMailSender.send(mimeMessage);
    }
}
