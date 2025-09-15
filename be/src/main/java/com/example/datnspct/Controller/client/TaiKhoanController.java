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
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
