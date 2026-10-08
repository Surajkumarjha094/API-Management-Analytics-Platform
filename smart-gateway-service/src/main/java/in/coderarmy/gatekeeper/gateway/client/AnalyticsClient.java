package in.coderarmy.gatekeeper.gateway.client;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class AnalyticsClient {

    private final RestClient restClient;

    public AnalyticsClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://localhost:8082")
                .build();
    }

    public void recordEvent(
            Long organizationId,
            String routeId,
            String method,
            String path,
            int statusCode,
            long responseTimeMs) {

        Map<String, Object> event = Map.of(
                "organizationId", organizationId,
                "routeId", routeId,
                "method", method,
                "path", path,
                "statusCode", statusCode,
                "responseTimeMs", responseTimeMs
        );

        try {
            restClient.post()
                    .uri("/api/analytics/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(event)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ignored) {
            // Analytics failure should not break the actual API request.
        }
    }
}