package pl.pollub.backend.mail.interfaces;

// start interface segregation principle
public interface Contact {
    String getDisplayName();
    String getEmailAddress();
}
