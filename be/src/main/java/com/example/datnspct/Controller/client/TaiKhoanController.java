package com.example.datnspct.Controller.client;

import com.example.datnspct.Service.TaiKhoanService;
import com.example.datnspct.dto.ChangePasswordRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/client/api/taikhoan")
@CrossOrigin(origins = {"http://localhost:5175", "http://127.0.0.1:5175"})
public class TaiKhoanController {

    @Autowired
    private TaiKhoanService taiKhoanService;


    @PutMapping("/{idTK}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable Integer idTK,
            @RequestBody ChangePasswordRequestDTO request) {
        try {
            taiKhoanService.changePassword(idTK, request);
            return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công."));
        } catch (IllegalArgumentException e) {
            // Lỗi do logic (sai mật khẩu, mật khẩu không hợp lệ)
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (EntityNotFoundException e) {
            // Lỗi không tìm thấy tài nguyên
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }
}
