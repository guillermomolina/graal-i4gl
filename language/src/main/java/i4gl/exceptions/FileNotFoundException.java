package i4gl.exceptions;

/**
 * Exception thrown when a file with specified path can not be found.
 */
public class FileNotFoundException extends I4GLRuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 4907589282417642659L;

    public FileNotFoundException(String filePath) {
        super("File " + filePath + " not found");
    }

}
