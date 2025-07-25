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

    public DanhMucDTO create(DanhMucDTO dto) {
        DanhMuc entity = toEntity(dto);
        return toDTO(danhMucRepository.save(entity));
    }

    public DanhMucDTO getById(Integer id) {
        return danhMucRepository.findById(id).map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy"));
    }

    public List<DanhMucDTO> getAll() {
        return danhMucRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public DanhMucDTO update(Integer id, DanhMucDTO dto) {
        DanhMuc entity = danhMucRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy"));
        entity.setTenDM(dto.getTenDM());
        entity.setTrangThai(dto.getTrangThai());
        return toDTO(danhMucRepository.save(entity));
    }

    public void delete(Integer id) {
        danhMucRepository.deleteById(id);
    }
}
