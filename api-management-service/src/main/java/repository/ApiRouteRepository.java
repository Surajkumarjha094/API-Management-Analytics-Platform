package in.coderarmy.gatekeeper.management.repository;

import in.coderarmy.gatekeeper.management.entity.ApiRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApiRouteRepository extends JpaRepository<ApiRoute, Long> {

    @Query("""
            SELECT r
            FROM ApiRoute r
            WHERE r.organization.id = :organizationId
              AND r.routeId = :routeId
            """)
    Optional<ApiRoute> findByOrganization_IdAndRouteId(
            @Param("organizationId") Long organizationId,
            @Param("routeId") String routeId
    );

    @Query("""
            SELECT r
            FROM ApiRoute r
            WHERE r.organization.id = :organizationId
            """)
    List<ApiRoute> findByOrganization_Id(
            @Param("organizationId") Long organizationId
    );

    // Direct database check for route existence
    @Query(
            value = """
                    SELECT COUNT(*)
                    FROM api_routes
                    WHERE organization_id = :organizationId
                      AND TRIM(route_id) = TRIM(:routeId)
                    """,
            nativeQuery = true
    )
    long countRouteByOrganizationAndRouteId(
            @Param("organizationId") Long organizationId,
            @Param("routeId") String routeId
    );
}