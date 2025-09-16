package com.example.datnspct.Service;

import com.example.datnspct.Model.MoMoTransaction;
import com.example.datnspct.Repository.MoMoTransactionRepository;
import com.example.datnspct.dto.MoMoPaymentRequest;
import com.example.datnspct.dto.MoMoPaymentResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MoMoPaymentService {

    private final MoMoTransactionRepository moMoTransactionRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // MoMo Test Environment Configuration
    @Value("${momo.partner.code:MOMO}")
    private String partnerCode;

    @Value("${momo.access.key:F8BBA842ECF85}")
    private String accessKey;

    @Value("${momo.secret.key:K951B6PE1waDMi640xX08PD3vg6EkVlz}")
    private String secretKey;

    @Value("${momo.endpoint:https://test-payment.momo.vn/v2/gateway/api/create}")
    private String endpoint;

    @Value("${app.base.url:http://localhost:8080}")
    private String baseUrl;

    public MoMoPaymentResponse createPayment(MoMoPaymentRequest request) {
        try {
            System.out.println("=== MoMo Payment Request ===");
            System.out.println("Amount: " + request.getAmount());
            System.out.println("OrderId: " + request.getOrderId());

            String requestId = UUID.randomUUID().toString();
            String orderId = request.getOrderId();
            String orderInfo = request.getOrderInfo();
            String redirectUrl = request.getRedirectUrl() != null ? request.getRedirectUrl() : baseUrl + "/momo/return";
            String ipnUrl = request.getIpnUrl() != null ? request.getIpnUrl() : baseUrl + "/api/momo/notify";
            String amount = String.valueOf(request.getAmount().longValue()); // Chuyển về long để tránh lỗi decimal
            String extraData = request.getExtraData() != null ? request.getExtraData() : "";
            String requestType = "captureWallet";

            System.out.println("Processing amount as: " + amount);

            // Tạo raw signature
            String rawSignature = "accessKey=" + accessKey +
                    "&amount=" + amount +
                    "&extraData=" + extraData +
                    "&ipnUrl=" + ipnUrl +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + partnerCode +
                    "&redirectUrl=" + redirectUrl +
                    "&requestId=" + requestId +
                    "&requestType=" + requestType;

            // Tạo signature
            String signature = hmacSHA256(rawSignature, secretKey);

            // Tạo request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("partnerCode", partnerCode);
            requestBody.put("partnerName", "NEXVIBE Store");
            requestBody.put("storeId", "MomoTestStore");
            requestBody.put("requestId", requestId);
            requestBody.put("amount", Long.parseLong(amount));
            requestBody.put("orderId", orderId);
            requestBody.put("orderInfo", orderInfo);
            requestBody.put("redirectUrl", redirectUrl);
            requestBody.put("ipnUrl", ipnUrl);
            requestBody.put("lang", "vi");
            requestBody.put("extraData", extraData);
            requestBody.put("requestType", requestType);
            requestBody.put("signature", signature);

            System.out.println("Request body: " + objectMapper.writeValueAsString(requestBody));
            System.out.println("Signature: " + signature);
            System.out.println("Raw signature: " + rawSignature);

            // Gửi request tới MoMo
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            System.out.println("Sending request to: " + endpoint);
            ResponseEntity<String> response = restTemplate.postForEntity(endpoint, entity, String.class);

            System.out.println("MoMo response: " + response.getBody());

            // Parse response
            MoMoPaymentResponse momoResponse = objectMapper.readValue(response.getBody(), MoMoPaymentResponse.class);

            // Lưu transaction vào database
            MoMoTransaction transaction = new MoMoTransaction();
            transaction.setOrderId(orderId);
            transaction.setRequestId(requestId);
            transaction.setAmount(request.getAmount());
            transaction.setOrderInfo(orderInfo);
            transaction.setRedirectUrl(redirectUrl);
            transaction.setIpnUrl(ipnUrl);
            transaction.setPayUrl(momoResponse.getPayUrl());
            transaction.setQrCodeUrl(momoResponse.getQrCodeUrl());
            transaction.setDeepLink(momoResponse.getDeeplink());
            transaction.setResponseCode(momoResponse.getResultCode());
            transaction.setMessage(momoResponse.getMessage());
            transaction.setPayType("QR");
            transaction.setTransactionStatus("PENDING");
            transaction.setNgayTao(LocalDateTime.now());
            transaction.setExtraData(extraData);
            transaction.setSignature(signature);

            moMoTransactionRepository.save(transaction);

            return momoResponse;

        } catch (Exception e) {
            System.err.println("=== ERROR in MoMo Payment ===");
            System.err.println("Error message: " + e.getMessage());
            System.err.println("Error class: " + e.getClass().getSimpleName());
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tạo thanh toán MoMo: " + e.getMessage());
        }
    }

    public void updateTransactionStatus(String orderId, String transId, String responseCode, String message) {
        MoMoTransaction transaction = moMoTransactionRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch với orderId: " + orderId));

        transaction.setTransId(transId);
        transaction.setResponseCode(responseCode);
        transaction.setMessage(message);
        transaction.setNgayCapNhat(LocalDateTime.now());

        if ("0".equals(responseCode)) {
            transaction.setTransactionStatus("SUCCESS");
        } else {
            transaction.setTransactionStatus("FAILED");
        }

        moMoTransactionRepository.save(transaction);
    }

    public boolean verifySignature(Map<String, String> params) {
        try {
            String signature = params.get("signature");
            String partnerCode = params.get("partnerCode");
            String orderId = params.get("orderId");
            String requestId = params.get("requestId");
            String amount = params.get("amount");
            String orderInfo = params.get("orderInfo");
            String orderType = params.get("orderType");
            String transId = params.get("transId");
            String resultCode = params.get("resultCode");
            String message = params.get("message");
            String payType = params.get("payType");
            String responseTime = params.get("responseTime");
            String extraData = params.get("extraData");

            String rawSignature = "accessKey=" + accessKey +
                    "&amount=" + amount +
                    "&extraData=" + extraData +
                    "&message=" + message +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&orderType=" + orderType +
                    "&partnerCode=" + partnerCode +
                    "&payType=" + payType +
                    "&requestId=" + requestId +
                    "&responseTime=" + responseTime +
                    "&resultCode=" + resultCode +
                    "&transId=" + transId;

            String computedSignature = hmacSHA256(rawSignature, secretKey);
            return signature.equals(computedSignature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String hmacSHA256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder result = new StringBuilder();
        for (byte b : hash) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public void saveTransactionFromPOS(Map<String, Object> transactionData) {
        try {
            MoMoTransaction transaction = new MoMoTransaction();

            // Lấy dữ liệu từ Map
            transaction.setIdHD(Integer.valueOf(transactionData.get("idHD").toString()));
            transaction.setOrderId(transactionData.get("orderId").toString());
            transaction.setRequestId(transactionData.get("requestId").toString());
            transaction.setAmount(new BigDecimal(transactionData.get("amount").toString()));
            transaction.setOrderInfo(transactionData.get("orderInfo").toString());
            transaction.setTransactionStatus(transactionData.get("transactionStatus").toString());
            transaction.setMessage(transactionData.get("message").toString());
            transaction.setLocalMessage(transactionData.get("localMessage").toString());
            transaction.setResponseCode(transactionData.get("responseCode").toString());
            transaction.setPayType("QR");
            transaction.setNgayTao(LocalDateTime.now());
            transaction.setNgayCapNhat(LocalDateTime.now());

            moMoTransactionRepository.save(transaction);
            System.out.println("Đã lưu MoMo transaction thành công với orderId: " + transaction.getOrderId());

        } catch (Exception e) {
            System.err.println("Lỗi khi lưu MoMo transaction: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Không thể lưu thông tin giao dịch MoMo");
        }
    }
}