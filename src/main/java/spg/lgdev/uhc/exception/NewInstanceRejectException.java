package spg.lgdev.uhc.exception;

import org.apache.commons.lang.IllegalClassException;

public class NewInstanceRejectException extends IllegalClassException {

    private static final long serialVersionUID = 1L;

    public NewInstanceRejectException(String className) {
        super("plugin iUHC have reject you to new Instance on " + className + " !");
    }

}
