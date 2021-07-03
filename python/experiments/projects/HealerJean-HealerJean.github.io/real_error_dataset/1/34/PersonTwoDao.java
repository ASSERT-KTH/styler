package com.hlj.many.datasourse.dataresource.dao.db;

import com.hlj.many.datasourse.dataresource.dao.db.entry.PersonTwo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface PersonTwoDao extends CrudRepository<PersonTwo,Long> {

    List<PersonTwo> findAll();
}
