package courseitda.auth.ui.dto.request;

import courseitda.auth.application.dto.request.LoginCommand;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String email,
        @NotBlank String password
) {

    public LoginCommand toCommand() {
        return new LoginCommand(email, password);
    }
}
