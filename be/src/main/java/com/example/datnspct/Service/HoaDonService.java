package com.example.datnspct.Service;

import com.example.datnspct.Model.HoaDon;
import com.example.datnspct.Model.HoaDonChiTiet;
import com.example.datnspct.Model.KhachHang;
import com.example.datnspct.Model.KhuyenMai;
import com.example.datnspct.Model.PhuongTT;
import com.example.datnspct.Model.SanPhamChiTiet;
import com.example.datnspct.Repository.HoaDonChiTietRepository;
import com.example.datnspct.Repository.HoaDonRepository;
import com.example.datnspct.Repository.KhachHangRepository;
import com.example.datnspct.Repository.KhuyenMaiRepository;
import com.example.datnspct.Repository.PhuongThucThanhToanRepository;
import com.example.datnspct.Repository.SanPhamChiTietRepository;
import com.example.datnspct.dto.HoaDonDTO;
import com.example.datnspct.dto.OrderRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HoaDonService {

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

    // Manual mapping: Entity to DTO
    private HoaDonDTO convertToDTO(HoaDon hoaDon) {
        HoaDonDTO dto = new HoaDonDTO();
        dto.setIdHD(hoaDon.getIdHD());
        dto.setMaHD(hoaDon.getMaHD());
        dto.setIdKhachHang(hoaDon.getIdKhachHang());
        dto.setCustomerName(hoaDon.getKhachHang() != null ? hoaDon.getKhachHang().getTenKH() : "N/A");
        dto.setIdNhanVien(hoaDon.getIdNhanVien());
        dto.setNgayTao(hoaDon.getNgayTao());
        dto.setNgaySua(hoaDon.getNgaySua());
        dto.setTongTien(hoaDon.getTongTien());
        dto.setTrangThai(hoaDon.getTrangThai());
        return dto;
    }

    // Tạo hóa đơn từ OrderRequestDTO
    @Transactional
    public HoaDon createHoaDon(OrderRequestDTO request) {
        // Kiểm tra khách hàng
        Optional<KhachHang> khachHangOpt = khachHangRepository.findByIdTK(request.getIdTK());
        if (!khachHangOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy khách hàng với idTK: " + request.getIdTK());
        }
        KhachHang khachHang = khachHangOpt.get();

        // Kiểm tra phương thức thanh toán
        Optional<PhuongTT> phuongTTOpt = phuongThanhToanRepository.findByIdPTT(request.getPaymentMethod());
        if (!phuongTTOpt.isPresent()) {
            throw new RuntimeException("Phương thức thanh toán không hợp lệ: " + request.getPaymentMethod());
        }
        PhuongTT phuongTT = phuongTTOpt.get();

        // Kiểm tra số lượng tồn kho
        for (OrderRequestDTO.OrderItemDTO item : request.getItems()) {
            Optional<SanPhamChiTiet> sanPhamCTOpt = sanPhamChiTietRepository.findById(item.getIdSPCT());
            if (!sanPhamCTOpt.isPresent()) {
                throw new RuntimeException("Không tìm thấy sản phẩm chi tiết với idSPCT: " + item.getIdSPCT());
            }
            SanPhamChiTiet sanPhamCT = sanPhamCTOpt.get();
            if (sanPhamCT.getSoLuong() < item.getSoLuong()) {
                throw new RuntimeException("Số lượng tồn kho không đủ cho sản phẩm idSPCT: " + item.getIdSPCT());
            }
        }

        // Tạo hóa đơn
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHD("HD" + UUID.randomUUID().toString().substring(0, 8));
        hoaDon.setIdKhachHang(khachHang.getIdKH());
        hoaDon.setIdNhanVien(null); // Chưa có nhân viên xử lý
        hoaDon.setNgayTao(LocalDateTime.now());
        hoaDon.setNgaySua(LocalDateTime.now());
        hoaDon.setTongTien(new BigDecimal(String.valueOf(request.getTotal())));
        hoaDon.setTrangThai(true);

        // Lưu hóa đơn
        hoaDon = hoaDonRepository.save(hoaDon);

        // Xử lý chi tiết hóa đơn và trừ số lượng tồn kho
        for (OrderRequestDTO.OrderItemDTO item : request.getItems()) {
            // Tìm sản phẩm chi tiết
            SanPhamChiTiet sanPhamCT = sanPhamChiTietRepository.findById(item.getIdSPCT()).get();

            // Tạo chi tiết hóa đơn
            HoaDonChiTiet hoaDonCT = new HoaDonChiTiet();
            hoaDonCT.setHoaDon(hoaDon);
            hoaDonCT.setSanPhamct(sanPhamCT);
            hoaDonCT.setSoLuong(item.getSoLuong());
            hoaDonCT.setDonGia(new BigDecimal(String.valueOf(item.getDonGia())));
            hoaDonCT.setThanhTien(new BigDecimal(String.valueOf(item.getDonGia())).multiply(BigDecimal.valueOf(item.getSoLuong())));
            hoaDonCT.setTrangThai(true);
            hoaDonCT.setNgayTao(LocalDateTime.now());
            hoaDonCT.setNgaySua(LocalDateTime.now());
            hoaDonCT.setPhuongThucThanhToan(phuongTT);

            // Xử lý khuyến mãi
            if (request.getVoucherCode() != null) {
                Optional<KhuyenMai> khuyenMaiOpt = khuyenMaiRepository.findByMaKM(request.getVoucherCode());
                if (khuyenMaiOpt.isPresent()) {
                    KhuyenMai khuyenMai = khuyenMaiOpt.get();
                    if (khuyenMai.getSoLuong() <= khuyenMai.getDaSuDung()) {
                        throw new RuntimeException("Voucher " + request.getVoucherCode() + " đã hết lượt sử dụng");
                    }
                    khuyenMai.setDaSuDung(khuyenMai.getDaSuDung() + 1);
                    khuyenMaiRepository.save(khuyenMai);
                    hoaDonCT.setKhuyenMai(khuyenMai);
                } else {
                    throw new RuntimeException("Không tìm thấy voucher: " + request.getVoucherCode());
                }
            } else {
                hoaDonCT.setKhuyenMai(null);
            }

            // Lưu chi tiết hóa đơn
            hoaDonChiTietRepository.save(hoaDonCT);

            // Trừ số lượng tồn kho
            sanPhamCT.setSoLuong(sanPhamCT.getSoLuong() - item.getSoLuong());
            sanPhamChiTietRepository.save(sanPhamCT);
        }

        return hoaDon;
    }

    // Các phương thức hiện có
    public HoaDonDTO getHoaDonById(Integer id) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tìm thấy"));
        return convertToDTO(hoaDon);
    }

    public List<HoaDonDTO> getAllHoaDon() {
        return hoaDonRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public HoaDonDTO updateHoaDon(Integer id, HoaDonDTO dto) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));
        hoaDon.setMaHD(dto.getMaHD());
        hoaDon.setIdKhachHang(dto.getIdKhachHang());
        hoaDon.setIdNhanVien(dto.getIdNhanVien());
        hoaDon.setTongTien(dto.getTongTien());
        hoaDon.setTrangThai(dto.getTrangThai());
        hoaDon.setNgaySua(LocalDateTime.now());
        HoaDon updatedHoaDon = hoaDonRepository.save(hoaDon);
        return convertToDTO(updatedHoaDon);
    }

    public void deleteHoaDon(Integer id) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));
        hoaDonRepository.delete(hoaDon);
    }

    // Create
    public HoaDonDTO createHoaDon(HoaDonDTO dto) {
        HoaDon hoaDon = convertToEntity(dto);
        hoaDon.setNgayTao(LocalDateTime.now());
        hoaDon.setNgaySua(LocalDateTime.now());
        HoaDon savedHoaDon = hoaDonRepository.save(hoaDon);
        return convertToDTO(savedHoaDon);
    }

    // Manual mapping: DTO to Entity
    private HoaDon convertToEntity(HoaDonDTO dto) {
        HoaDon hoaDon = new HoaDon();
        hoaDon.setIdHD(dto.getIdHD());
        hoaDon.setMaHD(dto.getMaHD());
        hoaDon.setIdKhachHang(dto.getIdKhachHang());
        hoaDon.setIdNhanVien(dto.getIdNhanVien());
        hoaDon.setNgayTao(dto.getNgayTao());
        hoaDon.setNgaySua(dto.getNgaySua());
        hoaDon.setTongTien(dto.getTongTien());
        hoaDon.setTrangThai(dto.getTrangThai());
        return hoaDon;
    }
}