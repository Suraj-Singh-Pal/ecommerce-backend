package com.suraj.ecommerce.controller;

import com.suraj.ecommerce.dto.CreateOrderRequest;
import com.suraj.ecommerce.dto.OrderResponse;
import com.suraj.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            Authentication authentication,
            @Valid @RequestBody CreateOrderRequest request) {

        OrderResponse orderResponse =
                orderService.createOrder(
                        authentication.getName(),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderResponse);
    }

@GetMapping
public ResponseEntity<List<OrderResponse>> getMyOrders(
        Authentication authentication) {

    List<OrderResponse> orders =
            orderService.getMyOrders(
                    authentication.getName()
            );

    return ResponseEntity.ok(orders);
}

@GetMapping("/{orderId}")
public ResponseEntity<OrderResponse> getMyOrder(
        Authentication authentication,
        @PathVariable Long orderId) {

    OrderResponse orderResponse =
            orderService.getMyOrder(
                    authentication.getName(),
                    orderId
            );

    return ResponseEntity.ok(orderResponse);
}

// =========================================================
// ADMIN - GET ALL ORDERS
// =========================================================

@GetMapping("/admin")
public ResponseEntity<Page<OrderResponse>> getAllOrders(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size) {

    return ResponseEntity.ok(
            orderService.getAllOrders(page, size)
    );
}

// =========================================================
// ADMIN - GET ORDERS BY STATUS
// =========================================================

@GetMapping("/admin/status")
public ResponseEntity<Page<OrderResponse>> getOrdersByStatus(
        @RequestParam String status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size) {

    return ResponseEntity.ok(
            orderService.getOrdersByStatus(status, page, size)
    );
}

@PatchMapping("/{orderId}/cancel")
public ResponseEntity<OrderResponse> cancelOrder(
        Authentication authentication,
        @PathVariable Long orderId) {

    OrderResponse orderResponse =
            orderService.cancelOrder(
                    authentication.getName(),
                    orderId
            );

    return ResponseEntity.ok(orderResponse);
}

@PatchMapping("/{orderId}/status")
public ResponseEntity<OrderResponse> updateOrderStatus(
        @PathVariable Long orderId,
        @RequestParam String status) {

    OrderResponse orderResponse =
            orderService.updateOrderStatus(
                    orderId,
                    status
            );

    return ResponseEntity.ok(orderResponse);
}

}