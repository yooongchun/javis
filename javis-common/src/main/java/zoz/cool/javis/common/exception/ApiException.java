package zoz.cool.javis.common.exception;

import lombok.Getter;
import zoz.cool.javis.common.api.IErrorCode;

/**
 * 自定义API异常 Created by zhayongchun on 2023/11/16.
 */
@Getter
public class ApiException extends RuntimeException {
    private IErrorCode errorCode;

    public ApiException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(Throwable cause) {
        super(cause);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

}
