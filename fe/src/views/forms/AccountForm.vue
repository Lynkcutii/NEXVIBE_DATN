<template>
  <h1 class="h3 mb-4 text-gray-800">{{ isEditing ? 'Chỉnh sửa Tài khoản' : 'Thêm Tài khoản mới' }}</h1>
  <form @submit.prevent="saveAccount">
    <div class="card shadow">
      <div class="card-body">
        <div class="mb-3">
          <label for="fullName" class="form-label">Họ và tên</label>
          <input type="text" class="form-control" id="fullName" v-model="account.name" required>
        </div>
        <div class="mb-3">
          <label for="email" class="form-label">Email</label>
          <input type="email" class="form-control" id="email" v-model="account.email" required>
        </div>
         <div class="mb-3">
          <label for="password" class="form-label">Mật khẩu</label>
          <input type="password" class="form-control" id="password" :placeholder="isEditing ? 'Để trống nếu không muốn đổi' : ''">
        </div>
        <div class="mb-3">
          <label for="role" class="form-label">Vai trò</label>
          <select class="form-select" id="role" v-model="account.role">
            <option value="admin">Admin</option>
            <option value="staff">Nhân viên</option>
            <option value="customer">Khách hàng</option>
          </select>
        </div>
      </div>
    </div>
    <div class="d-flex justify-content-end gap-2 mt-4">
      <router-link :to="{ name: 'admin.accounts.list' }" class="btn btn-secondary">Hủy</router-link>
      <button type="submit" class="btn btn-primary">Lưu tài khoản</button>
    </div>
  </form>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();

const isEditing = computed(() => !!route.params.id);
const account = ref({ name: '', email: '', role: 'customer' });

onMounted(() => {
    if (isEditing.value) {
        console.log(`Lấy dữ liệu cho tài khoản ID: ${route.params.id}`);
        // Giả lập dữ liệu
        account.value = { name: 'Trần Minh Tuấn', email: 'tuan.tm@nexvibe.com', role: 'admin' };
    }
});

const saveAccount = () => {
    console.log('Đang lưu tài khoản:', account.value);
    router.push({ name: 'admin.accounts.list' });
};
</script>