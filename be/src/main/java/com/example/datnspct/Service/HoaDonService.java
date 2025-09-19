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
import com.example.datnspct.dto.HoaDonChiTietDTO;
import com.example.datnspct.dto.HoaDonDTO;
import com.example.datnspct.dto.OrderRequestDTO;
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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
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
            }
        }

        System.out.println("deleteCartItemsAfterOrder: Completed");
    }

    // Manual mapping: Entity to DTO
    private HoaDonDTO convertToDTO(HoaDon hoaDon) {
        HoaDonDTO dto = new HoaDonDTO();
        dto.setIdHD(hoaDon.getIdHD());
        dto.setMaHD(hoaDon.getMaHD());
        // Ánh xạ idKhachHang và customerName từ khachHang
        if (hoaDon.getKhachHang() != null) {
            dto.setIdKhachHang(hoaDon.getKhachHang().getIdKH());
            dto.setCustomerName(hoaDon.getKhachHang().getTenKH());
        } else {
            dto.setIdKhachHang(null);
            dto.setCustomerName("Khách lẻ");
        }

        if(hoaDon.getKhuyenMai() != null){
            dto.setIdKM(hoaDon.getKhuyenMai().getIdKM());
        } else {
            dto.setIdKM(null);
        }
        dto.setIdNhanVien(hoaDon.getIdNhanVien());
        dto.setNgayTao(hoaDon.getNgayTao());
        dto.setNgaySua(hoaDon.getNgaySua());
        dto.setTongTien(hoaDon.getTongTien());
        dto.setTrangThai(hoaDon.getTrangThai());
        dto.setLoaiHoaDon(hoaDon.getLoaiHoaDon());
        dto.setTotalProducts(hoaDonChiTietRepository.getTotalProductsByHoaDonId(hoaDon.getIdHD()));
        dto.setIdPT(hoaDon.getPhuongThucThanhToan() != null ? hoaDon.getPhuongThucThanhToan().getIdPTT() : null);
        dto.setMaGiaoDich(hoaDon.getMaHD());

        // Lấy danh sách chi tiết hóa đơn
        List<HoaDonChiTietDTO> chiTietDTOs = hoaDonChiTietRepository.findByHoaDonId(hoaDon.getIdHD())
                .stream()
                .map(ct -> {
                    HoaDonChiTietDTO ctDTO = new HoaDonChiTietDTO();
                    ctDTO.setIdHDCT(ct.getIdHDCT());
                    ctDTO.setIdHD(ct.getHoaDon().getIdHD());
                    ctDTO.setIdCtSanPham(ct.getSanPhamct().getId());
                    ctDTO.setIdSP(ct.getSanPhamct().getSanPham().getId());
                    ctDTO.setMaSPCT(ct.getSanPhamct().getMaSPCT());
                    ctDTO.setTenSanPham(ct.getSanPhamct().getSanPham() != null ? ct.getSanPhamct().getSanPham().getTenSP() : "N/A");
                    ctDTO.setDonGia(ct.getDonGia());
                    ctDTO.setSoLuong(ct.getSoLuong());
                    ctDTO.setThanhTien(ct.getThanhTien());
                    ctDTO.setTenSize(ct.getSanPhamct().getSize() != null ? ct.getSanPhamct().getSize().getTenSize() : "N/A");
                    ctDTO.setTenMauSac(ct.getSanPhamct().getMauSac() != null ? ct.getSanPhamct().getMauSac().getTenMauSac() : "N/A");
                    ctDTO.setTenThuongHieu(ct.getSanPhamct().getSanPham() != null && ct.getSanPhamct().getSanPham().getThuongHieu() != null
                            ? ct.getSanPhamct().getSanPham().getThuongHieu().getTenThuongHieu() : "N/A");
                    ctDTO.setTenChatLieu(ct.getSanPhamct().getSanPham() != null && ct.getSanPhamct().getSanPham().getChatLieu() != null
                            ? ct.getSanPhamct().getSanPham().getChatLieu().getTenChatLieu() : "N/A");
                    ctDTO.setAnhGiay(ct.getSanPhamct().getSanPham() != null && ct.getSanPhamct().getSanPham().getImg() != null
                            ? ct.getSanPhamct().getSanPham().getImg() : "https://via.placeholder.com/60");
                    ctDTO.setSoLuongTonKho(ct.getSanPhamct().getSoLuong());
                    ctDTO.setNgayTao(ct.getNgayTao());
                    ctDTO.setNgaySua(ct.getNgaySua());
                    ctDTO.setTrangThai(true);
                    return ctDTO;
                })
                .collect(Collectors.toList());
        dto.setChiTietSanPham(chiTietDTOs);
        return dto;
    }

    // Tạo hóa đơn từ OrderRequestDTO
    @Transactional
    public HoaDon createHoaDon(OrderRequestDTO request) {
        List<String> errors = new ArrayList<>();
        System.out.println("createHoaDon: Starting with request: " + request);

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
            errors.add("Phương thức thanh toán không hợp lệ: " + request.getPaymentMethod());
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
                    errors.add("Số lượng tồn kho không đủ cho sản phẩm idSPCT: " + item.getIdSPCT());
                }
                if (item.getSoLuong() <= 0) {
                    errors.add("Số lượng sản phẩm idSPCT: " + item.getIdSPCT() + " phải lớn hơn 0");
                }
                BigDecimal itemTotal = item.getDonGia().multiply(BigDecimal.valueOf(item.getSoLuong()));
                calculatedSubTotal = calculatedSubTotal.add(itemTotal);
            }
        }

        // Kiểm tra khuyến mãi
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
                    errors.add("Tổng tiền đơn hàng không đạt mức tối thiểu để áp dụng khuyến mãi");
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
            errors.add("Tổng tiền không khớp. Tổng tính toán: " + calculatedTotal + ", Tổng gửi: " + request.getTotal());
        }

        if (!errors.isEmpty()) {
            throw new RuntimeException("Lỗi khi tạo hóa đơn: " + String.join("; ", errors));
        }

        // Tạo hóa đơn
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHD("HD" + UUID.randomUUID().toString().substring(0, 8));
        hoaDon.setIdNhanVien(null);
        hoaDon.setNgayTao(LocalDateTime.now());
        hoaDon.setNgaySua(LocalDateTime.now());
        hoaDon.setTongTien(request.getTotal());
        hoaDon.setTrangThai("CHO_XAC_NHAN");
        hoaDon.setLoaiHoaDon("Trực tuyến");
        hoaDon.setPhuongThucThanhToan(phuongTTOpt.get());

        hoaDon = hoaDonRepository.save(hoaDon);

        // Trừ số lượng khuyến mãi
        if (request.getIdKM() != null) {
            Optional<KhuyenMai> khuyenMaiOpt = khuyenMaiRepository.findById(request.getIdKM());
            if (khuyenMaiOpt.isPresent()) {
                KhuyenMai khuyenMai = khuyenMaiOpt.get();
                khuyenMai.setSoLuong(khuyenMai.getSoLuong() - 1);
                khuyenMaiRepository.save(khuyenMai);
            }
        }

        // Xử lý chi tiết hóa đơn
        for (OrderRequestDTO.OrderItemDTO item : request.getItems()) {
            SanPhamChiTiet sanPhamCT = sanPhamChiTietRepository.findById(item.getIdSPCT()).get();
            int newSoLuong = sanPhamCT.getSoLuong() - item.getSoLuong();
            if (newSoLuong < 0) {
                throw new RuntimeException("Số lượng tồn kho không đủ cho sản phẩm idSPCT: " + item.getIdSPCT());
            }
            sanPhamCT.setSoLuong(newSoLuong);
            sanPhamChiTietRepository.save(sanPhamCT);

            HoaDonChiTiet hoaDonCT = new HoaDonChiTiet();
            hoaDonCT.setHoaDon(hoaDon);
            hoaDonCT.setSanPhamct(sanPhamCT);
            hoaDonCT.setSoLuong(item.getSoLuong());
            hoaDonCT.setDonGia(item.getDonGia());
            BigDecimal thanhTien = item.getDonGia().multiply(BigDecimal.valueOf(item.getSoLuong()));
            hoaDonCT.setThanhTien(thanhTien);
            hoaDonCT.setNgayTao(LocalDateTime.now());
            hoaDonCT.setNgaySua(LocalDateTime.now());
            hoaDonChiTietRepository.save(hoaDonCT);

            if (item.getIdVoucher() != null) {
                Optional<Voucher> voucherOpt = voucherRepository.findById(item.getIdVoucher());
                if (voucherOpt.isPresent()) {
                    Voucher voucher = voucherOpt.get();
                    voucher.setSoLuong(voucher.getSoLuong() - 1);
                    voucherRepository.save(voucher);
                }
            }
        }

        deleteCartItemsAfterOrder(request.getItems());
        return hoaDon;
    }

    public Page<HoaDonDTO> filterHoaDon(String keyword, String status, String type, String dateFrom, String dateTo, Pageable pageable) {
        List<HoaDon> all = hoaDonRepository.findAll();
        Stream<HoaDon> stream = all.stream();

        // Lọc theo keyword (nếu có)
        if (keyword != null && !keyword.isEmpty()) {
            String kw = keyword.toLowerCase();
            stream = stream.filter(hd ->
                    (hd.getMaHD() != null && hd.getMaHD().toLowerCase().contains(kw)) ||
                            (hd.getKhachHang() != null && hd.getKhachHang().getTenKH() != null && hd.getKhachHang().getTenKH().toLowerCase().contains(kw))
            );
        }

        // Lọc theo danh sách trạng thái
        if (status != null && !status.isEmpty()) {
            List<String> statuses = Arrays.asList(status.split(","));
            stream = stream.filter(hd -> statuses.stream().anyMatch(s -> s.equalsIgnoreCase(hd.getTrangThai())));
        }

        // Lọc theo loại hóa đơn (nếu có)
        if (type != null && !type.isEmpty()) {
            stream = stream.filter(hd -> type.trim().equalsIgnoreCase(hd.getLoaiHoaDon().trim()));
        }

        // Lọc theo ngày (nếu có)
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (dateFrom != null && !dateFrom.isEmpty()) {
            LocalDateTime from = LocalDateTime.parse(dateFrom, dtf);
            stream = stream.filter(hd -> hd.getNgayTao() != null && !hd.getNgayTao().isBefore(from));
        }
        if (dateTo != null && !dateTo.isEmpty()) {
            LocalDateTime to = LocalDateTime.parse(dateTo, dtf);
            stream = stream.filter(hd -> hd.getNgayTao() != null && !hd.getNgayTao().isAfter(to));
        }

        // Thêm log chi tiết
        List<HoaDonDTO> filtered = stream.map(this::convertToDTO).collect(Collectors.toList());
        System.out.println("Danh sách hóa đơn sau lọc: " + filtered.size() + " hóa đơn");
        filtered.forEach(hd -> System.out.println("Hóa đơn: idHD=" + hd.getIdHD() + ", maHD=" + hd.getMaHD() + ", trangThai=" + hd.getTrangThai()));

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        List<HoaDonDTO> pageContent = start >= end ? new ArrayList<>() : filtered.subList(start, end);
        return new PageImpl<>(pageContent, pageable, filtered.size());
    }

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

    @Transactional
    public HoaDonDTO capNhatHoaDon(Integer id, HoaDonDTO dto) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại: " + id));
        String oldStatus = hoaDon.getTrangThai();
        System.out.println("capNhatHoaDon: Cập nhật hóa đơn idHD=" + id + ", trạng thái cũ=" + oldStatus + ", trạng thái mới=" + dto.getTrangThai() + ", idKM gửi từ DTO=" + dto.getIdKM());

        // Cập nhật thông tin hóa đơn cơ bản
        hoaDon.setMaHD(dto.getMaHD());
        hoaDon.setIdNhanVien(dto.getIdNhanVien());
        hoaDon.setLoaiHoaDon(dto.getLoaiHoaDon());
        hoaDon.setNgayTao(dto.getNgayTao());
        hoaDon.setNgaySua(LocalDateTime.now());
        hoaDon.setTrangThai(dto.getTrangThai());

        // Xử lý khách hàng
        if (dto.getIdKhachHang() != null) {
            KhachHang khachHang = khachHangRepository.findById(dto.getIdKhachHang())
                    .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại: " + dto.getIdKhachHang()));
            hoaDon.setKhachHang(khachHang);
        } else {
            hoaDon.setKhachHang(null); // Khách lẻ
        }

        // Xử lý phương thức thanh toán
        if (dto.getIdPT() != null) {
            PhuongTT ptt = phuongThanhToanRepository.findById(dto.getIdPT())
                    .orElseThrow(() -> new RuntimeException("Phương thức thanh toán không tồn tại: " + dto.getIdPT()));
            hoaDon.setPhuongThucThanhToan(ptt);
        } else {
            hoaDon.setPhuongThucThanhToan(null);
        }

        // Xử lý khuyến mãi hóa đơn
        Set<Integer> processedKhuyenMai = new HashSet<>();
        boolean isCompleting = !"HOAN_THANH".equals(oldStatus) && "HOAN_THANH".equals(dto.getTrangThai());

        if (dto.getIdKM() != null) {
            KhuyenMai km = khuyenMaiRepository.findById(dto.getIdKM())
                    .orElseThrow(() -> new RuntimeException("Khuyến mãi không tồn tại: " + dto.getIdKM()));
            // Kiểm tra điều kiện khuyến mãi
            BigDecimal tongTienHang = calculateTongTienHang(hoaDon, dto.getChiTietSanPham());
            if (tongTienHang.compareTo(km.getGiaTriDonHangToiThieu()) < 0) {
                throw new RuntimeException("Tổng tiền đơn hàng không đạt mức tối thiểu để áp dụng khuyến mãi: " + km.getTenKM());
            }
            if (km.getSoLuong() <= 0) {
                throw new RuntimeException("Khuyến mãi " + km.getTenKM() + " đã hết số lượng");
            }
            if (isCompleting) {
                decreaseKhuyenMaiQuantity(dto.getIdKM(), processedKhuyenMai);
            }
            hoaDon.setKhuyenMai(km);
            System.out.println("capNhatHoaDon: Đã gán khuyến mãi idKM=" + dto.getIdKM() + " cho hóa đơn idHD=" + id);
        } else {
            hoaDon.setKhuyenMai(null);
            System.out.println("capNhatHoaDon: Không có khuyến mãi được áp dụng (idKM=null)");
        }

        // Xử lý chi tiết hóa đơn
        List<HoaDonChiTiet> chiTietSanPham = new ArrayList<>();
        List<Integer> newIds = new ArrayList<>();
        Set<Integer> processedVouchers = new HashSet<>();

        if (dto.getChiTietSanPham() != null && !dto.getChiTietSanPham().isEmpty()) {
            for (HoaDonChiTietDTO ctDto : dto.getChiTietSanPham()) {
                // Kiểm tra hợp lệ chi tiết hóa đơn
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

                // Tìm hoặc tạo mới chi tiết hóa đơn
                HoaDonChiTiet hdct;
                if (ctDto.getIdHDCT() != null) {
                    hdct = hoaDonChiTietRepository.findById(ctDto.getIdHDCT())
                            .orElseThrow(() -> new RuntimeException("Chi tiết hóa đơn không tồn tại: " + ctDto.getIdHDCT()));
                    newIds.add(ctDto.getIdHDCT());
                } else {
                    hdct = new HoaDonChiTiet();
                    hdct.setHoaDon(hoaDon);
                }

                // Tìm sản phẩm chi tiết
                SanPhamChiTiet spct = sanPhamChiTietRepository.findById(ctDto.getIdCtSanPham())
                        .orElseThrow(() -> new RuntimeException("Sản phẩm chi tiết không tồn tại: " + ctDto.getIdCtSanPham()));

                // Trừ số lượng sản phẩm nếu chuyển sang HOAN_THANH
                if (isCompleting) {
                    int soLuongCanTru = ctDto.getSoLuong();
                    if (soLuongCanTru > spct.getSoLuong()) {
                        throw new RuntimeException("Số lượng tồn kho không đủ cho sản phẩm idSPCT: " + ctDto.getIdCtSanPham() + ". Còn lại: " + spct.getSoLuong());
                    }
                    spct.setSoLuong(spct.getSoLuong() - soLuongCanTru);
                    sanPhamChiTietRepository.save(spct);
                    System.out.println("capNhatHoaDon: Trừ số lượng sản phẩm idSPCT=" + ctDto.getIdCtSanPham() + ", số lượng trừ=" + soLuongCanTru + ", còn lại=" + spct.getSoLuong());
                }

                // Trừ voucher nếu áp dụng
                hdct.setVoucher(null);
                if (isCompleting && ctDto.getIdVoucher() != null) {
                    decreaseVoucherQuantity(ctDto.getIdVoucher(), processedVouchers);
                    Voucher voucher = voucherRepository.findById(ctDto.getIdVoucher()).orElse(null);
                    hdct.setVoucher(voucher);
                }

                // Cập nhật thông tin chi tiết hóa đơn
                hdct.setSanPhamct(spct);
                hdct.setSoLuong(ctDto.getSoLuong());
                hdct.setDonGia(ctDto.getDonGia());
                hdct.setThanhTien(ctDto.getThanhTien());
                hdct.setNgayTao(ctDto.getNgayTao() != null ? ctDto.getNgayTao() : LocalDateTime.now());
                hdct.setNgaySua(LocalDateTime.now());
                chiTietSanPham.add(hdct);
            }

            // Xóa các bản ghi chi tiết không còn trong danh sách mới
            hoaDon.getChiTietSanPham().removeIf(ct -> !newIds.contains(ct.getIdHDCT()));
            hoaDon.getChiTietSanPham().addAll(chiTietSanPham);
        } else {
            System.out.println("capNhatHoaDon: Danh sách chi tiết hóa đơn rỗng, không cập nhật chi tiết.");
        }

        // Tính toán lại tổng tiền
        BigDecimal tongTienHang = calculateTongTienHang(hoaDon, dto.getChiTietSanPham());
        BigDecimal giamGiaKM = calculateGiamGiaKM(hoaDon.getKhuyenMai(), tongTienHang);
        BigDecimal giamGiaVoucher = calculateGiamGiaVoucher(chiTietSanPham);
        BigDecimal tongTien = tongTienHang.subtract(giamGiaKM).subtract(giamGiaVoucher);
        if (tongTien.compareTo(BigDecimal.ZERO) < 0) {
            tongTien = BigDecimal.ZERO;
        }
        hoaDon.setTongTien(tongTien);
        System.out.println("capNhatHoaDon: Tổng tiền hàng=" + tongTienHang + ", giảm giá KM=" + giamGiaKM + ", giảm giá voucher=" + giamGiaVoucher + ", tổng tiền cuối=" + tongTien);

        // Lưu hóa đơn
        HoaDon saved = hoaDonRepository.save(hoaDon);
        HoaDonDTO result = convertToDTO(saved);
        System.out.println("capNhatHoaDon: Lưu hóa đơn thành công, idKM trong DB=" + (saved.getKhuyenMai() != null ? saved.getKhuyenMai().getIdKM() : null));
        return result;
    }

    private BigDecimal calculateTongTienHang(HoaDon hoaDon, List<HoaDonChiTietDTO> chiTietDTOs) {
        BigDecimal tongTienHang = BigDecimal.ZERO;
        if (chiTietDTOs != null && !chiTietDTOs.isEmpty()) {
            for (HoaDonChiTietDTO ct : chiTietDTOs) {
                BigDecimal thanhTien = ct.getThanhTien();
                if (thanhTien != null) {
                    tongTienHang = tongTienHang.add(thanhTien);
                }
            }
        } else {
            for (HoaDonChiTiet ct : hoaDon.getChiTietSanPham()) {
                tongTienHang = tongTienHang.add(ct.getThanhTien());
            }
        }
        System.out.println("calculateTongTienHang: Tổng tiền hàng=" + tongTienHang);
        return tongTienHang;
    }

    private BigDecimal calculateGiamGiaKM(KhuyenMai khuyenMai, BigDecimal tongTienHang) {
        if (khuyenMai == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal giamGia = BigDecimal.ZERO;
        if (khuyenMai.getHinhThucGiam().equalsIgnoreCase("PERCENTAGE")) {
            giamGia = tongTienHang.multiply(khuyenMai.getMucGiam().divide(new BigDecimal("100")));
            if (khuyenMai.getGiamToiDa() != null && khuyenMai.getGiamToiDa().compareTo(BigDecimal.ZERO) > 0) {
                giamGia = giamGia.min(khuyenMai.getGiamToiDa());
            }
        } else if (khuyenMai.getHinhThucGiam().equalsIgnoreCase("FIXED")) {
            giamGia = khuyenMai.getMucGiam();
        }
        System.out.println("calculateGiamGiaKM: idKM=" + khuyenMai.getIdKM() + ", giảm giá=" + giamGia);
        return giamGia;
    }

    private BigDecimal calculateGiamGiaVoucher(List<HoaDonChiTiet> chiTietSanPham) {
        BigDecimal giamGia = BigDecimal.ZERO;
        for (HoaDonChiTiet ct : chiTietSanPham) {
            if (ct.getVoucher() != null) {
                Voucher voucher = ct.getVoucher();
                BigDecimal thanhTien = ct.getThanhTien();
                if (voucher.getHinhThucGiam().equalsIgnoreCase("PERCENTAGE")) {
                    BigDecimal giam = thanhTien.multiply(voucher.getMucGiam().divide(new BigDecimal("100")));
                    if (voucher.getGiamToiDa() != null && voucher.getGiamToiDa().compareTo(BigDecimal.ZERO) > 0) {
                        giam = giam.min(voucher.getGiamToiDa());
                    }
                    giamGia = giamGia.add(giam);
                } else if (voucher.getHinhThucGiam().equalsIgnoreCase("FIXED")) {
                    giamGia = giamGia.add(voucher.getMucGiam());
                }
                System.out.println("calculateGiamGiaVoucher: idVoucher=" + voucher.getIdVoucher() + ", giảm giá=" + giamGia);
            }
        }
        return giamGia;
    }

    // Hàm riêng trừ khuyến mãi
    private void decreaseKhuyenMaiQuantity(Integer idKM, Set<Integer> processedKhuyenMai) {
        if (!processedKhuyenMai.contains(idKM)) {
            KhuyenMai km = khuyenMaiRepository.findById(idKM)
                    .orElseThrow(() -> new RuntimeException("Khuyến mãi không tồn tại: " + idKM));
            if (km.getSoLuong() <= 0) {
                throw new RuntimeException("Khuyến mãi " + km.getTenKM() + " đã hết số lượng");
            }
            km.setSoLuong(km.getSoLuong() - 1);
            khuyenMaiRepository.save(km);
            processedKhuyenMai.add(idKM);
            System.out.println("capNhatHoaDon: Trừ khuyến mãi idKM=" + idKM + ", còn lại=" + km.getSoLuong());
        }
    }

    // Hàm riêng trừ voucher
    private void decreaseVoucherQuantity(Integer idVoucher, Set<Integer> processedVouchers) {
        if (!processedVouchers.contains(idVoucher)) {
            Voucher voucher = voucherRepository.findById(idVoucher)
                    .orElseThrow(() -> new RuntimeException("Voucher không tồn tại: " + idVoucher));
            if (voucher.getSoLuong() <= 0) {
                throw new RuntimeException("Voucher " + voucher.getTenVoucher() + " đã hết số lượng");
            }
            voucher.setSoLuong(voucher.getSoLuong() - 1);
            voucherRepository.save(voucher);
            processedVouchers.add(idVoucher);
            System.out.println("capNhatHoaDon: Trừ voucher idVoucher=" + idVoucher + ", còn lại=" + voucher.getSoLuong());
        }
    }

    @Transactional
    public void deleteHoaDon(Integer id) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));

        // Hoàn lại số lượng sản phẩm chi tiết
        for (HoaDonChiTiet ct : hoaDon.getChiTietSanPham()) {
            SanPhamChiTiet spct = sanPhamChiTietRepository.findById(ct.getSanPhamct().getId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm chi tiết không tồn tại: " + ct.getSanPhamct().getId()));
            spct.setSoLuong(spct.getSoLuong() + ct.getSoLuong());
            sanPhamChiTietRepository.save(spct);
            System.out.println("deleteHoaDon: Đã hoàn lại số lượng sản phẩm idSPCT: " + ct.getSanPhamct().getId() + ", còn lại: " + spct.getSoLuong());
        }

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
        hoaDon.setIdNhanVien(dto.getIdNhanVien());
        hoaDon.setNgayTao(dto.getNgayTao());
        hoaDon.setNgaySua(dto.getNgaySua());
        hoaDon.setTongTien(dto.getTongTien());
        hoaDon.setTrangThai(dto.getTrangThai());
        hoaDon.setLoaiHoaDon(dto.getLoaiHoaDon());
        return hoaDon;
    }

    public void updateOrderStatus(Integer idHD, String newStatus, String ghiChu) {
        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với ID: " + idHD));
        hoaDon.setTrangThai(newStatus);
        hoaDon.setNgaySua(LocalDateTime.now());
        hoaDonRepository.save(hoaDon);
    }
}