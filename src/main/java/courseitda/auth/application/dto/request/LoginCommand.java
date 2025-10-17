package courseitda.auth.application.dto.request;

import courseitda.auth.ui.dto.request.LoginRequest;

public record LoginCommand(
        String email,
        String password
) {

    public static LoginCommand from(final LoginRequest request) {
        return new LoginCommand(
                request.email(),
                request.password()
        );
    }
}
