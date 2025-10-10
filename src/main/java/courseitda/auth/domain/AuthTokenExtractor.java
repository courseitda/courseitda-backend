package courseitda.auth.domain;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthTokenExtractor<T> {

    String AUTH_TOKEN_NAME = "token";

    boolean isCookiesExist(HttpServletRequest request);

    @Nullable
    T extract(HttpServletRequest request);
}
