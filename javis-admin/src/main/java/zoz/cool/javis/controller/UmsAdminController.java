package zoz.cool.javis.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.taptap.ratelimiter.annotation.RateLimit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.BeanUtils;
import zoz.cool.javis.common.api.CommonPage;
import zoz.cool.javis.common.api.CommonResult;
import zoz.cool.javis.common.exception.Asserts;
import zoz.cool.javis.domain.OmsAccountBalance;
import zoz.cool.javis.domain.UmsMenu;
import zoz.cool.javis.dto.request.UmsAdminLoginParam;
import zoz.cool.javis.dto.request.UmsAdminParam;
import zoz.cool.javis.dto.request.UpdateAdminPasswordParam;
import zoz.cool.javis.domain.UmsAdmin;
import zoz.cool.javis.domain.UmsRole;
import zoz.cool.javis.dto.response.LoginResp;
import zoz.cool.javis.dto.response.UserInfo;
import zoz.cool.javis.service.OmsAccountBalanceService;
import zoz.cool.javis.service.UmsAdminService;

import java.util.List;

/**
 * 后台用户管理Controller Created by zhayongchun on 2023/11/17.
 */
@RestController
@Api(tags = "UmsAdminController")
@Tag(name = "UmsAdminController", description = "后台用户管理")
@RequestMapping("/admin")
@Slf4j
public class UmsAdminController {

    private final UmsAdminService adminService;
    private final OmsAccountBalanceService accountBalanceService;

    @Autowired
    public UmsAdminController(UmsAdminService adminService, OmsAccountBalanceService accountBalanceService) {
        this.adminService = adminService;
        this.accountBalanceService = accountBalanceService;
    }

    @ApiOperation(value = "用户注册")
    @PostMapping("/register")
    @ResponseBody
    public CommonResult<UmsAdmin> register(@Validated @RequestBody UmsAdminParam umsAdminParam) {
        UmsAdmin umsAdmin = adminService.register(umsAdminParam);
        return CommonResult.success(umsAdmin);
    }

    @ApiOperation(value = "获取验证码")
    @GetMapping("/verifyCode")
    @ResponseBody
    @RateLimit(rate = 10, rateInterval = "60s")
    public CommonResult<Boolean> getVerifyCode(@RequestParam(value = "email", required = false) String email, @RequestParam(value = "phone", required = false) String phone) {
        boolean ok = adminService.getVerifyCode(StrUtil.isEmpty(email) ? phone : email);
        if (!ok) {
            return CommonResult.failed("获取验证码失败");
        }
        return CommonResult.success(true);
    }

    @ApiOperation(value = "登录以后返回token")
    @PostMapping("/login")
    @ResponseBody
    public CommonResult<LoginResp> login(@Validated @RequestBody UmsAdminLoginParam umsAdminLoginParam) {
        String emailOrPhone = StrUtil.isEmpty(umsAdminLoginParam.getEmail()) ? umsAdminLoginParam.getPhone() : umsAdminLoginParam.getEmail();
        SaTokenInfo tokenInfo = adminService.login(emailOrPhone, umsAdminLoginParam.getPassword());
        LoginResp resp = new LoginResp();
        resp.setToken(tokenInfo.getTokenValue());
        resp.setTokenHead(tokenInfo.getTokenName());
        return CommonResult.success(resp);
    }

    @ApiOperation(value = "获取当前登录用户信息")
    @GetMapping("/info")
    @ResponseBody
    @SaCheckLogin
    public CommonResult<UserInfo> getAdminInfo() {
        UmsAdmin umsAdmin = adminService.getById(StpUtil.getLoginIdAsLong());
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(umsAdmin, userInfo);
        List<UmsRole> roleList = adminService.getRoleList(umsAdmin.getId());
        userInfo.setRoles(roleList);
        List<UmsMenu> menuList = adminService.getMenuList(umsAdmin.getId());
        userInfo.setMenus(menuList);
        // 用户余额
        OmsAccountBalance account = accountBalanceService.getOne(new QueryWrapper<OmsAccountBalance>().eq("user_id", umsAdmin.getId()));
        Asserts.failIfNull(account, "用户账户不存在");
        userInfo.setBalance(account.getBalance());
        return CommonResult.success(userInfo);
    }

    @ApiOperation(value = "登出功能")
    @PostMapping("/logout")
    @ResponseBody
    @SaCheckLogin
    public CommonResult<String> logout() {
        StpUtil.logout();
        return CommonResult.success(null);
    }

    @ApiOperation("根据用户名或姓名分页获取用户列表")
    @GetMapping("/list")
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole("admin")
    public CommonResult<CommonPage<UmsAdmin>> list(@RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<UmsAdmin> adminList = adminService.list(keyword, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(adminList));
    }

    @ApiOperation("获取指定用户信息")
    @GetMapping("/{id}")
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole("admin")
    public CommonResult<UmsAdmin> getItem(@PathVariable Long id) {
        UmsAdmin admin = adminService.getById(id);
        return CommonResult.success(admin);
    }

    @ApiOperation("修改指定用户信息")
    @PostMapping("/update/{id}")
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole("admin")
    public CommonResult<Boolean> update(@PathVariable Long id, @RequestBody UmsAdmin admin) {
        admin.setId(id);
        boolean ok = adminService.updateById(admin);
        if (ok) {
            return CommonResult.success(true);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改指定用户密码")
    @PostMapping("/updatePassword")
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole("admin")
    public CommonResult<Boolean> updatePassword(@Validated @RequestBody UpdateAdminPasswordParam updatePasswordParam) {
        int status = adminService.updatePassword(updatePasswordParam);
        if (status > 0) {
            return CommonResult.success(true);
        } else if (status == -1) {
            return CommonResult.failed("提交参数不合法");
        } else if (status == -2) {
            return CommonResult.failed("找不到该用户");
        } else if (status == -3) {
            return CommonResult.failed("旧密码错误");
        } else if (status == -4) {
            return CommonResult.failed("新密码与旧密码相同");
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("删除指定用户信息")
    @PostMapping("/delete/{id}")
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole("admin")
    public CommonResult<Boolean> delete(@PathVariable Long id) {
        if (adminService.removeById(id)) {
            return CommonResult.success(true);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改帐号状态")
    @PostMapping("/updateStatus/{id}")
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole("admin")
    public CommonResult<Boolean> updateStatus(@PathVariable Long id, @RequestParam(value = "status") Integer status) {
        UmsAdmin admin = adminService.getById(id);
        Asserts.failIfNull(admin, "用户不存在");
        admin.setStatus(status);
        if (adminService.updateById(admin)) {
            return CommonResult.success(true);
        }
        return CommonResult.failed();
    }

    @ApiOperation("给用户分配角色")
    @PostMapping("/role/update")
    @ResponseBody
    @SaCheckRole("admin")
    public CommonResult<Boolean> updateRole(@RequestParam("adminId") Long adminId, @RequestParam("roleIds") List<Long> roleIds) {
        if (adminService.updateRole(adminId, roleIds)) {
            return CommonResult.success(true);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取指定用户的角色")
    @GetMapping("/role/{adminId}")
    @ResponseBody
    @SaCheckRole("admin")
    public CommonResult<List<UmsRole>> getRoleList(@PathVariable Long adminId) {
        List<UmsRole> roleList = adminService.getRoleList(adminId);
        return CommonResult.success(roleList);
    }
}
