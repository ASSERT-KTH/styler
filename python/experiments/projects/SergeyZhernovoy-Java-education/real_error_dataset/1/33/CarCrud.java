package ru.szhernovoy.jpa.carstore.persistance;

import org.springframework.data.repository.CrudRepository;
import ru.szhernovoy.jpa.carstore.domain.Car;

import java.util.List;

/**
 * Created by Admin on 05.02.2017.
 */
public interface CarCrud extends CrudRepository<Car,Integer> {
    List<Car> findByAll();
    Car findById(int id);
}
