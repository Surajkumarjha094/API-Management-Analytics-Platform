package in.coderarmy.gatekeeper.management.service;

import in.coderarmy.gatekeeper.management.dto.admin.CreateTenantAdminRequest;
import in.coderarmy.gatekeeper.management.entity.ManagementUser;
import in.coderarmy.gatekeeper.management.entity.Role;
import in.coderarmy.gatekeeper.management.repository.ManagementUserRepository;
import in.coderarmy.gatekeeper.management.repository.OrganizationRepository;
import in.coderarmy.gatekeeper.management.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TenantAdminService {

    private final ManagementUserRepository managementUserRepository;
    private final OrganizationRepository organizationRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public TenantAdminService(
            ManagementUserRepository managementUserRepository,
            OrganizationRepository organizationRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        this.managementUserRepository = managementUserRepository;
        this.organizationRepository = organizationRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ManagementUser createTenantAdmin(
            Long organizationId,
            CreateTenantAdminRequest request) {

        organizationRepository.findById(organizationId)
                .orElseThrow(() ->
                        new RuntimeException("Organization not found"));

        if (managementUserRepository
                .findByUsername(request.getUsername())
                .isPresent()) {

            throw new RuntimeException("Username already exists");
        }

        Role tenantAdminRole = roleRepository
                .findByName("TENANT_ADMIN")
                .orElseThrow(() ->
                        new RuntimeException("TENANT_ADMIN role not found"));

        ManagementUser user = new ManagementUser();

        user.setOrganizationId(organizationId);
        user.setRoleId(tenantAdminRole.getId());
        user.setUsername(request.getUsername());

        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        user.setEnabled(true);

        LocalDateTime now = LocalDateTime.now();

        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        return managementUserRepository.save(user);
    }
}
