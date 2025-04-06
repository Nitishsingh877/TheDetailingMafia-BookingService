package com.thederailingmafia.carwash.bookingservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "car_orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customerEmail;
    private String washerEmail;
    private Long carId;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private LocalDateTime scheduledTime;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if (status == null) status = OrderStatus.PENDING;
    }
    //initially if nothing goes mark order as pending order.

}
