package com.capitalone.dashboard.model;



public class AuditException extends Exception{


    private static final long serialVersionUID = 4596406816345733781L;
    public static final int NO_COLLECTOR_ITEM_CONFIGURED = -1;
    public static final int MISSING_DETAILS = -2;
    public static final int BAD_INPUT_DATA = -3;


    private int errorCode = 0;

    public AuditException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AuditException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public AuditException(Throwable cause) {
        super(cause);
    }

    public AuditException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public int getErrorCode() {
        return errorCode;
    }
}
