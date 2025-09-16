package com.example.datnspct.Service;
import com.example.datnspct.Model.TaiKhoan;
import com.example.datnspct.Repository.login.TaiKhoanRepository;
import com.example.datnspct.dto.ChangePasswordRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Inject PasswordEncoder
import org.springframework.stereotype.Service;

@Service
public class TaiKhoanService {
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Transactional

    public void changePassword(Integer idTK, ChangePasswordRequestDTO request) {
        // --- Bước 1: Validate dữ liệu đầu vào ---
        if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu mới không được để trống.");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp.");
        }

        // --- Bước 2: Tìm tài khoản trong database ---
        TaiKhoan taiKhoan = taiKhoanRepository.findById(idTK)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản với ID: " + idTK));

        // --- Bước 3: Xác thực mật khẩu hiện tại ---
        // So sánh mật khẩu thô người dùng nhập với mật khẩu đã hash trong DB
        if (!passwordEncoder.matches(request.getCurrentPassword(), taiKhoan.getMatKhau())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không chính xác.");
        }

        // --- Bước 4: Kiểm tra mật khẩu mới có trùng với mật khẩu cũ không ---
        if (passwordEncoder.matches(request.getNewPassword(), taiKhoan.getMatKhau())) {
            throw new IllegalArgumentException("Mật khẩu mới không được trùng với mật khẩu cũ.");
        }

        // --- Bước 5: (Tùy chọn) Thêm các quy tắc validate cho mật khẩu mới ---
        // Ví dụ: Mật khẩu phải có ít nhất 8 ký tự
        if (request.getNewPassword().length() < 8) {
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 8 ký tự.");
        }
        // Thêm các quy tắc khác nếu muốn (chữ hoa, chữ thường, số, ký tự đặc biệt)

        // --- Bước 6: Hash mật khẩu mới và cập nhật vào database ---
        String newHashedPassword = passwordEncoder.encode(request.getNewPassword());
        taiKhoan.setMatKhau(newHashedPassword);
        taiKhoanRepository.save(taiKhoan);
    }
}
