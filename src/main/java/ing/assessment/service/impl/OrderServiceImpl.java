package ing.assessment.service.impl;

import ing.assessment.db.order.Order;
import ing.assessment.db.order.OrderProduct;
import ing.assessment.db.product.Product;
import ing.assessment.db.repository.OrderRepository;
import ing.assessment.db.repository.ProductRepository;
import ing.assessment.exception.InvalidOrderException;
import ing.assessment.exception.ItemNotFound;
import ing.assessment.model.Location;
import ing.assessment.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public Order createOrder(List<OrderProduct> orderProductList) {
        if (orderProductList == null || orderProductList.isEmpty()) {
            throw new InvalidOrderException("Order must contain at least one product!");
        }

        Double productsTotalCost = computeOrderProductsTotalSum(orderProductList);
        return populateOrder(new Order(), orderProductList, productsTotalCost);
    }

    public Order deleteOrderProduct(Integer orderId, Integer productId, Location location) {
        Order orderToBeModified = orderRepository.findById(orderId).orElseThrow(() -> new ItemNotFound("Order with ID: "+ orderId +" was not found!"));

        boolean removedOrderProduct = orderToBeModified.getOrderProducts().removeIf(orderProduct ->
                orderProduct.getProductId().equals(productId) &&
                        orderProduct.getLocation().equals(location));

        if (!removedOrderProduct) {
            throw new InvalidOrderException("Product with ID: " + productId + " at location: " + location + " not found in order!");
        }

        Double productsTotalCost = computeOrderProductsTotalSum(orderToBeModified.getOrderProducts());
        return orderRepository.save(populateOrder(orderToBeModified, orderToBeModified.getOrderProducts(), productsTotalCost));
    }

    private Double computeOrderProductsTotalSum(List<OrderProduct> orderProductList) {
       return orderProductList.stream()
                .map(orderProduct -> {
                    Product product = productRepository.findByProductIdAndOrderId(
                            orderProduct.getProductId(), orderProduct.getLocation()
                    );
                    return product.getPrice() * orderProduct.getQuantity();
                })
                .reduce(0.0, Double::sum);
    }

    private Order populateOrder(Order order, List<OrderProduct> orderProductList, Double productsTotalCost) {

        Integer deliveryTime = order.getDeliveryTime() * computeExtraDays(orderProductList);
        order.setTimestamp(new Date());
        order.setOrderProducts(orderProductList);
        order.setOrderCost(computeOrderCost(productsTotalCost));
        order.setDeliveryCost(computeDeliveryCost(productsTotalCost, order));
        order.setDeliveryTime(deliveryTime);
        return order;
    }

    private Integer computeExtraDays(List<OrderProduct> orderProductList) {
        return Math.toIntExact(orderProductList.stream().map(OrderProduct::getLocation).distinct()
                .count());
    }

    private Integer computeDeliveryCost(Double productsTotalCost, Order order) {
        return productsTotalCost > 500 ? 0 : order.getDeliveryCost();
    }

    private Double computeOrderCost(Double productsTotalCost) {
        return productsTotalCost > 1000 ? productsTotalCost * 0.9 : productsTotalCost;
    }
}