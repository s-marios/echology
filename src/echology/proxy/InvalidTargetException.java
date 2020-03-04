package echology.proxy;

/**
 * An exception raised representing an invalid target
 *
 * @author smarios
 */
public class InvalidTargetException extends Exception {

    public InvalidTargetException(String msg) {
        super(msg);
    }

    @Override
    public String toString() {
        return "Invalid Target Exception: " + this.getMessage();
    }
}
