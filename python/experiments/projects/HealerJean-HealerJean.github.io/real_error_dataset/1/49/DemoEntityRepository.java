package com.hlj.dao.db;

import com.hlj.entity.db.demo.DemoEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface DemoEntityRepository extends CrudRepository<DemoEntity,Long> {

    List<DemoEntity> findAll();

    Optional<DemoEntity> findById(Long id);

}
