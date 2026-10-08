
package in.coderarmy.gatekeeper.management.controller;

import in.coderarmy.gatekeeper.management.dto.route.CreateRouteRequest;
import in.coderarmy.gatekeeper.management.dto.route.RouteResponse;
import in.coderarmy.gatekeeper.management.service.ApiRouteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations/{organizationId}/routes")
public class ApiRouteController {

    private final ApiRouteService apiRouteService;

    public ApiRouteController(ApiRouteService apiRouteService) {
        this.apiRouteService = apiRouteService;
    }

    @PostMapping
    public ResponseEntity<RouteResponse> createRoute(
            @PathVariable Long organizationId,
            @RequestBody CreateRouteRequest request) {

        RouteResponse response =
                apiRouteService.createRoute(
                        organizationId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<RouteResponse>> getRoutes(
            @PathVariable Long organizationId) {

        return ResponseEntity.ok(
                apiRouteService.getRoutes(organizationId)
        );
    }

    @GetMapping("/{routeId}")
    public ResponseEntity<RouteResponse> getRoute(
            @PathVariable Long organizationId,
            @PathVariable String routeId) {

        return ResponseEntity.ok(
                apiRouteService.getRoute(
                        organizationId,
                        routeId
                )
        );
    }

    @PutMapping("/{routeId}")
    public ResponseEntity<RouteResponse> updateRoute(
            @PathVariable Long organizationId,
            @PathVariable String routeId,
            @RequestBody CreateRouteRequest request) {

        RouteResponse response =
                apiRouteService.updateRoute(
                        organizationId,
                        routeId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{routeId}")
    public ResponseEntity<Void> deleteRoute(
            @PathVariable Long organizationId,
            @PathVariable String routeId) {

        apiRouteService.deleteRoute(
                organizationId,
                routeId
        );

        return ResponseEntity.noContent().build();
    }
}

