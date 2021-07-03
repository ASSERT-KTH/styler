package ru.szhernovoy.security.carstore.repositories;


import org.springframework.data.repository.CrudRepository;
import ru.szhernovoy.security.carstore.domain.DriveType;

/**
 * Created by Admin on 05.02.2017.
 */

public interface DriveTypeCrud extends CrudRepository<DriveType,Integer> {
}
