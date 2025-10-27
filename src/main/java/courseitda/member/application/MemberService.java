package courseitda.member.application;

import courseitda.auth.domain.AuthRole;
import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.member.application.dto.request.CheckEmailDuplicateCommand;
import courseitda.member.application.dto.request.CheckNicknameDuplicateCommand;
import courseitda.member.application.dto.request.SignUpCommand;
import courseitda.member.application.dto.response.CheckEmailDuplicateResult;
import courseitda.member.application.dto.response.CheckNicknameDuplicateResult;
import courseitda.member.application.dto.response.SignUpResult;
import courseitda.member.domain.Member;
import courseitda.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public SignUpResult create(final SignUpCommand command) {
        validateDuplicateEmail(command.email());
        validateDuplicateNickname(command.nickname());

        final String encodedPassword = passwordEncoder.encode(command.password());

        final Member member = Member.builder()
                .nickname(command.nickname())
                .email(command.email())
                .password(encodedPassword)
                .authRole(AuthRole.MEMBER)
                .build();
        final Member createdMember = memberRepository.save(member);

        return SignUpResult.from(createdMember);
    }

    public Member findById(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public CheckNicknameDuplicateResult checkNicknameDuplicate(final CheckNicknameDuplicateCommand command) {
        validateNicknameNotEmpty(command.nickname());

        final boolean isDuplicated = memberRepository.existsByNickname(command.nickname());

        return new CheckNicknameDuplicateResult(isDuplicated);
    }

    public CheckEmailDuplicateResult checkEmailDuplicate(final CheckEmailDuplicateCommand command) {
        validateEmailNotEmpty(command.email());
        validateEmailFormat(command.email());

        final boolean isDuplicated = memberRepository.existsByEmail(command.email());

        return new CheckEmailDuplicateResult(isDuplicated);
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
