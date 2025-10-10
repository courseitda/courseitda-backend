package courseitda.member.ui.dto.response;

import courseitda.member.domain.Member;

public record MemberReadDropdownResponse(
        String nickname,
        String email
) {

    public static MemberReadDropdownResponse from(final Member member) {
        return new MemberReadDropdownResponse(
                member.getNickname(),
                member.getEmail()
        );
    }
}
