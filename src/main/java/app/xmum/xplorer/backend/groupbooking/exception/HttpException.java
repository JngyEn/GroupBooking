package app.xmum.xplorer.backend.groupbooking.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class HttpException extends RuntimeException  implements ApiException{
    @Getter
    private final HttpStatus httpStatus;
    private final String message;
    public HttpException(HttpStatus apiCode) {
        this(apiCode, apiCode.getReasonPhrase());
    }
    public HttpException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
    @Override
    public String getCode() {
        return httpStatus.name();
    }
    @Override
    public String getMessage() {
        return message;
    }
}
