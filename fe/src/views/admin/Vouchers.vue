```vue
<template>
  <div class="container-fluid">
    <!-- Thông báo lỗi hoặc thành công -->
    <div v-if="message" :class="['alert', messageType === 'success' ? 'alert-success' : 'alert-danger']" role="alert">
      {{ message }}
      <button type="button" class="btn-close" @click="message = ''" aria-label="Close"></button>
    </div>

    <h1 class="h3 mb-4 text-gray-800">Quản lý Khuyến mại</h1>

    <!-- Bộ lọc trạng thái -->
    <div class="card shadow mb-4">
      <div class="card-header">
        <h6 class="m-0 fw-bold text-primary">Danh sách Khuyến mại</h6>
      </div>
      <div class="card-body">
        <div class="row mb-3">
          <div class="col-md-4">
            <select v-model="filterStatus" class="form-select">
              <option value="all">Tất cả</option>
              <option value="active">Đang hoạt động</option>
              <option value="upcoming">Sắp diễn ra</option>
              <option value="expired">Đã hết hạn</option>
            </select>
          </div>
          <div class="col-md-8 text-end">
            <button class="btn btn-primary" @click="openCreateModal">Tạo khuyến mại mới</button>
          </div>
        </div>

        <!-- Bảng danh sách khuyến mại -->
        <div class="table-responsive">
          <table class="table table-bordered table-hover">
            <thead>
              <tr>
                <th>Mã KM</th>
                <th>Tên KM</th>
                <th>Giá trị</th>
                <th>Khách hàng áp dụng</th>
                <th>Ngày bắt đầu</th>
                <th>Ngày kết thúc</th>
                <th>Số lượng</th>
                <th>Đã sử dụng</th>
                <th>Trạng thái</th>
                <th>Hành động</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="filteredVouchers.length === 0">
                <td colspan="10" class="text-center">Không có dữ liệu</td>
              </tr>
              <tr v-for="voucher in filteredVouchers" :key="voucher.id">
                <td>{{ voucher.code }}</td>
                <td>{{ voucher.description || 'Không có mô tả' }}</td>
                <td>{{ formatValue(voucher) }}</td>
                <td>{{ voucher.customer_names.length ? voucher.customer_names.join(', ') : 'Tất cả' }}</td>
                <td>{{ formatDate(voucher.start_date) }}</td>
                <td>{{ formatDate(voucher.end_date) }}</td>
                <td>{{ voucher.max_uses || 'Không giới hạn' }}</td>
                <td>{{ voucher.used_count }}</td>
                <td>
                  <span :class="['badge', getStatusInfo(voucher).class]">{{ getStatusInfo(voucher).text }}</span>
                </td>
                <td>
                  <router-link :to="{ name: 'admin.vouchers.edit', params: { id: voucher.id } }" class="btn btn-sm btn-warning me-2">
                    Sửa
                  </router-link>
                  <button class="btn btn-sm btn-danger" @click="openDeleteModal(voucher)">Xóa</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Modal Tạo Khuyến mại -->
    <div class="modal fade" id="createVoucherModal" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Tạo Khuyến mại mới</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveVoucher">
              <!-- Thông tin cơ bản -->
              <div class="mb-3">
                <label for="voucherCode" class="form-label">Mã Code <span class="text-danger">*</span></label>
                <input
                  type="text"
                  class="form-control text-uppercase"
                  id="voucherCode"
                  v-model="newVoucher.code"
                  required
                  :class="{ 'is-invalid': errors.code }"
                  disabled
                />
                <div class="invalid-feedback">{{ errors.code }}</div>
              </div>
              <div class="mb-3">
                <label for="voucherDesc" class="form-label">Tên khuyến mại</label>
                <input
                  type="text"
                  class="form-control"
                  id="voucherDesc"
                  v-model="newVoucher.description"
                  :class="{ 'is-invalid': errors.description }"
                />
                <div class="invalid-feedback">{{ errors.description }}</div>
              </div>
              <div class="mb-3">
                <label for="customerIds" class="form-label">Khách hàng áp dụng</label>
                <div class="input-group">
                  <input
                    type="text"
                    class="form-control"
                    :value="newVoucher.customer_names.length ? newVoucher.customer_names.join(', ') : 'Tất cả'"
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
                    v-if="newVoucher.customer_ids.length"
                    type="button"
                    class="btn btn-outline-danger"
                    @click="clearCustomers"
                  >
                    Xóa tất cả
                  </button>
                </div>
                <div v-if="newVoucher.customer_ids.length" class="mt-2">
                  <span
                    v-for="(name, index) in newVoucher.customer_names"
                    :key="newVoucher.customer_ids[index]"
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

              <!-- Giá trị & Điều kiện -->
              <div class="mb-3">
                <label for="voucherType" class="form-label">Loại giảm giá <span class="text-danger">*</span></label>
                <select
                  class="form-select"
                  id="voucherType"
                  v-model="newVoucher.type"
                  required
                  :class="{ 'is-invalid': errors.type }"
                >
                  <option value="percentage">Theo phần trăm (%)</option>
                  <option value="fixed">Số tiền cố định (VND)</option>
                </select>
                <div class="invalid-feedback">{{ errors.type }}</div>
              </div>
              <div class="mb-3">
                <label for="voucherValue" class="form-label">Giá trị giảm <span class="text-danger">*</span></label>
                <input
                  type="number"
                  class="form-control"
                  id="voucherValue"
                  v-model.number="newVoucher.value"
                  required
                  min="0"
                  :class="{ 'is-invalid': errors.value }"
                />
                <div class="invalid-feedback">{{ errors.value }}</div>
              </div>
              <div class="mb-3">
                <label for="maxDiscount" class="form-label">Giảm tối đa (VND)</label>
                <input
                  type="number"
                  class="form-control"
                  id="maxDiscount"
                  v-model.number="newVoucher.max_discount"
                  :disabled="newVoucher.type !== 'percentage'"
                  min="0"
                  :class="{ 'is-invalid': errors.max_discount }"
                />
                <div class="invalid-feedback">{{ errors.max_discount }}</div>
              </div>
              <div class="mb-3">
                <label for="minOrderValue" class="form-label">Giá trị đơn hàng tối thiểu (VND)</label>
                <input
                  type="number"
                  class="form-control"
                  id="minOrderValue"
                  v-model.number="newVoucher.min_order_value"
                  min="0"
                  :class="{ 'is-invalid': errors.min_order_value }"
                />
                <div class="invalid-feedback">{{ errors.min_order_value }}</div>
              </div>
              <div class="mb-3">
                <label for="maxUses" class="form-label">Số lần sử dụng tối đa</label>
                <input
                  type="number"
                  class="form-control"
                  id="maxUses"
                  v-model.number="newVoucher.max_uses"
                  min="0"
                  :class="{ 'is-invalid': errors.max_uses }"
                />
                <div class="invalid-feedback">{{ errors.max_uses }}</div>
              </div>
              <div class="mb-3">
                <label for="usedCount" class="form-label">Số lần đã sử dụng</label>
                <input
                  type="number"
                  class="form-control"
                  id="usedCount"
                  v-model.number="newVoucher.used_count"
                  min="0"
                  :class="{ 'is-invalid': errors.used_count }"
                />
                <div class="invalid-feedback">{{ errors.used_count }}</div>
              </div>

              <!-- Thời gian hiệu lực & Trạng thái -->
              <div class="mb-3">
                <label for="startDate" class="form-label">Ngày bắt đầu <span class="text-danger">*</span></label>
                <input
                  type="datetime-local"
                  class="form-control"
                  id="startDate"
                  v-model="newVoucher.start_date"
                  required
                  :class="{ 'is-invalid': errors.start_date }"
                />
                <div class="invalid-feedback">{{ errors.start_date }}</div>
              </div>
              <div class="mb-3">
                <label for="endDate" class="form-label">Ngày kết thúc <span class="text-danger">*</span></label>
                <input
                  type="datetime-local"
                  class="form-control"
                  id="endDate"
                  v-model="newVoucher.end_date"
                  required
                  :class="{ 'is-invalid': errors.end_date }"
                />
                <div class="invalid-feedback">{{ errors.end_date }}</div>
              </div>
              <div class="mb-3">
                <label for="status" class="form-label">Trạng thái</label>
                <select
                  class="form-select"
                  id="status"
                  v-model="newVoucher.status"
                  :class="{ 'is-invalid': errors.status }"
                >
                  <option :value="true">Hoạt động</option>
                  <option :value="false">Không hoạt động</option>
                </select>
                <div class="invalid-feedback">{{ errors.status }}</div>
              </div>

              <div class="d-flex justify-content-end gap-2">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                <button type="submit" class="btn btn-primary" :disabled="isSubmitting">Lưu khuyến mại</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Xóa -->
    <div class="modal fade" id="deleteVoucherModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Xác nhận xóa</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            Bạn có chắc muốn xóa khuyến mại <strong>{{ voucherToDelete?.code }}</strong> không?
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
            <button type="button" class="btn btn-danger" @click="confirmDelete">Xóa</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Chọn Khách hàng -->
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
                        :checked="newVoucher.customer_ids.includes(customer.id)"
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { Modal } from 'bootstrap';
import axios from 'axios';
import { debounce } from 'lodash';

const vouchers = ref([]);
const filterStatus = ref('all');
const message = ref('');
const messageType = ref('');
const isSubmitting = ref(false);
const customers = ref([]);
const customerSearchQuery = ref('');
const currentPage = ref(0);
const totalPages = ref(1);
const pageSize = 10;

// Dữ liệu cho form tạo khuyến mại
const newVoucher = ref({
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

// Sinh mã khuyến mại tự động
const generateVoucherCode = () => {
  const now = new Date();
  const timestamp = now.toISOString().replace(/[-T:]/g, '').slice(0, 14);
  const random = Math.floor(1000 + Math.random() * 9000);
  return `KM${timestamp}${random}`;
};

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

  if (!newVoucher.value.code) {
    errors.value.code = 'Mã khuyến mãi không được để trống';
    isValid = false;
  }

  if (!['percentage', 'fixed'].includes(newVoucher.value.type)) {
    errors.value.type = 'Loại giảm giá không hợp lệ';
    isValid = false;
  }

  if (!newVoucher.value.value || newVoucher.value.value <= 0) {
    errors.value.value = 'Giá trị giảm phải lớn hơn 0';
    isValid = false;
  } else if (newVoucher.value.type === 'percentage' && newVoucher.value.value > 100) {
    errors.value.value = 'Giá trị giảm phần trăm không được vượt quá 100';
    isValid = false;
  }

  if (newVoucher.value.type === 'percentage' && newVoucher.value.max_discount !== null && newVoucher.value.max_discount <= 0) {
    errors.value.max_discount = 'Giảm tối đa phải lớn hơn 0';
    isValid = false;
  }

  if (newVoucher.value.min_order_value !== null && newVoucher.value.min_order_value < 0) {
    errors.value.min_order_value = 'Giá trị đơn hàng tối thiểu không được âm';
    isValid = false;
  }

  if (newVoucher.value.max_uses !== null && newVoucher.value.max_uses < 0) {
    errors.value.max_uses = 'Số lần sử dụng tối đa không được âm';
    isValid = false;
  }
  if (newVoucher.value.used_count < 0) {
    errors.value.used_count = 'Số lần đã sử dụng không được âm';
    isValid = false;
  }
  if (newVoucher.value.max_uses !== null && newVoucher.value.used_count > newVoucher.value.max_uses) {
    errors.value.used_count = 'Số lần đã sử dụng không được vượt quá số lần tối đa';
    isValid = false;
  }

  if (!newVoucher.value.start_date) {
    errors.value.start_date = 'Ngày bắt đầu không được để trống';
    isValid = false;
  }
  if (!newVoucher.value.end_date) {
    errors.value.end_date = 'Ngày kết thúc không được để trống';
    isValid = false;
  }
  if (newVoucher.value.start_date && newVoucher.value.end_date) {
    const start = new Date(newVoucher.value.start_date);
    const end = new Date(newVoucher.value.end_date);
    if (end <= start) {
      errors.value.end_date = 'Ngày kết thúc phải sau ngày bắt đầu';
      isValid = false;
    }
  }

  if (newVoucher.value.customer_ids.some(id => id <= 0)) {
    errors.value.customer_ids = 'ID khách hàng phải lớn hơn 0';
    isValid = false;
  }

  return isValid;
};

// Hàm lấy danh sách khuyến mại
const fetchVouchers = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/khuyenmai');
    vouchers.value = res.data.map(v => ({
      id: v.idKM,
      code: v.maKM,
      description: v.tenKM,
      type: v.hinhThucGiam,
      value: v.mucGiam,
      max_uses: v.soLuong,
      used_count: v.daSuDung,
      start_date: v.ngayBatDau,
      end_date: v.ngayKetThuc,
      status: v.trangThai,
      min_order_value: v.giaTriDonHangToiThieu,
      max_discount: v.giamToiDa,
      customer_ids: v.customerIds || [],
      customer_names: v.customerNames || [],
    }));
  } catch (e) {
    console.error('Lỗi khi lấy danh sách khuyến mại:', e);
    message.value = 'Lỗi khi lấy danh sách khuyến mại';
    messageType.value = 'error';
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

// Hàm xóa một khách hàng
const removeCustomer = (index) => {
  newVoucher.value.customer_ids.splice(index, 1);
  newVoucher.value.customer_names.splice(index, 1);
};

// Hàm xóa tất cả khách hàng
const clearCustomers = () => {
  newVoucher.value.customer_ids = [];
  newVoucher.value.customer_names = [];
};

// Hàm lưu khuyến mại
const saveVoucher = async () => {
  if (!validateForm()) {
    message.value = 'Vui lòng kiểm tra lại các trường thông tin';
    messageType.value = 'error';
    return;
  }

  isSubmitting.value = true;
  const payload = {
    idKM: newVoucher.value.id,
    maKM: newVoucher.value.code,
    tenKM: newVoucher.value.description || null,
    hinhThucGiam: newVoucher.value.type,
    mucGiam: newVoucher.value.value,
    giaTriDonHangToiThieu: newVoucher.value.min_order_value || 0,
    giamToiDa: newVoucher.value.type === 'percentage' ? newVoucher.value.max_discount : null,
    soLuong: newVoucher.value.max_uses,
    daSuDung: newVoucher.value.used_count || 0,
    ngayBatDau: new Date(newVoucher.value.start_date).toISOString(),
    ngayKetThuc: new Date(newVoucher.value.end_date).toISOString(),
    trangThai: newVoucher.value.status,
    customerIds: newVoucher.value.customer_ids.length ? newVoucher.value.customer_ids : null,
  };

  try {
    await axios.post('http://localhost:8080/api/khuyenmai', payload);
    message.value = 'Tạo khuyến mại thành công';
    messageType.value = 'success';
    createModalInstance?.hide();
    await fetchVouchers();
    resetForm();
  } catch (error) {
    console.error('Lỗi khi lưu khuyến mại:', error);
    message.value = 'Lỗi khi lưu khuyến mại: ' + (error.response?.data?.message || error.message);
    messageType.value = 'error';
  } finally {
    isSubmitting.value = false;
  }
};

// Hàm reset form
const resetForm = () => {
  newVoucher.value = {
    id: null,
    code: generateVoucherCode(),
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
  };
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
};

// Logic Modal Xóa
let deleteModalInstance = null;
const voucherToDelete = ref(null);

const debounceFetchCustomers = debounce(() => {
  currentPage.value = 0;
  fetchCustomers();
}, 300);

// Hàm toggle khách hàng
const toggleCustomer = (id, tenKH) => {
  const index = newVoucher.value.customer_ids.indexOf(id);
  if (index === -1) {
    newVoucher.value.customer_ids.push(id);
    newVoucher.value.customer_names.push(tenKH);
  } else {
    newVoucher.value.customer_ids.splice(index, 1);
    newVoucher.value.customer_names.splice(index, 1);
  }
};

const openDeleteModal = (voucher) => {
  voucherToDelete.value = voucher;
  deleteModalInstance?.show();
};

const confirmDelete = async () => {
  try {
    await axios.delete(`http://localhost:8080/api/khuyenmai/${voucherToDelete.value.id}`);
    vouchers.value = vouchers.value.filter(v => v.id !== voucherToDelete.value.id);
    message.value = 'Xóa khuyến mại thành công';
    messageType.value = 'success';
    deleteModalInstance?.hide();
  } catch (error) {
    console.error('Lỗi khi xóa khuyến mại:', error);
    message.value = 'Lỗi khi xóa khuyến mại: ' + (error.response?.data?.message || error.message);
    messageType.value = 'error';
  }
};

// Logic Modal Tạo
let createModalInstance = null;
const openCreateModal = () => {
  resetForm();
  createModalInstance?.show();
};

// Logic Modal Chọn Khách hàng
let selectCustomerModalInstance = null;
const openSelectCustomerModal = () => {
  customerSearchQuery.value = '';
  currentPage.value = 0;
  fetchCustomers();
  selectCustomerModalInstance?.show();
};

// Hàm định dạng
const getStatusInfo = (voucher) => {
  const now = new Date();
  const start = new Date(voucher.start_date);
  const end = new Date(voucher.end_date);
  end.setHours(23, 59, 59, 999);

  if (now < start) return { text: 'Sắp diễn ra', class: 'bg-info', status: 'upcoming' };
  if (now > end) return { text: 'Đã hết hạn', class: 'bg-secondary', status: 'expired' };
  return { text: 'Đang hoạt động', class: 'bg-success', status: 'active' };
};

const filteredVouchers = computed(() => {
  if (filterStatus.value === 'all') {
    return vouchers.value;
  }
  return vouchers.value.filter(v => getStatusInfo(v).status === filterStatus.value);
});

const formatValue = (voucher) => {
  if (voucher.type === 'percentage') {
    return `${voucher.value}%${voucher.max_discount ? ` (Tối đa ${formatCurrency(voucher.max_discount)})` : ''}`;
  }
  return formatCurrency(voucher.value);
};

const formatCurrency = (value) => {
  if (!value) return '0 ₫';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN');
};

// Vòng đời Component
onMounted(() => {
  fetchVouchers();
  const deleteModalElement = document.getElementById('deleteVoucherModal');
  if (deleteModalElement) {
    deleteModalInstance = new Modal(deleteModalElement);
  }
  const createModalElement = document.getElementById('createVoucherModal');
  if (createModalElement) {
    createModalInstance = new Modal(createModalElement);
  }
  const selectCustomerModalElement = document.getElementById('selectCustomerModal');
  if (selectCustomerModalElement) {
    selectCustomerModalInstance = new Modal(selectCustomerModalElement);
  }
});

onUnmounted(() => {
  deleteModalInstance?.dispose();
  createModalInstance?.dispose();
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