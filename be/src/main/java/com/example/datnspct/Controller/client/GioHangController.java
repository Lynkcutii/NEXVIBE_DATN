package com.example.datnspct.Controller.client;

import com.example.datnspct.Model.GioHangCT;
import com.example.datnspct.Repository.GioHangCTRepository;
import com.example.datnspct.Service.GioHangCTService;
import com.example.datnspct.Service.GioHangService;
import com.example.datnspct.dto.AddToCartRequest;
import com.example.datnspct.dto.GioHangCTDTO;
import com.example.datnspct.dto.GioHangDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/client/api/giohang")
public class GioHangController {
    @Autowired
    private GioHangService service;
    @Autowired
    private GioHangCTRepository gioHangCTRepository;
    @Autowired
    private GioHangCTService gioHangCTService;

    // Lấy hoặc tạo giỏ hàng theo idTK
    @GetMapping("/byTaiKhoanId/{idTK}")
    public ResponseEntity<GioHangDTO> getByTaiKhoanId(@PathVariable Integer idTK, Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isEmpty()) {
            System.out.println("getByTaiKhoanId: Authentication is null or empty");
            return ResponseEntity.status(401).body(null);
        }
        try {
            String taiKhoan = authentication.getName();
            System.out.println("getByTaiKhoanId: taiKhoan=" + taiKhoan + ", idTK=" + idTK + ", authorities=" + authentication.getAuthorities());
            GioHangDTO dto = service.getByTaiKhoanId(idTK);
            System.out.println("getByTaiKhoanId: Returning GioHangDTO with id=" + dto.getIdGH());
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            System.err.println("Error in getByTaiKhoanId: " + e.getMessage());
            return ResponseEntity.status(400).body(null);
        }
    }

    // Lấy hoặc tạo giỏ hàng theo taiKhoan
    @GetMapping("/byTaiKhoan")
    public ResponseEntity<GioHangDTO> getOrCreateByTaiKhoan(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isEmpty()) {
            System.out.println("getOrCreateByTaiKhoan: Authentication is null or empty");
            return ResponseEntity.status(401).body(null);
        }
        try {
            String taiKhoan = authentication.getName();
            System.out.println("getOrCreateByTaiKhoan: taiKhoan=" + taiKhoan + ", authorities=" + authentication.getAuthorities());
            GioHangDTO dto = service.getOrCreateByTaiKhoan(taiKhoan);
            System.out.println("getOrCreateByTaiKhoan: Returning GioHangDTO with id=" + dto.getIdGH());
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            System.err.println("Error in getOrCreateByTaiKhoan: " + e.getMessage());
            return ResponseEntity.status(400).body(null);
        }
    }

    // Thêm sản phẩm vào giỏ hàng
    @PostMapping("/addToCart")
    public ResponseEntity<String> addToCart(
            Authentication authentication,
            @RequestBody AddToCartRequest request) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isEmpty()) {
            System.out.println("addToCart: Authentication is null or empty");
            return ResponseEntity.status(401).body("Chưa đăng nhập");
        }
        try {
            String taiKhoan = authentication.getName();
            System.out.println("addToCart: taiKhoan=" + taiKhoan + ", authorities=" + authentication.getAuthorities());
            System.out.println("addToCart request: " + request);
            service.addToCart(
                    taiKhoan,
                    request.getIdSpct(),
                    request.getSoLuong(),
                    request.getMauSac(),
                    request.getKichThuoc()
            );
            System.out.println("addToCart: Successfully added to cart for taiKhoan=" + taiKhoan);
            return ResponseEntity.ok("Thêm vào giỏ hàng thành công");
        } catch (RuntimeException e) {
            System.err.println("Error in addToCart: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    // Cập nhật số lượng chi tiết giỏ hàng
    @PutMapping("/giohangct/{idGHCT}")
    public ResponseEntity<String> updateGioHangCT(
            @PathVariable Integer idGHCT,
            @RequestBody GioHangCTDTO request,
            Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isEmpty()) {
            System.out.println("updateGioHangCT: Authentication is null or empty");
            return ResponseEntity.status(401).body("Chưa đăng nhập");
        }
        try {
            System.out.println("updateGioHangCT: idGHCT=" + idGHCT + ", request=" + request);
            GioHangCT chiTiet = gioHangCTRepository.findById(idGHCT)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết giỏ hàng: " + idGHCT));
            chiTiet.setSoLuong(request.getSoLuong());
            chiTiet.setDonGia(request.getDonGia());
            gioHangCTRepository.save(chiTiet);
            System.out.println("updateGioHangCT: Success for idGHCT=" + idGHCT);
            return ResponseEntity.ok("Cập nhật số lượng thành công");
        } catch (RuntimeException e) {
            System.err.println("Error in updateGioHangCT: " + e.getMessage());
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    // Xóa chi tiết giỏ hàng
    @DeleteMapping("/giohangct/{idGHCT}")
    public ResponseEntity<String> deleteGioHangCT(
            @PathVariable Integer idGHCT,
            Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isEmpty()) {
            System.out.println("deleteGioHangCT: Authentication is null or empty");
            return ResponseEntity.status(401).body("Chưa đăng nhập");
        }
        try {
            System.out.println("deleteGioHangCT: Attempting to delete idGHCT=" + idGHCT);
            gioHangCTService.delete(idGHCT);
            System.out.println("deleteGioHangCT: Successfully deleted idGHCT=" + idGHCT);
            return ResponseEntity.ok("Đã xóa chi tiết giỏ hàng thành công");
        } catch (RuntimeException e) {
            System.err.println("deleteGioHangCT: Error - " + e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("deleteGioHangCT: Unexpected error - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
        }
    }

    // Xóa nhiều chi tiết giỏ hàng cùng lúc
    @DeleteMapping("/giohangct/batch")
    public ResponseEntity<String> deleteMultipleGioHangCT(
            @RequestBody List<Integer> idGHCTs,
            Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isEmpty()) {
            System.out.println("deleteMultipleGioHangCT: Authentication is null or empty");
            return ResponseEntity.status(401).body("Chưa đăng nhập");
        }
        try {
            System.out.println("deleteMultipleGioHangCT: Starting, idGHCTs=" + idGHCTs);
            if (idGHCTs == null || idGHCTs.isEmpty()) {
                System.out.println("deleteMultipleGioHangCT: Empty idGHCTs list");
                return ResponseEntity.badRequest().body("Danh sách ID không được rỗng");
            }
            int deletedCount = 0;
            List<String> errors = new ArrayList<>();
            for (Integer idGHCT : idGHCTs) {
                try {
                    gioHangCTService.delete(idGHCT);
                    deletedCount++;
                    System.out.println("deleteMultipleGioHangCT: Successfully deleted idGHCT=" + idGHCT);
                } catch (RuntimeException e) {
                    System.err.println("deleteMultipleGioHangCT: Error deleting idGHCT=" + idGHCT + " - " + e.getMessage());
                    errors.add("ID " + idGHCT + ": " + e.getMessage());
                }
            }
            String message = "Đã xóa thành công " + deletedCount + "/" + idGHCTs.size() + " sản phẩm";
            if (!errors.isEmpty()) {
                message += ". Lỗi: " + String.join(", ", errors);
                System.out.println("deleteMultipleGioHangCT: Completed with errors - " + message);
                return ResponseEntity.status(207).body(message);
            }
            System.out.println("deleteMultipleGioHangCT: Completed successfully - " + message);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            System.err.println("deleteMultipleGioHangCT: General error - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
        }
    }

    // Kiểm tra trạng thái giỏ hàng sau khi thanh toán
    @GetMapping("/status/after-order")
    public ResponseEntity<Map<String, Object>> checkStatusAfterOrder(
            @RequestParam("idGHCTs") List<Integer> idGHCTs,
            Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isEmpty()) {
            System.out.println("checkStatusAfterOrder: Authentication is null or empty");
            return ResponseEntity.status(401).body(null);
        }
        try {
            System.out.println("checkStatusAfterOrder: Checking idGHCTs=" + idGHCTs);
            Map<String, Object> result = new HashMap<>();
            List<GioHangCT> remainingItems = gioHangCTRepository.findAllById(idGHCTs);
            result.put("totalChecked", idGHCTs.size());
            result.put("stillExists", remainingItems.size());
            result.put("items", remainingItems);
            System.out.println("checkStatusAfterOrder: Completed, stillExists=" + remainingItems.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("checkStatusAfterOrder: Error - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    // Các phương thức khác giữ nguyên
    @PostMapping
    public ResponseEntity<GioHangDTO> create(@RequestBody GioHangDTO dto) {
        GioHangDTO created = service.create(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GioHangDTO> getById(@PathVariable Integer id) {
        GioHangDTO dto = service.getById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<GioHangDTO>> getAll() {
        List<GioHangDTO> list = service.getAll();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GioHangDTO> update(@PathVariable Integer id, @RequestBody GioHangDTO dto) {
        GioHangDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/giohangct/all/{idGH}")
    public ResponseEntity<String> deleteAllGioHangCT(
            @PathVariable Integer idGH,
            Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isEmpty()) {
            System.out.println("deleteAllGioHangCT: Authentication is null or empty");
            return ResponseEntity.status(401).body("Chưa đăng nhập");
        }
        try {
            System.out.println("deleteAllGioHangCT: idGH=" + idGH);
            // Kiểm tra xem giỏ hàng có tồn tại không
            GioHangDTO gioHang = service.getById(idGH);
            if (gioHang == null) {
                System.out.println("deleteAllGioHangCT: Giỏ hàng không tồn tại, idGH=" + idGH);
                return ResponseEntity.status(404).body("Giỏ hàng không tồn tại");
            }
            // Lấy danh sách GioHangCT theo idGH
            List<GioHangCT> chiTiets = gioHangCTRepository.findByGioHangIdGH(idGH);
            if (chiTiets.isEmpty()) {
                System.out.println("deleteAllGioHangCT: Không có sản phẩm trong giỏ hàng, idGH=" + idGH);
                return ResponseEntity.ok("Giỏ hàng đã trống");
            }
            // Xóa từng GioHangCT
            gioHangCTRepository.deleteAll(chiTiets);
            System.out.println("deleteAllGioHangCT: Successfully deleted " + chiTiets.size() + " GioHangCT for idGH=" + idGH);
            return ResponseEntity.ok("Đã xóa toàn bộ sản phẩm trong giỏ hàng");
        } catch (RuntimeException e) {
            System.out.println("Error in deleteAllGioHangCT: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(400).body("Lỗi: " + e.getMessage());
        }
    }
}