```vue
<template>
  <div class="container-fluid">
    <h1 class="h3 mb-4 text-gray-800">{{ isEditing ? 'Chỉnh sửa Voucher Sản phẩm' : 'Tạo Voucher Sản phẩm mới' }}</h1>
    <div v-if="message" :class="['alert', messageType === 'success' ? 'alert-success' : 'alert-danger']" role="alert">
      {{ message }}
      <button type="button" class="btn-close" @click="message = ''" aria-label="Close"></button>
    </div>
    <form @submit.prevent="saveVoucher">
      <!-- Thông tin cơ bản -->
      <div class="card shadow mb-4">
        <div class="card-header">
          <h6 class="m-0 fw-bold text-primary">Thông tin cơ bản</h6>
        </div>
        <div class="card-body">
          <div class="row">
            <div class="col-md-6 mb-3">
              <label for="voucherCode" class="form-label">Mã Voucher <span class="text-danger">*</span></label>
              <input
                type="text"
                class="form-control text-uppercase"
                id="voucherCode"
                v-model="voucher.maVoucher"
                required
                :class="{ 'is-invalid': errors.maVoucher }"
              />
              <div class="invalid-feedback">{{ errors.maVoucher }}</div>
            </div>
            <div class="col-md-6 mb-3">
              <label for="voucherDesc" class="form-label">Tên Voucher</label>
              <input
                type="text"
                class="form-control"
                id="voucherDesc"
                v-model="voucher.tenVoucher"
                :class="{ 'is-invalid': errors.tenVoucher }"
              />
              <div class="invalid-feedback">{{ errors.tenVoucher }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Giá trị & Điều kiện -->
      <div class="card shadow mb-4">
        <div class="card-header">
          <h6 class="m-0 fw-bold text-primary">Giá trị & Điều kiện</h6>
        </div>
        <div class="card-body">
          <div class="row">
            <div class="col-md-6 mb-3">
              <label for="voucherType" class="form-label">Loại giảm giá <span class="text-danger">*</span></label>
              <select
                class="form-select"
                id="voucherType"
                v-model="voucher.hinhThucGiam"
                required
                :class="{ 'is-invalid': errors.hinhThucGiam }"
              >
                <option value="percentage">Theo phần trăm (%)</option>
                <option value="fixed">Số tiền cố định (VND)</option>
              </select>
              <div class="invalid-feedback">{{ errors.hinhThucGiam }}</div>
            </div>
            <div class="col-md-6 mb-3">
              <label for="voucherValue" class="form-label">Giá trị giảm <span class="text-danger">*</span></label>
              <input
                type="number"
                class="form-control"
                id="voucherValue"
                v-model.number="voucher.mucGiam"
                required
                min="0"
                :class="{ 'is-invalid': errors.mucGiam }"
              />
              <div class="invalid-feedback">{{ errors.mucGiam }}</div>
            </div>
          </div>
          <div class="row">
            <div class="col-md-6 mb-3">
              <label for="maxDiscount" class="form-label">Giảm tối đa (VND)</label>
              <input
                type="number"
                class="form-control"
                id="maxDiscount"
                v-model.number="voucher.giamToiDa"
                :disabled="voucher.hinhThucGiam !== 'percentage'"
                min="0"
                :class="{ 'is-invalid': errors.giamToiDa }"
              />
              <div class="invalid-feedback">{{ errors.giamToiDa }}</div>
            </div>
            <div class="col-md-6 mb-3">
              <label for="donGiaKhiGiam" class="form-label">Đơn giá khi giảm (VND)</label>
              <input
                type="number"
                class="form-control"
                id="donGiaKhiGiam"
                v-model.number="voucher.donGiaKhiGiam"
                min="0"
                :class="{ 'is-invalid': errors.donGiaKhiGiam }"
              />
              <div class="invalid-feedback">{{ errors.donGiaKhiGiam }}</div>
            </div>
          </div>
          <div class="row">
            <div class="col-md-6 mb-3">
              <label for="giaGiam" class="form-label">Giá giảm (VND)</label>
              <input
                type="number"
                class="form-control"
                id="giaGiam"
                v-model.number="voucher.giaGiam"
                min="0"
                :class="{ 'is-invalid': errors.giaGiam }"
              />
              <div class="invalid-feedback">{{ errors.giaGiam }}</div>
            </div>
            <div class="col-md-6 mb-3">
              <label for="soLuong" class="form-label">Số lượng</label>
              <input
                type="number"
                class="form-control"
                id="soLuong"
                v-model.number="voucher.soLuong"
                min="0"
                :class="{ 'is-invalid': errors.soLuong }"
              />
              <div class="invalid-feedback">{{ errors.soLuong }}</div>
            </div>
          </div>
          <div class="row">
            <div class="col-md-12 mb-3">
              <label for="applicableProductCodes" class="form-label">Mã sản phẩm áp dụng (Mã SPCT)</label>
              <input
                type="text"
                class="form-control"
                id="applicableProductCodes"
                v-model="applicableProductCodesInput"
                placeholder="Ví dụ: SPCT001,SPCT002,SPCT003"
                :class="{ 'is-invalid': errors.applicableProductCodes }"
              />
              <div class="invalid-feedback">{{ errors.applicableProductCodes }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Thời gian hiệu lực & Trạng thái -->
      <div class="card shadow mb-4">
        <div class="card-header">
          <h6 class="m-0 fw-bold text-primary">Thời gian hiệu lực & Trạng thái</h6>
        </div>
        <div class="card-body">
          <div class="row">
            <div class="col-md-6 mb-3">
              <label for="startDate" class="form-label">Ngày bắt đầu <span class="text-danger">*</span></label>
              <input
                type="date"
                class="form-control"
                id="startDate"
                v-model="voucher.ngayBatDau"
                required
                :class="{ 'is-invalid': errors.ngayBatDau }"
              />
              <div class="invalid-feedback">{{ errors.ngayBatDau }}</div>
            </div>
            <div class="col-md-6 mb-3">
              <label for="endDate" class="form-label">Ngày kết thúc <span class="text-danger">*</span></label>
              <input
                type="date"
                class="form-control"
                id="endDate"
                v-model="voucher.ngayKetThuc"
                required
                :class="{ 'is-invalid': errors.ngayKetThuc }"
              />
              <div class="invalid-feedback">{{ errors.ngayKetThuc }}</div>
            </div>
          </div>
          <div class="row">
            <div class="col-md-6 mb-3">
              <label for="status" class="form-label">Trạng thái</label>
              <select
                class="form-select"
                id="status"
                v-model="voucher.trangThai"
                :class="{ 'is-invalid': errors.trangThai }"
              >
                <option :value="true">Hoạt động</option>
                <option :value="false">Không hoạt động</option>
              </select>
              <div class="invalid-feedback">{{ errors.trangThai }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="d-flex justify-content-end gap-2">
        <router-link :to="{ name: 'admin.vouchersp.list' }" class="btn btn-secondary">Hủy</router-link>
        <button type="submit" class="btn btn-primary" :disabled="isSubmitting">Lưu voucher</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const router = useRouter();
const isEditing = computed(() => !!route.params.id);
const isSubmitting = ref(false);
const message = ref('');
const messageType = ref('');
const applicableProductCodesInput = ref('');

const voucher = ref({
  id: null,
  maVoucher: '',
  tenVoucher: '',
  hinhThucGiam: 'percentage',
  mucGiam: 0,
  giamToiDa: null,
  donGiaKhiGiam: null,
  giaGiam: null,
  soLuong: null,
  ngayBatDau: '',
  ngayKetThuc: '',
  trangThai: true,
  applicableProductCodes: [], // Thay applicableProductIds bằng applicableProductCodes
});

const errors = ref({
  maVoucher: '',
  tenVoucher: '',
  hinhThucGiam: '',
  mucGiam: '',
  giamToiDa: '',
  donGiaKhiGiam: '',
  giaGiam: '',
  soLuong: '',
  ngayBatDau: '',
  ngayKetThuc: '',
  trangThai: '',
  applicableProductCodes: '',
});

const validateForm = () => {
  errors.value = {
    maVoucher: '',
    tenVoucher: '',
    hinhThucGiam: '',
    mucGiam: '',
    giamToiDa: '',
    donGiaKhiGiam: '',
    giaGiam: '',
    soLuong: '',
    ngayBatDau: '',
    ngayKetThuc: '',
    trangThai: '',
    applicableProductCodes: '',
  };
  let isValid = true;

  if (!voucher.value.maVoucher.trim()) {
    errors.value.maVoucher = 'Mã voucher không được để trống';
    isValid = false;
  } else if (!/^[A-Z0-9]+$/.test(voucher.value.maVoucher.trim())) {
    errors.value.maVoucher = 'Mã voucher chỉ được chứa chữ cái in hoa và số';
    isValid = false;
  }

  if (!['percentage', 'fixed'].includes(voucher.value.hinhThucGiam)) {
    errors.value.hinhThucGiam = 'Loại giảm giá không hợp lệ';
    isValid = false;
  }

  if (!voucher.value.mucGiam || voucher.value.mucGiam <= 0) {
    errors.value.mucGiam = 'Giá trị giảm phải lớn hơn 0';
    isValid = false;
  } else if (voucher.value.hinhThucGiam === 'percentage' && voucher.value.mucGiam > 100) {
    errors.value.mucGiam = 'Giá trị giảm phần trăm không được vượt quá 100';
    isValid = false;
  }

  if (voucher.value.hinhThucGiam === 'percentage' && voucher.value.giamToiDa !== null && voucher.value.giamToiDa <= 0) {
    errors.value.giamToiDa = 'Giảm tối đa phải lớn hơn 0';
    isValid = false;
  }

  if (voucher.value.donGiaKhiGiam !== null && voucher.value.donGiaKhiGiam < 0) {
    errors.value.donGiaKhiGiam = 'Đơn giá khi giảm không được âm';
    isValid = false;
  }

  if (voucher.value.giaGiam !== null && voucher.value.giaGiam < 0) {
    errors.value.giaGiam = 'Giá giảm không được âm';
    isValid = false;
  }

  if (voucher.value.soLuong !== null && voucher.value.soLuong < 0) {
    errors.value.soLuong = 'Số lượng không được âm';
    isValid = false;
  }

  if (!voucher.value.ngayBatDau) {
    errors.value.ngayBatDau = 'Ngày bắt đầu không được để trống';
    isValid = false;
  }
  if (!voucher.value.ngayKetThuc) {
    errors.value.ngayKetThuc = 'Ngày kết thúc không được để trống';
    isValid = false;
  }
  if (voucher.value.ngayBatDau && voucher.value.ngayKetThuc) {
    const start = new Date(voucher.value.ngayBatDau);
    const end = new Date(voucher.value.ngayKetThuc);
    if (end <= start) {
      errors.value.ngayKetThuc = 'Ngày kết thúc phải sau ngày bắt đầu';
      isValid = false;
    }
  }

  if (applicableProductCodesInput.value) {
    const codes = applicableProductCodesInput.value.split(',').map(code => code.trim()).filter(code => code);
    if (codes.length === 0) {
      errors.value.applicableProductCodes = 'Danh sách mã sản phẩm áp dụng không hợp lệ';
      isValid = false;
    }
    voucher.value.applicableProductCodes = codes;
  } else {
    voucher.value.applicableProductCodes = [];
  }

  return isValid;
};

const fetchVoucher = async () => {
  if (!isEditing.value) return;
  try {
    const response = await axios.get(`http://localhost:8080/api/voucher/${route.params.id}`);
    const data = response.data;
    voucher.value = {
      id: data.id,
      maVoucher: data.maVoucher,
      tenVoucher: data.tenVoucher || '',
      hinhThucGiam: data.hinhThucGiam,
      mucGiam: data.mucGiam,
      giamToiDa: data.giamToiDa,
      donGiaKhiGiam: data.donGiaKhiGiam,
      giaGiam: data.giaGiam,
      soLuong: data.soLuong,
      ngayBatDau: data.ngayBatDau ? new Date(data.ngayBatDau).toISOString().slice(0, 10) : '',
      ngayKetThuc: data.ngayKetThuc ? new Date(data.ngayKetThuc).toISOString().slice(0, 10) : '',
      trangThai: data.trangThai ?? true,
      applicableProductCodes: data.applicableProductCodes || [], // Nhận danh sách MaSPCT
    };
    applicableProductCodesInput.value = voucher.value.applicableProductCodes.join(', ');
  } catch (error) {
    console.error('Lỗi khi lấy dữ liệu voucher:', error);
    message.value = 'Không thể lấy dữ liệu voucher';
    messageType.value = 'error';
  }
};

const saveVoucher = async () => {
  if (!validateForm()) {
    message.value = 'Vui lòng kiểm tra lại các trường thông tin';
    messageType.value = 'error';
    return;
  }

  isSubmitting.value = true;
  const payload = {
    id: voucher.value.id,
    maVoucher: voucher.value.maVoucher.trim(),
    tenVoucher: voucher.value.tenVoucher || null,
    hinhThucGiam: voucher.value.hinhThucGiam,
    mucGiam: voucher.value.mucGiam,
    giamToiDa: voucher.value.hinhThucGiam === 'percentage' ? voucher.value.giamToiDa : null,
    donGiaKhiGiam: voucher.value.donGiaKhiGiam,
    giaGiam: voucher.value.giaGiam,
    giaTriDonHangToiThieu: 0,
    soLuong: voucher.value.soLuong,
    ngayBatDau: new Date(voucher.value.ngayBatDau).toISOString(),
    ngayKetThuc: new Date(voucher.value.ngayKetThuc).toISOString(),
    trangThai: voucher.value.trangThai,
    applicableProductCodes: voucher.value.applicableProductCodes, // Gửi danh sách MaSPCT
  };

  try {
    if (isEditing.value) {
      await axios.put(`http://localhost:8080/api/voucher/${voucher.value.id}`, payload);
      message.value = 'Cập nhật voucher thành công';
      messageType.value = 'success';
    } else {
      await axios.post('http://localhost:8080/api/voucher', payload);
      message.value = 'Tạo voucher thành công';
      messageType.value = 'success';
    }
    setTimeout(() => {
      router.push({ name: 'admin.vouchersp.list' });
    }, 2000);
  } catch (error) {
    console.error('Lỗi khi lưu voucher:', error);
    message.value = 'Lỗi khi lưu voucher: ' + (error.response?.data?.error || error.message);
    messageType.value = 'error';
  } finally {
    isSubmitting.value = false;
  }
};

onMounted(() => {
  fetchVoucher();
});
</script>

<style scoped>
.alert {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 1000;
  min-width: 300px;
}
</style>
```