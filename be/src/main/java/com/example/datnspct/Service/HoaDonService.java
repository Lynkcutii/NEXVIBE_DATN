package com.example.datnspct.Service;

import com.example.datnspct.Model.HoaDon;
import com.example.datnspct.Model.HoaDonChiTiet;
import com.example.datnspct.Model.KhachHang;
import com.example.datnspct.Model.KhuyenMai;
import com.example.datnspct.Model.PhuongTT;
import com.example.datnspct.Model.SanPhamChiTiet;
import com.example.datnspct.Model.Voucher;
import com.example.datnspct.Repository.GioHangCTRepository;
import com.example.datnspct.Repository.HoaDonChiTietRepository;
import com.example.datnspct.Repository.HoaDonRepository;
import com.example.datnspct.Repository.KhachHangRepository;
import com.example.datnspct.Repository.KhuyenMaiRepository;
import com.example.datnspct.Repository.PhuongThucThanhToanRepository;
import com.example.datnspct.Repository.SanPhamChiTietRepository;
import com.example.datnspct.Repository.VoucherRepository;
import com.example.datnspct.dto.HoaDonDTO;
import com.example.datnspct.dto.OrderRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

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

    @Autowired
    private GioHangCTRepository gioHangCTRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    // Phương thức xóa sản phẩm chi tiết giỏ hàng sau khi thanh toán thành công
    private void deleteCartItemsAfterOrder(List<OrderRequestDTO.OrderItemDTO> items) {
        System.out.println("deleteCartItemsAfterOrder: Starting to delete " + items.size() + " cart items");
        
        for (OrderRequestDTO.OrderItemDTO item : items) {
            try {
                if (item.getIdGHCT() != null) {
                    // Kiểm tra xem sản phẩm chi tiết giỏ hàng có tồn tại không
                    if (gioHangCTRepository.existsById(item.getIdGHCT())) {
                        gioHangCTRepository.deleteById(item.getIdGHCT());
                        System.out.println("deleteCartItemsAfterOrder: Successfully deleted GioHangChiTiet idGHCT: " + item.getIdGHCT());
                    } else {
                        System.out.println("deleteCartItemsAfterOrder: Warning - GioHangChiTiet idGHCT: " + item.getIdGHCT() + " not found, may have been already deleted");
                    }
                } else {
                    System.out.println("deleteCartItemsAfterOrder: Warning - idGHCT is null for item idSPCT: " + item.getIdSPCT());
                }
            } catch (Exception e) {
                System.err.println("deleteCartItemsAfterOrder: Error deleting GioHangChiTiet idGHCT: " + item.getIdGHCT() + " - " + e.getMessage());
                // Không throw exception để tránh rollback toàn bộ transaction
                // Chỉ log lỗi và tiếp tục xử lý
            }
        }
        
        System.out.println("deleteCartItemsAfterOrder: Completed");
    }

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
        dto.setLoaiHoaDon(hoaDon.getLoaiHoaDon());
        // Lấy tổng số sản phẩm từ repository
        int totalProducts = hoaDonChiTietRepository.getTotalProductsByHoaDonId(hoaDon.getIdHD());
        dto.setTotalProducts(totalProducts);
        return dto;
    }

    // Tạo hóa đơn từ OrderRequestDTO
    @Transactional
    public HoaDon createHoaDon(OrderRequestDTO request) {
        List<String> errors = new ArrayList<>();
        System.out.println("createHoaDon: Starting with request: " + request);
        System.out.println("[DEBUG] Dữ liệu nhận được khi tạo hóa đơn: " + request);

        // Kiểm tra khách hàng (chỉ bắt buộc nếu có idTK)
        Optional<KhachHang> khachHangOpt = Optional.empty();
        if (request.getIdTK() != null) {
            khachHangOpt = khachHangRepository.findByIdTK(request.getIdTK());
            if (!khachHangOpt.isPresent()) {
                errors.add("Không tìm thấy khách hàng với idTK: " + request.getIdTK());
            }
        }

        // Kiểm tra phương thức thanh toán
        Optional<PhuongTT> phuongTTOpt = phuongThanhToanRepository.findByIdPTT(request.getPaymentMethod());
        if (!phuongTTOpt.isPresent()) {
            errors.add("Phương thức thanh toán không hợp lệ: " + request.getPaymentMethod() +
                    ". Các giá trị hợp lệ: " + phuongThanhToanRepository.findAll().stream()
                    .map(ptt -> ptt.getIdPTT() + ": " + ptt.getTen())
                    .collect(Collectors.joining(", ")));
        }

        // Kiểm tra số lượng tồn kho và tính tổng tiền
        BigDecimal calculatedSubTotal = BigDecimal.ZERO;
        for (OrderRequestDTO.OrderItemDTO item : request.getItems()) {
            Optional<SanPhamChiTiet> sanPhamCTOpt = sanPhamChiTietRepository.findById(item.getIdSPCT());
            if (!sanPhamCTOpt.isPresent()) {
                errors.add("Không tìm thấy sản phẩm chi tiết với idSPCT: " + item.getIdSPCT());
            } else {
                SanPhamChiTiet sanPhamCT = sanPhamCTOpt.get();
                if (sanPhamCT.getSoLuong() < item.getSoLuong()) {
                    errors.add("Số lượng tồn kho không đủ cho sản phẩm idSPCT: " + item.getIdSPCT() +
                            ". Tồn kho hiện tại: " + sanPhamCT.getSoLuong());
                }
                if (item.getSoLuong() <= 0) {
                    errors.add("Số lượng sản phẩm idSPCT: " + item.getIdSPCT() + " phải lớn hơn 0");
                }
                BigDecimal itemTotal = item.getDonGia().multiply(BigDecimal.valueOf(item.getSoLuong()));
                calculatedSubTotal = calculatedSubTotal.add(itemTotal);
            }
        }

        // Kiểm tra khuyến mãi áp dụng cho toàn đơn hàng
        BigDecimal promotionDiscount = BigDecimal.ZERO;
        if (request.getIdKM() != null) {
            Optional<KhuyenMai> khuyenMaiOpt = khuyenMaiRepository.findById(request.getIdKM());
            if (!khuyenMaiOpt.isPresent()) {
                errors.add("Không tìm thấy khuyến mãi với idKM: " + request.getIdKM());
            } else {
                KhuyenMai khuyenMai = khuyenMaiOpt.get();
                if (khachHangOpt.isPresent() && (khuyenMai.getKhachHang() == null ||
                        khuyenMai.getKhachHang().getIdKH() != khachHangOpt.get().getIdKH())) {
                    errors.add("Khuyến mãi idKM: " + request.getIdKM() + " không áp dụng cho khách hàng idTK: " + request.getIdTK());
                } else if (khuyenMai.getSoLuong() <= khuyenMai.getDaSuDung()) {
                    errors.add("Khuyến mãi idKM: " + request.getIdKM() + " đã hết lượt sử dụng");
                } else if (calculatedSubTotal.compareTo(khuyenMai.getGiaTriDonHangToiThieu()) < 0) {
                    errors.add("Tổng tiền đơn hàng (" + calculatedSubTotal +
                            ") không đạt mức tối thiểu để áp dụng khuyến mãi: " +
                            khuyenMai.getGiaTriDonHangToiThieu());
                } else {
                    if (khuyenMai.getHinhThucGiam().equalsIgnoreCase("PERCENTAGE")) {
                        promotionDiscount = calculatedSubTotal.multiply(khuyenMai.getMucGiam().divide(new BigDecimal("100")));
                        if (khuyenMai.getGiamToiDa() != null && khuyenMai.getGiamToiDa().compareTo(BigDecimal.ZERO) > 0) {
                            promotionDiscount = promotionDiscount.min(khuyenMai.getGiamToiDa());
                        }
                    } else {
                        promotionDiscount = khuyenMai.getMucGiam();
                    }
                }
            }
        }

        // Kiểm tra tổng tiền
        BigDecimal calculatedTotal = calculatedSubTotal.subtract(promotionDiscount);
        if (calculatedTotal.subtract(request.getTotal()).abs().compareTo(new BigDecimal("100")) > 0) {
            errors.add("Tổng tiền không khớp. Tổng tính toán (sản phẩm - khuyến mãi): " + calculatedTotal +
                    ", Tổng gửi: " + request.getTotal());
        }

        // Kiểm tra lỗi
        if (!errors.isEmpty()) {
            throw new RuntimeException("Lỗi khi tạo hóa đơn: " + String.join("; ", errors));
        }

        // Lấy thông tin phương thức thanh toán
        PhuongTT phuongTT = phuongTTOpt.get();

        // Tạo hóa đơn
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHD("HD" + UUID.randomUUID().toString().substring(0, 8));
        hoaDon.setIdKhachHang(khachHangOpt.map(KhachHang::getIdKH).orElse(null));
        hoaDon.setIdNhanVien(null);
        hoaDon.setNgayTao(LocalDateTime.now());
        hoaDon.setNgaySua(LocalDateTime.now());
        hoaDon.setTongTien(request.getTotal());
        hoaDon.setTrangThai("Chờ xác nhận");
        // Phân biệt loại hóa đơn dựa trên nguồn thanh toán
        if (request.getIdTK() == null) {
            // Không có idTK => Bán tại quầy
            hoaDon.setLoaiHoaDon("Tại quầy");
        } else {
            // Có idTK => Thanh toán online
            String loaiHoaDon = request.getLoaiHoaDon();
            if (loaiHoaDon == null || loaiHoaDon.trim().isEmpty()) {
                loaiHoaDon = "Trực tuyến"; // Mặc định cho thanh toán online
            }
            hoaDon.setLoaiHoaDon(loaiHoaDon);
        }
        if (request.getIdKM() != null) {
            hoaDon.setIdKM(request.getIdKM());
        }

        // Lưu hóa đơn
        System.out.println("createHoaDon: Saving HoaDon: " + hoaDon);
        hoaDon = hoaDonRepository.save(hoaDon);

        // Trừ số lượng khuyến mãi nếu có áp dụng
        if (request.getIdKM() != null) {
            Optional<KhuyenMai> khuyenMaiOpt = khuyenMaiRepository.findById(request.getIdKM());
            if (khuyenMaiOpt.isPresent()) {
                KhuyenMai khuyenMai = khuyenMaiOpt.get();
                khuyenMai.setSoLuong(khuyenMai.getSoLuong() - 1);
                khuyenMaiRepository.save(khuyenMai);
                System.out.println("createHoaDon: Updated KhuyenMai idKM: " + request.getIdKM() +
                        ", DaSuDung: " + khuyenMai.getDaSuDung());
            }
        }

        // Xử lý chi tiết hóa đơn và trừ số lượng tồn kho
        for (OrderRequestDTO.OrderItemDTO item : request.getItems()) {
            SanPhamChiTiet sanPhamCT = sanPhamChiTietRepository.findById(item.getIdSPCT()).get();
            System.out.println("createHoaDon: Processing item idSPCT: " + item.getIdSPCT() +
                    ", SoLuong trước: " + sanPhamCT.getSoLuong() +
                    ", SoLuong cần trừ: " + item.getSoLuong());

            // Trừ số lượng tồn kho
            int newSoLuong = sanPhamCT.getSoLuong() - item.getSoLuong();
            if (newSoLuong < 0) {
                throw new RuntimeException("Số lượng tồn kho không đủ sau khi tính toán cho sản phẩm idSPCT: " + item.getIdSPCT());
            }
            sanPhamCT.setSoLuong(newSoLuong);
            sanPhamChiTietRepository.save(sanPhamCT);
            System.out.println("createHoaDon: Updated SanPhamChiTiet idSPCT: " + item.getIdSPCT() +
                    ", SoLuong sau: " + sanPhamCT.getSoLuong());

            // Tạo chi tiết hóa đơn
            HoaDonChiTiet hoaDonCT = new HoaDonChiTiet();
            hoaDonCT.setHoaDon(hoaDon);
            hoaDonCT.setSanPhamct(sanPhamCT);
            hoaDonCT.setSoLuong(item.getSoLuong());
            hoaDonCT.setDonGia(new BigDecimal(String.valueOf(item.getDonGia())));
            BigDecimal thanhTien = new BigDecimal(String.valueOf(item.getDonGia())).multiply(BigDecimal.valueOf(item.getSoLuong()));
            hoaDonCT.setThanhTien(thanhTien);
            hoaDonCT.setNgayTao(LocalDateTime.now());
            hoaDonCT.setNgaySua(LocalDateTime.now());
            hoaDonCT.setPhuongThucThanhToan(phuongTT);

            // Trừ số lượng voucher nếu có
            if (item.getIdVoucher() != null) {
                Optional<Voucher> voucherOpt = voucherRepository.findById(item.getIdVoucher());
                if (voucherOpt.isPresent()) {
                    Voucher voucher = voucherOpt.get();
                    voucher.setSoLuong(voucher.getSoLuong() - 1);
                    voucherRepository.save(voucher);
                    System.out.println("createHoaDon: Updated Voucher idVoucher: " + item.getIdVoucher() +
                            ", SoLuong: " + voucher.getSoLuong());
                    // hoaDonCT.setVoucher(voucher); // Nếu entity có trường này
                }
            }

            hoaDonChiTietRepository.save(hoaDonCT);
            System.out.println("createHoaDon: Saved HoaDonChiTiet for idSPCT: " + item.getIdSPCT());
        }

        // Xóa sản phẩm chi tiết giỏ hàng sau khi tạo hóa đơn thành công
        deleteCartItemsAfterOrder(request.getItems());

        System.out.println("createHoaDon: Order created successfully, idHD: " + hoaDon.getIdHD());
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

    public Page<HoaDonDTO> filterHoaDon(String keyword, String status, String type, String dateFrom, String dateTo, Pageable pageable) {
        List<HoaDon> all = hoaDonRepository.findAll();
        Stream<HoaDon> stream = all.stream();
        if (keyword != null && !keyword.isEmpty()) {
            String kw = keyword.toLowerCase();
            stream = stream.filter(hd ->
                (hd.getMaHD() != null && hd.getMaHD().toLowerCase().contains(kw)) ||
                (hd.getKhachHang() != null && hd.getKhachHang().getTenKH() != null && hd.getKhachHang().getTenKH().toLowerCase().contains(kw))
            );
        }
        if (status != null && !status.isEmpty()) {
            stream = stream.filter(hd -> status.equalsIgnoreCase(hd.getTrangThai()));
        }
        if (type != null && !type.isEmpty()) {
            stream = stream.filter(hd ->
                hd.getLoaiHoaDon() != null && type.trim().equalsIgnoreCase(hd.getLoaiHoaDon().trim())
            );
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (dateFrom != null && !dateFrom.isEmpty()) {
            LocalDateTime from = LocalDateTime.parse(dateFrom, dtf);
            stream = stream.filter(hd -> hd.getNgayTao() != null && !hd.getNgayTao().isBefore(from));
        }
        if (dateTo != null && !dateTo.isEmpty()) {
            LocalDateTime to = LocalDateTime.parse(dateTo, dtf);
            stream = stream.filter(hd -> hd.getNgayTao() != null && !hd.getNgayTao().isAfter(to));
        }
        List<HoaDonDTO> filtered = stream.map(this::convertToDTO).collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        List<HoaDonDTO> pageContent = start > end ? new ArrayList<>() : filtered.subList(start, end);
        return new PageImpl<>(pageContent, pageable, filtered.size());
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

    public HoaDonDTO createHoaDon(HoaDonDTO dto) {
        HoaDon hoaDon = convertToEntity(dto);
        hoaDon.setNgayTao(LocalDateTime.now());
        hoaDon.setNgaySua(LocalDateTime.now());
        HoaDon savedHoaDon = hoaDonRepository.save(hoaDon);
        return convertToDTO(savedHoaDon);
    }

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
        hoaDon.setLoaiHoaDon(dto.getLoaiHoaDon());
        return hoaDon;
    }

    // Method to update order status
    public void updateOrderStatus(Integer idHD, String newStatus, String ghiChu) {
        // Tìm hóa đơn theo ID
        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với ID: " + idHD));
        
        // Cập nhật trạng thái
        hoaDon.setTrangThai(newStatus);
        hoaDon.setNgaySua(java.time.LocalDateTime.now());
        
        // Lưu vào database
        hoaDonRepository.save(hoaDon);
    }
}