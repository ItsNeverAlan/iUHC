package spg.lgdev.uhc.exception;

public class InvalidConfigException extends NullPointerException {

    private static final long serialVersionUID = 1L;

    public InvalidConfigException() {
        super();
    }

    public InvalidConfigException(String reason) {
        super(reason);
    }

}
