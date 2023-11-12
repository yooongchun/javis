package zoz.cool.javis.service;

import org.springframework.scheduling.annotation.Async;
import zoz.cool.javis.domain.OmsAlipayOrder;
import zoz.cool.javis.dto.request.OmsAlipayOrderParam;

import java.util.Map;

/**
 * 支付宝支付Service
 */
public interface OmsAlipayService {
    /**
     * 根据提交参数生成支付二维码
     */
    OmsAlipayOrder pay(OmsAlipayOrderParam alipayOrderParam);

    /**
     * 支付宝异步回调处理
     */
    String notify(Map<String, String> params);

    /**
     * 异步查询
     */
    @Async
    void asyncQuery(String outTradeNo);
}