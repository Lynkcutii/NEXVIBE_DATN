package com.example.datnspct.Service;

import com.example.datnspct.Model.Voucher;
import com.example.datnspct.Repository.VoucherRepository;
import com.example.datnspct.dto.VoucherDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    // Chuyển từ Entity sang DTO
    private VoucherDTO toDTO(Voucher voucher) {
        VoucherDTO dto = new VoucherDTO();
        dto.setId(voucher.getIdVoucher());
        dto.setCode(voucher.getMaVoucher());
        dto.setName(voucher.getTenVoucher());
        dto.setDescription("Giảm " + (voucher.getMucGiam() != null ? voucher.getMucGiam() + "%" : voucher.getGiaGiam() + "đ"));
        dto.setType(voucher.getMucGiam() != null ? "percentage" : "fixed");
        dto.setValue(voucher.getMucGiam() != null ? voucher.getMucGiam() : voucher.getGiaGiam());
        dto.setMinOrder(BigDecimal.ZERO); // Có thể thêm logic để lấy giá trị đơn hàng tối thiểu từ bảng khác
        dto.setEndDate(voucher.getNgayKetThuc());
        return dto;
    }

    // Lấy danh sách voucher áp dụng được cho danh sách IdSPCT
    public List<VoucherDTO> getApplicableVouchersForProducts(List<Integer> idSPCTs) {
        List<VoucherDTO> result = new ArrayList<>();
        Date currentDate = new Date();

        for (Integer idSPCT : idSPCTs) {
            List<Voucher> vouchers = voucherRepository.findApplicableVouchersBySanPhamChiTietId(idSPCT, currentDate);
            result.addAll(vouchers.stream().map(this::toDTO).collect(Collectors.toList()));
        }

        // Loại bỏ voucher trùng lặp
        return result.stream()
                .distinct()
                .collect(Collectors.toList());
    }
}