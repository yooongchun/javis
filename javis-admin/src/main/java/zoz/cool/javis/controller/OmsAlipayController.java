package zoz.cool.javis.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import zoz.cool.javis.service.OmsAlipayService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝订单管理Controller
 */
@Controller
@Api(tags = "AlipayOrderController")
@Tag(name = "AlipayOrderController", description = "支付宝订单管理")
@RequestMapping("/alipay/order")
public class OmsAlipayController {
    private final OmsAlipayService alipayService;

    @Autowired
    public OmsAlipayController(OmsAlipayService alipayService) {
        this.alipayService = alipayService;
    }

    @ApiOperation(value = "支付宝异步回调", notes = "必须为POST请求，执行成功返回success，执行失败返回failure")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            params.put(name, request.getParameter(name));
        }
        return alipayService.notify(params);
    }

}