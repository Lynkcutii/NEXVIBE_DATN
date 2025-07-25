<!-- src/views/OrderDetail.vue -->
<template>
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h3 mb-0 text-gray-800">Chi tiết Đơn hàng #{{ orderId }}</h1>
    <router-link :to="{ name: 'admin.orders.list' }" class="btn btn-secondary">
      <i class="fas fa-arrow-left me-2"></i> Quay lại Danh sách
    </router-link>
  </div>
  
  <div class="row">
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
              <!-- Dữ liệu mẫu, sau này sẽ lặp qua danh sách sản phẩm của đơn hàng -->
              <tr>
                <td>Áo Thun Thể Thao</td>
                <td>250.000đ</td>
                <td>1</td>
                <td>250.000đ</td>
              </tr>
              <tr>
                <td>Áo Hoodie Nỉ Bông</td>
                <td>550.000đ</td>
                <td>1</td>
                <td>550.000đ</td>
              </tr>
            </tbody>
            <tfoot>
              <tr>
                <td colspan="3" class="text-end"><strong>Tạm tính:</strong></td>
                <td>800.000đ</td>
              </tr>
              <tr>
                <td colspan="3" class="text-end"><strong>Phí vận chuyển:</strong></td>
                <td>30.000đ</td>
              </tr>
              <tr>
                <td colspan="3" class="text-end"><strong>Tổng cộng:</strong></td>
                <td class="h5"><strong>830.000đ</strong></td>
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
          <p><strong>Tên:</strong> Nguyễn Văn A</p>
          <p><strong>Email:</strong> nguyenvana@email.com</p>
          <p><strong>Địa chỉ:</strong> 123 Đường ABC, Phường XYZ, Quận 1, TP.HCM</p>
          <p><strong>Ghi chú:</strong> Giao hàng giờ hành chính.</p>
        </div>
      </div>

      <div class="card shadow">
        <div class="card-header">
          <h6 class="m-0 font-weight-bold text-primary">Cập nhật Trạng thái</h6>
        </div>
        <div class="card-body">
            <div class="mb-3">
                <label for="paymentStatus" class="form-label">Trạng thái Thanh toán</label>
                <select id="paymentStatus" class="form-select">
                    <option value="paid" selected>Đã trả</option>
                    <option value="unpaid">Chưa trả</option>
                    <option value="failed">Thất bại</option>
                </select>
            </div>
             <div class="mb-3">
                <label for="shippingStatus" class="form-label">Trạng thái Giao hàng</label>
                <select id="shippingStatus" class="form-select">
                    <option value="pending">Chờ xử lý</option>
                    <option value="shipping" selected>Đang giao</option>
                    <option value="completed">Hoàn thành</option>
                    <option value="cancelled">Đã hủy</option>
                </select>
            </div>
            <button class="btn btn-primary w-100">Cập nhật đơn hàng</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const orderId = ref(route.params.id);

// Dữ liệu mẫu cho đơn hàng, sau này sẽ gọi API
const order = ref(null);

onMounted(() => {
  // Khi component được tạo, gọi API để lấy chi tiết đơn hàng theo orderId.value
  console.log(`Đang lấy dữ liệu cho đơn hàng ID: ${orderId.value}`);
  // Giả lập dữ liệu
  order.value = {
    // ... dữ liệu chi tiết của đơn hàng
  };
});
</script>