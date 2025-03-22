package ing.assessment.service;

import ing.assessment.db.order.Order;
import ing.assessment.db.order.OrderProduct;
import ing.assessment.model.Location;

import java.util.List;

public interface OrderService {
    List<Order> getAllOrders();
    Order getOrderById(Integer id);
    List<Order> getOrdersById(List<Integer> ids);
    Order createOrder(List<OrderProduct> orderProductList);
    void deleteOrderProduct(Integer idOrder, Integer idProduct, Location location);
}