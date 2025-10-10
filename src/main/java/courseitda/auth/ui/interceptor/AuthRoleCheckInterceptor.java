package courseitda.auth.ui.interceptor;

import java.util.Arrays;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import courseitda.auth.domain.AuthRole;
import courseitda.auth.domain.AuthTokenExtractor;
import courseitda.auth.domain.AuthTokenProvider;
import courseitda.auth.domain.RequiresRole;
import courseitda.exception.auth.AuthenticationException;
import courseitda.exception.auth.AuthorizationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthRoleCheckInterceptor implements HandlerInterceptor {

    private final AuthTokenExtractor<String> authTokenExtractor;
    private final AuthTokenProvider authTokenProvider;

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) throws AuthenticationException {
        if (!(handler instanceof final HandlerMethod handlerMethod)) {
            return true;
        }

        // 1. 클래스에 @RequiresRole 어노테이션이 붙어있는지 확인
        RequiresRole requiresRole = handlerMethod.getMethodAnnotation(RequiresRole.class);

        // 2. 메서드에 @RequiresRole 어노테이션이 붙어있지 않으면 클래스에 붙어있는지 확인
        if (requiresRole == null) {
            requiresRole = handlerMethod.getBeanType().getAnnotation(RequiresRole.class);
        }

        if (requiresRole == null) {
            return true;
        }

        final String accessToken = authTokenExtractor.extract(request);
        if (!authTokenProvider.isValidToken(accessToken)) {
            throw new AuthenticationException("유효하지 않은 토큰입니다.");
        }

        final AuthRole role = authTokenProvider.getRole(accessToken);
        if (Arrays.stream(requiresRole.authRoles())
                .noneMatch(authRole -> authRole == role)) {
            throw new AuthorizationException("권한이 없습니다.");
        }
        return true;
    }
}
