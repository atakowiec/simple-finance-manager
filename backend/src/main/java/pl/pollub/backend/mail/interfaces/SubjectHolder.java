package pl.pollub.backend.mail.interfaces;

public interface SubjectHolder<T extends SubjectHolder<T>> extends Mail<T> {
    String getSubject();
}
