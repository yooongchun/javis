package zoz.cool.javis.domain;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.TableName;

/**
* 角色资源关系表
*/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ums_role_resource_relation")
@ApiModel(value="UmsRoleResourceRelation对象", description="角色资源关系表")
public class UmsRoleResourceRelation implements Serializable {

private static final long serialVersionUID=1L;

    @ApiModelProperty("主键")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("角色ID")
    private Long roleId;

    @ApiModelProperty("资源ID")
    private Long resourceId;

}