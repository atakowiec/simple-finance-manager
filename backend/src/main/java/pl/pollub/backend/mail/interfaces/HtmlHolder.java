package pl.pollub.backend.mail.interfaces;

public interface HtmlHolder<T extends HtmlHolder<T>> extends Mail<T> {
    String getHtml();
}
