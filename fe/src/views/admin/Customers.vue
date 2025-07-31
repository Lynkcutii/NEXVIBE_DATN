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
            <th>{{ customer.idKH }}</th>
            <td>{{ customer.maKH }}</td>
            <td>{{ customer.tenKH }}</td>
            <td>{{ customer.sdt }}</td>
            <td>{{ customer.gioiTinh }}</td>
            <td>{{ customer.diaChi }}</td>
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
                :to="{ name: 'admin.customers.edit', params: { id: customer.idKH } }"
                class="btn btn-link text-primary p-1"
                title="Sửa"
              >
                <i class="fas fa-edit"></i>
              </router-link>
              <button
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

// Khởi tạo danh sách khách hàng
const customers = ref([]);

// Hàm gọi API để lấy danh sách khách hàng
const fetchCustomers = async () => {
  try {
    const response = await axios.get('http://localhost:8080/admin/api/khachhang');
    customers.value = response.data;
  } catch (error) {
    console.error('Lỗi khi lấy danh sách khách hàng:', error);
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
    // Cập nhật lại danh sách khách hàng sau khi thay đổi trạng thái
    fetchCustomers();
  } catch (error) {
    console.error('Lỗi khi thay đổi trạng thái:', error);
  }
};

// Gọi API khi component được mount
onMounted(() => {
  fetchCustomers();
});
</script>