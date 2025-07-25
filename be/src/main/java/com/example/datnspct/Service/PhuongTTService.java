package com.example.datnspct.Service;

import com.example.datnspct.dto.PhuongTTDTO;
import com.example.datnspct.Model.PhuongTT;
import com.example.datnspct.Repository.PhuongTTRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PhuongTTService {
    @Autowired
    private PhuongTTRepository repository;

    // Entity -> DTO
    private PhuongTTDTO toDTO(PhuongTT entity) {
        PhuongTTDTO dto = new PhuongTTDTO();
        dto.setIdPTT(entity.getIdPTT());
        dto.setTen(entity.getTen());
        return dto;
    }

    // DTO -> Entity
    private PhuongTT toEntity(PhuongTTDTO dto) {
        PhuongTT entity = new PhuongTT();
        entity.setIdPTT(dto.getIdPTT());
        entity.setTen(dto.getTen());
        return entity;
    }

    // Tạo mới
    public PhuongTTDTO create(PhuongTTDTO dto) {
        PhuongTT entity = toEntity(dto);
        PhuongTT saved = repository.save(entity);
        return toDTO(saved);
    }

    // Lấy theo ID
    public PhuongTTDTO getById(Integer id) {
        PhuongTT entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán"));
        return toDTO(entity);
    }

    // Lấy tất cả
    public List<PhuongTTDTO> getAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Cập nhật
    public PhuongTTDTO update(Integer id, PhuongTTDTO dto) {
        PhuongTT entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán"));
        entity.setTen(dto.getTen());
        PhuongTT updated = repository.save(entity);
        return toDTO(updated);
    }

    // Xóa
    public void delete(Integer id) {
        PhuongTT entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán"));
        repository.delete(entity);
    }
} 