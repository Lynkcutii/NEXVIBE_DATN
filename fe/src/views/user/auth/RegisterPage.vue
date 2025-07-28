<template>
  <div class="auth-page-wrapper">
    <div class="auth-form-container">
      <div class="card border-0">
        <div class="card-body p-4 p-lg-5">
          <div class="text-center mb-4">
            <h4 class="card-title mt-3 mb-1 fw-bold">Tạo tài khoản mới</h4>
            <p class="text-muted">Bắt đầu hành trình cùng NEXVIBE</p>
          </div>
          
          <form @submit.prevent="handleRegister">
            <div class="row g-3">
              <!-- Họ và tên -->
              <div class="col-12">
                <input type="text" class="form-control" placeholder="Họ và tên" v-model="formData.name" required>
              </div>

              <!-- Email -->
              <div class="col-12">
                <input type="email" class="form-control" placeholder="Email" v-model="formData.email" required>
              </div>

              <!-- Số điện thoại -->
              <div class="col-12">
                <input type="tel" class="form-control" placeholder="Số điện thoại" v-model="formData.phone">
              </div>
              
              <!-- Ngày sinh & Giới tính -->
              <div class="col-md-7">
                 <input type="date" class="form-control" v-model="formData.dob" placeholder="Ngày sinh">
              </div>
              <div class="col-md-5 d-flex align-items-center justify-content-around pt-2">
                <div class="form-check form-check-inline">
                  <input class="form-check-input" type="radio" name="gender" id="male" value="MALE" v-model="formData.gender">
                  <label class="form-check-label" for="male">Nam</label>
                </div>
                <div class="form-check form-check-inline">
                  <input class="form-check-input" type="radio" name="gender" id="female" value="FEMALE" v-model="formData.gender">
                  <label class="form-check-label" for="female">Nữ</label>
                </div>
              </div>
              
              <!-- Địa chỉ -->
              <div class="col-12">
                <input type="text" class="form-control" placeholder="Địa chỉ" v-model="formData.address">
              </div>

              <!-- Mật khẩu -->
              <div class="col-md-6">
                <input type="password" class="form-control" placeholder="Mật khẩu" v-model="formData.password" required>
              </div>
              <div class="col-md-6">
                <input type="password" class="form-control" placeholder="Xác nhận mật khẩu" v-model="formData.confirmPassword" required>
              </div>
            </div>
            
            <button type="submit" class="btn btn-primary w-100 mt-4 btn-lg">ĐĂNG KÝ</button>
          </form>
          
          <p class="text-muted small text-center mt-3">Bằng việc đăng kí, bạn đã đồng ý với NEXVIBE về<br><a href="#">Điều khoản dịch vụ</a> & <a href="#">Chính sách bảo mật</a></p>

          <div class="text-center mt-3">
            <span class="text-muted small">Bạn đã có tài khoản? </span>
            <router-link to="/login" class="small fw-bold">Đăng nhập</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';

const router = useRouter();
const toast = useToast();

const formData = reactive({
    name: '',
    email: '',
    phone: '',
    dob: '',
    gender: 'MALE',
    address: '',
    password: '',
    confirmPassword: ''
});

const handleRegister = () => {
    if (formData.password !== formData.confirmPassword) {
        toast.error("Mật khẩu xác nhận không khớp!");
        return;
    }
    console.log("Dữ liệu đăng ký:", formData);
    toast.success("Đăng ký tài khoản thành công!");
    router.push({ name: 'login' });
};
</script>