package zoz.cool.javis.common.api;

import lombok.Getter;

/**
 * API返回码接口 Created by zhayongchun on 2023/11/16.
 */

@Getter
public enum ResultCode implements IErrorCode {
    SUCCESS(200, "操作成功"), FAILED(200, "操作失败"),
    INTERNAL_ERROR(500, "服务器内部错误"), VALIDATE_FAILED(400, "参数检验失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"), FORBIDDEN(403, "没有相关权限"),
    NOT_FOUND(404, "找不到资源"), BUSINESS_SUCCESS(0, "成功"),
    BUSINESS_FAILED(1, "失败"), BUSINESS_ERROR(2, "错误"),
    BUSINESS_EXCEPTION(3, "异常"), BUSINESS_TIMEOUT(4, "超时"),
    BUSINESS_RETRY(5, "重试"), BUSINESS_UNKNOWN(6, "未知");

    private final long code;
    private final String message;

    ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }
}