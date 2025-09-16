package com.example.datnspct.Service;

import com.example.datnspct.Model.*;
import com.example.datnspct.Repository.*;
import com.example.datnspct.dto.HoaDonChiTietDTO;
import com.example.datnspct.dto.HoaDonDTO;
import com.example.datnspct.dto.OrderDetailResponseDTO;
import com.example.datnspct.dto.OrderRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class HoaDonService {

    // Autowired all necessary repositories
    @Autowired
    private HoaDonRepository hoaDonRepository;
    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;
    @Autowired
    private KhachHangRepository khachHangRepository;
    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;
    @Autowired
    private PhuongThucThanhToanRepository phuongThanhToanRepository;
    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;
    @Autowired
    private GioHangCTRepository gioHangCTRepository;
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private DiaChiKhachHangRepository diaChiKhachHangRepository;

    /**
     * Tạo một hóa đơn mới từ yêu cầu của khách hàng (checkout).
     * Đây là một giao dịch (transaction), nếu có lỗi xảy ra, mọi thay đổi sẽ được
     * hoàn tác.
     */
    @Transactional
    public HoaDon createHoaDon(OrderRequestDTO request) {
        List<String> validationErrors = new ArrayList<>();

        // 1. Xác thực thông tin cơ bản
        KhachHang khachHang = khachHangRepository.findByTaiKhoanId(request.getIdTK())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Không tìm thấy khách hàng với tài khoản ID: " + request.getIdTK()));

        PhuongTT phuongTT = phuongThanhToanRepository.findByTen(request.getPaymentMethod())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Phương thức thanh toán không hợp lệ: " + request.getPaymentMethod()));

        DiaChiKhachHang diaChiGiaoHang = diaChiKhachHangRepository.findById(request.getShippingInfo().getAddressId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Địa chỉ giao hàng không hợp lệ: " + request.getShippingInfo().getAddressId()));

        // Kiểm tra xem địa chỉ có thuộc về khách hàng không
        if (!diaChiGiaoHang.getKhachHang().getIdKH().equals(khachHang.getIdKH())) {
            // Sửa lại cú pháp throw exception
            throw new IllegalArgumentException("Địa chỉ giao hàng không thuộc về khách hàng này.");
        }

        // 2. Kiểm tra sản phẩm và tính toán lại tổng tiền để xác thực
        BigDecimal serverCalculatedTotal = BigDecimal.ZERO;
        for (OrderRequestDTO.OrderItemDTO item : request.getItems()) {
            SanPhamChiTiet spct = sanPhamChiTietRepository.findById(item.getIdSPCT())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Không tìm thấy sản phẩm với ID: " + item.getIdSPCT()));

            if (spct.getSoLuong() < item.getSoLuong()) {
                validationErrors.add("Sản phẩm '" + spct.getSanPham().getTenSP() + "' không đủ số lượng tồn kho.");
            }
            // An toàn hơn khi tính tổng tiền dựa trên giá từ DB
            serverCalculatedTotal = serverCalculatedTotal
                    .add(spct.getGia().multiply(BigDecimal.valueOf(item.getSoLuong())));
        }

        // So sánh tổng tiền client gửi lên và server tính toán (cho phép sai số nhỏ do
        // làm tròn)
        if (serverCalculatedTotal.subtract(request.getTotal()).abs().compareTo(new BigDecimal("1")) > 0) {
            validationErrors.add("Tổng tiền không khớp. Vui lòng thử lại.");
        }

        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", validationErrors));
        }

        // 3. Tạo hóa đơn
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHD("HD" + System.currentTimeMillis());
        hoaDon.setKhachHang(khachHang);
        hoaDon.setDiaChiGiao(diaChiGiaoHang);
        hoaDon.setPhuongThucThanhToan(phuongTT);
        hoaDon.setTongTien(request.getTotal());
        hoaDon.setNgayTao(LocalDateTime.now());
        hoaDon.setNgaySua(LocalDateTime.now());
        hoaDon.setTrangThai("CHO_XAC_NHAN");
        hoaDon.setLoaiHoaDon("Trực tuyến");

        HoaDon savedHoaDon = hoaDonRepository.save(hoaDon);

        // 4. Tạo chi tiết hóa đơn, cập nhật tồn kho
        for (OrderRequestDTO.OrderItemDTO item : request.getItems()) {
            SanPhamChiTiet spct = sanPhamChiTietRepository.findById(item.getIdSPCT()).get();

            spct.setSoLuong(spct.getSoLuong() - item.getSoLuong());
            sanPhamChiTietRepository.save(spct);

            HoaDonChiTiet chiTiet = new HoaDonChiTiet();
            chiTiet.setHoaDon(savedHoaDon);
            chiTiet.setSanPhamct(spct);
            chiTiet.setSoLuong(item.getSoLuong());
            chiTiet.setDonGia(spct.getGia());
            chiTiet.setThanhTien(spct.getGia().multiply(BigDecimal.valueOf(item.getSoLuong())));
            hoaDonChiTietRepository.save(chiTiet);
        }

        // 5. Xóa các sản phẩm đã đặt khỏi giỏ hàng
        deleteCartItemsAfterOrder(request.getItems());

        return savedHoaDon;
    }

    /**
     * Lấy danh sách hóa đơn của một khách hàng.
     */
    public List<HoaDonDTO> findByKhachHangId(Integer idKH) {
        List<HoaDon> hoaDons = hoaDonRepository.findByKhachHangIdOrderByNgayTaoDesc(idKH);
        return hoaDons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy thông tin chi tiết của một hóa đơn, bao gồm cả danh sách sản phẩm.
     */
    public OrderDetailResponseDTO getHoaDonDetail(Integer idHD) {
        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hóa đơn với ID: " + idHD));

        List<HoaDonChiTiet> chiTietList = hoaDonChiTietRepository.findByHoaDon_IdHD(idHD);

        OrderDetailResponseDTO response = new OrderDetailResponseDTO();
        response.setHoaDon(convertToDTO(hoaDon));
        response.setChiTiet(chiTietList.stream().map(this::convertChiTietToDto).collect(Collectors.toList()));

        return response;
    }

    /**
     * Hủy một đơn hàng (chỉ khi ở trạng thái chờ xác nhận).
     */
    @Transactional
    public void cancelOrder(Integer idHD) {
        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hóa đơn với ID: " + idHD));

        if (!"CHO_XAC_NHAN".equalsIgnoreCase(hoaDon.getTrangThai())) {
            throw new IllegalArgumentException("Không thể hủy đơn hàng ở trạng thái: " + hoaDon.getTrangThai());
        }

        hoaDon.setTrangThai("DA_HUY");
        hoaDon.setNgaySua(LocalDateTime.now());
        hoaDonRepository.save(hoaDon);

        // Hoàn trả lại số lượng sản phẩm vào kho
        List<HoaDonChiTiet> chiTietList = hoaDonChiTietRepository.findByHoaDon_IdHD(idHD);
        for (HoaDonChiTiet chiTiet : chiTietList) {
            SanPhamChiTiet spct = chiTiet.getSanPhamct();
            if (spct != null) {
                spct.setSoLuong(spct.getSoLuong() + chiTiet.getSoLuong());
                sanPhamChiTietRepository.save(spct);
            }
        }
    }

    /**
     * Thay đổi địa chỉ giao hàng của một đơn hàng.
     */
    @Transactional
    public void changeOrderAddress(Integer idHD, Integer newAddressId) {
        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hóa đơn với ID: " + idHD));

        if (!"CHO_XAC_NHAN".equalsIgnoreCase(hoaDon.getTrangThai())) {
            throw new IllegalArgumentException(
                    "Không thể thay đổi địa chỉ cho đơn hàng ở trạng thái: " + hoaDon.getTrangThai());
        }

        DiaChiKhachHang newAddress = diaChiKhachHangRepository.findById(newAddressId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy địa chỉ với ID: " + newAddressId));

        if (!newAddress.getKhachHang().getIdKH().equals(hoaDon.getKhachHang().getIdKH())) {
            throw new IllegalArgumentException("Địa chỉ mới không thuộc về khách hàng của đơn hàng này.");
        }

        hoaDon.setDiaChiGiao(newAddress);
        hoaDon.setNgaySua(LocalDateTime.now());
        hoaDonRepository.save(hoaDon);
    }

    /**
     * Cập nhật trạng thái của một đơn hàng (dành cho admin).
     */
    @Transactional
    public void updateOrderStatus(Integer idHD, String newStatus, String ghiChu) {
        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hóa đơn với ID: " + idHD));
        hoaDon.setTrangThai(newStatus);
        hoaDon.setNgaySua(LocalDateTime.now());
        // Logic để lưu ghiChu có thể được thêm vào đây
        hoaDonRepository.save(hoaDon);
    }

    // ========== CÁC PHƯƠNG THỨC HELPER VÀ PHỤ TRỢ ==========

    private void deleteCartItemsAfterOrder(List<OrderRequestDTO.OrderItemDTO> items) {
        if (items == null || items.isEmpty())
            return;
        List<Integer> cartItemIdsToDelete = items.stream()
                .map(OrderRequestDTO.OrderItemDTO::getIdGHCT)
                .collect(Collectors.toList());
        gioHangCTRepository.deleteAllById(cartItemIdsToDelete);
    }

    private HoaDonDTO convertToDTO(HoaDon hoaDon) {
        if (hoaDon == null)
            return null;
        HoaDonDTO dto = new HoaDonDTO();
        dto.setIdHD(hoaDon.getIdHD());
        dto.setMaHD(hoaDon.getMaHD());
        if (hoaDon.getKhachHang() != null) {
            dto.setIdKhachHang(hoaDon.getKhachHang().getIdKH());
            dto.setCustomerName(hoaDon.getKhachHang().getTenKH());
        } else {
            dto.setCustomerName("Khách lẻ");
        }
        if (hoaDon.getKhuyenMai() != null) {
            dto.setIdKM(hoaDon.getKhuyenMai().getIdKM());
        }
        dto.setIdNhanVien(hoaDon.getIdNhanVien());
        dto.setNgayTao(hoaDon.getNgayTao());
        dto.setNgaySua(hoaDon.getNgaySua());
        dto.setTongTien(hoaDon.getTongTien());
        dto.setTrangThai(hoaDon.getTrangThai());
        dto.setLoaiHoaDon(hoaDon.getLoaiHoaDon());
        dto.setIdPT(hoaDon.getPhuongThucThanhToan() != null ? hoaDon.getPhuongThucThanhToan().getIdPTT() : null);
        return dto;
    }

    private HoaDonChiTietDTO convertChiTietToDto(HoaDonChiTiet chiTiet) {
        if (chiTiet == null)
            return null;
        HoaDonChiTietDTO dto = new HoaDonChiTietDTO();
        dto.setIdHDCT(chiTiet.getIdHDCT());
        dto.setSoLuong(chiTiet.getSoLuong());
        dto.setDonGia(chiTiet.getDonGia());

        SanPhamChiTiet spct = chiTiet.getSanPhamct();
        if (spct != null) {
            dto.setIdCtSanPham(spct.getId());
            if (spct.getSanPham() != null) {
                dto.setTenSanPham(spct.getSanPham().getTenSP());
                dto.setHinhAnh(
                        spct.getSanPham().getImg() != null ? spct.getSanPham().getImg() : "https://placehold.co/100");
            }
            if (spct.getMauSac() != null)
                dto.setTenMauSac(spct.getMauSac().getTenMauSac());
            if (spct.getSize() != null)
                dto.setTenSize(spct.getSize().getTenSize());
        }
        return dto;
    }

}
