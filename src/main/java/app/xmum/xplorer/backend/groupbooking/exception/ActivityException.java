package app.xmum.xplorer.backend.groupbooking.exception;

import app.xmum.xplorer.backend.groupbooking.enums.ErrorCode;

public class ActivityException extends BaseException {
    public ActivityException(String message) {
        super(message);
    }
    public ActivityException(ErrorCode code, String cause) {
        super(code, cause);
    }
}
