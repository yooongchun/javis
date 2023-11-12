package zoz.cool.javis.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import zoz.cool.javis.common.service.RedisService;
import zoz.cool.javis.domain.UmsRoleResourceRelation;
import zoz.cool.javis.mapper.UmsAdminRoleRelationMapper;
import zoz.cool.javis.domain.UmsAdmin;
import zoz.cool.javis.domain.UmsAdminRoleRelation;
import zoz.cool.javis.domain.UmsResource;
import zoz.cool.javis.mapper.UmsRoleResourceRelationMapper;
import zoz.cool.javis.service.UmsAdminCacheService;
import zoz.cool.javis.service.UmsAdminService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 后台用户缓存操作Service实现类 Created by zhayongchun on 2023/11/17.
 */
@Service
public class UmsAdminCacheServiceImpl implements UmsAdminCacheService {
    private final UmsAdminService adminService;
    private final RedisService redisService;
    private final UmsAdminRoleRelationMapper adminRoleRelationMapper;
    private final UmsRoleResourceRelationMapper roleResourceRelationMapper;
    @Value("${redis.database}")
    private String REDIS_DATABASE;
    @Value("${redis.expire.common}")
    private Long REDIS_EXPIRE;
    @Value("${redis.key.admin}")
    private String REDIS_KEY_ADMIN;
    @Value("${redis.key.resourceList}")
    private String REDIS_KEY_RESOURCE_LIST;

    @Autowired
    public UmsAdminCacheServiceImpl(UmsAdminService adminService, RedisService redisService,
                                    UmsAdminRoleRelationMapper adminRoleRelationMapper,
                                    UmsRoleResourceRelationMapper roleResourceRelationMapper) {
        this.adminService = adminService;
        this.redisService = redisService;
        this.adminRoleRelationMapper = adminRoleRelationMapper;
        this.roleResourceRelationMapper = roleResourceRelationMapper;
    }

    @Override
    public void delAdmin(Long adminId) {
        UmsAdmin admin = adminService.getById(adminId);
        if (admin != null) {
            String key = REDIS_DATABASE + ":" + REDIS_KEY_ADMIN + ":" + admin.getUsername();
            redisService.del(key);
        }
    }

    @Override
    public void delResourceList(Long adminId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_RESOURCE_LIST + ":" + adminId;
        redisService.del(key);
    }

    @Override
    public void delResourceListByRole(Long roleId) {
        List<UmsAdminRoleRelation> relationList = adminRoleRelationMapper.selectByMap(Collections.singletonMap("role_id", roleId));
        if (CollUtil.isNotEmpty(relationList)) {
            String keyPrefix = REDIS_DATABASE + ":" + REDIS_KEY_RESOURCE_LIST + ":";
            List<String> keys = relationList.stream().map(relation -> keyPrefix + relation.getAdminId()).collect(Collectors.toList());
            redisService.del(keys);
        }
    }

    @Override
    public void delResourceListByRoleIds(List<Long> roleIds) {

        List<UmsAdminRoleRelation> relationList = adminRoleRelationMapper.selectList(new QueryWrapper<UmsAdminRoleRelation>().in("role_id", roleIds));
        if (CollUtil.isNotEmpty(relationList)) {
            String keyPrefix = REDIS_DATABASE + ":" + REDIS_KEY_RESOURCE_LIST + ":";
            List<String> keys = relationList.stream().map(relation -> keyPrefix + relation.getAdminId()).collect(Collectors.toList());
            redisService.del(keys);
        }
    }

    @Override
    public void delResourceListByResource(Long resourceId) {
        List<UmsRoleResourceRelation> relationList = roleResourceRelationMapper.selectByMap(Collections.singletonMap("resource_id", resourceId));
        List<Long> roleList = relationList.stream().map(UmsRoleResourceRelation::getRoleId).distinct().collect(Collectors.toList());
        List<UmsAdminRoleRelation> adminList = adminRoleRelationMapper.selectList(new QueryWrapper<UmsAdminRoleRelation>().in("role_id", roleList));
        List<Long> adminIdList = adminList.stream().map(UmsAdminRoleRelation::getAdminId).distinct().collect(Collectors.toList());
        if (CollUtil.isNotEmpty(adminIdList)) {
            String keyPrefix = REDIS_DATABASE + ":" + REDIS_KEY_RESOURCE_LIST + ":";
            List<String> keys = adminIdList.stream().map(adminId -> keyPrefix + adminId).collect(Collectors.toList());
            redisService.del(keys);
        }
    }

    @Override
    public UmsAdmin getAdmin(String emailOrPhone) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_ADMIN + ":" + emailOrPhone;
        return (UmsAdmin) redisService.get(key);
    }

    @Override
    public void setAdmin(UmsAdmin admin) {
        String emailOrPhone = StrUtil.isEmpty(admin.getEmail()) ? admin.getPhone() : admin.getEmail();
        String key = REDIS_DATABASE + ":" + REDIS_KEY_ADMIN + ":" + emailOrPhone;
        redisService.set(key, admin, REDIS_EXPIRE);
    }

    @Override
    public List<UmsResource> getResourceList(Long adminId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_RESOURCE_LIST + ":" + adminId;
        return (List<UmsResource>) redisService.get(key);
    }

    @Override
    public void setResourceList(Long adminId, List<UmsResource> resourceList) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_RESOURCE_LIST + ":" + adminId;
        redisService.set(key, resourceList, REDIS_EXPIRE);
    }
}
