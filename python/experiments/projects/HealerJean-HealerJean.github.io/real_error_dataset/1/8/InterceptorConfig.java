package com.hlj.config;

import com.hlj.config.interceotor.SessionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
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
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionInterceptor())
                .excludePathPatterns("/demo/**")
                .excludePathPatterns("/main")
                .addPathPatterns("/**");
        super.addInterceptors(registry);
    }



}
