
package in.coderarmy.gatekeeper.management.service;

import in.coderarmy.gatekeeper.management.dto.route.CreateHeaderRuleRequest;
import in.coderarmy.gatekeeper.management.dto.route.HeaderRuleResponse;
import in.coderarmy.gatekeeper.management.entity.ApiRoute;
import in.coderarmy.gatekeeper.management.entity.RouteHeaderRule;
import in.coderarmy.gatekeeper.management.repository.ApiRouteRepository;
import in.coderarmy.gatekeeper.management.repository.RouteHeaderRuleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RouteHeaderRuleService {

    private final RouteHeaderRuleRepository headerRuleRepository;
    private final ApiRouteRepository apiRouteRepository;

    public RouteHeaderRuleService(
            RouteHeaderRuleRepository headerRuleRepository,
            ApiRouteRepository apiRouteRepository) {

        this.headerRuleRepository = headerRuleRepository;
        this.apiRouteRepository = apiRouteRepository;
    }

    public HeaderRuleResponse createRule(
            Long organizationId,
            String routeId,
            CreateHeaderRuleRequest request) {

        ApiRoute route = getRoute(organizationId, routeId);

        RouteHeaderRule rule = new RouteHeaderRule();

        rule.setRoute(route);
        rule.setDirection(request.getDirection());
        rule.setAction(request.getAction());
        rule.setHeaderName(request.getHeaderName());
        rule.setHeaderValue(request.getHeaderValue());

        LocalDateTime now = LocalDateTime.now();

        rule.setCreatedAt(now);
        rule.setUpdatedAt(now);

        RouteHeaderRule savedRule =
                headerRuleRepository.save(rule);

        return mapToResponse(savedRule);
    }

    public List<HeaderRuleResponse> getRules(
            Long organizationId,
            String routeId) {

        ApiRoute route = getRoute(organizationId, routeId);

        return headerRuleRepository
                .findByRoute_Id(route.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void deleteRule(
            Long organizationId,
            String routeId,
            Long ruleId) {

        ApiRoute route = getRoute(organizationId, routeId);

        RouteHeaderRule rule =
                headerRuleRepository.findById(ruleId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Header rule not found"));

        if (!rule.getRoute().getId().equals(route.getId())) {
            throw new RuntimeException(
                    "Header rule does not belong to this route");
        }

        headerRuleRepository.delete(rule);
    }

    private ApiRoute getRoute(
            Long organizationId,
            String routeId) {

        return apiRouteRepository
                .findByOrganization_IdAndRouteId(
                        organizationId,
                        routeId.trim())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Route not found for organizationId="
                                        + organizationId
                                        + ", routeId="
                                        + routeId));
    }

    private HeaderRuleResponse mapToResponse(
            RouteHeaderRule rule) {

        HeaderRuleResponse response =
                new HeaderRuleResponse();

        response.setId(rule.getId());

        // Use the database ID because the DTO expects Long.
        response.setRouteId(
                rule.getRoute().getId());

        response.setDirection(
                rule.getDirection());

        response.setAction(
                rule.getAction());

        response.setHeaderName(
                rule.getHeaderName());

        response.setHeaderValue(
                rule.getHeaderValue());

        response.setCreatedAt(
                rule.getCreatedAt());

        response.setUpdatedAt(
                rule.getUpdatedAt());

        return response;
    }
}

