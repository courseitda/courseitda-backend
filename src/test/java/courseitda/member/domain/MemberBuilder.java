package courseitda.member.domain;

import courseitda.auth.domain.AuthRole;
import java.time.LocalDateTime;

public class MemberBuilder {

    private String nickname = MemberFixture.anyNickname();
    private String email = MemberFixture.anyEmail();
    private String password = MemberFixture.anyPassword();
    private AuthRole authRole = AuthRole.MEMBER;
    private String profileImageUrl = MemberFixture.anyProfileImageUrl();
    private LoginAuthenticationProvider loginAuthenticationProvider = LoginAuthenticationProvider.COURSEITDA;
    private String loginAuthenticationIdentifier = MemberFixture.anyLoginAuthenticationIdentifier();
    private LocalDateTime deletedAt;

    public MemberBuilder nickname(final String nickname) {
        this.nickname = nickname;
        return this;
    }

    public MemberBuilder email(final String email) {
        this.email = email;
        return this;
    }

    public MemberBuilder password(final String password) {
        this.password = password;
        return this;
    }

    public MemberBuilder authRole(final AuthRole authRole) {
        this.authRole = authRole;
        return this;
    }

    public MemberBuilder profileImageUrl(final String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        return this;
    }

    public MemberBuilder loginAuthenticationProvider(final LoginAuthenticationProvider loginAuthenticationProvider) {
        this.loginAuthenticationProvider = loginAuthenticationProvider;
        return this;
    }

    public MemberBuilder loginAuthenticationIdentifier(final String loginAuthenticationIdentifier) {
        this.loginAuthenticationIdentifier = loginAuthenticationIdentifier;
        return this;
    }

    public MemberBuilder deletedAt(final LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

    public Member build() {
        return new Member(
                null,
                nickname,
                email,
                password,
                authRole,
                profileImageUrl,
                loginAuthenticationProvider,
                loginAuthenticationIdentifier,
                deletedAt
        );
    }
}
