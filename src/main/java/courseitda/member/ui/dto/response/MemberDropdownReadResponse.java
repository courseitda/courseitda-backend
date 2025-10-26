package courseitda.member.ui.dto.response;

import courseitda.member.domain.Member;

public record MemberDropdownReadResponse(
        String nickname,
        String email
) {

    public static MemberDropdownReadResponse from(final Member member) {
        return new MemberDropdownReadResponse(
                member.getNickname(),
                member.getEmail()
        );
    }
}
