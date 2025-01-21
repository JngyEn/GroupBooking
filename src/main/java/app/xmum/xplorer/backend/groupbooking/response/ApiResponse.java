package app.xmum.xplorer.backend.groupbooking.response;

import app.xmum.xplorer.backend.groupbooking.enums.ErrorCode;
import lombok.Data;

@Data
public class ApiResponse<T> {
    private int code;       // HTTP 状态码
    private String message; // 返回信息
    private T data;         // 返回的数据
    private Object error;   // 错误详情

    // 私有构造函数，禁止外部直接创建实例
    private ApiResponse() {}

    // 成功响应（无参）
    public static ApiResponse<?> success() {
        return success(null);
    }

    // 成功响应（有参）
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(ErrorCode.SUCCESS.getCode());
        response.setMessage(ErrorCode.SUCCESS.getMessage());
        response.setData(data);
        return response;
    }

    // 失败响应（使用 ErrorCode）
    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(errorCode.getCode());
        response.setMessage(errorCode.getMessage());
        return response;
    }

    // 失败响应（使用 ErrorCode 和自定义错误详情）
    public static <T> ApiResponse<T> fail(ErrorCode errorCode, Object errorDetails) {
        ApiResponse<T> response = fail(errorCode);
        response.setError(errorDetails);
        return response;
    }
}