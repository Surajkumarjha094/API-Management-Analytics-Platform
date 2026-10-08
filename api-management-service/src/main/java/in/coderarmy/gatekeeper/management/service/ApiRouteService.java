
package in.coderarmy.gatekeeper.management.service;

import in.coderarmy.gatekeeper.management.dto.route.CreateRouteRequest;
import in.coderarmy.gatekeeper.management.dto.route.RouteResponse;
import in.coderarmy.gatekeeper.management.entity.ApiRoute;
import in.coderarmy.gatekeeper.management.entity.Organization;
import in.coderarmy.gatekeeper.management.repository.ApiRouteRepository;
import in.coderarmy.gatekeeper.management.repository.OrganizationRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApiRouteService {

    private final ApiRouteRepository apiRouteRepository;
    private final OrganizationRepository organizationRepository;
    private final RouteConfigurationVersionService routeConfigurationVersionService;
    private final ControlPlaneAuditRecordService auditRecordService;

    public ApiRouteService(
            ApiRouteRepository apiRouteRepository,
            OrganizationRepository organizationRepository,
            RouteConfigurationVersionService routeConfigurationVersionService,
            ControlPlaneAuditRecordService auditRecordService) {

        this.apiRouteRepository = apiRouteRepository;
        this.organizationRepository = organizationRepository;
        this.routeConfigurationVersionService = routeConfigurationVersionService;
        this.auditRecordService = auditRecordService;
    }

    public RouteResponse createRoute(
            Long organizationId,
            CreateRouteRequest request) {

        Organization organization =
                organizationRepository.findById(organizationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Organization not found"));

        String routeId =
                request.getRouteId() != null
                        ? request.getRouteId().trim()
                        : "";

        if (routeId.isBlank()) {
            throw new RuntimeException(
                    "Route ID cannot be empty");
        }

        long existingCount =
                apiRouteRepository
                        .countRouteByOrganizationAndRouteId(
                                organizationId,
                                routeId);

        System.out.println(
                "DEBUG CREATE ROUTE -> organizationId="
                        + organizationId
                        + ", routeId=["
                        + routeId
                        + "], existingCount="
                        + existingCount);

        if (existingCount > 0) {
            throw new RuntimeException(
                    "Route ID already exists for this organization");
        }

        ApiRoute route = new ApiRoute();

        route.setOrganization(organization);
        route.setRouteId(routeId);
        route.setName(request.getName());
        route.setPathPattern(request.getPathPattern());
        route.setTargetBaseUrl(request.getTargetBaseUrl());
        route.setTargetPathPrefix(request.getTargetPathPrefix());
        route.setRequestsPerMinute(
                request.getRequestsPerMinute());
        route.setResponseTimeoutMs(
                request.getResponseTimeoutMs());

        route.setIdempotencyEnabled(
                request.getIdempotencyEnabled() != null
                        ? request.getIdempotencyEnabled()
                        : false
        );

        route.setActive(
                request.getActive() != null
                        ? request.getActive()
                        : false
        );

        route.setConfigurationVersion(1L);

        LocalDateTime now = LocalDateTime.now();

        route.setCreatedAt(now);
        route.setUpdatedAt(now);

        long startTime = System.currentTimeMillis();

        ApiRoute savedRoute =
                apiRouteRepository.save(route);

        routeConfigurationVersionService.createVersion(
                savedRoute,
                "CREATE"
        );

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String actor =
                authentication != null
                        ? authentication.getName()
                        : "UNKNOWN";

        long durationMs =
                System.currentTimeMillis() - startTime;

        auditRecordService.record(
                organizationId,
                actor,
                "CREATE_ROUTE",
                "API_ROUTE",
                savedRoute.getRouteId(),
                durationMs,
                "SUCCESS",
                "Route created successfully"
        );

        return mapToResponse(savedRoute);
    }

    public List<RouteResponse> getRoutes(Long organizationId) {

        return apiRouteRepository
                .findByOrganization_Id(organizationId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public RouteResponse getRoute(
            Long organizationId,
            String routeId) {

        ApiRoute route =
                apiRouteRepository
                        .findByOrganization_IdAndRouteId(
                                organizationId,
                                routeId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Route not found"));

        return mapToResponse(route);
    }

    public RouteResponse updateRoute(
            Long organizationId,
            String routeId,
            CreateRouteRequest request) {

        ApiRoute route =
                apiRouteRepository
                        .findByOrganization_IdAndRouteId(
                                organizationId,
                                routeId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Route not found"));

        route.setName(request.getName());
        route.setPathPattern(request.getPathPattern());
        route.setTargetBaseUrl(request.getTargetBaseUrl());
        route.setTargetPathPrefix(request.getTargetPathPrefix());
        route.setRequestsPerMinute(request.getRequestsPerMinute());
        route.setResponseTimeoutMs(request.getResponseTimeoutMs());

        route.setIdempotencyEnabled(
                request.getIdempotencyEnabled() != null
                        ? request.getIdempotencyEnabled()
                        : false
        );

        route.setActive(
                request.getActive() != null
                        ? request.getActive()
                        : route.getActive()
        );

        route.setConfigurationVersion(
                route.getConfigurationVersion() + 1
        );

        route.setUpdatedAt(LocalDateTime.now());

        long startTime = System.currentTimeMillis();

        ApiRoute updatedRoute =
                apiRouteRepository.save(route);

        routeConfigurationVersionService.createVersion(
                updatedRoute,
                "UPDATE"
        );

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String actor =
                authentication != null
                        ? authentication.getName()
                        : "UNKNOWN";

        long durationMs =
                System.currentTimeMillis() - startTime;

        auditRecordService.record(
                organizationId,
                actor,
                "UPDATE_ROUTE",
                "API_ROUTE",
                updatedRoute.getRouteId(),
                durationMs,
                "SUCCESS",
                "Route updated successfully"
        );

        return mapToResponse(updatedRoute);
    }

    public void deleteRoute(
            Long organizationId,
            String routeId) {

        ApiRoute route =
                apiRouteRepository
                        .findByOrganization_IdAndRouteId(
                                organizationId,
                                routeId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Route not found"));

        long startTime = System.currentTimeMillis();

        String deletedRouteId =
                route.getRouteId();

        apiRouteRepository.delete(route);

        long durationMs =
                System.currentTimeMillis() - startTime;

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String actor =
                authentication != null
                        ? authentication.getName()
                        : "UNKNOWN";

        auditRecordService.record(
                organizationId,
                actor,
                "DELETE_ROUTE",
                "API_ROUTE",
                deletedRouteId,
                durationMs,
                "SUCCESS",
                "Route deleted successfully"
        );
    }

    private RouteResponse mapToResponse(
            ApiRoute route) {

        RouteResponse response =
                new RouteResponse();

        response.setId(
                route.getId());

        response.setOrganizationId(
                route.getOrganization().getId());

        response.setRouteId(
                route.getRouteId());

        response.setName(
                route.getName());

        response.setPathPattern(
                route.getPathPattern());

        response.setTargetBaseUrl(
                route.getTargetBaseUrl());

        response.setTargetPathPrefix(
                route.getTargetPathPrefix());

        response.setRequestsPerMinute(
                route.getRequestsPerMinute());

        response.setResponseTimeoutMs(
                route.getResponseTimeoutMs());

        response.setIdempotencyEnabled(
                route.getIdempotencyEnabled());

        response.setActive(
                route.getActive());

        response.setConfigurationVersion(
                route.getConfigurationVersion());

        response.setCreatedAt(
                route.getCreatedAt());

        response.setUpdatedAt(
                route.getUpdatedAt());

        return response;
    }
}

