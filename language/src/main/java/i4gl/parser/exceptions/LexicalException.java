package i4gl.parser.exceptions;

public class LexicalException extends Exception {
    private static final long serialVersionUID = -8347238723547896294L;
    private final String message;

    public LexicalException(final String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
