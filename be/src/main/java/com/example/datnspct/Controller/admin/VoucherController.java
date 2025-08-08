package com.example.datnspct.Controller.admin;

import com.example.datnspct.Service.VoucherService;
import com.example.datnspct.dto.VoucherDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            if (idSPCTs == null || idSPCTs.isEmpty()) {
                return ResponseEntity.badRequest().body("Danh sách idSPCT không được để trống");
            }
            List<VoucherDTO> vouchers = voucherService.getApplicableVouchersForProducts(idSPCTs);
            if (vouchers.isEmpty()) {
                return ResponseEntity.ok().body("Không tìm thấy voucher nào phù hợp với các sản phẩm đã chọn");
            }
            return ResponseEntity.ok(vouchers);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi lấy danh sách voucher: " + e.getMessage());
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
     * Cập nhật số lượng voucher sau khi sử dụng
     * @param id ID của voucher
     * @param soLuong Số lượng mới
     * @return Thông báo trạng thái
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateVoucherQuantity(@PathVariable Integer id, @RequestBody Integer soLuong) {
        try {
            if (soLuong < 0) {
                return ResponseEntity.badRequest().body("Số lượng voucher không thể âm");
            }
            voucherService.updateVoucherQuantity(id, soLuong);
            return ResponseEntity.ok("Cập nhật số lượng voucher thành công");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi cập nhật số lượng voucher: " + e.getMessage());
        }
    }
}