package in.coderarmy.gatekeeper.management.repository;

import in.coderarmy.gatekeeper.management.entity.RouteHeaderRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteHeaderRuleRepository extends JpaRepository<RouteHeaderRule, Long> {

    List<RouteHeaderRule> findByRoute_Id(Long routeId);

    void deleteByRoute_Id(Long routeId);
}
