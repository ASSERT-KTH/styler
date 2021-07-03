package com.hlj.proj.config.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;

import java.io.Serializable;
import java.util.UUID;

/**
 * @ClassName UuidSessionIdGenerator
 * @Author TD
 * @Date 2019/1/29 10:16
 * @Description session Key 生成
 */
public class UuidSessionIdGenerator implements SessionIdGenerator {
    @Override
    public Serializable generateId(Session session) {
            return UUID.randomUUID().toString().replaceAll("-","");
    }
}
