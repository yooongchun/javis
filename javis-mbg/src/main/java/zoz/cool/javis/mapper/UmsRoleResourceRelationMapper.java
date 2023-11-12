package zoz.cool.javis.mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Collection;

import zoz.cool.javis.domain.UmsRoleResourceRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zhayongchun
* @description 针对表【ums_role_resource_relation(角色资源关系表)】的数据库操作Mapper
* @createDate 2023-11-26 10:28:30
* @Entity zoz.cool.javis.domain.UmsRoleResourceRelation
*/
public interface UmsRoleResourceRelationMapper extends BaseMapper<UmsRoleResourceRelation> {
    int insertBatch(@Param("umsRoleResourceRelationCollection") Collection<UmsRoleResourceRelation> umsRoleResourceRelationCollection);
}




