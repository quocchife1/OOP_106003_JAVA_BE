package com.example.rental.service;

import java.util.Map;
import com.example.rental.dto.momo.CreateMomoResponse;

public interface MomoService {
    CreateMomoResponse createATMPayment(long amount, String orderId);

    void handleMomoCallback(Map<String, Object> payload);
}