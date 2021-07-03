package ru.szhernovoy.jpa.carstore.persistance;

import org.springframework.data.repository.CrudRepository;
import ru.szhernovoy.jpa.carstore.domain.Order;

import java.util.List;

/**
 * Created by Admin on 05.02.2017.
 */
public interface OrderCrud extends CrudRepository<Order,Integer> {
    List<Order> findByAll();
    Order findById(int id);
}
