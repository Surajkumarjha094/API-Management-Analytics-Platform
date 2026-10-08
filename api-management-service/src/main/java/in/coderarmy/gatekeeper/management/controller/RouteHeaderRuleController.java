package in.coderarmy.gatekeeper.management.controller;

import in.coderarmy.gatekeeper.management.dto.route.CreateHeaderRuleRequest;
import in.coderarmy.gatekeeper.management.dto.route.HeaderRuleResponse;
import in.coderarmy.gatekeeper.management.service.RouteHeaderRuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations/{organizationId}/routes/{routeId}/headers")
public class RouteHeaderRuleController {

    private final RouteHeaderRuleService headerRuleService;

    public RouteHeaderRuleController(
            RouteHeaderRuleService headerRuleService) {
        this.headerRuleService = headerRuleService;
    }

    @PostMapping
    public ResponseEntity<HeaderRuleResponse> createRule(
            @PathVariable Long organizationId,
            @PathVariable String routeId,
            @RequestBody CreateHeaderRuleRequest request) {

        HeaderRuleResponse response =
                headerRuleService.createRule(
                        organizationId,
                        routeId,
                        request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<HeaderRuleResponse>> getRules(
            @PathVariable Long organizationId,
            @PathVariable String routeId) {

        return ResponseEntity.ok(
                headerRuleService.getRules(
                        organizationId,
                        routeId));
    }

    @DeleteMapping("/{ruleId}")
    public ResponseEntity<Void> deleteRule(
            @PathVariable Long organizationId,
            @PathVariable String routeId,
            @PathVariable Long ruleId) {

        headerRuleService.deleteRule(
                organizationId,
                routeId,
                ruleId);

        return ResponseEntity.noContent().build();
    }
}
