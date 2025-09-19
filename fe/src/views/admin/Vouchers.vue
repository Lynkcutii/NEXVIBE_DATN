```vue
<template>
  <div class="container-fluid">
    <!-- Thông báo lỗi hoặc thành công -->
    <div v-if="message" :class="['alert', messageType === 'success' ? 'alert-success' : 'alert-danger']" role="alert">
      {{ message }}
      <button type="button" class="btn-close" @click="message = ''" aria-label="Close"></button>
    </div>

    <!-- Header của trang -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h3 mb-0 text-gray-800">Quản lý Khuyến mại</h1>
      <button class="btn btn-primary" @click="openCreateModal">
        <i class="fas fa-plus me-2"></i>Tạo khuyến mại mới
      </button>
    </div>

    <!-- Card Bộ lọc -->
    <div class="card mb-4">
      <div class="card-body">
        <div class="row">
          <div class="col-md-4">
            <label for="statusFilter" class="form-label">Lọc theo trạng thái</label>
            <select id="statusFilter" class="form-select" v-model="filterStatus">
              <option value="all">Tất cả</option>
              <option value="active">Đang hoạt động</option>
              <option value="upcoming">Sắp diễn ra</option>
              <option value="expired">Đã hết hạn</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <!-- Bảng danh sách Khuyến mại -->
    <div class="card">
      <div class="card-body">
        <div class="table-responsive">
          <table class="table table-hover align-middle">
            <thead>
              <tr>
                <th>Mã Code</th>
                <th>Tên voucher</th>
                <th>Giá trị</th>
                <th>Giảm tối đa</th>
                <th>Đơn hàng tối thiểu</th>
                <th>Khách hàng</th>
                <th>Đã dùng / Tổng</th>
                <th>Hiệu lực</th>
                <th>Trạng thái</th>
                <th>Hành động</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="filteredVouchers.length === 0">
                <td colspan="10" class="text-center py-4">Không có khuyến mại nào phù hợp.</td>
              </tr>
              <tr v-for="voucher in filteredVouchers" :key="voucher.id">
                <td class="fw-bold">{{ voucher.code }}</td>
                <td>{{ voucher.description }}</td>
                <td class="fw-bold">{{ formatValue(voucher) }}</td>
                <td>{{ formatCurrency(voucher.max_discount) }}</td>
                <td>{{ formatCurrency(voucher.min_order_value) }}</td>
                <td>{{ voucher.customer_name || 'Tất cả' }}</td>
                <td>{{ voucher.used_count }} / {{ voucher.max_uses || '∞' }}</td>
                <td>{{ formatDate(voucher.start_date) }} - {{ formatDate(voucher.end_date) }}</td>
                <td>
                  <span class="badge" :class="getStatusInfo(voucher).class">{{ getStatusInfo(voucher).text }}</span>
                </td>
                <td>
                  <router-link
                    :to="{ name: 'admin.vouchers.edit', params: { id: voucher.id } }"
                    class="btn btn-link text-primary p-1"
                    title="Sửa"
                  >
                    <i class="fas fa-edit"></i>
                  </router-link>
                  <button
                    @click="openDeleteModal(voucher)"
                    class="btn btn-link text-danger p-1"
                    title="Xóa"
                  >
                    <i class="fas fa-trash-alt"></i>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Modal xác nhận xóa -->
    <div class="modal fade" id="deleteVoucherModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Xác nhận Xóa</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            Bạn có chắc chắn muốn xóa mã khuyến mại "<strong>{{ voucherToDelete?.code }}</strong>" ({{ voucherToDelete?.description }})?
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
            <button type="button" class="btn btn-danger" @click="confirmDelete">Xóa</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal tạo khuyến mại mới -->
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
                        class="form-control"
                        id="voucherCode"
                        v-model="newVoucher.code"
                        readonly
                      />
                    </div>
                    <div class="col-md-6 mb-3">
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
                  </div>
                  <div class="row">
                    <div class="col-md-6 mb-3">
                      <label for="customerId" class="form-label">Khách hàng áp dụng</label>
                      <div class="input-group">
                        <input
                          type="text"
                          class="form-control"
                          :value="newVoucher.customer_name || 'Tất cả'"
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
                          v-if="newVoucher.customer_id"
                          type="button"
                          class="btn btn-outline-danger"
                          @click="clearCustomer"
                        >
                          Xóa
                        </button>
                      </div>
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
                        v-model="newVoucher.type"
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
                        v-model.number="newVoucher.value"
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
                        v-model.number="newVoucher.max_discount"
                        :disabled="newVoucher.type !== 'percentage'"
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
                        v-model.number="newVoucher.min_order_value"
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
                        v-model.number="newVoucher.max_uses"
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
                        v-model.number="newVoucher.used_count"
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
                        v-model="newVoucher.start_date"
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
                        v-model="newVoucher.end_date"
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
                        v-model="newVoucher.status"
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
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
            <button type="button" class="btn btn-primary" :disabled="isSubmitting" @click="saveVoucher">Lưu khuyến mại</button>
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
                placeholder="Tìm kiếm theo tên hoặc ID khách hàng"
                @input="fetchCustomers"
              />
            </div>
            <div class="table-responsive">
              <table class="table table-hover">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Tên khách hàng</th>
                    <th>Trạng thái</th>
                    <th>Hành động</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="customers.length === 0">
                    <td colspan="4" class="text-center">Không tìm thấy khách hàng</td>
                  </tr>
                  <tr v-for="customer in customers" :key="customer.id">
                    <td>{{ customer.id }}</td>
                    <td>{{ customer.tenKH }}</td>
                    <td>{{ customer.trangThai ? 'Hoạt động' : 'Không hoạt động' }}</td>
                    <td>
                      <button
                        class="btn btn-primary btn-sm"
                        @click="selectCustomer(customer)"
                        :disabled="!customer.trangThai"
                      >
                        Chọn
                      </button>
                    </td>
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
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
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
  customer_id: null,
  customer_name: null,
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
    customer_id: '',
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

  if (newVoucher.value.customer_id !== null && newVoucher.value.customer_id <= 0) {
    errors.value.customer_id = 'ID khách hàng phải lớn hơn 0';
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
      customer_id: v.idKH,
      customer_name: v.tenKH,
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
      trangThai: true, // Chỉ lấy khách hàng hoạt động
    };
    const res = await axios.get('http://localhost:8080/admin/api/khachhang', { params });
    customers.value = res.data.content;
    totalPages.value = res.data.totalPages;
  } catch (e) {
    console.error('Lỗi khi lấy danh sách khách hàng:', e);
    message.value = 'Lỗi khi lấy danh sách khách hàng';
    messageType.value = 'error';
  }
};

// Hàm chọn khách hàng
const selectCustomer = (customer) => {
  newVoucher.value.customer_id = customer.id;
  newVoucher.value.customer_name = customer.tenKH;
  selectCustomerModalInstance?.hide();
};

// Hàm xóa lựa chọn khách hàng
const clearCustomer = () => {
  newVoucher.value.customer_id = null;
  newVoucher.value.customer_name = null;
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
    idKH: newVoucher.value.customer_id || null,
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
    customer_id: null,
    customer_name: null,
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
    customer_id: '',
  };
};

// Logic Modal Xóa
let deleteModalInstance = null;
const voucherToDelete = ref(null);

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
</style>
```