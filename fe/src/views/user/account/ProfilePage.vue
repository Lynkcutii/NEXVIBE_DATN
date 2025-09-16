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
            <router-link :to="{ name: 'profile' }" class="list-group-item list-group-item-action" active-class="active"><i class="fas fa-user-edit fa-fw me-2"></i>Thông tin tài khoản</router-link>
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
      <div v-else-if="profile">
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
            <div class="mb-3">
              <label for="currentPassword" class="form-label">Mật khẩu hiện tại</label>
              <div class="input-group-with-icon">
                <i class="bi bi-shield-lock input-icon"></i>
                <input :type="showCurrentPassword ? 'text' : 'password'" class="form-control" id="currentPassword" v-model="passwordForm.currentPassword" required>
                <i class="bi toggle-password-icon" :class="showCurrentPassword ? 'bi-eye-slash' : 'bi-eye'" @click="showCurrentPassword = !showCurrentPassword"></i>
              </div>
            </div>
            <div class="mb-3">
              <label for="newPassword" class="form-label">Mật khẩu mới</label>
              <div class="input-group-with-icon">
                <i class="bi bi-shield-lock-fill input-icon"></i>
                <input :type="showNewPassword ? 'text' : 'password'" class="form-control" id="newPassword" v-model="passwordForm.newPassword" required>
                <i class="bi toggle-password-icon" :class="showNewPassword ? 'bi-eye-slash' : 'bi-eye'" @click="showNewPassword = !showNewPassword"></i>
              </div>
              <div class="password-strength-meter mt-2">
                <div class="strength-bar" :style="{ width: passwordStrength.width, backgroundColor: passwordStrength.color }"></div>
              </div>
              <div class="strength-text small mt-1" :style="{ color: passwordStrength.color }">
                {{ passwordStrength.text }}
              </div>
            </div>
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
          <button type="button" class="btn btn-primary" @click="changePassword" :disabled="isSubmittingPassword || passwordsDoNotMatch">
            {{ isSubmittingPassword ? 'Đang lưu...' : 'Lưu thay đổi' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted, ref, computed, onUnmounted } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';
import axios from 'axios';
import { Modal } from 'bootstrap';

const authStore = useAuthStore();
const router = useRouter();
const toast = useToast();

// --- STATE ---
const profile = ref(null);
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
    error.value = "Không thể xác thực người dùng."; 
    loading.value = false;
    router.push({ name: 'login' }); 
    return;
  }
  try {
    loading.value = true;
    error.value = null;
    const response = await axios.get(`${API_BASE_URL}/client/api/khachhang/${customerId}`);
    profile.value = response.data;
    Object.assign(profileForm, {
      tenKH: response.data.tenKH || '',
      sdt: response.data.sdt || '',
      gioiTinh: response.data.gioiTinh || 'Nam',
      ngaySinh: response.data.ngaySinh ? response.data.ngaySinh.split('T')[0] : '',
    });
    authStore.userFullName = response.data.tenKH;
  } catch (err) {
    console.error("Lỗi khi tải profile:", err);
    error.value = "Không thể tải thông tin tài khoản.";
    profile.value = null;
  } finally {
    loading.value = false;
  }
};

const updateProfile = async () => {
  isSubmitting.value = true;
  try {
    const customerId = authStore.user?.idKH;
    const response = await axios.put(`${API_BASE_URL}/client/api/khachhang/${customerId}`, profileForm);
    profile.value = response.data;
    if (profileModalInstance) profileModalInstance.hide();
    toast.success("Cập nhật thông tin thành công!");
  } catch (err) {
    toast.error("Lỗi khi cập nhật thông tin.");
  } finally {
    isSubmitting.value = false;
  }
};

const resetPasswordForm = () => {
  passwordForm.currentPassword = '';
  passwordForm.newPassword = '';
  passwordForm.confirmPassword = '';
};

const changePassword = async () => {
  if (passwordsDoNotMatch.value) {
    toast.error("Mật khẩu xác nhận không khớp!");
    return;
  }
  if (!passwordForm.currentPassword || !passwordForm.newPassword) {
    toast.error("Vui lòng điền đầy đủ thông tin.");
    return;
  }

  isSubmittingPassword.value = true;
  
  try {
    const accountId = authStore.user?.idTK;
    if (!accountId) {
      throw new Error("Không tìm thấy ID tài khoản người dùng.");
    }
    
    const payload = {
      currentPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword,
      confirmPassword: passwordForm.confirmPassword,
    };
    
    await axios.put(`${API_BASE_URL}/client/api/taikhoan/${accountId}/change-password`, payload);

    if (passwordModalInstance) {
      passwordModalInstance.hide();
    }
    toast.success("Đổi mật khẩu thành công! Vui lòng đăng nhập lại để tiếp tục.");
    
    setTimeout(() => {
        handleLogout();
    }, 2000);

  } catch (err) {
    const errorMessage = err.response?.data?.message || "Đã xảy ra lỗi không mong muốn. Vui lòng thử lại.";
    toast.error(errorMessage);
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

// --- COMPUTED PROPERTIES ---
const passwordStrength = computed(() => {
  const password = passwordForm.newPassword;
  let score = 0;
  if (!password) {
    return { width: '0%', color: '#dc3545', text: '' };
  }
  if (password.length >= 8) score++;
  if (/[A-Z]/.test(password)) score++;
  if (/[a-z]/.test(password)) score++;
  if (/[0-9]/.test(password)) score++;
  if (/[^A-Za-z0-9]/.test(password)) score++;
  switch (score) {
    case 0:
    case 1: return { width: '20%', color: '#dc3545', text: 'Rất yếu' };
    case 2: return { width: '40%', color: '#fd7e14', text: 'Yếu' };
    case 3: return { width: '60%', color: '#ffc107', text: 'Trung bình' };
    case 4: return { width: '80%', color: '#20c997', text: 'Mạnh' };
    case 5: return { width: '100%', color: '#198754', text: 'Rất mạnh' };
    default: return { width: '0%', color: '#dc3545', text: '' };
  }
});

const passwordsDoNotMatch = computed(() => {
  return passwordForm.newPassword && passwordForm.confirmPassword && passwordForm.newPassword !== passwordForm.confirmPassword;
});

// --- LIFECYCLE HOOKS ---
onMounted(() => {
  loadProfile();
  const profileModalElement = document.getElementById('profileModal');
  const passwordModalElement = document.getElementById('changePasswordModal');
  if (profileModalElement) {
    profileModalInstance = new Modal(profileModalElement);
  }
  if (passwordModalElement) {
    passwordModalInstance = new Modal(passwordModalElement);
    passwordModalElement.addEventListener('hidden.bs.modal', resetPasswordForm);
  }
});

onUnmounted(() => {
    const passwordModalElement = document.getElementById('changePasswordModal');
    if (passwordModalElement) {
        passwordModalElement.removeEventListener('hidden.bs.modal', resetPasswordForm);
    }
});
</script>

<style scoped>
.account-sidebar .list-group-item.active { 
  color: #fff; 
  background-color: var(--bs-primary); 
  border-color: var(--bs-primary);
}
.section-title { font-weight: 600; font-size: 1.25rem; margin-bottom: 1.5rem; padding-bottom: 0.75rem; border-bottom: 1px solid #e9ecef; }
.info-item { display: flex; justify-content: space-between; align-items: center; padding: 1rem 0; border-bottom: 1px solid #f8f9fa; }
.section-content .info-item:last-child { border-bottom: none; }
.info-item span:first-child { color: #6c757d; }
.info-item span:last-child { font-weight: 500; }
.section-footer { text-align: right; padding-top: 1.5rem; }
.password-strength-meter { height: 8px; background-color: #e9ecef; border-radius: 4px; overflow: hidden; }
.strength-bar { height: 100%; transition: width 0.3s ease-in-out, background-color 0.3s ease-in-out; }
.strength-text { font-weight: 500; }
.input-group-with-icon { position: relative; }
.input-icon { position: absolute; left: 12px; top: 50%; transform: translateY(-50%); color: #6c757d; }
.input-group-with-icon .form-control { padding-left: 2.5rem; }
.toggle-password-icon { position: absolute; right: 12px; top: 50%; transform: translateY(-50%); cursor: pointer; color: #6c757d; }
</style>