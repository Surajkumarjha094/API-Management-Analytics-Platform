package in.coderarmy.gatekeeper.management.controller;

import in.coderarmy.gatekeeper.management.dto.admin.CreateTenantAdminRequest;
import in.coderarmy.gatekeeper.management.entity.ManagementUser;
import in.coderarmy.gatekeeper.management.service.TenantAdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationAdminController {

    private final TenantAdminService tenantAdminService;

    public OrganizationAdminController(
            TenantAdminService tenantAdminService) {
        this.tenantAdminService = tenantAdminService;
    }

    @PostMapping("/{organizationId}/admins")
    public ResponseEntity<String> createTenantAdmin(
            @PathVariable Long organizationId,
            @RequestBody CreateTenantAdminRequest request) {

        ManagementUser user =
                tenantAdminService.createTenantAdmin(
                        organizationId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Tenant Admin created successfully: "
                        + user.getUsername());
    }
}
