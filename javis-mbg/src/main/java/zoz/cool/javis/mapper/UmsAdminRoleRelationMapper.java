package zoz.cool.javis.mapper;

import org.apache.ibatis.annotations.Param;
import zoz.cool.javis.domain.UmsAdminRoleRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author zhayongchun
* @description 针对表【ums_admin_role_relation(用户角色关系表)】的数据库操作Mapper
* @createDate 2023-11-26 10:28:30
* @Entity zoz.cool.javis.domain.UmsAdminRoleRelation
*/
public interface UmsAdminRoleRelationMapper extends BaseMapper<UmsAdminRoleRelation> {
    int insertBatch(@Param("roles") List<UmsAdminRoleRelation> roles);
}