package ing.assessment.service;

import ing.assessment.db.order.Order;
import ing.assessment.db.order.OrderProduct;
import ing.assessment.model.Location;

import java.util.List;

public interface OrderService {
    Order createOrder(List<OrderProduct> orderProductList);
    Order deleteOrderProduct(Integer idOrder, Integer idProduct, Location location);
}