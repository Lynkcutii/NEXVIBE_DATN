<template>
   <div class="auth-page-wrapper">
      <!-- Cột Form Đăng ký -->
      <div class="auth-form-container">
        <div class="card border-0">
          <div class="card-body p-4 p-lg-5">
            <h4 class="card-title mb-4 fw-bold">Đăng ký</h4>
            
            <form>
              <div class="mb-3"><input type="email" class="form-control" placeholder="Email"></div>
              <div class="mb-3"><input type="password" class="form-control" placeholder="Mật khẩu"></div>
              <div class="mb-3"><input type="password" class="form-control" placeholder="Xác nhận mật khẩu"></div>
              <button type="submit" class="btn btn-primary w-100 mt-2">ĐĂNG KÝ</button>
            </form>
            
            <p class="text-muted small text-center mt-3">Bằng việc đăng kí, bạn đã đồng ý với NEXVIBE về<br><a href="#">Điều khoản dịch vụ</a> & <a href="#">Chính sách bảo mật</a></p>

            <div class="text-center mt-4">
              <span class="text-muted small">Bạn đã có tài khoản? </span>
              <router-link to="/login" class="small fw-bold">Đăng nhập</router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
</template>