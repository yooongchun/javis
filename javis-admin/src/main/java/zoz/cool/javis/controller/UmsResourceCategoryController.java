package zoz.cool.javis.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import zoz.cool.javis.common.api.CommonResult;
import zoz.cool.javis.domain.UmsResourceCategory;
import zoz.cool.javis.service.UmsResourceCategoryService;

import java.util.List;

/**
 * 后台资源分类管理Controller
 */
@Controller
@Api(tags = "UmsResourceCategoryController")
@Tag(name = "UmsResourceCategoryController", description = "后台资源分类管理")
@RequestMapping("/resourceCategory")
@SaCheckLogin
@SaCheckRole("admin")
public class UmsResourceCategoryController {
    private final UmsResourceCategoryService resourceCategoryService;

    @Autowired
    public UmsResourceCategoryController(UmsResourceCategoryService resourceCategoryService) {
        this.resourceCategoryService = resourceCategoryService;
    }

    @ApiOperation("查询所有后台资源分类")
    @GetMapping("/listAll")
    @ResponseBody
    public CommonResult<List<UmsResourceCategory>> listAll() {
        List<UmsResourceCategory> resourceList = resourceCategoryService.list();
        return CommonResult.success(resourceList);
    }

    @ApiOperation("添加后台资源分类")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult<Boolean> create(@RequestBody UmsResourceCategory umsResourceCategory) {
        if (resourceCategoryService.save(umsResourceCategory)) {
            return CommonResult.success(true);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改后台资源分类")
    @PostMapping("/update/{id}")
    @ResponseBody
    public CommonResult<Boolean> update(@PathVariable Long id,
                                        @RequestBody UmsResourceCategory umsResourceCategory) {
        umsResourceCategory.setId(id);
        if (resourceCategoryService.updateById(umsResourceCategory)) {
            return CommonResult.success(true);
        }
        return CommonResult.failed();
    }

    @ApiOperation("根据ID删除后台资源分类")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult<Boolean> delete(@PathVariable Long id) {
        if (resourceCategoryService.removeById(id)) {
            return CommonResult.success(true);
        }
        return CommonResult.failed();
    }
}
