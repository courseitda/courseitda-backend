package courseitda.member.ui.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank String nickname,
        @NotBlank String email,
        @NotBlank @Size(min = 6, max = 20, message = "비밀번호는 6자 이상 20자 이하이어야 합니다.") String password
) {
}
