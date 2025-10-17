package courseitda.auth.application.dto.request;

public record LoginCommand(
        String email,
        String password
) {
}
