package courseitda.auth.infrastructure;

import courseitda.auth.domain.AuthTokenExtractor;
import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
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
            throw new BusinessException(ErrorCode.AUTH_TOKEN_NOT_FOUND);
        }

        if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN_FORMAT);
        }

        return authorizationHeader.substring(BEARER_PREFIX.length());
    }
}
