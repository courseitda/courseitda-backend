package courseitda.member.application.dto.request;

import courseitda.member.ui.dto.request.SignUpRequest;

public record SignUpCommand(
        String nickname,
        String email,
        String password
) {

    public static SignUpCommand from(final SignUpRequest request) {
        return new SignUpCommand(
                request.nickname(),
                request.email(),
                request.password()
        );
    }
}
