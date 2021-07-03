package com.hlj;

import com.hlj.quartz.core.event.QuartzListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 类描述：
 * 创建人： HealerJean
 */
@Configuration
public class ListenerConfig {

    /**
     * 配置定时器初始化
     * @return
     */
    @Bean
    public QuartzListener quartzListener(){
        return new QuartzListener();
    }

}
