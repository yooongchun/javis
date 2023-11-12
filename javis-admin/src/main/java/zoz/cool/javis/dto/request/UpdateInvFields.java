package zoz.cool.javis.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UpdateInvFields {
    @ApiModelProperty("是否已人工校验,0-->否，1-->是")
    private Boolean checked;
    @ApiModelProperty("是否已解析,0-->否，1-->是")
    private Boolean reimbursed;
    @ApiModelProperty("发票代码")
    @JsonProperty("inv_code")
    private String invCode;
    @ApiModelProperty("发票号码")
    @JsonProperty("inv_num")
    private String invNum;
    @ApiModelProperty("校验码")
    @JsonProperty("inv_chk")
    private String invChk;
    @ApiModelProperty("开票日期")
    @JsonProperty("inv_date")
    private Date invDate;
    @ApiModelProperty("开具金额")
    @JsonProperty("inv_money")
    private BigDecimal invMoney;
    @ApiModelProperty("税额")
    @JsonProperty("inv_tax")
    private String invTax;
    @ApiModelProperty("价税合计")
    @JsonProperty("inv_total")
    private BigDecimal invTotal;
    @ApiModelProperty("备注")
    private String remark;
}
