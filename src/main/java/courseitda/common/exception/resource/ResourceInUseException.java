package courseitda.common.exception.resource;

public class ResourceInUseException extends RuntimeException {

    public ResourceInUseException(final String message) {
        super(message);
    }
}
