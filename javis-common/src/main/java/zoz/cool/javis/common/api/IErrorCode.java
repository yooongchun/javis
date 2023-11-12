package zoz.cool.javis.common.api;

/**
 * API返回码接口 Created by zhayongchun on 2023/11/16.
 */
public interface IErrorCode {
    /**
     * 返回码
     */
    long getCode();

    /**
     * 返回信息
     */
    String getMessage();
}
