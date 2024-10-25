package pl.pollub.frontend.service;

import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.injector.Injectable;
import pl.pollub.frontend.util.JsonUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Injectable
public class HttpService {
    private static final String BASE_URL = "http://localhost:5000";

    @Inject
    private AuthService authService;

    public HttpResponse<String> post(String url, Object body) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(getNormalizedUri(url))
                .POST(HttpRequest.BodyPublishers.ofString(JsonUtil.toJson(body)))
                .header("Content-Type", "application/json");

        if(authService.getToken() != null) {
            request.header("Authorization", "Bearer " + authService.getToken());
        }

        try {
            return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpResponse<String> get(String url) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(getNormalizedUri(url))
                .GET();

        if(authService.getToken() != null) {
            request.header("Authorization", "Bearer " + authService.getToken());
        }

        try {
            return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private URI getNormalizedUri(String url) {
        if(url.startsWith("/")) {
            url = url.substring(1);
        }
        return URI.create(BASE_URL + "/" + url);
    }
}
