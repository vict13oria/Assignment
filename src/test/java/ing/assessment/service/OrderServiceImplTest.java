package ing.assessment.service;

import ing.assessment.db.order.Order;
import ing.assessment.db.order.OrderProduct;
import ing.assessment.db.product.Product;
import ing.assessment.db.product.ProductCK;
import ing.assessment.db.repository.OrderRepository;
import ing.assessment.db.repository.ProductRepository;
import ing.assessment.exception.InsufficientStockException;
import ing.assessment.exception.ItemNotFound;
import ing.assessment.model.Location;
import ing.assessment.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Spy
    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private OrderProduct orderProduct;
    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setProductCk(new ProductCK(1, Location.MUNICH));
        product.setQuantity(10);
        product.setPrice(100.0);

        orderProduct = new OrderProduct(1, Location.MUNICH, 5);
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);

        order = new Order();
        order.setId(1);
        order.setOrderProducts(orderProducts);
        order.setOrderCost(500.0);
        order.setDeliveryCost(30);
    }

    @Test
    void getAllOrders_ShouldReturnOrders() {
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(order));
        List<Order> orders = orderService.getAllOrders();
        assertFalse(orders.isEmpty());
        verify(orderRepository, times(1)).findAll();
    }


    @Test
    void getOrderById_ShouldReturnOrder() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        Order result = orderService.getOrderById(1);
        assertEquals(order.getId(), result.getId());
    }

    @Test
    void getOrderById_ShouldThrowExceptionWhenNotFound() {
        when(orderRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(ItemNotFound.class, () -> orderService.getOrderById(99));
    }

    @Test
    void createOrder_ShouldCreateOrder() {
        when(productRepository.findByProductCk_IdAndProductCk_Location(any(), any())).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order createdOrder = orderService.createOrder(Collections.singletonList(orderProduct));

        assertNotNull(createdOrder);
        assertEquals(500.0, createdOrder.getOrderCost());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_ShouldThrowException_WhenStockIsInsufficient() {
        product.setQuantity(3);
        when(productRepository.findByProductCk_IdAndProductCk_Location(any(), any())).thenReturn(product);

        assertThrows(InsufficientStockException.class, () -> orderService.createOrder(Collections.singletonList(orderProduct)));
    }

    @Test
    void deleteOrder_ShouldDeleteOrderAndRestoreStock() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        doNothing().when(orderService).increaseProductsStockAfterDeletingOrder(any());

        orderService.deleteOrder(1);
        assertEquals(10, product.getQuantity());
        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    void deleteOrderProduct_ShouldRemoveProductAndIncreaseStock() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(productRepository.findByProductCk_IdAndProductCk_Location(any(), any())).thenReturn(product);

        orderService.deleteOrderProduct(1, 1, Location.MUNICH);
        assertEquals(15, product.getQuantity());

        verify(orderRepository, times(1)).saveAndFlush(any(Order.class));
    }

    @Test
    void editOrderProductQuantity_ShouldUpdateQuantity() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(productRepository.findByProductCk_IdAndProductCk_Location(any(), any())).thenReturn(product);
        when(orderRepository.saveAndFlush(order)).thenReturn(order);

        Order updatedOrder = orderService.editOrderProductQuantity(1, 1, Location.MUNICH, 3);

        assertEquals(3, updatedOrder.getOrderProducts().get(0).getQuantity());
        verify(orderRepository, times(1)).saveAndFlush(any(Order.class));
    }

    @Test
    void createOrder_ShouldDecreaseProductStock() {
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);

        when(productRepository.findByProductCk_IdAndProductCk_Location(1, Location.MUNICH)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);

        orderService.createOrder(orderProducts);

        assertEquals(5, product.getQuantity());
        verify(productRepository, times(1)).save(product);
    }

}
