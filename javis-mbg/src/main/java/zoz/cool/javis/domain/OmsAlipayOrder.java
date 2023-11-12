package zoz.cool.javis.domain;

import java.io.Serializable;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.TableName;

/**
* 订单表
*/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("oms_alipay_order")
@ApiModel(value="OmsAlipayOrder对象", description="订单表")
public class OmsAlipayOrder implements Serializable {

private static final long serialVersionUID=1L;

    @ApiModelProperty("主键")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("订单ID")
    private String orderId;

    @ApiModelProperty("二维码链接")
    private String qrCode;

    @ApiModelProperty("订单标题/商品标题/交易标题")
    private String subject;

    @ApiModelProperty("订单总金额")
    private BigDecimal totalAmount;

    @ApiModelProperty("交易状态")
    private String tradeStatus;

    @ApiModelProperty("支付宝交易号")
    private String tradeNo;

    @ApiModelProperty("买家支付宝账号")
    private String buyerId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @ApiModelProperty("订单创建时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @ApiModelProperty("交易付款时间")
    private Date gmtPayment;

    @ApiModelProperty("用户在交易中支付的金额")
    private BigDecimal buyerPayAmount;

}