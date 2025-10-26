package com.example.rental.controller;

import com.example.rental.service.MomoService;
import com.example.rental.dto.momo.CreateMomoResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/momo")
public class MomoController {
    private final MomoService momoService;

    @PostMapping("create")
    public CreateMomoResponse createQR(){
        return momoService.createQR();
    }
}