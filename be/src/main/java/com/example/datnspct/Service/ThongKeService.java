package com.example.datnspct.Service;

import com.example.datnspct.Repository.HoaDonRepository;
import com.example.datnspct.Repository.KhachHangRepository;
import com.example.datnspct.Repository.SanPhamChiTietRepository;
import com.example.datnspct.Repository.HoaDonChiTietRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ThongKeService {

    private final HoaDonRepository hoaDonRepository;
    private final KhachHangRepository khachHangRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;

    // Thống kê tổng quan hóa đơn
    public Map<String, Object> getTongQuanHoaDon(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> result = new HashMap<>();

        if (startDate != null && endDate != null) {
            // Thống kê theo khoảng thời gian được chọn
            long tongHoaDon = hoaDonRepository.countByNgayTaoBetween(startDate, endDate);
            result.put("tongSo", tongHoaDon);

            // Tính hóa đơn trong ngày đầu tiên của khoảng thời gian
            LocalDateTime startOfFirstDay = startDate.toLocalDate().atStartOfDay();
            LocalDateTime endOfFirstDay = startOfFirstDay.plusDays(1);
            long hoaDonNgayDau = hoaDonRepository.countByNgayTaoBetween(startOfFirstDay, endOfFirstDay);
            result.put("homNay", hoaDonNgayDau);

            // Tính hóa đơn trong tháng của ngày bắt đầu
            LocalDateTime startOfMonth = startDate.toLocalDate().withDayOfMonth(1).atStartOfDay();
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
            long hoaDonTrongThang = hoaDonRepository.countByNgayTaoBetween(startOfMonth, endOfMonth);
            result.put("trongThang", hoaDonTrongThang);
        } else {
            // Thống kê toàn bộ (mặc định)
            long tongHoaDon = hoaDonRepository.count();
            result.put("tongSo", tongHoaDon);

            // Hóa đơn hôm nay
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1);
            long hoaDonHomNay = hoaDonRepository.countByNgayTaoBetween(startOfDay, endOfDay);
            result.put("homNay", hoaDonHomNay);

            // Hóa đơn trong tháng
            LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
            long hoaDonTrongThang = hoaDonRepository.countByNgayTaoBetween(startOfMonth, endOfMonth);
            result.put("trongThang", hoaDonTrongThang);
        }

        return result;
    }

    // Thống kê tổng quan doanh thu
    public Map<String, Object> getTongQuanDoanhThu(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> result = new HashMap<>();

        if (startDate != null && endDate != null) {
            // Thống kê theo khoảng thời gian được chọn
            BigDecimal tongDoanhThu = hoaDonRepository.sumTongTienByNgayTaoBetween(startDate, endDate);
            if (tongDoanhThu == null) tongDoanhThu = BigDecimal.ZERO;
            result.put("tongTatCa", tongDoanhThu.doubleValue());

            // Tính doanh thu trong ngày đầu tiên của khoảng thời gian
            LocalDateTime startOfFirstDay = startDate.toLocalDate().atStartOfDay();
            LocalDateTime endOfFirstDay = startOfFirstDay.plusDays(1);
            BigDecimal doanhThuNgayDau = hoaDonRepository.sumTongTienByNgayTaoBetween(startOfFirstDay, endOfFirstDay);
            if (doanhThuNgayDau == null) doanhThuNgayDau = BigDecimal.ZERO;
            result.put("homNay", doanhThuNgayDau.doubleValue());

            // Tính doanh thu trong tháng của ngày bắt đầu
            LocalDateTime startOfMonth = startDate.toLocalDate().withDayOfMonth(1).atStartOfDay();
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
            BigDecimal doanhThuTrongThang = hoaDonRepository.sumTongTienByNgayTaoBetween(startOfMonth, endOfMonth);
            if (doanhThuTrongThang == null) doanhThuTrongThang = BigDecimal.ZERO;
            result.put("trongThang", doanhThuTrongThang.doubleValue());
        } else {
            // Thống kê toàn bộ (mặc định)
            BigDecimal tongDoanhThu = hoaDonRepository.sumTongTienByNgayTaoBetween(
                    LocalDateTime.of(2020, 1, 1, 0, 0), // Từ năm 2020
                    LocalDateTime.now()
            );
            if (tongDoanhThu == null) tongDoanhThu = BigDecimal.ZERO;
            result.put("tongTatCa", tongDoanhThu.doubleValue());

            // Doanh thu hôm nay
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1);
            BigDecimal doanhThuHomNay = hoaDonRepository.sumTongTienByNgayTaoBetween(startOfDay, endOfDay);
            if (doanhThuHomNay == null) doanhThuHomNay = BigDecimal.ZERO;
            result.put("homNay", doanhThuHomNay.doubleValue());

            // Doanh thu trong tháng
            LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
            BigDecimal doanhThuTrongThang = hoaDonRepository.sumTongTienByNgayTaoBetween(startOfMonth, endOfMonth);
            if (doanhThuTrongThang == null) doanhThuTrongThang = BigDecimal.ZERO;
            result.put("trongThang", doanhThuTrongThang.doubleValue());
        }

        return result;
    }

    // Thống kê tổng quan khách hàng
    public Map<String, Object> getTongQuanKhachHang(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> result = new HashMap<>();

        if (startDate != null && endDate != null) {
            // Thống kê khách hàng theo khoảng thời gian được chọn
            // (Chỉ đếm khách hàng có hóa đơn trong khoảng thời gian)
            long khachHangTrongKhoang = khachHangRepository.countKhachHangCoHoaDonTrongKhoang(startDate, endDate);
            result.put("tongSo", khachHangTrongKhoang);
            result.put("hoatDong", khachHangTrongKhoang);
            result.put("khongHoatDong", 0);
        } else {
            // Thống kê toàn bộ (mặc định)
            long tongKhachHang = khachHangRepository.count();
            result.put("tongSo", tongKhachHang);

            // Khách hàng hoạt động (có ít nhất 1 đơn hàng)
            long khachHangHoatDong = khachHangRepository.countKhachHangCoHoaDon();
            result.put("hoatDong", khachHangHoatDong);
            result.put("khongHoatDong", tongKhachHang - khachHangHoatDong);
        }

        return result;
    }

    // Thống kê tổng quan sản phẩm
    public Map<String, Object> getTongQuanSanPham() {
        Map<String, Object> result = new HashMap<>();

        long tongSanPham = sanPhamChiTietRepository.count();
        result.put("tongSo", tongSanPham);

        // Đếm số sản phẩm theo trạng thái tồn kho
        long conHang = sanPhamChiTietRepository.countBySoLuongGreaterThan(0);
        long hetHang = sanPhamChiTietRepository.countBySoLuong(0);
        long sapHetHang = sanPhamChiTietRepository.countBySoLuongBetween(1, 10);

        result.put("conHang", conHang);
        result.put("hetHang", hetHang);
        result.put("sapHetHang", sapHetHang);

        return result;
    }

    // Thống kê doanh thu theo tháng (12 tháng gần nhất)
    public List<Map<String, Object>> getDoanhThuTheoThang(LocalDateTime startDate, LocalDateTime endDate) {
        List<Map<String, Object>> result = new ArrayList<>();

        if (startDate != null && endDate != null) {
            // Thống kê theo khoảng thời gian được chọn
            LocalDate start = startDate.toLocalDate();
            LocalDate end = endDate.toLocalDate();

            // Tạo danh sách các tháng trong khoảng thời gian
            LocalDate current = start.withDayOfMonth(1);
            while (!current.isAfter(end)) {
                LocalDateTime startOfMonth = current.atStartOfDay();
                LocalDateTime endOfMonth = current.plusMonths(1).atStartOfDay();

                // Tính tổng doanh thu trong tháng
                BigDecimal doanhThu = hoaDonRepository.sumTongTienByNgayTaoBetween(startOfMonth, endOfMonth);
                if (doanhThu == null) {
                    doanhThu = BigDecimal.ZERO;
                }

                Map<String, Object> thangData = new HashMap<>();
                thangData.put("thang", current.getMonthValue());
                thangData.put("nam", current.getYear());
                thangData.put("doanhThu", doanhThu.doubleValue());
                result.add(thangData);

                current = current.plusMonths(1);
            }
        } else {
            // Thống kê 12 tháng gần nhất (mặc định)
            for (int i = 11; i >= 0; i--) {
                LocalDate thang = LocalDate.now().minusMonths(i);
                LocalDateTime startOfMonth = thang.withDayOfMonth(1).atStartOfDay();
                LocalDateTime endOfMonth = startOfMonth.plusMonths(1);

                // Tính tổng doanh thu trong tháng
                BigDecimal doanhThu = hoaDonRepository.sumTongTienByNgayTaoBetween(startOfMonth, endOfMonth);
                if (doanhThu == null) {
                    doanhThu = BigDecimal.ZERO;
                }

                Map<String, Object> thangData = new HashMap<>();
                thangData.put("thang", thang.getMonthValue());
                thangData.put("nam", thang.getYear());
                thangData.put("doanhThu", doanhThu.doubleValue());
                result.add(thangData);
            }
        }

        return result;
    }

    // Top sản phẩm bán chạy
    public List<Map<String, Object>> getTopSanPhamBanChay(int limit) {
        List<Map<String, Object>> result = new ArrayList<>();

        // Lấy top sản phẩm bán chạy từ HoaDonChiTiet
        List<Object[]> topProducts = hoaDonChiTietRepository.findTopSanPhamBanChay(limit);

        for (Object[] row : topProducts) {
            Map<String, Object> item = new HashMap<>();
            item.put("tenSanPham", row[0]); // Tên sản phẩm
            item.put("soLuongBan", ((Number) row[1]).intValue()); // Số lượng bán
            item.put("doanhThu", ((BigDecimal) row[2]).doubleValue()); // Doanh thu
            result.add(item);
        }

        return result;
    }

    // Thống kê sản phẩm bán chạy chi tiết (theo số ngày)
    public List<Map<String, Object>> getSanPhamBanChayChiTiet(int soNgay) {
        List<Map<String, Object>> result = new ArrayList<>();

        LocalDateTime fromDate = LocalDateTime.now().minusDays(soNgay);
        List<Object[]> products = hoaDonChiTietRepository.findSanPhamBanChayTrongKhoang(fromDate, LocalDateTime.now());

        for (Object[] row : products) {
            Map<String, Object> item = new HashMap<>();
            item.put("tenSanPham", row[0]);
            item.put("soLuongBan", ((Number) row[1]).intValue());
            item.put("doanhThu", ((BigDecimal) row[2]).doubleValue());
            result.add(item);
        }

        return result;
    }

    // Thống kê tồn kho
    public List<Map<String, Object>> getThongKeTonKho() {
        List<Map<String, Object>> result = new ArrayList<>();

        sanPhamChiTietRepository.findAll().forEach(sp -> {
            Map<String, Object> item = new HashMap<>();
            String tenSanPham = sp.getSanPham() != null ? sp.getSanPham().getTenSP() : "Không xác định";
            item.put("tenSanPham", tenSanPham);
            item.put("maSanPham", sp.getMaSPCT());
            item.put("soLuongTon", sp.getSoLuong());
            item.put("giaTriTonKho", sp.getGia().multiply(BigDecimal.valueOf(sp.getSoLuong())));

            String trangThaiTonKho;
            if (sp.getSoLuong() == 0) {
                trangThaiTonKho = "Hết hàng";
            } else if (sp.getSoLuong() <= 10) {
                trangThaiTonKho = "Sắp hết";
            } else {
                trangThaiTonKho = "Còn hàng";
            }
            item.put("trangThai", trangThaiTonKho);

            result.add(item);
        });

        return result;
    }

    // Thống kê theo danh mục
//    public List<Map<String, Object>> getThongKeTheoDanhMuc(int soNgay) {
//        List<Map<String, Object>> result = new ArrayList<>();
//
//        LocalDateTime fromDate = LocalDateTime.now().minusDays(soNgay);
//        List<Object[]> categories = hoaDonChiTietRepository.findThongKeTheoDanhMuc(fromDate, LocalDateTime.now());
//
//        for (Object[] row : categories) {
//            Map<String, Object> item = new HashMap<>();
//            item.put("tenDanhMuc", row[0]);
//            item.put("soLuong", ((Number) row[1]).intValue());
//            item.put("doanhThu", ((BigDecimal) row[2]).doubleValue());
//            result.add(item);
//        }
//
//        return result;
//    }

    // Khách hàng mới (lấy theo ID mới nhất)
    public List<Map<String, Object>> getKhachHangMoi(int soNgay) {
        List<Map<String, Object>> result = new ArrayList<>();

        LocalDateTime fromDate = LocalDateTime.now().minusDays(soNgay);
        List<Object[]> customers = khachHangRepository.findKhachHangMoi(fromDate, LocalDateTime.now());

        // Chỉ lấy 10 khách hàng mới nhất
        int limit = Math.min(10, customers.size());
        for (int i = 0; i < limit; i++) {
            Object[] row = customers.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("id", row[0]);
            item.put("hoTen", row[1]);
            item.put("email", row[2]);
            item.put("ngayTao", "Mới"); // Placeholder vì không có ngày tạo thực
            result.add(item);
        }

        return result;
    }

    // Khách hàng VIP (chi tiêu nhiều)
    public List<Map<String, Object>> getKhachHangVIP() {
        List<Map<String, Object>> result = new ArrayList<>();

        List<Object[]> vipCustomers = khachHangRepository.findKhachHangVIP();

        for (Object[] row : vipCustomers) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", row[0]);
            item.put("hoTen", row[1]);
            item.put("soDonHang", ((Number) row[2]).intValue());
            item.put("tongChiTieu", ((BigDecimal) row[3]).doubleValue());
            result.add(item);
        }

        return result;
    }
}