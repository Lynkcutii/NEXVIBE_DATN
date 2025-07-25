package com.example.datnspct.Service;

import com.example.datnspct.dto.SizeDTO;
import com.example.datnspct.Model.Size;
import com.example.datnspct.Repository.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SizeService {
    @Autowired
    private SizeRepository repository;

    // Entity -> DTO
    private SizeDTO toDTO(Size entity) {
        SizeDTO dto = new SizeDTO();
        dto.setIdSize(entity.getIdSize());
        dto.setMaSize(entity.getMaSize());
        dto.setTenSize(entity.getTenSize());
        dto.setTrangThai(entity.getTrangThai());
        return dto;
    }

    // DTO -> Entity
    private Size toEntity(SizeDTO dto) {
        Size entity = new Size();
        entity.setIdSize(dto.getIdSize());
        entity.setMaSize(dto.getMaSize());
        entity.setTenSize(dto.getTenSize());
        entity.setTrangThai(dto.getTrangThai());
        return entity;
    }

    // Tạo mới
    public SizeDTO create(SizeDTO dto) {
        Size entity = toEntity(dto);
        Size saved = repository.save(entity);
        return toDTO(saved);
    }

    // Lấy theo ID
    public SizeDTO getById(Integer id) {
        Size entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy size"));
        return toDTO(entity);
    }

    // Lấy tất cả
    public List<SizeDTO> getAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Cập nhật
    public SizeDTO update(Integer id, SizeDTO dto) {
        Size entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy size"));
        entity.setMaSize(dto.getMaSize());
        entity.setTenSize(dto.getTenSize());
        entity.setTrangThai(dto.getTrangThai());
        Size updated = repository.save(entity);
        return toDTO(updated);
    }

    // Xóa
    public void delete(Integer id) {
        Size entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy size"));
        repository.delete(entity);
    }
} 