package ing.assessment.controller;

import ing.assessment.db.order.Order;
import ing.assessment.db.order.OrderProduct;
import ing.assessment.model.Location;
import ing.assessment.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;


    @PostMapping
    public Order createOrder(@RequestBody List<OrderProduct> orderProductList) {
        return orderService.createOrder(orderProductList);
    }

    @DeleteMapping("/{orderId}/product/{productId}/location/{location}")
    public Order deleteOrderProduct(@PathVariable Integer orderId,
                                    @PathVariable Integer productId,
                                    @PathVariable Location location) {
        return orderService.deleteOrderProduct(orderId, productId, location);
    }

}
