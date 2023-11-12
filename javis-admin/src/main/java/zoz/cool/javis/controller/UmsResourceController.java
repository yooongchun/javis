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
import zoz.cool.javis.domain.UmsResource;
import zoz.cool.javis.service.UmsResourceService;

import java.util.List;


/**
 * 后台资源管理Controller
 */
@Controller
@Api(tags = "UmsResourceController")
@Tag(name = "UmsResourceController", description = "后台资源管理")
@SaCheckLogin
@SaCheckRole("admin")
@RequestMapping("/resource")
public class UmsResourceController {
    private final UmsResourceService resourceService;

    @Autowired
    public UmsResourceController(UmsResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @ApiOperation("添加后台资源")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult<Boolean> create(@RequestBody UmsResource umsResource) {
        if (resourceService.save(umsResource)) {
            return CommonResult.success(true);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改后台资源")
    @PostMapping("/update/{id}")
    @ResponseBody
    public CommonResult<Boolean> update(@PathVariable Long id,
                                        @RequestBody UmsResource umsResource) {
        umsResource.setId(id);
        if (resourceService.updateById(umsResource)) {
            return CommonResult.success(true);
        }
        return CommonResult.failed();
    }

    @ApiOperation("根据ID获取资源详情")
    @GetMapping("/{id}")
    @ResponseBody
    public CommonResult<UmsResource> getItem(@PathVariable Long id) {
        UmsResource umsResource = resourceService.getById(id);
        return CommonResult.success(umsResource);
    }

    @ApiOperation("根据ID删除后台资源")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult<Boolean> delete(@PathVariable Long id) {
        if (resourceService.removeById(id)) {
            return CommonResult.success(true);
        }
        return CommonResult.failed();
    }

    @ApiOperation("分页模糊查询后台资源")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<CommonPage<UmsResource>> list(@RequestParam(required = false) Long categoryId,
                                                      @RequestParam(required = false) String nameKeyword,
                                                      @RequestParam(required = false) String urlKeyword,
                                                      @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        QueryWrapper<UmsResource> query = new QueryWrapper<>();
        if (categoryId != null) {
            query.eq("category_id", categoryId);
        }
        if (nameKeyword != null) {
            query.like("name", nameKeyword);
        }
        if (urlKeyword != null) {
            query.like("url", urlKeyword);
        }
        Page<UmsResource> page = new Page<>(pageNum, pageSize);
        Page<UmsResource> resourceList = resourceService.page(page, query);
        return CommonResult.success(CommonPage.restPage(resourceList));
    }

    @ApiOperation("查询所有后台资源")
    @GetMapping("/listAll")
    @ResponseBody
    public CommonResult<List<UmsResource>> listAll() {
        List<UmsResource> resourceList = resourceService.list();
        return CommonResult.success(resourceList);
    }
}
