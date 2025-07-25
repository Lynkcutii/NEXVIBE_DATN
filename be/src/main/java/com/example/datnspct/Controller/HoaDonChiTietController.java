package com.example.datnspct.Controller;

import com.example.datnspct.dto.HoaDonChiTietDTO;
import com.example.datnspct.Service.HoaDonChiTietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hoadonchitiet")
public class HoaDonChiTietController {

    @Autowired
    private HoaDonChiTietService hoaDonChiTietService;

    // Tạo mới
    @PostMapping
    public ResponseEntity<HoaDonChiTietDTO> taoHoaDonChiTiet(@RequestBody HoaDonChiTietDTO hoaDonChiTietDTO) {
        HoaDonChiTietDTO hoaDonChiTietDaTao = hoaDonChiTietService.createHoaDonChiTiet(hoaDonChiTietDTO);
        return ResponseEntity.ok(hoaDonChiTietDaTao);
    }

//    // Lấy theo ID
    @GetMapping("/{id}")
    public ResponseEntity<HoaDonChiTietDTO> layHoaDonChiTietTheoId(@PathVariable Integer id) {
        HoaDonChiTietDTO hoaDonChiTietDTO = hoaDonChiTietService.layHoaDonChiTietTheoId(id);
        return ResponseEntity.ok(hoaDonChiTietDTO);
    }

    // Lấy tất cả
    @GetMapping
    public ResponseEntity<List<HoaDonChiTietDTO>> layTatCaHoaDonChiTiet() {
        List<HoaDonChiTietDTO> hoaDonChiTietDTOs = hoaDonChiTietService.layTatCaHoaDonChiTiet();
        return ResponseEntity.ok(hoaDonChiTietDTOs);
    }

    // Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<HoaDonChiTietDTO> capNhatHoaDonChiTiet(@PathVariable Integer id, @RequestBody HoaDonChiTietDTO hoaDonChiTietDTO) {
        HoaDonChiTietDTO hoaDonChiTietDaCapNhat = hoaDonChiTietService.capNhatHoaDonChiTiet(id, hoaDonChiTietDTO);
        return ResponseEntity.ok(hoaDonChiTietDaCapNhat);
    }

    // Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaHoaDonChiTiet(@PathVariable Integer id) {
        hoaDonChiTietService.xoaHoaDonChiTiet(id);
        return ResponseEntity.noContent().build();
    }
}