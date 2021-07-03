package ru.szhernovoy.repositories;


import org.springframework.data.repository.CrudRepository;
import ru.szhernovoy.domain.Car;

/**
 * Created by Admin on 05.02.2017.
 */
public interface CarCrud extends CrudRepository<Car,Integer> {
}
