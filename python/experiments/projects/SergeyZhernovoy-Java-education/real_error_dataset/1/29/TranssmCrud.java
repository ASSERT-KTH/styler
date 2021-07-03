package ru.szhernovoy.jpa.carstore.repositories;


import org.springframework.data.repository.CrudRepository;
import ru.szhernovoy.jpa.carstore.domain.Transmission;

/**
 * Created by Admin on 05.02.2017.
 */

public interface TranssmCrud extends CrudRepository<Transmission,Integer> {
}
