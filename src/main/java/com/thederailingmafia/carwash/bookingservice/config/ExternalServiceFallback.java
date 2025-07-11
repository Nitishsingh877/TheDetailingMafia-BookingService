package com.thederailingmafia.carwash.bookingservice.config;

import com.thederailingmafia.carwash.bookingservice.feign.UserServiceClient;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class ExternalServiceFallback implements UserServiceClient {
    @Override
    public List<String> getWashers() {
        System.out.println("Fallback triggered: User service unavailable.");
        return Collections.singletonList("Fallback response: No Washer Available , Check user Service.");
    }
}