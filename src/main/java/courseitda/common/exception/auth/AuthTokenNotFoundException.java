package courseitda.common.exception.auth;

public class AuthTokenNotFoundException extends RuntimeException {

    public AuthTokenNotFoundException(final String message) {
        super(message);
    }
}
