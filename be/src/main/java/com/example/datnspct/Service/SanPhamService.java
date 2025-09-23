package com.example.datnspct.Service;

import com.example.datnspct.Model.SanPham;
import com.example.datnspct.Model.SanPhamChiTiet;
import com.example.datnspct.Model.DanhMuc;
import com.example.datnspct.Model.ThuongHieu;
import com.example.datnspct.Model.ChatLieu;
import com.example.datnspct.Repository.SanPhamChiTietRepository;
import com.example.datnspct.Repository.SanPhamRepository;
import com.example.datnspct.Repository.DanhMucRepository;
import com.example.datnspct.Repository.ThuongHieuRepository;
import com.example.datnspct.Repository.ChatLieuRepository;
import com.example.datnspct.dto.SanPhamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SanPhamService {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private ThuongHieuRepository thuongHieuRepository;

    @Autowired
    private ChatLieuRepository chatLieuRepository;

    private SanPhamDTO chuyenSangDTO(SanPham sanPham) {
        SanPhamDTO dto = new SanPhamDTO();
        dto.setIdSP(sanPham.getId());
        dto.setMaSP(sanPham.getMaSP());
        dto.setTenSP(sanPham.getTenSP());
        dto.setNgayTao(sanPham.getNgayTao());
        dto.setMoTa(sanPham.getMoTa());
        dto.setTrangThai(sanPham.getTrangThai());

        // Tính giá trung bình từ SanPhamChiTiet
        List<SanPhamChiTiet> chiTiets = sanPhamChiTietRepository.findBySanPhamId(sanPham.getId());
        if (!chiTiets.isEmpty()) {
            BigDecimal giaTrungBinh = chiTiets.stream()
                    .map(SanPhamChiTiet::getGia)
                    .filter(gia -> gia != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(chiTiets.size()), 2, BigDecimal.ROUND_HALF_UP);
            dto.setGia(giaTrungBinh);
        } else {
            dto.setGia(BigDecimal.ZERO); // Mặc định là 0 nếu chưa có chi tiết
        }

        // Thiết lập các trường quan hệ
        if (sanPham.getDanhMuc() != null) {
            dto.setIdDanhMuc(sanPham.getDanhMuc().getIdDM());
        }
        if (sanPham.getThuongHieu() != null) {
            dto.setIdThuongHieu(sanPham.getThuongHieu().getIdThuongHieu());
        }
        if (sanPham.getChatLieu() != null) {
            dto.setIdChatLieu(sanPham.getChatLieu().getIdChatLieu());
        }

        Integer tongSoLuong = sanPhamChiTietRepository.findBySanPhamId(sanPham.getId())
                .stream()
                .mapToInt(SanPhamChiTiet::getSoLuong)
                .sum();
        dto.setTongSoLuongSanPham(tongSoLuong);
        dto.setImageLink(sanPham.getImg());
        return dto;
    }

    private SanPham chuyenSangEntity(SanPhamDTO dto) {
        SanPham sanPham = new SanPham();
        sanPham.setId(dto.getIdSP());
        sanPham.setMaSP(dto.getMaSP());
        sanPham.setTenSP(dto.getTenSP());
        sanPham.setNgayTao(dto.getNgayTao());
        sanPham.setMoTa(dto.getMoTa());
        sanPham.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : true);
        sanPham.setGia(dto.getGia() != null ? dto.getGia() : BigDecimal.ZERO); // Giá sẽ được cập nhật từ chi tiết
        sanPham.setTongSoLuongSanPham(0);
        sanPham.setImg(dto.getImageLink());
        return sanPham;
    }

    public SanPhamDTO taoSanPham(SanPhamDTO dto) {
        SanPham sanPham = new SanPham();
        sanPham.setTenSP(dto.getTenSP());
        sanPham.setMoTa(dto.getMoTa());
        sanPham.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : true);
        sanPham.setMaSP(generateMaSP());
        sanPham.setNgayTao(new java.util.Date());
        sanPham.setGia(BigDecimal.ZERO); // Ban đầu để 0, sẽ tính lại sau
        sanPham.setTongSoLuongSanPham(0);
        sanPham.setImg(dto.getImageLink());

        // Thiết lập các trường quan hệ
        if (dto.getIdDanhMuc() != null) {
            DanhMuc danhMuc = danhMucRepository.findById(dto.getIdDanhMuc())
                    .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
            sanPham.setDanhMuc(danhMuc);
        }

        if (dto.getIdThuongHieu() != null) {
            ThuongHieu thuongHieu = thuongHieuRepository.findById(dto.getIdThuongHieu())
                    .orElseThrow(() -> new RuntimeException("Thương hiệu không tồn tại"));
            sanPham.setThuongHieu(thuongHieu);
        }

        if (dto.getIdChatLieu() != null) {
            ChatLieu chatLieu = chatLieuRepository.findById(dto.getIdChatLieu())
                    .orElseThrow(() -> new RuntimeException("Chất liệu không tồn tại"));
            sanPham.setChatLieu(chatLieu);
        }

        SanPham sanPhamDaLuu = sanPhamRepository.save(sanPham);
        return chuyenSangDTO(sanPhamDaLuu);
    }

    private String generateMaSP() {
        Long count = sanPhamRepository.count();
        return String.format("SP%03d", count + 1);
    }

    public SanPhamDTO laySanPhamTheoId(Integer id) {
        SanPham sanPham = sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        return chuyenSangDTO(sanPham);
    }

    public List<SanPhamDTO> layTatCaSanPham() {
        return sanPhamRepository.findAll().stream()
                .map(this::chuyenSangDTO)
                .collect(Collectors.toList());
    }

    public Page<SanPhamDTO> findByFilters(Pageable pageable, String keyword, Boolean status, Integer[] danhMuc, Integer[] thuongHieu, Integer[] chatLieu, BigDecimal minPrice, BigDecimal maxPrice) {
        Page<SanPham> sanPhamPage = sanPhamRepository.findByFilters(keyword, status, danhMuc, thuongHieu, chatLieu, minPrice, maxPrice, pageable);
        return sanPhamPage.map(this::chuyenSangDTO);
    }

    public SanPhamDTO capNhatSanPham(Integer id, SanPhamDTO dto) {
        SanPham sanPham = sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        sanPham.setTenSP(dto.getTenSP());
        sanPham.setMoTa(dto.getMoTa());
        sanPham.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : true);
        sanPham.setImg(dto.getImageLink());

        // Không cập nhật giá trực tiếp, để giá được tính từ SanPhamChiTiet
        // Thiết lập các trường quan hệ
        if (dto.getIdDanhMuc() != null) {
            DanhMuc danhMuc = danhMucRepository.findById(dto.getIdDanhMuc())
                    .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
            sanPham.setDanhMuc(danhMuc);
        }

        if (dto.getIdThuongHieu() != null) {
            ThuongHieu thuongHieu = thuongHieuRepository.findById(dto.getIdThuongHieu())
                    .orElseThrow(() -> new RuntimeException("Thương hiệu không tồn tại"));
            sanPham.setThuongHieu(thuongHieu);
        }

        if (dto.getIdChatLieu() != null) {
            ChatLieu chatLieu = chatLieuRepository.findById(dto.getIdChatLieu())
                    .orElseThrow(() -> new RuntimeException("Chất liệu không tồn tại"));
            sanPham.setChatLieu(chatLieu);
        }

        Integer tongSoLuong = sanPhamChiTietRepository.findBySanPhamId(id)
                .stream()
                .mapToInt(SanPhamChiTiet::getSoLuong)
                .sum();
        sanPham.setTongSoLuongSanPham(tongSoLuong);
        SanPham sanPhamDaCapNhat = sanPhamRepository.save(sanPham);
        return chuyenSangDTO(sanPhamDaCapNhat);
    }

    public void xoaSanPham(Integer id) {
        SanPham sanPham = sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        sanPhamRepository.delete(sanPham);
    }

    public Page<SanPhamDTO> findAll(String tenSP, Boolean status, Pageable pageable) {
        // Xử lý trường hợp status null
        if (status == null) {
            return sanPhamRepository.findByTenSPContainingAndTrangThai(tenSP != null ? tenSP : "", null, pageable)
                    .map(this::chuyenSangDTO);
        } else {
            return sanPhamRepository.findByTenSPContainingAndTrangThai(tenSP != null ? tenSP : "", status, pageable)
                    .map(this::chuyenSangDTO);
        }
    }

    public List<SanPhamDTO> searchProductsByKeyword(String keyword) {
        return sanPhamRepository.findByTenSPContainingIgnoreCase(keyword)
                .stream()
                .map(this::chuyenSangDTO)
                .collect(Collectors.toList());
    }
}