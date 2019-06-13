package spg.lgdev.uhc.exception;

public class NullConfigurationSectionException extends NullPointerException {

    private static final long serialVersionUID = 1L;

    public NullConfigurationSectionException() {
        super();
    }

    public NullConfigurationSectionException(String reason) {
        super(reason);
    }

}
