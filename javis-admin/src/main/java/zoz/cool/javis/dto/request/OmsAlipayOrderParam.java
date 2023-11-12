package zoz.cool.javis.dto.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 支付宝支付请求参数
 */
@Data
public class OmsAlipayOrderParam {
    /**
     * 订单号
     */
    private String outTradeNo;
    /**
     * 商品的标题/交易标题/订单标题/订单关键字等
     */
    @NotNull(message = "subject不能为空")
    @Size(min = 5, max = 100, message = "订单标题长度必须在5到100之间")
    private String subject;
    /**
     * 订单总金额，单位为元，精确到小数点后两位
     */
    @NotNull(message = "totalAmount不能为空")
    @Min(value = 1, message = "订单金额不能小于1.0")
    @Max(value = 1000, message = "单笔订单金额不能大于1000")
    private BigDecimal totalAmount;
}
