package com.example.datnspct.Controller.admin;

import com.example.datnspct.Model.MoMoTransaction;
import com.example.datnspct.Repository.MoMoTransactionRepository;
import com.example.datnspct.dto.MoMoPaymentResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/admin/api/momo")
public class MoMoController {

    private final MoMoTransactionRepository moMoTransactionRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${momo.partner.code:MOMO}")
    private String partnerCode;

    @Value("${momo.access.key:F8BBA842ECF85}")
    private String accessKey;

    @Value("${momo.secret.key:K951B6PE1waDMi640xX08PD3vg6EkVlz}")
    private String secretKey;

    @Value("${momo.query.endpoint:https://test-payment.momo.vn/v2/gateway/api/query}")
    private String queryEndpoint;

    public MoMoController(MoMoTransactionRepository moMoTransactionRepository, RestTemplate restTemplate) {
        this.moMoTransactionRepository = moMoTransactionRepository;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/check-status/{orderId}")
    public ResponseEntity<Map<String, Object>> checkTransactionStatus(@PathVariable String orderId) {
        try {
            // Tìm giao dịch trong database
            Optional<MoMoTransaction> transactionOpt = moMoTransactionRepository.findByOrderId(orderId);
            if (!transactionOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Không tìm thấy giao dịch với orderId: " + orderId));
            }
            MoMoTransaction transaction = transactionOpt.get();

            // Tạo request body cho API queryTransaction của MoMo
            String requestId = UUID.randomUUID().toString();
            String rawSignature = "accessKey=" + accessKey +
                    "&orderId=" + orderId +
                    "&partnerCode=" + partnerCode +
                    "&requestId=" + requestId;

            String signature = hmacSHA256(rawSignature, secretKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("partnerCode", partnerCode);
            requestBody.put("requestId", requestId);
            requestBody.put("orderId", orderId);
            requestBody.put("signature", signature);
            requestBody.put("lang", "vi");

            // Gửi request tới MoMo
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            System.out.println("Sending query request to MoMo: " + queryEndpoint);
            System.out.println("Request body: " + objectMapper.writeValueAsString(requestBody));

            ResponseEntity<String> response = restTemplate.postForEntity(queryEndpoint, entity, String.class);
            System.out.println("MoMo query response: " + response.getBody());

            // Parse response
            Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);
            String resultCode = String.valueOf(responseBody.get("resultCode"));
            String transId = (String) responseBody.get("transId");
            String message = (String) responseBody.get("message");

            // Cập nhật trạng thái giao dịch trong database
            if ("0".equals(resultCode)) {
                transaction.setTransactionStatus("SUCCESS");
                transaction.setTransId(transId);
                transaction.setMessage(message);
                transaction.setResponseCode(resultCode);
                transaction.setNgayCapNhat(LocalDateTime.now());
                moMoTransactionRepository.save(transaction);
            } else if (responseBody.containsKey("resultCode") && !"0".equals(resultCode)) {
                transaction.setTransactionStatus("FAILED");
                transaction.setTransId(transId);
                transaction.setMessage(message);
                transaction.setResponseCode(resultCode);
                transaction.setNgayCapNhat(LocalDateTime.now());
                moMoTransactionRepository.save(transaction);
            }

            // Trả về phản hồi cho frontend
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("orderId", orderId);
            responseMap.put("transactionStatus", transaction.getTransactionStatus());
            responseMap.put("transId", transaction.getTransId());
            responseMap.put("amount", transaction.getAmount());
            responseMap.put("message", transaction.getMessage());
            return ResponseEntity.ok(responseMap);

        } catch (Exception e) {
            System.err.println("Error checking MoMo transaction status: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi kiểm tra trạng thái giao dịch: " + e.getMessage()));
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
}