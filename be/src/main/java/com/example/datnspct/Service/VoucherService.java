package com.example.datnspct.Service;

import com.example.datnspct.Model.Voucher;
import com.example.datnspct.Model.VoucherSP;
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

    public List<VoucherDTO> getApplicableVouchersForProducts(List<Integer> idSPCTs) {
        Date now = new Date();
        List<Voucher> vouchers = voucherRepository.findAllByTrangThaiAndSoLuongGreaterThan((byte) 1, 0);
        if (vouchers == null) {
            return Collections.emptyList(); // Trả về mảng rỗng nếu vouchers là null
        }
        return vouchers.stream()
                .filter(v -> now.after(v.getNgayBatDau()) && now.before(v.getNgayKetThuc()))
                .filter(v -> {
                    List<VoucherSP> voucherSPs = voucherSPRepository.findByVoucherIdVoucherAndTrangThai(v.getIdVoucher(), (byte) 1);
                    return voucherSPs.stream().anyMatch(vsp -> idSPCTs.contains(vsp.getSanPhamCT().getId()));
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
        // Giả định giaTriDonHangToiThieu là 0 nếu không có yêu cầu cụ thể
        dto.setGiaTriDonHangToiThieu(BigDecimal.ZERO);
        dto.setSoLuong(voucher.getSoLuong());
        dto.setNgayBatDau(voucher.getNgayBatDau());
        dto.setNgayKetThuc(voucher.getNgayKetThuc());
        dto.setTrangThai(voucher.getTrangThai() == 1);
        List<Integer> applicableProductIds = voucherSPRepository.findByVoucherIdVoucherAndTrangThai(voucher.getIdVoucher(), (byte) 1)
                .stream()
                .map(vsp -> vsp.getSanPhamCT().getId())
                .collect(Collectors.toList());
        dto.setApplicableProductIds(applicableProductIds);
        return dto;
    }
}