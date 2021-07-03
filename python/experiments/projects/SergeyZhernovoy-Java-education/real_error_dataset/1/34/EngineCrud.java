package ru.szhernovoy.jpa.carstore.persistance;

import org.springframework.data.repository.CrudRepository;
import ru.szhernovoy.jpa.carstore.domain.Engine;

import java.util.List;

/**
 * Created by Admin on 05.02.2017.
 */
public interface EngineCrud extends CrudRepository<Engine,Integer> {
    List<Engine> findByAll();
    Engine findById(int id);
}
