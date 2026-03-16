package pl.pollub.backend.mail.mails;

import jakarta.mail.MessagingException;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.group.observer.GroupMemberChangeAction;
import pl.pollub.backend.mail.interfaces.FullHtmlMail;
import pl.pollub.backend.mail.interfaces.Mail;
import pl.pollub.backend.mail.interfaces.MailRequest;
import pl.pollub.backend.mail.interfaces.MailSenderImplementation;

// start prototype
/**
 * Mail informing users about changes in group membership.
 */
public class MemberChangeMail extends Mail implements FullHtmlMail, Cloneable {
    private User recipient;
    private User actor;
    private User member;
    private Group group;
    private GroupMemberChangeAction action;

    private MemberChangeMail(Builder builder) {
        super(builder.sender);
        this.recipient = builder.recipient;
        this.actor = builder.actor;
        this.member = builder.member;
        this.group = builder.group;
        this.action = builder.action;
    }

    // start builder
    // start builder
    public static class Builder {
        private MailSenderImplementation sender;
        private User recipient;
        private User actor;
        private User member;
        private Group group;
        private GroupMemberChangeAction action;

        public Builder sender(MailSenderImplementation sender) {
            this.sender = sender;
            return this;
        }

        public Builder recipient(User recipient) {
            this.recipient = recipient;
            return this;
        }

        public Builder actor(User actor) {
            this.actor = actor;
            return this;
        }

        public Builder member(User member) {
            this.member = member;
            return this;
        }

        public Builder group(Group group) {
            this.group = group;
            return this;
        }

        public Builder action(GroupMemberChangeAction action) {
            this.action = action;
            return this;
        }

        public MemberChangeMail build() {
            return new MemberChangeMail(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public MemberChangeMail withRecipient(User recipient) {
        this.recipient = recipient;
        return this;
    }

    public MemberChangeMail withActor(User actor) {
        this.actor = actor;
        return this;
    }

    public MemberChangeMail withMember(User member) {
        this.member = member;
        return this;
    }

    public MemberChangeMail withGroup(Group group) {
        this.group = group;
        return this;
    }

    public MemberChangeMail withAction(GroupMemberChangeAction action) {
        this.action = action;
        return this;
    }

    @Override
    public MemberChangeMail clone() {
        try {
            return (MemberChangeMail) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Cloning mail failed", e);
        }
    }

    @Override
    public void send() throws MessagingException {
        sender.send(new MailRequest(getTo(), getSubject(), getHtml(), true));
    }

    @Override
    public String getHtml() {
        return String.format("<p>Witaj %s!</p><p>%s</p>", recipient.getUsername(), getMessageBody());
    }

    @Override
    public String getSubject() {
        return switch (action) {
            case JOINED -> String.format("Nowy członek w grupie %s", group.getName());
            case LEFT -> String.format("Członek opuścił grupę %s", group.getName());
            case REMOVED -> String.format("Członek został usunięty z grupy %s", group.getName());
        };
    }

    @Override
    public String getTo() {
        return recipient.getEmail();
    }

    private String getMessageBody() {
        return switch (action) {
            case JOINED -> String.format("Użytkownik %s dołączył do grupy <b>%s</b>.", member.getUsername(), group.getName());
            case LEFT -> String.format("Użytkownik %s opuścił grupę <b>%s</b>.", member.getUsername(), group.getName());
            case REMOVED -> String.format("Użytkownik %s został usunięty z grupy <b>%s</b> przez %s.",
                    member.getUsername(),
                    group.getName(),
                    actor.getUsername());
        };
    }
}

