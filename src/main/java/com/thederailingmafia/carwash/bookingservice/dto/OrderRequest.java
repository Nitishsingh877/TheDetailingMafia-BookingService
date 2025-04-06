package com.thederailingmafia.carwash.bookingservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderRequest {
    private Long carId;
    private LocalDateTime scheduledTime;
}
