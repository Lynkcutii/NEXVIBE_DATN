package com.example.datnspct.Service;

import com.example.datnspct.Model.SanPhamChiTiet;
import com.example.datnspct.Model.SanPham;
import com.example.datnspct.Model.DanhMuc;
import com.example.datnspct.Model.ThuongHieu;
import com.example.datnspct.Model.MauSac;
import com.example.datnspct.Model.ChatLieu;
import com.example.datnspct.Model.Size;
import com.example.datnspct.Repository.SanPhamChiTietRepository;
import com.example.datnspct.Repository.SanPhamRepository;
import com.example.datnspct.Repository.DanhMucRepository;
import com.example.datnspct.Repository.ThuongHieuRepository;
import com.example.datnspct.Repository.MauSacRepository;
import com.example.datnspct.Repository.ChatLieuRepository;
import com.example.datnspct.Repository.SizeRepository;
import com.example.datnspct.dto.SanPhamChiTietDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SanPhamChiTietService {

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private ThuongHieuRepository thuongHieuRepository;

    @Autowired
    private MauSacRepository mauSacRepository;

    @Autowired
    private ChatLieuRepository chatLieuRepository;

    @Autowired
    private SizeRepository sizeRepository;

    public SanPhamChiTietDTO taoSanPhamChiTiet(SanPhamChiTietDTO dto) {
        SanPhamChiTiet spct = new SanPhamChiTiet();
        spct.setMaSPCT(generateMaSPCT());
        spct.setGia(dto.getGia());
        spct.setSoLuong(dto.getSoLuong());
        spct.setMoTa(dto.getMoTa());
        spct.setTrangThai(true); // Mặc định true

        if (dto.getIdSP() != null) {
            SanPham sanPham = sanPhamRepository.findById(dto.getIdSP())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
            spct.setSanPham(sanPham);
        }
        if (dto.getIdDanhMuc() != null) {
            DanhMuc danhMuc = danhMucRepository.findById(dto.getIdDanhMuc())
                    .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
            spct.setDanhMuc(danhMuc);
        }
        if (dto.getIdThuongHieu() != null) {
            ThuongHieu thuongHieu = thuongHieuRepository.findById(dto.getIdThuongHieu())
                    .orElseThrow(() -> new RuntimeException("Thương hiệu không tồn tại"));
            spct.setThuongHieu(thuongHieu);
        }
        if (dto.getIdMauSac() != null) {
            MauSac mauSac = mauSacRepository.findById(dto.getIdMauSac())
                    .orElseThrow(() -> new RuntimeException("Màu sắc không tồn tại"));
            spct.setMauSac(mauSac);
        }
        if (dto.getIdChatLieu() != null) {
            ChatLieu chatLieu = chatLieuRepository.findById(dto.getIdChatLieu())
                    .orElseThrow(() -> new RuntimeException("Chất liệu không tồn tại"));
            spct.setChatLieu(chatLieu);
        }
        if (dto.getIdSize() != null) {
            Size size = sizeRepository.findById(dto.getIdSize())
                    .orElseThrow(() -> new RuntimeException("Kích thước không tồn tại"));
            spct.setSize(size);
        }

        SanPhamChiTiet saved = sanPhamChiTietRepository.save(spct);

        // Cập nhật tongSoLuongSanPham trong SanPham
        if (dto.getIdSP() != null) {
            SanPham sanPham = sanPhamRepository.findById(dto.getIdSP()).orElse(null);
            if (sanPham != null) {
                Integer tongSoLuong = sanPhamChiTietRepository.findBySanPhamId(dto.getIdSP())
                        .stream()
                        .mapToInt(SanPhamChiTiet::getSoLuong)
                        .sum();
                sanPham.setTongSoLuongSanPham(tongSoLuong);
                sanPhamRepository.save(sanPham);
            }
        }

        return convertToDTO(saved);
    }

    private String generateMaSPCT() {
        Long count = sanPhamChiTietRepository.count();
        return String.format("SPCT%03d", count + 1);
    }

    public SanPhamChiTietDTO laySanPhamChiTietTheoId(Integer id) {
        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm chi tiết không tồn tại"));
        return convertToDTO(spct);
    }

    public List<SanPhamChiTietDTO> laySanPhamChiTietTheoSanPham(Integer idSP) {
        List<SanPhamChiTiet> spctList = sanPhamChiTietRepository.findBySanPhamId(idSP);
        return spctList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<SanPhamChiTietDTO> layTatCaSanPhamChiTiet() {
        List<SanPhamChiTiet> spctList = sanPhamChiTietRepository.findAll();
        return spctList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public SanPhamChiTietDTO capNhatSanPhamChiTiet(Integer id, SanPhamChiTietDTO dto) {
        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm chi tiết không tồn tại"));
        spct.setMaSPCT(dto.getMaSPCT());
        spct.setGia(dto.getGia());
        spct.setSoLuong(dto.getSoLuong());
        spct.setMoTa(dto.getMoTa());
        spct.setTrangThai(true);

        if (dto.getIdSP() != null) {
            SanPham sanPham = sanPhamRepository.findById(dto.getIdSP())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
            spct.setSanPham(sanPham);
        }
        if (dto.getIdDanhMuc() != null) {
            DanhMuc danhMuc = danhMucRepository.findById(dto.getIdDanhMuc())
                    .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
            spct.setDanhMuc(danhMuc);
        }
        if (dto.getIdThuongHieu() != null) {
            ThuongHieu thuongHieu = thuongHieuRepository.findById(dto.getIdThuongHieu())
                    .orElseThrow(() -> new RuntimeException("Thương hiệu không tồn tại"));
            spct.setThuongHieu(thuongHieu);
        }
        if (dto.getIdMauSac() != null) {
            MauSac mauSac = mauSacRepository.findById(dto.getIdMauSac())
                    .orElseThrow(() -> new RuntimeException("Màu sắc không tồn tại"));
            spct.setMauSac(mauSac);
        }
        if (dto.getIdChatLieu() != null) {
            ChatLieu chatLieu = chatLieuRepository.findById(dto.getIdChatLieu())
                    .orElseThrow(() -> new RuntimeException("Chất liệu không tồn tại"));
            spct.setChatLieu(chatLieu);
        }
        if (dto.getIdSize() != null) {
            Size size = sizeRepository.findById(dto.getIdSize())
                    .orElseThrow(() -> new RuntimeException("Kích thước không tồn tại"));
            spct.setSize(size);
        }

        SanPhamChiTiet updated = sanPhamChiTietRepository.save(spct);

        // Cập nhật tongSoLuongSanPham trong SanPham
        if (dto.getIdSP() != null) {
            SanPham sanPham = sanPhamRepository.findById(dto.getIdSP()).orElse(null);
            if (sanPham != null) {
                Integer tongSoLuong = sanPhamChiTietRepository.findBySanPhamId(dto.getIdSP())
                        .stream()
                        .mapToInt(SanPhamChiTiet::getSoLuong)
                        .sum();
                sanPham.setTongSoLuongSanPham(tongSoLuong);
                sanPhamRepository.save(sanPham);
            }
        }

        return convertToDTO(updated);
    }

    public void xoaSanPhamChiTiet(Integer id) {
        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm chi tiết không tồn tại"));
        Integer idSP = spct.getSanPham().getId();
        sanPhamChiTietRepository.deleteById(id);

        // Cập nhật tongSoLuongSanPham trong SanPham
        SanPham sanPham = sanPhamRepository.findById(idSP).orElse(null);
        if (sanPham != null) {
            Integer tongSoLuong = sanPhamChiTietRepository.findBySanPhamId(idSP)
                    .stream()
                    .mapToInt(SanPhamChiTiet::getSoLuong)
                    .sum();
            sanPham.setTongSoLuongSanPham(tongSoLuong);
            sanPhamRepository.save(sanPham);
        }
    }

    public Page<SanPhamChiTietDTO> findWithFilters(String keyword, String danhMuc, String thuongHieu,
                                                   String mauSac, String chatLieu, String size,
                                                   BigDecimal minPrice, BigDecimal maxPrice,
                                                   Pageable pageable) {
        List<Integer> danhMucIds = null;
        if (danhMuc != null && !danhMuc.isEmpty()) {
            try {
                danhMucIds = Arrays.stream(danhMuc.split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                danhMucIds = List.of(Integer.parseInt(danhMuc));
            }
        }
        keyword = keyword != null ? keyword.trim() : null;
        thuongHieu = thuongHieu != null ? thuongHieu.trim().toLowerCase() : null;
        mauSac = mauSac != null ? mauSac.trim().toLowerCase() : null;
        chatLieu = chatLieu != null ? chatLieu.trim().toLowerCase() : null;
        size = size != null ? size.trim().toLowerCase() : null;

        Page<SanPhamChiTiet> page = sanPhamChiTietRepository.findByFilters(
                keyword, danhMucIds, thuongHieu, mauSac, chatLieu, size,
                minPrice != null ? minPrice : BigDecimal.ZERO,
                maxPrice != null ? maxPrice : new BigDecimal("5000000"),
                pageable);
        return page.map(this::convertToDTO);
    }

    private SanPhamChiTietDTO convertToDTO(SanPhamChiTiet spct) {
        SanPhamChiTietDTO dto = new SanPhamChiTietDTO();
        dto.setId(spct.getId());
        dto.setIdSP(spct.getSanPham() != null ? spct.getSanPham().getId() : null);
        dto.setMaSPCT(spct.getMaSPCT());
        dto.setTenSP(spct.getSanPham() != null ? spct.getSanPham().getTenSP() : null);
        dto.setGia(spct.getGia());
        dto.setSoLuong(spct.getSoLuong());
        dto.setMoTa(spct.getMoTa());
        dto.setTrangThai(spct.getTrangThai());
        dto.setTenDanhMuc(spct.getDanhMuc() != null ? spct.getDanhMuc().getTenDM() : null);
        dto.setTenThuongHieu(spct.getThuongHieu() != null ? spct.getThuongHieu().getTenThuongHieu() : null);
        dto.setTenMauSac(spct.getMauSac() != null ? spct.getMauSac().getTenMauSac() : null);
        dto.setTenChatLieu(spct.getChatLieu() != null ? spct.getChatLieu().getTenChatLieu() : null);
        dto.setTenSize(spct.getSize() != null ? spct.getSize().getTenSize() : null);
        dto.setIdDanhMuc(spct.getDanhMuc() != null ? spct.getDanhMuc().getIdDM() : null);
        dto.setIdThuongHieu(spct.getThuongHieu() != null ? spct.getThuongHieu().getIdThuongHieu() : null);
        dto.setIdMauSac(spct.getMauSac() != null ? spct.getMauSac().getIdMauSac() : null);
        dto.setIdChatLieu(spct.getChatLieu() != null ? spct.getChatLieu().getIdChatLieu() : null);
        dto.setIdSize(spct.getSize() != null ? spct.getSize().getIdSize() : null);
        return dto;
    }
}