<template>
  <div class="container-fluid">
    <!-- Header của trang -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h3 mb-0 text-gray-800">Quản lý Voucher Sản phẩm</h1>
      <router-link :to="{ name: 'admin.vouchers.create' }" class="btn btn-primary">
        <i class="fas fa-plus me-2"></i>Tạo voucher mới
      </router-link>
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
                <th>Sản phẩm áp dụng</th>
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
                <td>{{ voucher.applicableProductIds.join(', ') || 'Không có' }}</td>
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

const vouchers = ref([]);
const filterStatus = ref('all');
const message = ref('');
const messageType = ref('');
const voucherToDelete = ref(null);
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
  if (voucher.hinhThucGiam === 'percentage') {
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
  const modalElement = document.getElementById('deleteVoucherModal');
  if (modalElement) {
    deleteModalInstance = new Modal(modalElement);
  }
});

onUnmounted(() => {
  deleteModalInstance?.dispose();
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