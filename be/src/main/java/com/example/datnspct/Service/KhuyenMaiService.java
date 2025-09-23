package com.example.datnspct.Service;

import com.example.datnspct.Model.KhachHang;
import com.example.datnspct.Model.KhuyenMai;
import com.example.datnspct.Repository.KhachHangRepository;
import com.example.datnspct.Repository.KhuyenMaiRepository;
import com.example.datnspct.dto.KhuyenMaiDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KhuyenMaiService {
    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

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
        dto.setCustomerIds(entity.getCustomers().stream().map(KhachHang::getIdKH).collect(Collectors.toList()));
        dto.setCustomerNames(entity.getCustomers().stream().map(KhachHang::getTenKH).collect(Collectors.toList()));
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
        if (dto.getCustomerIds() != null && !dto.getCustomerIds().isEmpty()) {
            List<KhachHang> customers = khachHangRepository.findAllById(dto.getCustomerIds());
            entity.setCustomers(customers);
        } else {
            entity.setCustomers(new ArrayList<>());
        }
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
        if (dto.getCustomerIds() != null && !dto.getCustomerIds().isEmpty()) {
            List<KhachHang> customers = khachHangRepository.findAllById(dto.getCustomerIds());
            entity.setCustomers(customers);
        } else {
            entity.setCustomers(new ArrayList<>());
        }
        KhuyenMai updated = khuyenMaiRepository.save(entity);
        return toDTO(updated);
    }

    // Xóa
    public void delete(Integer id) {
        KhuyenMai entity = khuyenMaiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi"));
        khuyenMaiRepository.delete(entity);
    }

    // Lấy danh sách khuyến mãi áp dụng cho khách hàng (gán riêng hoặc toàn hệ thống)
    public List<KhuyenMaiDTO> getApplicableVouchers(Integer idKH) {
        LocalDateTime now = LocalDateTime.now();
        List<KhuyenMai> byCustomer = khuyenMaiRepository.findByCustomer(idKH, now);
        List<KhuyenMai> global = khuyenMaiRepository.findGlobal(now);
        List<KhuyenMai> all = new ArrayList<>();
        all.addAll(byCustomer);
        all.addAll(global);
        return all.stream()
                .filter(km -> km.getSoLuong() > km.getDaSuDung())
                .filter(km -> Boolean.TRUE.equals(km.getTrangThai()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Lấy danh sách khuyến mãi chỉ gán cho khách hàng
    public List<KhuyenMaiDTO> getVouchersByCustomerId(Integer idKH) {
        LocalDateTime now = LocalDateTime.now();
        return khuyenMaiRepository.findByCustomer(idKH, now)
                .stream()
                .filter(km -> km.getSoLuong() > km.getDaSuDung())
                .filter(km -> Boolean.TRUE.equals(km.getTrangThai()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}