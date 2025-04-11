package com.thederailingmafia.carwash.bookingservice.service;

import com.thederailingmafia.carwash.bookingservice.dto.OrderRequest;
import com.thederailingmafia.carwash.bookingservice.dto.OrderResponse;
import com.thederailingmafia.carwash.bookingservice.exception.OrderNotFoundException;
import com.thederailingmafia.carwash.bookingservice.model.Order;
import com.thederailingmafia.carwash.bookingservice.model.OrderStatus;
import com.thederailingmafia.carwash.bookingservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public OrderResponse bookWashNow(OrderRequest request, String userEmail) {
        Order order = new Order();
        order.setCustomerEmail(userEmail);
        order.setCarId(request.getCarId());

        Order savedOrder = orderRepository.save(order);
        return  mapToResponse(savedOrder);
    }

    public OrderResponse scheduleWashNow(OrderRequest request, String userEmail) {
        Order order = new Order();
        order.setCustomerEmail(userEmail);
        order.setCarId(request.getCarId());
        order.setScheduledTime(request.getScheduledTime());
        Order savedOrder = orderRepository.save(order);
        return  mapToResponse(savedOrder);
    }

    public List<OrderResponse> getPendingOrders() {
        return orderRepository.findByStatus(OrderStatus.PENDING)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public OrderResponse assignOrder(Long orderId, String washerEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if(order.getStatus() != OrderStatus.PENDING){
            throw new RuntimeException("Order status is not PENDING");
        }

        order.setWasherEmail(washerEmail);
        order.setStatus(OrderStatus.ASSIGNED);
        Order savedOrder = orderRepository.save(order);
        return  mapToResponse(savedOrder);
    }

    public List<OrderResponse> getCurrentOrders(String userEmail, String role) {
        List<Order> orders;
        String normalizedRole = role.startsWith("ROLE_") ? role.substring(5) : role;
        System.out.println("OrderService getCurrentOrders for " + userEmail + " (" + normalizedRole + ")");
        if ("CUSTOMER".equals(normalizedRole)) {
            orders = orderRepository.findByCustomerEmail(userEmail);
        } else if ("WASHER".equals(normalizedRole)) {
            orders = orderRepository.findByWasherEmail(userEmail);
        } else {
            throw new RuntimeException("Invalid role: " + normalizedRole);
        }
        return orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PENDING || o.getStatus() == OrderStatus.ASSIGNED)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getPastOrders(String userEmail, String role) {
        List<Order> orders;
        if ("CUSTOMER".equals(role)) {
            orders = orderRepository.findByCustomerEmail(userEmail);
        } else if ("WASHER".equals(role)) {
            orders = orderRepository.findByWasherEmail(userEmail);
        } else {
            throw new RuntimeException("Invalid role");
        }
        return orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.ACCEPTED || o.getStatus() == OrderStatus.CANCELED)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrder(Long orderId, String userEmail, String role) {
        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getCustomerEmail().equals(userEmail) ||
                        (o.getWasherEmail() != null && o.getWasherEmail().equals(userEmail)) ||
                        "ADMIN".equals(role))
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        return  mapToResponse(order);
    }


    // OrderService.java
    public OrderResponse updateOrder(Long id, OrderResponse request, String userEmail, String role) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        System.out.println("OrderService updating order " + id + " by " + userEmail + " (" + role + ")");
        String normalizedRole = role.startsWith("ROLE_") ? role.substring(5) : role;

        // Check permissions based on role
        if ("WASHER".equals(normalizedRole)) {
            if (!userEmail.equals(order.getWasherEmail())) {
                throw new RuntimeException("Order not assigned to this washer");
            }
        } else if ("CUSTOMER".equals(normalizedRole)) {
            if (!userEmail.equals(order.getCustomerEmail())) {
                throw new RuntimeException("Order not owned by this customer");
            }
        } else if (!"ADMIN".equals(normalizedRole)) {
            throw new RuntimeException("Unauthorized role: " + normalizedRole);
        }

        String statusStr = request.getStatus();
        if (statusStr == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        try {
            OrderStatus status = OrderStatus.valueOf(statusStr.trim().toUpperCase());
            order.setStatus(status);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status value: " + statusStr + ", error: " + e.getMessage());
            throw new RuntimeException("Invalid status: " + statusStr);
        }

        if (request.getWasherEmail() != null) {
            order.setWasherEmail(request.getWasherEmail());
        }
        Order updatedOrder = orderRepository.save(order);
        System.out.println("OrderService updated order: " + updatedOrder.getId() + ", status: " + updatedOrder.getStatus());
        return mapToResponse(updatedOrder);
    }
    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerEmail(order.getCustomerEmail());
        response.setWasherEmail(order.getWasherEmail());
        response.setCarId(order.getCarId());
        response.setStatus(order.getStatus().name());
        response.setScheduledTime(order.getScheduledTime());
        response.setCreatedAt(order.getCreatedAt());
        return response;
    }
}
