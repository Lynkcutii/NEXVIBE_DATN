package com.example.datnspct.Service;

import com.example.datnspct.dto.NhanVienDTO;
import com.example.datnspct.Model.NhanVien;
import com.example.datnspct.Model.TaiKhoan;
import com.example.datnspct.Repository.NhanVienRepository;
import com.example.datnspct.Repository.TaiKhoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NhanVienService {
    @Autowired
    private NhanVienRepository nhanVienRepository;
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    // Entity -> DTO
    private NhanVienDTO toDTO(NhanVien entity) {
        NhanVienDTO dto = new NhanVienDTO();
        dto.setIdNV(entity.getIdNV());
        dto.setMaNV(entity.getMaNV());
        dto.setTenNV(entity.getTenNV());
        dto.setGioiTinh(entity.getGioiTinh());
        dto.setNgaySinh(entity.getNgaySinh());
        dto.setSdt(entity.getSdt());
        dto.setEmail(entity.getEmail());
        dto.setDiaChi(entity.getDiaChi());
        dto.setIdTK(entity.getTaiKhoan() != null ? entity.getTaiKhoan().getIdTK() : null);
        dto.setTrangThai(entity.getTrangThai());
        return dto;
    }

    // DTO -> Entity
    private NhanVien toEntity(NhanVienDTO dto) {
        NhanVien entity = new NhanVien();
        entity.setIdNV(dto.getIdNV());
        entity.setMaNV(dto.getMaNV());
        entity.setTenNV(dto.getTenNV());
        entity.setGioiTinh(dto.getGioiTinh());
        entity.setNgaySinh(dto.getNgaySinh());
        entity.setSdt(dto.getSdt());
        entity.setEmail(dto.getEmail());
        entity.setDiaChi(dto.getDiaChi());
        if (dto.getIdTK() != null) {
            TaiKhoan tk = taiKhoanRepository.findById(dto.getIdTK())
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
            entity.setTaiKhoan(tk);
        }
        entity.setTrangThai(dto.getTrangThai());
        return entity;
    }

    // Tạo mới
    public NhanVienDTO create(NhanVienDTO dto) {
        NhanVien entity = toEntity(dto);
        NhanVien saved = nhanVienRepository.save(entity);
        return toDTO(saved);
    }

    // Lấy theo ID
    public NhanVienDTO getById(Integer id) {
        NhanVien entity = nhanVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
        return toDTO(entity);
    }

    // Lấy tất cả
    public List<NhanVienDTO> getAll() {
        return nhanVienRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Cập nhật
    public NhanVienDTO update(Integer id, NhanVienDTO dto) {
        NhanVien entity = nhanVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
        entity.setMaNV(dto.getMaNV());
        entity.setTenNV(dto.getTenNV());
        entity.setGioiTinh(dto.getGioiTinh());
        entity.setNgaySinh(dto.getNgaySinh());
        entity.setSdt(dto.getSdt());
        entity.setEmail(dto.getEmail());
        entity.setDiaChi(dto.getDiaChi());
        if (dto.getIdTK() != null) {
            TaiKhoan tk = taiKhoanRepository.findById(dto.getIdTK())
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
            entity.setTaiKhoan(tk);
        } else {
            entity.setTaiKhoan(null);
        }
        entity.setTrangThai(dto.getTrangThai());
        NhanVien updated = nhanVienRepository.save(entity);
        return toDTO(updated);
    }

    // Xóa
    public void delete(Integer id) {
        NhanVien entity = nhanVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
        nhanVienRepository.delete(entity);
    }
} 