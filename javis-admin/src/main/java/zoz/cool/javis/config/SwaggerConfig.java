package zoz.cool.javis.config;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import zoz.cool.javis.common.config.BaseSwaggerConfig;
import zoz.cool.javis.common.domain.SwaggerProperties;

/**
 * Swagger相关配置 Created by zhayongchun on 2023/11/17.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig extends BaseSwaggerConfig {

    @Override
    public SwaggerProperties swaggerProperties() {
        return SwaggerProperties.builder().apiBasePackage("zoz.cool.javis.controller").title("Javis Admin Console")
                .description("Javis admin控制台").contactName("javis").version("1.0").enableSecurity(true).build();
    }

    @Bean
    public BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
        return generateBeanPostProcessor();
    }

}
