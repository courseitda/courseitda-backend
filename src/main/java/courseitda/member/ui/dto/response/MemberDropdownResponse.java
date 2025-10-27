package courseitda.member.ui.dto.response;

import courseitda.member.domain.Member;

public record MemberDropdownResponse(
        String nickname,
        String email
) {

    public static MemberDropdownResponse from(final Member member) {
        return new MemberDropdownResponse(
                member.getNickname(),
                member.getEmail()
        );
    }
}
