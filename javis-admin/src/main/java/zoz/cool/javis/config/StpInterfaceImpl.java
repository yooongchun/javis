package zoz.cool.javis.config;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zoz.cool.javis.domain.UmsAdminRoleRelation;
import zoz.cool.javis.domain.UmsRole;
import zoz.cool.javis.service.UmsAdminService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义权限验证接口扩展 Created by zhayongchun on 2023/11/20.
 */
@Component // 保证此类被spring boot扫描，完成sa-token的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {
    private final UmsAdminService adminRoleRelation;

    @Autowired
    public StpInterfaceImpl(UmsAdminService adminRoleRelation) {
        this.adminRoleRelation = adminRoleRelation;
    }

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginKey) {
        return new ArrayList<>();
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginKey) {
        List<UmsRole> roles = adminRoleRelation.getRoleList(Long.valueOf(loginId.toString()));

        return roles.stream().map(UmsRole::getRoleName).collect(Collectors.toList());
    }
}