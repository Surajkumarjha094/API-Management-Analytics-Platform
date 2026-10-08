package in.coderarmy.gatekeeper.management.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TenantIsolationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        

        // Only apply tenant isolation to organization APIs
        if (!requestUri.startsWith("/api/organizations/")) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
                

        // No authenticated user
        if (authentication == null ||
                !authentication.isAuthenticated()) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Extract organizationId from URL
        String[] parts = requestUri.split("/");

        if (parts.length < 4) {
            filterChain.doFilter(request, response);
            return;
        }

        Long requestedOrganizationId;

        try {
            requestedOrganizationId = Long.parseLong(parts[3]);
        } catch (NumberFormatException exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Platform admin can access any organization
        boolean isPlatformAdmin =
                authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch("ROLE_PLATFORM_ADMIN"::equals);

        if (isPlatformAdmin) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get organizationId stored in JWT authentication details
        Object details = authentication.getDetails();

        if (!(details instanceof Long)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Long userOrganizationId = (Long) details;

        // Tenant admin can access only its own organization
        if (!userOrganizationId.equals(requestedOrganizationId)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
