package com.example.datnspct.Controller;

import com.example.datnspct.Service.HoaDonChiTietService;
import com.example.datnspct.dto.HoaDonChiTietDTO;
import com.example.datnspct.dto.HoaDonDTO;
import com.example.datnspct.Service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hoadon")
public class HoaDonController {

    @Autowired
    private HoaDonService hoaDonService;
    @Autowired
    private HoaDonChiTietService hoaDonCTService;

    // Create
    @PostMapping
    public ResponseEntity<HoaDonDTO> createHoaDon(@RequestBody HoaDonDTO hoaDonDTO) {
        HoaDonDTO createdHoaDon = hoaDonService.createHoaDon(hoaDonDTO);
        return ResponseEntity.ok(createdHoaDon);
    }

    // Read (Get by ID)
    @GetMapping("/{id}")
    public ResponseEntity<HoaDonDTO> getHoaDonById(@PathVariable Integer id) {
        HoaDonDTO hoaDonDTO = hoaDonService.getHoaDonById(id);
        return ResponseEntity.ok(hoaDonDTO);
    }

    // Read (Get all)
    @GetMapping
    public ResponseEntity<List<HoaDonDTO>> getAllHoaDon() {
        List<HoaDonDTO> hoaDonDTOs = hoaDonService.getAllHoaDon();
        return ResponseEntity.ok(hoaDonDTOs);
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<HoaDonDTO> updateHoaDon(@PathVariable Integer id, @RequestBody HoaDonDTO hoaDonDTO) {
        HoaDonDTO updatedHoaDon = hoaDonService.updateHoaDon(id, hoaDonDTO);
        return ResponseEntity.ok(updatedHoaDon);
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHoaDon(@PathVariable Integer id) {
        hoaDonService.deleteHoaDon(id);
        return ResponseEntity.noContent().build();
    }
//    @GetMapping("/{id}/chitiet")
//    public ResponseEntity<List<HoaDonChiTietDTO>> getChiTietHoaDonByHoaDonId(@PathVariable Integer id) {
//        List<HoaDonChiTietDTO> chiTietList = hoaDonCTService.getChiTietByHoaDonId(id);
//        return ResponseEntity.ok(chiTietList);
//    }
}