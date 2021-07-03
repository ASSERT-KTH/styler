package com.hlj.many.datasourse.dataresource.dao.db;

import com.hlj.many.datasourse.dataresource.dao.db.entry.PersonOne;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface PersonOneDao extends CrudRepository<PersonOne,Long> {

    List<PersonOne> findAll();
}
