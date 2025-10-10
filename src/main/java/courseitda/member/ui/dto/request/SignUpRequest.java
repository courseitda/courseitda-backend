package courseitda.member.ui.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
                            @NotBlank String nickname,
                            @NotBlank String email,
                            @NotBlank String password
) {
}
