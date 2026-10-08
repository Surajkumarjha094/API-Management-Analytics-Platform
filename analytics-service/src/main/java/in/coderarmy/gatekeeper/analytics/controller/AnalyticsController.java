package in.coderarmy.gatekeeper.analytics.controller;

import in.coderarmy.gatekeeper.analytics.AnalyticsEventRepository;
import in.coderarmy.gatekeeper.analytics.dto.AnalyticsEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsEventRepository repository;

    public AnalyticsController(AnalyticsEventRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/events")
    public ResponseEntity<AnalyticsEvent> createEvent(
            @RequestBody AnalyticsEvent event) {

        if (event.getTimestamp() == null) {
            event.setTimestamp(Instant.now());
        }

        return ResponseEntity.ok(repository.save(event));
    }

    @GetMapping("/organizations/{organizationId}/events")
    public ResponseEntity<List<AnalyticsEvent>> getOrganizationEvents(
            @PathVariable Long organizationId) {

        return ResponseEntity.ok(
                repository.findByOrganizationId(organizationId)
        );
    }

    @GetMapping("/organizations/{organizationId}/summary")
    public ResponseEntity<Map<String, Object>> getSummary(
            @PathVariable Long organizationId) {

        List<AnalyticsEvent> events =
                repository.findByOrganizationId(organizationId);

        Map<String, Object> summary = new HashMap<>();

        summary.put("organizationId", organizationId);
        summary.put("totalRequests", events.size());

        long successfulRequests = events.stream()
                .filter(event ->
                        event.getStatusCode() != null
                                && event.getStatusCode() >= 200
                                && event.getStatusCode() < 400)
                .count();

        long failedRequests = events.stream()
                .filter(event ->
                        event.getStatusCode() != null
                                && event.getStatusCode() >= 400)
                .count();

        summary.put("successfulRequests", successfulRequests);
        summary.put("failedRequests", failedRequests);

        return ResponseEntity.ok(summary);
    }
}