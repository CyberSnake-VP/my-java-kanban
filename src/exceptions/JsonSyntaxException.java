package exceptions;

public class JsonSyntaxException extends RuntimeException {
    public JsonSyntaxException(String message) {
        super(message);
    }
}
