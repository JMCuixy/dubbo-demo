package org.dubbo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

/**
 * Api 接口访问地址：http://localhost:8081/swagger-ui.html
 */
@Configuration
@EnableSwagger2
@ComponentScan(basePackages = "org.dubbo.controller")
public class SwaggerConfig {

    @Bean
    public Docket swaggerSpringMvcPlugin() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                // 选择哪些路径和api会生成document
                .select()
                // 对所有Api进行监控
                .apis(RequestHandlerSelectors.any())
                // 对所有路径进行扫描
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * api具体信息
     *
     * @return
     */
    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo(
                "接口文档",
                "",
                "v2.0.0",
                "",
                new Contact("JMCui", "https://i.cnblogs.com/", "1099442418@qq.com"),
                "",
                "",
                new ArrayList<>());
        return apiInfo;
    }
}
