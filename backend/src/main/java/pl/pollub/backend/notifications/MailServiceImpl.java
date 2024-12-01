package pl.pollub.backend.notifications;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.transaction.repository.ExpenseRepository;

import java.time.LocalDate;

/**
 * Service for sending e-mails. It uses JavaMailSender to send e-mails.
 */
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender javaMailSender;
    private final ExpenseRepository expenseRepository;

    @Override
    public void sendMail(String to, String subject, String text, boolean isHtmlContent) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(text, isHtmlContent);

        javaMailSender.send(mimeMessage);
    }

    @Override
    public void trySendLimitWarningMail(User user, Group group) {
        if (group.getExpenseLimit() <= 0)
            return;

        LocalDate now = LocalDate.now();
        LocalDate startOfTheMonth = now.withDayOfMonth(1);
        Double totalExpenses = expenseRepository.getTotalByGroupAndMinDate(group, startOfTheMonth);

        if (totalExpenses == null) {
            totalExpenses = 0.0;
        }

        double remainingPart = 1 - (totalExpenses / group.getExpenseLimit());

        String emailContent = "";
        String subject = "";

        String formattedTotal = String.format("%.2f", totalExpenses) + " zł";
        String formattedLimit = String.format("%.2f", group.getExpenseLimit()) + " zł";

        if (remainingPart < 0) {
            subject = "Przekroczyłeś limit wydatków!";
            emailContent = "<h2>Uważaj! Przekroczyłeś limit wydatków! (" + formattedTotal + "/" + formattedLimit + ")</h2><br>:(";
        } else if (remainingPart < 0.1) {
            subject = "Zbliżasz się do przekroczenia limitu wydatków";
            emailContent = "<h2>Uważaj z wydatkami, twój limit niebawem zostanie przekroczony (" + formattedTotal + "/" + formattedLimit + ")</h2><br>:P";
        }

        if (!emailContent.isEmpty()) {
            try {
                this.sendMail(user.getEmail(), subject, emailContent, true);
            } catch (Exception e) {
                throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Błąd podczas wysyłania e-maila");
            }
        }
    }
}
