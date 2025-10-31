package courseitda.member.ui.dto.response;

public record SignUpResponse(
        Long id,
        String nickname,
        String email
) {

    public static SignUpResponse from(
            final Long id,
            final String nickname,
            final String email
    ) {
        return new SignUpResponse(
                id,
                nickname,
                email
        );
    }
}
