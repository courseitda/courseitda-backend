package courseitda.member.application;

import courseitda.auth.domain.AuthRole;
import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.member.domain.Member;
import courseitda.member.domain.MemberRepository;
import courseitda.member.ui.dto.request.SignUpRequest;
import courseitda.member.ui.dto.response.CheckEmailDuplicateResponse;
import courseitda.member.ui.dto.response.CheckNicknameDuplicateResponse;
import courseitda.member.ui.dto.response.SignUpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public SignUpResponse signUp(final SignUpRequest request) {
        validatePassword(request.password());
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

        return SignUpResponse.from(
                createdMember.getId(),
                createdMember.getNickname(),
                createdMember.getEmail()
        );
    }

    public Member findById(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public CheckNicknameDuplicateResponse checkNicknameDuplicate(final String nickname) {
        validateNicknameNotEmpty(nickname);

        final boolean isDuplicated = memberRepository.existsByNickname(nickname);

        return new CheckNicknameDuplicateResponse(isDuplicated);
    }

    public CheckEmailDuplicateResponse checkEmailDuplicate(final String email) {
        validateEmailNotEmpty(email);
        validateEmailFormat(email);

        final boolean isDuplicated = memberRepository.existsByEmail(email);

        return new CheckEmailDuplicateResponse(isDuplicated);
    }

    private void validateNicknameNotEmpty(final String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new BusinessException(ErrorCode.MEMBER_NICKNAME_EMPTY);
        }
    }

    private void validateEmailNotEmpty(final String email) {
        if (email == null || email.isBlank()) {
            throw new BusinessException(ErrorCode.MEMBER_EMAIL_EMPTY);
        }
    }

    private void validateEmailFormat(final String email) {
        final String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL_FORMAT);
        }
    }

    private void validatePassword(final String password) {
        if (password == null || password.isBlank()) {
            throw new BusinessException(ErrorCode.MEMBER_PASSWORD_EMPTY);
        }
        if (password.length() < 6 || password.length() > 20) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD_LENGTH);
        }
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
