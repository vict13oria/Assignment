package ing.assessment.controller;

import ing.assessment.db.order.Order;
import ing.assessment.db.order.OrderProduct;
import ing.assessment.model.Location;
import ing.assessment.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable("id") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody List<OrderProduct> orderProductList) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(orderProductList));
    }

    @DeleteMapping("/{orderId}/product/{productId}/location/{location}")
    public ResponseEntity<HttpStatus> deleteOrderProduct(@PathVariable Integer orderId,
                                    @PathVariable Integer productId,
                                    @PathVariable Location location) {
        orderService.deleteOrderProduct(orderId, productId, location);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
