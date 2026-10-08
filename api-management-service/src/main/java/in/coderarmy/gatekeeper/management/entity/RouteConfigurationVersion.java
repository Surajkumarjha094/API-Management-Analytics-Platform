package in.coderarmy.gatekeeper.management.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "route_configuration_versions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_route_versions_tenant_version",
                        columnNames = {
                                "organization_id",
                                "route_id",
                                "version_number"
                        }
                )
        }
)
public class RouteConfigurationVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Column(name = "route_id", nullable = false, length = 100)
    private String routeId;

    @Column(name = "version_number", nullable = false)
    private Long versionNumber;

    @Column(name = "change_type", nullable = false, length = 30)
    private String changeType;

    @Column(
            name = "configuration_snapshot",
            nullable = false,
            columnDefinition = "LONGTEXT"
    )
    private String configurationSnapshot;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getConfigurationSnapshot() {
        return configurationSnapshot;
    }

    public void setConfigurationSnapshot(
            String configurationSnapshot) {
        this.configurationSnapshot = configurationSnapshot;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
