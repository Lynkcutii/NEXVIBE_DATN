package com.example.datnspct.Controller.login;

import com.example.datnspct.Model.KhachHang;
import com.example.datnspct.Model.NhanVien;
import com.example.datnspct.Model.TaiKhoan;
import com.example.datnspct.Repository.KhachHangRepository;
import com.example.datnspct.Repository.NhanVienRepository;
import com.example.datnspct.Repository.login.TaiKhoanRepository;
import com.example.datnspct.dto.login.LoginRequest;
import com.example.datnspct.dto.login.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final TaiKhoanRepository taiKhoanRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthController(TaiKhoanRepository taiKhoanRepository,
                          KhachHangRepository khachHangRepository,
                          NhanVienRepository nhanVienRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.khachHangRepository = khachHangRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/registerAdmin")
    public ResponseEntity<?> registerAdmin(@RequestBody RegisterRequest registerRequest) {
        try {
            // Kiểm tra tài khoản đã tồn tại
            if (taiKhoanRepository.findByTaiKhoan(registerRequest.getTaiKhoan()).isPresent()) {
                return ResponseEntity.status(409).body(Map.of("message", "Tên đăng nhập đã tồn tại!"));
            }

            // Tạo tài khoản
            TaiKhoan taiKhoan = new TaiKhoan();
            taiKhoan.setTaiKhoan(registerRequest.getTaiKhoan());
            taiKhoan.setMatKhau(passwordEncoder.encode(registerRequest.getMatKhau()));
            taiKhoan.setChucVu("ADMIN");
            taiKhoan.setTrangThai(true);
            TaiKhoan savedTaiKhoan = taiKhoanRepository.save(taiKhoan);

            // Tạo nhân viên
            NhanVien nhanVien = new NhanVien();
            nhanVien.setMaNV(registerRequest.getMaKH()); // Using MaKH as MaNV for consistency
            nhanVien.setTenNV(registerRequest.getTenKH()); // Using TenKH as TenNV
            nhanVien.setGioiTinh(registerRequest.getGioiTinh());
            nhanVien.setSdt(registerRequest.getSdt());
            nhanVien.setDiaChi(registerRequest.getDiaChi());
            nhanVien.setEmail(registerRequest.getEmail());
            nhanVien.setTrangThai(true);
            nhanVien.setTaiKhoan(savedTaiKhoan);
            nhanVienRepository.save(nhanVien);

            Map<String, Object> response = new HashMap<>();
            response.put("idTK", savedTaiKhoan.getIdTK());
            response.put("taiKhoan", savedTaiKhoan.getTaiKhoan());
            response.put("chucVu", savedTaiKhoan.getChucVu());
            response.put("message", "Đăng ký tài khoản admin thành công!");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Error creating admin account: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi khi đăng ký admin: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            // Kiểm tra tài khoản đã tồn tại
            if (taiKhoanRepository.findByTaiKhoan(registerRequest.getTaiKhoan()).isPresent()) {
                return ResponseEntity.status(409).body(Map.of("message", "Tên đăng nhập đã tồn tại!"));
            }

            // Tạo tài khoản
            TaiKhoan taiKhoan = new TaiKhoan();
            taiKhoan.setTaiKhoan(registerRequest.getTaiKhoan());
            taiKhoan.setMatKhau(passwordEncoder.encode(registerRequest.getMatKhau()));
            taiKhoan.setChucVu("KHACH_HANG");
            taiKhoan.setTrangThai(true);
            TaiKhoan savedTaiKhoan = taiKhoanRepository.save(taiKhoan);

            // Tạo khách hàng
            KhachHang khachHang = new KhachHang();
            khachHang.setMaKH(registerRequest.getMaKH());
            khachHang.setTenKH(registerRequest.getTenKH());
            khachHang.setGioiTinh(registerRequest.getGioiTinh());
            khachHang.setSdt(registerRequest.getSdt());
            khachHang.setDiaChi(registerRequest.getDiaChi());
            khachHang.setEmail(registerRequest.getEmail());
            khachHang.setTrangThai(true);
            khachHang.setTaiKhoan(savedTaiKhoan);
            khachHangRepository.save(khachHang);

            Map<String, Object> response = new HashMap<>();
            response.put("idTK", savedTaiKhoan.getIdTK());
            response.put("taiKhoan", savedTaiKhoan.getTaiKhoan());
            response.put("chucVu", savedTaiKhoan.getChucVu());
            response.put("message", "Đăng ký thành công!");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Error creating account or customer: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi khi đăng ký: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("Login attempt: taiKhoan=" + loginRequest.getTaiKhoan());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getTaiKhoan(),
                            loginRequest.getMatKhau()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            TaiKhoan taiKhoan = taiKhoanRepository.findByTaiKhoan(loginRequest.getTaiKhoan())
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

            Map<String, Object> response = new HashMap<>();
            response.put("idTK", taiKhoan.getIdTK());
            response.put("taiKhoan", taiKhoan.getTaiKhoan());
            response.put("chucVu", taiKhoan.getChucVu());

            Optional<KhachHang> khachHang = khachHangRepository.findByTaiKhoanIdTK(taiKhoan.getIdTK());
            khachHang.ifPresent(kh -> response.put("idKH", kh.getIdKH()));

            Optional<NhanVien> nhanVien = nhanVienRepository.findByTaiKhoanIdTK(taiKhoan.getIdTK());
            nhanVien.ifPresent(nv -> response.put("idNV", nv.getIdNV()));

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            System.out.println("Login failed: Bad credentials for taiKhoan=" + loginRequest.getTaiKhoan());
            return ResponseEntity.status(400).body(Map.of("error", "Sai tài khoản hoặc mật khẩu"));
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Lỗi server: " + e.getMessage()));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
                return ResponseEntity.status(401).body("Chưa đăng nhập");
            }

            String username = authentication.getName();
            TaiKhoan taiKhoan = taiKhoanRepository.findByTaiKhoan(username)
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

            Map<String, Object> response = new HashMap<>();
            response.put("idTK", taiKhoan.getIdTK());
            response.put("taiKhoan", taiKhoan.getTaiKhoan());
            response.put("chucVu", taiKhoan.getChucVu());

            Optional<KhachHang> khachHang = khachHangRepository.findByTaiKhoanIdTK(taiKhoan.getIdTK());
            khachHang.ifPresent(kh -> response.put("idKH", kh.getIdKH()));

            Optional<NhanVien> nhanVien = nhanVienRepository.findByTaiKhoanIdTK(taiKhoan.getIdTK());
            nhanVien.ifPresent(nv -> response.put("idNV", nv.getIdNV()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Chưa đăng nhập");
        }
    }
}