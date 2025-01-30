package app.xmum.xplorer.backend.groupbooking.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 成功
    SUCCESS(200, "Operation successful"),

    // 客户端错误
    BAD_REQUEST(400, "Invalid parameters"),
    UNAUTHORIZED(401, "Unauthorized access"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Resource not found"),

    // 服务器错误
    INTERNAL_ERROR(500, "Internal server error"),
    SERVICE_UNAVAILABLE(503, "Service unavailable"),

    /**
     * 通用错误标识码 四位数，对应编码1XXX开头，枚举COMM_*，对应公共异常如参数错误
     */

    // 输入为空
    COMMON_INPUT_EMPTY(1001, "Input is empty");

    private final int code; // HTTP 状态码
    private final String message; // 错误信息

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}