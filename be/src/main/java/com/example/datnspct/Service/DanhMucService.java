package com.example.datnspct.Service;

import com.example.datnspct.Model.DanhMuc;
import com.example.datnspct.Repository.DanhMucRepository;
import com.example.datnspct.dto.DanhMucDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DanhMucService {

    @Autowired
    private DanhMucRepository danhMucRepository;

    // Sinh mã danh mục tự động
    private String generateMaDanhMuc() {
        long count = danhMucRepository.count();
        return String.format("DM%03d", count + 1); // Ví dụ: DM001, DM002
    }

    // Entity -> DTO
    private DanhMucDTO toDTO(DanhMuc entity) {
        DanhMucDTO dto = new DanhMucDTO();
        dto.setIdDM(entity.getIdDM());
        dto.setTenDM(entity.getTenDM());
        dto.setTrangThai(entity.getTrangThai());
        return dto;
    }

    // DTO -> Entity
    private DanhMuc toEntity(DanhMucDTO dto) {
        DanhMuc entity = new DanhMuc();
        entity.setIdDM(dto.getIdDM());
        entity.setTenDM(dto.getTenDM());
        entity.setTrangThai(dto.getTrangThai());
        return entity;
    }

    // Tạo mới
    public DanhMucDTO create(DanhMucDTO dto) {
        DanhMuc entity = toEntity(dto);
        entity.setMaDM(generateMaDanhMuc()); // Tự động sinh mã
        entity.setTrangThai(true); // Mặc định true
        return toDTO(danhMucRepository.save(entity));
    }

    // Lấy theo ID
    public DanhMucDTO getById(Integer id) {
        return danhMucRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
    }

    // Lấy tất cả
    public List<DanhMucDTO> getAll() {
        return danhMucRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Cập nhật
    public DanhMucDTO update(Integer id, DanhMucDTO dto) {
        DanhMuc entity = danhMucRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        entity.setTenDM(dto.getTenDM()); // Chỉ cập nhật tên
        entity.setTrangThai(dto.getTrangThai());
        return toDTO(danhMucRepository.save(entity));
    }

    // Xóa
    public void delete(Integer id) {
        DanhMuc entity = danhMucRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        danhMucRepository.delete(entity);
    }
}