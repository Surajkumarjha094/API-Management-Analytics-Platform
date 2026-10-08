package in.coderarmy.gatekeeper.management.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "api_routes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_api_routes_tenant_route",
                        columnNames = {"organization_id", "route_id"}
                )
        }
)
public class ApiRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "route_id", nullable = false, length = 100)
    private String routeId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "path_pattern", nullable = false, length = 500)
    private String pathPattern;

    @Column(name = "target_base_url", nullable = false, length = 1000)
    private String targetBaseUrl;

    @Column(name = "target_path_prefix", nullable = false, length = 500)
    private String targetPathPrefix;

    @Column(name = "requests_per_minute")
    private Integer requestsPerMinute;

    @Column(name = "response_timeout_ms", nullable = false)
    private Integer responseTimeoutMs;

    @Column(name = "idempotency_enabled", nullable = false)
    private Boolean idempotencyEnabled = false;

    @Column(nullable = false)
    private Boolean active = false;

    @Column(name = "configuration_version", nullable = false)
    private Long configurationVersion = 1L;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathPattern() {
        return pathPattern;
    }

    public void setPathPattern(String pathPattern) {
        this.pathPattern = pathPattern;
    }

    public String getTargetBaseUrl() {
        return targetBaseUrl;
    }

    public void setTargetBaseUrl(String targetBaseUrl) {
        this.targetBaseUrl = targetBaseUrl;
    }

    public String getTargetPathPrefix() {
        return targetPathPrefix;
    }

    public void setTargetPathPrefix(String targetPathPrefix) {
        this.targetPathPrefix = targetPathPrefix;
    }

    public Integer getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public void setRequestsPerMinute(Integer requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
    }

    public Integer getResponseTimeoutMs() {
        return responseTimeoutMs;
    }

    public void setResponseTimeoutMs(Integer responseTimeoutMs) {
        this.responseTimeoutMs = responseTimeoutMs;
    }

    public Boolean getIdempotencyEnabled() {
        return idempotencyEnabled;
    }

    public void setIdempotencyEnabled(Boolean idempotencyEnabled) {
        this.idempotencyEnabled = idempotencyEnabled;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getConfigurationVersion() {
        return configurationVersion;
    }

    public void setConfigurationVersion(Long configurationVersion) {
        this.configurationVersion = configurationVersion;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

