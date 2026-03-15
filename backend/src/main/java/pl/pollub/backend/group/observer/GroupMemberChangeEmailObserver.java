package pl.pollub.backend.group.observer;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.mail.MailService;
import pl.pollub.backend.mail.interfaces.Mail;
import pl.pollub.backend.mail.interfaces.MailSenderImplementation;
import pl.pollub.backend.mail.mails.MemberChangeMail;
import pl.pollub.backend.notification.NotificationSubscriptionService;
import pl.pollub.backend.notification.NotificationType;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Observer that sends e-mails when group membership changes.
 */
@Component
public class GroupMemberChangeEmailObserver implements GroupMemberChangeObserver {
    private final MailService mailService;
    private final GroupMemberChangeMailAdapter mailAdapter;
    private final NotificationSubscriptionService subscriptionService;

    public GroupMemberChangeEmailObserver(
            MailService mailService,
            MailSenderImplementation mailSenderImplementation,
            NotificationSubscriptionService subscriptionService
    ) {
        this.mailService = mailService;
        this.subscriptionService = subscriptionService;

        MemberChangeMail prototype = MemberChangeMail.builder()
                .sender(mailSenderImplementation)
                .build();
        this.mailAdapter = new GroupMemberChangeMailAdapter(prototype);
    }

    @Override
    public void update(GroupMemberChangeEvent event) {
        for (User recipient : getDistinctRecipients(event)) {
            if (!hasDeliverableEmail(recipient)) {
                continue;
            }

            if (!subscriptionService.isSubscribed(recipient, NotificationType.MEMBER_CHANGE)) {
                continue;
            }

            Mail mail = mailAdapter.toMail(recipient, event);
            sendMailWithErrorHandling(mail);
        }
    }

    private List<User> getDistinctRecipients(GroupMemberChangeEvent event) {
        List<User> recipients = new ArrayList<>();
        Set<String> seenRecipients = new LinkedHashSet<>();

        for (User recipient : event.recipients()) {
            if (recipient == null) {
                continue;
            }

            String identity = resolveRecipientIdentity(recipient);
            if (seenRecipients.add(identity)) {
                recipients.add(recipient);
            }
        }

        return recipients;
    }

    private String resolveRecipientIdentity(User recipient) {
        if (recipient.getId() != null) {
            return "id:" + recipient.getId();
        }
        if (recipient.getEmail() != null && !recipient.getEmail().isBlank()) {
            return "email:" + recipient.getEmail();
        }
        return "username:" + recipient.getUsername();
    }

    private boolean hasDeliverableEmail(User recipient) {
        return recipient.getEmail() != null && !recipient.getEmail().isBlank();
    }

    private void sendMailWithErrorHandling(Mail mail) {
        try {
            mailService.sendMail(mail);
        } catch (Exception e) {
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Błąd podczas wysyłania e-maila o zmianie członków grupy");
        }
    }
}

