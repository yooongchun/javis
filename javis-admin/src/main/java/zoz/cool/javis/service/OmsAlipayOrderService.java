package zoz.cool.javis.service;


import zoz.cool.javis.domain.OmsAlipayOrder;
import zoz.cool.javis.dto.request.OmsAlipayOrderParam;

/**
 * 支付宝订单管理Service
 */
public interface OmsAlipayOrderService {
    /**
     * 创建订单
     */
    OmsAlipayOrder create(OmsAlipayOrderParam aliPayParam);

    /**
     * 根据订单ID查询订单
     */
    OmsAlipayOrder info(String orderId);
}