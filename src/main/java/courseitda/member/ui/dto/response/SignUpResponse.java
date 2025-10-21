package courseitda.member.ui.dto.response;

import courseitda.member.application.dto.response.CreateMemberResult;

public record SignUpResponse(
        Long id,
        String nickname,
        String email
) {

    public static SignUpResponse from(final CreateMemberResult result) {
        return new SignUpResponse(
                result.id(),
                result.nickname(),
                result.email()
        );
    }
}
