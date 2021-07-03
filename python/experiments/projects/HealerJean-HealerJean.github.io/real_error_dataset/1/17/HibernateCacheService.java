package com.hlj.moudle.cache.service;

import com.hlj.entity.db.demo.DemoEntity;

import java.util.List;

/**
 * @Desc:
 * @Author HealerJean
 * @Date 2018/9/17  下午2:39.
 */
public interface HibernateCacheService {

    DemoEntity addDemoEntity(DemoEntity demoEntity);

    List<DemoEntity> findAll();

    DemoEntity findById(Long id);

    void oneCache(Long id);

     void updateOneCache(Long id) ;

    void twoCache(Long id);
}
