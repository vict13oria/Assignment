package ing.assessment.controller;

import ing.assessment.db.order.Order;
import ing.assessment.db.order.OrderProduct;
import ing.assessment.model.Location;
import ing.assessment.service.OrderService;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
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
        return ResponseEntity.ok().body(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable("id") Integer id) {
        return ResponseEntity.ok().body(orderService.getOrderById(id));
    }

    @GetMapping("/get-orders-by-ids")
    public ResponseEntity<List<Order>> getOrders(@RequestParam("ids") List<Integer> ids) {
        return ResponseEntity.ok().body(orderService.getOrdersByIds(ids));
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody List<OrderProduct> orderProductList) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(orderProductList));
    }

    @PatchMapping("/{orderId}/edit-product-quantity")
    public ResponseEntity<Order> editOrderProductQuantity(
            @PathVariable("orderId") @Nonnull Integer orderId,
            @RequestParam("productId") Integer productId,
            @RequestParam("location") Location location,
            @RequestBody @Valid Integer quantity) {
        return ResponseEntity.ok().body(orderService.editOrderProductQuantity(orderId, productId, location, quantity));
    }

    @DeleteMapping("/{orderId}/delete-product")
    public ResponseEntity<HttpStatus> deleteOrderProduct(
            @PathVariable("orderId") Integer orderId,
            @RequestParam("productId") Integer productId,
            @RequestParam("location") Location location) {

        orderService.deleteOrderProduct(orderId, productId, location);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<HttpStatus> deleteOrder(@PathVariable("orderId") Integer orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
