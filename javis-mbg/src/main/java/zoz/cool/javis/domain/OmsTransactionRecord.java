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
* 用户交易记录表
*/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("oms_transaction_record")
@ApiModel(value="OmsTransactionRecord对象", description="用户交易记录表")
public class OmsTransactionRecord implements Serializable {

private static final long serialVersionUID=1L;

    @ApiModelProperty("主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("交易ID")
    private String transactionId;

    @ApiModelProperty("交易金额")
    private BigDecimal transactionAmount;

    @ApiModelProperty("交易类型")
    private String transactionType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @ApiModelProperty("交易时间")
    private Date transactionTime;

    @ApiModelProperty("交易状态:SUCCEED-成功，FAILED-失败，CANCELED-取消")
    private String transactionStatus;

    @ApiModelProperty("备注信息")
    private String remark;

}