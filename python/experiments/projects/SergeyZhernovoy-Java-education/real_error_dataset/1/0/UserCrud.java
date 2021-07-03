package ru.szhernovoy.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.szhernovoy.domain.User;

/**
 * Created by admin on 17.02.2017.
 */
public interface UserCrud extends CrudRepository<User,Integer> {

}
