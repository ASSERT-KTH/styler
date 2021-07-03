package com.duodian.youhui.admin.config.anno.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 校验用户token
 * 可以用于class 或者 method
 * 如果在method和class都有，则以method的为准。
 * @author HealerJean
 */
@Target(value = {ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Token {

    /**
     * 是否校验token
     * @return
     */
    boolean check() default true;
}
