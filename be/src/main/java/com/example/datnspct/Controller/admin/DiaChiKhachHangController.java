package com.example.datnspct.Controller.admin;

import com.example.datnspct.Model.DiaChiKhachHang;
import com.example.datnspct.Model.KhachHang;
import com.example.datnspct.Repository.DiaChiKhachHangRepository;
import com.example.datnspct.Repository.KhachHangRepository;
import com.example.datnspct.dto.DiaChiKhachHangDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dia-chi-khach-hang")
public class DiaChiKhachHangController {

    @Autowired
    private DiaChiKhachHangRepository diaChiKhachHangRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    // Create
    @PostMapping
    public ResponseEntity<?> createDiaChi(@RequestBody DiaChiKhachHangDTO dto) {
        try {
            KhachHang khachHang = khachHangRepository.findById(dto.getIdKH())
                    .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

            DiaChiKhachHang diaChi = new DiaChiKhachHang();
            diaChi.setKhachHang(khachHang);
            diaChi.setDiaChiCuThe(dto.getDiaChiCuThe());
            diaChi.setTinhThanh(dto.getTinhThanh());
            diaChi.setPhuongXa(dto.getPhuongXa());
            diaChi.setSoDienThoai(dto.getSoDienThoai());
            diaChi.setGhiChu(dto.getGhiChu());
            diaChi.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : 0);

            DiaChiKhachHang savedDiaChi = diaChiKhachHangRepository.save(diaChi);
            return ResponseEntity.ok(convertToDTO(savedDiaChi));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi khi tạo địa chỉ: " + e.getMessage()));
        }
    }

    // Read (Get by IdKH)
    @GetMapping("/khach-hang/{idKH}")
    public ResponseEntity<?> getDiaChiByKhachHang(@PathVariable Integer idKH) {
        try {
            List<DiaChiKhachHang> diaChis = diaChiKhachHangRepository.findAll()
                    .stream()
                    .filter(dc -> dc.getKhachHang().getIdKH().equals(idKH))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(diaChis.stream().map(this::convertToDTO).collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi khi lấy danh sách địa chỉ: " + e.getMessage()));
        }
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDiaChi(@PathVariable Integer id, @RequestBody DiaChiKhachHangDTO dto) {
        try {
            DiaChiKhachHang diaChi = diaChiKhachHangRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại"));

            diaChi.setDiaChiCuThe(dto.getDiaChiCuThe());
            diaChi.setTinhThanh(dto.getTinhThanh());
            diaChi.setPhuongXa(dto.getPhuongXa());
            diaChi.setSoDienThoai(dto.getSoDienThoai());
            diaChi.setGhiChu(dto.getGhiChu());
            diaChi.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : 0);

            DiaChiKhachHang updatedDiaChi = diaChiKhachHangRepository.save(diaChi);
            return ResponseEntity.ok(convertToDTO(updatedDiaChi));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi khi cập nhật địa chỉ: " + e.getMessage()));
        }
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDiaChi(@PathVariable Integer id) {
        try {
            DiaChiKhachHang diaChi = diaChiKhachHangRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại"));
            diaChiKhachHangRepository.delete(diaChi);
            return ResponseEntity.ok(Map.of("message", "Xóa địa chỉ thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi khi xóa địa chỉ: " + e.getMessage()));
        }
    }

    private DiaChiKhachHangDTO convertToDTO(DiaChiKhachHang diaChi) {
        DiaChiKhachHangDTO dto = new DiaChiKhachHangDTO();
        dto.setIdDiaChi(diaChi.getIdDiaChi());
        dto.setIdKH(diaChi.getKhachHang().getIdKH());
        dto.setDiaChiCuThe(diaChi.getDiaChiCuThe());
        dto.setTinhThanh(diaChi.getTinhThanh());
        dto.setPhuongXa(diaChi.getPhuongXa());
        dto.setSoDienThoai(diaChi.getSoDienThoai());
        dto.setGhiChu(diaChi.getGhiChu());
        dto.setTrangThai(diaChi.getTrangThai());
        return dto;
    }
}
