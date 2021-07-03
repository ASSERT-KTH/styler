package com.hlj.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author HealerJean
 * @version 1.0v
 * @ClassName PropertySourceT
 * @date 2019/5/31  17:23.
 * @Description
 */
@Data
@PropertySource("classpath:profile.properties")
@Component
public class PropertySourceT {

    @Value("${profile.age}")
    private String age ;

}
