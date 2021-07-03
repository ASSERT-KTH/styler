package com.hlj.dao.mybatis.demo;

import com.hlj.entity.db.demo.DemoEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 作者 ：HealerJean
 * 日期 ：2018/11/14  下午12:54.
 * 类描述：
 */
public interface DemoEntityMapper {

    List<DemoEntity> findAll();

    DemoEntity findById(@Param("id") Long id);

    /**
     * 1、获取 存储过程out参数值
     * @param map
     * @return
     */
    void procedureGetOut(Map map);


    /**
     * 2、获取 存储过程的结果集合-只有一个
     * @param map
     * @return
     */
    List<DemoEntity> procedureGetOneList(Map map) ;



    /**
     * 3、获取 存储过程的 获取多个集合 使用了泛型，有可能集合中是不同的
     * @param
     * @return
     */
    List<List<?>> procedureGetManyList(Map map);


}
