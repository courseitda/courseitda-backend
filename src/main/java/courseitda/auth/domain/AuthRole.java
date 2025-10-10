package courseitda.auth.domain;

import lombok.Getter;

@Getter
public enum AuthRole {

    ADMIN("관리자"), // 시스템 관리자
    MEMBER("일반 회원"), // 회원가입한 일반 회원
    GUEST("게스트") // 회원가입하지 않은 게스트
    ;

    private final String roleName;

    AuthRole(final String roleName) {
        this.roleName = roleName;
    }

    public static AuthRole from(final String roleName) {
        for (final AuthRole authRole : values()) {
            if (authRole.getRoleName().equals(roleName)) {
                return authRole;
            }
        }
        return GUEST;
    }
}
