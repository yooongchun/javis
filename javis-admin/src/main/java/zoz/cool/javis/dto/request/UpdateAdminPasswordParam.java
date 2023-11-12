package zoz.cool.javis.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;


/**
 * 修改用户名密码参数 Created by zhayongchun on 2023/11/17.
 */
@Data
public class UpdateAdminPasswordParam {
    @ApiModelProperty(value = "用ID", required = true)
    @Range(min = 1, message = "用户ID必须大于0")
    private Long id;

    @Length(min = 6, max = 20, message = "密码长度必须在6-20之间")
    @ApiModelProperty(value = "旧密码", required = true)
    private String oldPassword;

    @Length(min = 6, max = 20, message = "密码长度必须在6-20之间")
    @ApiModelProperty(value = "新密码", required = true)
    private String newPassword;
}
