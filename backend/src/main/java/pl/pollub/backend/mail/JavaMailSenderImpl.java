package pl.pollub.backend.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import pl.pollub.backend.mail.interfaces.MailRequest;
import pl.pollub.backend.mail.interfaces.MailSenderImplementation;

/**
 * Concrete implementation of the MailSenderImplementation using Spring's JavaMailSender.
 */
@Component
@RequiredArgsConstructor
public class JavaMailSenderImpl implements MailSenderImplementation {
    private final JavaMailSender javaMailSender;

    @Override
    public void send(MailRequest mailRequest) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(mailRequest.to());
        helper.setSubject(mailRequest.subject());
        helper.setText(mailRequest.body(), mailRequest.html());
        javaMailSender.send(message);
    }
}
