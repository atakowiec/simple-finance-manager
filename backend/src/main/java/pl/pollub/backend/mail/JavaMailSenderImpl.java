package pl.pollub.backend.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import pl.pollub.backend.mail.interfaces.MailSenderImplementation;

/**
 * Concrete implementation of the MailSenderImplementation using Spring's JavaMailSender.
 */
@Component
@RequiredArgsConstructor
public class JavaMailSenderImpl implements MailSenderImplementation {
    private final JavaMailSender javaMailSender;

    @Override
    public void send(String to, String subject, String body, boolean isHtml) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, isHtml);
        javaMailSender.send(message);
    }
}
