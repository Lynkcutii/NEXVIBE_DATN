package com.example.datnspct.Service.login;

import com.example.datnspct.Model.TaiKhoan;
import com.example.datnspct.Repository.login.TaiKhoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AccountDetailService implements UserDetailsService {
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Override
    public UserDetails loadUserByUsername(String taiKhoan) throws UsernameNotFoundException {
        TaiKhoan account = taiKhoanRepository.findByTaiKhoan(taiKhoan)
                .orElseThrow(() -> new UsernameNotFoundException("Tài khoản không tồn tại: " + taiKhoan));
        return new User(
                account.getTaiKhoan(),
                account.getMatKhau(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + account.getChucVu()))
        );
    }

    public TaiKhoan findByTaiKhoan(String taiKhoan) {
        return taiKhoanRepository.findByTaiKhoan(taiKhoan)
                .orElseThrow(() -> new UsernameNotFoundException("Tài khoản không tồn tại: " + taiKhoan));
    }
}