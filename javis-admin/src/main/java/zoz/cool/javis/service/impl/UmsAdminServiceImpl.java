package zoz.cool.javis.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import zoz.cool.javis.common.constants.RedisConstant;
import zoz.cool.javis.common.exception.Asserts;
import zoz.cool.javis.common.service.EmailService;
import zoz.cool.javis.common.service.RedisService;
import zoz.cool.javis.common.service.SmsService;
import zoz.cool.javis.common.util.RequestUtil;
import zoz.cool.javis.common.util.ToolKit;
import zoz.cool.javis.dto.request.UmsAdminParam;
import zoz.cool.javis.dto.request.UpdateAdminPasswordParam;
import zoz.cool.javis.mapper.*;
import zoz.cool.javis.domain.*;
import zoz.cool.javis.service.OmsAccountBalanceService;
import zoz.cool.javis.service.UmsAdminCacheService;
import zoz.cool.javis.service.UmsAdminService;
import org.springframework.beans.BeanUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 后台用户管理Service实现类 Created by zhayongchun on 2023/11/17.
 */
@Slf4j
@Service
public class UmsAdminServiceImpl extends ServiceImpl<UmsAdminMapper, UmsAdmin> implements UmsAdminService {
    private final UmsAdminMapper adminMapper;
    private final UmsAdminRoleRelationMapper adminRoleRelationMapper;
    private final UmsRoleResourceRelationMapper roleResourceRelationMapper;
    private final UmsAdminLoginLogMapper loginLogMapper;
    private final UmsRoleMapper roleMapper;
    private final UmsResourceMapper resourceMapper;
    private final RedisService redisService;
    private final EmailService mailService;
    private final SmsService smsService;
    private final UmsMenuMapper menuMapper;
    private final UmsRoleMenuRelationMapper roleMenuRelationMapper;
    private final OmsAccountBalanceService accountBalanceService;

    @Autowired
    public UmsAdminServiceImpl(UmsAdminMapper adminMapper, UmsAdminRoleRelationMapper adminRoleRelationMapper,
                               UmsRoleResourceRelationMapper roleResourceRelationMapper, UmsAdminLoginLogMapper loginLogMapper,
                               UmsRoleMapper roleMapper, UmsResourceMapper resourceMapper, RedisService redisService,
                               EmailService mailService, SmsService smsService, UmsMenuMapper menuMapper, UmsRoleMenuRelationMapper roleMenuRelationMapper, OmsAccountBalanceService accountBalanceService) {
        this.adminMapper = adminMapper;
        this.adminRoleRelationMapper = adminRoleRelationMapper;
        this.roleResourceRelationMapper = roleResourceRelationMapper;
        this.loginLogMapper = loginLogMapper;
        this.roleMapper = roleMapper;
        this.resourceMapper = resourceMapper;
        this.redisService = redisService;
        this.mailService = mailService;
        this.smsService = smsService;
        this.menuMapper = menuMapper;
        this.roleMenuRelationMapper = roleMenuRelationMapper;
        this.accountBalanceService = accountBalanceService;
    }

    @Override
    public Boolean getVerifyCode(String emailOrPhone) {
        Asserts.failIfNull(emailOrPhone, "邮箱或手机号必须指定一个");
        String key = RedisConstant.REDIS_REGISTER_KEY_PREFIX + emailOrPhone;
        String verifyCode = (String) redisService.get(key);
        if (StrUtil.isEmpty(verifyCode)) {
            verifyCode = ToolKit.randomCode(6);
            redisService.set(key, verifyCode, RedisConstant.REDIS_REGISTER_VERIFYCODE_EXPIRE);
        }
        if (ToolKit.isValidEmail(emailOrPhone)) {
            // 发送邮件
            mailService.sendMailVerifyCode(emailOrPhone, verifyCode);
        } else if (ToolKit.isValidPhone(emailOrPhone)) {
            // 发送短信
            smsService.sendSmsVerifyCode(emailOrPhone, verifyCode);
        } else {
            Asserts.fail("邮箱或手机号格式不正确");
        }
        return true;
    }

    @Override
    public UmsAdmin getAdminByEmailOrPhone(String emailOrPhone) {
        // 先从缓存中获取数据
        UmsAdmin admin = getCacheService().getAdmin(emailOrPhone);
        if (admin != null) return admin;
        // 缓存中没有从数据库中获取
        String field = emailOrPhone.contains("@") ? "email" : "phone";
        List<UmsAdmin> adminList = adminMapper.selectByMap(Collections.singletonMap(field, emailOrPhone));
        if (!CollUtil.isEmpty(adminList)) {
            admin = adminList.get(0);
            // 将数据库中的数据存入缓存中
            getCacheService().setAdmin(admin);
            return admin;
        }
        return null;
    }

    @Override
    public UmsAdmin register(UmsAdminParam umsAdminParam) {
        UmsAdmin umsAdmin = new UmsAdmin();
        BeanUtils.copyProperties(umsAdminParam, umsAdmin);
        umsAdmin.setCreateTime(new Date());
        umsAdmin.setStatus(1);
        // 邮箱号或手机号是否已存在
        String email = umsAdminParam.getEmail();
        if (StrUtil.isNotEmpty(email) && getAdminByEmailOrPhone(email) != null) {
            // 邮箱已存在
            Asserts.fail("该邮箱已注册");
        }
        String phone = umsAdminParam.getPhone();
        if (StrUtil.isNotEmpty(phone) && getAdminByEmailOrPhone(phone) != null) {
            // 手机号已存在
            Asserts.fail("该手机号已注册");
        }
        if (StrUtil.isEmpty(email) && StrUtil.isEmpty(phone)) {
            Asserts.fail("邮箱和手机号不能都为空");
        }
        if (!umsAdminParam.getPassword().equals(umsAdminParam.getPasswordAgain())) {
            Asserts.fail("两次密码不一致");
        }
        String emailOrPhone = StrUtil.isNotEmpty(email) ? email : phone;
        String REGISTER_VERIFY_CODE = RedisConstant.REDIS_REGISTER_KEY_PREFIX + emailOrPhone;
        String storeCode = (String) redisService.get(REGISTER_VERIFY_CODE);
        if (StrUtil.isEmpty(storeCode) || !storeCode.equals(umsAdminParam.getVerifyCode())) {
            Asserts.fail("验证码不正确或已过期");
        }
        // 将密码进行加密操作
        String pw_hash = BCrypt.hashpw(umsAdmin.getPassword(), BCrypt.gensalt());
        umsAdmin.setPassword(pw_hash);
        adminMapper.insert(umsAdmin);

        // 注册成功之后创建账户
        OmsAccountBalance accountBalance = new OmsAccountBalance();
        accountBalance.setUserId(umsAdmin.getId());
        accountBalance.setBalance(BigDecimal.ONE);
        accountBalanceService.save(accountBalance);
        return umsAdmin;
    }

    @Override
    public SaTokenInfo login(String emailOrPhone, String password) {
        // 获取用户信息
        String keyword = ToolKit.isValidEmail(emailOrPhone) ? "email" : "phone";
        UmsAdmin admin = getOne(new QueryWrapper<UmsAdmin>().eq(keyword, emailOrPhone));
        Asserts.failIfNull(admin, "账号或密码错误");
        // 密码需要客户端加密后传递
        if (!BCrypt.checkpw(password, admin.getPassword())) {
            Asserts.fail("密码不正确");
        }
        if (admin.getStatus() == 0) {
            Asserts.fail("帐号已被禁用");
        }
        // 密码校验成功后登录，一行代码实现登录
        StpUtil.login(admin.getId());
        // 获取当前登录用户Token信息
        SaTokenInfo saTokenInfo = StpUtil.getTokenInfo();
        insertLoginLog(admin.getId());
        return saTokenInfo;
    }

    /**
     * 添加登录记录
     *
     * @param id 用户ID
     */
    private void insertLoginLog(Long id) {
        UmsAdmin admin = adminMapper.selectById(id);
        if (admin == null) {
            log.warn("添加登录登录记录失败，用户不存在：{}", id);
            return;
        }
        UmsAdminLoginLog loginLog = new UmsAdminLoginLog();
        loginLog.setAdminId(admin.getId());
        loginLog.setCreateTime(new Date());
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Asserts.failIfNull(attributes, "无法获取请求信息！");
        HttpServletRequest request = attributes.getRequest();
        loginLog.setIp(RequestUtil.getRequestIp(request));
        loginLogMapper.insert(loginLog);
    }

    @Override
    public List<UmsAdmin> list(String keyword, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<UmsAdmin> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(keyword)) {
            queryWrapper.like("username", keyword).or().like("nick_name", keyword);
        }
        return adminMapper.selectList(queryWrapper);
    }

    @Override
    public boolean updateRole(Long adminId, List<Long> roleIds) {
        // 先删除原来的关系
        List<UmsAdminRoleRelation> roleList = adminRoleRelationMapper.selectList(new QueryWrapper<UmsAdminRoleRelation>().eq("admin_id", adminId));
        adminRoleRelationMapper.deleteBatchIds(roleList.stream().map(UmsAdminRoleRelation::getId).collect(Collectors.toList()));
        // 建立新关系
        if (CollectionUtils.isNotEmpty(roleIds)) {
            List<UmsAdminRoleRelation> relations = new ArrayList<>();
            for (Long roleId : roleIds) {
                UmsAdminRoleRelation roleRelation = new UmsAdminRoleRelation();
                roleRelation.setAdminId(adminId);
                roleRelation.setRoleId(roleId);
                relations.add(roleRelation);
            }
            adminRoleRelationMapper.insertBatch(relations);
        }
        getCacheService().delResourceList(adminId);
        return true;
    }

    @Override
    public List<UmsRole> getRoleList(Long adminId) {
        List<UmsAdminRoleRelation> relations = adminRoleRelationMapper.selectByMap(Collections.singletonMap("admin_id", adminId));
        List<Long> roleIds = relations.stream().map(UmsAdminRoleRelation::getRoleId).distinct().collect(Collectors.toList());
        if (CollUtil.isEmpty(roleIds)) {
            return new ArrayList<>();
        }
        return roleMapper.selectBatchIds(roleIds);
    }

    @Override
    public List<UmsMenu> getMenuList(Long adminId) {
        List<UmsAdminRoleRelation> relations = adminRoleRelationMapper.selectByMap(Collections.singletonMap("admin_id", adminId));
        List<Long> roleList = relations.stream().map(UmsAdminRoleRelation::getRoleId).distinct().collect(Collectors.toList());
        List<UmsRoleMenuRelation> menuRelations = roleMenuRelationMapper.selectList(new QueryWrapper<UmsRoleMenuRelation>().in("role_id", roleList));
        List<Long> menuIds = menuRelations.stream().map(UmsRoleMenuRelation::getMenuId).distinct().collect(Collectors.toList());
        return menuMapper.selectBatchIds(menuIds);
    }

    @Override
    public List<UmsResource> getResourceList(Long adminId) {
        // 先从缓存中获取数据
        List<UmsResource> resourceList = getCacheService().getResourceList(adminId);
        if (CollUtil.isNotEmpty(resourceList)) {
            return resourceList;
        }
        // 缓存中没有从数据库中获取
        List<UmsAdminRoleRelation> roles = adminRoleRelationMapper.selectByMap(Collections.singletonMap("admin_id", adminId));
        List<Long> roleIds = roles.stream().map(UmsAdminRoleRelation::getRoleId).distinct().collect(Collectors.toList());
        List<UmsRoleResourceRelation> resources = roleResourceRelationMapper.selectList(new QueryWrapper<UmsRoleResourceRelation>().in("role_id", roleIds));
        List<Long> resourceIds = resources.stream().map(UmsRoleResourceRelation::getResourceId).distinct().collect(Collectors.toList());
        resourceList = resourceMapper.selectBatchIds(resourceIds);
        if (CollUtil.isNotEmpty(resourceList)) {
            // 将数据库中的数据存入缓存中
            getCacheService().setResourceList(adminId, resourceList);
        }
        return resourceList;
    }

    @Override
    public int updatePassword(UpdateAdminPasswordParam param) {
        Long userId = param.getId();
        String oldPasswd = param.getOldPassword();
        String newPasswd = param.getNewPassword();
        if (userId == null || StrUtil.isEmpty(oldPasswd) || StrUtil.isEmpty(newPasswd)) {
            return -1;
        }
        UmsAdmin umsAdmin = adminMapper.selectById(userId);
        Asserts.failIfNull(umsAdmin, "用户不存在");
        if (!BCrypt.checkpw(oldPasswd, umsAdmin.getPassword())) {
            return -3;
        }
        if (BCrypt.checkpw(newPasswd, umsAdmin.getPassword())) {
            return -4;
        }
        umsAdmin.setPassword(BCrypt.hashpw(newPasswd, BCrypt.gensalt()));
        adminMapper.updateById(umsAdmin);
        getCacheService().delAdmin(umsAdmin.getId());
        return 1;
    }

    @Override
    public UmsAdminCacheService getCacheService() {
        return SpringUtil.getBean(UmsAdminCacheService.class);
    }
}
