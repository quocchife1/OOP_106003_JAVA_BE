package com.example.rental.service.impl;

import com.example.rental.exception.SignatureVerificationException;
import com.example.rental.client.MomoApi;
import com.example.rental.service.MomoService;
import com.example.rental.service.PartnerPostService;
import com.example.rental.entity.PostApprovalStatus;
import com.example.rental.entity.PartnerPost;
import com.example.rental.dto.momo.CreateMomoResponse;
import com.example.rental.dto.momo.CreateMomoRequest;
import com.example.rental.repository.PartnerPostRepository;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.Map;
import java.util.Objects;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
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
    private final PartnerPostRepository partnerPostRepository;

    @Override
    public CreateMomoResponse createATMPayment(long amount, String orderId) {

        String orderInfo = "Thanh toán bài đăng";
        String requestId = UUID.randomUUID().toString();
        String extraData = "";

        String rawSignature = String.format(
                "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                ACCESS_KEY, amount, extraData, IPN_URL, orderId, orderInfo, PARTNER_CODE, REDIRECT_URL, requestId,
                REQUEST_TYPE);

        String prettySignature = "";
        log.debug("rawSignature" + rawSignature);
        log.debug(SECRET_KEY);
        try {
            prettySignature = signHmacSHA256(rawSignature, SECRET_KEY);
        } catch (Exception e) {
            log.error(">>>>Co loi khi hash code: " + e);
            return null;
        }

        if (prettySignature.isBlank()) {
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

    @Override
    public void handleMomoCallback(Map<String, Object> payload) {
        log.info("Call back tu momo nhan duoc: {}", payload);

        String momoSignature = (String) payload.get("signature");
        if (momoSignature == null) {
            log.warn("Khong tim thay chu ky (signature) trong callback.");
            // Ném lỗi để Controller bắt
            throw new IllegalArgumentException("Invalid callback: Missing signature");
        }

        String amountStr = Objects.toString(payload.get("amount"));
        String extraData = Objects.toString(payload.get("extraData"));
        String message = Objects.toString(payload.get("message"));
        String orderId = Objects.toString(payload.get("orderId"));
        String orderInfo = Objects.toString(payload.get("orderInfo"));
        String orderType = Objects.toString(payload.get("orderType"));
        String partnerCode = Objects.toString(payload.get("partnerCode"));
        String payType = Objects.toString(payload.get("payType"));
        String requestId = Objects.toString(payload.get("requestId"));
        String responseTime = Objects.toString(payload.get("responseTime"));
        String resultCode = Objects.toString(payload.get("resultCode"));
        String transId = Objects.toString(payload.get("transId"));

        long amountLong = 0L;
        try {
            // Nếu amountStr là rỗng "" hoặc null, gán mặc định là 0
            if (amountStr == null || amountStr.isEmpty()) {
                amountLong = 0L;
            } else {
                amountLong = Long.parseLong(amountStr);
            }
        } catch (NumberFormatException e) {
            log.warn("Không thể ép kiểu 'amount' từ payload: {}", amountStr);
            // Ném lỗi nếu 'amount' là bắt buộc
            throw new IllegalArgumentException("Invalid amount format in callback");
        }

        String rawSignature = String.format(
                "accessKey=%s&amount=%s&extraData=%s&message=%s&orderId=%s&orderInfo=%s&orderType=%s&partnerCode=%s&payType=%s&requestId=%s&responseTime=%s&resultCode=%s&transId=%s",
                ACCESS_KEY, amountLong, extraData, message, orderId, orderInfo, orderType, partnerCode, payType, requestId,
                responseTime, resultCode, transId);

        log.debug("rawSignature" + rawSignature);
        log.debug(SECRET_KEY);
        try {
            String mySignature = signHmacSHA256(rawSignature, SECRET_KEY);
            log.debug("Chu ky MoMo: {}", momoSignature);
            log.debug("Chu ky cua toi: {}", mySignature);

            if (!mySignature.equals(momoSignature)) {
                log.warn("XAC THUC CHU KY THAT BAI, Callback co the da bi gia mao.");
                // Ném lỗi bảo mật
                throw new SignatureVerificationException("Invalid signature");
            }

            log.info("Xac thuc chu ky thanh cong cho orderId: {}", orderId);

            PartnerPost post = partnerPostRepository.findByOrderId(orderId)
                    .orElseThrow(
                            () -> new EntityNotFoundException("Khong tim thay bai dang moi voi orderId: " + orderId));

            if ("0".equals(resultCode)) { // 0 = Thành công
                log.info("Thanh toan THANH CONG cho orderId: {}. Cap nhat trang thai...", orderId);
                post.setStatus(PostApprovalStatus.PENDING_APPROVAL);
                partnerPostRepository.save(post);
            } else {
                log.info("Thanh toan THAT BAI cho orderId: {}. Ma loi: {}. Cap nhat trang thai...", orderId,
                        resultCode);
                post.setStatus(PostApprovalStatus.PENDING_PAYMENT);
                partnerPostRepository.save(post);
            }

        } catch (SignatureVerificationException e) {
            // Ném lại lỗi bảo mật để Controller xử lý
            throw e;
        } catch (Exception e) {
            // Ném lỗi chung nếu có vấn đề khi băm
            log.error("Loi khi xu ly callback: ", e);
            throw new RuntimeException("Callback processing error", e);
        }
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
