package com.example.datnspct.Service;

import com.example.datnspct.dto.ImgDTO;
import com.example.datnspct.Model.Img;
import com.example.datnspct.Model.SanPhamChiTiet;
import com.example.datnspct.Repository.ImgRepository;
import com.example.datnspct.Repository.SanPhamChiTietRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImgService {
    @Autowired
    private ImgRepository imgRepository;
    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    // Entity -> DTO
    private ImgDTO toDTO(Img entity) {
        ImgDTO dto = new ImgDTO();
        dto.setIdImg(entity.getIdImg());
        dto.setIdSPCT(entity.getSanPhamChiTiet() != null ? Integer.valueOf(entity.getSanPhamChiTiet().getMaSPCT()) : null);
        dto.setLink(entity.getLink());
        dto.setName(entity.getName());
        dto.setSize(entity.getSize());
        return dto;
    }

    // DTO -> Entity
    private Img toEntity(ImgDTO dto) {
        Img entity = new Img();
        entity.setIdImg(dto.getIdImg());
        if (dto.getIdSPCT() != null) {
            SanPhamChiTiet spct = sanPhamChiTietRepository.findById(dto.getIdSPCT())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm chi tiết không tồn tại"));
            entity.setSanPhamChiTiet(spct);
        }
        entity.setLink(dto.getLink());
        entity.setName(dto.getName());
        entity.setSize(dto.getSize());
        return entity;
    }

    // Tạo mới
    public ImgDTO create(ImgDTO dto) {
        Img entity = toEntity(dto);
        Img saved = imgRepository.save(entity);
        return toDTO(saved);
    }

    // Lấy theo ID
    public ImgDTO getById(Integer id) {
        Img entity = imgRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ảnh"));
        return toDTO(entity);
    }

    // Lấy tất cả
    public List<ImgDTO> getAll() {
        return imgRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Cập nhật
    public ImgDTO update(Integer id, ImgDTO dto) {
        Img entity = imgRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ảnh"));
        if (dto.getIdSPCT() != null) {
            SanPhamChiTiet spct = sanPhamChiTietRepository.findById(dto.getIdSPCT())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm chi tiết không tồn tại"));
            entity.setSanPhamChiTiet(spct);
        } else {
            entity.setSanPhamChiTiet(null);
        }
        entity.setLink(dto.getLink());
        entity.setName(dto.getName());
        entity.setSize(dto.getSize());
        Img updated = imgRepository.save(entity);
        return toDTO(updated);
    }

    // Xóa
    public void delete(Integer id) {
        Img entity = imgRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ảnh"));
        imgRepository.delete(entity);
    }
} 