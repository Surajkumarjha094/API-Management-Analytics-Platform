package in.coderarmy.gatekeeper.gateway.ratelimit;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiter {

    private final Map<String, RequestCounter> counters =
            new ConcurrentHashMap<>();

    public boolean allowRequest(String key, int maxRequests) {

        long currentTime = System.currentTimeMillis();

        RequestCounter counter = counters.computeIfAbsent(
                key,
                k -> new RequestCounter(currentTime)
        );

        synchronized (counter) {

            // Reset counter after 1 minute
            if (currentTime - counter.windowStart >= 60_000) {
                counter.windowStart = currentTime;
                counter.count = 0;
            }

            if (counter.count >= maxRequests) {
                return false;
            }

            counter.count++;
            return true;
        }
    }

    private static class RequestCounter {

        private long windowStart;
        private int count;

        private RequestCounter(long windowStart) {
            this.windowStart = windowStart;
            this.count = 0;
        }
    }
}