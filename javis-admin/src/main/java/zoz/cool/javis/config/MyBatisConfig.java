package zoz.cool.javis.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis相关配置 Created by zhayongchun on 2023/11/17.
 */
@Configuration
@EnableTransactionManagement
@MapperScan({ "zoz.cool.javis.mapper", "zoz.cool.javis.dao" })
public class MyBatisConfig {
}
