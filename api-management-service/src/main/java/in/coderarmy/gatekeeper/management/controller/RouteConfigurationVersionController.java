package in.coderarmy.gatekeeper.management.controller;

import in.coderarmy.gatekeeper.management.entity.RouteConfigurationVersion;
import in.coderarmy.gatekeeper.management.service.RouteConfigurationVersionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations/{organizationId}/routes/{routeId}/versions")
public class RouteConfigurationVersionController {

    private final RouteConfigurationVersionService versionService;

    public RouteConfigurationVersionController(
            RouteConfigurationVersionService versionService) {
        this.versionService = versionService;
    }

    @GetMapping
    public ResponseEntity<List<RouteConfigurationVersion>> getVersions(
            @PathVariable Long organizationId,
            @PathVariable String routeId) {

        return ResponseEntity.ok(
                versionService.getVersions(
                        organizationId,
                        routeId
                )
        );
    }

    @GetMapping("/{versionNumber}")
    public ResponseEntity<RouteConfigurationVersion> getVersion(
            @PathVariable Long organizationId,
            @PathVariable String routeId,
            @PathVariable Long versionNumber) {

        return ResponseEntity.ok(
                versionService.getVersion(
                        organizationId,
                        routeId,
                        versionNumber
                )
        );
    }
}
