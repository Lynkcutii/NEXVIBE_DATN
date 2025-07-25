package com.example.datnspct.Service;

import com.example.datnspct.dto.SanPhamDTO;
import com.example.datnspct.Model.SanPham;
import com.example.datnspct.Repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SanPhamService {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    // Ánh xạ thủ công: Từ Entity sang DTO
    private SanPhamDTO chuyenSangDTO(SanPham sanPham) {
        SanPhamDTO dto = new SanPhamDTO();
        dto.setIdSP(sanPham.getIdSP());
        dto.setMaSP(sanPham.getMaSP());
        dto.setTenSP(sanPham.getTenSP());
        dto.setTrangThai(sanPham.getTrangThai());
        return dto;
    }

    // Ánh xạ thủ công: Từ DTO sang Entity
    private SanPham chuyenSangEntity(SanPhamDTO dto) {
        SanPham sanPham = new SanPham();
        sanPham.setIdSP(dto.getIdSP());
        sanPham.setMaSP(dto.getMaSP());
        sanPham.setTenSP(dto.getTenSP());
        sanPham.setTrangThai(dto.getTrangThai());
        return sanPham;
    }

    // Tạo mới
    public SanPhamDTO taoSanPham(SanPhamDTO dto) {
        SanPham sanPham = chuyenSangEntity(dto);
        SanPham sanPhamDaLuu = sanPhamRepository.save(sanPham);
        return chuyenSangDTO(sanPhamDaLuu);
    }

    // Lấy theo ID
    public SanPhamDTO laySanPhamTheoId(Integer id) {
        SanPham sanPham = sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        return chuyenSangDTO(sanPham);
    }

    // Lấy tất cả
    public List<SanPhamDTO> layTatCaSanPham() {
        return sanPhamRepository.findAll().stream()
                .map(this::chuyenSangDTO)
                .collect(Collectors.toList());
    }

    // Cập nhật
    public SanPhamDTO capNhatSanPham(Integer id, SanPhamDTO dto) {
        SanPham sanPham = sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        sanPham.setMaSP(dto.getMaSP());
        sanPham.setTenSP(dto.getTenSP());
        sanPham.setTrangThai(dto.getTrangThai());
        SanPham sanPhamDaCapNhat = sanPhamRepository.save(sanPham);
        return chuyenSangDTO(sanPhamDaCapNhat);
    }

    // Xóa
    public void xoaSanPham(Integer id) {
        SanPham sanPham = sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        sanPhamRepository.delete(sanPham);
    }
}