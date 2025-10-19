package courseitda.member.ui.dto.response;

import courseitda.member.application.dto.response.CreateMemberResponse;

public record SignUpResponse(
        Long id,
        String nickname,
        String email
) {

    public static SignUpResponse from(final CreateMemberResponse response) {
        return new SignUpResponse(
                response.id(),
                response.nickname(),
                response.email()
        );
    }
}
