package zoz.cool.javis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zoz.cool.javis.domain.OmsAlipayOrder;
import zoz.cool.javis.dto.request.OmsAlipayOrderParam;
import zoz.cool.javis.mapper.OmsAlipayOrderMapper;
import zoz.cool.javis.service.OmsAlipayOrderService;
import zoz.cool.javis.service.OmsAlipayService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 支付宝订单管理Service实现类
 */
@Service
public class OmsAlipayOrderServiceImpl implements OmsAlipayOrderService {
    private final OmsAlipayOrderMapper alipayOrderMapper;
    private final OmsAlipayService alipayService;

    @Autowired
    public OmsAlipayOrderServiceImpl(OmsAlipayOrderMapper alipayOrderMapper, OmsAlipayService alipayService) {
        this.alipayOrderMapper = alipayOrderMapper;
        this.alipayService = alipayService;
    }

    @Override
    public OmsAlipayOrder create(OmsAlipayOrderParam aliPayParam) {
        String orderId = new SimpleDateFormat("yyyyMMdd").format(new Date()) + System.currentTimeMillis();
        aliPayParam.setOutTradeNo(orderId);
        // 调用支付宝创建订单
        OmsAlipayOrder alipayOrder = alipayService.pay(aliPayParam);
        alipayOrderMapper.insert(alipayOrder);
        // 异步查询订单状态
        alipayService.asyncQuery(orderId);
        return alipayOrder;
    }

    @Override
    public OmsAlipayOrder info(String orderId) {
        return alipayOrderMapper.selectOne(new QueryWrapper<OmsAlipayOrder>().eq("order_id", orderId));
    }
}