package com.thederailingmafia.carwash.bookingservice.controller;

import com.thederailingmafia.carwash.bookingservice.dto.OrderRequest;
import com.thederailingmafia.carwash.bookingservice.dto.OrderResponse;
import com.thederailingmafia.carwash.bookingservice.model.Order;
import com.thederailingmafia.carwash.bookingservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class BookingController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @PostMapping("/wash-now")
    public OrderResponse bookWashNow(@RequestBody OrderRequest orderRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        OrderResponse response = orderService.bookWashNow(orderRequest, email);
        return response;
    }

    @PostMapping("/schdule/wash")
    public OrderResponse schduleBookWash(Authentication authentication ,@RequestBody OrderRequest orderRequest) {
        String email = authentication.getName();
        OrderResponse response = orderService.scheduleWashNow(orderRequest, email);
        return response;
    }

    @GetMapping("/pending")
    public List<OrderResponse> getPendingOrder() {
        List<OrderResponse> pendingOrders = orderService.getPendingOrders();
        return pendingOrders;
    }

    @PostMapping("/assign")
    public OrderResponse assignOrder(@RequestParam Long orderId, @RequestParam String washerEmail) {
        OrderResponse response = orderService.assignOrder(orderId, washerEmail);
        return response;
    }

    @GetMapping("/current")
    public List<OrderResponse> getCurrentOrder() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        String role = auth.getAuthorities().stream().findFirst().get().getAuthority();
        List<OrderResponse> orderResponses = orderService.getCurrentOrders(email, role);
        return orderResponses;
    }

    @GetMapping("/past")
    public List<OrderResponse> getPastOrder() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        String role = auth.getAuthorities().stream().findFirst().get().getAuthority();
        List<OrderResponse> orderResponses = orderService.getPastOrders(email, role);
        return orderResponses;
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        String role = auth.getAuthorities().stream().findFirst().get().getAuthority();
        OrderResponse response = orderService.getOrder(id, email, role);
        return response;
    }
}
