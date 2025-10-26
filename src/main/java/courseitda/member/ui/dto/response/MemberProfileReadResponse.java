package courseitda.member.ui.dto.response;

import courseitda.member.domain.Member;

public record MemberProfileReadResponse(
        String nickName,
        String email
) {

    public static MemberProfileReadResponse from(final Member memeber) {
        return new MemberProfileReadResponse(
                memeber.getNickname(),
                memeber.getEmail()
        );
    }
}
