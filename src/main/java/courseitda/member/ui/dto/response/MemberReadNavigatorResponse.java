package courseitda.member.ui.dto.response;

import courseitda.member.domain.Member;

public record MemberReadNavigatorResponse(
        String nickname
) {

    public static MemberReadNavigatorResponse from(final Member member) {
        return new MemberReadNavigatorResponse(member.getNickname());
    }
}
