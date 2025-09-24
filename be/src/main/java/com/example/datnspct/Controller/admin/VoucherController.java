package com.example.datnspct.Controller.admin;

import com.example.datnspct.Service.VoucherService;
import com.example.datnspct.dto.VoucherDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/voucher")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    /**
     * Lấy danh sách voucher áp dụng được cho danh sách sản phẩm dựa trên idSPCT
     * @param idSPCTs Danh sách ID của sản phẩm chi tiết (SanPhamChiTiet)
     * @return Danh sách VoucherDTO hợp lệ
     */
    @PostMapping("/applicable")
    public ResponseEntity<?> getApplicableVouchers(@RequestBody List<Integer> idSPCTs) {
        try {
            List<VoucherDTO> vouchers = voucherService.getApplicableVouchersForProducts(idSPCTs);
            return ResponseEntity.ok(vouchers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Không thể lấy danh sách voucher: " + e.getMessage()));
        }
    }

    /**
     * Lấy tất cả voucher hiện có (dùng cho quản lý hoặc hiển thị toàn bộ voucher)
     * @return Danh sách tất cả VoucherDTO
     */
    @GetMapping
    public ResponseEntity<?> getAllVouchers() {
        try {
            List<VoucherDTO> vouchers = voucherService.getAllVouchers();
            if (vouchers.isEmpty()) {
                return ResponseEntity.ok().body("Hiện không có voucher nào trong hệ thống");
            }
            return ResponseEntity.ok(vouchers);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi lấy danh sách tất cả voucher: " + e.getMessage());
        }
    }

    /**
     * Lấy thông tin voucher theo ID
     * @param id ID của voucher
     * @return VoucherDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getVoucherById(@PathVariable Integer id) {
        try {
            VoucherDTO voucher = voucherService.getVoucherById(id);
            return ResponseEntity.ok(voucher);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Không thể lấy thông tin voucher: " + e.getMessage()));
        }
    }

    /**
     * Cập nhật số lượng voucher sau khi sử dụng
     * @param id ID của voucher
     * @param body Body chứa số lượng mới { "soLuong": <giá trị> }
     * @return Thông báo trạng thái
     */
    @PutMapping("/{id}/quantity")
    public ResponseEntity<?> updateVoucherQuantity(@PathVariable Integer id, @RequestBody Map<String, Integer> body) {
        try {
            Integer soLuong = body.get("soLuong");
            if (soLuong == null) {
                return ResponseEntity.badRequest().body("Thiếu trường 'soLuong' trong body");
            }
            if (soLuong < 0) {
                return ResponseEntity.badRequest().body("Số lượng voucher không thể âm");
            }
            voucherService.updateVoucherQuantity(id, soLuong);
            return ResponseEntity.ok("Cập nhật số lượng voucher thành công");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Không thể cập nhật số lượng voucher: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createVoucher(@RequestBody VoucherDTO dto) {
        try {
            VoucherDTO createdVoucher = voucherService.createVoucher(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVoucher);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Không thể tạo voucher: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVoucher(@PathVariable Integer id, @RequestBody VoucherDTO dto) {
        try {
            VoucherDTO updatedVoucher = voucherService.updateVoucher(id, dto);
            return ResponseEntity.ok(updatedVoucher);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Không thể cập nhật voucher: " + e.getMessage()));
        }
    }

    @GetMapping("/generate-code")
    public ResponseEntity<?> generateVoucherCode() {
        try {
            String code = voucherService.generateVoucherCode();
            return ResponseEntity.ok(Collections.singletonMap("maVoucher", code));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Không thể sinh mã voucher: " + e.getMessage()));
        }
    }
}