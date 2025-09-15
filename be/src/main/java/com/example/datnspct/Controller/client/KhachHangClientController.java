package com.example.datnspct.Controller.client;

import com.example.datnspct.Service.KhachHangService;
import com.example.datnspct.dto.KhachHangDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    // API LẤY KHÁCH HÀNG THEO idTK (Dùng sau khi đăng nhập)
    @GetMapping("/byTaiKhoanId/{idTK}")
    public ResponseEntity<?> getKhachHangByTaiKhoanId(@PathVariable Integer idTK) {
        try {
            KhachHangDTO khachHangDTO = khachHangService.layKhachHangTheoTaiKhoanId(idTK);
            return ResponseEntity.ok(khachHangDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    // API LẤY KHÁCH HÀNG THEO idKH (Dùng cho trang profile)
    @GetMapping("/{idKH}")
    public ResponseEntity<?> getKhachHangById(@PathVariable Integer idKH) {
        try {
            KhachHangDTO khachHangDTO = khachHangService.layKhachHangTheoId(idKH);
            return ResponseEntity.ok(khachHangDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    // API CẬP NHẬT THÔNG TIN KHÁCH HÀNG
    @PutMapping("/{idKH}")
    public ResponseEntity<?> updateKhachHang(@PathVariable Integer idKH, @RequestBody KhachHangDTO dto) {
        try {
            KhachHangDTO updatedKhachHang = khachHangService.capNhatKhachHang(idKH, dto);
            return ResponseEntity.ok(updatedKhachHang);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Lỗi khi cập nhật thông tin."));
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

    @PutMapping("/{idKH}/link-account")
    public ResponseEntity<?> linkAccount(@PathVariable Integer idKH, @RequestBody Map<String, Integer> linkData) {
        try {
            Integer idTK = linkData.get("idTK");
//            if (idTK == null) {
//                Logger.warn("Yêu cầu liên kết tài khoản không hợp lệ: idTK bị thiếu cho idKH={}", idKH);
//                return ResponseEntity.badRequest().body(Map.of("message", "idTK là bắt buộc"));
//            }

//            Logger.info("Liên kết idKH={} với idTK={}", idKH, idTK);

            // Lấy thông tin khách hàng hiện tại
            KhachHangDTO existingKhachHang = khachHangService.layKhachHangTheoId(idKH);

            // Tạo DTO mới với thông tin hiện tại và chỉ cập nhật idTK
            KhachHangDTO updatedKhachHangDTO = new KhachHangDTO();
            updatedKhachHangDTO.setIdKH(existingKhachHang.getIdKH());
            updatedKhachHangDTO.setMaKH(existingKhachHang.getMaKH());
            updatedKhachHangDTO.setTenKH(existingKhachHang.getTenKH());
            updatedKhachHangDTO.setGioiTinh(existingKhachHang.getGioiTinh());
            updatedKhachHangDTO.setNgaySinh(existingKhachHang.getNgaySinh());
            updatedKhachHangDTO.setEmail(existingKhachHang.getEmail());
            updatedKhachHangDTO.setSdt(existingKhachHang.getSdt());
            updatedKhachHangDTO.setIdTK(idTK); // Cập nhật idTK
            updatedKhachHangDTO.setTrangThai(existingKhachHang.getTrangThai());
            updatedKhachHangDTO.setDiaChiList(existingKhachHang.getDiaChiList());

            // Cập nhật khách hàng
            KhachHangDTO updatedKhachHang = khachHangService.capNhatKhachHang(idKH, updatedKhachHangDTO);
//            Logger.info("Liên kết tài khoản thành công cho idKH={}", idKH);
            return ResponseEntity.ok(updatedKhachHang);
        } catch (RuntimeException e) {
//            Logger.error("Lỗi khi liên kết tài khoản cho idKH={}: {}", idKH, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi khi liên kết tài khoản: " + e.getMessage()));
        }
    }

}