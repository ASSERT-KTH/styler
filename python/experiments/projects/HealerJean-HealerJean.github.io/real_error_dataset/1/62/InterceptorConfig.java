package com.hlj.springboot.dome.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


/**
 * 拦截器
 * 作者：  HealerJean
 * 日期:  2018/11/8 下午3:57.
 * 类描述: 
 */

@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {


    /**
     *  swagger增加url映射
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/demo/swagger/**")
                .addResourceLocations("classpath:/swagger/dist/");

        registry.addResourceHandler("/demo/swagger/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/demo/swagger/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("/demo/swagger/public/**")
                .addResourceLocations("classpath:/public/");

        registry.addResourceHandler("/**").addResourceLocations("");

    }

}
