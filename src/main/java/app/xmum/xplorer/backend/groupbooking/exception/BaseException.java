package app.xmum.xplorer.backend.groupbooking.exception;

import app.xmum.xplorer.backend.groupbooking.enums.ErrorCode;

public class BaseException extends RuntimeException {
    private final ErrorCode statusCode;
    private final String msg;
    public BaseException(ErrorCode code, String msg) {
        super(msg);
        this.statusCode = code;
        this.msg = msg;
    }
    public BaseException(String msg) {
        super(msg);
        this.statusCode = ErrorCode.INTERNAL_ERROR;
        this.msg = msg;
    }
}
