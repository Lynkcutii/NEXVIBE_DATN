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

    public HoaDonChiTietDTO createHoaDonChiTiet(HoaDonChiTietDTO dto) {
        System.out.println("Nhận yêu cầu tạo chi tiết hóa đơn: " + dto);

        // Kiểm tra tính hợp lệ
        HoaDon hoaDon = hoaDonRepository.findById(dto.getIdHD())
                .orElseThrow(() -> new IllegalArgumentException("Hóa đơn không tồn tại: " + dto.getIdHD()));
        SanPhamChiTiet sanPham = sanPhamChiTietRepository.findById(dto.getIdCtSanPham())
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm chi tiết không tồn tại: " + dto.getIdCtSanPham()));

        // Kiểm tra tồn kho
        if (sanPham.getSoLuong() < dto.getSoLuong()) {
            throw new IllegalArgumentException("Số lượng tồn kho không đủ: " + sanPham.getSoLuong() + " cho sản phẩm " + dto.getIdCtSanPham());
        }

        HoaDonChiTiet entity = new HoaDonChiTiet();
        entity.setHoaDon(hoaDon);
        entity.setSanPhamct(sanPham);
        entity.setSoLuong(dto.getSoLuong());
        entity.setDonGia(dto.getDonGia());
        entity.setThanhTien(dto.getThanhTien() != null ? dto.getThanhTien() : dto.getDonGia().multiply(BigDecimal.valueOf(dto.getSoLuong())));
        entity.setNgayTao(dto.getNgayTao() != null ? dto.getNgayTao() : LocalDateTime.now());
        entity.setNgaySua(LocalDateTime.now());

        HoaDonChiTiet saved = hoaDonChiTietRepository.save(entity);
        System.out.println("Lưu chi tiết hóa đơn thành công: " + saved);
        return convertToDTO(saved);
    }

    private HoaDonChiTietDTO convertToDTO(HoaDonChiTiet entity) {
        HoaDonChiTietDTO dto = new HoaDonChiTietDTO();
        dto.setIdHDCT(entity.getIdHDCT());
        dto.setIdHD(entity.getHoaDon().getIdHD());
        dto.setIdCtSanPham(entity.getSanPhamct().getId());
        dto.setIdSP(entity.getSanPhamct().getSanPham().getId() != null ? entity.getSanPhamct().getSanPham().getId() : null);
        dto.setMaSPCT(entity.getSanPhamct().getMaSPCT());
        dto.setTenSanPham(entity.getSanPhamct().getSanPham() != null ? entity.getSanPhamct().getSanPham().getTenSP() : "N/A");
        dto.setTenSize(entity.getSanPhamct().getSize() != null ? entity.getSanPhamct().getSize().getTenSize() : "N/A");
        dto.setTenMauSac(entity.getSanPhamct().getMauSac() != null ? entity.getSanPhamct().getMauSac().getTenMauSac() : "N/A");
        dto.setTenThuongHieu(entity.getSanPhamct().getSanPham() != null && entity.getSanPhamct().getSanPham().getThuongHieu() != null
                ? entity.getSanPhamct().getSanPham().getThuongHieu().getTenThuongHieu() : "N/A");
        dto.setTenChatLieu(entity.getSanPhamct().getSanPham() != null && entity.getSanPhamct().getSanPham().getChatLieu() != null
                ? entity.getSanPhamct().getSanPham().getChatLieu().getTenChatLieu() : "N/A");
        dto.setAnhGiay(entity.getSanPhamct().getSanPham() != null && entity.getSanPhamct().getSanPham().getImg() != null
                ? entity.getSanPhamct().getSanPham().getImg() : "https://via.placeholder.com/60");
        dto.setSoLuongTonKho(entity.getSanPhamct().getSoLuong());
        dto.setSoLuong(entity.getSoLuong());
        dto.setDonGia(entity.getDonGia());
        dto.setThanhTien(entity.getThanhTien());
        dto.setNgayTao(entity.getNgayTao());
        dto.setNgaySua(entity.getNgaySua());
        dto.setTrangThai(true);
        dto.setIdVoucher(entity.getIdVoucher()); // Trả về idVoucher
        return dto;
    }

    public HoaDonChiTietDTO layHoaDonChiTietTheoId(Integer id) {
        HoaDonChiTiet entity = hoaDonChiTietRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn chi tiết: " + id));
        return convertToDTO(entity);
    }

    public List<HoaDonChiTietDTO> layTatCaHoaDonChiTiet() {
        return hoaDonChiTietRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public HoaDonChiTietDTO capNhatHoaDonChiTiet(Integer id, HoaDonChiTietDTO dto) {
        HoaDon hoaDon = hoaDonRepository.findById(dto.getIdHD())
                .orElseThrow(() -> new IllegalArgumentException("Hóa đơn không tồn tại: " + dto.getIdHD()));
        HoaDonChiTiet entity = hoaDonChiTietRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn chi tiết: " + id));
        SanPhamChiTiet sanPham = sanPhamChiTietRepository.findById(dto.getIdCtSanPham())
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm chi tiết không tồn tại: " + dto.getIdCtSanPham()));

        // Kiểm tra tồn kho
        if (sanPham.getSoLuong() < dto.getSoLuong()) {
            throw new IllegalArgumentException("Số lượng tồn kho không đủ: " + sanPham.getSoLuong() + " cho sản phẩm " + dto.getIdCtSanPham());
        }

        entity.setHoaDon(hoaDon);
        entity.setSanPhamct(sanPham);
        entity.setSoLuong(dto.getSoLuong());
        entity.setDonGia(dto.getDonGia());
        entity.setThanhTien(dto.getThanhTien() != null ? dto.getThanhTien() : dto.getDonGia().multiply(BigDecimal.valueOf(dto.getSoLuong())));
        entity.setNgaySua(LocalDateTime.now());
        entity.setIdVoucher(dto.getIdVoucher()); // Cập nhật idVoucher từ DTO
        HoaDonChiTiet saved = hoaDonChiTietRepository.save(entity);
        return convertToDTO(saved);
    }

    public void truTonKhoKhiHoanThanhHoaDon(Integer idHD) {
        HoaDon hoaDon = hoaDonRepository.findById(idHD)
                .orElseThrow(() -> new IllegalArgumentException("Hóa đơn không tồn tại: " + idHD));
        if (!"HOAN_THANH".equals(hoaDon.getTrangThai())) {
            throw new IllegalStateException("Hóa đơn phải ở trạng thái HOAN_THANH để trừ tồn kho");
        }
        List<HoaDonChiTiet> chiTietList = hoaDonChiTietRepository.findByHoaDonId(idHD);
        for (HoaDonChiTiet chiTiet : chiTietList) {
            SanPhamChiTiet sanPham = chiTiet.getSanPhamct();
            int soLuong = chiTiet.getSoLuong();
            System.out.println("truTonKhoKhiHoanThanhHoaDon: Số lượng trước khi trừ: " + sanPham.getSoLuong() + ", soLuong: " + soLuong + ", idSPCT: " + sanPham.getId());
            if (sanPham.getSoLuong() < soLuong) {
                throw new IllegalArgumentException("Số lượng tồn kho không đủ cho sản phẩm idSPCT: " + sanPham.getId() +
                        ". Tồn kho hiện tại: " + sanPham.getSoLuong());
            }
            sanPham.setSoLuong(sanPham.getSoLuong() - soLuong);
            sanPhamChiTietRepository.save(sanPham);
            System.out.println("truTonKhoKhiHoanThanhHoaDon: Số lượng sau khi trừ: " + sanPham.getSoLuong() + ", idSPCT: " + sanPham.getId());
        }
    }

    public void xoaHoaDonChiTiet(Integer id) {
        HoaDonChiTiet entity = hoaDonChiTietRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn chi tiết: " + id));
        SanPhamChiTiet sanPham = entity.getSanPhamct();
        sanPham.setSoLuong(sanPham.getSoLuong() + entity.getSoLuong());
        sanPhamChiTietRepository.save(sanPham);
        hoaDonChiTietRepository.deleteById(id);
    }

    public List<HoaDonChiTietDTO> getByHoaDonId(Integer idHD) {
        List<HoaDonChiTiet> list = hoaDonChiTietRepository.findByHoaDonId(idHD);
        return list.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}