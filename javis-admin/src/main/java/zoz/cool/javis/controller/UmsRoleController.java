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
import zoz.cool.javis.domain.*;
import zoz.cool.javis.service.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 后台用户角色管理Controller
 */
@Controller
@Api(tags = "UmsRoleController")
@Tag(name = "UmsRoleController", description = "后台用户角色管理")
@RequestMapping("/role")
@SaCheckLogin
@SaCheckRole("admin")
public class UmsRoleController {
    private final UmsRoleService roleService;
    private final UmsMenuService menuService;
    private final UmsRoleMenuRelationService umsRoleMenuRelationService;
    private final UmsResourceService umsResourceService;
    private final UmsRoleResourceRelationService umsRoleResourceRelationService;
    private final UmsAdminCacheService adminCacheService;

    @Autowired
    public UmsRoleController(UmsRoleService roleService, UmsMenuService menuService,
                             UmsRoleMenuRelationService umsRoleMenuRelationService,
                             UmsResourceService umsResourceService, UmsRoleResourceRelationService umsRoleResourceRelationService, UmsAdminCacheService adminCacheService) {
        this.roleService = roleService;
        this.menuService = menuService;
        this.umsRoleMenuRelationService = umsRoleMenuRelationService;
        this.umsResourceService = umsResourceService;
        this.umsRoleResourceRelationService = umsRoleResourceRelationService;
        this.adminCacheService = adminCacheService;
    }

    @ApiOperation("添加角色")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult<Boolean> create(@RequestBody UmsRole role) {
        if (roleService.save(role)) {
            return CommonResult.success(true);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改角色")
    @PostMapping("/update/{id}")
    @ResponseBody
    public CommonResult<Boolean> update(@PathVariable Long id, @RequestBody UmsRole role) {
        role.setId(id);
        if (roleService.updateById(role)) {
            return CommonResult.success(true);
        }
        return CommonResult.failed();
    }

    @ApiOperation("批量删除角色")
    @PostMapping("/delete")
    @ResponseBody
    public CommonResult<Boolean> delete(@RequestParam("ids") List<Long> ids) {
        if (roleService.removeBatchByIds(ids)) {
            return CommonResult.success(true);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取所有角色")
    @GetMapping("/listAll")
    @ResponseBody
    public CommonResult<List<UmsRole>> listAll() {
        List<UmsRole> roleList = roleService.list();
        return CommonResult.success(roleList);
    }

    @ApiOperation("根据角色名称分页获取角色列表")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<CommonPage<UmsRole>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                                  @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        Page<UmsRole> page = new Page<>(pageNum, pageSize, roleService.count());
        QueryWrapper<UmsRole> query = new QueryWrapper<>();
        if (keyword != null) {
            query.eq("role_name", keyword);
        }
        Page<UmsRole> roleList = roleService.page(page, query);
        return CommonResult.success(CommonPage.restPage(roleList));
    }

    @ApiOperation("修改角色状态")
    @PostMapping("/updateStatus/{id}")
    @ResponseBody
    public CommonResult<Boolean> updateStatus(@PathVariable Long id, @RequestParam(value = "status") Integer status) {
        UmsRole umsRole = roleService.getById(id);
        umsRole.setStatus(status);
        if (roleService.updateById(umsRole)) {
            return CommonResult.success(true);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取角色相关菜单")
    @GetMapping("/listMenu/{roleId}")
    @ResponseBody
    public CommonResult<List<UmsMenu>> listMenu(@PathVariable Long roleId) {
        List<UmsRoleMenuRelation> roleMenuList = umsRoleMenuRelationService.list(new QueryWrapper<UmsRoleMenuRelation>().eq("role_id", roleId));
        List<UmsMenu> roleList = menuService.list(new QueryWrapper<UmsMenu>().in("id", roleMenuList.stream().map(UmsRoleMenuRelation::getMenuId).toArray()));
        return CommonResult.success(roleList);
    }

    @ApiOperation("获取角色相关资源")
    @GetMapping("/listResource/{roleId}")
    @ResponseBody
    public CommonResult<List<UmsResource>> listResource(@PathVariable Long roleId) {
        List<UmsRoleResourceRelation> roleResourceRelationList = umsRoleResourceRelationService.list(new QueryWrapper<UmsRoleResourceRelation>().eq("role_id", roleId));
        List<UmsResource> resourceList = umsResourceService.list(new QueryWrapper<UmsResource>().in("id", roleResourceRelationList.stream().map(UmsRoleResourceRelation::getResourceId).toArray()));
        return CommonResult.success(resourceList);
    }

    @ApiOperation("给角色分配菜单")
    @PostMapping("/allocMenu")
    @ResponseBody
    public CommonResult<Boolean> allocMenu(@RequestParam Long roleId, @RequestParam List<Long> menuIds) {
        // 先删除原有关系
        umsRoleMenuRelationService.removeByMap(Collections.singletonMap("role_id", roleId));
        // 批量插入新关系
        List<UmsRoleMenuRelation> relationList = new ArrayList<>();
        for (Long menuId : menuIds) {
            UmsRoleMenuRelation relation = new UmsRoleMenuRelation();
            relation.setRoleId(roleId);
            relation.setMenuId(menuId);
            relationList.add(relation);
        }
        umsRoleMenuRelationService.saveBatch(relationList);
        return CommonResult.success(true);
    }

    @ApiOperation("给角色分配资源")
    @PostMapping("/allocResource")
    @ResponseBody
    public CommonResult<Boolean> allocResource(@RequestParam Long roleId, @RequestParam List<Long> resourceIds) {
        // 先删除原有关系
        umsRoleResourceRelationService.removeByMap(Collections.singletonMap("role_id", roleId));
        // 批量插入新关系
        List<UmsRoleResourceRelation> relationList = new ArrayList<>();
        for (Long resourceId : resourceIds) {
            UmsRoleResourceRelation relation = new UmsRoleResourceRelation();
            relation.setRoleId(roleId);
            relation.setResourceId(resourceId);
            relationList.add(relation);
        }
        umsRoleResourceRelationService.saveBatch(relationList);
        adminCacheService.delResourceList(roleId);
        return CommonResult.success(true);
    }
}

