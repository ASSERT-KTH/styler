package ru.szhernovoy.jpa.carstore.persistance;

import org.springframework.data.repository.CrudRepository;
import ru.szhernovoy.jpa.carstore.domain.DriveType;

import java.util.List;

/**
 * Created by Admin on 05.02.2017.
 */
public interface DriveTypeCrud extends CrudRepository<DriveType,Integer> {
    List<DriveType> findByAll();
    DriveType findById(int id);
}
