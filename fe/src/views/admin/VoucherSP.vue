<template>
  <div class="container-fluid">
    <!-- Header của trang -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h3 mb-0 text-gray-800">Quản lý Voucher Sản phẩm</h1>
      <button class="btn btn-primary" @click="openCreateVoucherModal">
        <i class="fas fa-plus me-2"></i>Thêm voucher mới
      </button>
    </div>

    <!-- Thông báo -->
    <div v-if="message" :class="['alert', messageType === 'success' ? 'alert-success' : 'alert-danger']" role="alert">
      {{ message }}
      <button type="button" class="btn-close" @click="message = ''" aria-label="Close"></button>
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

    <!-- Bảng danh sách Voucher -->
    <div class="card">
      <div class="card-body">
        <div class="table-responsive">
          <table class="table table-hover align-middle">
            <thead>
              <tr>
                <th>Mã Voucher</th>
                <th>Tên Voucher</th>
                <th>Giá trị</th>
                <th>Giảm tối đa</th>
                <th>Đơn giá khi giảm</th>
                <th>Giá giảm</th>
                <th>Số lượng</th>
                <th>Sản phẩm chi tiết áp dụng</th>
                <th>Hiệu lực</th>
                <th>Trạng thái</th>
                <th>Hành động</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="filteredVouchers.length === 0">
                <td colspan="11" class="text-center py-4">Không có voucher nào phù hợp.</td>
              </tr>
              <tr v-for="voucher in filteredVouchers" :key="voucher.id">
                <td class="fw-bold">{{ voucher.maVoucher }}</td>
                <td>{{ voucher.tenVoucher }}</td>
                <td class="fw-bold">{{ formatValue(voucher) }}</td>
                <td>{{ formatCurrency(voucher.giamToiDa) }}</td>
                <td>{{ formatCurrency(voucher.donGiaKhiGiam) }}</td>
                <td>{{ formatCurrency(voucher.giaGiam) }}</td>
                <td>{{ voucher.soLuong || '∞' }}</td>
                <td>{{ Array.isArray(voucher.applicableProductCodes) && voucher.applicableProductCodes.length ? voucher.applicableProductCodes.join(', ') : 'Tất cả sản phẩm chi tiết' }}</td>
                <td>{{ formatDate(voucher.ngayBatDau) }} - {{ formatDate(voucher.ngayKetThuc) }}</td>
                <td>
                  <span class="badge" :class="getStatusInfo(voucher).class">{{ getStatusInfo(voucher).text }}</span>
                </td>
                <td>
                  <router-link :to="{ name: 'admin.vouchersp.edit', params: { id: voucher.id } }" class="btn btn-link text-primary p-1" title="Sửa">
                    <i class="fas fa-edit"></i>
                  </router-link>
                  <button @click="openDeleteModal(voucher)" class="btn btn-link text-danger p-1" title="Xóa">
                    <i class="fas fa-trash-alt"></i>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Modal thêm voucher -->
    <div class="modal fade" id="createVoucherModal" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Thêm Voucher Mới</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="createVoucher">
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label for="maVoucher" class="form-label">Mã Voucher (Tự động sinh, không chỉnh sửa) <span class="text-danger">*</span></label>
                  <input
                    type="text"
                    class="form-control text-uppercase"
                    id="maVoucher"
                    v-model="newVoucher.maVoucher"
                    readonly
                    :class="{ 'is-invalid': errors.maVoucher }"
                  />
                  <div class="invalid-feedback">{{ errors.maVoucher }}</div>
                </div>
                <div class="col-md-6 mb-3">
                  <label for="tenVoucher" class="form-label">Tên Voucher (Ví dụ: Giảm giá áo thun mùa hè)</label>
                  <input
                    type="text"
                    class="form-control"
                    id="tenVoucher"
                    v-model="newVoucher.tenVoucher"
                    :class="{ 'is-invalid': errors.tenVoucher }"
                  />
                  <div class="invalid-feedback">{{ errors.tenVoucher }}</div>
                </div>
              </div>
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label for="hinhThucGiam" class="form-label">Hình thức giảm giá <span class="text-danger">*</span></label>
                  <select
                    class="form-select"
                    id="hinhThucGiam"
                    v-model="newVoucher.hinhThucGiam"
                    required
                    :class="{ 'is-invalid': errors.hinhThucGiam }"
                  >
                    <option value="PHAN_TRAM">Theo phần trăm (%)</option>
                    <option value="TIEN_MAT">Số tiền cố định (VND)</option>
                  </select>
                  <div class="invalid-feedback">{{ errors.hinhThucGiam }}</div>
                </div>
                <div class="col-md-6 mb-3">
                  <label for="mucGiam" class="form-label">Giá trị giảm (Phần trăm hoặc số tiền cố định) <span class="text-danger">*</span></label>
                  <input
                    type="number"
                    class="form-control"
                    id="mucGiam"
                    v-model.number="newVoucher.mucGiam"
                    required
                    min="0"
                    :class="{ 'is-invalid': errors.mucGiam }"
                  />
                  <div class="invalid-feedback">{{ errors.mucGiam }}</div>
                </div>
              </div>
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label for="giamToiDa" class="form-label">Giảm tối đa (Chỉ áp dụng cho giảm phần trăm, VND)</label>
                  <input
                    type="number"
                    class="form-control"
                    id="giamToiDa"
                    v-model.number="newVoucher.giamToiDa"
                    :disabled="newVoucher.hinhThucGiam !== 'PHAN_TRAM'"
                    min="0"
                    :class="{ 'is-invalid': errors.giamToiDa }"
                  />
                  <div class="invalid-feedback">{{ errors.giamToiDa }}</div>
                </div>
                <div class="col-md-6 mb-3">
                  <label for="donGiaKhiGiam" class="form-label">Đơn giá tối thiểu khi áp dụng voucher (VND)</label>
                  <input
                    type="number"
                    class="form-control"
                    id="donGiaKhiGiam"
                    v-model.number="newVoucher.donGiaKhiGiam"
                    min="0"
                    :class="{ 'is-invalid': errors.donGiaKhiGiam }"
                  />
                  <div class="invalid-feedback">{{ errors.donGiaKhiGiam }}</div>
                </div>
              </div>
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label for="giaGiam" class="form-label">Số tiền giảm cụ thể (VND, nếu có)</label>
                  <input
                    type="number"
                    class="form-control"
                    id="giaGiam"
                    v-model.number="newVoucher.giaGiam"
                    min="0"
                    :class="{ 'is-invalid': errors.giaGiam }"
                  />
                  <div class="invalid-feedback">{{ errors.giaGiam }}</div>
                </div>
                <div class="col-md-6 mb-3">
                  <label for="soLuong" class="form-label">Số lượng voucher khả dụng</label>
                  <input
                    type="number"
                    class="form-control"
                    id="soLuong"
                    v-model.number="newVoucher.soLuong"
                    min="0"
                    :class="{ 'is-invalid': errors.soLuong }"
                  />
                  <div class="invalid-feedback">{{ errors.soLuong }}</div>
                </div>
              </div>
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label for="ngayBatDau" class="form-label">Ngày bắt đầu hiệu lực <span class="text-danger">*</span></label>
                  <input
                    type="datetime-local"
                    class="form-control"
                    id="ngayBatDau"
                    v-model="newVoucher.ngayBatDau"
                    required
                    :class="{ 'is-invalid': errors.ngayBatDau }"
                  />
                  <div class="invalid-feedback">{{ errors.ngayBatDau }}</div>
                </div>
                <div class="col-md-6 mb-3">
                  <label for="ngayKetThuc" class="form-label">Ngày kết thúc hiệu lực <span class="text-danger">*</span></label>
                  <input
                    type="datetime-local"
                    class="form-control"
                    id="ngayKetThuc"
                    v-model="newVoucher.ngayKetThuc"
                    required
                    :class="{ 'is-invalid': errors.ngayKetThuc }"
                  />
                  <div class="invalid-feedback">{{ errors.ngayKetThuc }}</div>
                </div>
              </div>
              <div class="row">
                <div class="col-md-12 mb-3">
                  <label for="applicableProducts" class="form-label">Sản phẩm chi tiết áp dụng (Chọn để giới hạn, để trống để áp dụng tất cả)</label>
                  <div class="input-group">
                    <input
                      type="text"
                      class="form-control"
                      :value="newVoucher.applicableProductCodes.length ? newVoucher.applicableProductCodes.join(', ') : 'Tất cả sản phẩm chi tiết'"
                      readonly
                    />
                    <button
                      type="button"
                      class="btn btn-outline-primary"
                      @click="openSelectProductModal"
                    >
                      Chọn sản phẩm chi tiết
                    </button>
                    <button
                      v-if="newVoucher.applicableProductCodes.length"
                      type="button"
                      class="btn btn-outline-danger"
                      @click="clearProducts"
                    >
                      Xóa tất cả
                    </button>
                  </div>
                  <div v-if="newVoucher.applicableProductCodes.length" class="mt-2">
                    <span
                      v-for="(code, index) in newVoucher.applicableProductCodes"
                      :key="code"
                      class="badge bg-secondary me-1 mb-1"
                    >
                      {{ code }}
                      <button
                        type="button"
                        class="btn-close btn-close-white ms-1"
                        @click="removeProduct(index)"
                        aria-label="Xóa"
                      ></button>
                    </span>
                  </div>
                  <div v-if="errors.applicableProductCodes" class="invalid-feedback d-block">{{ errors.applicableProductCodes }}</div>
                </div>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
            <button type="button" class="btn btn-primary" @click="createVoucher">Lưu</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal chọn sản phẩm chi tiết -->
    <div class="modal fade" id="selectProductModal" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Chọn Sản phẩm chi tiết</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <div class="row mb-3">
              <div class="col-md-6">
                <input
                  type="text"
                  class="form-control"
                  v-model="productSearchQuery"
                  placeholder="Tìm kiếm theo mã hoặc tên sản phẩm"
                  @input="debounceFetchProducts"
                />
              </div>
              <div class="col-md-6">
                <label for="productStatusFilter" class="form-label">Lọc theo trạng thái</label>
                <select id="productStatusFilter" class="form-select" v-model="productStatusFilter" @change="debounceFetchProducts">
                  <option :value="null">Tất cả</option>
                  <option :value="true">Hoạt động</option>
                  <option :value="false">Không hoạt động</option>
                </select>
              </div>
            </div>
            <div class="table-responsive">
              <table class="table table-hover">
                <thead>
                  <tr>
                    <th>Chọn</th>
                    <th>Mã sản phẩm chi tiết</th>
                    <th>Tên sản phẩm</th>
                    <th>Trạng thái</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="products.length === 0">
                    <td colspan="4" class="text-center">Không tìm thấy sản phẩm chi tiết</td>
                  </tr>
                  <tr v-for="product in products" :key="product.id">
                    <td>
                      <input
                        type="checkbox"
                        :value="product.maSPCT"
                        :checked="newVoucher.applicableProductCodes.includes(product.maSPCT)"
                        @change="toggleProduct(product.maSPCT)"
                      />
                    </td>
                    <td>{{ product.maSPCT }}</td>
                    <td>{{ product.tenSP }}</td>
                    <td>{{ product.trangThai ? 'Hoạt động' : 'Không hoạt động' }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div class="d-flex justify-content-between">
              <button
                class="btn btn-secondary"
                :disabled="productPage === 0"
                @click="productPage--; fetchProducts()"
              >
                Trang trước
              </button>
              <span>Trang {{ productPage + 1 }} / {{ productTotalPages }}</span>
              <button
                class="btn btn-secondary"
                :disabled="productPage >= productTotalPages - 1"
                @click="productPage++; fetchProducts()"
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

    <!-- Modal xác nhận xóa -->
    <div class="modal fade" id="deleteVoucherModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Xác nhận Xóa</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            Bạn có chắc chắn muốn xóa voucher "<strong>{{ voucherToDelete?.maVoucher }}</strong>" ({{ voucherToDelete?.tenVoucher }})?
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
            <button type="button" class="btn btn-danger" @click="confirmDelete">Xóa</button>
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
const voucherToDelete = ref(null);
const newVoucher = ref({
  maVoucher: '',
  tenVoucher: '',
  hinhThucGiam: 'PHAN_TRAM',
  mucGiam: 0,
  giamToiDa: null,
  donGiaKhiGiam: null,
  giaGiam: null,
  soLuong: null,
  ngayBatDau: '',
  ngayKetThuc: '',
  trangThai: true,
  applicableProductCodes: [],
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
  applicableProductCodes: '',
});
const products = ref([]);
const productSearchQuery = ref('');
const productStatusFilter = ref(null); // Bộ lọc trạng thái sản phẩm
const productPage = ref(0);
const productTotalPages = ref(1);
const pageSize = 5; // Kích thước trang là 5
let createVoucherModalInstance = null;
let selectProductModalInstance = null;
let deleteModalInstance = null;

const getStatusInfo = (voucher) => {
  const now = new Date();
  const start = new Date(voucher.ngayBatDau);
  const end = new Date(voucher.ngayKetThuc);
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
  if (voucher.hinhThucGiam === 'PHAN_TRAM') {
    return `${voucher.mucGiam}%${voucher.giamToiDa ? ` (Tối đa ${formatCurrency(voucher.giamToiDa)})` : ''}`;
  }
  return formatCurrency(voucher.mucGiam);
};

const formatCurrency = (value) => {
  if (!value) return '0 ₫';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

const formatDate = (date) => {
  if (!date) return '';
  return new Date(date).toLocaleDateString('vi-VN');
};

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
    applicableProductCodes: '',
  };
  let isValid = true;

  // Định nghĩa giới hạn (dựa trên DECIMAL(10,2) cho tiền tệ và INT cho số lượng)
  const MAX_CURRENCY = 99999999.99; // Tối đa cho tiền tệ (VND)
  const MAX_QUANTITY = 999999999;   // Tối đa cho số lượng

  // Validate maVoucher
  if (!newVoucher.value.maVoucher?.trim()) {
    errors.value.maVoucher = 'Mã voucher không được để trống';
    isValid = false;
  } else if (!/^[A-Z0-9]+$/.test(newVoucher.value.maVoucher.trim())) {
    errors.value.maVoucher = 'Mã voucher chỉ được chứa chữ cái in hoa và số';
    isValid = false;
  }

  // Validate hinhThucGiam
  if (!['PHAN_TRAM', 'TIEN_MAT'].includes(newVoucher.value.hinhThucGiam)) {
    errors.value.hinhThucGiam = 'Hình thức giảm giá không hợp lệ';
    isValid = false;
  }

  // Validate mucGiam
  if (!newVoucher.value.mucGiam || newVoucher.value.mucGiam <= 0) {
    errors.value.mucGiam = 'Giá trị giảm phải lớn hơn 0';
    isValid = false;
  } else if (newVoucher.value.hinhThucGiam === 'PHAN_TRAM' && newVoucher.value.mucGiam > 100) {
    errors.value.mucGiam = 'Giá trị giảm phần trăm không được vượt quá 100';
    isValid = false;
  } else if (newVoucher.value.hinhThucGiam === 'TIEN_MAT' && newVoucher.value.mucGiam > MAX_CURRENCY) {
    errors.value.mucGiam = `Giá trị giảm cố định không được vượt quá ${formatCurrency(MAX_CURRENCY)}`;
    isValid = false;
  }

  // Validate giamToiDa (chỉ cho PHAN_TRAM)
  if (newVoucher.value.hinhThucGiam === 'PHAN_TRAM') {
    if (newVoucher.value.giamToiDa !== null && newVoucher.value.giamToiDa !== undefined) {
      if (newVoucher.value.giamToiDa <= 0) {
        errors.value.giamToiDa = 'Giảm tối đa phải lớn hơn 0';
        isValid = false;
      } else if (newVoucher.value.giamToiDa > MAX_CURRENCY) {
        errors.value.giamToiDa = `Giảm tối đa không được vượt quá ${formatCurrency(MAX_CURRENCY)}`;
        isValid = false;
      }
    }
  }

  // Validate donGiaKhiGiam
  if (newVoucher.value.donGiaKhiGiam !== null && newVoucher.value.donGiaKhiGiam !== undefined) {
    if (newVoucher.value.donGiaKhiGiam < 0) {
      errors.value.donGiaKhiGiam = 'Đơn giá tối thiểu không được âm';
      isValid = false;
    } else if (newVoucher.value.donGiaKhiGiam > MAX_CURRENCY * 10) { // Giới hạn lớn hơn một chút cho đơn giá
      errors.value.donGiaKhiGiam = `Đơn giá tối thiểu không được vượt quá ${formatCurrency(MAX_CURRENCY * 10)}`;
      isValid = false;
    }
  }

  // Validate giaGiam
  if (newVoucher.value.giaGiam !== null && newVoucher.value.giaGiam !== undefined) {
    if (newVoucher.value.giaGiam < 0) {
      errors.value.giaGiam = 'Số tiền giảm không được âm';
      isValid = false;
    } else if (newVoucher.value.giaGiam > MAX_CURRENCY) {
      errors.value.giaGiam = `Số tiền giảm không được vượt quá ${formatCurrency(MAX_CURRENCY)}`;
      isValid = false;
    }
  }

  // Validate soLuong
  if (newVoucher.value.soLuong !== null && newVoucher.value.soLuong !== undefined) {
    if (newVoucher.value.soLuong < 0) {
      errors.value.soLuong = 'Số lượng không được âm';
      isValid = false;
    } else if (newVoucher.value.soLuong > MAX_QUANTITY) {
      errors.value.soLuong = `Số lượng không được vượt quá ${MAX_QUANTITY}`;
      isValid = false;
    }
  }

  // Validate ngày
  if (!newVoucher.value.ngayBatDau) {
    errors.value.ngayBatDau = 'Ngày bắt đầu không được để trống';
    isValid = false;
  }
  if (!newVoucher.value.ngayKetThuc) {
    errors.value.ngayKetThuc = 'Ngày kết thúc không được để trống';
    isValid = false;
  }
  if (newVoucher.value.ngayBatDau && newVoucher.value.ngayKetThuc) {
    const start = new Date(newVoucher.value.ngayBatDau);
    const end = new Date(newVoucher.value.ngayKetThuc);
    if (end <= start) {
      errors.value.ngayKetThuc = 'Ngày kết thúc phải sau ngày bắt đầu';
      isValid = false;
    }
  }

  // Validate tenVoucher (tùy chọn, nhưng kiểm tra độ dài nếu cần)
  if (newVoucher.value.tenVoucher && newVoucher.value.tenVoucher.length > 255) {
    errors.value.tenVoucher = 'Tên voucher không được vượt quá 255 ký tự';
    isValid = false;
  }

  // Validate applicableProductCodes (tùy chọn)
  if (newVoucher.value.applicableProductCodes && !Array.isArray(newVoucher.value.applicableProductCodes)) {
    errors.value.applicableProductCodes = 'Danh sách sản phẩm không hợp lệ';
    isValid = false;
  }

  return isValid;
};

const fetchVouchers = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/voucher');
    if (typeof res.data === 'string') {
      message.value = res.data;
      messageType.value = 'info';
      vouchers.value = [];
    } else {
      vouchers.value = res.data;
    }
  } catch (e) {
    console.error('Lỗi khi lấy danh sách voucher:', e);
    message.value = 'Lỗi khi lấy danh sách voucher';
    messageType.value = 'error';
  }
};

const fetchVoucherCode = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/voucher/generate-code');
    newVoucher.value.maVoucher = res.data.maVoucher;
  } catch (e) {
    console.error('Lỗi khi sinh mã voucher:', e);
    message.value = 'Lỗi khi sinh mã voucher';
    messageType.value = 'error';
  }
};

const fetchProducts = async () => {
  try {
    const params = {
      page: productPage.value,
      size: pageSize,
      keyword: productSearchQuery.value || '',
      trangThai: productStatusFilter.value
    };
    console.log('Fetching products with params:', params);
    const res = await axios.get('http://localhost:8080/admin/api/sanphamchitiet/search', { params });
    console.log('API response:', res.data);
    if (res.data && res.data.content) {
      products.value = res.data.content.map(p => ({
        id: p.id,
        maSPCT: p.maSPCT,
        tenSP: p.tenSP,
        trangThai: p.trangThai,
      }));
      productTotalPages.value = res.data.totalPages || 1;
    } else {
      products.value = [];
      productTotalPages.value = 1;
      message.value = 'Không tìm thấy sản phẩm chi tiết';
      messageType.value = 'info';
    }
  } catch (e) {
    console.error('Lỗi khi lấy danh sách sản phẩm chi tiết:', e);
    console.error('Error response:', e.response?.data);
    message.value = 'Lỗi khi lấy danh sách sản phẩm chi tiết: ' + (e.response?.data?.error || e.message);
    messageType.value = 'error';
    products.value = [];
    productTotalPages.value = 1;
  }
};

const debounceFetchProducts = debounce(() => {
  productPage.value = 0;
  fetchProducts();
}, 300);

const createVoucher = async () => {
  if (!validateForm()) {
    message.value = 'Vui lòng kiểm tra lại các trường thông tin';
    messageType.value = 'error';
    return;
  }

  try {
    const payload = {
      maVoucher: newVoucher.value.maVoucher.trim(),
      tenVoucher: newVoucher.value.tenVoucher || '',
      hinhThucGiam: newVoucher.value.hinhThucGiam,
      mucGiam: newVoucher.value.mucGiam,
      giamToiDa: newVoucher.value.hinhThucGiam === 'PHAN_TRAM' ? newVoucher.value.giamToiDa : null,
      donGiaKhiGiam: newVoucher.value.donGiaKhiGiam,
      giaGiam: newVoucher.value.giaGiam,
      soLuong: newVoucher.value.soLuong,
      ngayBatDau: new Date(newVoucher.value.ngayBatDau).toISOString(),
      ngayKetThuc: new Date(newVoucher.value.ngayKetThuc).toISOString(),
      trangThai: newVoucher.value.trangThai,
      applicableProductCodes: newVoucher.value.applicableProductCodes.length ? newVoucher.value.applicableProductCodes : null,
    };
    const res = await axios.post('http://localhost:8080/api/voucher', payload);
    vouchers.value.push(res.data);
    message.value = 'Thêm voucher thành công';
    messageType.value = 'success';
    createVoucherModalInstance?.hide();
    resetNewVoucher();
  } catch (e) {
    console.error('Lỗi khi thêm voucher:', e);
    message.value = 'Lỗi khi thêm voucher: ' + (e.response?.data?.error || e.message);
    messageType.value = 'error';
  }
};

const openCreateVoucherModal = async () => {
  resetNewVoucher();
  await fetchVoucherCode();
  createVoucherModalInstance?.show();
};

const openSelectProductModal = () => {
  productSearchQuery.value = '';
  productStatusFilter.value = null;
  productPage.value = 0;
  fetchProducts();
  selectProductModalInstance?.show();
};

const toggleProduct = (maSPCT) => {
  const index = newVoucher.value.applicableProductCodes.indexOf(maSPCT);
  if (index === -1) {
    newVoucher.value.applicableProductCodes.push(maSPCT);
  } else {
    newVoucher.value.applicableProductCodes.splice(index, 1);
  }
};

const removeProduct = (index) => {
  newVoucher.value.applicableProductCodes.splice(index, 1);
};

const clearProducts = () => {
  newVoucher.value.applicableProductCodes = [];
};

const resetNewVoucher = () => {
  newVoucher.value = {
    maVoucher: '',
    tenVoucher: '',
    hinhThucGiam: 'PHAN_TRAM',
    mucGiam: 0,
    giamToiDa: null,
    donGiaKhiGiam: null,
    giaGiam: null,
    soLuong: null,
    ngayBatDau: '',
    ngayKetThuc: '',
    trangThai: true,
    applicableProductCodes: [],
  };
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
    applicableProductCodes: '',
  };
};

const openDeleteModal = (voucher) => {
  voucherToDelete.value = voucher;
  deleteModalInstance?.show();
};

const confirmDelete = async () => {
  try {
    await axios.delete(`http://localhost:8080/api/voucher/${voucherToDelete.value.id}`);
    vouchers.value = vouchers.value.filter(v => v.id !== voucherToDelete.value.id);
    message.value = 'Xóa voucher thành công';
    messageType.value = 'success';
    deleteModalInstance?.hide();
  } catch (e) {
    console.error('Lỗi khi xóa voucher:', e);
    message.value = 'Lỗi khi xóa voucher';
    messageType.value = 'error';
  }
};

onMounted(() => {
  fetchVouchers();
  const createModalElement = document.getElementById('createVoucherModal');
  const selectProductModalElement = document.getElementById('selectProductModal');
  const deleteModalElement = document.getElementById('deleteVoucherModal');
  if (createModalElement) createVoucherModalInstance = new Modal(createModalElement);
  if (selectProductModalElement) selectProductModalInstance = new Modal(selectProductModalElement);
  if (deleteModalElement) deleteModalInstance = new Modal(deleteModalElement);
});

onUnmounted(() => {
  createVoucherModalInstance?.dispose();
  selectProductModalInstance?.dispose();
  deleteModalInstance?.dispose();
});
</script>