<template>
  <!-- Header của trang -->
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h3 mb-0 text-gray-800">Quản lý Khuyến mại</h1>
    <router-link :to="{ name: 'admin.vouchers.create' }" class="btn btn-primary">
      <i class="fas fa-plus me-2"></i>Tạo khuyến mại mới
    </router-link>
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
              <th>Mô tả</th>
              <th>Giá trị</th>
              <th>Đã dùng / Tổng</th>
              <th>Hiệu lực</th>
              <th>Trạng thái</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="filteredVouchers.length === 0">
              <td colspan="7" class="text-center py-4">Không có khuyến mại nào phù hợp.</td>
            </tr>
            <tr v-for="voucher in filteredVouchers" :key="voucher.id">
              <td class="fw-bold">{{ voucher.code }}</td>
              <td>{{ voucher.description }}</td>
              <td class="fw-bold">{{ formatValue(voucher) }}</td>
              <td>{{ voucher.used_count }} / {{ voucher.max_uses || '∞' }}</td>
              <td>{{ formatDate(voucher.start_date) }} - {{ formatDate(voucher.end_date) }}</td>
              <td>
                <span class="badge" :class="getStatusInfo(voucher).class">{{ getStatusInfo(voucher).text }}</span>
              </td>
              <td>
                <router-link :to="{ name: 'admin.vouchers.edit', params: { id: voucher.id } }" class="btn btn-link text-primary p-1" title="Sửa">
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

  <!-- Modal xác nhận xóa -->
  <div class="modal fade" id="deleteVoucherModal" tabindex="-1">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Xác nhận Xóa</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          Bạn có chắc chắn muốn xóa mã khuyến mại "<strong>{{ voucherToDelete?.code }}</strong>"?
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
          <button type="button" class="btn btn-danger" @click="confirmDelete">Xóa</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { Modal } from 'bootstrap';

// Dữ liệu mẫu (sau này sẽ lấy từ API)
const vouchers = ref([
  { id: 1, code: 'SALE50', description: 'Giảm 50% cho đơn hàng đầu tiên', type: 'percentage', value: 50, used_count: 12, max_uses: 100, start_date: '2024-07-01', end_date: '2024-07-31' },
  { id: 2, code: 'FREESHIP', description: 'Miễn phí vận chuyển', type: 'fixed', value: 30000, used_count: 250, max_uses: null, start_date: '2024-06-01', end_date: '2024-06-30' },
  { id: 3, code: 'BIGSALE', description: 'Siêu sale cuối tháng 8', type: 'percentage', value: 30, used_count: 0, max_uses: 500, start_date: '2024-08-25', end_date: '2024-08-31' },
]);

const filterStatus = ref('all');

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
  if (voucher.type === 'percentage') return `${voucher.value}%`;
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(voucher.value);
};

const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('vi-VN');
}

// Logic Modal Xóa
let deleteModalInstance = null;
const voucherToDelete = ref(null);

const openDeleteModal = (voucher) => {
  voucherToDelete.value = voucher;
  deleteModalInstance?.show();
};

const confirmDelete = () => {
  console.log(`Deleting voucher ID: ${voucherToDelete.value.id}`);
  vouchers.value = vouchers.value.filter(v => v.id !== voucherToDelete.value.id);
  deleteModalInstance?.hide();
};

// Vòng đời Component
onMounted(() => {
  const modalElement = document.getElementById('deleteVoucherModal');
  if (modalElement) {
    deleteModalInstance = new Modal(modalElement);
  }
});

onUnmounted(() => {
  deleteModalInstance?.dispose();
});
</script>