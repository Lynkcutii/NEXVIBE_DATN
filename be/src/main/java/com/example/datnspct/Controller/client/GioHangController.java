package com.example.datnspct.Controller.client;

import com.example.datnspct.Model.GioHangCT;
import com.example.datnspct.Repository.GioHangCTRepository;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/client/api/giohang")
public class GioHangController {
    @Autowired
    private GioHangService service;
    @Autowired
    private GioHangCTRepository gioHangCTRepository;

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
            System.out.println("Error in getByTaiKhoanId: " + e.getMessage());
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
            System.out.println("Error in getOrCreateByTaiKhoan: " + e.getMessage());
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
            System.out.println("Error in addToCart: " + e.getMessage());
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
            System.out.println("Error in updateGioHangCT: " + e.getMessage());
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    // Xóa chi tiết giỏ hàng
    @DeleteMapping("/giohangct/{idGHCT}")
    public ResponseEntity<Void> deleteGioHangCT(
            @PathVariable Integer idGHCT,
            Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isEmpty()) {
            System.out.println("deleteGioHangCT: Authentication is null or empty");
            return ResponseEntity.status(401).build();
        }
        try {
            System.out.println("deleteGioHangCT: idGHCT=" + idGHCT);
            gioHangCTRepository.deleteById(idGHCT);
            System.out.println("deleteGioHangCT: Success for idGHCT=" + idGHCT);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            System.out.println("Error in deleteGioHangCT: " + e.getMessage());
            return ResponseEntity.status(400).build();
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
}