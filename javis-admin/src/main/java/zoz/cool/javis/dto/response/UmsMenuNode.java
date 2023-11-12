package zoz.cool.javis.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import zoz.cool.javis.domain.UmsMenu;

import java.util.List;

/**
 * 后台菜单节点封装
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UmsMenuNode extends UmsMenu {
    @ApiModelProperty(value = "子级菜单")
    private List<UmsMenuNode> children;
}
