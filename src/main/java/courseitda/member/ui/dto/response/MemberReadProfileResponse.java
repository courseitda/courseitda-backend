package courseitda.member.ui.dto.response;

import courseitda.member.domain.Member;

public record MemberReadProfileResponse(
        String nickName,
        String email
) {

    public static MemberReadProfileResponse from(final Member memeber) {
        return new MemberReadProfileResponse(
                memeber.getNickname(),
                memeber.getEmail()
        );
    }
}
