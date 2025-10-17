package courseitda.auth.application;

import courseitda.auth.application.dto.request.LoginCommand;
import courseitda.auth.domain.AuthTokenProvider;
import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.member.domain.Member;
import courseitda.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthTokenProvider authTokenProvider;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public String login(final LoginCommand command) {
        final Member member = memberRepository.findByEmail(command.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND_BY_EMAIL));

        if (!passwordEncoder.matches(command.password(), member.getPassword())) {
            throw new BusinessException(ErrorCode.INCORRECT_PASSWORD);
        }

        return authTokenProvider.createAccessToken(member.getId().toString(), member.getAuthRole());
    }
}
