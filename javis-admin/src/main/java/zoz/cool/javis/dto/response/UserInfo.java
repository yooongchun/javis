package zoz.cool.javis.dto.response;

import lombok.Data;
import zoz.cool.javis.domain.UmsMenu;
import zoz.cool.javis.domain.UmsRole;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class UserInfo {
    private Long id;
    private String username;
    private String icon;
    private String phone;
    private String email;
    private String nickName;
    private BigDecimal balance;
    private String note;
    private Date createTime;
    private Date loginTime;
    private Integer status;
    private List<UmsRole> roles;
    private List<UmsMenu> menus;
}
