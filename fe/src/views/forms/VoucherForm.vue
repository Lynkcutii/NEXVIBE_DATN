```vue
<template>
  <div class="container-fluid">
    <!-- Thông báo lỗi hoặc thành công -->
    <div v-if="message" :class="['alert', messageType === 'success' ? 'alert-success' : 'alert-danger']" role="alert">
      {{ message }}
      <button type="button" class="btn-close" @click="message = ''" aria-label="Close"></button>
    </div>

    <h1 class="h3 mb-4 text-gray-800">Chỉnh sửa Khuyến mại</h1>
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
                disabled
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
            <div class="col-md-12 mb-3">
              <label for="customerIds" class="form-label">Khách hàng áp dụng</label>
              <div class="input-group">
                <input
                  type="text"
                  class="form-control"
                  :value="voucher.customer_names.length ? voucher.customer_names.join(', ') : 'Tất cả'"
                  readonly
                />
                <button
                  type="button"
                  class="btn btn-outline-primary"
                  @click="openSelectCustomerModal"
                >
                  Chọn khách hàng
                </button>
                <button
                  v-if="voucher.customer_ids.length"
                  type="button"
                  class="btn btn-outline-danger"
                  @click="clearCustomers"
                >
                  Xóa tất cả
                </button>
              </div>
              <!-- Hiển thị danh sách khách hàng đã chọn -->
              <div v-if="voucher.customer_ids.length" class="mt-2">
                <span
                  v-for="(name, index) in voucher.customer_names"
                  :key="voucher.customer_ids[index]"
                  class="badge bg-secondary me-1 mb-1"
                >
                  {{ name }}
                  <button
                    type="button"
                    class="btn-close btn-close-white ms-1"
                    @click="removeCustomer(index)"
                    aria-label="Xóa"
                  ></button>
                </span>
              </div>
              <div v-if="errors.customer_ids" class="invalid-feedback d-block">{{ errors.customer_ids }}</div>
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

      <!-- Modal chọn khách hàng -->
      <div class="modal fade" id="selectCustomerModal" tabindex="-1">
        <div class="modal-dialog modal-lg">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Chọn Khách hàng</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
              <div class="mb-3">
                <input
                  type="text"
                  class="form-control"
                  v-model="customerSearchQuery"
                  placeholder="Tìm kiếm theo tên hoặc số điện thoại khách hàng"
                  @input="debounceFetchCustomers"
                />
              </div>
              <div class="table-responsive">
                <table class="table table-hover">
                  <thead>
                    <tr>
                      <th>Chọn</th>
                      <th>Tên khách hàng</th>
                      <th>Số điện thoại</th>
                      <th>Trạng thái</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-if="customers.length === 0">
                      <td colspan="4" class="text-center">Không tìm thấy khách hàng</td>
                    </tr>
                    <tr v-for="customer in customers" :key="customer.id">
                      <td>
                        <input
                          type="checkbox"
                          :value="customer.id"
                          :checked="voucher.customer_ids.includes(customer.id)"
                          @change="toggleCustomer(customer.id, customer.tenKH)"
                          :disabled="!customer.trangThai"
                        />
                      </td>
                      <td>{{ customer.tenKH }}</td>
                      <td>{{ customer.sdt }}</td>
                      <td>{{ customer.trangThai ? 'Hoạt động' : 'Không hoạt động' }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <div class="d-flex justify-content-between">
                <button
                  class="btn btn-secondary"
                  :disabled="currentPage === 0"
                  @click="currentPage--; fetchCustomers()"
                >
                  Trang trước
                </button>
                <span>Trang {{ currentPage + 1 }} / {{ totalPages }}</span>
                <button
                  class="btn btn-secondary"
                  :disabled="currentPage >= totalPages - 1"
                  @click="currentPage++; fetchCustomers()"
                >
                  Trang sau
                </button>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-primary" data-bs-dismiss="modal">Xong</button>
            </div>
          </div>
        </div>
      </div>

      <div class="d-flex justify-content-end gap-2">
        <a href="http://localhost:5175/admin/vouchers" class="btn btn-secondary">Hủy</a>
        <button type="submit" class="btn btn-primary" :disabled="isSubmitting">Lưu khuyến mại</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { Modal } from 'bootstrap';
import { debounce } from 'lodash';

const route = useRoute();
const router = useRouter();
const isSubmitting = ref(false);
const message = ref('');
const messageType = ref('');
const customers = ref([]);
const customerSearchQuery = ref('');
const currentPage = ref(0);
const totalPages = ref(1);
const pageSize = 10;
let selectCustomerModalInstance = null;

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
  customer_ids: [],
  customer_names: [],
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
  customer_ids: '',
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
    customer_ids: '',
  };
  let isValid = true;

  if (!voucher.value.code.trim()) {
    errors.value.code = 'Mã khuyến mãi không được để trống';
    isValid = false;
  } else if (!/^[A-Z0-9]+$/.test(voucher.value.code.trim())) {
    errors.value.code = 'Mã khuyến mãi chỉ được chứa chữ cái in hoa và số';
    isValid = false;
  }

  if (!['percentage', 'fixed'].includes(voucher.value.type)) {
    errors.value.type = 'Loại giảm giá không hợp lệ';
    isValid = false;
  }

  if (!voucher.value.value || voucher.value.value <= 0) {
    errors.value.value = 'Giá trị giảm phải lớn hơn 0';
    isValid = false;
  } else if (voucher.value.type === 'percentage' && voucher.value.value > 100) {
    errors.value.value = 'Giá trị giảm phần trăm không được vượt quá 100';
    isValid = false;
  }

  if (voucher.value.type === 'percentage' && voucher.value.max_discount !== null && voucher.value.max_discount <= 0) {
    errors.value.max_discount = 'Giảm tối đa phải lớn hơn 0';
    isValid = false;
  }

  if (voucher.value.min_order_value !== null && voucher.value.min_order_value < 0) {
    errors.value.min_order_value = 'Giá trị đơn hàng tối thiểu không được âm';
    isValid = false;
  }

  if (voucher.value.max_uses !== null && voucher.value.max_uses < 0) {
    errors.value.max_uses = 'Số lần sử dụng tối đa không được âm';
    isValid = false;
  }
  if (voucher.value.used_count < 0) {
    errors.value.used_count = 'Số lần đã sử dụng không được âm';
    isValid = false;
  }
  if (voucher.value.max_uses !== null && voucher.value.used_count > voucher.value.max_uses) {
    errors.value.used_count = 'Số lần đã sử dụng không được vượt quá số lần tối đa';
    isValid = false;
  }

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

  if (voucher.value.customer_ids.some(id => id <= 0)) {
    errors.value.customer_ids = 'ID khách hàng phải lớn hơn 0';
    isValid = false;
  }

  return isValid;
};

// Lấy dữ liệu khuyến mãi
const fetchVoucher = async () => {
  if (!route.params.id) {
    message.value = 'Không tìm thấy ID khuyến mãi';
    messageType.value = 'error';
    return;
  }
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
      customer_ids: data.customerIds || [],
      customer_names: data.customerNames || [],
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
    customerIds: voucher.value.customer_ids.length ? voucher.value.customer_ids : null,
  };

  try {
    await axios.put(`http://localhost:8080/api/khuyenmai/${voucher.value.id}`, payload);
    message.value = 'Cập nhật khuyến mãi thành công';
    messageType.value = 'success';
    setTimeout(() => {
      window.location.href = 'http://localhost:5175/admin/vouchers';
    }, 2000);
  } catch (error) {
    console.error('Lỗi khi cập nhật khuyến mãi:', error);
    message.value = 'Lỗi khi cập nhật khuyến mãi: ' + (error.response?.data?.message || error.message);
    messageType.value = 'error';
  } finally {
    isSubmitting.value = false;
  }
};

// Hàm lấy danh sách khách hàng
const fetchCustomers = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: pageSize,
      search: customerSearchQuery.value || '',
      trangThai: true,
    };
    const res = await axios.get('http://localhost:8080/admin/api/khachhang', { params });
    customers.value = res.data.content.map(c => ({
      id: c.idKH,
      tenKH: c.tenKH,
      sdt: c.sdt,
      trangThai: c.trangThai,
    }));
    totalPages.value = res.data.totalPages;
  } catch (e) {
    console.error('Lỗi khi lấy danh sách khách hàng:', e);
    message.value = 'Lỗi khi lấy danh sách khách hàng';
    messageType.value = 'error';
  }
};

// Debounce cho tìm kiếm
const debounceFetchCustomers = debounce(() => {
  currentPage.value = 0;
  fetchCustomers();
}, 300);

// Hàm toggle khách hàng
const toggleCustomer = (id, tenKH) => {
  const index = voucher.value.customer_ids.indexOf(id);
  if (index === -1) {
    voucher.value.customer_ids.push(id);
    voucher.value.customer_names.push(tenKH);
  } else {
    voucher.value.customer_ids.splice(index, 1);
    voucher.value.customer_names.splice(index, 1);
  }
};

// Hàm xóa một khách hàng
const removeCustomer = (index) => {
  voucher.value.customer_ids.splice(index, 1);
  voucher.value.customer_names.splice(index, 1);
};

// Hàm xóa tất cả khách hàng
const clearCustomers = () => {
  voucher.value.customer_ids = [];
  voucher.value.customer_names = [];
};

// Mở modal chọn khách hàng
const openSelectCustomerModal = () => {
  customerSearchQuery.value = '';
  currentPage.value = 0;
  fetchCustomers();
  selectCustomerModalInstance?.show();
};

onMounted(() => {
  fetchVoucher();
  const selectCustomerModalElement = document.getElementById('selectCustomerModal');
  if (selectCustomerModalElement) {
    selectCustomerModalInstance = new Modal(selectCustomerModalElement);
  }
});

onUnmounted(() => {
  selectCustomerModalInstance?.dispose();
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
.modal-lg {
  max-width: 800px;
}
.badge {
  font-size: 0.9em;
  padding: 0.5em 0.75em;
}
.btn-close-white {
  filter: invert(1);
}
</style>
```