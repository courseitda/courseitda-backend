package courseitda.member.application;

import courseitda.auth.domain.AuthRole;
import courseitda.member.domain.Member;
import courseitda.member.domain.MemberRepository;
import courseitda.member.ui.dto.request.SignUpRequest;
import courseitda.member.ui.dto.response.SignUpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public SignUpResponse create(final SignUpRequest request) {
        final Member member = Member.builder()
                .nickname(request.nickname())
                .email(request.email())
                .password(request.password())
                .authRole(AuthRole.MEMBER)
                .build();
        final Member createdMember = memberRepository.save(member);

        return SignUpResponse.from(createdMember);
    }
}
