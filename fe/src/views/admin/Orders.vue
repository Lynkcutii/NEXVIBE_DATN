<template>
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h3 mb-0 text-gray-800">Quản lý Đơn hàng</h1>
  </div>
  <div class="card">
    <div class="card-body">
      <div class="table-responsive">
        <table class="table table-hover align-middle">
          <thead>
            <tr>
              <th>Mã ĐH</th>
              <th>Khách hàng</th>
              <th>Ngày đặt</th>
              <th>Tổng tiền</th>
              <th>Thanh toán</th>
              <th>Giao hàng</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in orders" :key="order.idHD">
              <th>#{{ order.maHD }}</th>
              <td>{{ order.customerName || 'N/A' }}</td>
              <td>{{ formatDate(order.ngayTao) }}</td>
              <td>{{ formatCurrency(order.tongTien) }}đ</td>
              <td>
                <span :class="getPaymentStatusClass(order.trangThai)">
                  {{ getPaymentStatusText(order.trangThai) }}
                </span>
              </td>
              <td>
                <span :class="getDeliveryStatusClass(order.trangThai)">
                  {{ getDeliveryStatusText(order.trangThai) }}
                </span>
              </td>
              <td>
                <router-link
                  :to="{ name: 'admin.orders.detail', params: { id: order.idHD } }"
                  class="btn btn-link text-primary p-1"
                  title="Xem chi tiết"
                >
                  <i class="fas fa-eye"></i>
                </router-link>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

// Reactive state to store orders
const orders = ref([]);

// API base URL (adjust to your backend URL)
const API_BASE_URL = 'http://localhost:8080/admin/api';

// Fetch orders from the API
const fetchOrders = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/hoadon`);
    const hoaDonList = response.data;

    // Fetch customer names for each order (if customer API exists)
    const ordersWithCustomerNames = await Promise.all(
      hoaDonList.map(async (order) => {
        let customerName = 'N/A';
        try {
          const customerResponse = await axios.get(`${API_BASE_URL}/khachhang/${order.idKhachHang}`);
          customerName = customerResponse.data.tenKH; // Adjust field name based on actual customer DTO
        } catch (error) {
          console.error(`Error fetching customer ${order.idKhachHang}:`, error);
        }
        return { ...order, customerName };
      })
    );

    orders.value = ordersWithCustomerNames;
  } catch (error) {
    console.error('Error fetching orders:', error);
  }
};

// Format date to DD/MM/YYYY
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  });
};

// Format currency (e.g., 800000 -> 800.000)
const formatCurrency = (amount) => {
  if (!amount) return '0';
  return new Intl.NumberFormat('vi-VN').format(amount);
};

// Map trangThai to payment status text
const getPaymentStatusText = (trangThai) => {
  return trangThai ? 'Đã trả' : 'Chưa trả'; // Adjust based on your logic
};

// Map trangThai to delivery status text
const getDeliveryStatusText = (trangThai) => {
  return trangThai ? 'Hoàn thành' : 'Chờ xử lý'; // Adjust based on your logic
};

// Get CSS class for payment status badge
const getPaymentStatusClass = (trangThai) => {
  return trangThai ? 'badge bg-success' : 'badge bg-warning';
};

// Get CSS class for delivery status badge
const getDeliveryStatusClass = (trangThai) => {
  return trangThai ? 'badge bg-primary' : 'badge bg-secondary';
};

// Fetch orders when component is mounted
onMounted(() => {
  fetchOrders();
});
</script>