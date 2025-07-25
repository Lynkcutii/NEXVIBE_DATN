package com.example.datnspct.Service;

import com.example.datnspct.dto.MauSacDTO;
import com.example.datnspct.Model.MauSac;
import com.example.datnspct.Repository.MauSacRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MauSacService {
    @Autowired
    private MauSacRepository repository;

    // Entity -> DTO
    private MauSacDTO toDTO(MauSac entity) {
        MauSacDTO dto = new MauSacDTO();
        dto.setIdMauSac(entity.getIdMauSac());
        dto.setMaMauSac(entity.getMaMauSac());
        dto.setTenMauSac(entity.getTenMauSac());
        dto.setTrangThai(entity.getTrangThai());
        return dto;
    }

    // DTO -> Entity
    private MauSac toEntity(MauSacDTO dto) {
        MauSac entity = new MauSac();
        entity.setIdMauSac(dto.getIdMauSac());
        entity.setMaMauSac(dto.getMaMauSac());
        entity.setTenMauSac(dto.getTenMauSac());
        entity.setTrangThai(dto.getTrangThai());
        return entity;
    }

    // Tạo mới
    public MauSacDTO create(MauSacDTO dto) {
        MauSac entity = toEntity(dto);
        MauSac saved = repository.save(entity);
        return toDTO(saved);
    }

    // Lấy theo ID
    public MauSacDTO getById(Integer id) {
        MauSac entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy màu sắc"));
        return toDTO(entity);
    }

    // Lấy tất cả
    public List<MauSacDTO> getAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Cập nhật
    public MauSacDTO update(Integer id, MauSacDTO dto) {
        MauSac entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy màu sắc"));
        entity.setMaMauSac(dto.getMaMauSac());
        entity.setTenMauSac(dto.getTenMauSac());
        entity.setTrangThai(dto.getTrangThai());
        MauSac updated = repository.save(entity);
        return toDTO(updated);
    }

    // Xóa
    public void delete(Integer id) {
        MauSac entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy màu sắc"));
        repository.delete(entity);
    }
} 