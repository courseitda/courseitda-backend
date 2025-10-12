package courseitda.auth.infrastructure;

import courseitda.auth.domain.AuthTokenExtractor;
import courseitda.common.exception.auth.AuthTokenNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenExtractor implements AuthTokenExtractor<String> {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public String extract(final HttpServletRequest request) {
        final String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new AuthTokenNotFoundException("Authorization 헤더가 존재하지 않습니다.");
        }

        if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new AuthTokenNotFoundException("Bearer 토큰 형식이 아닙니다.");
        }

        return authorizationHeader.substring(BEARER_PREFIX.length());
    }
}
