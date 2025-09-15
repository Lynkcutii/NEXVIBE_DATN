package com.example.datnspct.dto;
import com.example.datnspct.Model.HoaDonChiTiet;
import com.example.datnspct.Repository.HoaDonChiTietRepository;
import lombok.Data;
import java.util.List;

@Data
public class OrderDetailResponseDTO {
    private HoaDonDTO hoaDon;
    private List<HoaDonChiTietDTO> chiTiet;
    public void setHoaDon(HoaDonDTO hoaDon) {
        this.hoaDon = hoaDon;
    }

    public void setChiTiet(List<HoaDonChiTietDTO> chiTiet) {
        this.chiTiet = chiTiet;
    }

    // (Tùy chọn, thêm cả getter)
    public HoaDonDTO getHoaDon() {
        return hoaDon;
    }

    public List<HoaDonChiTietDTO> getChiTiet() {
        return chiTiet;
    }
}
