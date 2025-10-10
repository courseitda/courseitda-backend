package courseitda.auth.ui.dto.response;

public record LoginResponse(
        String tokenType,
        String accessToken
) {
}
