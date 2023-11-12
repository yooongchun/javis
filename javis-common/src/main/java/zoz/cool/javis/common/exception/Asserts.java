package zoz.cool.javis.common.exception;

import zoz.cool.javis.common.api.IErrorCode;

/**
 * 断言处理类，用于抛出各种API异常 Created by zhayongchun on 2023/11/16.
 */
public class Asserts {
    public static void fail(String message) {
        throw new ApiException(message);
    }

    public static void fail(IErrorCode errorCode) {
        throw new ApiException(errorCode);
    }

    public static void failIfNull(Object obj, String message) {
        if (obj == null) {
            throw new ApiException(message);
        }
    }
}
