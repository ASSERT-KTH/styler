package ru.szhernovoy.repositories;


import org.springframework.data.repository.CrudRepository;
import ru.szhernovoy.domain.Transmission;

/**
 * Created by Admin on 05.02.2017.
 */

public interface TranssmCrud extends CrudRepository<Transmission,Integer> {
}
