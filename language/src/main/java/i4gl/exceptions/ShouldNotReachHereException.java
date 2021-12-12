package i4gl.exceptions;

public class ShouldNotReachHereException extends UnsupportedOperationException {

    private static final long serialVersionUID = -348573950734584507L;

    public ShouldNotReachHereException() {
        super("Should not reach here exception occurred");
    }
 
    public ShouldNotReachHereException(Exception e) {
        super(e.getMessage());
    }
  
    public ShouldNotReachHereException(final String message) {
        super(message);
    }
  
}
