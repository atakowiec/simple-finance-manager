package pl.pollub.backend.mail.adapter;

import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.mail.interfaces.Contact;

public class UserContactAdapter implements Contact {
    private final User user;

    public UserContactAdapter(User user) {
        this.user = user;
    }

    @Override
    public String getDisplayName() {
        return user.getUsername();
    }

    @Override
    public String getEmailAddress() {
        return user.getEmail();
    }
}
