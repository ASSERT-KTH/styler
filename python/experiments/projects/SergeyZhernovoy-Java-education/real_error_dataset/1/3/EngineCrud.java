package ru.szhernovoy.repositories;


import org.springframework.data.repository.CrudRepository;
import ru.szhernovoy.domain.Engine;

/**
 * Created by Admin on 05.02.2017.
 */

public interface EngineCrud extends CrudRepository<Engine,Integer> {
}
