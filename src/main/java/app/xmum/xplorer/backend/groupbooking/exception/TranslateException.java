package app.xmum.xplorer.backend.groupbooking.exception;

import app.xmum.xplorer.backend.groupbooking.enums.ErrorCode;

public class TranslateException extends BaseException implements ApiException{
    public TranslateException(ErrorCode code, String msg) {
        super(code, msg);
    }

    public TranslateException(String msg) {
        super(msg);
    }

    @Override
    public String getCode() {
        return "";
    }
}
