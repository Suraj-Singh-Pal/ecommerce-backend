package com.suraj.ecommerce.service;

import com.suraj.ecommerce.dto.CreateOrderRequest;
import com.suraj.ecommerce.dto.OrderItemResponse;
import com.suraj.ecommerce.dto.OrderResponse;
import com.suraj.ecommerce.entity.Cart;
import com.suraj.ecommerce.entity.CartItem;
import com.suraj.ecommerce.entity.Order;
import com.suraj.ecommerce.entity.OrderItem;
import com.suraj.ecommerce.entity.Product;
import com.suraj.ecommerce.entity.User;
import com.suraj.ecommerce.exception.PriceChangedException;
import com.suraj.ecommerce.exception.ResourceNotFoundException;
import com.suraj.ecommerce.repository.CartItemRepository;
import com.suraj.ecommerce.repository.CartRepository;
import com.suraj.ecommerce.repository.OrderItemRepository;
import com.suraj.ecommerce.repository.OrderRepository;
import com.suraj.ecommerce.repository.ProductRepository;
import com.suraj.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository,
            ProductRepository productRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // =========================================================
    // CREATE ORDER
    // =========================================================

    
@Transactional
public OrderResponse createOrder(
        String username,
        CreateOrderRequest request
) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() ->
                    new ResourceNotFoundException("User not found"));

    Cart cart = cartRepository.findByUserIdAndActiveTrue(user.getId())
            .orElseThrow(() ->
                    new ResourceNotFoundException("Active cart not found"));

    List<CartItem> cartItems =
            cartItemRepository.findByCartIdAndCartActiveTrue(cart.getId());

    if (cartItems.isEmpty()) {
        throw new IllegalArgumentException(
                "Cannot create order because cart is empty");
    }

    List<Map<String, Object>> priceDetails = new ArrayList<>();
    boolean priceChanged = false;

    // Verify current product price and stock before creating an order.
    for (CartItem cartItem : cartItems) {
        Product product = cartItem.getProduct();

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new IllegalArgumentException(
                    "Product is not active: " + product.getName());
        }

        if (product.getStockQuantity() < cartItem.getQuantity()) {
            throw new IllegalArgumentException(
                    "Insufficient stock for product: " + product.getName());
        }

        BigDecimal oldPrice = cartItem.getUnitPrice();
        BigDecimal currentPrice = product.getPrice();

        boolean thisPriceChanged =
                oldPrice.compareTo(currentPrice) != 0;

        if (thisPriceChanged) {
            priceChanged = true;
        }

        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("productId", product.getId());
        detail.put("productName", product.getName());
        detail.put("quantity", cartItem.getQuantity());
        detail.put("previousUnitPrice", oldPrice);
        detail.put("currentUnitPrice", currentPrice);
        detail.put(
                "currentTotalPrice",
                currentPrice.multiply(
                        BigDecimal.valueOf(cartItem.getQuantity())));
        detail.put("priceChanged", thisPriceChanged);

        priceDetails.add(detail);
    }

    // If prices changed, require confirmation of the exact current prices.
    if (priceChanged) {
        Map<Long, BigDecimal> confirmedPrices =
                request.getConfirmedPrices();

        boolean confirmationMatches =
                request.isConfirmPriceChanges()
                && confirmedPrices != null;

        if (confirmationMatches) {
            for (CartItem cartItem : cartItems) {
                Long productId = cartItem.getProduct().getId();
                BigDecimal confirmedPrice = confirmedPrices.get(productId);
                BigDecimal currentPrice = cartItem.getProduct().getPrice();

                if (confirmedPrice == null
                        || confirmedPrice.compareTo(currentPrice) != 0) {
                    confirmationMatches = false;
                    break;
                }
            }
        }

        if (!confirmationMatches) {
            throw new PriceChangedException(
                    "Product prices have changed. Review the current prices "
                            + "and retry checkout with confirmedPrices.",
                    priceDetails);
        }
    }

    // Calculate order total using current database prices.
    BigDecimal totalAmount = BigDecimal.ZERO;

    for (CartItem cartItem : cartItems) {
        BigDecimal itemTotal = cartItem.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        totalAmount = totalAmount.add(itemTotal);
    }

    Order order = Order.builder()
            .user(user)
            .totalAmount(totalAmount)
            .status("PENDING")
            .shippingAddress(request.getShippingAddress())
            .build();

    Order savedOrder = orderRepository.save(order);

    // Atomically decrease stock and save confirmed price snapshots.
    for (CartItem cartItem : cartItems) {
        Product product = cartItem.getProduct();

        int updatedRows = productRepository.decreaseStock(
                product.getId(),
                cartItem.getQuantity());

        if (updatedRows == 0) {
            throw new IllegalArgumentException(
                    "Insufficient stock for product: " + product.getName());
        }

        BigDecimal confirmedPrice = product.getPrice();

        BigDecimal itemTotal = confirmedPrice.multiply(
                BigDecimal.valueOf(cartItem.getQuantity()));

        OrderItem orderItem = OrderItem.builder()
                .order(savedOrder)
                .product(product)
                .quantity(cartItem.getQuantity())
                .unitPrice(confirmedPrice)
                .totalPrice(itemTotal)
                .build();

        orderItemRepository.save(orderItem);
    }

    cartItemRepository.deleteByCartId(cart.getId());

    return mapToResponse(savedOrder);
}

    // =========================================================
    // GET MY ORDERS
    // =========================================================

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        List<Order> orders =
                orderRepository.findByUserIdOrderByCreatedAtDesc(
                        user.getId()
                );

        return orders.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================================================
    // GET SINGLE ORDER
    // =========================================================

    @Transactional(readOnly = true)
    public OrderResponse getMyOrder(
            String username,
            Long orderId
    ) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + orderId
                        )
                );

        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "Order does not belong to your account"
            );
        }

        return mapToResponse(order);
    }

    // =========================================================
    // CANCEL ORDER
    // =========================================================

    @Transactional
    public OrderResponse cancelOrder(
            String username,
            Long orderId
    ) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + orderId
                        )
                );

        // Verify order ownership
        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "Order does not belong to your account"
            );
        }

        // Only PENDING orders can be cancelled
        if (!"PENDING".equals(order.getStatus())) {
            throw new IllegalArgumentException(
                    "Only PENDING orders can be cancelled"
            );
        }

        // Restore product stock
        List<OrderItem> orderItems =
                orderItemRepository.findByOrderId(order.getId());

        for (OrderItem orderItem : orderItems) {

            productRepository.increaseStock(
                    orderItem.getProduct().getId(),
                    orderItem.getQuantity()
            );
        }

        // Change order status
        order.setStatus("CANCELLED");

        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    // =========================================================
    // UPDATE ORDER STATUS - ADMIN
    // =========================================================

    @Transactional
    public OrderResponse updateOrderStatus(
            Long orderId,
            String status
    ) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + orderId
                        )
                );

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException(
                    "Order status is required"
            );
        }

        String newStatus = status.toUpperCase();

        // Allowed statuses
        Set<String> allowedStatuses = Set.of(
                "PENDING",
                "CONFIRMED",
                "SHIPPED",
                "DELIVERED",
                "CANCELLED"
        );

        if (!allowedStatuses.contains(newStatus)) {
            throw new IllegalArgumentException(
                    "Invalid order status"
            );
        }

        String currentStatus = order.getStatus();

        // Valid status transitions
        boolean validTransition =
                ("PENDING".equals(currentStatus)
                        && "CONFIRMED".equals(newStatus))

                || ("CONFIRMED".equals(currentStatus)
                        && "SHIPPED".equals(newStatus))

                || ("SHIPPED".equals(currentStatus)
                        && "DELIVERED".equals(newStatus));

        if (!validTransition) {
            throw new IllegalArgumentException(
                    "Invalid status transition from "
                            + currentStatus
                            + " to "
                            + newStatus
            );
        }

        order.setStatus(newStatus);

        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

// =========================================================
// ADMIN - GET ALL ORDERS
// =========================================================

@Transactional(readOnly = true)
public Page<OrderResponse> getAllOrders(
        int page,
        int size
) {

    Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by("createdAt").descending()
    );

    Page<Order> orders =
            orderRepository.findAllByOrderByCreatedAtDesc(pageable);

    return orders.map(this::mapToResponse);
}

// =========================================================
// ADMIN - GET ORDERS BY STATUS
// =========================================================

@Transactional(readOnly = true)
public Page<OrderResponse> getOrdersByStatus(
        String status,
        int page,
        int size
) {

    if (status == null || status.isBlank()) {
        throw new IllegalArgumentException(
                "Order status is required"
        );
    }

    String orderStatus = status.toUpperCase();

    Set<String> allowedStatuses = Set.of(
            "PENDING",
            "CONFIRMED",
            "SHIPPED",
            "DELIVERED",
            "CANCELLED"
    );

    if (!allowedStatuses.contains(orderStatus)) {
        throw new IllegalArgumentException(
                "Invalid order status"
        );
    }

    Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by("createdAt").descending()
    );

    Page<Order> orders =
            orderRepository.findByStatusOrderByCreatedAtDesc(
                    orderStatus,
                    pageable
            );

    return orders.map(this::mapToResponse);
}

    // =========================================================
    // MAP ORDER TO RESPONSE
    // =========================================================

    @Transactional(readOnly = true)
    private OrderResponse mapToResponse(Order order) {

        List<OrderItem> orderItems =
                orderItemRepository.findByOrderId(order.getId());

        List<OrderItemResponse> itemResponses =
                orderItems.stream()
                        .map(item ->
                                new OrderItemResponse(
                                        item.getId(),
                                        item.getProduct().getId(),
                                        item.getProduct().getName(),
                                        item.getQuantity(),
                                        item.getUnitPrice(),
                                        item.getTotalPrice()
                                )
                        )
                        .toList();

        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getShippingAddress(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                itemResponses
        );
    }
}