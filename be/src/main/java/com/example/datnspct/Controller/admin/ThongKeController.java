package com.example.datnspct.Controller.admin;

import com.example.datnspct.Service.ThongKeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/admin/api/thongke")
@RequiredArgsConstructor
public class ThongKeController {

    private final ThongKeService thongKeService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Chuyển đổi ngày tháng nếu có
            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;
            
            if (startDate != null && endDate != null) {
                startDateTime = LocalDate.parse(startDate).atStartOfDay();
                endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);
            }
            
            // Thống kê tổng quan với bộ lọc ngày tháng
            stats.put("tongQuanHoaDon", thongKeService.getTongQuanHoaDon(startDateTime, endDateTime));
            stats.put("tongQuanKhachHang", thongKeService.getTongQuanKhachHang(startDateTime, endDateTime));
            stats.put("tongQuanSanPham", thongKeService.getTongQuanSanPham());
            stats.put("tongQuanDoanhThu", thongKeService.getTongQuanDoanhThu(startDateTime, endDateTime));
            stats.put("doanhThuTheoThang", thongKeService.getDoanhThuTheoThang(startDateTime, endDateTime));
            
            // Top sản phẩm bán chạy
            stats.put("topSanPhamBanChay", thongKeService.getTopSanPhamBanChay(5));
            
            // Khách hàng mới
            stats.put("khachHangMoi", thongKeService.getKhachHangMoi(30));
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi khi lấy thống kê dashboard: " + e.getMessage());
            e.printStackTrace(); // In lỗi chi tiết để debug
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/san-pham")
    public ResponseEntity<?> getThongKeSanPham(
            @RequestParam(defaultValue = "30") int soNgay) {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            stats.put("sanPhamBanChay", thongKeService.getSanPhamBanChayChiTiet(soNgay));
            stats.put("tonKho", thongKeService.getThongKeTonKho());
            stats.put("theoDanhMuc", thongKeService.getThongKeTheoDanhMuc(soNgay));
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi khi lấy thống kê sản phẩm: " + e.getMessage());
            e.printStackTrace(); // In lỗi chi tiết để debug
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/khach-hang")
    public ResponseEntity<?> getThongKeKhachHang(
            @RequestParam(defaultValue = "30") int soNgay) {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            stats.put("tongQuan", thongKeService.getTongQuanKhachHang(null, null));
            stats.put("khachHangMoi", thongKeService.getKhachHangMoi(soNgay));
            stats.put("khachHangVIP", thongKeService.getKhachHangVIP());
            stats.put("theoThang", new ArrayList<>()); // Placeholder
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi khi lấy thống kê khách hàng: " + e.getMessage());
            e.printStackTrace(); // In lỗi chi tiết để debug
            return ResponseEntity.status(500).body(error);
        }
    }
}
