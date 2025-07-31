package com.example.datnspct.Service;

import com.example.datnspct.Model.HoaDon;
import com.example.datnspct.Model.HoaDonChiTiet;
import com.example.datnspct.Model.SanPhamChiTiet;
import com.example.datnspct.Repository.HoaDonChiTietRepository;
import com.example.datnspct.Repository.HoaDonRepository;
import com.example.datnspct.Repository.SanPhamChiTietRepository;
import com.example.datnspct.dto.HoaDonChiTietDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HoaDonChiTietService {
    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    private HoaDonChiTietDTO convertToDTO(HoaDonChiTiet entity) {
        HoaDonChiTietDTO dto = new HoaDonChiTietDTO();
        dto.setIdHDCT(entity.getIdHDCT());
        dto.setIdSP(entity.getSanPhamct().getId()); // Hoặc getIdSpct() nếu dùng camelCase
        dto.setIdHD(entity.getHoaDon().getIdHD());
        dto.setDonGia(entity.getDonGia());
        dto.setSoLuong(entity.getSoLuong());
        dto.setThanhTien(entity.getThanhTien());
        // Thêm logic để lấy tenSanPham nếu cần
        String tenSanPham = entity.getSanPhamct().getSanPham() != null ? entity.getSanPhamct().getSanPham().getTenSP() : "N/A";
        dto.setTenSanPham(tenSanPham);
        return dto;
    }

    private HoaDonChiTiet convertToEntity(HoaDonChiTietDTO dto) {
        HoaDonChiTiet entity = new HoaDonChiTiet();
        entity.setIdHDCT(dto.getIdHDCT());
        HoaDon hoaDon = hoaDonRepository.findById(dto.getIdHD())
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));
        SanPhamChiTiet sanPham = sanPhamChiTietRepository.findById(dto.getIdSP())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        entity.setHoaDon(hoaDon);
        entity.setSanPhamct(sanPham);
        entity.setDonGia(dto.getDonGia());
        entity.setSoLuong(dto.getSoLuong());
        entity.setThanhTien(dto.getDonGia().multiply(BigDecimal.valueOf(dto.getSoLuong())));
        entity.setNgayTao(LocalDateTime.now());
        entity.setNgaySua(LocalDateTime.now());
        return entity;
    }

    public HoaDonChiTietDTO createHoaDonChiTiet(HoaDonChiTietDTO dto) {
        HoaDonChiTiet entity = convertToEntity(dto);
        HoaDonChiTiet saved = hoaDonChiTietRepository.save(entity);
        return convertToDTO(saved);
    }

    public HoaDonChiTietDTO layHoaDonChiTietTheoId(Integer id) {
        HoaDonChiTiet entity = hoaDonChiTietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn chi tiết"));
        return convertToDTO(entity);
    }

    public List<HoaDonChiTietDTO> layTatCaHoaDonChiTiet() {
        List<HoaDonChiTiet> entities = hoaDonChiTietRepository.findAll();
        return entities.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public HoaDonChiTietDTO capNhatHoaDonChiTiet(Integer id, HoaDonChiTietDTO dto) {
        HoaDonChiTiet entity = hoaDonChiTietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn chi tiết"));
        entity.setHoaDon(hoaDonRepository.findById(dto.getIdHD())
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại")));
        entity.setSanPhamct(sanPhamChiTietRepository.findById(dto.getIdSP())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại")));
        entity.setDonGia(dto.getDonGia());
        entity.setSoLuong(dto.getSoLuong());
        entity.setThanhTien(dto.getDonGia().multiply(BigDecimal.valueOf(dto.getSoLuong())));
        entity.setNgaySua(LocalDateTime.now());
        HoaDonChiTiet saved = hoaDonChiTietRepository.save(entity);
        return convertToDTO(saved);
    }

    public void xoaHoaDonChiTiet(Integer id) {
        hoaDonChiTietRepository.deleteById(id);
    }
}