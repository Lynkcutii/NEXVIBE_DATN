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
        // 1. Kiểm tra mật khẩu mới và xác nhận có khớp không
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới không khớp.");
        }

        // 2. Tìm tài khoản
        TaiKhoan taiKhoan = taiKhoanRepository.findById(idTK)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản."));

        // 3. Kiểm tra mật khẩu hiện tại có đúng không
        if (!passwordEncoder.matches(request.getCurrentPassword(), taiKhoan.getMatKhau())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không chính xác.");
        }

        // 4. Mã hóa và cập nhật mật khẩu mới
        taiKhoan.setMatKhau(passwordEncoder.encode(request.getNewPassword()));
        taiKhoanRepository.save(taiKhoan);
    }
}
