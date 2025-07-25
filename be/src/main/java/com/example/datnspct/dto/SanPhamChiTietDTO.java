package com.example.datnspct.dto;

import com.example.datnspct.Model.SanPhamChiTiet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SanPhamChiTietDTO {
    private Integer id; // ID Sản Phẩm Chi Tiết
    private String maSPCT; // Mã Sản Phẩm Chi Tiết
    private String tenSP;
    private String tenDanhMuc;
    private String tenThuongHieu;
    private String tenMauSac;
    private String tenChatLieu;
    private String tenKichThuoc;
    private Integer idSP; // ID Sản Phẩm
    private Integer idChatLieu; // ID Chất Liệu
    private Integer idThuongHieu; // ID Thương Hiệu
    private Integer idSize; // ID Kích Thước
    private Integer idMauSac; // ID Màu Sắc
    private Integer soLuong; // Số Lượng
    private BigDecimal gia; // Giá
    private String moTa; // Mô Tả
    private Boolean trangThai; // Trạng Thái

    public SanPhamChiTietDTO(SanPhamChiTiet spct) {
        this.id = spct.getId();
        this.maSPCT = spct.getMaSPCT();
        this.tenSP = spct.getSanPham().getTenSP();
        this.tenDanhMuc = spct.getSanPham().getDanhMuc().getTenDM();
        this.tenThuongHieu = spct.getThuongHieu().getTenThuongHieu();
        this.tenMauSac = spct.getMauSac().getTenMauSac();
        this.tenChatLieu = spct.getChatLieu().getTenChatLieu();
        this.tenKichThuoc = spct.getSize().getTenSize();
        this.gia = spct.getGia();
        this.soLuong = spct.getSoLuong();
        this.idSP = spct.getSanPham().getIdSP();
        this.idChatLieu = spct.getChatLieu().getIdChatLieu();
        this.idThuongHieu = spct.getThuongHieu().getIdThuongHieu();
        this.idSize = spct.getSize().getIdSize();
        this.idMauSac = spct.getMauSac().getIdMauSac();
        this.moTa = spct.getMoTa();
        this.trangThai = spct.getTrangThai();
    }
}
