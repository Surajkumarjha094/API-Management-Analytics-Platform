package in.coderarmy.gatekeeper.gateway.controller;

import in.coderarmy.gatekeeper.gateway.client.ManagementClient;
import in.coderarmy.gatekeeper.gateway.dto.RouteResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/gateway")
public class RouteTestController {

    private final ManagementClient managementClient;

    public RouteTestController(ManagementClient managementClient) {
        this.managementClient = managementClient;
    }

    @GetMapping("/routes")
    public List<RouteResponse> getRoutes(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");

        Long organizationId = 4L;

        return managementClient.getRoutes(organizationId, token);
    }
}
