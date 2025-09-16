package com.example.datnspct.Controller.admin;

import com.example.datnspct.Model.MoMoTransaction;
import com.example.datnspct.Repository.MoMoTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/api/momo")
@RequiredArgsConstructor
public class MoMoStatusController {

    private final MoMoTransactionRepository moMoTransactionRepository;

    @GetMapping("/check-status/{orderId}")
    public ResponseEntity<Map<String, Object>> checkTransactionStatus(@PathVariable String orderId) {
        try {
            MoMoTransaction transaction = moMoTransactionRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch với orderId: " + orderId));

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", transaction.getOrderId());
            response.put("transactionStatus", transaction.getTransactionStatus());
            response.put("responseCode", transaction.getResponseCode());
            response.put("message", transaction.getMessage());
            response.put("transId", transaction.getTransId());
            response.put("amount", transaction.getAmount());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi khi kiểm tra trạng thái: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}