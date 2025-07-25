package com.example.datnspct.Service;

import com.example.datnspct.Model.ChatLieu;
import com.example.datnspct.Model.MauSac;
import com.example.datnspct.Model.Size;
import com.example.datnspct.Model.ThuongHieu;
import com.example.datnspct.dto.SanPhamChiTietDTO;
import com.example.datnspct.Model.SanPham;
import com.example.datnspct.Model.SanPhamChiTiet;
import com.example.datnspct.Repository.SanPhamChiTietRepository;
import com.example.datnspct.Repository.SanPhamRepository;
import com.example.datnspct.Repository.ChatLieuRepository;
import com.example.datnspct.Repository.ThuongHieuRepository;
import com.example.datnspct.Repository.SizeRepository;
import com.example.datnspct.Repository.MauSacRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SanPhamChiTietService {

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private ChatLieuRepository chatLieuRepository;

    @Autowired
    private ThuongHieuRepository thuongHieuRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private MauSacRepository mauSacRepository;

    // Ánh xạ thủ công: Từ Entity sang DTO
    private SanPhamChiTietDTO chuyenSangDTO(SanPhamChiTiet sanPhamChiTiet) {
        return new SanPhamChiTietDTO(sanPhamChiTiet);
    }

    // Ánh xạ thủ công: Từ DTO sang Entity
    private SanPhamChiTiet chuyenSangEntity(SanPhamChiTietDTO dto) {
        SanPhamChiTiet sanPhamChiTiet = new SanPhamChiTiet();
        sanPhamChiTiet.setId(dto.getId());
        sanPhamChiTiet.setMaSPCT(dto.getMaSPCT());
        // Lấy entity từ repository
        SanPham sanPham = sanPhamRepository.findById(dto.getIdSP())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        sanPhamChiTiet.setSanPham(sanPham);
        ChatLieu chatLieu = chatLieuRepository.findById(dto.getIdChatLieu())
                .orElseThrow(() -> new RuntimeException("Chất liệu không tồn tại"));
        sanPhamChiTiet.setChatLieu(chatLieu);
        ThuongHieu thuongHieu = thuongHieuRepository.findById(dto.getIdThuongHieu())
                .orElseThrow(() -> new RuntimeException("Thương hiệu không tồn tại"));
        sanPhamChiTiet.setThuongHieu(thuongHieu);
        Size size = sizeRepository.findById(dto.getIdSize())
                .orElseThrow(() -> new RuntimeException("Size không tồn tại"));
        sanPhamChiTiet.setSize(size);
        MauSac mauSac = mauSacRepository.findById(dto.getIdMauSac())
                .orElseThrow(() -> new RuntimeException("Màu sắc không tồn tại"));
        sanPhamChiTiet.setMauSac(mauSac);
        sanPhamChiTiet.setSoLuong(dto.getSoLuong());
        sanPhamChiTiet.setGia(dto.getGia());
        sanPhamChiTiet.setMoTa(dto.getMoTa());
        sanPhamChiTiet.setTrangThai(dto.getTrangThai());
        return sanPhamChiTiet;
    }

    // Tạo mới
    public SanPhamChiTietDTO taoSanPhamChiTiet(SanPhamChiTietDTO dto) {
        // Kiểm tra sản phẩm tồn tại
        sanPhamRepository.findById(dto.getIdSP())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        SanPhamChiTiet sanPhamChiTiet = chuyenSangEntity(dto);
        SanPhamChiTiet sanPhamChiTietDaLuu = sanPhamChiTietRepository.save(sanPhamChiTiet);
        return chuyenSangDTO(sanPhamChiTietDaLuu);
    }

    // Lấy theo ID
    public SanPhamChiTietDTO laySanPhamChiTietTheoId(Integer id) {
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm chi tiết không tồn tại"));
        return chuyenSangDTO(sanPhamChiTiet);
    }

    // Lấy tất cả
    public List<SanPhamChiTietDTO> layTatCaSanPhamChiTiet() {
        return sanPhamChiTietRepository.findAll().stream()
                .map(this::chuyenSangDTO)
                .collect(Collectors.toList());
    }

    // Cập nhật
    public SanPhamChiTietDTO capNhatSanPhamChiTiet(Integer id, SanPhamChiTietDTO dto) {
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm chi tiết không tồn tại"));
        sanPhamChiTiet.setMaSPCT(dto.getMaSPCT());
        sanPhamChiTiet.setSoLuong(dto.getSoLuong());
        sanPhamChiTiet.setGia(dto.getGia());
        sanPhamChiTiet.setMoTa(dto.getMoTa());
        sanPhamChiTiet.setTrangThai(dto.getTrangThai());
        // Kiểm tra sản phẩm tồn tại
        sanPhamRepository.findById(dto.getIdSP())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        SanPhamChiTiet sanPhamChiTietDaCapNhat = sanPhamChiTietRepository.save(sanPhamChiTiet);
        return chuyenSangDTO(sanPhamChiTietDaCapNhat);
    }

    // Xóa
    public void xoaSanPhamChiTiet(Integer id) {
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm chi tiết không tồn tại"));
        sanPhamChiTietRepository.delete(sanPhamChiTiet);
    }
}