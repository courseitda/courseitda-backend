package courseitda.member.ui.dto.request;

import courseitda.member.application.dto.request.SignUpCommand;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
        @NotBlank String nickname,
        @NotBlank String email,
        @NotBlank String password
) {

    public SignUpCommand toCommand() {
        return new SignUpCommand(nickname, email, password);
    }
}
