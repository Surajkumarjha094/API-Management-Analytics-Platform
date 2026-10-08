package in.coderarmy.gatekeeper.management.dto.responseDto;

import java.time.LocalDateTime;

public class HealthResponse {

    private String serviceStatus;
    private String dbStatus;
    private LocalDateTime timestamp;

    public HealthResponse(
            String serviceStatus,
            String dbStatus,
            LocalDateTime timestamp) {

        this.serviceStatus = serviceStatus;
        this.dbStatus = dbStatus;
        this.timestamp = timestamp;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public String getDbStatus() {
        return dbStatus;
    }

    public void setDbStatus(String dbStatus) {
        this.dbStatus = dbStatus;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
