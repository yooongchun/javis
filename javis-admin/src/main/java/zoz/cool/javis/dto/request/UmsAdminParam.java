package zoz.cool.javis.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * 用户注册参数 Created by zhayongchun on 2023/11/17.
 */
@Data
public class UmsAdminParam {
    @NotEmpty
    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    @Pattern(regexp = "^\\w{6,20}$", message = "密码必须为6~20位！")
    @ApiModelProperty(value = "密码", required = true)
    private String password;

    @Pattern(regexp = "^\\w{6,20}$", message = "密码必须为6~20位！")
    @ApiModelProperty(value = "密码复核", required = true)
    private String passwordAgain;

    @Pattern(regexp = "^\\d{6}$", message = "验证码长度为6位！")
    @ApiModelProperty(value = "验证码", required = true)
    private String verifyCode;

    @ApiModelProperty(value = "用户头像")
    private String icon;

    @Email
    @ApiModelProperty(value = "邮箱")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @ApiModelProperty(value = "备注")
    private String note;
}
