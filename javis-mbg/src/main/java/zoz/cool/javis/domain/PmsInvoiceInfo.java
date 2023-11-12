package zoz.cool.javis.domain;

import java.io.Serializable;

import java.util.Date;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.TableName;
import zoz.cool.javis.common.util.json_util.JsonNodeToStringDeserializer;
import zoz.cool.javis.common.util.json_util.StringToJsonNodeSerializer;

/**
 * 发票信息表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("pms_invoice_info")
@ApiModel(value = "PmsInvoiceInfo对象", description = "发票信息表")
public class PmsInvoiceInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("解析状态,0-->初始化，1-->处理中，2-->成功，-1-->失败")
    private Integer status;

    @ApiModelProperty("是否已人工校验,0-->否，1-->是")
    private Boolean checked;

    @ApiModelProperty("是否已报销：0->未报销；1->已报销")
    private Boolean reimbursed;

    @ApiModelProperty("发票代码")
    private String invCode;

    @ApiModelProperty("发票号码")
    private String invNum;

    @ApiModelProperty("校验码")
    private String invChk;

    @JsonFormat(pattern = "yyyy年MM月dd日", timezone = "Asia/Shanghai")
    @ApiModelProperty("开票日期")
    private Date invDate;

    @ApiModelProperty("开具金额")
    private BigDecimal invMoney;

    @ApiModelProperty("税额")
    private String invTax;

    @ApiModelProperty("价税合计")
    private String invTotal;

    @ApiModelProperty("详细信息")
    @JsonSerialize(using = StringToJsonNodeSerializer.class)
    @JsonDeserialize(using = JsonNodeToStringDeserializer.class)
    private String invDetail;

    @ApiModelProperty("发票类型:增值税专用发票、增值税电子专用发票、增值税普通发票、增值税电子普通发票、增值税普通发票(卷票)、增值税电子普通发票(通行费)")
    private String invType;

    @ApiModelProperty("解析方式")
    private String method;

    @ApiModelProperty("文件hash")
    private String fileHash;

    @JsonIgnore
    @ApiModelProperty("文件路径")
    private String filePath;

    @ApiModelProperty("文件名")
    private String fileName;

    @ApiModelProperty("文件类型")
    private String fileType;

    @ApiModelProperty("object对象名称")
    private String objectName;

    @ApiModelProperty("bucket名称")
    private String bucketName;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @ApiModelProperty("订单创建时间")
    private Date createTime;

    @JsonIgnore
    @ApiModelProperty("是否删除:0-->否，1-->是")
    private Boolean isDelete;

    @ApiModelProperty("备注信息")
    private String remark;

}
