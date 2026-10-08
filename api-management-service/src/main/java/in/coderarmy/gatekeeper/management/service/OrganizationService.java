package in.coderarmy.gatekeeper.management.service;

import in.coderarmy.gatekeeper.management.dto.organization.CreateOrganizationRequest;
import in.coderarmy.gatekeeper.management.dto.organization.OrganizationResponse;
import in.coderarmy.gatekeeper.management.entity.Organization;
import in.coderarmy.gatekeeper.management.entity.OrganizationStatus;
import in.coderarmy.gatekeeper.management.repository.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public OrganizationResponse createOrganization(CreateOrganizationRequest request) {

        if (organizationRepository.existsByName(request.getName())) {
            throw new RuntimeException("Organization name already exists");
        }

        if (organizationRepository.existsBySlug(request.getSlug())) {
            throw new RuntimeException("Organization slug already exists");
        }

        Organization organization = new Organization();

        organization.setName(request.getName());
        organization.setSlug(request.getSlug());
        organization.setStatus(OrganizationStatus.PENDING);

        LocalDateTime now = LocalDateTime.now();
        organization.setCreatedAt(now);
        organization.setUpdatedAt(now);

        Organization savedOrganization =
                organizationRepository.save(organization);

        return mapToResponse(savedOrganization);
    }

    private OrganizationResponse mapToResponse(Organization organization) {

        OrganizationResponse response = new OrganizationResponse();

        response.setId(organization.getId());
        response.setName(organization.getName());
        response.setSlug(organization.getSlug());
        response.setStatus(organization.getStatus());
        response.setCreatedAt(organization.getCreatedAt());
        response.setUpdatedAt(organization.getUpdatedAt());

        return response;
    }
}
