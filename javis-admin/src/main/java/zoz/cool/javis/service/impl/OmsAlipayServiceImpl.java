package zoz.cool.javis.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;
import zoz.cool.javis.common.constants.AlipayOrderConstant;
import zoz.cool.javis.common.exception.ApiException;
import zoz.cool.javis.config.AlipayConfig;
import zoz.cool.javis.domain.OmsAccountBalance;
import zoz.cool.javis.domain.OmsAlipayOrder;
import zoz.cool.javis.dto.request.OmsAlipayOrderParam;
import zoz.cool.javis.mapper.OmsAlipayOrderMapper;
import zoz.cool.javis.service.OmsAccountBalanceService;
import zoz.cool.javis.service.OmsAlipayService;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 支付宝支付Service实现类
 */
@Slf4j
@Service
public class OmsAlipayServiceImpl implements OmsAlipayService {
    private final AlipayConfig alipayConfig;
    private final AlipayClient alipayClient;
    private final OmsAlipayOrderMapper alipayOrderMapper;
    private static final ObjectMapper mapper;

    private final OmsAccountBalanceService accountBalanceService;

    static {
        mapper = Jackson2ObjectMapperBuilder.json().simpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .featuresToEnable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature())
                .build();
    }


    @Autowired
    public OmsAlipayServiceImpl(AlipayConfig alipayConfig, AlipayClient alipayClient, OmsAlipayOrderMapper alipayOrderMapper, OmsAccountBalanceService accountBalanceService) {
        this.alipayConfig = alipayConfig;
        this.alipayClient = alipayClient;
        this.alipayOrderMapper = alipayOrderMapper;
        this.accountBalanceService = accountBalanceService;
    }

    @Override
    public String notify(Map<String, String> params) {
        String result = "failure";
        boolean signVerified;
        try {
            //调用SDK验证签名
            String alipayPublicKey = alipayConfig.getAlipayPublicKey();
            signVerified = AlipaySignature.rsaCheckV1(params, alipayPublicKey, alipayConfig.getCharset(), alipayConfig.getSignType());
        } catch (AlipayApiException e) {
            log.error("支付回调签名校验异常！", e);
            return result;
        }
        if (signVerified) {
            String tradeStatus = params.get("trade_status");
            if ("TRADE_SUCCESS".equals(tradeStatus)) {
                result = "success";
                OmsAlipayOrder alipayOrder = BeanUtil.mapToBean(params, OmsAlipayOrder.class, true, null);
                alipayOrder.setOrderId(params.get("out_trade_no"));
                log.info("[notify]订单支付成功，alipayOrder:{}", JSONUtil.toJsonStr(alipayOrder));
                // 根据orderId查询订单，并修改订单状态
                OmsAlipayOrder order = alipayOrderMapper.selectOne(new QueryWrapper<OmsAlipayOrder>().eq("order_id", alipayOrder.getOrderId()));
                BeanUtils.copyProperties(alipayOrder, order);
                alipayOrderMapper.updateById(order);
            } else {
                log.warn("[notify]订单未支付成功，trade_status:{}", tradeStatus);
            }
        } else {
            log.warn("支付回调签名校验失败！params:{}", params);
        }
        return result;
    }

    @Override
    public void asyncQuery(String outTradeNo) {
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(alipayConfig.getMaxQueryTime());
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        final String[] previousTradeStatus = {AlipayOrderConstant.WAIT_BUYER_PAY.name()};
        executorService.scheduleAtFixedRate(() -> {
            if (LocalDateTime.now().isAfter(expireTime)) {
                if (!previousTradeStatus[0].equals(AlipayOrderConstant.TRADE_SUCCESS.name()) && !previousTradeStatus[0].equals(AlipayOrderConstant.TRADE_FINISHED.name())) {
                    log.warn("[AlipayOrder.AsyncQuery] 支付超时，tradeStatus:{}", previousTradeStatus[0]);
                }
                executorService.shutdown();
            }
            AlipayTradeQueryResponse trade = query(outTradeNo);
            if (trade == null) {
                return;
            }
            if (!trade.isSuccess()) {
                log.warn("[AlipayOrder.AsyncQuery] trade not success, response.body: {}", trade.getBody());
                return;
            }
            String tradeStatus = trade.getTradeStatus();
            if (trade.getTradeStatus().equals(previousTradeStatus[0])) {
                log.info("[AlipayOrder.AsyncQuery] 订单状态未改变，tradeStatus:{}", trade.getTradeStatus());
                return;
            }
            log.info("[AlipayOrder.AsyncQuery] 更新订单状态，tradeStatus:{}-->{}", previousTradeStatus[0], tradeStatus);
            previousTradeStatus[0] = tradeStatus;
            OmsAlipayOrder order = alipayOrderMapper.selectOne(new QueryWrapper<OmsAlipayOrder>().eq("order_id", outTradeNo));
            order.setTradeStatus(tradeStatus);
            order.setBuyerId(trade.getBuyerUserId());
            order.setBuyerPayAmount(new BigDecimal(trade.getBuyerPayAmount()));
            order.setTradeNo(trade.getTradeNo());
            order.setGmtPayment(trade.getSendPayDate());
            alipayOrderMapper.updateById(order);
            if (tradeStatus.equals(AlipayOrderConstant.TRADE_SUCCESS.name()) || tradeStatus.equals(AlipayOrderConstant.TRADE_FINISHED.name())) {
                // 支付成功需要更新账户余额
                if (tradeStatus.equals(AlipayOrderConstant.TRADE_SUCCESS.name())) {
                    OmsAccountBalance account = accountBalanceService.getOne(new QueryWrapper<OmsAccountBalance>().eq("user_id", order.getUserId()));
                    if (account == null) {
                        account = new OmsAccountBalance();
                        account.setUserId(order.getUserId());
                    }
                    BigDecimal balance = account.getBalance();
                    account.setBalance(balance.add(order.getTotalAmount()));
                    log.info("[AlipayOrder.AsyncQuery] 更新账户，account:{}-->{}", balance, account.getBalance());
                    accountBalanceService.save(account);
                }
                log.info("[AlipayOrder.AsyncQuery] 订单完成，tradeStatus:{}", tradeStatus);
                executorService.shutdown();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private AlipayTradeQueryResponse query(String outTradeNo) {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        //******必传参数******
        JSONObject bizContent = new JSONObject();
        //设置查询参数，out_trade_no和trade_no至少传一个
        bizContent.put("out_trade_no", outTradeNo);
        //交易结算信息: trade_settle_info
        String[] queryOptions = {"trade_settle_info"};
        bizContent.put("query_options", queryOptions);

        request.setBizContent(bizContent.toString());
        try {
            return alipayClient.execute(request);
        } catch (AlipayApiException e) {
            log.error("查询支付宝账单异常！", e);
            return null;
        }
    }

    @Override
    public OmsAlipayOrder pay(OmsAlipayOrderParam alipayOrderParam) {
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        if (StrUtil.isNotEmpty(alipayConfig.getNotifyUrl())) {
            //异步接收地址，公网可访问
            request.setNotifyUrl(alipayConfig.getNotifyUrl());
        }
        if (StrUtil.isNotEmpty(alipayConfig.getReturnUrl())) {
            //同步跳转地址
            request.setReturnUrl(alipayConfig.getReturnUrl());
        }
        // ******必传参数******
        JSONObject bizContent = getBizContent(alipayOrderParam);
        request.setBizContent(bizContent.toString());
        String result;
        try {
            result = alipayClient.execute(request).getBody();
        } catch (AlipayApiException e) {
            log.error("请求支付宝创建订单失败" + e);
            throw new ApiException("请求支付宝创建订单失败");
        }
        JsonNode rootNode;
        try {
            rootNode = mapper.readValue(result, JsonNode.class);
        } catch (JsonProcessingException e) {
            log.error("请求支付宝创建订单失败", e);
            throw new ApiException("请求支付宝创建订单失败");
        }
        JsonNode node = rootNode.get("alipay_trade_precreate_response");
        if (!node.get("code").asText().equals("10000")) {
            log.error("请求支付宝创建订单失败, body: {}", result);
            throw new ApiException("请求支付宝创建订单失败");
        }

        OmsAlipayOrder alipayOrder = new OmsAlipayOrder();
        alipayOrder.setOrderId(alipayOrderParam.getOutTradeNo());
        alipayOrder.setSubject(alipayOrderParam.getSubject());
        alipayOrder.setTotalAmount(alipayOrderParam.getTotalAmount());
        alipayOrder.setTradeStatus("WAIT_BUYER_PAY");
        alipayOrder.setQrCode(node.get("qr_code").asText());
        alipayOrder.setUserId(StpUtil.getLoginIdAsLong());
        return alipayOrder;
    }

    @NotNull
    private static JSONObject getBizContent(OmsAlipayOrderParam alipayOrderParam) {
        JSONObject bizContent = new JSONObject();
        //商户订单号，商家自定义，保持唯一性
        bizContent.put("out_trade_no", alipayOrderParam.getOutTradeNo());
        //支付金额，最小值0.01元
        bizContent.put("total_amount", alipayOrderParam.getTotalAmount());
        //订单标题，不可使用特殊符号
        bizContent.put("subject", alipayOrderParam.getSubject());
        bizContent.put("scene", "bar_code");
        return bizContent;
    }
}