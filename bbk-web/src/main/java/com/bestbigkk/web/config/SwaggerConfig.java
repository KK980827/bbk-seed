package com.bestbigkk.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
*  @author: xugongkai
*  @data: 2019-12-11 17:09:23
*  @describe: 文档配置
**/
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.bestbigkk.web"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("BestBigKK-种子快速脚手架")
                .description("落地，生根，发芽，壮大...")
                .termsOfServiceUrl("http://localhost:21584/")
                .contact(new Contact("xugongkai", "bestbigkk.com", "KK980827@163.com"))
                .version("1.0-SNAPSHOT")
                .build();
    }
}
