package ru.szhernovoy.repositories;


import org.springframework.data.repository.CrudRepository;
import ru.szhernovoy.domain.Model;

/**
 * Created by Admin on 05.02.2017.
 */

public interface ModelCrud extends CrudRepository<Model,Integer> {
}
