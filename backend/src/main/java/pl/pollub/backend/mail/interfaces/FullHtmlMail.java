package pl.pollub.backend.mail.interfaces;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

public interface FullHtmlMail<T extends FullHtmlMail<T>> extends SubjectHolder<T>, HtmlHolder<T> {
    @Override
    default void applyTo(MimeMessage mimeMessage) throws MessagingException {
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setTo(getTo());
        mimeMessageHelper.setText(getHtml(), true);
        mimeMessageHelper.setSubject(getSubject());
    }
}
