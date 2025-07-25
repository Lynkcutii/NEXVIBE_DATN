package com.example.datnspct.Service;

import com.example.datnspct.Model.PhuongTT;
import com.example.datnspct.Repository.PhuongTTRepository;
import com.example.datnspct.dto.HoaDonChiTietDTO;
import com.example.datnspct.Model.HoaDon;
import com.example.datnspct.Model.HoaDonChiTiet;
import com.example.datnspct.Model.SanPhamChiTiet;
import com.example.datnspct.Repository.HoaDonChiTietRepository;
import com.example.datnspct.Repository.HoaDonRepository;
import com.example.datnspct.Repository.SanPhamChiTietRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HoaDonChiTietService {
//    @Autowired
//    private PhuongTTRepository phuongTTRepository;

    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    private HoaDonChiTietDTO convertToDTO(HoaDonChiTiet entity) {
        HoaDonChiTietDTO dto = new HoaDonChiTietDTO();
        dto.setIdHDCT(entity.getIdHDCT());
        dto.setIdSP(entity.getSanPhamct().getId());
        dto.setIdHD(entity.getHoaDon().getIdHD());
        // Set other fields
        return dto;
    }

    private HoaDonChiTiet convertToEntity(HoaDonChiTietDTO dto) {
        HoaDonChiTiet entity = new HoaDonChiTiet();
        entity.setIdHDCT(dto.getIdHDCT());
        // Fetch related entities
        HoaDon hoaDon = hoaDonRepository.findById(dto.getIdHD())
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));
        SanPhamChiTiet sanPham = sanPhamChiTietRepository.findById(dto.getIdSP())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        entity.setHoaDon(hoaDon);
        entity.setSanPhamct(sanPham);
        // Set other fields
        entity.setThanhTien(dto.getDonGia().multiply(BigDecimal.valueOf(dto.getSoLuong())));
        return entity;
    }

    public HoaDonChiTietDTO createHoaDonChiTiet(HoaDonChiTietDTO dto) {
        HoaDonChiTiet entity = convertToEntity(dto);
        entity.setNgayTao(LocalDateTime.now());
        entity.setNgaySua(LocalDateTime.now());
        HoaDonChiTiet saved = hoaDonChiTietRepository.save(entity);
        return convertToDTO(saved);
    }
    // ... các import và khai báo khác ...

    public HoaDonChiTietDTO layHoaDonChiTietTheoId(Integer id) {
        // TODO: Lấy chi tiết hóa đơn theo id
        // Ví dụ:
        HoaDonChiTiet entity = hoaDonChiTietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn chi tiết"));
        return convertToDTO(entity);
    }

    public List<HoaDonChiTietDTO> layTatCaHoaDonChiTiet() {
        // TODO: Lấy tất cả hóa đơn chi tiết
        List<HoaDonChiTiet> entities = hoaDonChiTietRepository.findAll();
        return entities.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public HoaDonChiTietDTO capNhatHoaDonChiTiet(Integer id, HoaDonChiTietDTO dto) {
        // TODO: Cập nhật hóa đơn chi tiết
        HoaDonChiTiet entity = hoaDonChiTietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn chi tiết"));
        // Cập nhật các trường từ dto vào entity
        // ...
        HoaDonChiTiet saved = hoaDonChiTietRepository.save(entity);
        return convertToDTO(saved);
    }

    public void xoaHoaDonChiTiet(Integer id) {
        // TODO: Xóa hóa đơn chi tiết
        hoaDonChiTietRepository.deleteById(id);
    }


//    public List<HoaDonChiTietDTO> getChiTietByHoaDonId(Integer idHD) {
//        return hoaDonChiTietRepository.findByHoaDon_IdHD(idHD).stream().map(hoaDonCT -> {
//            HoaDonChiTietDTO dto = new HoaDonChiTietDTO();
//            dto.setIdHDCT(hoaDonCT.getIdHDCT());
//            dto.setIdHD(hoaDonCT.getHoaDon() != null ? hoaDonCT.getHoaDon().getIdHD() : null);
//
//            // Lấy id sản phẩm chi tiết
//            Integer idSPCT = hoaDonCT.getSanPhamct() != null ? hoaDonCT.getSanPhamct().getId() : null;
//            dto.setIdSP(idSPCT);
//
//            // Lấy tên sản phẩm chi tiết
//            String tenSanPham = "N/A";
//            if (idSPCT != null) {
//                SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepository.findById(idSPCT).orElse(null);
//                if (sanPhamChiTiet != null && sanPhamChiTiet.getSanPham() != null) {
//                    tenSanPham = sanPhamChiTiet.getSanPham().getTenSP();
//                }
//            }
//            dto.setTenSanPham(tenSanPham);
//
//            dto.setDonGia(hoaDonCT.getDonGia());
//            dto.setSoLuong(hoaDonCT.getSoLuong());
//            dto.setThanhTien(hoaDonCT.getThanhTien());
//
//            // Lấy phương thức thanh toán
//            Integer idPTT = hoaDonCT.getPhuongThucThanhToan() != null ? hoaDonCT.getPhuongThucThanhToan().getIdPTT() : null;
//            String tenPTT = "N/A";
//            if (idPTT != null) {
//                PhuongTT phuongThucThanhToan = phuongTTRepository.findById(idPTT).orElse(null);
//                if (phuongThucThanhToan != null) {
//                    tenPTT = phuongThucThanhToan.getTen();
//                }
//            }
//            dto.setPhuongThanhToan(tenPTT);
//
//            return dto;
//        }).collect(Collectors.toList());
//    }
}