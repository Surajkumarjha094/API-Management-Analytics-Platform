package in.coderarmy.gatekeeper.gateway.controller;

import in.coderarmy.gatekeeper.gateway.client.AnalyticsClient;
import in.coderarmy.gatekeeper.gateway.client.ManagementClient;
import in.coderarmy.gatekeeper.gateway.dto.HeaderRuleResponse;
import in.coderarmy.gatekeeper.gateway.dto.RouteResponse;
import in.coderarmy.gatekeeper.gateway.ratelimit.RateLimiter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;

@RestController
@RequestMapping("/gateway")
public class GatewayProxyController {

    private final ManagementClient managementClient;
    private final RestClient restClient;
    private final RateLimiter rateLimiter;
    private final AnalyticsClient analyticsClient;

    public GatewayProxyController(
            ManagementClient managementClient,
            RestClient.Builder restClientBuilder,
            RateLimiter rateLimiter,
            AnalyticsClient analyticsClient) {

        this.managementClient = managementClient;
        this.restClient = restClientBuilder.build();
        this.rateLimiter = rateLimiter;
        this.analyticsClient = analyticsClient;
    }

    @GetMapping("/proxy")
    public ResponseEntity<String> proxyRequest(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");

        Long organizationId = 4L;

        List<RouteResponse> routes =
                managementClient.getRoutes(organizationId, token);

        if (routes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RouteResponse route = routes.get(0);

        String targetUrl =
                route.getTargetBaseUrl()
                        + route.getTargetPathPrefix();

        return restClient.get()
                .uri(targetUrl)
                .retrieve()
                .toEntity(String.class);
    }

    @GetMapping("/products")
    public ResponseEntity<String> productsRequest(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

        return forwardProductRequest(authorizationHeader, null);
    }

    @GetMapping("/products/{path}")
    public ResponseEntity<String> productByIdRequest(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @PathVariable String path) {

        return forwardProductRequest(authorizationHeader, path);
    }

    private ResponseEntity<String> forwardProductRequest(
            String authorizationHeader,
            String path) {

        long startTime = System.currentTimeMillis();

        String token = authorizationHeader.replace("Bearer ", "");

        Long organizationId = 4L;

        List<RouteResponse> routes =
                managementClient.getRoutes(organizationId, token);

        if (routes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RouteResponse route = routes.stream()
                .filter(r -> "products-api".equals(r.getRouteId()))
                .findFirst()
                .orElse(null);

        if (route == null) {
            return ResponseEntity.notFound().build();
        }

        int maxRequests = route.getRequestsPerMinute() != null
                ? route.getRequestsPerMinute()
                : 100;

        String rateLimitKey =
                "org-" + organizationId + "-" + route.getRouteId();

        if (!rateLimiter.allowRequest(rateLimitKey, maxRequests)) {
            return ResponseEntity.status(429)
                    .body("Rate limit exceeded. Try again later.");
        }

        String targetUrl =
                route.getTargetBaseUrl()
                        + route.getTargetPathPrefix();

        String gatewayPath = "/gateway/products";

        if (path != null && !path.isBlank()) {
            targetUrl = targetUrl + "/" + path;
            gatewayPath = gatewayPath + "/" + path;
        }

        List<HeaderRuleResponse> headerRules =
                managementClient.getHeaderRules(
                        organizationId,
                        route.getRouteId(),
                        token
                );

        RestClient.RequestHeadersSpec<?> request =
                restClient.get().uri(targetUrl);

        for (HeaderRuleResponse rule : headerRules) {

            if ("REQUEST".equalsIgnoreCase(rule.getDirection())
                    && "ADD".equalsIgnoreCase(rule.getAction())
                    && rule.getHeaderName() != null
                    && rule.getHeaderValue() != null) {

                request = request.header(
                        rule.getHeaderName(),
                        rule.getHeaderValue()
                );
            }
        }

        ResponseEntity<String> response =
                request
                        .retrieve()
                        .toEntity(String.class);

        long responseTime =
                System.currentTimeMillis() - startTime;

        analyticsClient.recordEvent(
                organizationId,
                route.getRouteId(),
                "GET",
                gatewayPath,
                response.getStatusCode().value(),
                responseTime
        );

        return response;
    }
}