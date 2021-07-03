package com.hlj.swagger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

//import springfox.documentation.service.Contact;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Profile({"dev","test"})
    @Bean
    public Docket createRestApi() { //一般只在开发或者测试的时候用到
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com"))//扫描com路径下的api文档
                .paths(PathSelectors.any())//路径判断
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Swagger 开发规范")//标题
                .description("Saggger 开发规范详文档细地址(HealerJean博客)--->>>>>>>：http://blog.healerjean.top/")//描述
                .termsOfServiceUrl("http://blog.healerjean.top/")//（不可见）条款地址
                .version("1.0.0")//版本号
                .build();
    }

}
