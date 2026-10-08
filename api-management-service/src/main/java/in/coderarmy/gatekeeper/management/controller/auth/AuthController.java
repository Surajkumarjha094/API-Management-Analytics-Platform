package in.coderarmy.gatekeeper.management.controller.auth;

import in.coderarmy.gatekeeper.management.dto.auth.LoginRequest;
import in.coderarmy.gatekeeper.management.dto.auth.LoginResponse;
import in.coderarmy.gatekeeper.management.service.auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }
}
