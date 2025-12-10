package com.example.rental.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.example.rental.exception.SignatureVerificationException;
import com.example.rental.service.MomoService;
import com.example.rental.dto.momo.CreateMomoResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/momo")
@Slf4j
public class MomoController {
    private final MomoService momoService;

    // @PostMapping("create")
    // public CreateMomoResponse createATMPayment(){
    //     return momoService.createATMPayment();
    // }

    @PostMapping("/ipn-handler")
    public ResponseEntity<String> handleMomoCallback(@RequestBody Map<String, Object> payload) {
        
        try {
            momoService.handleMomoCallback(payload);

            return ResponseEntity.noContent().build();

        } catch (SignatureVerificationException | IllegalArgumentException e) {
            log.warn("Loi xac thuc callback: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            
        } catch (Exception e) {
            log.error("Loi nghiem trong khi xu ly callback Momo: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Callback processing error");
        }
    }
    
}