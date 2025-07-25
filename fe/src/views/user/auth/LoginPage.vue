<template>
  <div class="auth-page-wrapper">
    <!-- Cột Form Đăng nhập -->
    <div class="auth-form-container m-2 ">
        <div class="card border-0">
          <div class="card-body p-4 p-lg-5">
            <h4 class="card-title mb-4 fw-bold">Đăng nhập</h4>
            
            <form @submit.prevent="handleLogin">
              <div class="mb-3">
                <input type="email" class="form-control" placeholder="Email / Tên đăng nhập">
              </div>
              <div class="mb-3">
                <input type="password" class="form-control" placeholder="Mật khẩu">
              </div>
              <button type="submit" class="btn btn-primary w-100 mt-2">ĐĂNG NHẬP</button>
              <div class="text-end mt-2">
                <a href="#" class="small">Quên mật khẩu?</a>
              </div>
            </form>

            <div class="social-login-divider">HOẶC</div>

            <div class="row g-2">
              <div class="col">
                <button class="btn social-login-btn">
                  <i class="fab fa-facebook"></i> Facebook
                </button>
              </div>
              <div class="col">
                <button class="btn social-login-btn">
                  <i class="fab fa-google"></i> Google
                </button>
              </div>
            </div>

            <div class="text-center mt-4">
              <span class="text-muted small">Bạn mới biết đến NEXVIBE? </span>
              <router-link to="/register" class="small fw-bold">Đăng ký</router-link>
            </div>
          </div>
        </div>
    </div>
  </div>
</template>

<script setup>
// Script giữ nguyên như cũ
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';
const auth = useAuthStore();
const router = useRouter();
const toast = useToast();
const handleLogin = () => {
    const userData = { id: 1, name: 'Trần Minh Tuấn', email: 'tuan.tm@nexvibe.com' };
    const token = 'fake-jwt-token-string';
    auth.login(userData, token);
    toast.success('Đăng nhập thành công!');
    router.push({ name: 'home' });
};
</script>