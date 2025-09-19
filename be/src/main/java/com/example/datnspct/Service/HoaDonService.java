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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
    public HoaDonDTO capNhatHoaDon(Integer id, HoaDonDTO dto) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại: " + id));

        // Cập nhật thông tin hóa đơn
        hoaDon.setMaHD(dto.getMaHD());
        hoaDon.setIdNhanVien(dto.getIdNhanVien());
        hoaDon.setLoaiHoaDon(dto.getLoaiHoaDon());
        hoaDon.setNgayTao(dto.getNgayTao());
        hoaDon.setNgaySua(dto.getNgaySua() != null ? dto.getNgaySua() : LocalDateTime.now());
        hoaDon.setTongTien(dto.getTongTien());
        hoaDon.setTrangThai(dto.getTrangThai());

        // Xử lý idKhachHang
        if (dto.getIdKhachHang() != null) {
            KhachHang khachHang = khachHangRepository.findById(dto.getIdKhachHang())
                    .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại: " + dto.getIdKhachHang()));
            hoaDon.setKhachHang(khachHang);
        } else {
            hoaDon.setKhachHang(null); // Khách lẻ
        }

        // Xử lý idPT
        if (dto.getIdPT() != null) {
            PhuongTT ptt = phuongThanhToanRepository.findById(dto.getIdPT())
                    .orElseThrow(() -> new RuntimeException("Phương thức thanh toán không tồn tại: " + dto.getIdPT()));
            hoaDon.setPhuongThucThanhToan(ptt);
        } else {
            hoaDon.setPhuongThucThanhToan(null);
        }

        // Xử lý idKM
        if (dto.getIdKM() != null) {
            KhuyenMai km = khuyenMaiRepository.findById(dto.getIdKM())
                    .orElseThrow(() -> new RuntimeException("Khuyến mãi không tồn tại: " + dto.getIdKM()));
            hoaDon.setKhuyenMai(km);
        } else {
            hoaDon.setKhuyenMai(null);
        }

        // Xử lý chi tiết hóa đơn
        List<HoaDonChiTiet> chiTietSanPham = new ArrayList<>();
        List<Integer> newIds = new ArrayList<>();

        // Chỉ xử lý chi tiết hóa đơn nếu danh sách không rỗng
        if (dto.getChiTietSanPham() != null && !dto.getChiTietSanPham().isEmpty()) {
            if ("HOAN_THANH".equals(dto.getTrangThai())) {
                for (HoaDonChiTietDTO ctDto : dto.getChiTietSanPham()) {
                    if (ctDto.getIdCtSanPham() == null) {
                        throw new RuntimeException("idCtSanPham không được null");
                    }
                    if (ctDto.getSoLuong() == null || ctDto.getSoLuong() <= 0) {
                        throw new RuntimeException("Số lượng sản phẩm phải lớn hơn 0");
                    }
                    if (ctDto.getDonGia() == null || ctDto.getDonGia().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new RuntimeException("Đơn giá không hợp lệ");
                    }
                    if (ctDto.getThanhTien() == null || ctDto.getThanhTien().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new RuntimeException("Thành tiền không hợp lệ");
                    }

                    HoaDonChiTiet ct;
                    if (ctDto.getIdHDCT() != null) {
                        ct = hoaDonChiTietRepository.findById(ctDto.getIdHDCT())
                                .orElseThrow(() -> new RuntimeException(
                                        "Chi tiết hóa đơn không tồn tại: " + ctDto.getIdHDCT()));
                        newIds.add(ctDto.getIdHDCT());
                    } else {
                        ct = new HoaDonChiTiet();
                        ct.setHoaDon(hoaDon);
                    }

                    SanPhamChiTiet spct = sanPhamChiTietRepository.findById(ctDto.getIdCtSanPham())
                            .orElseThrow(() -> new RuntimeException(
                                    "Sản phẩm chi tiết không tồn tại: " + ctDto.getIdCtSanPham()));

                    // Trừ tồn kho
                    int soLuongCu = ct.getIdHDCT() != null ? ct.getSoLuong() : 0;
                    int delta = ctDto.getSoLuong() - soLuongCu;
                    System.out.println("capNhatHoaDon: Số lượng trước khi trừ: " + spct.getSoLuong() + ", delta: "
                            + delta + ", idSPCT: " + ctDto.getIdCtSanPham());
                    if (delta > 0 && delta > spct.getSoLuong()) {
                        throw new RuntimeException(
                                "Số lượng tồn kho không đủ cho sản phẩm idSPCT: " + ctDto.getIdCtSanPham());
                    }
                    spct.setSoLuong(spct.getSoLuong() - delta);
                    sanPhamChiTietRepository.save(spct);
                    System.out.println("capNhatHoaDon: Số lượng sau khi trừ: " + spct.getSoLuong() + ", idSPCT: "
                            + ctDto.getIdCtSanPham());

                    ct.setSanPhamct(spct);
                    ct.setSoLuong(ctDto.getSoLuong());
                    ct.setDonGia(ctDto.getDonGia());
                    ct.setThanhTien(ctDto.getThanhTien());
                    ct.setNgayTao(ctDto.getNgayTao() != null ? ctDto.getNgayTao() : LocalDateTime.now());
                    ct.setNgaySua(LocalDateTime.now());
                    chiTietSanPham.add(ct);
                }
            } else {
                // Cập nhật chi tiết hóa đơn mà không trừ tồn kho
                for (HoaDonChiTietDTO ctDto : dto.getChiTietSanPham()) {
                    HoaDonChiTiet ct;
                    if (ctDto.getIdHDCT() != null) {
                        ct = hoaDonChiTietRepository.findById(ctDto.getIdHDCT())
                                .orElseThrow(() -> new RuntimeException(
                                        "Chi tiết hóa đơn không tồn tại: " + ctDto.getIdHDCT()));
                        newIds.add(ctDto.getIdHDCT());
                    } else {
                        ct = new HoaDonChiTiet();
                        ct.setHoaDon(hoaDon);
                    }

                    SanPhamChiTiet spct = sanPhamChiTietRepository.findById(ctDto.getIdCtSanPham())
                            .orElseThrow(() -> new RuntimeException(
                                    "Sản phẩm chi tiết không tồn tại: " + ctDto.getIdCtSanPham()));

                    ct.setSanPhamct(spct);
                    ct.setSoLuong(ctDto.getSoLuong());
                    ct.setDonGia(ctDto.getDonGia());
                    ct.setThanhTien(ctDto.getThanhTien());
                    ct.setNgayTao(ctDto.getNgayTao() != null ? ctDto.getNgayTao() : LocalDateTime.now());
                    ct.setNgaySua(LocalDateTime.now());
                    chiTietSanPham.add(ct);
                }
            }

            // Xóa các bản ghi chi tiết không còn trong danh sách mới
            hoaDon.getChiTietSanPham().removeIf(ct -> !newIds.contains(ct.getIdHDCT()));
            hoaDon.getChiTietSanPham().addAll(chiTietSanPham);
        } else {
            // Nếu chiTietSanPham rỗng, không cập nhật danh sách chi tiết
            System.out.println("capNhatHoaDon: Danh sách chi tiết hóa đơn rỗng, không cập nhật chi tiết.");
        }

        HoaDon saved = hoaDonRepository.save(hoaDon);
        return convertToDTO(saved);
    }

    public void deleteHoaDon(Integer id) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));
        hoaDonRepository.delete(hoaDon);
    }

    public HoaDonDTO createHoaDon(HoaDonDTO dto) {
        HoaDon hoaDon = convertToEntity(dto);
        hoaDon.setNgayTao(LocalDateTime.now());
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
