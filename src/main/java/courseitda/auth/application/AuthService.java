package courseitda.auth.application;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import courseitda.auth.domain.AuthTokenProvider;
import courseitda.auth.ui.dto.request.LoginRequest;
import courseitda.exception.auth.AuthenticationException;
import courseitda.exception.resource.ResourceNotFoundException;
import courseitda.member.domain.Member;
import courseitda.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthTokenProvider authTokenProvider;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public String login(final LoginRequest request) {
        final Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("해당 이메일을 가진 회원이 존재하지 않습니다."));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new AuthenticationException("비밀번호가 올바르지 않습니다.");
        }

        return authTokenProvider.createAccessToken(member.getId().toString(), member.getAuthRole());
    }
}
