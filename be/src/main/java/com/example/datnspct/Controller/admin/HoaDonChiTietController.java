package com.example.datnspct.Controller.admin;

import com.example.datnspct.Service.HoaDonChiTietService;
import com.example.datnspct.dto.HoaDonChiTietDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/api/hoadonchitiet")
public class HoaDonChiTietController {

    @Autowired
    private HoaDonChiTietService hoaDonChiTietService;

    // Tạo mới
    @PostMapping
    public ResponseEntity<?> taoHoaDonChiTiet(@Valid @RequestBody HoaDonChiTietDTO hoaDonChiTietDTO, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest()
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .body(Map.of("errors", errors));
        }
        try {
            HoaDonChiTietDTO created = hoaDonChiTietService.createHoaDonChiTiet(hoaDonChiTietDTO);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .body(Map.of("errors", List.of(e.getMessage())));
        }
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

    // Lấy tất cả chi tiết theo id hóa đơn
    @GetMapping("/hoadonct/{idHD}")
    public ResponseEntity<List<HoaDonChiTietDTO>> getByHoaDonId(@PathVariable Integer idHD) {
        List<HoaDonChiTietDTO> list = hoaDonChiTietService.getByHoaDonId(idHD);
        return ResponseEntity.ok(list);
    }

    // Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<?> capNhatHoaDonChiTiet(@PathVariable Integer id, @Valid @RequestBody HoaDonChiTietDTO hoaDonChiTietDTO, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest()
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .body(Map.of("errors", errors));
        }
        try {
            HoaDonChiTietDTO updated = hoaDonChiTietService.capNhatHoaDonChiTiet(id, hoaDonChiTietDTO);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .body(Map.of("errors", List.of(e.getMessage())));
        }
    }

    // Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaHoaDonChiTiet(@PathVariable Integer id) {
        hoaDonChiTietService.xoaHoaDonChiTiet(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tru-ton-kho/{idHD}")
    public ResponseEntity<Void> truTonKho(@PathVariable Integer idHD) {
        try {
            hoaDonChiTietService.truTonKhoKhiHoanThanhHoaDon(idHD);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}