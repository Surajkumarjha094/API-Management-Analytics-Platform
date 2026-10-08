package in.coderarmy.gatekeeper.analytics;

import in.coderarmy.gatekeeper.analytics.dto.AnalyticsEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AnalyticsEventRepository
        extends MongoRepository<AnalyticsEvent, String> {

    List<AnalyticsEvent> findByOrganizationId(Long organizationId);

    List<AnalyticsEvent> findByRouteId(String routeId);
}