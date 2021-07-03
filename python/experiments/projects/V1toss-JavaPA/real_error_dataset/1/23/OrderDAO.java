package vkaretko.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import vkaretko.domain.Order;

/**
 * Order DAO class.
 *
 * @author Karetko Victor.
 * @version 1.00.
 * @since 08.05.2017.
 */
@Repository
public interface OrderDAO extends CrudRepository<Order,Integer> {


}
