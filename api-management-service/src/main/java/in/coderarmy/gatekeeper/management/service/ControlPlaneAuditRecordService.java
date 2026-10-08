package in.coderarmy.gatekeeper.management.service;

import in.coderarmy.gatekeeper.management.entity.ControlPlaneAuditRecord;
import in.coderarmy.gatekeeper.management.repository.ControlPlaneAuditRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ControlPlaneAuditRecordService {

    private final ControlPlaneAuditRecordRepository auditRepository;

    public ControlPlaneAuditRecordService(
            ControlPlaneAuditRecordRepository auditRepository) {

        this.auditRepository = auditRepository;
    }

    public ControlPlaneAuditRecord record(
            Long organizationId,
            String actor,
            String action,
            String resourceType,
            String resourceId,
            Long durationMs,
            String result,
            String details) {

        ControlPlaneAuditRecord audit =
                new ControlPlaneAuditRecord();

        audit.setOrganizationId(organizationId);
        audit.setActor(actor);
        audit.setAction(action);
        audit.setResourceType(resourceType);
        audit.setResourceId(resourceId);
        audit.setCorrelationId(
                UUID.randomUUID().toString());
        audit.setDurationMs(durationMs);
        audit.setResult(result);
        audit.setDetails(details);
        audit.setCreatedAt(LocalDateTime.now());

        return auditRepository.save(audit);
    }

    public List<ControlPlaneAuditRecord> getByOrganization(
            Long organizationId) {

        return auditRepository
                .findByOrganizationIdOrderByCreatedAtDesc(
                        organizationId);
    }
}
