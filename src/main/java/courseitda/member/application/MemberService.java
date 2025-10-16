package courseitda.member.application;

import courseitda.auth.domain.AuthRole;
import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.member.domain.Member;
import courseitda.member.domain.MemberRepository;
import courseitda.member.ui.dto.request.SignUpRequest;
import courseitda.member.ui.dto.response.SignUpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public SignUpResponse create(final SignUpRequest request) {
        validateDuplicateEmail(request.email());
        validateDuplicateNickname(request.nickname());

        final String encodedPassword = passwordEncoder.encode(request.password());

        final Member member = Member.builder()
                .nickname(request.nickname())
                .email(request.email())
                .password(encodedPassword)
                .authRole(AuthRole.MEMBER)
                .build();
        final Member createdMember = memberRepository.save(member);

        return SignUpResponse.from(createdMember);
    }

    public Member findById(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private void validateDuplicateEmail(final String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    private void validateDuplicateNickname(final String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }
}
