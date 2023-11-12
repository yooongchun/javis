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
* 用户登录日志
*/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ums_admin_login_log")
@ApiModel(value="UmsAdminLoginLog对象", description="用户登录日志")
public class UmsAdminLoginLog implements Serializable {

private static final long serialVersionUID=1L;

    @ApiModelProperty("主键")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户ID")
    private Long adminId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("登陆IP")
    private String ip;

    @ApiModelProperty("登陆地址")
    private String address;

    @ApiModelProperty("浏览器信息")
    private String userAgent;

}