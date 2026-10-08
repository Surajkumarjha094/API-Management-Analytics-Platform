package in.coderarmy.gatekeeper.management.dto.route;

public class CreateRouteRequest {

    private String routeId;
    private String name;
    private String pathPattern;
    private String targetBaseUrl;
    private String targetPathPrefix;
    private Integer requestsPerMinute;
    private Integer responseTimeoutMs;
    private Boolean idempotencyEnabled;
    private Boolean active;

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
}
