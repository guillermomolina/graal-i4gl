package i4gl.parser.exceptions;

public class TypeMismatchException extends LexicalException {
    private static final long serialVersionUID = 8228596073240133854L;

    public TypeMismatchException(final String targetType, final String sourceType) {
        super("Can not assign a \"" + sourceType + "\" to a \"" + targetType + "\"");
    }
}
