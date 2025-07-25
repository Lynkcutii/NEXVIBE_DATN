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
              <h6 class="mb-0 fw-bold">{{ auth.userFullName }}</h6>
            </div>
          </div>
          <hr>
          <div class="list-group list-group-flush">
            <router-link to="/profile" class="list-group-item list-group-item-action">
              <i class="fas fa-user-edit fa-fw me-2"></i>Thông tin cá nhân
            </router-link>
            <router-link to="/order-history" class="list-group-item list-group-item-action active">
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
          <div class="table-responsive">
            <table class="table table-hover align-middle">
              <thead>
                <tr>
                  <th>Mã Đơn hàng</th>
                  <th>Ngày đặt</th>
                  <th>Tổng tiền</th>
                  <th>Trạng thái</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <!-- Dùng v-for để lặp qua danh sách đơn hàng -->
                <tr v-for="order in orders" :key="order.id">
                  <td class="fw-bold">#{{ order.id }}</td>
                  <td>{{ new Date(order.date).toLocaleDateString('vi-VN') }}</td>
                  <td>{{ order.total.toLocaleString() }}đ</td>
                  <td><span class="badge" :class="getStatusClass(order.status)">{{ order.status }}</span></td>
                  <td>
                    <!-- Link đến trang chi tiết đơn hàng -->
                    <router-link :to="`/orders/${order.id}`" class="btn btn-sm btn-outline-dark">Xem chi tiết</router-link>
                  </td>
                </tr>
                <tr v-if="orders.length === 0">
                    <td colspan="5" class="text-center text-muted py-4">Bạn chưa có đơn hàng nào.</td>
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
import { ref } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';

const auth = useAuthStore();
const router = useRouter();
const toast = useToast();

// Dữ liệu mẫu, sau này sẽ lấy từ API
const orders = ref([
    { id: 1052, date: '2024-06-28', total: 830000, status: 'Hoàn thành' },
    { id: 1051, date: '2024-06-27', total: 320000, status: 'Đang giao' },
    { id: 1050, date: '2024-06-25', total: 450000, status: 'Đã hủy' },
]);

// Hàm để lấy class CSS cho badge trạng thái
const getStatusClass = (status) => {
    switch (status.toLowerCase()) {
        case 'hoàn thành': return 'bg-success';
        case 'đang giao': return 'bg-info';
        case 'đã hủy': return 'bg-danger';
        default: return 'bg-secondary';
    }
};

const handleLogout = () => {
    auth.logout();
    toast.info('Bạn đã đăng xuất.');
    router.push({ name: 'home' });
};
</script>

<!-- Style đã có trong file css chung, không cần thêm ở đây -->