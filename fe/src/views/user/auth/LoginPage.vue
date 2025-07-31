<template>
  <div class="auth-page-wrapper">
    <div class="auth-form-container m-2">
      <div class="card border-0">
        <div class="card-body p-4 p-lg-5">
          <h4 class="card-title mb-4 fw-bold">Đăng nhập</h4>

          <form @submit.prevent="handleLogin">
            <div class="mb-3">
              <input
                v-model="form.taiKhoan"
                type="text"
                class="form-control"
                placeholder="Tên đăng nhập"
                required
              />
            </div>
            <div class="mb-3">
              <input
                v-model="form.matKhau"
                type="password"
                class="form-control"
                placeholder="Mật khẩu"
                required
              />
            </div>
            <button type="submit" class="btn btn-primary w-100 mt-2">ĐĂNG NHẬP</button>
            <div class="text-end mt-2">
              <a href="#" class="small">Quên mật khẩu?</a>
            </div>
            <div v-if="errorMessage" class="text-danger mt-2">
              {{ errorMessage }}
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
import { ref } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useRouter, useRoute } from 'vue-router';
import { useToast } from 'vue-toastification';
import axios from 'axios';

const auth = useAuthStore();
const router = useRouter();
const route = useRoute();
const toast = useToast();

const form = ref({
  taiKhoan: '',
  matKhau: ''
});
const errorMessage = ref('');

const handleLogin = async () => {
  console.log('handleLogin: Starting, taiKhoan=' + form.value.taiKhoan);
  try {
    const response = await axios.post('http://localhost:8080/api/auth/login', {
      username: form.value.taiKhoan,
      password: form.value.matKhau
    }, {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      withCredentials: true,
      transformRequest: [(data) => {
        return `username=${encodeURIComponent(data.username)}&password=${encodeURIComponent(data.password)}`;
      }]
    });

    const userData = response.data;
    console.log('handleLogin: Success, userData=' + JSON.stringify(userData));
    await auth.login(userData);

    toast.success('Đăng nhập thành công!');
    const redirect = route.query.redirect || {
      name: userData.chucVu === 'ADMIN' ? 'admin.dashboard' : 'home'
    };
    console.log('handleLogin: Redirecting to ' + redirect.name);
    router.push(redirect);
  } catch (error) {
    console.error('handleLogin: Error -', error);
    if (error.response) {
      console.error('handleLogin: Response error - status=' + error.response.status + ', data=' + JSON.stringify(error.response.data));
      errorMessage.value = error.response.data.error || 'Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin!';
    } else if (error.request) {
      console.error('handleLogin: No response received, possible CORS or network issue');
      errorMessage.value = 'Lỗi mạng hoặc CORS, vui lòng kiểm tra kết nối!';
    } else {
      console.error('handleLogin: Error setting up request - ' + error.message);
      errorMessage.value = 'Lỗi khi đăng nhập: ' + error.message;
    }
    toast.error(errorMessage.value);
  }
};
</script>