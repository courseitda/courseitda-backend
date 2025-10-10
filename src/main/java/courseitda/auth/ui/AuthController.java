package courseitda.auth.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import courseitda.auth.application.AuthService;
import courseitda.auth.ui.dto.request.LoginRequest;
import courseitda.auth.ui.dto.response.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid final LoginRequest request
    ) {
        final String authToken = authService.login(request);
        final LoginResponse loginResponse = new LoginResponse("Bearer", authToken);

        return ResponseEntity.ok(loginResponse);
    }
}
