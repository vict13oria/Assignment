package ing.assessment.service;

import ing.assessment.db.order.Order;
import ing.assessment.db.order.OrderProduct;
import ing.assessment.model.Location;

import java.util.List;

public interface OrderService {
    List<Order> getAllOrders();
    Order getOrderById(Integer orderId);
    List<Order> getOrdersByIds(List<Integer> orderIds);
    Order createOrder(List<OrderProduct> orderProducts);
    Order editOrderProductQuantity(Integer orderId, Integer productId, Location location, Integer quantity);
    void deleteOrderProduct(Integer orderId, Integer productId, Location location);
    void deleteOrder(Integer orderId);
}