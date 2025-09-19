```vue
<template>
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h3 mb-0 text-gray-800">Quản lý Khách hàng</h1>
    <button class="btn btn-primary" @click="openAddCustomerModal">
      <i class="fas fa-plus me-2"></i>Thêm khách hàng
    </button>
  </div>

  <div class="card mb-4">
    <div class="card-body">
      <!-- Bộ lọc và tìm kiếm -->
      <div class="row mb-4">
        <div class="col-md-4">
          <input
            v-model="searchQuery"
            type="text"
            class="form-control"
            placeholder="Tìm theo tên hoặc số điện thoại..."
          />
        </div>
        <div class="col-md-3">
          <select v-model="statusFilter" class="form-select">
            <option value="">Tất cả trạng thái</option>
            <option value="true">Hoạt động</option>
            <option value="false">Ngừng hoạt động</option>
          </select>
        </div>
      </div>

      <table class="table table-hover align-middle">
        <thead>
          <tr>
            <th>ID</th>
            <th>Mã KH</th>
            <th>Họ tên</th>
            <th>Số điện thoại</th>
            <th>Giới tính</th>
            <th>Địa chỉ</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="customer in customers" :key="customer.idKH">
            <th>{{ customer.idKH || 'N/A' }}</th>
            <td>{{ customer.maKH || 'N/A' }}</td>
            <td>{{ customer.tenKH || 'N/A' }}</td>
            <td>{{ customer.sdt || 'N/A' }}</td>
            <td>{{ customer.gioiTinh || 'N/A' }}</td>
            <td>{{ customer.diaChiList?.[0]?.diaChiCuThe || 'N/A' }}</td>
            <td>
              <span
                class="badge"
                :class="customer.trangThai ? 'bg-success' : 'bg-danger'"
              >
                {{ customer.trangThai ? 'Hoạt động' : 'Ngừng hạt động' }}
              </span>
            </td>
            <td>
              <router-link
                v-if="customer.idKH"
                :to="{ name: 'admin.customers.edit', params: { id: customer.idKH } }"
                class="btn btn-link text-primary p-1"
                title="Sửa"
              >
                <i class="fas fa-edit"></i>
              </router-link>
              <button
                v-if="customer.idKH"
                class="btn btn-link p-1"
                :class="customer.trangThai ? 'text-danger' : 'text-success'"
                :title="customer.trangThai ? 'Khóa' : 'Mở khóa'"
                @click="toggleStatus(customer.idKH, customer.trangThai)"
              >
                <i
                  class="fas"
                  :class="customer.trangThai ?  'fa-lock-open':  'fa-lock'"
                ></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>

  <!-- Modal thêm khách hàng -->
  <el-dialog
    title="Thêm khách hàng"
    v-model="showAddCustomerModal"
    width="50%"
  >
    <form @submit.prevent="handleAddCustomer">
  <div class="row">
    <div class="col-md-6 mb-3">
      <label for="tenKH" class="form-label">Họ tên <span class="text-danger">*</span></label>
      <input
        v-model="newCustomer.tenKH"
        type="text"
        class="form-control"
        id="tenKH"
        required
      />
    </div>
    <div class="col-md-6 mb-3">
      <label for="gioiTinh" class="form-label">Giới tính</label>
      <select v-model="newCustomer.gioiTinh" class="form-select" id="gioiTinh">
        <option value="">Chọn giới tính</option>
        <option value="Nam">Nam</option>
        <option value="Nữ">Nữ</option>
        <option value="Khác">Khác</option>
      </select>
    </div>
    <div class="col-md-6 mb-3">
      <label for="ngaySinh" class="form-label">Ngày sinh</label>
      <input
        v-model="newCustomer.ngaySinh"
        type="date"
        class="form-control"
        id="ngaySinh"
      />
    </div>
    <div class="col-md-6 mb-3">
      <label for="email" class="form-label">Email</label>
      <input
        v-model="newCustomer.email"
        type="email"
        class="form-control"
        id="email"
      />
    </div>
    <div class="col-md-6 mb-3">
      <label for="sdt" class="form-label">Số điện thoại <span class="text-danger">*</span></label>
      <input
        v-model="newCustomer.sdt"
        type="text"
        class="form-control"
        id="sdt"
        required
      />
    </div>
    <!-- Bỏ trường idTK -->
    <!-- Trạng thái: Ẩn, mặc định Hoạt động -->
    <div class="col-md-6 mb-3 d-none">
      <label for="trangThai" class="form-label">Trạng thái</label>
      <select v-model="newCustomer.trangThai" class="form-select" id="trangThai">
        <option :value="true">Hoạt động</option>
      </select>
    </div>
  </div>

  <!-- Phần địa chỉ giữ nguyên, tùy chọn -->
  <!-- Phần thêm địa chỉ -->
      <div class="mb-3">
        <h5>Địa chỉ</h5>
        <div v-for="(diaChi, index) in newCustomer.diaChiList" :key="index" class="card mb-2">
          <div class="card-body">
            <div class="row">
              <div class="col-md-6 mb-3">
                <label :for="'diaChiCuThe-' + index" class="form-label">Địa chỉ cụ thể</label>
                <input
                  v-model="diaChi.diaChiCuThe"
                  type="text"
                  class="form-control"
                  :id="'diaChiCuThe-' + index"
                  required
                />
              </div>
              <div class="col-md-6 mb-3">
                <label :for="'tinhThanh-' + index" class="form-label">Tỉnh/Thành</label>
                <input
                  v-model="diaChi.tinhThanh"
                  type="text"
                  class="form-control"
                  :id="'tinhThanh-' + index"
                  required
                />
              </div>
              <div class="col-md-6 mb-3">
                <label :for="'phuongXa-' + index" class="form-label">Phường/Xã</label>
                <input
                  v-model="diaChi.phuongXa"
                  type="text"
                  class="form-control"
                  :id="'phuongXa-' + index"
                  required
                />
              </div>
              <div class="col-md-6 mb-3">
                <label :for="'soDienThoai-' + index" class="form-label">Số điện thoại</label>
                <input
                  v-model="diaChi.soDienThoai"
                  type="text"
                  class="form-control"
                  :id="'soDienThoai-' + index"
                  required
                />
              </div>
              <div class="col-md-6 mb-3">
                <label :for="'ghiChu-' + index" class="form-label">Ghi chú</label>
                <input
                  v-model="diaChi.ghiChu"
                  type="text"
                  class="form-control"
                  :id="'ghiChu-' + index"
                />
              </div>
              <div class="col-md-6 mb-3">
                <label :for="'trangThaiDiaChi-' + index" class="form-label">Trạng thái</label>
                <select v-model="diaChi.trangThai" class="form-select" :id="'trangThaiDiaChi-' + index" required>
                  <option :value="true">Hoạt động</option>
                  <option :value="false">Đã khóa</option>
                </select>
              </div>
              <div class="col-md-12">
                <button type="button" class="btn btn-danger" @click="removeAddress(index)">
                  <i class="fas fa-trash"></i> Xóa địa chỉ
                </button>
              </div>
            </div>
          </div>
        </div>
        <button type="button" class="btn btn-outline-primary mb-3" @click="addAddress">
          <i class="fas fa-plus me-2"></i>Thêm địa chỉ
        </button>
      </div>

  <button type="submit" class="btn btn-primary">Lưu khách hàng</button>
</form>
  </el-dialog>
</template>

<script setup>
import { ref, watch, onMounted, computed } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { useToast } from 'vue-toastification';
import { useAuthStore } from '@/stores/auth';

// Khởi tạo toast
const toast = useToast();

// Kiểm tra quyền admin
const authStore = useAuthStore();
const isAdmin = computed(() => authStore.isAdmin());

// Khởi tạo danh sách khách hàng và biến cho modal
const customers = ref([]);
const showAddCustomerModal = ref(false);
const newCustomer = ref({
  maKH: '', // Để trống, backend sẽ tự sinh
  tenKH: '',
  gioiTinh: '',
  ngaySinh: '',
  email: '',
  sdt: '',
  trangThai: true, // Mặc định Hoạt động
  diaChiList: [],
});

// Khởi tạo biến tìm kiếm và lọc
const searchQuery = ref('');
const statusFilter = ref('');

// Hàm mở modal thêm khách hàng
const openAddCustomerModal = () => {
  if (!isAdmin.value) {
    toast.error('Bạn không có quyền thêm khách hàng!');
    return;
  }
  newCustomer.value = {
    maKH: '', // Không sinh mã ở frontend, để backend xử lý
    tenKH: '',
    gioiTinh: '',
    ngaySinh: '',
    email: '',
    sdt: '',
    trangThai: true, // Mặc định Hoạt động
    diaChiList: [],
  };
  showAddCustomerModal.value = true;
};

// Hàm thêm địa chỉ mới
const addAddress = () => {
  newCustomer.value.diaChiList.push({
    diaChiCuThe: '',
    tinhThanh: '',
    phuongXa: '',
    soDienThoai: '',
    ghiChu: '',
    trangThai: true,
  });
};

// Hàm xóa địa chỉ
const removeAddress = (index) => {
  newCustomer.value.diaChiList.splice(index, 1);
};

// Hàm thêm khách hàng
const handleAddCustomer = async () => {
  try {
    if (!isAdmin.value) {
      toast.error('Bạn không có quyền thêm khách hàng!');
      return;
    }
    // Validation chỉ bắt buộc tenKH và sdt
    if (!newCustomer.value.tenKH || !newCustomer.value.sdt) {
      toast.error('Vui lòng điền họ tên và số điện thoại!');
      return;
    }
    // Địa chỉ tùy chọn, không kiểm tra required

    // Gửi payload với maKH rỗng để backend tự sinh
    await axios.post('/admin/api/khachhang', newCustomer.value);
    toast.success('Thêm khách hàng thành công!');
    showAddCustomerModal.value = false;
    fetchCustomers();
  } catch (error) {
    console.error('Lỗi khi thêm khách hàng:', error);
    toast.error('Lỗi khi thêm khách hàng: ' + (error.response?.data?.message || error.message));
  }
};

// Hàm gọi API để lấy danh sách khách hàng
const fetchCustomers = async () => {
  try {
    if (!isAdmin.value) {
      toast.error('Bạn không có quyền xem danh sách khách hàng!');
      customers.value = [];
      return;
    }
    const params = {
      page: 0,
      size: 10,
    };
    if (searchQuery.value) params.search = searchQuery.value;
    if (statusFilter.value !== '') params.trangThai = statusFilter.value === 'true';

    const response = await axios.get('/admin/api/khachhang', { params });
    customers.value = response.data.content; // Lấy content từ phản hồi phân trang
  } catch (error) {
    console.error('Lỗi khi lấy danh sách khách hàng:', error);
    toast.error('Không thể tải danh sách khách hàng.');
  }
};

// Hàm thay đổi trạng thái (khóa/mở khóa)
const toggleStatus = async (id, trangThai) => {
  try {
    if (!isAdmin.value) {
      toast.error('Bạn không có quyền thay đổi trạng thái khách hàng!');
      return;
    }
    const updatedCustomer = {
      ...customers.value.find((c) => c.idKH === id),
      trangThai: !trangThai,
    };
    await axios.put(`/admin/api/khachhang/${id}`, updatedCustomer);
    toast.success('Cập nhật trạng thái thành công!');
    fetchCustomers();
  } catch (error) {
    console.error('Lỗi khi thay đổi trạng thái:', error);
    toast.error('Lỗi khi cập nhật trạng thái khách hàng.');
  }
};

// Theo dõi thay đổi của searchQuery và statusFilter để gọi lại API
watch([searchQuery, statusFilter], () => {
  fetchCustomers();
});

// Gọi API khi component được mount
onMounted(() => {
  fetchCustomers();
});
</script>