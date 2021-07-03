package ru.szhernovoy.security.carstore.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.szhernovoy.security.carstore.domain.User;

/**
 * Created by admin on 17.02.2017.
 */
public interface UserCrud extends CrudRepository<User,Integer> {

}
