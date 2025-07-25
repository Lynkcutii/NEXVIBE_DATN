<template>
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h3 mb-0 text-gray-800">Quản lý Khách hàng</h1>
    <!-- Nút thêm khách hàng thường không cần ở đây -->
  </div>
  <div class="card">
    <div class="card-body">
      <table class="table table-hover align-middle">
        <thead><tr><th>ID</th><th>Họ tên</th><th>Email</th><th>Trạng thái</th><th>Hành động</th></tr></thead>
        <tbody>
          <tr v-for="account in customers" :key="account.id">
            <th>{{ account.id }}</th>
            <td>{{ account.name }}</td>
            <td>{{ account.email }}</td>
            <td><span class="badge" :class="account.isActive ? 'bg-success' : 'bg-danger'">{{ account.isActive ? 'Hoạt động' : 'Đã khóa' }}</span></td>
            <td>
              <router-link :to="{ name: 'admin.customers.edit', params: { id: account.id } }" class="btn btn-link text-primary p-1" title="Sửa"><i class="fas fa-edit"></i></router-link>
              <button class="btn btn-link p-1" :class="account.isActive ? 'text-danger' : 'text-success'" :title="account.isActive ? 'Khóa' : 'Mở khóa'">
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
import { ref, computed } from 'vue';
const allAccounts = ref([
  { id: 1, name: 'Trần Minh Tuấn', email: 'tuan.tm@nexvibe.com', role: 'Admin', isActive: true },
  { id: 2, name: 'Lê Thị Hoa', email: 'hoa.lt@nexvibe.com', role: 'Nhân viên', isActive: true },
  { id: 3, name: 'Nguyễn Văn Khách', email: 'khach.nv@email.com', role: 'Khách hàng', isActive: false },
  { id: 4, name: 'Phạm Thị D', email: 'd.pt@email.com', role: 'Khách hàng', isActive: true },
]);
const customers = computed(() => allAccounts.value.filter(acc => acc.role === 'Khách hàng'));
</script>