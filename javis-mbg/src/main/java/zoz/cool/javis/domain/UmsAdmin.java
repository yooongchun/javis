package zoz.cool.javis.domain;

import java.io.Serializable;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 用户表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ums_admin")
@ApiModel(value = "UmsAdmin对象", description = "用户表")
public class UmsAdmin implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增主键")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户名")
    private String username;

    @JsonIgnore
    @ApiModelProperty("hash密码")
    private String password;

    @ApiModelProperty("昵称")
    private String nickName;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("头像")
    private String icon;

    @ApiModelProperty("备注信息")
    private String note;

    @ApiModelProperty("账号启用状态：0->禁用；1->启用")
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @ApiModelProperty("创建时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @ApiModelProperty("最后登录时间")
    private Date loginTime;

}