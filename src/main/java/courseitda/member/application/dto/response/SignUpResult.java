package courseitda.member.application.dto.response;

import courseitda.member.domain.Member;

public record SignUpResult(
        Long id,
        String nickname,
        String email
) {

    public static SignUpResult from(final Member member) {
        return new SignUpResult(
                member.getId(),
                member.getNickname(),
                member.getEmail()
        );
    }
}
