package zoz.cool.javis.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zoz.cool.javis.common.api.CommonResult;
import zoz.cool.javis.domain.OmsAlipayOrder;
import zoz.cool.javis.dto.request.OmsAlipayOrderParam;
import zoz.cool.javis.service.OmsAlipayOrderService;

import javax.validation.Valid;

/**
 * 支付宝订单管理Controller
 */
@RestController
@Api(tags = "AlipayOrderController")
@Tag(name = "AlipayOrderController", description = "支付宝订单管理")
@RequestMapping("/alipay/order")
public class OmsAlipayOrderController {

    private final OmsAlipayOrderService alipayOrderService;

    @Autowired
    public OmsAlipayOrderController(OmsAlipayOrderService alipayOrderService) {
        this.alipayOrderService = alipayOrderService;
    }

    @ApiOperation("创建订单")
    @PostMapping("/create")
    @ResponseBody
    @SaCheckLogin
    public CommonResult<OmsAlipayOrder> create(@Valid @RequestBody OmsAlipayOrderParam aliPayParam) {
        OmsAlipayOrder order = alipayOrderService.create(aliPayParam);
        if (order == null) {
            return CommonResult.failed("创建订单失败，请稍后再试！");
        }
        return CommonResult.success(order);
    }

    @ApiOperation("查询订单")
    @GetMapping("/info/{orderId}")
    @ResponseBody
    @SaCheckLogin
    public CommonResult<OmsAlipayOrder> info(@PathVariable String orderId) {
        OmsAlipayOrder order = alipayOrderService.info(orderId);
        return CommonResult.success(order);
    }
}
