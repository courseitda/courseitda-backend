package courseitda.member.application.dto.request;

public record SignUpCommand(
        String nickname,
        String email,
        String password
) {
}
