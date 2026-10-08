package in.coderarmy.gatekeeper.management.service;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import in.coderarmy.gatekeeper.management.entity.ApiRoute;
import in.coderarmy.gatekeeper.management.entity.RouteConfigurationVersion;
import in.coderarmy.gatekeeper.management.repository.RouteConfigurationVersionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RouteConfigurationVersionService {

    private final RouteConfigurationVersionRepository versionRepository;
    private final ObjectMapper objectMapper;

    public RouteConfigurationVersionService(
            RouteConfigurationVersionRepository versionRepository,
            ObjectMapper objectMapper) {

        this.versionRepository = versionRepository;
        this.objectMapper = objectMapper;
    }

    public void createVersion(
            ApiRoute route,
            String changeType) {

        RouteConfigurationVersion version =
                new RouteConfigurationVersion();

        version.setOrganizationId(
                route.getOrganization().getId());

        version.setRouteId(
                route.getRouteId());

        version.setVersionNumber(
                route.getConfigurationVersion());

        version.setChangeType(changeType);

        version.setConfigurationSnapshot(
                createSnapshot(route));

        version.setCreatedAt(
                LocalDateTime.now());

        versionRepository.save(version);
    }

    public List<RouteConfigurationVersion> getVersions(
            Long organizationId,
            String routeId) {

        return versionRepository
                .findByOrganizationIdAndRouteIdOrderByVersionNumberDesc(
                        organizationId,
                        routeId);
    }

    public RouteConfigurationVersion getVersion(
            Long organizationId,
            String routeId,
            Long versionNumber) {

        return versionRepository
                .findByOrganizationIdAndRouteIdAndVersionNumber(
                        organizationId,
                        routeId,
                        versionNumber)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Configuration version not found"));
    }

    private String createSnapshot(ApiRoute route) {

        Map<String, Object> snapshot =
                new LinkedHashMap<>();

        snapshot.put("routeId", route.getRouteId());
        snapshot.put("name", route.getName());
        snapshot.put(
                "pathPattern",
                route.getPathPattern());
        snapshot.put(
                "targetBaseUrl",
                route.getTargetBaseUrl());
        snapshot.put(
                "targetPathPrefix",
                route.getTargetPathPrefix());
        snapshot.put(
                "requestsPerMinute",
                route.getRequestsPerMinute());
        snapshot.put(
                "responseTimeoutMs",
                route.getResponseTimeoutMs());
        snapshot.put(
                "idempotencyEnabled",
                route.getIdempotencyEnabled());
        snapshot.put(
                "active",
                route.getActive());
        snapshot.put(
                "configurationVersion",
                route.getConfigurationVersion());

        try {
            return objectMapper.writeValueAsString(snapshot);
        } catch (JacksonException exception) {
            throw new RuntimeException(
                    "Failed to create configuration snapshot",
                    exception);
        }
    }
}
