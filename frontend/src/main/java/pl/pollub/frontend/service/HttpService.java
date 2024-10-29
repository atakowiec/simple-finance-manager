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
        HttpRequest.Builder request = getHttpRequestBuilder(url)
                .POST(HttpRequest.BodyPublishers.ofString(JsonUtil.toJson(body)));

        return sendRequest(request.build());
    }

    public HttpResponse<String> get(String url) {
        HttpRequest.Builder request = getHttpRequestBuilder(url)
                .GET();

        return sendRequest(request.build());
    }

    public HttpResponse<String> put(String url, Object body) {
        HttpRequest.Builder request = getHttpRequestBuilder(url)
                .PUT(HttpRequest.BodyPublishers.ofString(JsonUtil.toJson(body)));

        return sendRequest(request.build());
    }

    private HttpResponse<String> sendRequest(HttpRequest request) {
        HttpClient client = HttpClient.newHttpClient();

        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequest.Builder getHttpRequestBuilder(String url) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .uri(getNormalizedUri(url));

        if(authService.getToken() != null) {
            builder.header("Authorization", "Bearer " + authService.getToken());
        }

        return builder;
    }

    private URI getNormalizedUri(String url) {
        if(url.startsWith("/")) {
            url = url.substring(1);
        }
        return URI.create(BASE_URL + "/" + url);
    }
}