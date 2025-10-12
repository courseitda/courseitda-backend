package courseitda.member.domain;

import courseitda.auth.domain.AuthRole;
import courseitda.common.entity.Timestamp;
import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Table(name = "members")
@SQLRestriction("deleted_at is Null")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
public class Member extends Timestamp {

    private static final Pattern MEMBER_EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AuthRole authRole;

    private String profileImageUrl;

    private LoginAuthenticationProvider loginAuthenticationProvider;

    private String loginAuthenticationIdentifier;

    private LocalDateTime deletedAt;

    @Builder
    public Member(
            final String nickname,
            final String email,
            final String password,
            final AuthRole authRole
    ) {
        validateNickname(nickname);
        validateEmail(email);

        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.authRole = authRole;
    }

    private void validateNickname(final String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new BusinessException(ErrorCode.MEMBER_NICKNAME_BLANK);
        }
        if (nickname.length() < 2 || nickname.length() > 20) {
            throw new BusinessException(ErrorCode.MEMBER_NICKNAME_LENGTH_INVALID);
        }
    }

    private void validateEmail(final String email) {
        if (email == null || email.isBlank()) {
            throw new BusinessException(ErrorCode.MEMBER_EMAIL_BLANK);
        }
        if (!MEMBER_EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException(ErrorCode.MEMBER_EMAIL_INVALID);
        }
    }
}
