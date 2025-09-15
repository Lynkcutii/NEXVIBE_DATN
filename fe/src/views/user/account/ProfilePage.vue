<template>
  <div class="row g-4">
    <!-- Sidebar -->
    <div class="col-lg-3">
      <div class="account-sidebar card border-0 shadow-sm">
        <div class="card-body p-3">
          <div class="d-flex align-items-center mb-3">
            <i class="fas fa-user-circle fa-3x text-muted me-3"></i>
            <div>
              <small class="text-muted">Tài khoản của</small>
              <h6 class="mb-0 fw-bold">{{ authStore.userFullName }}</h6>
            </div>
          </div>
          <hr>
          <div class="list-group list-group-flush">
            <router-link :to="{ name: 'profile' }" class="list-group-item list-group-item-action active"><i class="fas fa-user-edit fa-fw me-2"></i>Thông tin tài khoản</router-link>
            <router-link :to="{ name: 'order.history' }" class="list-group-item list-group-item-action"><i class="fas fa-receipt fa-fw me-2"></i>Lịch sử đơn hàng</router-link>
            <a href="#" class="list-group-item list-group-item-action text-danger" @click.prevent="handleLogout"><i class="fas fa-sign-out-alt fa-fw me-2"></i>Đăng xuất</a>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Nội dung chính -->
    <div class="col-lg-9">
      <div v-if="loading" class="text-center p-5"><div class="spinner-border"></div></div>
      <div v-else-if="error" class="alert alert-danger">{{ error }}</div>
      <div v-else>
        <!-- Card Thông tin cá nhân -->
        <div class="card border-0 shadow-sm mb-4">
          <div class="card-body p-4 p-md-5">
            <h4 class="section-title">Thông tin tài khoản</h4>
            <div class="section-content">
              <div class="info-item"><span>Họ và tên</span><span>{{ profile.tenKH || 'Chưa cập nhật' }}</span></div>
              <div class="info-item"><span>Số điện thoại</span><span>{{ profile.sdt || 'Chưa cập nhật' }}</span></div>
              <div class="info-item"><span>Giới tính</span><span>{{ profile.gioiTinh || 'Chưa cập nhật' }}</span></div>
              <div class="info-item"><span>Ngày sinh</span><span>{{ profile.ngaySinh ? formatDate(profile.ngaySinh) : 'Chưa cập nhật' }}</span></div>
            </div>
            <div class="section-footer"><button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#profileModal">Cập nhật</button></div>
          </div>
        </div>
        <!-- Card Thông tin đăng nhập -->
        <div class="card border-0 shadow-sm">
          <div class="card-body p-4 p-md-5">
            <h4 class="section-title">Thông tin đăng nhập</h4>
            <div class="section-content">
              <div class="info-item"><span>Email / Tên đăng nhập</span><span>{{ profile.taiKhoan || 'Chưa cập nhật' }}</span></div>
              <div class="info-item"><span>Mật khẩu</span><span>••••••••••</span></div>
            </div>
            <div class="section-footer"><button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#changePasswordModal">Thay đổi</button></div>
          </div>
        </div>
      </div>
    </div>
  </div>
  
  <!-- Modal Cập nhật Profile -->
  <div class="modal fade" id="profileModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header"><h5 class="modal-title">Cập nhật thông tin</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
        <div class="modal-body">
          <form @submit.prevent="updateProfile">
            <div class="mb-3"><label class="form-label">Họ và tên</label><input type="text" class="form-control" v-model="profileForm.tenKH"></div>
            <div class="mb-3"><label class="form-label">Số điện thoại</label><input type="text" class="form-control" v-model="profileForm.sdt"></div>
            <div class="mb-3"><label class="form-label">Giới tính</label>
              <select class="form-select" v-model="profileForm.gioiTinh">
                <option>Nam</option><option>Nữ</option><option>Khác</option>
              </select>
            </div>
            <div class="mb-3"><label class="form-label">Ngày sinh</label><input type="date" class="form-control" v-model="profileForm.ngaySinh"></div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-light" data-bs-dismiss="modal">Hủy</button>
          <button type="button" class="btn btn-primary" @click="updateProfile" :disabled="isSubmitting">{{ isSubmitting ? 'Đang lưu...' : 'Lưu thay đổi' }}</button>
        </div>
      </div>
    </div>
  </div>
  
  


<!-- MODAL THAY ĐỔI MẬT KHẨU -->
<div class="modal fade" id="changePasswordModal" tabindex="-1">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Thay đổi mật khẩu</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <form @submit.prevent="changePassword">
          
          <!-- Mật khẩu hiện tại -->
          <div class="mb-3">
            <label for="currentPassword" class="form-label">Mật khẩu hiện tại</label>
            <div class="input-group-with-icon">
              <i class="bi bi-shield-lock input-icon"></i>
              <input :type="showCurrentPassword ? 'text' : 'password'" class="form-control" id="currentPassword" v-model="passwordForm.currentPassword" required ref="currentPasswordInput">
              <i class="bi toggle-password-icon" :class="showCurrentPassword ? 'bi-eye-slash' : 'bi-eye'" @click="showCurrentPassword = !showCurrentPassword"></i>
            </div>
          </div>
          
          <!-- Mật khẩu mới -->
          <div class="mb-3">
            <label for="newPassword" class="form-label">Mật khẩu mới</label>
            <div class="input-group-with-icon">
              <i class="bi bi-shield-lock-fill input-icon"></i>
              <input :type="showNewPassword ? 'text' : 'password'" class="form-control" id="newPassword" v-model="passwordForm.newPassword" required>
              <i class="bi toggle-password-icon" :class="showNewPassword ? 'bi-eye-slash' : 'bi-eye'" @click="showNewPassword = !showNewPassword"></i>
            </div>
            <!-- Thanh độ mạnh mật khẩu -->
            <div class="password-strength-meter mt-2">
              <div class="strength-bar" :style="passwordStrengthStyle"></div>
            </div>
            <div class="strength-text small mt-1" :style="{ color: passwordStrengthStyle.color }">
              {{ passwordStrengthText }}
            </div>
          </div>

          <!-- Xác nhận mật khẩu mới -->
          <div class="mb-3">
            <label for="confirmPassword" class="form-label">Xác nhận mật khẩu mới</label>
            <div class="input-group-with-icon">
               <i class="bi bi-shield-check input-icon"></i>
              <input :type="showConfirmPassword ? 'text' : 'password'" class="form-control" id="confirmPassword" v-model="passwordForm.confirmPassword" required>
              <i class="bi toggle-password-icon" :class="showConfirmPassword ? 'bi-eye-slash' : 'bi-eye'" @click="showConfirmPassword = !showConfirmPassword"></i>
            </div>
             <div v-if="passwordsDoNotMatch" class="text-danger small mt-1">
              Mật khẩu xác nhận không khớp.
            </div>
          </div>
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-light" data-bs-dismiss="modal">Hủy</button>
        <button type="button" class="btn btn-primary" @click="changePassword" :disabled="isSubmittingPassword">
          {{ isSubmittingPassword ? 'Đang lưu...' : 'Lưu thay đổi' }}
        </button>
      </div>
    </div>
  </div>
</div>
</template>

<script setup>
import { reactive, onMounted, ref } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';
import axios from 'axios';
import { Modal } from 'bootstrap';

const authStore = useAuthStore();
const router = useRouter();
const toast = useToast();

// --- STATE ---
const profile = reactive({});
const profileForm = reactive({ tenKH: '', sdt: '', gioiTinh: '', ngaySinh: '' });
const passwordForm = reactive({ currentPassword: '', newPassword: '', confirmPassword: '' });
const showCurrentPassword = ref(false);
const showNewPassword = ref(false);
const showConfirmPassword = ref(false);
const loading = ref(true);
const error = ref(null);
const isSubmitting = ref(false);

const isSubmittingPassword = ref(false);
let profileModalInstance = null;
let passwordModalInstance = null;
const API_BASE_URL = 'http://localhost:8080';

// --- LOGIC ---
const loadProfile = async () => {
  const customerId = authStore.user?.idKH;
  if (!authStore.isAuthenticated || !customerId) {
    error.value = "Không thể xác thực người dùng."; loading.value = false;
    router.push({ name: 'login' }); return;
  }
  try {
    loading.value = true;
    const response = await axios.get(`${API_BASE_URL}/client/api/khachhang/${customerId}`);
    Object.assign(profile, response.data);
    Object.assign(profileForm, response.data);
    authStore.userFullName = response.data.tenKH;
  } catch (err) {
    error.value = "Không thể tải thông tin tài khoản.";
  } finally {
    loading.value = false;
  }
};

const updateProfile = async () => {
  isSubmitting.value = true;
  try {
    const customerId = authStore.user?.idKH;
    const response = await axios.put(`${API_BASE_URL}/client/api/khachhang/${customerId}`, profileForm);
    Object.assign(profile, response.data);
    if (profileModalInstance) profileModalInstance.hide();
    toast.success("Cập nhật thông tin thành công!");
  } catch (err) {
    toast.error("Lỗi khi cập nhật thông tin.");
  } finally {
    isSubmitting.value = false;
  }
};

const changePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    toast.error("Mật khẩu mới không khớp!"); return;
  }
  isSubmittingPassword.value = true;
  try {
    const accountId = authStore.user?.idTK;
    await axios.put(`${API_BASE_URL}/client/api/taikhoan/${accountId}/change-password`, passwordForm);
    if (passwordModalInstance) passwordModalInstance.hide();
    toast.success("Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
    handleLogout();
  } catch (err) {
    toast.error(err.response?.data?.message || "Lỗi khi đổi mật khẩu.");
  } finally {
    isSubmittingPassword.value = false;
  }
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  return new Date(dateString).toLocaleDateString('vi-VN');
};
const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};

// --- LIFECYCLE HOOKS ---
onMounted(() => {
  loadProfile();
  const profileModalElement = document.getElementById('profileModal');
  const passwordModalElement = document.getElementById('changePasswordModal');
  if (profileModalElement) profileModalInstance = new Modal(profileModalElement);
  if (passwordModalElement) passwordModalInstance = new Modal(passwordModalElement);
});
</script>

<style scoped>
/* Toàn bộ CSS của bạn giữ nguyên */
.account-sidebar .list-group-item.router-link-exact-active { color: #fff; background-color: var(--bs-primary); }
.section-title { font-weight: 600; font-size: 1.25rem; margin-bottom: 1.5rem; padding-bottom: 0.75rem; border-bottom: 1px solid #e9ecef; }
.info-item { display: flex; justify-content: space-between; align-items: center; padding: 1.1rem 0; border-bottom: 1px solid #f8f9fa; }
.section-content .info-item:last-child { border-bottom: none; }
.info-label { color: #6c757d; }
.info-value { font-weight: 500; }
.section-footer { text-align: right; padding-top: 2rem; }
.update-btn { border-radius: 50px; padding: 0.6rem 1.75rem; font-weight: 600; }
</style>