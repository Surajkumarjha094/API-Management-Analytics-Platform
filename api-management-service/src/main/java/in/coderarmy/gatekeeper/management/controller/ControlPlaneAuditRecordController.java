package in.coderarmy.gatekeeper.management.controller;

import in.coderarmy.gatekeeper.management.entity.ControlPlaneAuditRecord;
import in.coderarmy.gatekeeper.management.service.ControlPlaneAuditRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations/{organizationId}/audit-records")
public class ControlPlaneAuditRecordController {

    private final ControlPlaneAuditRecordService auditService;

    public ControlPlaneAuditRecordController(
            ControlPlaneAuditRecordService auditService) {

        this.auditService = auditService;
    }

    @GetMapping
    public ResponseEntity<List<ControlPlaneAuditRecord>> getAuditRecords(
            @PathVariable Long organizationId) {

        return ResponseEntity.ok(
                auditService.getByOrganization(organizationId)
        );
    }
}
