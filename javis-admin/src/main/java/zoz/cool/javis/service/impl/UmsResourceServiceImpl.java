package zoz.cool.javis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import zoz.cool.javis.mapper.UmsResourceMapper;
import zoz.cool.javis.domain.UmsResource;
import zoz.cool.javis.service.UmsResourceService;

/**
 * 后台资源管理Service实现类 Created by zhayongchun on 2023/11/17.
 */
@Service
public class UmsResourceServiceImpl extends ServiceImpl<UmsResourceMapper, UmsResource> implements UmsResourceService {
}
