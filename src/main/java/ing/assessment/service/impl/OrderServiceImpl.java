package ing.assessment.service.impl;

import ing.assessment.db.order.Order;
import ing.assessment.db.order.OrderProduct;
import ing.assessment.db.product.Product;
import ing.assessment.db.repository.OrderRepository;
import ing.assessment.db.repository.ProductRepository;
import ing.assessment.exception.InsufficientStockException;
import ing.assessment.exception.InvalidOrderException;
import ing.assessment.exception.ItemNotFound;
import ing.assessment.model.Location;
import ing.assessment.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;


    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Integer id) {
        return orderRepository.findById(id).orElseThrow(() -> new ItemNotFound("Order with ID: "+ id +" was not found!"));
    }

    @Override
    public List<Order> getOrdersById(List<Integer> ids) {
        return orderRepository.findAllById(ids);
    }

    @Override
    public Order createOrder(List<OrderProduct> orderProductList) {
        if (orderProductList == null || orderProductList.isEmpty()) {
            throw new InvalidOrderException("Order must contain at least one product!");
        }

        Double productsTotalCost = computeOrderProductsTotalSum(orderProductList);
        Order createdOrder = populateOrder(new Order(), orderProductList, productsTotalCost);
        changeProductsFromOrderStock(orderProductList);
        return orderRepository.save(createdOrder);
    }

    @Override
    public void deleteOrderProduct(Integer orderId, Integer productId, Location location) {
        Order orderToBeModified = orderRepository.findById(orderId).orElseThrow(() -> new ItemNotFound("Order with ID: "+ orderId +" was not found!"));

        boolean removedOrderProduct = orderToBeModified.getOrderProducts().removeIf(orderProduct ->
                orderProduct.getProductId().equals(productId) &&
                        orderProduct.getLocation().equals(location));

        if (!removedOrderProduct) {
            throw new ItemNotFound("Product with ID: " + productId + " at location: " + location + " was not found in order!");
        }

        Double productsTotalCost = computeOrderProductsTotalSum(orderToBeModified.getOrderProducts());
        orderRepository.save(populateOrder(orderToBeModified, orderToBeModified.getOrderProducts(), productsTotalCost));
    }

    private Double computeOrderProductsTotalSum(List<OrderProduct> orderProductList) {
       return orderProductList.stream()
                .map(orderProduct -> {
                    Product product = productRepository.findByProductCk_IdAndProductCk_Location(
                            orderProduct.getProductId(), orderProduct.getLocation());
                    validateOrderProduct(product, orderProduct);
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

    private void validateOrderProduct(Product product, OrderProduct orderProduct) {
        if (product == null) {
            throw new ItemNotFound("There is no product with ID: " + orderProduct.getProductId() + " at location: " + orderProduct.getLocation());
        }
        if (product.getQuantity() < orderProduct.getQuantity()) {
            throw new InsufficientStockException("There is no stock for product with ID: " + orderProduct.getProductId());
        }
    }

    private void changeProductsFromOrderStock(List<OrderProduct> orderProducts) {
        orderProducts.forEach(orderProduct -> {
                    Product product = productRepository.findByProductCk_IdAndProductCk_Location(
                            orderProduct.getProductId(), orderProduct.getLocation());
                    Integer quantityAfterOrder = product.getQuantity() - orderProduct.getQuantity();
                    product.setQuantity(quantityAfterOrder);
                    productRepository.save(product);
                });
    }
}