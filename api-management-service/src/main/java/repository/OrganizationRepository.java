package in.coderarmy.gatekeeper.management.repository;

import in.coderarmy.gatekeeper.management.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsByName(String name);
}
