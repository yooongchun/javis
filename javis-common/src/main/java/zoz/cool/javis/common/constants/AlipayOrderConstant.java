package zoz.cool.javis.common.constants;

import lombok.Getter;

/**
 * Created by zhayongchun on 2023/11/29.
 */
@Getter
public enum AlipayOrderConstant {
    WAIT_BUYER_PAY(1, "交易创建，等待买家付款"),
    TRADE_CLOSED(2, "未付款交易超时关闭，或支付完成后全额退款"),
    TRADE_SUCCESS(3, "交易支付成功"),
    TRADE_FINISHED(4, "交易结束，不可退款");

    private final int code;
    private final String status;

    AlipayOrderConstant(int code, String status) {
        this.code = code;
        this.status = status;
    }
}
