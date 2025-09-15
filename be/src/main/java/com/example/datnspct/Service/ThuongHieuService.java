package com.example.datnspct.Service;

import com.example.datnspct.Model.ThuongHieu;
import com.example.datnspct.Repository.ThuongHieuRepository;
import com.example.datnspct.dto.ThuongHieuDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ThuongHieuService {

    @Autowired
    private ThuongHieuRepository repository;

    // Sinh mã thương hiệu tự động
    private String generateMaThuongHieu() {
        long count = repository.count();
        return String.format("TH%03d", count + 1); // Ví dụ: TH001, TH002
    }

    // Entity -> DTO
    private ThuongHieuDTO toDTO(ThuongHieu entity) {
        ThuongHieuDTO dto = new ThuongHieuDTO();
        dto.setIdThuongHieu(entity.getIdThuongHieu());
        dto.setMaThuongHieu(entity.getMaThuongHieu());
        dto.setTenThuongHieu(entity.getTenThuongHieu());
        dto.setTrangThai(entity.getTrangThai());
        return dto;
    }

    // DTO -> Entity
    private ThuongHieu toEntity(ThuongHieuDTO dto) {
        ThuongHieu entity = new ThuongHieu();
        entity.setIdThuongHieu(dto.getIdThuongHieu());
        entity.setTenThuongHieu(dto.getTenThuongHieu());
        entity.setTrangThai(dto.getTrangThai());
        return entity;
    }

    // Tạo mới
    public ThuongHieuDTO create(ThuongHieuDTO dto) {
        ThuongHieu entity = toEntity(dto);
        entity.setMaThuongHieu(generateMaThuongHieu()); // Tự động sinh mã
        entity.setTrangThai(true);
        ThuongHieu saved = repository.save(entity);
        return toDTO(saved);
    }

    // Lấy theo ID
    public ThuongHieuDTO getById(Integer id) {
        ThuongHieu entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thương hiệu"));
        return toDTO(entity);
    }

    // Lấy tất cả
    public List<ThuongHieuDTO> getAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Cập nhật
    public ThuongHieuDTO update(Integer id, ThuongHieuDTO dto) {
        ThuongHieu entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thương hiệu"));
        entity.setTenThuongHieu(dto.getTenThuongHieu()); // Chỉ cập nhật tên
        entity.setTrangThai(dto.getTrangThai());
        ThuongHieu updated = repository.save(entity);
        return toDTO(updated);
    }

    // Xóa
    public void delete(Integer id) {
        ThuongHieu entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thương hiệu"));
        repository.delete(entity);
    }
}