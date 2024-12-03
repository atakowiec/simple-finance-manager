package pl.pollub.backend.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pl.pollub.backend.mail.interfaces.HtmlHolder;
import pl.pollub.backend.mail.interfaces.Mail;
import pl.pollub.backend.mail.interfaces.SubjectHolder;
import pl.pollub.backend.mail.interfaces.TextHolder;

/**
 * Service for sending e-mails. It uses JavaMailSender to send e-mails.
 */
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender javaMailSender;

    @Override
    public void sendMail(Mail<?> mailToSend) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        mailToSend.applyTo(mimeMessage);
        javaMailSender.send(mimeMessage);
    }

    /**
     * Not used another approach to send mail. It uses interfactes
     * @param mailToSend the mail object to send
     * @throws MessagingException if an error occurs while sending the e-mail
     */
    public void anotherSendMail(Mail<?> mailToSend) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

        mimeMessageHelper.setTo(mailToSend.getTo());

        if(mailToSend instanceof SubjectHolder<?> subjectHolder) {
            mimeMessageHelper.setSubject(subjectHolder.getSubject());
        }

        if (mailToSend instanceof HtmlHolder<?> htmlHolder) {
            mimeMessageHelper.setText(htmlHolder.getHtml(), true);
        }

        if (mailToSend instanceof TextHolder<?> textHolder) {
            mimeMessageHelper.setText(textHolder.getText(), false);
        }

        javaMailSender.send(mimeMessage);
    }
}
