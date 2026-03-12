package pl.pollub.backend.transaction.observer;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pl.pollub.backend.config.constants.ExpenseLimitConstants;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.mail.MailService;
import pl.pollub.backend.mail.interfaces.Mail;
import pl.pollub.backend.mail.interfaces.MailSenderImplementation;
import pl.pollub.backend.mail.mails.CloseToLimitMail;
import pl.pollub.backend.mail.mails.LimitExceededMail;
import pl.pollub.backend.transaction.repository.ExpenseRepository;

import java.time.LocalDate;

/**
 * Observer that sends warning e-mails when expense limits are close or exceeded.
 */
@Component
public class ExpenseLimitEmailObserver implements ExpenseLimitObserver {
    private final ExpenseRepository expenseRepository;
    private final MailService mailService;
    private final ExpenseLimitMailAdapter mailAdapter;

    public ExpenseLimitEmailObserver(
            ExpenseRepository expenseRepository,
            MailService mailService,
            MailSenderImplementation mailSenderImplementation
    ) {
        this.expenseRepository = expenseRepository;
        this.mailService = mailService;

        // Prototype instances with shared sender configuration
        LimitExceededMail limitExceededPrototype = LimitExceededMail.builder()
                .sender(mailSenderImplementation)
                .build();
        CloseToLimitMail closeToLimitPrototype = CloseToLimitMail.builder()
                .sender(mailSenderImplementation)
                .build();
        this.mailAdapter = new ExpenseLimitMailAdapter(limitExceededPrototype, closeToLimitPrototype);
    }

    @Override
    public void update(ExpenseLimitEvent event) {
        Group group = event.group();

        if (group.getExpenseLimit() <= 0) {
            return;
        }

        Double totalExpenses = calculateTotalExpenses(group);
        double remainingPart = calculateRemainingPart(totalExpenses, group.getExpenseLimit());

        if (remainingPart > ExpenseLimitConstants.EXPENSE_WARNING_THRESHOLD) {
            return;
        }

        Mail mail = mailAdapter.toMail(event, totalExpenses, remainingPart);
        sendMailWithErrorHandling(mail);
    }

    private Double calculateTotalExpenses(Group group) {
        LocalDate startOfTheMonth = LocalDate.now().withDayOfMonth(1);
        Double totalExpenses = expenseRepository.getTotalByGroupAndMinDate(group, startOfTheMonth);
        return totalExpenses != null ? totalExpenses : 0.0;
    }

    private double calculateRemainingPart(Double totalExpenses, Double expenseLimit) {
        return 1.0 - (totalExpenses / expenseLimit);
    }

    private void sendMailWithErrorHandling(Mail mail) {
        try {
            mailService.sendMail(mail);
        } catch (Exception e) {
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Błąd podczas wysyłania e-maila");
        }
    }
}
