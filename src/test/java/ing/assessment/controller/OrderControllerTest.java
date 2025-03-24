package ing.assessment.controller;

import ing.assessment.db.order.Order;
import ing.assessment.db.order.OrderProduct;
import ing.assessment.model.Location;
import ing.assessment.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private Order order;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        order = new Order();
        order.setId(1);
    }

    @Test
    void getAllOrders_ShouldReturnListOfOrders() throws Exception {
        when(orderService.getAllOrders()).thenReturn(Collections.singletonList(order));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void getOrderById_ShouldReturnOrder() throws Exception {
        when(orderService.getOrderById(1)).thenReturn(order);

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(orderService, times(1)).getOrderById(1);
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() throws Exception {
        List<OrderProduct> orderProducts = List.of(new OrderProduct(1, Location.MUNICH, 5));
        when(orderService.createOrder(any())).thenReturn(order);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"productId\":1,\"location\":\"MUNICH\",\"quantity\":5}]"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(orderService, times(1)).createOrder(any());
    }

    @Test
    void editOrderProductQuantity_ShouldUpdateOrder() throws Exception {
        when(orderService.editOrderProductQuantity(anyInt(), anyInt(), any(), anyInt())).thenReturn(order);

        mockMvc.perform(patch("/orders/1/edit-product-quantity?productId=1&location=MUNICH")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(orderService, times(1)).editOrderProductQuantity(anyInt(), anyInt(), any(), anyInt());
    }

    @Test
    void deleteOrderProduct_ShouldReturnNoContent() throws Exception {
        doNothing().when(orderService).deleteOrderProduct(anyInt(), anyInt(), any());

        mockMvc.perform(delete("/orders/1/delete-product?productId=1&location=MUNICH"))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).deleteOrderProduct(anyInt(), anyInt(), any());
    }

    @Test
    void deleteOrder_ShouldReturnNoContent() throws Exception {
        doNothing().when(orderService).deleteOrder(anyInt());

        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).deleteOrder(1);
    }
}
