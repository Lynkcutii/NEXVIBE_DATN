<template>
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h3 mb-0">Quản lý Sản phẩm</h1>
    <router-link :to="{ name: 'admin.products.create' }" class="btn btn-primary">
    <i class="fas fa-plus me-2"></i>Thêm sản phẩm mới
</router-link>
  </div>

  <!-- Card Bộ lọc -->
  <div class="card shadow-sm mb-4">
    <div class="card-body">
      <div class="row g-3 align-items-center">
        <div class="col-md-5">
          <el-input
            v-model="searchQuery"
            placeholder="Tìm kiếm theo tên hoặc mã sản phẩm..."
            @input="debouncedSearch"
            clearable
          />
        </div>
        <div class="col-md-3">
          <el-select v-model="filterStatus" placeholder="Lọc theo trạng thái" style="width: 100%;" @change="fetchProducts" clearable>
            <el-option label="Tất cả" value=""></el-option>
            <el-option label="Đang bán" :value="true"></el-option>
            <el-option label="Ngừng bán" :value="false"></el-option>
          </el-select>
        </div>
      </div>
    </div>
  </div>

  <!-- Bảng dữ liệu sản phẩm -->
  <div class="card shadow-sm">
    <div class="card-body">
      <div v-loading="loading">
        <el-table :data="products" style="width: 100%" class="product-table">
          <el-table-column label="Ảnh" width="100">
            <template #default="scope">
              <img :src="scope.row.anhDaiDien || 'https://via.placeholder.com/150'" class="product-thumbnail" alt="Ảnh sản phẩm">
            </template>
          </el-table-column>
          
          <el-table-column label="Tên sản phẩm">
            <template #default="scope">
              <div class="fw-bold">{{ scope.row.tenSP }}</div>
              <small class="text-muted">Mã: {{ scope.row.maSP }}</small>
            </template>
          </el-table-column>
          
          <!-- SỬA LẠI CÁCH HIỂN THỊ DANH MỤC VÀ THƯƠNG HIỆU -->
          <el-table-column label="Danh mục">
            <template #default="scope">
              {{ scope.row.danhMuc?.tenDM || 'N/A' }}
            </template>
          </el-table-column>
          
          <el-table-column label="Thương hiệu">
            <template #default="scope">
              {{ scope.row.IdThuongHieu?.tenThuongHieu || 'N/A' }}
            </template>
          </el-table-column>

          <el-table-column label="Trạng thái" width="120">
            <template #default="scope">
              <el-tag :type="scope.row.trangThai ? 'success' : 'info'">
                {{ scope.row.trangThai ? 'Đang bán' : 'Ngừng bán' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="Hành động" width="120" align="center">
            <template #default="scope">
              <!-- SỬA LẠI ID CHO ĐÚNG VỚI DB -->
              <router-link :to="{ name: 'admin.products.edit', params: { id: scope.row.idSP } }" class="btn btn-link text-primary p-1" title="Sửa">
                <i class="fas fa-edit"></i>
              </router-link>
              <button @click="confirmDelete(scope.row)" class="btn btn-link text-danger p-1" title="Xóa">
                <i class="fas fa-trash-alt"></i>
              </button>
            </template>
          </el-table-column>
        </el-table>

        <!-- Phân trang -->
        <div class="d-flex justify-content-end mt-4">
            <el-pagination
                background
                layout="prev, pager, next, total, jumper"
                :current-page="page"
                :page-size="pageSize"
                :total="totalElements"
                @current-change="handlePageChange"
            />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// PHẦN SCRIPT GIỮ NGUYÊN, KHÔNG CẦN THAY ĐỔI
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { ElMessage, ElMessageBox } from 'element-plus';
import _ from 'lodash';

const products = ref([]);
const loading = ref(true);
const searchQuery = ref('');
const filterStatus = ref('');
const page = ref(1);
const pageSize = ref(10);
const totalElements = ref(0);

const fetchProducts = async () => {
  loading.value = true;
  try {
    const params = {
      page: page.value - 1,
      size: pageSize.value,
      keyword: searchQuery.value || null,
      status: filterStatus.value !== '' ? filterStatus.value : null,
    };
    const response = await axios.get('/api/san-pham', { params }); 

    if (response.data && response.data.content) {
        products.value = response.data.content;
        totalElements.value = response.data.totalElements;
    } else {
        products.value = response.data;
        totalElements.value = response.data.length;
    }
  } catch (error) {
    console.error("Lỗi khi tải danh sách sản phẩm:", error);
    ElMessage.error('Không thể tải dữ liệu sản phẩm.');
  } finally {
    loading.value = false;
  }
};

const handlePageChange = (newPage) => {
  page.value = newPage;
  fetchProducts();
};

const debouncedSearch = _.debounce(() => {
    page.value = 1;
    fetchProducts();
}, 500);

const confirmDelete = (product) => {
    ElMessageBox.confirm(
        `Bạn có chắc chắn muốn xóa sản phẩm "<strong>${product.tenSP}</strong>"?`,
        'Xác nhận Xóa', { confirmButtonText: 'Xóa', cancelButtonText: 'Hủy', type: 'warning', dangerouslyUseHTMLString: true }
    )
    .then(async () => {
        try {
            await axios.delete(`/api/san-pham/${product.idSP}`);
            ElMessage.success('Xóa thành công!');
            fetchProducts();
        } catch (error) {
            ElMessage.error('Có lỗi xảy ra khi xóa sản phẩm.');
        }
    })
    .catch(() => { ElMessage.info('Đã hủy thao tác xóa'); });
};

onMounted(fetchProducts);
</script>

<style scoped>
.product-table { border-radius: 8px; overflow: hidden; }
.product-thumbnail { width: 60px; height: 60px; object-fit: cover; border-radius: 4px; }
</style>