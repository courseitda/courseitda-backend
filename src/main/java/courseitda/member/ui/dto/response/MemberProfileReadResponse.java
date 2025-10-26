package courseitda.member.ui.dto.response;

import courseitda.member.domain.Member;

public record MemberProfileReadResponse(
        String nickname,
        String email
) {

    public static MemberProfileReadResponse from(final Member member) {
        return new MemberProfileReadResponse(
                member.getNickname(),
                member.getEmail()
        );
    }
}
