package com.example.datnspct.Service;

import com.example.datnspct.Model.SanPhamChiTiet;
import com.example.datnspct.Repository.SanPhamChiTietRepository;
import com.example.datnspct.dto.SanPhamChiTietDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SanPhamChiTietService {

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    public SanPhamChiTietDTO taoSanPhamChiTiet(SanPhamChiTietDTO dto) {
        SanPhamChiTiet spct = new SanPhamChiTiet();
        // Ánh xạ DTO sang entity
        spct.setMaSPCT(dto.getMaSPCT());
        // Thêm logic để tìm và gán SanPham, DanhMuc, ThuongHieu, MauSac, ChatLieu, Size
        SanPhamChiTiet saved = sanPhamChiTietRepository.save(spct);
        return convertToDTO(saved);
    }

    public SanPhamChiTietDTO laySanPhamChiTietTheoId(Integer id) {
        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm chi tiết không tồn tại"));
        return convertToDTO(spct);
    }

    public List<SanPhamChiTietDTO> laySanPhamChiTietTheoSanPham(Integer idSP) {
        List<SanPhamChiTiet> spctList = sanPhamChiTietRepository.findBySanPhamId(idSP);
        return spctList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<SanPhamChiTietDTO> layTatCaSanPhamChiTiet() {
        List<SanPhamChiTiet> spctList = sanPhamChiTietRepository.findAll();
        return spctList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public SanPhamChiTietDTO capNhatSanPhamChiTiet(Integer id, SanPhamChiTietDTO dto) {
        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm chi tiết không tồn tại"));
        // Ánh xạ DTO sang entity
        spct.setMaSPCT(dto.getMaSPCT());
        spct.setGia(dto.getGia());
        // Thêm logic để cập nhật các trường khác
        SanPhamChiTiet updated = sanPhamChiTietRepository.save(spct);
        return convertToDTO(updated);
    }

    public void xoaSanPhamChiTiet(Integer id) {
        sanPhamChiTietRepository.deleteById(id);
    }

    public Page<SanPhamChiTietDTO> findWithFilters(String keyword, String danhMuc, String thuongHieu,
                                                   String mauSac, String chatLieu, String size,
                                                   BigDecimal minPrice, BigDecimal maxPrice,
                                                   Pageable pageable) {
        List<Integer> danhMucIds = danhMuc != null && !danhMuc.isEmpty() ?
                Arrays.stream(danhMuc.split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList()) : null;

        Page<SanPhamChiTiet> page = sanPhamChiTietRepository.findByFilters(
                keyword, danhMucIds, thuongHieu, mauSac, chatLieu, size,
                minPrice != null ? minPrice : BigDecimal.ZERO,
                maxPrice != null ? maxPrice : new BigDecimal("2000000"),
                pageable);
        return page.map(this::convertToDTO);
    }

    private SanPhamChiTietDTO convertToDTO(SanPhamChiTiet spct) {
        SanPhamChiTietDTO dto = new SanPhamChiTietDTO();
        dto.setId(spct.getId());
        dto.setIdSP(spct.getSanPham() != null ? spct.getSanPham().getId() : null);
        dto.setMaSPCT(spct.getMaSPCT());
        dto.setTenSP(spct.getSanPham() != null ? spct.getSanPham().getTenSP() : null);
        dto.setGia(spct.getGia());
        dto.setSoLuong(spct.getSoLuong());
        dto.setMoTa(spct.getMoTa());
        dto.setTrangThai(spct.getTrangThai());
        dto.setTenDanhMuc(spct.getDanhMuc() != null ? spct.getDanhMuc().getTenDM() : null);
        dto.setTenThuongHieu(spct.getThuongHieu() != null ? spct.getThuongHieu().getTenThuongHieu() : null);
        dto.setTenMauSac(spct.getMauSac() != null ? spct.getMauSac().getTenMauSac() : null);
        dto.setTenChatLieu(spct.getChatLieu() != null ? spct.getChatLieu().getTenChatLieu() : null);
        dto.setTenSize(spct.getSize() != null ? spct.getSize().getTenSize() : null);
        return dto;
    }
}