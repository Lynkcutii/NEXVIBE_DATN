package com.example.datnspct.Controller.admin;

import com.example.datnspct.Service.MoMoPaymentService;
import com.example.datnspct.dto.MoMoPaymentRequest;
import com.example.datnspct.dto.MoMoPaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

import java.util.Map;

@RestController
@RequestMapping("/admin/api/momo")
@RequiredArgsConstructor
public class MoMoPaymentController {

    private final MoMoPaymentService moMoPaymentService;

    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(@RequestBody MoMoPaymentRequest request) {
        try {
            System.out.println("=== RECEIVED MOMO REQUEST ===");
            System.out.println("OrderId: " + request.getOrderId());
            System.out.println("Amount: " + request.getAmount());
            
            // Gọi MoMo API thật
            MoMoPaymentResponse response = moMoPaymentService.createPayment(request);
            
            System.out.println("=== RETURNING RESPONSE ===");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("=== ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/notify")
    public ResponseEntity<String> handleNotification(@RequestBody Map<String, String> params) {
        try {
            // Verify signature
            boolean isValid = moMoPaymentService.verifySignature(params);
            if (!isValid) {
                return ResponseEntity.badRequest().body("Invalid signature");
            }

            String orderId = params.get("orderId");
            String transId = params.get("transId");
            String responseCode = params.get("resultCode");
            String message = params.get("message");

            // Update transaction status
            moMoPaymentService.updateTransactionStatus(orderId, transId, responseCode, message);

            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/return")
    public ResponseEntity<String> handleReturn(@RequestParam Map<String, String> params) {
        try {
            String orderId = params.get("orderId");
            String responseCode = params.get("resultCode");
            String message = params.get("message");

            if ("0".equals(responseCode)) {
                return ResponseEntity.ok("Thanh toán thành công! Mã đơn hàng: " + orderId);
            } else {
                return ResponseEntity.ok("Thanh toán thất bại: " + message);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi xử lý: " + e.getMessage());
        }
    }

    @PostMapping("/save-transaction")
    public ResponseEntity<?> saveTransaction(@RequestBody Map<String, Object> transactionData) {
        try {
            System.out.println("=== SAVE MOMO TRANSACTION ===");
            System.out.println("Data: " + transactionData);
            
            moMoPaymentService.saveTransactionFromPOS(transactionData);
            
            return ResponseEntity.ok(Map.of("message", "Lưu thông tin giao dịch thành công"));
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu giao dịch MoMo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Lỗi: " + e.getMessage()));
        }
    }
}
