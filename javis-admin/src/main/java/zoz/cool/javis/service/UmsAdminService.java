package zoz.cool.javis.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;
import zoz.cool.javis.domain.UmsMenu;
import zoz.cool.javis.dto.request.UmsAdminParam;
import zoz.cool.javis.dto.request.UpdateAdminPasswordParam;
import zoz.cool.javis.domain.UmsAdmin;
import zoz.cool.javis.domain.UmsResource;
import zoz.cool.javis.domain.UmsRole;

import java.util.List;

/**
 * 后台用户管理Service Created by zhayongchun on 2023/11/17.
 */
public interface UmsAdminService extends IService<UmsAdmin> {
    /**
     * 根据手机号或邮箱获取管理员
     */
    UmsAdmin getAdminByEmailOrPhone(String emailOrPhone);

    /**
     * 根据手机号或邮箱获取管理员
     */
    Boolean getVerifyCode(String emailOrPhone);

    /**
     * 注册功能
     */
    UmsAdmin register(UmsAdminParam umsAdminParam);

    /**
     * 登录功能
     *
     * @param username 用户名
     * @param password 密码
     * @return 生成的JWT的token
     */
    SaTokenInfo login(String username, String password);

    /**
     * 根据用户名或昵称分页查询用户
     */
    List<UmsAdmin> list(String keyword, Integer pageSize, Integer pageNum);

    /**
     * 修改用户角色关系
     */
    @Transactional
    boolean updateRole(Long adminId, List<Long> roleIds);

    /**
     * 获取用户对应角色
     */
    List<UmsRole> getRoleList(Long adminId);

    List<UmsMenu> getMenuList(Long adminId);

    /**
     * 获取指定用户的可访问资源
     */
    List<UmsResource> getResourceList(Long adminId);

    /**
     * 修改密码
     */
    int updatePassword(UpdateAdminPasswordParam updatePasswordParam);

    /**
     * 获取缓存服务
     */
    UmsAdminCacheService getCacheService();

}
