package com.example.datnspct.Service;

import com.example.datnspct.dto.KhachHangDTO;
import com.example.datnspct.Model.KhachHang;
import com.example.datnspct.Model.TaiKhoan;
import com.example.datnspct.Repository.KhachHangRepository;
import com.example.datnspct.Repository.TaiKhoanRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class KhachHangService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    // Ánh xạ thủ công: Từ Entity sang DTO
    private KhachHangDTO chuyenSangDTO(KhachHang khachHang) {
        KhachHangDTO dto = new KhachHangDTO();
        dto.setIdKH(khachHang.getIdKH());
        dto.setMaKH(khachHang.getMaKH());
        dto.setTenKH(khachHang.getTenKH());
        dto.setGioiTinh(khachHang.getGioiTinh());
        dto.setSdt(khachHang.getSdt());
        dto.setDiaChi(khachHang.getDiaChi());
        dto.setIdTK(khachHang.getTaiKhoan() != null ? khachHang.getTaiKhoan().getIdTK() : null);
        dto.setTrangThai(khachHang.getTrangThai());
        return dto;
    }

    // Ánh xạ thủ công: Từ DTO sang Entity
    private KhachHang chuyenSangEntity(KhachHangDTO dto) {
        KhachHang khachHang = new KhachHang();
        khachHang.setIdKH(dto.getIdKH());
        khachHang.setMaKH(dto.getMaKH());
        khachHang.setTenKH(dto.getTenKH());
        khachHang.setGioiTinh(dto.getGioiTinh());
        khachHang.setSdt(dto.getSdt());
        khachHang.setDiaChi(dto.getDiaChi());
        if (dto.getIdTK() != null) {
            TaiKhoan taiKhoan = taiKhoanRepository.findById(dto.getIdTK())
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
            khachHang.setTaiKhoan(taiKhoan);
        }
        khachHang.setTrangThai(dto.getTrangThai());
        return khachHang;
    }

    // Tạo mới
    public KhachHangDTO taoKhachHang(KhachHangDTO dto) {
        KhachHang khachHang = chuyenSangEntity(dto);
        KhachHang khachHangDaLuu = khachHangRepository.save(khachHang);
        return chuyenSangDTO(khachHangDaLuu);
    }

    // Lấy theo ID
    public KhachHangDTO layKhachHangTheoId(Integer id) {
        KhachHang khachHang = khachHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tìm thấy"));
        KhachHangDTO dto = new KhachHangDTO();
        dto.setIdKH(khachHang.getIdKH());
        dto.setMaKH(khachHang.getMaKH());
        dto.setTenKH(khachHang.getTenKH());
        dto.setGioiTinh(khachHang.getGioiTinh());
        dto.setSdt(khachHang.getSdt());
        dto.setDiaChi(khachHang.getDiaChi());
        dto.setIdTK(khachHang.getTaiKhoan() != null ? khachHang.getTaiKhoan().getIdTK() : null);
        dto.setTrangThai(khachHang.getTrangThai());
        return dto;
    }

    // Lấy tất cả
    public Page<KhachHangDTO> layTatCaKhachHang(Pageable pageable) {
        return khachHangRepository.findAll(pageable).map(this::chuyenSangDTO);
    }
    public Page<KhachHangDTO> searchKhachHang(String keyword, Pageable pageable) {
        return khachHangRepository.findByTenKHContainingIgnoreCaseOrSdtContaining(keyword, keyword, pageable)
                .map(this::chuyenSangDTO);
    }

    // Cập nhật
    public KhachHangDTO capNhatKhachHang(Integer id, KhachHangDTO dto) {
        KhachHang khachHang = khachHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
        khachHang.setMaKH(dto.getMaKH());
        khachHang.setTenKH(dto.getTenKH());
        khachHang.setGioiTinh(dto.getGioiTinh());
        khachHang.setSdt(dto.getSdt());
        khachHang.setDiaChi(dto.getDiaChi());
        if (dto.getIdTK() != null) {
            TaiKhoan taiKhoan = taiKhoanRepository.findById(dto.getIdTK())
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
            khachHang.setTaiKhoan(taiKhoan);
        } else {
            khachHang.setTaiKhoan(null);
        }
        khachHang.setTrangThai(dto.getTrangThai());
        KhachHang khachHangDaCapNhat = khachHangRepository.save(khachHang);
        return chuyenSangDTO(khachHangDaCapNhat);
    }

    // Xóa
    public void xoaKhachHang(Integer id) {
        KhachHang khachHang = khachHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
        khachHangRepository.delete(khachHang);
    }
}