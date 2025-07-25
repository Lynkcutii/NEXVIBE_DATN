package com.example.datnspct.Service;

import com.example.datnspct.dto.GioHangDTO;
import com.example.datnspct.Model.GioHang;
import com.example.datnspct.Model.KhachHang;
import com.example.datnspct.Model.TaiKhoan;
import com.example.datnspct.Repository.GioHangRepository;
import com.example.datnspct.Repository.KhachHangRepository;
import com.example.datnspct.Repository.TaiKhoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GioHangService {
    @Autowired
    private GioHangRepository gioHangRepository;
    @Autowired
    private KhachHangRepository khachHangRepository;
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    // Entity -> DTO
    private GioHangDTO toDTO(GioHang entity) {
        GioHangDTO dto = new GioHangDTO();
        dto.setIdGH(entity.getIdGH());
        dto.setMaGH(entity.getMaGH());
        dto.setIdKH(entity.getKhachHang() != null ? entity.getKhachHang().getIdKH() : null);
        dto.setTrangThai(entity.getTrangThai());
        dto.setNgayTao(entity.getNgayTao());
        dto.setNgaySua(entity.getNgaySua());
        dto.setIdTK(entity.getTaiKhoan() != null ? entity.getTaiKhoan().getIdTK() : null);
        return dto;
    }

    // DTO -> Entity
    private GioHang toEntity(GioHangDTO dto) {
        GioHang entity = new GioHang();
        entity.setIdGH(dto.getIdGH());
        entity.setMaGH(dto.getMaGH());
        if (dto.getIdKH() != null) {
            KhachHang kh = khachHangRepository.findById(dto.getIdKH())
                    .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
            entity.setKhachHang(kh);
        }
        entity.setTrangThai(dto.getTrangThai());
        entity.setNgayTao(dto.getNgayTao());
        entity.setNgaySua(dto.getNgaySua());
        if (dto.getIdTK() != null) {
            TaiKhoan tk = taiKhoanRepository.findById(dto.getIdTK())
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
            entity.setTaiKhoan(tk);
        }
        return entity;
    }

    // Tạo mới
    public GioHangDTO create(GioHangDTO dto) {
        GioHang entity = toEntity(dto);
        GioHang saved = gioHangRepository.save(entity);
        return toDTO(saved);
    }

    // Lấy theo ID
    public GioHangDTO getById(Integer id) {
        GioHang entity = gioHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));
        return toDTO(entity);
    }

    // Lấy tất cả
    public List<GioHangDTO> getAll() {
        return gioHangRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Cập nhật
    public GioHangDTO update(Integer id, GioHangDTO dto) {
        GioHang entity = gioHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));
        entity.setMaGH(dto.getMaGH());
        if (dto.getIdKH() != null) {
            KhachHang kh = khachHangRepository.findById(dto.getIdKH())
                    .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
            entity.setKhachHang(kh);
        } else {
            entity.setKhachHang(null);
        }
        entity.setTrangThai(dto.getTrangThai());
        entity.setNgayTao(dto.getNgayTao());
        entity.setNgaySua(dto.getNgaySua());
        if (dto.getIdTK() != null) {
            TaiKhoan tk = taiKhoanRepository.findById(dto.getIdTK())
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
            entity.setTaiKhoan(tk);
        } else {
            entity.setTaiKhoan(null);
        }
        GioHang updated = gioHangRepository.save(entity);
        return toDTO(updated);
    }

    // Xóa
    public void delete(Integer id) {
        GioHang entity = gioHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));
        gioHangRepository.delete(entity);
    }
} 