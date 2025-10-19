package courseitda.member.application.dto.response;

import courseitda.member.domain.Member;

public record CreateMemberResponse(
        Long id,
        String nickname,
        String email
) {

    public static CreateMemberResponse from(final Member member) {
        return new CreateMemberResponse(
                member.getId(),
                member.getNickname(),
                member.getEmail()
        );
    }
}
