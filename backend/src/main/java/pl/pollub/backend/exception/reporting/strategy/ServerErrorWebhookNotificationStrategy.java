package pl.pollub.backend.exception.reporting.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.pollub.backend.exception.reporting.ExceptionReportEvent;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Strategy that forwards server-side exceptions as JSON to an external HTTP endpoint.
 */
@Component
@Profile("prod")
public class ServerErrorWebhookNotificationStrategy implements ExceptionNotificationStrategy {
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final URI webhookUri;
    private final boolean enabled;

    @Autowired
    public ServerErrorWebhookNotificationStrategy(
            ObjectMapper objectMapper,
            @Value("${exception.reporting.server-error-webhook-url:http://localhost:8080}") String webhookUrl,
            @Value("${exception.reporting.server-error-webhook-enabled:false}") boolean enabled
    ) {
        this(HttpClient.newHttpClient(), objectMapper, webhookUrl, enabled);
    }

    ServerErrorWebhookNotificationStrategy(HttpClient httpClient, ObjectMapper objectMapper, String webhookUrl, boolean enabled) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.webhookUri = URI.create(webhookUrl);
        this.enabled = enabled;
    }

    @Override
    public boolean supports(ExceptionReportEvent event) {
        return enabled && event.isServerError();
    }

    @Override
    public void notify(ExceptionReportEvent event) {
        String body = toJsonBody(event);
        HttpRequest request = HttpRequest.newBuilder(webhookUri)
                .timeout(REQUEST_TIMEOUT)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new IllegalStateException("Webhook returned HTTP " + response.statusCode());
            }
        } catch (IOException | InterruptedException exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new IllegalStateException("Failed to forward exception to webhook", exception);
        }
    }

    private String toJsonBody(ExceptionReportEvent event) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", event.occurredAt().toString());
        payload.put("status", event.status().value());
        payload.put("reason", event.status().getReasonPhrase());
        payload.put("method", event.method());
        payload.put("path", event.path());
        payload.put("exceptionType", event.exception().getClass().getName());
        payload.put("message", event.exception().getMessage());
        payload.put("stackTrace", stackTraceOf(event.exception()));

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize exception webhook payload", exception);
        }
    }

    private String stackTraceOf(Throwable throwable) {
        StringBuilder builder = new StringBuilder();
        builder.append(throwable).append(System.lineSeparator());
        for (StackTraceElement element : throwable.getStackTrace()) {
            builder.append("\tat ").append(element).append(System.lineSeparator());
        }
        return builder.toString();
    }
}




