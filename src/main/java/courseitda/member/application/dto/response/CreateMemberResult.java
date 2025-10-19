package courseitda.member.application.dto.response;

import courseitda.member.domain.Member;

public record CreateMemberResult(
        Long id,
        String nickname,
        String email
) {

    public static CreateMemberResult from(final Member member) {
        return new CreateMemberResult(
                member.getId(),
                member.getNickname(),
                member.getEmail()
        );
    }
}
