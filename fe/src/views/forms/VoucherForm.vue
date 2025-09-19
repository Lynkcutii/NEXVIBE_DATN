<template>
  <div class="container-fluid">
    <!-- Thông báo lỗi hoặc thành công -->
    <div v-if="message" :class="['alert', messageType === 'success' ? 'alert-success' : 'alert-danger']" role="alert">
      {{ message }}
      <button type="button" class="btn-close" @click="message = ''" aria-label="Close"></button>
    </div>

    <h1 class="h3 mb-4 text-gray-800">{{ isEditing ? 'Chỉnh sửa Khuyến mại' : 'Tạo Khuyến mại mới' }}</h1>
    <form @submit.prevent="saveVoucher">
      <!-- Thông tin cơ bản -->
      <div class="card shadow mb-4">
        <div class="card-header">
          <h6 class="m-0 fw-bold text-primary">Thông tin cơ bản</h6>
        </div>
        <div class="card-body">
          <div class="row">
            <div class="col-md-6 mb-3">
              <label for="voucherCode" class="form-label">Mã Code <span class="text-danger">*</span></label>
              <input
                type="text"
                class="form-control text-uppercase"
                id="voucherCode"
                v-model="voucher.code"
                required
                :class="{ 'is-invalid': errors.code }"
              />
              <div class="invalid-feedback">{{ errors.code }}</div>
            </div>
            <div class="col-md-6 mb-3">
              <label for="voucherDesc" class="form-label">Tên khuyến mại</label>
              <input
                type="text"
                class="form-control"
                id="voucherDesc"
                v-model="voucher.description"
                :class="{ 'is-invalid': errors.description }"
              />
              <div class="invalid-feedback">{{ errors.description }}</div>
            </div>
          </div>
          <div class="row">
            <div class="col-md-6 mb-3">
              <label for="customerId" class="form-label">ID Khách hàng (Để trống nếu áp dụng cho tất cả)</label>
              <input
                type="number"
                class="form-control"
                id="customerId"
                v-model.number="voucher.customer_id"
                min="0"
                :class="{ 'is-invalid': errors.customer_id }"
              />
              <div class="invalid-feedback">{{ errors.customer_id }}</div>
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
                v-model="voucher.type"
                required
                :class="{ 'is-invalid': errors.type }"
              >
                <option value="percentage">Theo phần trăm (%)</option>
                <option value="fixed">Số tiền cố định (VND)</option>
              </select>
              <div class="invalid-feedback">{{ errors.type }}</div>
            </div>
            <div class="col-md-6 mb-3">
              <label for="voucherValue" class="form-label">Giá trị giảm <span class="text-danger">*</span></label>
              <input
                type="number"
                class="form-control"
                id="voucherValue"
                v-model.number="voucher.value"
                required
                min="0"
                :class="{ 'is-invalid': errors.value }"
              />
              <div class="invalid-feedback">{{ errors.value }}</div>
            </div>
          </div>
          <div class="row">
            <div class="col-md-6 mb-3">
              <label for="maxDiscount" class="form-label">Giảm tối đa (VND)</label>
              <input
                type="number"
                class="form-control"
                id="maxDiscount"
                v-model.number="voucher.max_discount"
                :disabled="voucher.type !== 'percentage'"
                min="0"
                :class="{ 'is-invalid': errors.max_discount }"
              />
              <div class="invalid-feedback">{{ errors.max_discount }}</div>
            </div>
            <div class="col-md-6 mb-3">
              <label for="minOrderValue" class="form-label">Giá trị đơn hàng tối thiểu (VND)</label>
              <input
                type="number"
                class="form-control"
                id="minOrderValue"
                v-model.number="voucher.min_order_value"
                min="0"
                :class="{ 'is-invalid': errors.min_order_value }"
              />
              <div class="invalid-feedback">{{ errors.min_order_value }}</div>
            </div>
          </div>
          <div class="row">
            <div class="col-md-6 mb-3">
              <label for="maxUses" class="form-label">Số lần sử dụng tối đa</label>
              <input
                type="number"
                class="form-control"
                id="maxUses"
                v-model.number="voucher.max_uses"
                min="0"
                :class="{ 'is-invalid': errors.max_uses }"
              />
              <div class="invalid-feedback">{{ errors.max_uses }}</div>
            </div>
            <div class="col-md-6 mb-3">
              <label for="usedCount" class="form-label">Số lần đã sử dụng</label>
              <input
                type="number"
                class="form-control"
                id="usedCount"
                v-model.number="voucher.used_count"
                min="0"
                :class="{ 'is-invalid': errors.used_count }"
              />
              <div class="invalid-feedback">{{ errors.used_count }}</div>
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
                type="datetime-local"
                class="form-control"
                id="startDate"
                v-model="voucher.start_date"
                required
                :class="{ 'is-invalid': errors.start_date }"
              />
              <div class="invalid-feedback">{{ errors.start_date }}</div>
            </div>
            <div class="col-md-6 mb-3">
              <label for="endDate" class="form-label">Ngày kết thúc <span class="text-danger">*</span></label>
              <input
                type="datetime-local"
                class="form-control"
                id="endDate"
                v-model="voucher.end_date"
                required
                :class="{ 'is-invalid': errors.end_date }"
              />
              <div class="invalid-feedback">{{ errors.end_date }}</div>
            </div>
          </div>
          <div class="row">
            <div class="col-md-6 mb-3">
              <label for="status" class="form-label">Trạng thái</label>
              <select
                class="form-select"
                id="status"
                v-model="voucher.status"
                :class="{ 'is-invalid': errors.status }"
              >
                <option :value="true">Hoạt động</option>
                <option :value="false">Không hoạt động</option>
              </select>
              <div class="invalid-feedback">{{ errors.status }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="d-flex justify-content-end gap-2">
        <router-link :to="{ name: 'admin.vouchers.list' }" class="btn btn-secondary">Hủy</router-link>
        <button type="submit" class="btn btn-primary" :disabled="isSubmitting">Lưu khuyến mại</button>
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
const message = ref(''); // Thông báo lỗi hoặc thành công
const messageType = ref(''); // 'success' hoặc 'error'

const voucher = ref({
  id: null,
  code: '',
  description: '',
  type: 'percentage',
  value: 0,
  min_order_value: 0,
  max_discount: null,
  max_uses: null,
  used_count: 0,
  start_date: '',
  end_date: '',
  status: true,
  customer_id: null,
});

const errors = ref({
  code: '',
  description: '',
  type: '',
  value: '',
  min_order_value: '',
  max_discount: '',
  max_uses: '',
  used_count: '',
  start_date: '',
  end_date: '',
  status: '',
  customer_id: '',
});

// Hàm validate form
const validateForm = () => {
  errors.value = {
    code: '',
    description: '',
    type: '',
    value: '',
    min_order_value: '',
    max_discount: '',
    max_uses: '',
    used_count: '',
    start_date: '',
    end_date: '',
    status: '',
    customer_id: '',
  };
  let isValid = true;

  // Validate mã khuyến mãi
  if (!voucher.value.code.trim()) {
    errors.value.code = 'Mã khuyến mãi không được để trống';
    isValid = false;
  } else if (!/^[A-Z0-9]+$/.test(voucher.value.code.trim())) {
    errors.value.code = 'Mã khuyến mãi chỉ được chứa chữ cái in hoa và số';
    isValid = false;
  }

  // Validate loại giảm giá
  if (!['percentage', 'fixed'].includes(voucher.value.type)) {
    errors.value.type = 'Lo Précédent loai giảm giá không hợp lệ';
    isValid = false;
  }

  // Validate giá trị giảm
  if (!voucher.value.value || voucher.value.value <= 0) {
    errors.value.value = 'Giá trị giảm phải lớn hơn 0';
    isValid = false;
  } else if (voucher.value.type === 'percentage' && voucher.value.value > 100) {
    errors.value.value = 'Giá trị giảm phần trăm không được vượt quá 100';
    isValid = false;
  }

  // Validate giảm tối đa (nếu là percentage)
  if (voucher.value.type === 'percentage' && voucher.value.max_discount !== null && voucher.value.max_discount <= 0) {
    errors.value.max_discount = 'Giảm tối đa phải lớn hơn 0';
    isValid = false;
  }

  // Validate giá trị đơn hàng tối thiểu
  if (voucher.value.min_order_value !== null && voucher.value.min_order_value < 0) {
    errors.value.min_order_value = 'Giá trị đơn hàng tối thiểu không được âm';
    isValid = false;
  }

  // Validate số lần sử dụng tối đa và đã sử dụng
  if (voucher.value.max_uses !== null && voucher.value.max_uses < 0) {
    errors.value.max_uses = 'Số lần sử dụng tối đa không được âm';
    isValid = false;
  }
  if (voucher.value.used_count < 0) {
    errors.value.used_count = 'Số lần đã sử dụng không được âm';
    isValid = false;
  }
  if (
    voucher.value.max_uses !== null &&
    voucher.value.used_count > voucher.value.max_uses
  ) {
    errors.value.used_count = 'Số lần đã sử dụng không được vượt quá số lần tối đa';
    isValid = false;
  }

  // Validate ngày bắt đầu và ngày kết thúc
  if (!voucher.value.start_date) {
    errors.value.start_date = 'Ngày bắt đầu không được để trống';
    isValid = false;
  }
  if (!voucher.value.end_date) {
    errors.value.end_date = 'Ngày kết thúc không được để trống';
    isValid = false;
  }
  if (voucher.value.start_date && voucher.value.end_date) {
    const start = new Date(voucher.value.start_date);
    const end = new Date(voucher.value.end_date);
    if (end <= start) {
      errors.value.end_date = 'Ngày kết thúc phải sau ngày bắt đầu';
      isValid = false;
    }
  }

  // Validate ID khách hàng
  if (voucher.value.customer_id !== null && voucher.value.customer_id <= 0) {
    errors.value.customer_id = 'ID khách hàng phải lớn hơn 0';
    isValid = false;
  }

  return isValid;
};

// Lấy dữ liệu khuyến mãi khi chỉnh sửa
const fetchVoucher = async () => {
  if (!isEditing.value) return;
  try {
    const response = await axios.get(`http://localhost:8080/api/khuyenmai/${route.params.id}`);
    const data = response.data;
    voucher.value = {
      id: data.idKM,
      code: data.maKM,
      description: data.tenKM || '',
      type: data.hinhThucGiam,
      value: data.mucGiam,
      min_order_value: data.giaTriDonHangToiThieu || 0,
      max_discount: data.giamToiDa,
      max_uses: data.soLuong,
      used_count: data.daSuDung || 0,
      start_date: data.ngayBatDau ? new Date(data.ngayBatDau).toISOString().slice(0, 16) : '',
      end_date: data.ngayKetThuc ? new Date(data.ngayKetThuc).toISOString().slice(0, 16) : '',
      status: data.trangThai ?? true,
      customer_id: data.idKH,
    };
  } catch (error) {
    console.error('Lỗi khi lấy dữ liệu khuyến mãi:', error);
    message.value = 'Không thể lấy dữ liệu khuyến mãi';
    messageType.value = 'error';
  }
};

// Lưu khuyến mãi
const saveVoucher = async () => {
  if (!validateForm()) {
    message.value = 'Vui lòng kiểm tra lại các trường thông tin';
    messageType.value = 'error';
    return;
  }

  isSubmitting.value = true;
  const payload = {
    idKM: voucher.value.id,
    maKM: voucher.value.code.trim(),
    tenKM: voucher.value.description || null,
    hinhThucGiam: voucher.value.type,
    mucGiam: voucher.value.value,
    giaTriDonHangToiThieu: voucher.value.min_order_value || 0,
    giamToiDa: voucher.value.type === 'percentage' ? voucher.value.max_discount : null,
    soLuong: voucher.value.max_uses,
    daSuDung: voucher.value.used_count || 0,
    ngayBatDau: new Date(voucher.value.start_date).toISOString(),
    ngayKetThuc: new Date(voucher.value.end_date).toISOString(),
    trangThai: voucher.value.status,
    idKH: voucher.value.customer_id || null,
  };

  try {
    if (isEditing.value) {
      await axios.put(`http://localhost:8080/api/khuyenmai/${voucher.value.id}`, payload);
      message.value = 'Cập nhật khuyến mãi thành công';
      messageType.value = 'success';
    } else {
      await axios.post('http://localhost:8080/api/khuyenmai', payload);
      message.value = 'Tạo khuyến mãi thành công';
      messageType.value = 'success';
    }
    setTimeout(() => {
      router.push({ name: 'admin.vouchers.list' });
    }, 2000); // Chuyển hướng sau 2 giây để người dùng thấy thông báo
  } catch (error) {
    console.error('Lỗi khi lưu khuyến mãi:', error);
    message.value = 'Lỗi khi lưu khuyến mãi: ' + (error.response?.data?.message || error.message);
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