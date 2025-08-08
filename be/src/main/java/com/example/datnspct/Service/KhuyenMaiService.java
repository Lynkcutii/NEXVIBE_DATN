package com.example.datnspct.Service;

import com.example.datnspct.Model.KhuyenMai;
import com.example.datnspct.Repository.KhuyenMaiRepository;
import com.example.datnspct.dto.KhuyenMaiDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KhuyenMaiService {
    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;

    // Entity -> DTO
    private KhuyenMaiDTO toDTO(KhuyenMai entity) {
        KhuyenMaiDTO dto = new KhuyenMaiDTO();
        dto.setIdKM(entity.getIdKM());
        dto.setMaKM(entity.getMaKM());
        dto.setTenKM(entity.getTenKM());
        dto.setHinhThucGiam(entity.getHinhThucGiam());
        dto.setMucGiam(entity.getMucGiam());
        dto.setGiaTriDonHangToiThieu(entity.getGiaTriDonHangToiThieu());
        dto.setGiamToiDa(entity.getGiamToiDa());
        dto.setSoLuong(entity.getSoLuong());
        dto.setDaSuDung(entity.getDaSuDung());
        dto.setNgayBatDau(entity.getNgayBatDau());
        dto.setNgayKetThuc(entity.getNgayKetThuc());
        dto.setTrangThai(entity.getTrangThai());
        dto.setIdKH(entity.getKhachHang() != null ? entity.getKhachHang().getIdKH() : null);
        return dto;
    }

    // DTO -> Entity
    private KhuyenMai toEntity(KhuyenMaiDTO dto) {
        KhuyenMai entity = new KhuyenMai();
        entity.setIdKM(dto.getIdKM());
        entity.setMaKM(dto.getMaKM());
        entity.setTenKM(dto.getTenKM());
        entity.setHinhThucGiam(dto.getHinhThucGiam());
        entity.setMucGiam(dto.getMucGiam());
        entity.setGiaTriDonHangToiThieu(dto.getGiaTriDonHangToiThieu());
        entity.setGiamToiDa(dto.getGiamToiDa());
        entity.setSoLuong(dto.getSoLuong());
        entity.setDaSuDung(dto.getDaSuDung());
        entity.setNgayBatDau(dto.getNgayBatDau());
        entity.setNgayKetThuc(dto.getNgayKetThuc());
        entity.setTrangThai(dto.getTrangThai());
        // IdKH sẽ được set trong logic khác nếu cần
        return entity;
    }

    // Tạo mới
    public KhuyenMaiDTO create(KhuyenMaiDTO dto) {
        KhuyenMai entity = toEntity(dto);
        KhuyenMai saved = khuyenMaiRepository.save(entity);
        return toDTO(saved);
    }

    // Lấy theo ID
    public KhuyenMaiDTO getById(Integer id) {
        KhuyenMai entity = khuyenMaiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi"));
        return toDTO(entity);
    }

    // Lấy tất cả
    public List<KhuyenMaiDTO> getAll() {
        return khuyenMaiRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Cập nhật
    public KhuyenMaiDTO update(Integer id, KhuyenMaiDTO dto) {
        KhuyenMai entity = khuyenMaiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi"));
        entity.setMaKM(dto.getMaKM());
        entity.setTenKM(dto.getTenKM());
        entity.setHinhThucGiam(dto.getHinhThucGiam());
        entity.setMucGiam(dto.getMucGiam());
        entity.setGiaTriDonHangToiThieu(dto.getGiaTriDonHangToiThieu());
        entity.setGiamToiDa(dto.getGiamToiDa());
        entity.setSoLuong(dto.getSoLuong());
        entity.setDaSuDung(dto.getDaSuDung());
        entity.setNgayBatDau(dto.getNgayBatDau());
        entity.setNgayKetThuc(dto.getNgayKetThuc());
        entity.setTrangThai(dto.getTrangThai());
        KhuyenMai updated = khuyenMaiRepository.save(entity);
        return toDTO(updated);
    }

    // Xóa
    public void delete(Integer id) {
        KhuyenMai entity = khuyenMaiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi"));
        khuyenMaiRepository.delete(entity);
    }

    // Lấy danh sách khuyến mãi áp dụng theo IdKH
    public List<KhuyenMaiDTO> getApplicableVouchers(Integer idKH) {
        LocalDateTime now = LocalDateTime.now();
        return khuyenMaiRepository.findByKhachHangIdKHAndTrangThaiTrueAndNgayBatDauBeforeAndNgayKetThucAfter(idKH, now, now)
                .stream()
                .filter(km -> km.getSoLuong() > km.getDaSuDung())
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Lấy danh sách khuyến mãi theo IdKH
    public List<KhuyenMaiDTO> getVouchersByCustomerId(Integer idKH) {
        LocalDateTime now = LocalDateTime.now();
        return khuyenMaiRepository.findByKhachHangIdKHAndTrangThaiTrueAndNgayBatDauBeforeAndNgayKetThucAfter(idKH, now, now)
                .stream()
                .filter(km -> km.getSoLuong() > km.getDaSuDung())
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}