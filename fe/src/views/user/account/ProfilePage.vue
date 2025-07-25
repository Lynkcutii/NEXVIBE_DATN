<template>
  <div class="row g-4">
    <!-- Cột trái: Sidebar tài khoản -->
    <div class="col-lg-3">
      <div class="account-sidebar card border-0 shadow-sm">
        <div class="card-body p-3">
          <div class="d-flex align-items-center mb-3">
            <i class="fas fa-user-circle fa-3x text-muted me-3"></i>
            <div>
              <small class="text-muted">Tài khoản của</small>
              <h6 class="mb-0 fw-bold">{{ auth.userFullName }}</h6>
            </div>
          </div>
          <hr>
          <div class="list-group list-group-flush">
            <router-link to="/profile" class="list-group-item list-group-item-action active"><i class="fas fa-user-edit fa-fw me-2"></i>Thông tin cá nhân</router-link>
            <router-link to="/order-history" class="list-group-item list-group-item-action"><i class="fas fa-receipt fa-fw me-2"></i>Lịch sử đơn hàng</router-link>
            <a href="#" class="list-group-item list-group-item-action text-danger" @click.prevent="handleLogout"><i class="fas fa-sign-out-alt fa-fw me-2"></i>Đăng xuất</a>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Cột phải: Nội dung chính -->
    <div class="col-lg-9">
      <!-- Card Thông tin cá nhân -->
      <div class="card border-0 shadow-sm mb-4">
        <div class="card-header bg-transparent border-0 pt-4 px-4">
          <h4 class="mb-0">Thông tin cá nhân</h4>
        </div>
        <div class="card-body p-4">
          <form @submit.prevent="updateProfile">
            <div class="row">
              <div class="col-md-6 mb-3">
                <label for="fullName" class="form-label">Họ và tên</label>
                <input type="text" class="form-control" id="fullName" v-model="profile.name">
              </div>
              <div class="col-md-6 mb-3">
                <label for="email" class="form-label">Email</label>
                <input type="email" class="form-control" id="email" :value="auth.user?.email" disabled>
              </div>
            </div>
            <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
          </form>
        </div>
      </div>

      <!-- Card Đổi mật khẩu -->
      <div class="card border-0 shadow-sm">
         <div class="card-header bg-transparent border-0 pt-4 px-4">
          <h4 class="mb-0">Đổi mật khẩu</h4>
        </div>
        <div class="card-body p-4">
          <form @submit.prevent="changePassword">
            <div class="mb-3">
              <label for="currentPassword" class="form-label">Mật khẩu hiện tại</label>
              <input type="password" class="form-control" id="currentPassword">
            </div>
            <div class="mb-3">
              <label for="newPassword" class="form-label">Mật khẩu mới</label>
              <input type="password" class="form-control" id="newPassword">
            </div>
            <div class="mb-3">
              <label for="confirmPassword" class="form-label">Nhập lại mật khẩu mới</label>
              <input type="password" class="form-control" id="confirmPassword">
            </div>
            <button type="submit" class="btn btn-dark">Đổi mật khẩu</button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';

const auth = useAuthStore();
const router = useRouter();
const toast = useToast();

const profile = ref({
    name: ''
});

const updateProfile = () => {
    // Logic gọi API để cập nhật profile
    console.log('Cập nhật profile:', profile.value);
    toast.success('Cập nhật thông tin thành công!');
};

const changePassword = () => {
    // Logic gọi API để đổi mật khẩu
    console.log('Đổi mật khẩu...');
    toast.success('Đổi mật khẩu thành công!');
};

const handleLogout = () => {
    auth.logout();
    toast.info('Bạn đã đăng xuất.');
    router.push({ name: 'home' });
};

onMounted(() => {
    // Lấy thông tin người dùng từ store để điền vào form
    if (auth.isAuthenticated) {
        profile.value.name = auth.user.name;
    }
});
</script>

<style scoped>
/* Ghi đè style mặc định của list-group để khớp với thiết kế */
.account-sidebar .list-group-item {
  border: none;
  font-weight: 500;
  color: #495057;
  padding: 0.9rem 1.25rem;
  border-radius: 0.5rem;
  margin-bottom: 0.25rem; /* Thêm khoảng cách nhỏ giữa các mục */
  transition: background-color 0.2s, color 0.2s;
}
.account-sidebar .list-group-item i {
  margin-right: 12px;
  width: 20px;
  text-align: center;
}
/* Style cho mục đang active */
.account-sidebar .list-group-item.router-link-exact-active {
  color: #fff;
  background-color: var(--bs-primary);
}
/* Style khi hover */
.account-sidebar .list-group-item:not(.router-link-exact-active):hover {
  background-color: #f0f2f5;
}
</style>