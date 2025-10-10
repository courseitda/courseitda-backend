package courseitda.member.ui.dto.response;

import courseitda.member.domain.Member;

public record SignUpResponse(
        Long id,
        String nickname,
        String email
) {

    public static SignUpResponse from(final Member member) {
        return new SignUpResponse(
                member.getId(),
                member.getNickname(),
                member.getEmail()
        );
    }
}
