package pl.pollub.backend.mail.interfaces;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

public interface FullHtmlMail extends Mail {
    @Override
    default void applyTo(MimeMessage mimeMessage) throws MessagingException {
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setTo(getTo());
        mimeMessageHelper.setText(getHtml(), true);
        mimeMessageHelper.setSubject(getSubject());
    }

    String getHtml();

    String getSubject();

    String getTo();
}
