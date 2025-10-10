package courseitda.exception.auth;

public class AuthTokenNotFoundException extends RuntimeException {

    public AuthTokenNotFoundException(String message) {
        super(message);
    }
}
