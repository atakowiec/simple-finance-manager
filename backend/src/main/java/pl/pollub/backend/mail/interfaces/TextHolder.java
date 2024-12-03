package pl.pollub.backend.mail.interfaces;

public interface TextHolder<T extends TextHolder<T>> {
    String getText();
}
