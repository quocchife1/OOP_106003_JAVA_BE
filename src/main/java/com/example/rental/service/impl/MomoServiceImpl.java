package com.example.rental.service.impl;

import com.example.rental.client.MomoApi;
import com.example.rental.service.MomoService;
import com.example.rental.dto.momo.CreateMomoResponse;
import com.example.rental.dto.momo.CreateMomoRequest;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MomoServiceImpl implements MomoService {

    @Value(value = "${momo.partner-code}")
    private String PARTNER_CODE;
    @Value(value = "${momo.access-key}")
    private String ACCESS_KEY;
    @Value(value = "${momo.secret-key}")
    private String SECRET_KEY;
    @Value(value = "${momo.redirect-url}")
    private String REDIRECT_URL;
    @Value(value = "${momo.ipn-url}")
    private String IPN_URL;
    @Value(value = "${momo.request-type}")
    private String REQUEST_TYPE;

    private final MomoApi momoApi;

    public CreateMomoResponse createATMPayment(){

        String orderId = UUID.randomUUID().toString();
        String orderInfo = "Thanh toan don hang: " + orderId;
        String requestId = UUID.randomUUID().toString();
        String extraData = "Khong co khuyen mai gi het";
        long amount = 100000;

        String rawSignature = String.format(
            "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
            ACCESS_KEY, amount, extraData, IPN_URL, orderId, orderInfo, PARTNER_CODE, REDIRECT_URL, requestId, REQUEST_TYPE);

        String prettySignature = "";
        try {
            prettySignature = signHmacSHA256(rawSignature, SECRET_KEY);
        } catch (Exception e){
            log.error(">>>>Co loi khi hash code: " + e);
            return null;
        }

        if (prettySignature.isBlank()){
            log.error(">>>> signature is blank");
            return null;
        }

        CreateMomoRequest request = CreateMomoRequest.builder()
                .partnerCode(PARTNER_CODE)
                .requestType(REQUEST_TYPE)
                .ipnUrl(IPN_URL)
                .redirectUrl(REDIRECT_URL)
                .orderId(orderId)
                .orderInfo(orderInfo)
                .requestId(requestId)
                .extraData(extraData)
                .amount(amount)
                .signature(prettySignature)
                .lang("vi")
                .build();

        return momoApi.createMomoATMPayment(request);
    }

    private String signHmacSHA256(String data, String key) throws Exception {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSHA256.init(secretKey);
        byte[] hash = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
