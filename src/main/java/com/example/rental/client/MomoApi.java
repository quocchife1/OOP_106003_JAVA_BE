package com.example.rental.client;

import com.example.rental.dto.momo.CreateMomoRequest;
import com.example.rental.dto.momo.CreateMomoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@FeignClient(name="momo", url = "${momo.end-point}")
public interface MomoApi {

    @PostMapping("/create")
    CreateMomoResponse createMomoATMPayment(@RequestBody CreateMomoRequest createMomoRequest);
}