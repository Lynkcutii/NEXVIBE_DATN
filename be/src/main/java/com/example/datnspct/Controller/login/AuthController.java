package com.example.datnspct.Controller.login;

import com.example.datnspct.Model.DiaChiKhachHang;
import com.example.datnspct.Model.KhachHang;
import com.example.datnspct.Model.NhanVien;
import com.example.datnspct.Model.TaiKhoan;
import com.example.datnspct.Repository.DiaChiKhachHangRepository;
import com.example.datnspct.Repository.KhachHangRepository;
import com.example.datnspct.Repository.NhanVienRepository;
import com.example.datnspct.Repository.login.TaiKhoanRepository;
import com.example.datnspct.dto.login.LoginRequest;
import com.example.datnspct.dto.login.RegisterRequest;
import com.example.datnspct.dto.login.RegisterRequestKhachHang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final TaiKhoanRepository taiKhoanRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    private DiaChiKhachHangRepository diaChiKhachHangRepository;

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
                logger.warn("Tên đăng nhập {} đã tồn tại", registerRequest.getTaiKhoan());
                return ResponseEntity.status(409).body(Map.of("message", "Tên đăng nhập đã tồn tại!"));
            }

            // Tự sinh maNV
            String maNV;
            long count = nhanVienRepository.count();
            do {
                maNV = String.format("NV%03d", count + 1);
                count++;
            } while (nhanVienRepository.findByMaNV(maNV).isPresent());

            // Tạo tài khoản
            TaiKhoan taiKhoan = new TaiKhoan();
            taiKhoan.setTaiKhoan(registerRequest.getTaiKhoan());
            taiKhoan.setMatKhau(passwordEncoder.encode(registerRequest.getMatKhau()));
            taiKhoan.setChucVu("ADMIN");
            taiKhoan.setTrangThai(true);
            TaiKhoan savedTaiKhoan = taiKhoanRepository.save(taiKhoan);

            // Tạo nhân viên
            NhanVien nhanVien = new NhanVien();
            nhanVien.setMaNV(maNV);
            nhanVien.setTenNV(registerRequest.getTen());
            nhanVien.setGioiTinh(registerRequest.getGioiTinh());
            nhanVien.setSdt(registerRequest.getSdt());
            nhanVien.setNgaySinh(registerRequest.getNgaySinh());
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

            logger.info("Đăng ký admin thành công: {}", savedTaiKhoan.getTaiKhoan());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Lỗi khi đăng ký admin: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi khi đăng ký admin: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestKhachHang registerRequestKhachHang) {
        try {
            // Kiểm tra tài khoản đã tồn tại
            if (taiKhoanRepository.findByTaiKhoan(registerRequestKhachHang.getTaiKhoan()).isPresent()) {
                logger.warn("Tên đăng nhập {} đã tồn tại", registerRequestKhachHang.getTaiKhoan());
                return ResponseEntity.status(409).body(Map.of("message", "Tên đăng nhập đã tồn tại!"));
            }

            // Kiểm tra các trường bắt buộc
            if (registerRequestKhachHang.getTenKH() == null || registerRequestKhachHang.getTenKH().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Tên khách hàng là bắt buộc!"));
            }
            if (registerRequestKhachHang.getGioiTinh() == null || registerRequestKhachHang.getGioiTinh().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Giới tính là bắt buộc!"));
            }
            if (registerRequestKhachHang.getNgaySinh() == null ) {
                return ResponseEntity.badRequest().body(Map.of("message", "Ngày sinh là bắt buộc!"));
            }
            if (registerRequestKhachHang.getSdt() == null || registerRequestKhachHang.getSdt().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Số điện thoại là bắt buộc!"));
            }
            if (registerRequestKhachHang.getEmail() == null || registerRequestKhachHang.getEmail().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email là bắt buộc!"));
            }
            if (registerRequestKhachHang.getDiaChiCuThe() == null || registerRequestKhachHang.getDiaChiCuThe().isEmpty() ||
                    registerRequestKhachHang.getTinhThanh() == null || registerRequestKhachHang.getTinhThanh().isEmpty() ||
                    registerRequestKhachHang.getPhuongXa() == null || registerRequestKhachHang.getPhuongXa().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Thông tin địa chỉ không đầy đủ!"));
            }

            // Parse ngày sinh
            LocalDate ngaySinh;
            try {
                ngaySinh = registerRequestKhachHang.getNgaySinh();
                // Kiểm tra ngày sinh hợp lệ (phải trong quá khứ)
                if (ngaySinh.isAfter(LocalDate.now())) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Ngày sinh phải trong quá khứ!"));
                }
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest().body(Map.of("message", "Định dạng ngày sinh không hợp lệ!"));
            }

            // Tạo tài khoản
            TaiKhoan taiKhoan = new TaiKhoan();
            taiKhoan.setTaiKhoan(registerRequestKhachHang.getTaiKhoan());
            taiKhoan.setMatKhau(passwordEncoder.encode(registerRequestKhachHang.getMatKhau()));
            taiKhoan.setChucVu("KHACH_HANG");
            taiKhoan.setTrangThai(true);
            TaiKhoan savedTaiKhoan = taiKhoanRepository.save(taiKhoan);

            // Tạo khách hàng
            KhachHang khachHang = new KhachHang();
            khachHang.setMaKH(generateCustomerCode());
            khachHang.setTenKH(registerRequestKhachHang.getTenKH());
            khachHang.setGioiTinh(registerRequestKhachHang.getGioiTinh());
            khachHang.setNgaySinh(ngaySinh); // Set ngày sinh
            khachHang.setSdt(registerRequestKhachHang.getSdt());
            khachHang.setEmail(registerRequestKhachHang.getEmail());
            khachHang.setTrangThai(true);
            khachHang.setTaiKhoan(savedTaiKhoan);
            KhachHang savedKhachHang = khachHangRepository.save(khachHang);

            // Tạo địa chỉ khách hàng
            DiaChiKhachHang diaChi = new DiaChiKhachHang();
            diaChi.setKhachHang(savedKhachHang);
            diaChi.setDiaChiCuThe(registerRequestKhachHang.getDiaChiCuThe());
            diaChi.setTinhThanh(registerRequestKhachHang.getTinhThanh());
            diaChi.setPhuongXa(registerRequestKhachHang.getPhuongXa());
            diaChi.setSoDienThoai(registerRequestKhachHang.getSoDienThoai());
            diaChi.setGhiChu(registerRequestKhachHang.getGhiChu());
            diaChi.setTrangThai(true);
            diaChiKhachHangRepository.save(diaChi);

            Map<String, Object> response = new HashMap<>();
            response.put("idTK", savedTaiKhoan.getIdTK());
            response.put("taiKhoan", savedTaiKhoan.getTaiKhoan());
            response.put("chucVu", savedTaiKhoan.getChucVu());
            response.put("idKH", savedKhachHang.getIdKH());
            response.put("message", "Đăng ký thành công!");

            logger.info("Đăng ký khách hàng thành công: {}", savedTaiKhoan.getTaiKhoan());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Lỗi khi đăng ký khách hàng: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi khi đăng ký: " + e.getMessage()));
        }
    }

    private String generateCustomerCode() {
        return "KH" + System.currentTimeMillis();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Đăng nhập: taiKhoan={}", loginRequest.getTaiKhoan());
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

            logger.info("Đăng nhập thành công: {}", taiKhoan.getTaiKhoan());
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            logger.warn("Đăng nhập thất bại: Sai tài khoản hoặc mật khẩu cho taiKhoan={}", loginRequest.getTaiKhoan());
            return ResponseEntity.status(400).body(Map.of("error", "Sai tài khoản hoặc mật khẩu"));
        } catch (Exception e) {
            logger.error("Lỗi đăng nhập: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Lỗi server: " + e.getMessage()));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
                logger.warn("Truy cập /user: Chưa đăng nhập");
                return ResponseEntity.status(401).body("Chưa đăng nhập");
            }

            String username = authentication.getName();
            TaiKhoan taiKhoan = taiKhoanRepository.findByTaiKhoan(username)
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

            Map<String, Object> response = new HashMap<>();
            response.put("idTK", taiKhoan.getIdTK());
            response.put("taiKhoan", taiKhoan.getTaiKhoan());
            response.put("chucVu", taiKhoan.getChucVu());

            // Thêm thông tin khách hàng hoặc nhân viên
            Optional<KhachHang> khachHang = khachHangRepository.findByTaiKhoanIdTK(taiKhoan.getIdTK());
            khachHang.ifPresent(kh -> {
                response.put("idKH", kh.getIdKH());
                response.put("userFullName", kh.getTenKH()); // Thêm tenKH
            });

            Optional<NhanVien> nhanVien = nhanVienRepository.findByTaiKhoanIdTK(taiKhoan.getIdTK());
            nhanVien.ifPresent(nv -> {
                response.put("idNV", nv.getIdNV());
                response.put("userFullName", nv.getTenNV()); // Thêm tenNV nếu là nhân viên
            });

            logger.info("Lấy thông tin người dùng thành công: {}", username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy thông tin người dùng: {}", e.getMessage());
            return ResponseEntity.status(401).body("Chưa đăng nhập");
        }
    }
}