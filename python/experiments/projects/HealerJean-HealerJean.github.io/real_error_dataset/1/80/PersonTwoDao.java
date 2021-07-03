package com.hlj.many.datasourse.dataresource.dao.data;

import com.hlj.many.datasourse.dataresource.dao.data.entry.PersonTwo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface PersonTwoDao extends CrudRepository<PersonTwo,Long> {

    List<PersonTwo> findAll();
}
