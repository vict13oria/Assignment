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
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private static final String ORDER_NOT_FOUND = "Order with ID: '%s' was not found!";
    private static final String PRODUCT_NOT_FOUND = "Product with ID: '%s' at location: '%s' was not found!";
    private static final String STOCK_EXCEPTION = "There is not enough stock for product with ID: %s at location: '%s'. Maximum quantity available: '%s'";

    private static final Integer DEFAULT_DELIVERY_COST = 30;
    private static final Integer DEFAULT_DELIVERY_TIME = 2;

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
    public Order getOrderById(Integer orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new ItemNotFound(String.format(ORDER_NOT_FOUND, orderId)));
    }

    @Override
    public List<Order> getOrdersByIds(List<Integer> ids) {
        return orderRepository.findAllById(ids);
    }

    @Transactional
    @Override
    public Order createOrder(List<OrderProduct> orderProductList) {
        if (orderProductList == null || orderProductList.isEmpty()) {
            throw new InvalidOrderException("Order must contain at least one product!");
        }

        Double productsTotalCost = computeOrderTotalCostForNewOrder(orderProductList);
        Order createdOrder = populateOrder(new Order(), orderProductList, productsTotalCost);
        decreaseProductStockAfterPlacingOrder(orderProductList);
        return orderRepository.save(createdOrder);
    }

    @Transactional
    @Override
    public Order editOrderProductQuantity(Integer orderId, Integer productId, Location location, Integer newQuantity) {
        Order orderToEdit = orderRepository.findById(orderId).orElseThrow(() -> new ItemNotFound(String.format(ORDER_NOT_FOUND, orderId)));

        orderToEdit.getOrderProducts().stream()
                .filter(op -> op.getProductId().equals(productId) && op.getLocation().equals(location))
                .findFirst()
                .map(filteredProduct -> {
                    changeOrderProductStock(filteredProduct, newQuantity);
                    return filteredProduct;
                })
                .orElseThrow(() -> new ItemNotFound(String.format(PRODUCT_NOT_FOUND, productId, location)));

        Double productsTotalCost = computeOrderTotalCostForNewOrder(orderToEdit.getOrderProducts());
        populateOrder(orderToEdit, orderToEdit.getOrderProducts(), productsTotalCost);

        return orderRepository.saveAndFlush(orderToEdit);
    }

    @Transactional
    @Override
    public void deleteOrderProduct(Integer orderId, Integer productId, Location location) {
        Order orderToBeModified = orderRepository.findById(orderId)
                .orElseThrow(() -> new ItemNotFound(String.format(ORDER_NOT_FOUND, orderId)));

        OrderProduct removedOrderProduct = orderToBeModified.getOrderProducts().stream()
                .filter(orderProduct -> orderProduct.getProductId().equals(productId) &&
                        orderProduct.getLocation().equals(location))
                .findFirst()
                .orElseThrow(() -> new ItemNotFound(String.format(PRODUCT_NOT_FOUND, productId, location)));

        orderToBeModified.getOrderProducts().remove(removedOrderProduct);
        Double productsTotalCost = computeOrderTotalCostForExistingOrder(orderToBeModified.getOrderProducts());

        Product product =  productRepository.findByProductCk_IdAndProductCk_Location(productId, location);
        increaseProductStockAfterDeletingItFromOrder(product, removedOrderProduct.getQuantity());

        Order order = populateOrder(orderToBeModified, orderToBeModified.getOrderProducts(), productsTotalCost);

        orderRepository.saveAndFlush(order);
    }

    @Transactional
    @Override
    public void deleteOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ItemNotFound(String.format(ORDER_NOT_FOUND, orderId)));
        increaseProductsStockAfterDeletingOrder(order.getOrderProducts());
        orderRepository.delete(order);
    }

    private Double computeOrderTotalCostForNewOrder(List<OrderProduct> orderProductList) {
       return orderProductList.stream()
                .map(orderProduct -> {
                    Product product = productRepository.findByProductCk_IdAndProductCk_Location(
                            orderProduct.getProductId(), orderProduct.getLocation());
                    checkIfProductWantedForOrderExists(product, orderProduct);
                    checkIfQuantityWantedForOrderIsAvailable(product, orderProduct);
                    return product.getPrice() * orderProduct.getQuantity();
                })
                .reduce(0.0, Double::sum);
    }

    private Double computeOrderTotalCostForExistingOrder(List<OrderProduct> orderProductList) {
       return orderProductList.stream()
                .map(orderProduct -> {
                    Product product = productRepository.findByProductCk_IdAndProductCk_Location(
                            orderProduct.getProductId(), orderProduct.getLocation());
                    return product.getPrice() * orderProduct.getQuantity();
                })
                .reduce(0.0, Double::sum);
    }

    private Order populateOrder(Order order, List<OrderProduct> orderProductList, Double productsTotalCost) {

        Integer deliveryTime = DEFAULT_DELIVERY_TIME * computeExtraDays(orderProductList);
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
        return productsTotalCost > 500 ? 0 : DEFAULT_DELIVERY_COST;
    }

    private Double computeOrderCost(Double productsTotalCost) {
        return productsTotalCost > 1000 ? productsTotalCost * 0.9 : productsTotalCost;
    }

    private void checkIfProductWantedForOrderExists(Product product, OrderProduct orderProduct) {
        if (product == null) {
            throw new ItemNotFound(String.format(PRODUCT_NOT_FOUND, orderProduct.getProductId(), orderProduct.getLocation()));
        }
    }

    private void checkIfQuantityWantedForOrderIsAvailable(Product product, OrderProduct orderProduct) {
        if (product.getQuantity() < orderProduct.getQuantity()) {
            throw new InsufficientStockException(String.format(STOCK_EXCEPTION, orderProduct.getProductId(), orderProduct.getLocation(), product.getQuantity()));
        }
    }

    private void changeOrderProductStock(OrderProduct orderProduct, Integer newQuantity) {
        Product product = productRepository.findByProductCk_IdAndProductCk_Location(orderProduct.getProductId(), orderProduct.getLocation());
        Integer availableStock = product.getQuantity();
        Integer currentOrderQuantity = orderProduct.getQuantity();

        if (availableStock < newQuantity)
            throw new InsufficientStockException(String.format(STOCK_EXCEPTION, orderProduct.getProductId(), orderProduct.getLocation(), availableStock));

        modifyProductStockAfterOrderProductQuantityChanged(orderProduct, currentOrderQuantity - newQuantity);
        orderProduct.setQuantity(newQuantity);
    }

    private void decreaseProductStockAfterPlacingOrder(List<OrderProduct> orderProducts) {
        orderProducts.forEach(orderProduct -> {
                    Product product = productRepository.findByProductCk_IdAndProductCk_Location(
                            orderProduct.getProductId(), orderProduct.getLocation());
                    Integer quantityAfterPlacingOrder = product.getQuantity() - orderProduct.getQuantity();
                    if (quantityAfterPlacingOrder.equals(0)) {
                        productRepository.delete(product);
                    } else {
                        product.setQuantity(quantityAfterPlacingOrder);
                        productRepository.save(product);
                    }
                });
    }


    private void increaseProductsStockAfterDeletingOrder(List<OrderProduct> orderProducts) {
        orderProducts.forEach(orderProduct -> {
            Product product = productRepository.findByProductCk_IdAndProductCk_Location(
                    orderProduct.getProductId(), orderProduct.getLocation());
            Integer quantityAfterPlacingOrder = product.getQuantity() + orderProduct.getQuantity();
            product.setQuantity(quantityAfterPlacingOrder);
            productRepository.save(product);
        });
    }

    private void increaseProductStockAfterDeletingItFromOrder(Product product, Integer orderProductQuantity) {
            Integer quantityAfterPlacingOrder = product.getQuantity() + orderProductQuantity;
            product.setQuantity(quantityAfterPlacingOrder);
            productRepository.save(product);
    }

    private void modifyProductStockAfterOrderProductQuantityChanged(OrderProduct orderProduct, Integer quantity) {
            Product product = productRepository.findByProductCk_IdAndProductCk_Location(orderProduct.getProductId(), orderProduct.getLocation());
            product.setQuantity(product.getQuantity() + quantity);
            productRepository.save(product);
    }
}