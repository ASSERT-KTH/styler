package ru.szhernovoy.jpa.carstore.repositories;


import org.springframework.data.repository.CrudRepository;
import ru.szhernovoy.jpa.carstore.domain.Order;

/**
 * Created by Admin on 05.02.2017.
 */

public interface OrderCrud extends CrudRepository<Order,Integer> {
}
