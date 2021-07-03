package ru.szhernovoy.security.carstore.repositories;


import org.springframework.data.repository.CrudRepository;
import ru.szhernovoy.security.carstore.domain.Transmission;

/**
 * Created by Admin on 05.02.2017.
 */

public interface TranssmCrud extends CrudRepository<Transmission,Integer> {
}
