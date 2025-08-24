<template>
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h3 mb-0 text-gray-800">Chi tiết Đơn hàng #{{ order?.maHD || orderId }}</h1>
    <router-link :to="{ name: 'admin.orders.list' }" class="btn btn-secondary">
      <i class="fas fa-arrow-left me-2"></i> Quay lại Danh sách
    </router-link>
  </div>
  
  <div v-if="loading" class="text-center">
    <i class="fas fa-spinner fa-spin"></i> Đang tải...
  </div>
  <div v-else-if="error" class="alert alert-danger">
    {{ error }}
  </div>
  <div v-else class="row">
    <!-- Cột trái: Sản phẩm và Tổng tiền -->
    <div class="col-lg-8">
      <div class="card shadow mb-4">
        <div class="card-header">
          <h6 class="m-0 font-weight-bold text-primary">Các sản phẩm trong đơn</h6>
        </div>
        <div class="card-body">
          <table class="table">
            <thead>
              <tr>
                <th>Sản phẩm</th>
                <th>Giá</th>
                <th>Số lượng</th>
                <th>Thành tiền</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in orderItems" :key="item.idHDCT">
                <td>{{ item.tenSanPham }}</td>
                <td>{{ formatCurrency(item.donGia) }}đ</td>
                <td>{{ item.soLuong }}</td>
                <td>{{ formatCurrency(item.thanhTien) }}đ</td>
              </tr>
              <tr v-if="!orderItems.length">
                <td colspan="4" class="text-center">Không có sản phẩm</td>
              </tr>
            </tbody>
            <tfoot>
              <tr>
                <td colspan="3" class="text-end"><strong>Tạm tính:</strong></td>
                <td>{{ formatCurrency(order.tongTien) }}đ</td>
              </tr>
              <tr>
                <td colspan="3" class="text-end"><strong>Phí vận chuyển:</strong></td>
                <td>{{ formatCurrency(order.phiVanChuyen) }}đ</td>
              </tr>
              <tr>
                <td colspan="3" class="text-end"><strong>Tổng cộng:</strong></td>
                <td class="h5"><strong>{{ formatCurrency(Number(order.tongTien) + Number(order.phiVanChuyen)) }}đ</strong></td>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>
    </div>
    
    <!-- Cột phải: Thông tin khách hàng và Trạng thái -->
    <div class="col-lg-4">
      <div class="card shadow mb-4">
        <div class="card-header">
          <h6 class="m-0 font-weight-bold text-primary">Thông tin Khách hàng</h6>
        </div>
        <div class="card-body">
          <p><strong>Tên:</strong> {{ order.customerName || 'N/A' }}</p>
          <p><strong>Số điện thoại:</strong> {{ customer?.sdt || 'N/A' }}</p>
          <p><strong>Địa chỉ:</strong> {{ customer?.diaChi || 'N/A' }}</p>
          <p><strong>Ghi chú:</strong> {{ order.ghiChu || 'N/A' }}</p>
        </div>
      </div>

      <div class="card shadow">
        <div class="card-header">
          <h6 class="m-0 font-weight-bold text-primary">Cập nhật Trạng thái</h6>
        </div>
        <div class="card-body">
          <div class="mb-3">
            <label for="paymentStatus" class="form-label">Trạng thái Thanh toán</label>
            <select v-model="paymentStatus" id="paymentStatus" class="form-select">
              <option value="Đã trả">Đã trả</option>
              <option value="Chưa trả">Chưa trả</option>
              <option захи="Thất bại">Thất bại</option>
            </select>
          </div>
          <div class="mb-3">
            <label for="shippingStatus" class="form-label">Trạng thái Giao hàng</label>
            <select v-model="shippingStatus" id="shippingStatus" class="form-select">
              <option value="Chờ xử lý">Chờ xử lý</option>
              <option value="Đang giao">Đang giao</option>
              <option value="HOAN_THANH">Hoàn thành</option>
              <option value="Đã hủy">Đã hủy</option>
            </select>
          </div>
          <button @click="updateOrder" class="btn btn-primary w-100" :disabled="updating">
            <i v-if="updating" class="fas fa-spinner fa-spin me-2"></i>
            Cập nhật đơn hàng
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const orderId = ref(route.params.id);
const order = ref(null);
const orderItems = ref([]);
const customer = ref(null);
const loading = ref(true);
const error = ref(null);
const paymentStatus = ref('');
const shippingStatus = ref('');
const updating = ref(false);

const API_BASE_URL = 'http://localhost:8080/admin/api';

// Lấy chi tiết đơn hàng
const fetchOrder = async () => {
  try {
    loading.value = true;
    error.value = null;

    // Lấy chi tiết đơn hàng
    const orderResponse = await axios.get(`${API_BASE_URL}/hoadon/${orderId.value}`);
    order.value = orderResponse.data;

    // Lấy thông tin khách hàng
    const customerResponse = await axios.get(`${API_BASE_URL}/khachhang/${order.value.idKhachHang}`);
    customer.value = customerResponse.data;

    // Lấy danh sách sản phẩm
    const itemsResponse = await axios.get(`${API_BASE_URL}/hoadon/${orderId.value}/chitiet`);
    orderItems.value = itemsResponse.data;

    // Thiết lập giá trị trạng thái ban đầu
    paymentStatus.value = order.value.paymentStatus;
    shippingStatus.value = order.value.shippingStatus;

    loading.value = false;
  } catch (err) {
    console.error('Lỗi khi lấy chi tiết đơn hàng:', err);
    error.value = 'Không thể tải chi tiết đơn hàng';
    loading.value = false;
  }
};

// Cập nhật trạng thái đơn hàng
const updateOrder = async () => {
  try {
    updating.value = true;
    const updatedOrder = { ...order.value, paymentStatus: paymentStatus.value, shippingStatus: shippingStatus.value };
    const response = await axios.put(`${API_BASE_URL}/hoadon/${orderId.value}`, updatedOrder);
    order.value = response.data;
    alert('Cập nhật đơn hàng thành công!');
  } catch (err) {
    console.error('Lỗi khi cập nhật đơn hàng:', err);
    alert('Cập nhật đơn hàng thất bại.');
  } finally {
    updating.value = false;
  }
};

// Định dạng tiền tệ
const formatCurrency = (amount) => {
  if (!amount) return '0';
  return new Intl.NumberFormat('vi-VN').format(amount);
};

// Tải dữ liệu khi component được gắn
onMounted(() => {
  fetchOrder();
});
</script>