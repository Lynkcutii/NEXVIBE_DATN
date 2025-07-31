package com.example.datnspct.Controller.client;

import com.example.datnspct.Service.KhachHangService;
import com.example.datnspct.dto.KhachHangDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/client/api/khachhang")
public class KhachHangClientController {

    @Autowired
    private KhachHangService khachHangService;

    // Lấy thông tin khách hàng theo idTK
    @GetMapping("/byTaiKhoanId/{idTK}")
    public ResponseEntity<KhachHangDTO> getKhachHangByTaiKhoanId(@PathVariable Integer idTK) {
        try {
            KhachHangDTO khachHangDTO = khachHangService.layKhachHangTheoTaiKhoanId(idTK);
            return ResponseEntity.ok(khachHangDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<?> createKhachHang(@RequestBody KhachHangDTO khachHangDTO) {
        try {
            System.out.println("Creating customer: " + khachHangDTO);
            KhachHangDTO createdKhachHang = khachHangService.taoKhachHang(khachHangDTO);
            return ResponseEntity.ok(createdKhachHang);
        } catch (RuntimeException e) {
            System.out.println("Error creating customer: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi khi tạo khách hàng: " + e.getMessage()));
        }
    }

    // Liên kết tài khoản với khách hàng
    @PutMapping("/{idKH}/link-account")
    public ResponseEntity<?> linkAccount(@PathVariable Integer idKH, @RequestBody Map<String, Integer> linkData) {
        try {
            Integer idTK = linkData.get("idTK");
            System.out.println("Linking idKH=" + idKH + " with idTK=" + idTK);
            KhachHangDTO updatedKhachHang = khachHangService.capNhatKhachHang(idKH, new KhachHangDTO(idKH, null, null, null, null, null, idTK, null));
            return ResponseEntity.ok(updatedKhachHang);
        } catch (RuntimeException e) {
            System.out.println("Error linking account: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi khi liên kết tài khoản: " + e.getMessage()));
        }
    }
}