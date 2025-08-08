```vue
<template>
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h3 mb-0 text-gray-800">Quản lý Khách hàng</h1>
  </div>
  <div class="card">
    <div class="card-body">
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
            <td>{{ customer.diaChi || 'N/A' }}</td>
            <td>
              <span
                class="badge"
                :class="customer.trangThai ? 'bg-success' : 'bg-danger'"
              >
                {{ customer.trangThai ? 'Hoạt động' : 'Đã khóa' }}
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
                  :class="customer.trangThai ? 'fa-lock' : 'fa-lock-open'"
                ></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';

// Khởi tạo danh sách khách hàng
const customers = ref([]);

// Hàm gọi API để lấy danh sách khách hàng
const fetchCustomers = async () => {
  try {
    const response = await axios.get('http://localhost:8080/admin/api/khachhang?page=0&size=10');
    customers.value = response.data.content; // Lấy content từ phản hồi phân trang
  } catch (error) {
    console.error('Lỗi khi lấy danh sách khách hàng:', error);
    ElMessage.error('Không thể tải danh sách khách hàng.');
  }
};

// Hàm thay đổi trạng thái (khóa/mở khóa)
const toggleStatus = async (id, trangThai) => {
  try {
    const updatedCustomer = {
      ...customers.value.find((c) => c.idKH === id),
      trangThai: !trangThai,
    };
    await axios.put(`http://localhost:8080/admin/api/khachhang/${id}`, updatedCustomer);
    ElMessage.success('Cập nhật trạng thái thành công!');
    fetchCustomers();
  } catch (error) {
    console.error('Lỗi khi thay đổi trạng thái:', error);
    ElMessage.error('Lỗi khi cập nhật trạng thái khách hàng.');
  }
};

// Gọi API khi component được mount
onMounted(() => {
  fetchCustomers();
});
</script>
```