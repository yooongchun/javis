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
* 角色表
*/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ums_role")
@ApiModel(value="UmsRole对象", description="角色表")
public class UmsRole implements Serializable {

private static final long serialVersionUID=1L;

    @ApiModelProperty("主键")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("角色名称")
    private String roleName;

    @ApiModelProperty("描述")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("启用状态：0->禁用；1->启用")
    private Integer status;

}