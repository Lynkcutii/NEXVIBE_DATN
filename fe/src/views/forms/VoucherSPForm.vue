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
                :disabled="isEditing"
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
                <option value="PHAN_TRAM">Theo phần trăm (%)</option>
                <option value="TIEN_MAT">Số tiền cố định (VND)</option>
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
                step="0.01"
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
                :disabled="voucher.hinhThucGiam !== 'PHAN_TRAM'"
                min="0"
                step="0.01"
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
                step="0.01"
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
                step="0.01"
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
              <div class="input-group">
                <input
                  type="text"
                  class="form-control"
                  readonly
                  :value="voucher.applicableProductCodes.join(', ')"
                  placeholder="Chọn mã SPCT"
                  :class="{ 'is-invalid': errors.applicableProductCodes }"
                />
                <button
                  class="btn btn-outline-primary"
                  type="button"
                  data-bs-toggle="modal"
                  data-bs-target="#productModal"
                >
                  Chọn sản phẩm
                </button>
                <div class="invalid-feedback">{{ errors.applicableProductCodes }}</div>
              </div>
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

      <!-- Modal chọn mã sản phẩm chi tiết -->
      <div class="modal fade" id="productModal" tabindex="-1" aria-labelledby="productModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="productModalLabel">Chọn mã sản phẩm chi tiết</h5>
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
              <div class="table-responsive" style="max-height: 400px; overflow-y: auto;">
                <table class="table table-bordered table-hover">
                  <thead>
                    <tr>
                      <th><input type="checkbox" v-model="selectAll" @change="toggleSelectAll" /></th>
                      <th>Mã SPCT</th>
                      <th>Tên sản phẩm</th>
                      <th>Trạng thái</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="product in products" :key="product.id">
                      <td>
                        <input
                          type="checkbox"
                          :value="product.maSPCT"
                          v-model="voucher.applicableProductCodes"
                        />
                      </td>
                      <td>{{ product.maSPCT }}</td>
                      <td>{{ product.tenSP }}</td>
                      <td>{{ product.trangThai ? 'Hoạt động' : 'Không hoạt động' }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <div class="d-flex justify-content-between mt-3">
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
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
              <button type="button" class="btn btn-primary" data-bs-dismiss="modal">Xác nhận</button>
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
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { debounce } from 'lodash';

const route = useRoute();
const router = useRouter();
const isEditing = !!route.params.id;
const isSubmitting = ref(false);
const message = ref('');
const messageType = ref('');
const products = ref([]); // Danh sách sản phẩm chi tiết từ API
const productSearchQuery = ref(''); // Tìm kiếm trong modal
const productStatusFilter = ref(null);
const productPage = ref(0);
const productTotalPages = ref(1);
const pageSize = 5;
const selectAll = ref(false); // Checkbox chọn tất cả

const debounceFetchProducts = debounce(() => {
  productPage.value = 0;
  fetchProducts();
}, 300);

const voucher = ref({
  id: null,
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
  trangThai: '',
  applicableProductCodes: '',
});

const toggleSelectAll = () => {
  if (selectAll.value) {
    voucher.value.applicableProductCodes = products.value.map((p) => p.maSPCT);
  } else {
    voucher.value.applicableProductCodes = [];
  }
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
    trangThai: '',
    applicableProductCodes: '',
  };
  let isValid = true;

  const MAX_CURRENCY = 9999999999999.99; // DECIMAL(15,2)
  const MAX_QUANTITY = 2147483647; // INT
  const MAX_MUC_GIAM_PERCENT = 100; // Phần trăm tối đa

  if (!voucher.value.maVoucher.trim()) {
    errors.value.maVoucher = 'Mã voucher không được để trống';
    isValid = false;
  } else if (!/^[A-Z0-9]+$/.test(voucher.value.maVoucher.trim())) {
    errors.value.maVoucher = 'Mã voucher chỉ được chứa chữ cái in hoa và số';
    isValid = false;
  }

  if (!['PHAN_TRAM', 'TIEN_MAT'].includes(voucher.value.hinhThucGiam)) {
    errors.value.hinhThucGiam = 'Loại giảm giá không hợp lệ';
    isValid = false;
  }

  if (!voucher.value.mucGiam || voucher.value.mucGiam <= 0) {
    errors.value.mucGiam = 'Giá trị giảm phải lớn hơn 0';
    isValid = false;
  } else if (voucher.value.hinhThucGiam === 'PHAN_TRAM' && voucher.value.mucGiam > MAX_MUC_GIAM_PERCENT) {
    errors.value.mucGiam = 'Giá trị giảm phần trăm không được vượt quá 100';
    isValid = false;
  } else if (voucher.value.hinhThucGiam === 'TIEN_MAT' && voucher.value.mucGiam > MAX_CURRENCY) {
    errors.value.mucGiam = `Giá trị giảm cố định không được vượt quá ${MAX_CURRENCY.toLocaleString('vi-VN')} ₫`;
    isValid = false;
  }

  if (voucher.value.hinhThucGiam === 'PHAN_TRAM' && voucher.value.giamToiDa !== null && voucher.value.giamToiDa <= 0) {
    errors.value.giamToiDa = 'Giảm tối đa phải lớn hơn 0';
    isValid = false;
  } else if (voucher.value.giamToiDa !== null && voucher.value.giamToiDa > MAX_CURRENCY) {
    errors.value.giamToiDa = `Giảm tối đa không được vượt quá ${MAX_CURRENCY.toLocaleString('vi-VN')} ₫`;
    isValid = false;
  }

  if (voucher.value.donGiaKhiGiam !== null && voucher.value.donGiaKhiGiam < 0) {
    errors.value.donGiaKhiGiam = 'Đơn giá khi giảm không được âm';
    isValid = false;
  } else if (voucher.value.donGiaKhiGiam !== null && voucher.value.donGiaKhiGiam > MAX_CURRENCY) {
    errors.value.donGiaKhiGiam = `Đơn giá khi giảm không được vượt quá ${MAX_CURRENCY.toLocaleString('vi-VN')} ₫`;
    isValid = false;
  }

  if (voucher.value.giaGiam !== null && voucher.value.giaGiam < 0) {
    errors.value.giaGiam = 'Giá giảm không được âm';
    isValid = false;
  } else if (voucher.value.giaGiam !== null && voucher.value.giaGiam > MAX_CURRENCY) {
    errors.value.giaGiam = `Giá giảm không được vượt quá ${MAX_CURRENCY.toLocaleString('vi-VN')} ₫`;
    isValid = false;
  }

  if (voucher.value.soLuong !== null && voucher.value.soLuong < 0) {
    errors.value.soLuong = 'Số lượng không được âm';
    isValid = false;
  } else if (voucher.value.soLuong !== null && voucher.value.soLuong > MAX_QUANTITY) {
    errors.value.soLuong = `Số lượng không được vượt quá ${MAX_QUANTITY}`;
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

  if (voucher.value.applicableProductCodes.length > 0) {
    const invalidCodes = voucher.value.applicableProductCodes.filter(
      (code) => !products.value.some((p) => p.maSPCT === code)
    );
    if (invalidCodes.length > 0) {
      errors.value.applicableProductCodes = `Mã SPCT không hợp lệ: ${invalidCodes.join(', ')}`;
      isValid = false;
    }
  }

  return isValid;
};

const fetchVoucher = async () => {
  if (!isEditing) return;
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
      applicableProductCodes: data.applicableProductCodes || [],
    };
  } catch (error) {
    console.error('Lỗi khi lấy dữ liệu voucher:', error);
    message.value = 'Không thể lấy dữ liệu voucher';
    messageType.value = 'error';
  }
};

const fetchProducts = async () => {
  try {
    const params = {
      page: productPage.value,
      size: pageSize,
      keyword: productSearchQuery.value || '',
      trangThai: productStatusFilter.value,
    };
    console.log('Fetching products with params:', params);
    const response = await axios.get('http://localhost:8080/admin/api/sanphamchitiet/search', { params });
    console.log('API response.data:', response.data); // Debug

    let data = response.data;
    if (!Array.isArray(data)) {
      if (response.data.content && Array.isArray(response.data.content)) {
        data = response.data.content;
      } else {
        throw new Error('Dữ liệu API không phải là mảng');
      }
    }

    products.value = data.map((item) => ({
      id: item.id,
      maSPCT: item.maSPCT,
      tenSP: item.tenSP || '',
      trangThai: item.trangThai,
    }));
    productTotalPages.value = response.data.totalPages || 1;
  } catch (error) {
    console.error('Lỗi khi lấy danh sách sản phẩm chi tiết:', error);
    message.value = 'Không thể lấy danh sách sản phẩm chi tiết: ' + error.message;
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
    mucGiam: Number(voucher.value.mucGiam.toFixed(2)),
    giamToiDa: voucher.value.hinhThucGiam === 'PHAN_TRAM' ? Number(voucher.value.giamToiDa?.toFixed(2) || null) : null,
    donGiaKhiGiam: voucher.value.donGiaKhiGiam ? Number(voucher.value.donGiaKhiGiam.toFixed(2)) : null,
    giaGiam: voucher.value.giaGiam ? Number(voucher.value.giaGiam.toFixed(2)) : null,
    soLuong: voucher.value.soLuong,
    ngayBatDau: new Date(voucher.value.ngayBatDau).toISOString().split('T')[0],
    ngayKetThuc: new Date(voucher.value.ngayKetThuc).toISOString().split('T')[0],
    trangThai: voucher.value.trangThai,
    applicableProductCodes: voucher.value.applicableProductCodes.length ? voucher.value.applicableProductCodes : [],
  };

  try {
    console.log('Payload:', payload); // Debug payload
    if (isEditing) {
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
    message.value = 'Lỗi khi lưu voucher: ' + (error.response?.data?.message || error.response?.data?.error || error.message);
    messageType.value = 'error';
  } finally {
    isSubmitting.value = false;
  }
};

onMounted(() => {
  fetchVoucher();
  fetchProducts();
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
.table-responsive {
  max-height: 400px;
  overflow-y: auto;
}
</style>