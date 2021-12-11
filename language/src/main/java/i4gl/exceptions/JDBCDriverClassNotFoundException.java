package i4gl.exceptions;

public class JDBCDriverClassNotFoundException  extends I4GLRuntimeException {
    private static final long serialVersionUID = -323645849149800269L;

    public JDBCDriverClassNotFoundException() {
        super("Could not find JDBC driver class");
    }
}
