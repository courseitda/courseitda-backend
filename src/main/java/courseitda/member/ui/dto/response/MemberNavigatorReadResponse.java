package courseitda.member.ui.dto.response;

import courseitda.member.domain.Member;

public record MemberNavigatorReadResponse(
        String nickname
) {

    public static MemberNavigatorReadResponse from(final Member member) {
        return new MemberNavigatorReadResponse(member.getNickname());
    }
}
