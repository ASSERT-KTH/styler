package ru.szhernovoy.jpa.carstore.persistance;

import org.springframework.data.repository.CrudRepository;
import ru.szhernovoy.jpa.carstore.domain.Transmission;

import java.util.List;

/**
 * Created by Admin on 05.02.2017.
 */

public interface TranssmCrud extends CrudRepository<Transmission,Integer> {
    List<Transmission> findByAll();
    Transmission findById(int id);
}
