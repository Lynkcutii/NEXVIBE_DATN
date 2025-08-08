<template>
  <div class="auth-page-wrapper">
    <div class="auth-form-container m-2">
      <div class="card border-0">
        <div class="card-body p-4 p-lg-5">
          <!-- Progress Steps -->
          <div class="registration-steps mb-4">
            <div class="step-indicator">
              <div class="step" :class="{ active: currentStep === 1, completed: currentStep > 1 }">
                <div class="step-number">1</div>
                <div class="step-label">Thông tin cá nhân</div>
              </div>
              <div class="step-line" :class="{ completed: currentStep > 1 }"></div>
              <div class="step" :class="{ active: currentStep === 2 }">
                <div class="step-number">2</div>
                <div class="step-label">Tài khoản</div>
              </div>
            </div>
          </div>

          <!-- Step 1: Customer Information -->
          <div v-if="currentStep === 1">
            <h4 class="card-title mb-4 fw-bold">Thông tin cá nhân</h4>
            
            <form @submit.prevent="handleCustomerSubmit">
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label class="form-label">Họ và tên *</label>
                  <input
                    v-model="customerForm.tenKH"
                    type="text"
                    class="form-control"
                    placeholder="Nhập họ và tên"
                    required
                  />
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label">Giới tính *</label>
                  <select v-model="customerForm.gioiTinh" class="form-select" required>
                    <option value="">Chọn giới tính</option>
                    <option value="Nam">Nam</option>
                    <option value="Nữ">Nữ</option>
                    <option value="Khác">Khác</option>
                  </select>
                </div>
              </div>

              <div class="row">
                <div class="col-md-6 mb-3">
                  <label class="form-label">Ngày sinh *</label>
                  <input
                    v-model="customerForm.ngaySinh"
                    type="date"
                    class="form-control"
                    required
                  />
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label">Số điện thoại *</label>
                  <input
                    v-model="customerForm.sdt"
                    type="tel"
                    class="form-control"
                    placeholder="Nhập số điện thoại"
                    pattern="[0-9]{10,11}"
                    required
                  />
                </div>
              </div>

              <div class="row">
                <div class="col-md-6 mb-3">
                  <label class="form-label">Email *</label>
                  <input
                    v-model="customerForm.email"
                    type="email"
                    class="form-control"
                    placeholder="Nhập địa chỉ email"
                    required
                  />
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label">Địa chỉ cụ thể *</label>
                  <input
                    v-model="customerForm.diaChiCuThe"
                    type="text"
                    class="form-control"
                    placeholder="Nhập số nhà, đường"
                    required
                  />
                </div>
              </div>

              <div class="row">
                <div class="col-md-6 mb-3">
                  <label class="form-label">Tỉnh/Thành phố *</label>
                  <input
                    v-model="customerForm.tinhThanh"
                    type="text"
                    class="form-control"
                    placeholder="Nhập tỉnh/thành phố"
                    required
                  />
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label">Phường/Xã *</label>
                  <input
                    v-model="customerForm.phuongXa"
                    type="text"
                    class="form-control"
                    placeholder="Nhập phường/xã"
                    required
                  />
                </div>
              </div>

              <div class="mb-3">
                <label class="form-label">Ghi chú</label>
                <input
                  v-model="customerForm.ghiChu"
                  type="text"
                  class="form-control"
                  placeholder="Nhập ghi chú (nếu có)"
                />
              </div>

              <div v-if="errorMessage" class="alert alert-danger">
                {{ errorMessage }}
              </div>

              <button type="submit" class="btn btn-primary w-100 mt-3" :disabled="isLoading">
                <span v-if="isLoading" class="spinner-border spinner-border-sm me-2"></span>
                Tiếp tục
              </button>
            </form>
          </div>

          <!-- Step 2: Account Information -->
          <div v-if="currentStep === 2">
            <h4 class="card-title mb-4 fw-bold">Tạo tài khoản</h4>
            
            <form @submit.prevent="handleAccountSubmit">
              <div class="mb-3">
                <label class="form-label">Tên đăng nhập *</label>
                <input
                  v-model="accountForm.taiKhoan"
                  type="text"
                  class="form-control"
                  placeholder="Nhập tên đăng nhập"
                  minlength="6"
                  required
                />
                <div class="form-text">Tên đăng nhập phải có ít nhất 6 ký tự, chỉ chứa chữ cái, số hoặc dấu gạch dưới (_).</div>
                <div v-if="accountForm.taiKhoan && !isUsernameValid" class="text-danger small">
                  Tên đăng nhập không được chứa ký tự đặc biệt hoặc dấu cách!
                </div>
              </div>

              <div class="mb-3 position-relative">
                <label class="form-label">Mật khẩu *</label>
                <input
                  v-model="accountForm.matKhau"
                  :type="showPassword ? 'text' : 'password'"
                  class="form-control"
                  placeholder="Nhập mật khẩu"
                  minlength="8"
                  required
                />
                <i
                  :class="showPassword ? 'bi bi-eye-slash' : 'bi bi-eye'"
                  class="position-absolute password-toggle"
                  @click="showPassword = !showPassword"
                ></i>
                <div class="form-text">Mật khẩu phải có ít nhất 8 ký tự.</div>
              </div>

              <div class="mb-3 position-relative">
                <label class="form-label">Xác nhận mật khẩu *</label>
                <input
                  v-model="accountForm.xacNhanMatKhau"
                  :type="showConfirmPassword ? 'text' : 'password'"
                  class="form-control"
                  placeholder="Nhập lại mật khẩu"
                  required
                />
                <i
                  :class="showConfirmPassword ? 'bi bi-eye-slash' : 'bi bi-eye'"
                  class="position-absolute password-toggle"
                  @click="showConfirmPassword = !showConfirmPassword"
                ></i>
                <div v-if="accountForm.xacNhanMatKhau && accountForm.matKhau !== accountForm.xacNhanMatKhau" 
                     class="text-danger small">
                  Mật khẩu xác nhận không khớp
                </div>
              </div>

              <div class="mb-3">
                <div class="form-check">
                  <input
                    v-model="accountForm.dongYDieuKhoan"
                    class="form-check-input"
                    type="checkbox"
                    id="terms"
                    required
                  />
                  <label class="form-check-label small" for="terms">
                    Tôi đồng ý với 
                    <a href="#" class="text-decoration-none">Điều khoản dịch vụ</a> 
                    và 
                    <a href="#" class="text-decoration-none">Chính sách bảo mật</a> 
                    của NEXVIBE
                  </label>
                </div>
              </div>

              <div v-if="errorMessage" class="alert alert-danger">
                {{ errorMessage }}
              </div>

              <div class="row g-2">
                <div class="col">
                  <button type="button" class="btn btn-outline-secondary w-100" @click="goBack">
                    Quay lại
                  </button>
                </div>
                <div class="col">
                  <button type="submit" class="btn btn-primary w-100" :disabled="isLoading || !isAccountFormValid">
                    <span v-if="isLoading" class="spinner-border spinner-border-sm me-2"></span>
                    Đăng ký
                  </button>
                </div>
              </div>
            </form>
          </div>

          <div class="text-center mt-4">
            <span class="text-muted small">Bạn đã có tài khoản? </span>
            <router-link to="/login" class="small fw-bold text-decoration-none">Đăng nhập</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';
import axios from 'axios';

const router = useRouter();
const toast = useToast();

const currentStep = ref(1);
const isLoading = ref(false);
const errorMessage = ref('');

const customerForm = ref({
  tenKH: '',
  gioiTinh: '',
  ngaySinh: '',
  sdt: '',
  email: '',
  diaChiCuThe: '',
  tinhThanh: '',
  phuongXa: '',
  soDienThoai: '',
  ghiChu: ''
});

const accountForm = ref({
  taiKhoan: '',
  matKhau: '',
  xacNhanMatKhau: '',
  dongYDieuKhoan: false
});

const showPassword = ref(false);
const showConfirmPassword = ref(false);

const isUsernameValid = computed(() => {
  const regex = /^[a-zA-Z0-9_]+$/;
  return regex.test(accountForm.value.taiKhoan);
});

const isAccountFormValid = computed(() => {
  return accountForm.value.taiKhoan.length >= 6 &&
         isUsernameValid.value &&
         accountForm.value.matKhau.length >= 8 &&
         accountForm.value.matKhau === accountForm.value.xacNhanMatKhau &&
         accountForm.value.dongYDieuKhoan;
});

const handleCustomerSubmit = async () => {
  if (!customerForm.value.tenKH || !customerForm.value.gioiTinh || !customerForm.value.ngaySinh ||
      !customerForm.value.sdt || !customerForm.value.email || 
      !customerForm.value.diaChiCuThe || !customerForm.value.tinhThanh || !customerForm.value.phuongXa) {
    errorMessage.value = 'Vui lòng nhập đầy đủ các thông tin bắt buộc!';
    toast.error(errorMessage.value);
    return;
  }

  // Kiểm tra định dạng ngày sinh
  const today = new Date();
  const selectedDate = new Date(customerForm.value.ngaySinh);
  if (selectedDate >= today) {
    errorMessage.value = 'Ngày sinh không hợp lệ! Vui lòng chọn ngày sinh trong quá khứ.';
    toast.error(errorMessage.value);
    return;
  }

  currentStep.value = 2;
};

const handleAccountSubmit = async () => {
  isLoading.value = true;
  errorMessage.value = '';
  
  try {
    const registerData = {
      taiKhoan: accountForm.value.taiKhoan,
      matKhau: accountForm.value.matKhau,
      tenKH: customerForm.value.tenKH,
      gioiTinh: customerForm.value.gioiTinh,
      ngaySinh: customerForm.value.ngaySinh,
      sdt: customerForm.value.sdt,
      email: customerForm.value.email,
      diaChiCuThe: customerForm.value.diaChiCuThe,
      tinhThanh: customerForm.value.tinhThanh,
      phuongXa: customerForm.value.phuongXa,
      soDienThoai: customerForm.value.soDienThoai,
      ghiChu: customerForm.value.ghiChu
    };

    const response = await axios.post('http://localhost:8080/api/auth/register', registerData, {
      headers: { 'Content-Type': 'application/json' },
      withCredentials: true
    });

    toast.success('Đăng ký thành công! Vui lòng đăng nhập để tiếp tục.');
    router.push('/login');
    
  } catch (error) {
    if (error.response) {
      if (error.response.status === 409) {
        errorMessage.value = 'Tên đăng nhập đã tồn tại, vui lòng chọn tên khác!';
      } else {
        errorMessage.value = error.response.data.message || 'Có lỗi xảy ra khi đăng ký!';
      }
    } else if (error.request) {
      errorMessage.value = 'Lỗi mạng, vui lòng kiểm tra kết nối!';
    } else {
      errorMessage.value = 'Lỗi không xác định: ' + error.message;
    }
    toast.error(errorMessage.value);
  } finally {
    isLoading.value = false;
  }
};

const goBack = () => {
  currentStep.value = 1;
  errorMessage.value = '';
};
</script>

<style scoped>
.password-toggle {
  top: 50%;
  right: 10px;
  transform: translateY(-20%);
  cursor: pointer;
  font-size: 1.2rem;
}

.auth-form-container {
  width: 100%;
  max-width: 600px;
}

.card {
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border-radius: 15px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

.registration-steps {
  margin-bottom: 2rem;
}

.step-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 1rem;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  position: relative;
}

.step-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #e9ecef;
  color: #6c757d;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 8px;
  transition: all 0.3s ease;
}

.step.active .step-number {
  background: #0d6efd;
  color: white;
}

.step.completed .step-number {
  background: #198754;
  color: white;
}

.step-label {
  font-size: 12px;
  color: #6c757d;
  font-weight: 500;
}

.step.active .step-label {
  color: #0d6efd;
  font-weight: 600;
}

.step.completed .step-label {
  color: #198754;
  font-weight: 600;
}

.step-line {
  flex: 1;
  height: 2px;
  background: #e9ecef;
  margin: 0 20px;
  margin-top: -16px;
  transition: all 0.3s ease;
}

.step-line.completed {
  background: #198754;
}

.form-label {
  font-weight: 600;
  color: #495057;
  margin-bottom: 0.5rem;
}

.form-control, .form-select {
  border: 2px solid #e9ecef;
  border-radius: 8px;
  padding: 12px 16px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.form-control:focus, .form-select:focus {
  border-color: #0d6efd;
  box-shadow: 0 0 0 0.2rem rgba(13, 110, 253, 0.25);
}

.btn-primary {
  background: linear-gradient(135deg, #0d6efd, #0b5ed7);
  border: none;
  border-radius: 8px;
  padding: 12px 24px;
  font-weight: 600;
  font-size: 14px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  background: linear-gradient(135deg, #0b5ed7, #0a58ca);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(13, 110, 253, 0.4);
}

.btn-outline-secondary {
  border: 2px solid #6c757d;
  border-radius: 8px;
  padding: 12px 24px;
  font-weight: 600;
  font-size: 14px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
}

.btn-outline-secondary:hover {
  background: #6c757d;
  border-color: #6c757d;
  transform: translateY(-1px);
}

.form-check-input:checked {
  background-color: #0d6efd;
  border-color: #0d6efd;
}

.alert {
  border: none;
  border-radius: 8px;
  font-size: 14px;
}

.spinner-border-sm {
  width: 1rem;
  height: 1rem;
}

@media (max-width: 768px) {
  .step-indicator {
    flex-direction: column;
    gap: 1rem;
  }
  
  .step-line {
    display: none;
  }
  
  .auth-form-container {
    margin: 0;
  }
  
  .card-body {
    padding: 1.5rem !important;
  }
}
</style>