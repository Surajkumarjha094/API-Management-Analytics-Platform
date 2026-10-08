package in.coderarmy.gatekeeper.management.controller;

import in.coderarmy.gatekeeper.management.dto.organization.CreateOrganizationRequest;
import in.coderarmy.gatekeeper.management.dto.organization.OrganizationResponse;
import in.coderarmy.gatekeeper.management.service.OrganizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping
    public ResponseEntity<OrganizationResponse> createOrganization(
            @RequestBody CreateOrganizationRequest request) {

        OrganizationResponse response =
                organizationService.createOrganization(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
