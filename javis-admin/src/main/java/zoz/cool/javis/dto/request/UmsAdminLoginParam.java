package zoz.cool.javis.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * 用户登录参数 Created by zhayongchun on 2023/11/17.
 */
@Data
public class UmsAdminLoginParam {
    @Email
    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Pattern(regexp = "^\\w{6,20}$", message = "密码必须为6~20位！")
    @ApiModelProperty(value = "密码", required = true)
    private String password;
}
