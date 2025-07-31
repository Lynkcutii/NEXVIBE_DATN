<template>
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h3 mb-0 text-gray-800">Quản lý Tài khoản</h1>
    <router-link :to="{ name: 'admin.accounts.create' }" class="btn btn-primary"><i class="fas fa-plus"></i> Thêm tài khoản</router-link>
  </div>
  <div class="card">
    <div class="card-body">
      <table class="table table-hover align-middle">
        <thead>
          <tr>
            <th>ID</th>
            <th>Họ tên</th>
            <th>Email</th>
            <th>Vai trò</th>
            <th>Trạng thái</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          <!-- Dùng v-for để lặp qua danh sách tài khoản -->
          <tr v-for="account in accounts" :key="account.id">
            <th>{{ account.id }}</th>
            <td>{{ account.name }}</td>
            <td>{{ account.email }}</td>
            <td><span class="badge" :class="getRoleClass(account.role)">{{ account.role }}</span></td>
            <td><span class="badge" :class="account.isActive ? 'bg-success' : 'bg-danger'">{{ account.isActive ? 'Hoạt động' : 'Đã khóa' }}</span></td>
            <td>
              <!-- Nút Sửa -->
              <router-link :to="{ name: 'admin.accounts.edit', params: { id: account.id } }" class="btn btn-link text-primary p-1" title="Sửa"><i class="fas fa-edit"></i></router-link>

              <!-- Nút Khóa / Mở khóa -->
              <button 
                class="btn btn-link p-1"
                :class="account.isActive ? 'text-danger' : 'text-success'"
                :title="account.isActive ? 'Khóa tài khoản' : 'Mở khóa tài khoản'"
                @click="toggleLockStatus(account)"
                :disabled="account.id === loggedInUserId"
              >
                <i class="fas" :class="account.isActive ? 'fa-lock' : 'fa-lock-open'"></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

// Giả lập ID của người dùng đang đăng nhập. Thực tế bạn sẽ lấy từ store hoặc localStorage.
const loggedInUserId = ref(1); 

// Dữ liệu mẫu
const accounts = ref([
  { id: 1, name: 'Trần Minh Tuấn', email: 'tuan.tm@nexvibe.com', role: 'Admin', isActive: true },
  { id: 2, name: 'Lê Thị Hoa', email: 'hoa.lt@nexvibe.com', role: 'Nhân viên', isActive: true },
  { id: 3, name: 'Nguyễn Văn Khách', email: 'khach.nv@email.com', role: 'Khách hàng', isActive: false },
]);

// Hàm để đổi màu badge theo vai trò
const getRoleClass = (role) => {
  if (role === 'Admin') return 'bg-primary';
  if (role === 'Nhân viên') return 'bg-info';
  return 'bg-secondary';
};

// Hàm xử lý việc khóa/mở khóa
const toggleLockStatus = (account) => {
  // Hiển thị popup xác nhận trước khi thực hiện
  const actionText = account.isActive ? 'khóa' : 'mở khóa';
  if (confirm(`Bạn có chắc muốn ${actionText} tài khoản "${account.name}" không?`)) {
    // Gọi API để cập nhật trạng thái
    console.log(`Đang ${actionText} tài khoản ID: ${account.id}`);
    // Giả lập việc cập nhật thành công trên UI
    account.isActive = !account.isActive;
  }
};
</script>