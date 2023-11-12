package zoz.cool.javis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import zoz.cool.javis.domain.UmsMenu;
import zoz.cool.javis.dto.response.UmsMenuNode;

import java.util.List;

/**
 * 后台菜单管理Service
 */
public interface UmsMenuService extends IService<UmsMenu> {
    /**
     * 树形结构返回所有菜单列表
     */
    List<UmsMenuNode> treeList();
}
