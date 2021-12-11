package i4gl.exceptions;

public class NumberFormatterOverflowException  extends Exception {

    private static final long serialVersionUID = 892740234723024L;

    public NumberFormatterOverflowException(String pattern, long number) {
        super("Number " + number + " does not fit in pattern " + pattern);
    }
}