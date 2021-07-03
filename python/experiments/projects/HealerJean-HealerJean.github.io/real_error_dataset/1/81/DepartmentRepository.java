package com.hlj.dao.db;

import com.hlj.entity.db.demo.Department;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 作者 ：HealerJean
 * 日期 ：2019/3/4  下午2:11.
 * 类描述：
 */
public interface DepartmentRepository  extends JpaRepository<Department,Integer> {

}
