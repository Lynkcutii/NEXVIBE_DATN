package com.example.datnspct.Service;

import com.example.datnspct.Model.SanPham;
import com.example.datnspct.Model.SanPhamChiTiet;
import com.example.datnspct.Repository.SanPhamChiTietRepository;
import com.example.datnspct.Repository.SanPhamRepository;
import com.example.datnspct.dto.SanPhamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SanPhamService {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    // Ánh xạ thủ công: Từ Entity sang DTO
    private SanPhamDTO chuyenSangDTO(SanPham sanPham) {
        SanPhamDTO dto = new SanPhamDTO();
        dto.setIdSP(sanPham.getId());
        dto.setMaSP(sanPham.getMaSP());
        dto.setTenSP(sanPham.getTenSP());
        dto.setNgayTao(sanPham.getNgayTao());
        // Tính tổng số lượng từ SanPhamChiTiet
        Integer tongSoLuong = sanPhamChiTietRepository.findBySanPhamId(sanPham.getId())
                .stream()
                .mapToInt(SanPhamChiTiet::getSoLuong)
                .sum();
        dto.setTongSoLuongSanPham(tongSoLuong);
        return dto;
    }

    // Ánh xạ thủ công: Từ DTO sang Entity
    private SanPham chuyenSangEntity(SanPhamDTO dto) {
        SanPham sanPham = new SanPham();
        sanPham.setId(dto.getIdSP());
        sanPham.setMaSP(dto.getMaSP());
        sanPham.setTenSP(dto.getTenSP());
        sanPham.setNgayTao(dto.getNgayTao());
        sanPham.setTrangThai(true); // Mặc định true
        return sanPham;
    }

    // Tạo mới
    public SanPhamDTO taoSanPham(SanPhamDTO dto) {
        SanPham sanPham = new SanPham();
        sanPham.setTenSP(dto.getTenSP());
        sanPham.setTrangThai(true); // Trạng thái mặc định là true
        sanPham.setMaSP(generateMaSP()); // Tự động sinh maSP
        sanPham.setNgayTao(new java.util.Date()); // Gán ngày tạo hiện tại
        sanPham.setTongSoLuongSanPham(0); // Khởi tạo bằng 0, sẽ cập nhật sau khi thêm SanPhamChiTiet
        SanPham sanPhamDaLuu = sanPhamRepository.save(sanPham);
        return chuyenSangDTO(sanPhamDaLuu);
    }

    // Sinh mã SP tự động (SP001, SP002, ...)
    private String generateMaSP() {
        Long count = sanPhamRepository.count();
        return String.format("SP%03d", count + 1);
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

    // Lấy tất cả với phân trang và bộ lọc
    public Page<SanPhamDTO> layTatCaSanPham(Pageable pageable, String keyword, Boolean status) {
        Page<SanPham> sanPhamPage = sanPhamRepository.findByFilters(keyword, status, pageable);
        return sanPhamPage.map(this::chuyenSangDTO);
    }

    // Cập nhật
    public SanPhamDTO capNhatSanPham(Integer id, SanPhamDTO dto) {
        SanPham sanPham = sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        sanPham.setTenSP(dto.getTenSP());
        sanPham.setTrangThai(true); // Mặc định true
        // Tính lại tổng số lượng
        Integer tongSoLuong = sanPhamChiTietRepository.findBySanPhamId(id)
                .stream()
                .mapToInt(SanPhamChiTiet::getSoLuong)
                .sum();
        sanPham.setTongSoLuongSanPham(tongSoLuong);
        SanPham sanPhamDaCapNhat = sanPhamRepository.save(sanPham);
        return chuyenSangDTO(sanPhamDaCapNhat);
    }

    // Xóa
    public void xoaSanPham(Integer id) {
        SanPham sanPham = sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        sanPhamRepository.delete(sanPham);
    }

    public Page<SanPhamDTO> findAll(String tenSP, Boolean status, Pageable pageable) {
        return sanPhamRepository.findByTenSPContainingAndTrangThai(tenSP != null ? tenSP : "", status, pageable)
                .map(this::chuyenSangDTO);
    }
}