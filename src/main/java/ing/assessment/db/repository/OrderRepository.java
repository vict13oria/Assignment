package ing.assessment.db.repository;

import ing.assessment.db.order.Order;
import ing.assessment.db.order.OrderProduct;
import ing.assessment.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

}