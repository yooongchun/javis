package zoz.cool.javis.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import zoz.cool.javis.common.config.BaseRedisConfig;

/**
 * Redis相关配置 Created by zhayongchun on 2023/11/17.
 */
@EnableCaching
@Configuration
public class RedisConfig extends BaseRedisConfig {

}
