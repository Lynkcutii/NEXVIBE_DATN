package com.example.datnspct.Controller.admin;

import com.example.datnspct.Service.HoaDonChiTietService;
import com.example.datnspct.Service.HoaDonService;
import com.example.datnspct.dto.HoaDonChiTietDTO;
import com.example.datnspct.dto.HoaDonDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/admin/api/hoadon")
public class HoaDonController {

    @Autowired
    private HoaDonService hoaDonService;
    @Autowired
    private HoaDonChiTietService hoaDonCTService;

    // Create
    @PostMapping
    public ResponseEntity<HoaDonDTO> createHoaDon(@RequestBody HoaDonDTO hoaDonDTO) {
        HoaDonDTO createdHoaDon = hoaDonService.createHoaDon(hoaDonDTO);
        return ResponseEntity.ok(createdHoaDon);
    }

    // Read (Get by ID)
    @GetMapping("/{id}")
    public ResponseEntity<HoaDonDTO> getHoaDonById(@PathVariable Integer id) {
        HoaDonDTO hoaDonDTO = hoaDonService.getHoaDonById(id);
        return ResponseEntity.ok(hoaDonDTO);
    }

    // Read (Get all)
    @GetMapping
    public ResponseEntity<List<HoaDonDTO>> getAllHoaDon() {
        List<HoaDonDTO> hoaDonDTOs = hoaDonService.getAllHoaDon();
        return ResponseEntity.ok(hoaDonDTOs);
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<?> capNhatHoaDon(@PathVariable Integer id, @RequestBody HoaDonDTO hoaDonDTO) {
        try {
            // Kiểm tra ID hóa đơn
            if (hoaDonDTO.getIdHD() == null || !hoaDonDTO.getIdHD().equals(id)) {
                return ResponseEntity.badRequest().body(new ErrorResponse("ID hóa đơn không khớp"));
            }
            // Kiểm tra chi tiết hóa đơn
            if (hoaDonDTO.getChiTietSanPham() == null || hoaDonDTO.getChiTietSanPham().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Danh sách chi tiết hóa đơn không được rỗng"));
            }
            for (HoaDonChiTietDTO ct : hoaDonDTO.getChiTietSanPham()) {
                if (ct.getIdCtSanPham() == null) {
                    return ResponseEntity.badRequest().body(new ErrorResponse("idCtSanPham không được null"));
                }
                if (ct.getSoLuong() == null || ct.getSoLuong() <= 0) {
                    return ResponseEntity.badRequest().body(new ErrorResponse("Số lượng sản phẩm phải lớn hơn 0"));
                }
                if (ct.getDonGia() == null || ct.getDonGia().compareTo(BigDecimal.ZERO) <= 0) {
                    return ResponseEntity.badRequest().body(new ErrorResponse("Đơn giá không hợp lệ"));
                }
                if (ct.getThanhTien() == null || ct.getThanhTien().compareTo(BigDecimal.ZERO) <= 0) {
                    return ResponseEntity.badRequest().body(new ErrorResponse("Thành tiền không hợp lệ"));
                }
            }

            HoaDonDTO updated = hoaDonService.capNhatHoaDon(id, hoaDonDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Lỗi xác thực: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Lỗi không mong muốn: " + e.getMessage()));
        }
    }

    class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHoaDon(@PathVariable Integer id) {
        hoaDonService.deleteHoaDon(id);
        return ResponseEntity.noContent().build();
    }

    // API filter/phân trang/tìm kiếm hóa đơn
    @GetMapping("/filter")
    public ResponseEntity<Page<HoaDonDTO>> filterHoaDon(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo
    ) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<HoaDonDTO> result = hoaDonService.filterHoaDon(keyword, status, type, dateFrom, dateTo, pageable);
        return ResponseEntity.ok(result);
    }

    //update status
    @PutMapping("/updateStatus")
    public ResponseEntity<String> updateOrderStatus(@RequestBody UpdateStatusRequest request) {
        try {
            // Validate request
            if (request.getIdHD() == null) {
                return ResponseEntity.badRequest().body("ID hóa đơn không được để trống");
            }
            if (request.getTrangThai() == null || request.getTrangThai().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Trạng thái không được để trống");
            }

            hoaDonService.updateOrderStatus(request.getIdHD(), request.getTrangThai(), request.getGhiChu());
            return ResponseEntity.ok("Cập nhật trạng thái thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    // DTO class for update status request
    public static class UpdateStatusRequest {
        private Integer idHD;
        private String trangThai;
        private String ghiChu;

        // Getters and Setters
        public Integer getIdHD() { return idHD; }
        public void setIdHD(Integer idHD) { this.idHD = idHD; }

        public String getTrangThai() { return trangThai; }
        public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

        public String getGhiChu() { return ghiChu; }
        public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    }
}