package pl.pollub.backend.mail.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import pl.pollub.backend.mail.JavaMailSenderImpl;
import pl.pollub.backend.mail.decorators.LoggingMailSenderDecorator;
import pl.pollub.backend.mail.decorators.RetryMailSenderDecorator;
import pl.pollub.backend.mail.interfaces.MailSenderImplementation;

/**
 * Wiring for MailSenderImplementation decorators.
 */
@Configuration
public class MailSenderConfig {

    @Bean
    @Primary
    public MailSenderImplementation mailSenderImplementation(JavaMailSenderImpl baseSender) {
        MailSenderImplementation logging = new LoggingMailSenderDecorator(baseSender);
        return new RetryMailSenderDecorator(logging, 3, 300);
    }
}
