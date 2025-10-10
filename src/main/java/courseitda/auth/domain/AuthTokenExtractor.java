package courseitda.auth.domain;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthTokenExtractor<T> {

    @Nullable
    T extract(HttpServletRequest request);
}
