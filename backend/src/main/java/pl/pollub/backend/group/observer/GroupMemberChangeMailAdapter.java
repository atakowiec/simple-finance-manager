package pl.pollub.backend.group.observer;

import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.mail.interfaces.Mail;

// start adapter
/**
 * Adapter that turns GroupMemberChangeEvent data into a concrete Mail instance.
 */
public class GroupMemberChangeMailAdapter {
    private final pl.pollub.backend.mail.mails.MemberChangeMail prototype;

    public GroupMemberChangeMailAdapter(pl.pollub.backend.mail.mails.MemberChangeMail prototype) {
        this.prototype = prototype;
    }

    public Mail toMail(User recipient, GroupMemberChangeEvent event) {
        return prototype.clone()
                .withRecipient(recipient)
                .withActor(event.actor())
                .withMember(event.member())
                .withGroup(event.group())
                .withAction(event.action());
    }
}


