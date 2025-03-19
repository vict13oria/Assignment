package ing.assessment.service.impl;

import ing.assessment.db.order.Order;
import ing.assessment.db.order.OrderProduct;
import ing.assessment.db.product.Product;
import ing.assessment.db.repository.OrderRepository;
import ing.assessment.db.repository.ProductRepository;
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


    public void createOrder(List<OrderProduct> orderProductList) {
        Order order = new Order();

        Double productsTotalCost = computeOrderProductsTotalSum(orderProductList);
        Integer deliveryTime = order.getDeliveryTime() * computeExtraDays(orderProductList);

        order.setOrderCost(productsTotalCost > 1000 ? productsTotalCost * 0.9 : productsTotalCost);
        order.setDeliveryTime(deliveryTime);
        order.setDeliveryCost(productsTotalCost > 500 ? 0 : order.getDeliveryCost());
        order.setOrderProducts(orderProductList);
        order.setTimestamp(new Date());
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

    private Integer computeExtraDays(List<OrderProduct> orderProductList) {
        return Math.toIntExact(orderProductList.stream().map(OrderProduct::getLocation).distinct()
                .count());
    }
}