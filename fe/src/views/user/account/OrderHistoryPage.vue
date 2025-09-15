<template>
  <div class="row g-4">
    <!-- Cột trái: Sidebar tài khoản -->
    <div class="col-lg-3">
      <div class="account-sidebar card border-0 shadow-sm">
        <div class="card-body p-3">
          <div class="d-flex align-items-center mb-3">
            <i class="fas fa-user-circle fa-3x text-muted me-3"></i>
            <div>
              <small class="text-muted">Tài khoản của</small>
              <!-- Thêm v-if để đảm bảo auth.userFullName tồn tại trước khi render -->
              <h6 v-if="auth.userFullName" class="mb-0 fw-bold">{{ auth.userFullName }}</h6>
            </div>
          </div>
          <hr>
          <div class="list-group list-group-flush">
            <router-link to="/profile" class="list-group-item list-group-item-action">
              <i class="fas fa-user-edit fa-fw me-2"></i>Thông tin cá nhân
            </router-link>
          
            <router-link to="/order.history" class="list-group-item list-group-item-action active">
              <i class="fas fa-receipt fa-fw me-2"></i>Lịch sử đơn hàng
            </router-link>
            <a href="#" class="list-group-item list-group-item-action text-danger" @click.prevent="handleLogout">
              <i class="fas fa-sign-out-alt fa-fw me-2"></i>Đăng xuất
            </a>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Cột phải: Nội dung chính - Lịch sử đơn hàng -->
    <div class="col-lg-9">
      <div class="card border-0 shadow-sm">
        <div class="card-header bg-transparent border-0 pt-4 px-4">
          <h4 class="mb-0">Lịch sử đơn hàng</h4>
        </div>
        <div class="card-body p-4">
          <!-- Hiển thị thông báo đang tải -->
          <div v-if="loading" class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
            <p class="mt-3 text-muted">Đang tải lịch sử đơn hàng...</p>
          </div>
          
          <!-- Hiển thị thông báo lỗi -->
          <div v-else-if="error" class="alert alert-danger">
            <i class="fas fa-exclamation-triangle me-2"></i>{{ error }}
          </div>

          <!-- Hiển thị bảng dữ liệu -->
          <div v-else class="table-responsive">
            <table class="table table-hover align-middle">
              <thead>
                <tr>
                  <th class="text-uppercase small fw-bold">Mã Đơn hàng</th>
                  <th class="text-uppercase small fw-bold">Ngày đặt</th>
                  <th class="text-uppercase small fw-bold">Tổng tiền</th>
                  <th class="text-uppercase small fw-bold">Trạng thái</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="order in orders" :key="order.maHD">
                  <td class="fw-bold">#{{ order.maHD }}</td>
                  <td>{{ formatDate(order.ngayTao) }}</td>
                  <td>{{ formatCurrency(order.tongTien) }}đ</td>
                  <td>
                    <span class="badge" :class="getStatusClass(order.trangThai)">
                      {{ getStatusText(order.trangThai) }}
                    </span>
                  </td>
                  <td class="text-end">
                   <router-link 
                  :to="`/order-detail/${order.idHD}`" class="btn btn-sm btn-outline-dark"> Xem chi tiết
                </router-link>
                  </td>
                </tr>
                <tr v-if="!loading && orders.length === 0">
                    <td colspan="5" class="text-center text-muted py-5">
                      <i class="fas fa-box-open fa-2x mb-3"></i>
                      <p class="mb-0">Bạn chưa có đơn hàng nào.</p>
                    </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth'; // Import Pinia store
import { useToast } from 'vue-toastification';
import { useRouter } from 'vue-router';
import axios from 'axios';

// --- KHỞI TẠO ---
const auth = useAuthStore();
const toast = useToast();
const router = useRouter();
const orders = ref([]);
const loading = ref(true);
const error = ref(null);

const API_BASE_URL = 'http://localhost:8080'; // Giữ URL gốc, không có /client/api

// --- LOGIC GỌI API ---
const loadOrderHistory = async () => {
  if (!auth.isAuthenticated) {
    error.value = 'Vui lòng đăng nhập để xem lịch sử đơn hàng!';
    loading.value = false;
    router.push('/login');
    return;
  }

  const idKH = auth.user?.idKH;
  if (!idKH) {
    error.value = 'Không tìm thấy thông tin khách hàng. Vui lòng cập nhật thông tin cá nhân.';
    loading.value = false;
    return;
  }

  try {
    loading.value = true;
    const response = await axios.get(`${API_BASE_URL}/client/api/hoadon/customer/${idKH}`, {
      withCredentials: true // Quan trọng nếu backend yêu cầu session/cookie
    });
    
    orders.value = response.data;
  } catch (err) {
    console.error('Lỗi khi tải lịch sử đơn hàng:', err);
    if (err.response) {
      error.value = err.response.data.message || 'Lỗi khi tải lịch sử đơn hàng!';
      if (err.response.status === 401 || err.response.status === 403) {
        error.value = 'Phiên đăng nhập đã hết hạn! Vui lòng đăng nhập lại.';
        auth.logout();
        router.push('/login');
      }
    } else {
      error.value = 'Lỗi mạng hoặc không thể kết nối đến máy chủ!';
    }
    toast.error(error.value);
  } finally {
    loading.value = false;
  }
};

// --- CÁC HÀM HELPER VÀ XỬ LÝ SỰ KIỆN ---

const formatDate = (dateString) => {
  if (!dateString) return 'N/A';
  return new Date(dateString).toLocaleDateString('vi-VN');
};

const formatCurrency = (amount) => {
  if (amount == null) return '0';
  return new Intl.NumberFormat('vi-VN').format(amount);
};

const getStatusText = (status) => {
  const statusMap = { 'CHO_XAC_NHAN': 'Chờ xác nhận', 'DA_XAC_NHAN': 'Đã xác nhận', 'DANG_GIAO': 'Đang giao hàng', 'DA_GIAO': 'Đã giao', 'DA_HUY': 'Đã hủy' };
  return statusMap[status] || status;
};

const getStatusClass = (status) => {
  const classMap = { 'CHO_XAC_NHAN': 'bg-secondary', 'DA_XAC_NHAN': 'bg-info text-dark', 'DANG_GIAO': 'bg-primary', 'DA_GIAO': 'bg-success', 'DA_HUY': 'bg-danger' };
  return classMap[status] || 'bg-dark';
};

const handleLogout = () => {
  auth.logout();
  toast.success("Đăng xuất thành công!");
  router.push('/login');
};

// --- VÒNG ĐỜI COMPONENT ---

onMounted(() => {
  loadOrderHistory();
});
</script>

<style scoped>
/* Style của bạn giữ nguyên */
.account-sidebar .list-group-item.active {
  background-color: #0d6efd;
  color: white;
  border-color: #0d6efd;
}
.table th {
  color: #6c757d;
  font-size: 0.85rem;
}
.badge {
  font-size: 0.8rem;
  padding: 0.4em 0.7em;
}
</style>