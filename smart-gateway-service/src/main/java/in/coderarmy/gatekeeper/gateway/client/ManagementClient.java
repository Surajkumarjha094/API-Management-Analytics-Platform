package in.coderarmy.gatekeeper.gateway.client;

import in.coderarmy.gatekeeper.gateway.dto.HeaderRuleResponse;
import in.coderarmy.gatekeeper.gateway.dto.RouteResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class ManagementClient {

    private final RestClient restClient;

    public ManagementClient(
            RestClient.Builder restClientBuilder,
            @Value("${management.service.url:http://localhost:8081}") String managementServiceUrl) {

        this.restClient = restClientBuilder
                .baseUrl(managementServiceUrl)
                .build();
    }

    public List<RouteResponse> getRoutes(Long organizationId, String token) {

        RouteResponse[] routes = restClient.get()
                .uri("/api/organizations/{organizationId}/routes", organizationId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(RouteResponse[].class);

        if (routes == null) {
            return Collections.emptyList();
        }

        return Arrays.asList(routes);
    }

    public List<HeaderRuleResponse> getHeaderRules(
            Long organizationId,
            String routeId,
            String token) {

        HeaderRuleResponse[] rules = restClient.get()
                .uri("/api/organizations/{organizationId}/routes/{routeId}/headers",
                        organizationId, routeId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(HeaderRuleResponse[].class);

        if (rules == null) {
            return Collections.emptyList();
        }

        return Arrays.asList(rules);
    }
}