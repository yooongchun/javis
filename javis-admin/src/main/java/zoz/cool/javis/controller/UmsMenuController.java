package zoz.cool.javis.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import zoz.cool.javis.common.api.CommonPage;
import zoz.cool.javis.common.api.CommonResult;
import zoz.cool.javis.common.exception.Asserts;
import zoz.cool.javis.domain.UmsMenu;
import zoz.cool.javis.dto.response.UmsMenuNode;
import zoz.cool.javis.service.UmsMenuService;

import java.util.List;

/**
 * 后台菜单管理Controller
 */
@Controller
@Api(tags = "UmsMenuController")
@Tag(name = "UmsMenuController", description = "后台菜单管理")
@SaCheckLogin
@SaCheckRole("admin")
@RequestMapping("/menu")
public class UmsMenuController {
    private final UmsMenuService menuService;

    @Autowired
    public UmsMenuController(UmsMenuService menuService) {
        this.menuService = menuService;
    }

    @ApiOperation("添加后台菜单")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult<Boolean> create(@RequestBody UmsMenu umsMenu) {
        boolean ok = menuService.save(umsMenu);
        if (ok) {
            return CommonResult.success(true);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改后台菜单")
    @PostMapping("/update/{id}")
    @ResponseBody
    public CommonResult<Boolean> update(@PathVariable Long id,
                                        @RequestBody UmsMenu umsMenu) {
        umsMenu.setId(id);
        if (menuService.updateById(umsMenu)) {
            return CommonResult.success(true);
        }
        return CommonResult.failed();
    }

    @ApiOperation("根据ID获取菜单详情")
    @GetMapping("/{id}")
    @ResponseBody
    public CommonResult<UmsMenu> getItem(@PathVariable Long id) {
        UmsMenu umsMenu = menuService.getById(id);
        return CommonResult.success(umsMenu);
    }

    @ApiOperation("根据ID删除后台菜单")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult<Boolean> delete(@PathVariable Long id) {
        if (menuService.removeById(id)) {
            return CommonResult.success(true);
        }
        return CommonResult.failed();
    }

    @ApiOperation("分页查询后台菜单")
    @GetMapping("/list/{parentId}")
    @ResponseBody
    public CommonResult<CommonPage<UmsMenu>> list(@PathVariable Long parentId,
                                                  @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        Page<UmsMenu> page = new Page<>(pageNum, pageSize);
        Page<UmsMenu> menuList = menuService.page(page, new QueryWrapper<UmsMenu>().eq("parent_id", parentId));
        return CommonResult.success(CommonPage.restPage(menuList));
    }

    @ApiOperation("树形结构返回所有菜单列表")
    @GetMapping("/treeList")
    @ResponseBody
    public CommonResult<List<UmsMenuNode>> treeList() {
        List<UmsMenuNode> list = menuService.treeList();
        return CommonResult.success(list);
    }

    @ApiOperation("修改菜单显示状态")
    @RequestMapping(value = "/updateHidden/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Boolean> updateHidden(@PathVariable Long id, @RequestParam("hidden") Integer hidden) {
        UmsMenu menu = menuService.getById(id);
        Asserts.failIfNull(menu, "菜单ID不存在");
        menu.setHidden(hidden);
        if (menuService.updateById(menu)) {
            return CommonResult.success(true);
        }
        return CommonResult.failed();
    }
}
