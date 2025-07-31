package com.example.datnspct.Controller.client;


import com.example.datnspct.Service.HoaDonService;
import com.example.datnspct.dto.HoaDonDTO;
import com.example.datnspct.dto.OrderRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client/api/hoadon")
public class HoaDonClientController {
    @Autowired
    private HoaDonService hoaDonService;

    @PostMapping
    public ResponseEntity<HoaDonDTO> createHoaDon(@RequestBody OrderRequestDTO request) {
        try {
            var hoaDon = hoaDonService.createHoaDon(request);
            HoaDonDTO dto = new HoaDonDTO();
            dto.setIdHD(hoaDon.getIdHD());
            dto.setMaHD(hoaDon.getMaHD());
            dto.setIdKhachHang(hoaDon.getIdKhachHang());
            dto.setIdNhanVien(hoaDon.getIdNhanVien());
            dto.setNgayTao(hoaDon.getNgayTao());
            dto.setNgaySua(hoaDon.getNgaySua());
            dto.setTongTien(hoaDon.getTongTien());
            dto.setTrangThai(hoaDon.getTrangThai());
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}