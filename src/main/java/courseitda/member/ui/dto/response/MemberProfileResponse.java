package courseitda.member.ui.dto.response;

import courseitda.member.domain.Member;

public record MemberProfileResponse(
        String nickname,
        String email
) {

    public static MemberProfileResponse from(final Member member) {
        return new MemberProfileResponse(
                member.getNickname(),
                member.getEmail()
        );
    }
}
