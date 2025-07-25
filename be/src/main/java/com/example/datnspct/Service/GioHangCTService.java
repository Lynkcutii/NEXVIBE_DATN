package com.example.datnspct.Service;

import com.example.datnspct.dto.GioHangCTDTO;
import com.example.datnspct.Model.GioHangCT;
import com.example.datnspct.Model.GioHang;
import com.example.datnspct.Model.SanPhamChiTiet;
import com.example.datnspct.Repository.GioHangCTRepository;
import com.example.datnspct.Repository.GioHangRepository;
import com.example.datnspct.Repository.SanPhamChiTietRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GioHangCTService {
    @Autowired
    private GioHangCTRepository gioHangCTRepository;
    @Autowired
    private GioHangRepository gioHangRepository;
    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    // Entity -> DTO
    private GioHangCTDTO toDTO(GioHangCT entity) {
        GioHangCTDTO dto = new GioHangCTDTO();
        dto.setIdGHCT(entity.getIdGHCT());
        dto.setIdGH(entity.getGioHang() != null ? entity.getGioHang().getIdGH() : null);
        dto.setIdSPCT(entity.getSanPhamChiTiet() != null ? Integer.valueOf(entity.getSanPhamChiTiet().getMaSPCT()) : null);
        dto.setSoLuong(entity.getSoLuong());
        dto.setDonGia(entity.getDonGia());
        return dto;
    }

    // DTO -> Entity
    private GioHangCT toEntity(GioHangCTDTO dto) {
        GioHangCT entity = new GioHangCT();
        entity.setIdGHCT(dto.getIdGHCT());
        if (dto.getIdGH() != null) {
            GioHang gh = gioHangRepository.findById(dto.getIdGH())
                    .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại"));
            entity.setGioHang(gh);
        }
        if (dto.getIdSPCT() != null) {
            SanPhamChiTiet spct = sanPhamChiTietRepository.findById(dto.getIdSPCT())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm chi tiết không tồn tại"));
            entity.setSanPhamChiTiet(spct);
        }
        entity.setSoLuong(dto.getSoLuong());
        entity.setDonGia(dto.getDonGia());
        return entity;
    }

    // Tạo mới
    public GioHangCTDTO create(GioHangCTDTO dto) {
        GioHangCT entity = toEntity(dto);
        GioHangCT saved = gioHangCTRepository.save(entity);
        return toDTO(saved);
    }

    // Lấy theo ID
    public GioHangCTDTO getById(Integer id) {
        GioHangCT entity = gioHangCTRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết giỏ hàng"));
        return toDTO(entity);
    }

    // Lấy tất cả
    public List<GioHangCTDTO> getAll() {
        return gioHangCTRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Cập nhật
    public GioHangCTDTO update(Integer id, GioHangCTDTO dto) {
        GioHangCT entity = gioHangCTRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết giỏ hàng"));
        if (dto.getIdGH() != null) {
            GioHang gh = gioHangRepository.findById(dto.getIdGH())
                    .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại"));
            entity.setGioHang(gh);
        } else {
            entity.setGioHang(null);
        }
        if (dto.getIdSPCT() != null) {
            SanPhamChiTiet spct = sanPhamChiTietRepository.findById(dto.getIdSPCT())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm chi tiết không tồn tại"));
            entity.setSanPhamChiTiet(spct);
        } else {
            entity.setSanPhamChiTiet(null);
        }
        entity.setSoLuong(dto.getSoLuong());
        entity.setDonGia(dto.getDonGia());
        GioHangCT updated = gioHangCTRepository.save(entity);
        return toDTO(updated);
    }

    // Xóa
    public void delete(Integer id) {
        GioHangCT entity = gioHangCTRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết giỏ hàng"));
        gioHangCTRepository.delete(entity);
    }
} 