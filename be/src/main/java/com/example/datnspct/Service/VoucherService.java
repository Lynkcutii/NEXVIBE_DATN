package com.example.datnspct.Service;

import com.example.datnspct.Model.SanPhamChiTiet;
import com.example.datnspct.Model.Voucher;
import com.example.datnspct.Model.VoucherSP;
import com.example.datnspct.Repository.SanPhamChiTietRepository;
import com.example.datnspct.dto.VoucherDTO;
import com.example.datnspct.Repository.VoucherRepository;
import com.example.datnspct.Repository.VoucherSPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private VoucherSPRepository voucherSPRepository;

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    public String generateVoucherCode() {
        //        LocalDateTime today = LocalDateTime.now();
//
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//        String datePart = today.format(dateTimeFormatter);
        long count = voucherRepository.count();
        return String.format("VOUCHER%03d", count + 1);
    }

    public List<VoucherDTO> getApplicableVouchersForProducts(List<Integer> idSPCTs) {
        Date now = new Date();
        List<Voucher> vouchers = voucherRepository.findAllByTrangThaiAndSoLuongGreaterThan((byte) 1, 0);
        if (vouchers == null) {
            return Collections.emptyList();
        }
        return vouchers.stream()
                .filter(v -> now.after(v.getNgayBatDau()) && now.before(v.getNgayKetThuc()))
                .filter(v -> {
                    List<VoucherSP> voucherSPs = voucherSPRepository.findByVoucherIdVoucherAndTrangThai(v.getIdVoucher(), (byte) 1);
                    return voucherSPs.isEmpty() || voucherSPs.stream().anyMatch(vsp -> idSPCTs.contains(vsp.getSanPhamCT().getId()));
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<VoucherDTO> getAllVouchers() {
        return voucherRepository.findAllByTrangThai((byte) 1)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public VoucherDTO getVoucherById(Integer id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Voucher không tồn tại"));
        return convertToDTO(voucher);
    }

    public void updateVoucherQuantity(Integer id, Integer soLuong) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Voucher không tồn tại"));
        voucher.setSoLuong(soLuong);
        voucherRepository.save(voucher);
    }

    private VoucherDTO convertToDTO(Voucher voucher) {
        VoucherDTO dto = new VoucherDTO();
        dto.setId(voucher.getIdVoucher());
        dto.setMaVoucher(voucher.getMaVoucher());
        dto.setTenVoucher(voucher.getTenVoucher());
        dto.setHinhThucGiam(voucher.getHinhThucGiam());
        dto.setMucGiam(voucher.getMucGiam());
        dto.setGiamToiDa(voucher.getGiamToiDa());
        dto.setDonGiaKhiGiam(voucher.getDonGiaKhiGiam());
        dto.setGiaGiam(voucher.getGiaGiam());
        dto.setSoLuong(voucher.getSoLuong());
        dto.setNgayBatDau(voucher.getNgayBatDau());
        dto.setNgayKetThuc(voucher.getNgayKetThuc());
        dto.setTrangThai(voucher.getTrangThai() == 1);

        // Lấy cả mã và id sản phẩm chi tiết
        List<VoucherSP> voucherSPs = voucherSPRepository.findByVoucherIdVoucherAndTrangThai(voucher.getIdVoucher(), (byte) 1);

        List<String> applicableProductCodes = voucherSPs.stream()
                .map(vsp -> vsp.getSanPhamCT().getMaSPCT())
                .collect(Collectors.toList());
        dto.setApplicableProductCodes(applicableProductCodes.isEmpty() ? Collections.emptyList() : applicableProductCodes);

        List<Integer> applicableProductIds = voucherSPs.stream()
                .map(vsp -> vsp.getSanPhamCT().getId())
                .collect(Collectors.toList());
        dto.setApplicableProductIds(applicableProductIds.isEmpty() ? Collections.emptyList() : applicableProductIds);

        return dto;
    }

    public VoucherDTO createVoucher(VoucherDTO dto) {
        if (dto.getMaVoucher() == null || dto.getMaVoucher().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã voucher không được để trống");
        }
        if (dto.getHinhThucGiam() == null || !List.of("PHAN_TRAM", "TIEN_MAT").contains(dto.getHinhThucGiam())) {
            throw new IllegalArgumentException("Hình thức giảm không hợp lệ");
        }
        if (dto.getMucGiam() == null || dto.getMucGiam().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Mức giảm phải lớn hơn 0");
        }
        if (dto.getNgayBatDau() == null || dto.getNgayKetThuc() == null || dto.getNgayKetThuc().before(dto.getNgayBatDau())) {
            throw new IllegalArgumentException("Ngày bắt đầu và kết thúc không hợp lệ");
        }

        Voucher voucher = new Voucher();
        voucher.setMaVoucher(dto.getMaVoucher().trim());
        voucher.setTenVoucher(dto.getTenVoucher() != null ? dto.getTenVoucher() : "");
        voucher.setHinhThucGiam(dto.getHinhThucGiam());
        voucher.setMucGiam(dto.getMucGiam());
        voucher.setGiamToiDa(dto.getGiamToiDa());
        voucher.setDonGiaKhiGiam(dto.getDonGiaKhiGiam());
        voucher.setGiaGiam(dto.getGiaGiam());
        voucher.setSoLuong(dto.getSoLuong() != null ? dto.getSoLuong() : 0);
        voucher.setNgayBatDau(dto.getNgayBatDau());
        voucher.setNgayKetThuc(dto.getNgayKetThuc());
        voucher.setTrangThai((byte) (dto.getTrangThai() != null && dto.getTrangThai() ? 1 : 0));

        Voucher savedVoucher = voucherRepository.save(voucher);

        if (dto.getApplicableProductCodes() != null && !dto.getApplicableProductCodes().isEmpty()) {
            for (String maSPCT : dto.getApplicableProductCodes()) {
                SanPhamChiTiet spct = sanPhamChiTietRepository.findByMaSPCT(maSPCT)
                        .orElseThrow(() -> new IllegalArgumentException("Mã sản phẩm chi tiết " + maSPCT + " không tồn tại"));
                VoucherSP voucherSP = new VoucherSP();
                voucherSP.setVoucher(savedVoucher);
                voucherSP.setSanPhamCT(spct);
                voucherSP.setTrangThai((byte) 1);
                voucherSPRepository.save(voucherSP);
            }
        }

        return convertToDTO(savedVoucher);
    }

    public VoucherDTO updateVoucher(Integer id, VoucherDTO dto) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Voucher không tồn tại"));

        if (dto.getMaVoucher() == null || dto.getMaVoucher().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã voucher không được để trống");
        }
        if (dto.getHinhThucGiam() == null || !List.of("PHAN_TRAM", "TIEN_MAT").contains(dto.getHinhThucGiam())) {
            throw new IllegalArgumentException("Hình thức giảm không hợp lệ");
        }
        if (dto.getMucGiam() == null || dto.getMucGiam().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Mức giảm phải lớn hơn 0");
        }
        if (dto.getNgayBatDau() == null || dto.getNgayKetThuc() == null || dto.getNgayKetThuc().before(dto.getNgayBatDau())) {
            throw new IllegalArgumentException("Ngày bắt đầu và kết thúc không hợp lệ");
        }

        voucher.setMaVoucher(dto.getMaVoucher().trim());
        voucher.setTenVoucher(dto.getTenVoucher() != null ? dto.getTenVoucher() : "");
        voucher.setHinhThucGiam(dto.getHinhThucGiam());
        voucher.setMucGiam(dto.getMucGiam());
        voucher.setGiamToiDa(dto.getGiamToiDa());
        voucher.setDonGiaKhiGiam(dto.getDonGiaKhiGiam());
        voucher.setGiaGiam(dto.getGiaGiam());
        voucher.setSoLuong(dto.getSoLuong() != null ? dto.getSoLuong() : 0);
        voucher.setNgayBatDau(dto.getNgayBatDau());
        voucher.setNgayKetThuc(dto.getNgayKetThuc());
        voucher.setTrangThai((byte) (dto.getTrangThai() != null && dto.getTrangThai() ? 1 : 0));

        Voucher updatedVoucher = voucherRepository.save(voucher);

        voucherSPRepository.deleteByVoucherIdVoucher(id);
        if (dto.getApplicableProductCodes() != null && !dto.getApplicableProductCodes().isEmpty()) {
            for (String maSPCT : dto.getApplicableProductCodes()) {
                SanPhamChiTiet spct = sanPhamChiTietRepository.findByMaSPCT(maSPCT)
                        .orElseThrow(() -> new IllegalArgumentException("Mã sản phẩm chi tiết " + maSPCT + " không tồn tại"));
                VoucherSP voucherSP = new VoucherSP();
                voucherSP.setVoucher(updatedVoucher);
                voucherSP.setSanPhamCT(spct);
                voucherSP.setTrangThai((byte) 1);
                voucherSPRepository.save(voucherSP);
            }
        }

        return convertToDTO(updatedVoucher);
    }
}