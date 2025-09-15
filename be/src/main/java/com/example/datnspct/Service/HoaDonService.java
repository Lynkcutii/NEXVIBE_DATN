package com.example.datnspct.Service;

import com.example.datnspct.Model.*;
import com.example.datnspct.Repository.*;
import com.example.datnspct.dto.HoaDonChiTietDTO;
import com.example.datnspct.dto.HoaDonDTO;
import com.example.datnspct.dto.OrderRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.datnspct.dto.OrderDetailResponseDTO;
import jakarta.persistence.EntityNotFoundException;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Autowired
    private GioHangCTRepository gioHangCTRepository;

    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired private DiaChiKhachHangRepository diaChiKhachHangRepository;

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
        if (hoaDon == null) return null;
        HoaDonDTO dto = new HoaDonDTO();
        dto.setIdHD(hoaDon.getIdHD());
        dto.setMaHD(hoaDon.getMaHD());
        dto.setIdKhachHang(hoaDon.getIdKhachHang());
        dto.setCustomerName(hoaDon.getKhachHang() != null ? hoaDon.getKhachHang().getTenKH() : "N/A");
        dto.setIdNhanVien(hoaDon.getIdNhanVien());
        dto.setNgayTao(hoaDon.getNgayTao());
        dto.setNgaySua(hoaDon.getNgaySua());
        dto.setTongTien(hoaDon.getTongTien());
        dto.setNgaySua(hoaDon.getNgaySua());
        dto.setTrangThai(hoaDon.getTrangThai());
        if (hoaDon.getKhachHang() != null) {
            dto.setTenKH(hoaDon.getKhachHang().getTenKH());
        }

        if (hoaDon.getDiaChiGiao() != null) {
            DiaChiKhachHang diaChi = hoaDon.getDiaChiGiao();
            dto.setSdt(diaChi.getSoDienThoai());
            String fullAddress = String.join(", ",
                    diaChi.getDiaChiCuThe(), diaChi.getPhuongXa(), diaChi.getTinhThanh());
            dto.setDiaChiGiao(fullAddress);
        } else {
            dto.setSdt("N/A");
            dto.setDiaChiGiao("Chưa có địa chỉ");
        }
        return dto;
    }

    // Tạo hóa đơn từ OrderRequestDTO
    @Transactional
    public HoaDon createHoaDon(OrderRequestDTO request) {
        List<String> errors = new ArrayList<>();
        System.out.println("createHoaDon: Starting with request: " + request);

        // Kiểm tra khách hàng
        Optional<KhachHang> khachHangOpt = khachHangRepository.findByIdTK(request.getIdTK());
        if (!khachHangOpt.isPresent()) {
            errors.add("Không tìm thấy khách hàng với idTK: " + request.getIdTK());
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

        // Lấy thông tin khách hàng và phương thức thanh toán
        KhachHang khachHang = khachHangOpt.get();
        PhuongTT phuongTT = phuongTTOpt.get();
        // Tìm địa chỉ giao hàng mà khách đã chọn
        Integer idDiaChiDaChon = request.getShippingInfo().getAddressId();
        DiaChiKhachHang diaChiGiaoHang = diaChiKhachHangRepository.findById(idDiaChiDaChon)
                .orElseThrow(() -> new RuntimeException("Địa chỉ không hợp lệ"));

        // Tạo hóa đơn
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHD("HD" + UUID.randomUUID().toString().substring(0, 8));
        hoaDon.setIdKhachHang(khachHang.getIdKH());
        hoaDon.setIdNhanVien(null);
        hoaDon.setNgayTao(LocalDateTime.now());
        hoaDon.setNgaySua(LocalDateTime.now());
        hoaDon.setTongTien(calculatedTotal);
        hoaDon.setTrangThai("CHO_XAC_NHAN");
        hoaDon.setDiaChiGiao(diaChiGiaoHang);
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
        return hoaDon;
    }
    public List<HoaDonDTO> findByKhachHangId(Integer idKH) {

        List<HoaDon> hoaDons = hoaDonRepository.findByIdKhachHang(idKH);

        return hoaDons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public OrderDetailResponseDTO getHoaDonDetail(Integer idHD) {
        // Lấy thông tin Hóa Đơn
        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        HoaDonDTO hoaDonDTO = convertToDTO(hoaDon);

        // --- DÒNG CODE BỊ LỖI CỦA BẠN ĐƯỢC ĐẶT ĐÚNG VỊ TRÍ Ở ĐÂY ---
        List<HoaDonChiTiet> chiTietList = hoaDonChiTietRepository.findByHoaDon_IdHD(idHD);
        List<HoaDonChiTietDTO> chiTietDTOList = chiTietList.stream()
                .map(this::convertChiTietToDto)
                .collect(Collectors.toList());

        // Đóng gói vào DTO và trả về
        OrderDetailResponseDTO response = new OrderDetailResponseDTO();
        response.setHoaDon(hoaDonDTO);
        response.setChiTiet(chiTietDTOList);

        return response;
    }
    private HoaDonChiTietDTO convertChiTietToDto(HoaDonChiTiet chiTiet) {
        if (chiTiet == null) return null;
        HoaDonChiTietDTO dto = new HoaDonChiTietDTO();

        dto.setIdHDCT(chiTiet.getIdHDCT());
        dto.setSoLuong(chiTiet.getSoLuong());
        dto.setDonGia(chiTiet.getDonGia());

        SanPhamChiTiet spct = chiTiet.getSanPhamct();

        // Đặt giá trị mặc định để tránh null
        dto.setTenSanPham("Sản phẩm không có tên");
        dto.setHinhAnh("https://placehold.co/100");
        dto.setTenMauSac("N/A");
        dto.setTenSize("N/A");

        if (spct != null) {
            // KIỂM TRA KỸ HƠN
            if (spct.getSanPham() != null && spct.getSanPham().getTenSP() != null) {
                dto.setTenSanPham(spct.getSanPham().getTenSP());
            }

            if (spct.getMauSac() != null) {
                dto.setTenMauSac(spct.getMauSac().getTenMauSac());
            }

            if (spct.getSize() != null) {
                dto.setTenSize(spct.getSize().getTenSize());
            }

            try {
                if (spct.getImages() != null && !spct.getImages().isEmpty()) {
                    dto.setHinhAnh(spct.getImages().get(0).getLink());
                }
            } catch (Exception e) {
                System.err.println("Lỗi Lazy Loading khi lấy ảnh cho SPCT ID: " + spct.getId());
            }
        }

        return dto;
    }
    @Transactional
    public void cancelOrder(Integer idHD) {
        // 1. Tìm hóa đơn
        // Dùng EntityNotFoundException cho ngữ nghĩa rõ ràng hơn
        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hóa đơn với ID: " + idHD));

        // 2. Kiểm tra trạng thái
        // Dùng IllegalArgumentException cho lỗi dữ liệu đầu vào/trạng thái không hợp lệ
        if (!"CHO_XAC_NHAN".equalsIgnoreCase(hoaDon.getTrangThai())) {
            throw new IllegalArgumentException("Không thể hủy đơn hàng ở trạng thái: " + hoaDon.getTrangThai());
        }


        // 3. Cập nhật trạng thái hóa đơn bằng String
        hoaDon.setTrangThai("DA_HUY");
        hoaDon.setNgaySua(LocalDateTime.now());
        hoaDonRepository.save(hoaDon);

        // 4. Hoàn trả lại số lượng sản phẩm vào kho (Giữ nguyên logic này)
        List<HoaDonChiTiet> chiTietList = hoaDonChiTietRepository.findByHoaDon_IdHD(idHD);
        for (HoaDonChiTiet chiTiet : chiTietList) {
            SanPhamChiTiet spct = chiTiet.getSanPhamct();
            if (spct != null && chiTiet.getSoLuong() != null) {
                int currentStock = spct.getSoLuong();
                int returnedQuantity = chiTiet.getSoLuong();
                spct.setSoLuong(currentStock + returnedQuantity);
                sanPhamChiTietRepository.save(spct);
            }
        }
    }
    // === PHƯƠNG THỨC MỚI ĐỂ THAY ĐỔI ĐỊA CHỈ ĐƠN HÀNG ===
    // =======================================================

    @Transactional
    public void changeOrderAddress(Integer idHD, Integer newAddressId) {
        // 1. Tìm hóa đơn
        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                // SỬA LẠI: Dùng EntityNotFoundException
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hóa đơn với ID: " + idHD));

        // 2. Kiểm tra xem đơn hàng có được phép thay đổi địa chỉ không
        if (!"CHO_XAC_NHAN".equalsIgnoreCase(hoaDon.getTrangThai())) {
            // SỬA LẠI: Dùng IllegalArgumentException cho lỗi trạng thái
            throw new IllegalArgumentException("Không thể thay đổi địa chỉ cho đơn hàng ở trạng thái: " + hoaDon.getTrangThai());
        }

        // 3. Tìm địa chỉ mới
        DiaChiKhachHang newAddress = diaChiKhachHangRepository.findById(newAddressId)
                // SỬA LẠI: Dùng EntityNotFoundException
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy địa chỉ với ID: " + newAddressId));

        // 4. (Tùy chọn) Kiểm tra xem địa chỉ mới có thuộc về đúng khách hàng không
        if (!newAddress.getKhachHang().getIdKH().equals(hoaDon.getKhachHang().getIdKH())) {
            // SỬA LẠI: Dùng IllegalArgumentException cho lỗi logic nghiệp vụ
            throw new IllegalArgumentException("Địa chỉ mới không thuộc về khách hàng của đơn hàng này.");
        }

        // 5. Cập nhật hóa đơn với địa chỉ mới
        hoaDon.setDiaChiGiao(newAddress);
        hoaDon.setNgaySua(LocalDateTime.now());
        hoaDonRepository.save(hoaDon);
    }
}