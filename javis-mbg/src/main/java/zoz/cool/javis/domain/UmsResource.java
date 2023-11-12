package zoz.cool.javis.domain;

import java.io.Serializable;

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
* 资源表
*/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ums_resource")
@ApiModel(value="UmsResource对象", description="资源表")
public class UmsResource implements Serializable {

private static final long serialVersionUID=1L;

    @ApiModelProperty("主键")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("资源分类ID")
    private Long categoryId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("资源名称")
    private String name;

    @ApiModelProperty("资源URL")
    private String url;

    @ApiModelProperty("描述")
    private String description;

}