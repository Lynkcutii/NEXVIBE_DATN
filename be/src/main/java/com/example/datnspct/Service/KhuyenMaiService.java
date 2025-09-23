package com.example.datnspct.Service;

import com.example.datnspct.Model.KhachHang;
import com.example.datnspct.Model.KhuyenMai;
import com.example.datnspct.Repository.KhachHangRepository;
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
    private KhachHangRepository khachHangRepository;
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

        // map nhiều khách hàng
        dto.setIdKHs(entity.getKhachHangs() != null
                ? entity.getKhachHangs().stream()
                .map(KhachHang::getIdKH)
                .collect(Collectors.toList())
                : List.of());

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

        // set danh sách khách hàng nếu có
        if (dto.getIdKHs() != null && !dto.getIdKHs().isEmpty()) {
            entity.setKhachHangs(
                    dto.getIdKHs().stream()
                            .map(id -> khachHangRepository.findById(id)
                                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng Id=" + id)))
                            .collect(Collectors.toList())
            );
        }

        return entity;
    }

    // Tạo mới
    public KhuyenMaiDTO create(KhuyenMaiDTO dto) {
        KhuyenMai entity = new KhuyenMai();

        entity.setMaKM(dto.getMaKM());
        entity.setTenKM(dto.getTenKM());
        entity.setHinhThucGiam(dto.getHinhThucGiam());
        entity.setMucGiam(dto.getMucGiam());
        entity.setGiaTriDonHangToiThieu(dto.getGiaTriDonHangToiThieu());
        entity.setGiamToiDa(dto.getGiamToiDa());
        entity.setSoLuong(dto.getSoLuong());
        entity.setDaSuDung(dto.getDaSuDung() != null ? dto.getDaSuDung() : 0);
        entity.setNgayBatDau(dto.getNgayBatDau());
        entity.setNgayKetThuc(dto.getNgayKetThuc());
        entity.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : true);

        // ✅ set danh sách khách hàng
        entity.setKhachHangs(
                dto.getIdKHs() == null ? List.of() :
                        dto.getIdKHs().stream()
                                .map(idKH -> khachHangRepository.findById(idKH)
                                        .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng Id=" + idKH)))
                                .collect(Collectors.toList())
        );

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

        // cập nhật lại danh sách khách hàng
        entity.setKhachHangs(
                dto.getIdKHs() == null ? List.of() :
                        dto.getIdKHs().stream()
                                .map(idKH -> khachHangRepository.findById(idKH)
                                        .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng Id=" + idKH)))
                                .collect(Collectors.toList())
        );

        KhuyenMai updated = khuyenMaiRepository.save(entity);
        return toDTO(updated);
    }

    // Xóa
    public void delete(Integer id) {
        KhuyenMai entity = khuyenMaiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi"));
        khuyenMaiRepository.delete(entity);
    }

    // Lấy danh sách khuyến mãi áp dụng cho khách hàng
    public List<KhuyenMaiDTO> getApplicableVouchers(Integer idKH) {
        LocalDateTime now = LocalDateTime.now();
        return khuyenMaiRepository.findAll().stream()
                .filter(km -> km.getTrangThai()
                        && !km.getNgayBatDau().isAfter(now) // bắt đầu <= now
                        && !km.getNgayKetThuc().isBefore(now) // kết thúc >= now
                        && km.getSoLuong() > km.getDaSuDung()
                        && (km.getKhachHangs().isEmpty() || // áp dụng cho tất cả
                        km.getKhachHangs().stream().anyMatch(kh -> kh.getIdKH().equals(idKH))))
                .map(this::toDTO)
                .toList();
    }


    public List<KhuyenMaiDTO> getVouchersByCustomerId(Integer idKH) {
        LocalDateTime now = LocalDateTime.now();
        return khuyenMaiRepository.findApplicableByKhachHang(idKH, now)
                .stream()
                .filter(km -> km.getSoLuong() > km.getDaSuDung())
                .map(this::toDTO)
                .toList();
    }
}
