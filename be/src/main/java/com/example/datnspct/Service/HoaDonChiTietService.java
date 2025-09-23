package com.example.datnspct.Service;

import com.example.datnspct.Model.HoaDon;
import com.example.datnspct.Model.HoaDonChiTiet;
import com.example.datnspct.Model.PhuongTT;
import com.example.datnspct.Model.SanPhamChiTiet;
import com.example.datnspct.Repository.HoaDonChiTietRepository;
import com.example.datnspct.Repository.HoaDonRepository;
import com.example.datnspct.Repository.PhuongThucThanhToanRepository;
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

    @Autowired
    private PhuongThucThanhToanRepository phuongThanhToanRepository;

    private HoaDonChiTietDTO convertToDTO(HoaDonChiTiet entity) {
        HoaDonChiTietDTO dto = new HoaDonChiTietDTO();
        dto.setIdHDCT(entity.getIdHDCT());
        dto.setIdSP(entity.getSanPhamct().getId());
        dto.setIdHD(entity.getHoaDon().getIdHD());
        dto.setDonGia(entity.getDonGia());
        dto.setSoLuong(entity.getSoLuong());
        dto.setThanhTien(entity.getThanhTien());
        String tenSanPham = entity.getSanPhamct().getSanPham() != null ? entity.getSanPhamct().getSanPham().getTenSP() : "N/A";
        dto.setTenSanPham(tenSanPham);
        // Ánh xạ IdPT từ phuongThucThanhToan trong HoaDonChiTiet
        dto.setIdPT(entity.getPhuongThucThanhToan() != null ? entity.getPhuongThucThanhToan().getIdPTT() : null);
        return dto;
    }

    private HoaDonChiTiet convertToEntity(HoaDonChiTietDTO dto) {
        HoaDonChiTiet entity = new HoaDonChiTiet();
        entity.setIdHDCT(dto.getIdHDCT());
        HoaDon hoaDon = hoaDonRepository.findById(dto.getIdHD())
                .orElseThrow(() -> new IllegalArgumentException("Hóa đơn không tồn tại: " + dto.getIdHD()));
        SanPhamChiTiet sanPham = sanPhamChiTietRepository.findById(dto.getIdSP())
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại: " + dto.getIdSP()));
        entity.setHoaDon(hoaDon);
        entity.setSanPhamct(sanPham);
        entity.setDonGia(dto.getDonGia());
        entity.setSoLuong(dto.getSoLuong());
        entity.setThanhTien(dto.getDonGia().multiply(BigDecimal.valueOf(dto.getSoLuong())));
        entity.setNgayTao(LocalDateTime.now());
        entity.setNgaySua(LocalDateTime.now());
        // Xử lý IdPT từ DTO
        if (dto.getIdPT() != null) {
            PhuongTT phuongTT = phuongThanhToanRepository.findById(dto.getIdPT())
                    .orElseThrow(() -> new IllegalArgumentException("Phương thức thanh toán không tồn tại: " + dto.getIdPT()));
            entity.setPhuongThucThanhToan(phuongTT);
        } else {
            throw new IllegalArgumentException("IdPT là bắt buộc trong HoaDonChiTietDTO");
        }
        return entity;
    }

    public HoaDonChiTietDTO createHoaDonChiTiet(HoaDonChiTietDTO dto) {
        // Kiểm tra số lượng tồn kho
        SanPhamChiTiet sanPham = sanPhamChiTietRepository.findById(dto.getIdSP())
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại: " + dto.getIdSP()));
        if (sanPham.getSoLuong() < dto.getSoLuong()) {
            throw new IllegalArgumentException("Số lượng tồn kho không đủ cho sản phẩm idSP: " + dto.getIdSP() +
                    ". Tồn kho hiện tại: " + sanPham.getSoLuong());
        }

        // Trừ số lượng tồn kho
        sanPham.setSoLuong(sanPham.getSoLuong() - dto.getSoLuong());
        sanPhamChiTietRepository.save(sanPham);

        HoaDonChiTiet entity = convertToEntity(dto);
        HoaDonChiTiet saved = hoaDonChiTietRepository.save(entity);
        return convertToDTO(saved);
    }

    public HoaDonChiTietDTO layHoaDonChiTietTheoId(Integer id) {
        HoaDonChiTiet entity = hoaDonChiTietRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn chi tiết: " + id));
        return convertToDTO(entity);
    }

    public List<HoaDonChiTietDTO> layTatCaHoaDonChiTiet() {
        List<HoaDonChiTiet> entities = hoaDonChiTietRepository.findAll();
        return entities.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public HoaDonChiTietDTO capNhatHoaDonChiTiet(Integer id, HoaDonChiTietDTO dto) {
        HoaDonChiTiet entity = hoaDonChiTietRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn chi tiết: " + id));

        // Kiểm tra số lượng tồn kho khi cập nhật
        SanPhamChiTiet sanPham = sanPhamChiTietRepository.findById(dto.getIdSP())
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại: " + dto.getIdSP()));
        int soLuongCu = entity.getSoLuong();
        int soLuongMoi = dto.getSoLuong();
        int soLuongTonKho = sanPham.getSoLuong();
        int delta = soLuongMoi - soLuongCu;
        if (delta > 0 && delta > soLuongTonKho) {
            throw new IllegalArgumentException("Số lượng tồn kho không đủ cho sản phẩm idSP: " + dto.getIdSP() +
                    ". Tồn kho hiện tại: " + soLuongTonKho);
        }

        // Cập nhật số lượng tồn kho
        sanPham.setSoLuong(soLuongTonKho - delta);
        sanPhamChiTietRepository.save(sanPham);

        entity.setHoaDon(hoaDonRepository.findById(dto.getIdHD())
                .orElseThrow(() -> new IllegalArgumentException("Hóa đơn không tồn tại: " + dto.getIdHD())));
        entity.setSanPhamct(sanPham);
        entity.setDonGia(dto.getDonGia());
        entity.setSoLuong(dto.getSoLuong());
        entity.setThanhTien(dto.getDonGia().multiply(BigDecimal.valueOf(dto.getSoLuong())));
        entity.setNgaySua(LocalDateTime.now());
        if (dto.getIdPT() != null) {
            PhuongTT phuongTT = phuongThanhToanRepository.findById(dto.getIdPT())
                    .orElseThrow(() -> new IllegalArgumentException("Phương thức thanh toán không tồn tại: " + dto.getIdPT()));
            entity.setPhuongThucThanhToan(phuongTT);
        } else {
            throw new IllegalArgumentException("IdPT là bắt buộc trong HoaDonChiTietDTO");
        }
        HoaDonChiTiet saved = hoaDonChiTietRepository.save(entity);
        return convertToDTO(saved);
    }

    public void xoaHoaDonChiTiet(Integer id) {
        HoaDonChiTiet entity = hoaDonChiTietRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn chi tiết: " + id));
        // Hoàn lại số lượng tồn kho khi xóa
        SanPhamChiTiet sanPham = entity.getSanPhamct();
        sanPham.setSoLuong(sanPham.getSoLuong() + entity.getSoLuong());
        sanPhamChiTietRepository.save(sanPham);
        hoaDonChiTietRepository.deleteById(id);
    }

    public List<HoaDonChiTietDTO> getByHoaDonId(Integer idHD) {
        List<HoaDonChiTiet> list = hoaDonChiTietRepository.findByHoaDonId(idHD);
        return list.stream().map(entity -> {
            HoaDonChiTietDTO dto = convertToDTO(entity);
            // Bổ sung tên sản phẩm, size, màu nếu có
            if (entity.getSanPhamct() != null) {
                if (entity.getSanPhamct().getSanPham() != null) {
                    dto.setTenSanPham(entity.getSanPhamct().getSanPham().getTenSP());
                }
                if (entity.getSanPhamct().getSize() != null) {
                    dto.setTenSize(entity.getSanPhamct().getSize().getTenSize());
                }
                if (entity.getSanPhamct().getMauSac() != null) {
                    dto.setTenMauSac(entity.getSanPhamct().getMauSac().getTenMauSac());
                }
            }
            return dto;
        }).collect(Collectors.toList());
    }

    // Trừ tồn kho cho tất cả chi tiết khi hoàn thành hóa đơn
    public void truTonKhoKhiHoanThanhHoaDon(Integer idHD) {
        List<HoaDonChiTiet> list = hoaDonChiTietRepository.findByHoaDonId(idHD);
        for (HoaDonChiTiet ct : list) {
            SanPhamChiTiet spct = ct.getSanPhamct();
            if (spct != null && ct.getSoLuong() != null) {
                spct.setSoLuong(Math.max(0, spct.getSoLuong() - ct.getSoLuong()));
                sanPhamChiTietRepository.save(spct);
            }
        }
    }
}