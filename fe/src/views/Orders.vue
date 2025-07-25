<template>
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h3 mb-0">Quản lý Đơn hàng</h1>
  </div>
  <div class="card shadow-sm">
    <div class="card-body">
      <div v-loading="loading">
        <el-table :data="orders" style="width: 100%">
          <el-table-column label="Mã HĐ" prop="maHD" width="120" />
          <el-table-column label="Khách hàng" prop="khachHang.tenKH" />
          <el-table-column label="Nhân viên" prop="nhanVien.tenNV" />
          <el-table-column label="Ngày tạo">
              <template #default="scope">{{ new Date(scope.row.ngayTao).toLocaleDateString('vi-VN') }}</template>
          </el-table-column>
          <el-table-column label="Tổng tiền">
              <template #default="scope">{{ scope.row.tongTien.toLocaleString() }}đ</template>
          </el-table-column>
           <el-table-column label="Trạng thái">
            <template #default="scope">
              <el-tag :type="scope.row.trangThai ? 'success' : 'info'">{{ scope.row.trangThai ? 'Hoàn thành' : 'Chưa HT' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="Hành động" width="100" align="center">
            <template #default="scope">
              <router-link :to="{ name: 'admin.orders.detail', params: { id: scope.row.idHD } }" class="btn btn-link text-primary p-1" title="Xem chi tiết"><i class="fas fa-eye"></i></router-link>
            </template>
          </el-table-column>
        </el-table>
        <div class="d-flex justify-content-end mt-4">
            <el-pagination background layout="prev, pager, next" :total="totalElements" :page-size="pageSize" @current-change="handlePageChange" />
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
const orders = ref([]);
const loading = ref(true);
const page = ref(1);
const pageSize = ref(10);
const totalElements = ref(0);
const fetchOrders = async () => {
  loading.value = true;
  try {
    const response = await axios.get('/api/hoa-don', { params: { page: page.value - 1, size: pageSize.value } });
    orders.value = response.data.content;
    totalElements.value = response.data.totalElements;
  } catch (error) { console.error(error); } 
  finally { loading.value = false; }
};
const handlePageChange = (newPage) => { page.value = newPage; fetchOrders(); };
onMounted(fetchOrders);
</script>