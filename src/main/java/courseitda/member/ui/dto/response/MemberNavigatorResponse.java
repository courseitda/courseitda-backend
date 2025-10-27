package courseitda.member.ui.dto.response;

import courseitda.member.domain.Member;

public record MemberNavigatorResponse(
        String nickname
) {

    public static MemberNavigatorResponse from(final Member member) {
        return new MemberNavigatorResponse(member.getNickname());
    }
}
