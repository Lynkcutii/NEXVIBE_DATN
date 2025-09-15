package com.example.datnspct.Controller.client;
import com.example.datnspct.Service.DiaChiKhachHangService;
import com.example.datnspct.dto.DiaChiKhachHangDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/client/api/diachi")
public class DiaChiKhachHangClientController {
    @Autowired
    private DiaChiKhachHangService diaChiService;

    @GetMapping("/{idKH}") // <-- API mà frontend đang gọi
    public ResponseEntity<List<DiaChiKhachHangDTO>> getAddressesByCustomerId(@PathVariable Integer idKH) {
        List<DiaChiKhachHangDTO> addresses = diaChiService.findByKhachHangId(idKH);
        return ResponseEntity.ok(addresses);
    }
    @PostMapping
    public ResponseEntity<?> addAddress(@RequestBody DiaChiKhachHangDTO dto) {
        try {
            DiaChiKhachHangDTO newAddress = diaChiService.createAddress(dto);
            return ResponseEntity.ok(newAddress);
        } catch (EntityNotFoundException e) {
            // Trả về lỗi 404 (Not Found) nếu không tìm thấy khách hàng
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            // Lỗi chung
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Lỗi khi thêm địa chỉ."));
        }
    }

    @PutMapping("/{idDiaChi}")
    public ResponseEntity<?> updateAddress(@PathVariable Integer idDiaChi, @RequestBody DiaChiKhachHangDTO dto) {
        try {
            DiaChiKhachHangDTO updatedAddress = diaChiService.updateAddress(idDiaChi, dto);
            return ResponseEntity.ok(updatedAddress);
        } catch (EntityNotFoundException e) {
            // Trả về lỗi 404 (Not Found) nếu không tìm thấy địa chỉ
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            // Lỗi chung
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Lỗi khi cập nhật địa chỉ."));
        }
    }

}
