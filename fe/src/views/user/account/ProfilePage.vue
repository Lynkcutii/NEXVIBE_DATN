<template>
  <div class="row g-4">
    <!-- Cột trái: Sidebar tài khoản (Giữ nguyên) -->
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
            <router-link to="/profile" class="list-group-item list-group-item-action active"><i class="fas fa-user-edit fa-fw me-2"></i>Thông tin tài khoản</router-link>
            <router-link to="/order-history" class="list-group-item list-group-item-action"><i class="fas fa-receipt fa-fw me-2"></i>Lịch sử đơn hàng</router-link>
            <a href="#" class="list-group-item list-group-item-action text-danger" @click.prevent="handleLogout"><i class="fas fa-sign-out-alt fa-fw me-2"></i>Đăng xuất</a>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Cột phải: Nội dung chính với bố cục chuyên nghiệp -->
    <div class="col-lg-9">
      <div class="card border-0 shadow-sm">
        <div class="card-body p-4 p-md-5">

          <!-- Phần Thông tin tài khoản -->
          <div class="account-section">
            <h4 class="section-title">Thông tin tài khoản</h4>
            <div class="section-content">
              <div class="info-item">
                <span class="info-label">Họ và tên</span>
                <span class="info-value">{{ profile.name }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Số điện thoại</span>
                <span class="info-value">{{ profile.phone }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Giới tính</span>
                <span class="info-value text-muted fst-italic">{{ profile.gender }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Ngày sinh</span>
                <span class="info-value text-muted fst-italic">{{ profile.dob }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Chiều cao</span>
                <span class="info-value text-muted fst-italic">{{ profile.height }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Cân nặng</span>
                <span class="info-value text-muted fst-italic">{{ profile.weight }}</span>
              </div>
            </div>
            <div class="section-footer">
              <button class="btn btn-primary update-btn" @click="editProfile">
                <i class="fas fa-pencil-alt me-2"></i>CẬP NHẬT
              </button>
            </div>
          </div>

          <!-- Dấu phân cách -->
          <hr class="my-5">

          <!-- Phần Thông tin đăng nhập -->
          <div class="account-section">
            <h4 class="section-title">Thông tin đăng nhập</h4>
            <div class="section-content">
              <div class="info-item">
                <span class="info-label">Email</span>
                <span class="info-value">{{ userEmail }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Mật khẩu</span>
                <span class="info-value">••••••••••</span>
              </div>
            </div>
            <div class="section-footer">
               <button class="btn btn-primary update-btn" @click="editLoginInfo">
                <i class="fas fa-key me-2"></i>THAY ĐỔI
              </button>
            </div>
          </div>
          
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';

const auth = useAuthStore();
const router = useRouter();
const toast = useToast();

// Dữ liệu mẫu cho thông tin tài khoản
const profile = ref({
    name: 'Trần Minh Tuấn',
    phone: '0816169895',
    gender: 'Chưa cập nhật',
    dob: 'Hãy cập nhật để nhận quà sinh nhật',
    height: 'Chưa cập nhật',
    weight: 'Chưa cập nhật'
});

const userEmail = computed(() => auth.user?.email || 'tuan.tm@nexvibe.com');

const editProfile = () => {
    console.log('Chỉnh sửa thông tin tài khoản...');
    toast.info('Chức năng đang được phát triển!');
};

const editLoginInfo = () => {
    console.log('Chỉnh sửa thông tin đăng nhập...');
    toast.info('Chức năng đang được phát triển!');
};

const handleLogout = () => {
    auth.logout();
    toast.info('Bạn đã đăng xuất.');
    router.push({ name: 'home' });
};

onMounted(() => {
    if (auth.isAuthenticated && auth.user) {
        profile.value.name = auth.user.name || 'Trần Minh Tuấn';
    }
});
</script>

<style scoped>
/* CSS cho Sidebar (Giữ nguyên) */
.account-sidebar .list-group-item {
  border: none; font-weight: 500; color: #495057;
  padding: 0.9rem 1.25rem; border-radius: 0.5rem; margin-bottom: 0.25rem;
  transition: background-color 0.2s, color 0.2s;
}
.account-sidebar .list-group-item i {
  margin-right: 12px; width: 20px; text-align: center;
}
.account-sidebar .list-group-item.router-link-exact-active {
  color: #fff; background-color: var(--bs-primary);
}
.account-sidebar .list-group-item:not(.router-link-exact-active):hover {
  background-color: #f0f2f5;
}

/* --- CSS ĐÃ CẬP NHẬT CHO BỐ CỤC CHUYÊN NGHIỆP --- */
.section-title {
  font-weight: 600;
  font-size: 1.25rem;
  color: #343a40;
  margin-bottom: 1.5rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #e9ecef;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.1rem 0;
  border-bottom: 1px solid #f8f9fa; /* Đường kẻ siêu mờ */
}

/* Loại bỏ đường kẻ cho mục cuối cùng */
.section-content .info-item:last-child {
    border-bottom: none;
}

.info-label {
  font-size: 0.95rem;
  color: #6c757d;
}

.info-value {
  font-size: 0.95rem;
  font-weight: 500;
  color: #212529;
  text-align: right;
}

.section-footer {
    text-align: right;
    padding-top: 2rem;
}

.update-btn {
  border-radius: 50px;
  padding: 0.6rem 1.75rem;
  font-weight: 600;
  font-size: 0.9rem;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.update-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}
</style>```