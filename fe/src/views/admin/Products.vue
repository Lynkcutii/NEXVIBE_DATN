<template>
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h3 mb-0">Quản lý Sản phẩm</h1>
    <button class="btn btn-primary" @click="openAddProductModal">
      <i class="fas fa-plus me-2"></i>Thêm sản phẩm mới
    </button>
  </div>

  <div class="card shadow-sm mb-4">
    <div class="card-body">
      <div class="row g-3 align-items-center">
        <div class="col-md-3">
          <el-input
            v-model="searchQuery"
            placeholder="Tìm kiếm theo tên sản phẩm..."
            @input="debouncedSearch"
            clearable
          />
        </div>
      </div>
    </div>
  </div>

  <!-- Bảng dữ liệu sản phẩm -->
  <div class="card shadow-sm">
    <div class="card-body">
      <div v-loading="loading">
        <el-table :data="products" style="width: 100%" class="product-table">
          <el-table-column label="Mã sản phẩm" width="120">
            <template #default="scope">
              {{ scope.row.maSP || 'N/A' }}
            </template>
          </el-table-column>
          <el-table-column label="Tên sản phẩm">
            <template #default="scope">
              <div class="fw-bold">{{ scope.row.tenSP }}</div>
            </template>
          </el-table-column>
          <el-table-column label="Ngày tạo" width="150">
            <template #default="scope">
              {{ formatDate(scope.row.ngayTao) || 'N/A' }}
            </template>
          </el-table-column>
          <el-table-column label="Tổng số lượng" width="120">
            <template #default="scope">
              {{ scope.row.tongSoLuongSanPham || '0' }}
            </template>
          </el-table-column>
          <el-table-column label="Trạng thái" width="120">
            <template #default="scope">
              <el-tag type="success">Đang bán</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="Hành động" width="120" align="center">
            <template #default="scope">
              <router-link
                :to="{ name: 'admin.products.edit', params: { id: scope.row.idSP } }"
                class="btn btn-link text-primary p-1"
                title="Sửa"
              >
                <i class="fas fa-edit"></i>
              </router-link>
              <button
                @click="confirmDelete(scope.row)"
                class="btn btn-link text-danger p-1"
                title="Xóa"
              >
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

  <!-- Modal thêm sản phẩm -->
  <el-dialog
    title="Thêm sản phẩm mới"
    v-model="showAddProductModal"
    width="40%"
    :before-close="handleCloseAddModal"
  >
    <el-form :model="newProduct" :rules="rules" ref="productForm" label-position="top">
      <!-- Thông tin sản phẩm -->
      <el-form-item label="Tên sản phẩm" prop="tenSP">
        <el-input v-model="newProduct.tenSP" placeholder="Nhập tên sản phẩm"></el-input>
      </el-form-item>

      <!-- Thông tin sản phẩm chi tiết -->
      <el-divider content-position="left">Thông tin chi tiết</el-divider>
      <el-form-item label="Danh mục" prop="sanPhamChiTiet.idDanhMuc">
        <el-select
          v-model="newProduct.sanPhamChiTiet.idDanhMuc"
          placeholder="Chọn danh mục"
          :loading="loadingDanhMuc"
          style="width: 100%;"
        >
          <el-option
            v-for="item in danhMucList"
            :key="item.idDM"
            :label="item.tenDM"
            :value="item.idDM"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="Thương hiệu" prop="sanPhamChiTiet.idThuongHieu">
        <el-select
          v-model="newProduct.sanPhamChiTiet.idThuongHieu"
          placeholder="Chọn thương hiệu"
          :loading="loadingThuongHieu"
          style="width: 100%;"
        >
          <el-option
            v-for="item in thuongHieuList"
            :key="item.idThuongHieu"
            :label="item.tenThuongHieu"
            :value="item.idThuongHieu"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="Màu sắc" prop="sanPhamChiTiet.idMauSac">
        <el-select
          v-model="newProduct.sanPhamChiTiet.idMauSac"
          placeholder="Chọn màu sắc"
          :loading="loadingMauSac"
          style="width: 100%;"
        >
          <el-option
            v-for="item in mauSacList"
            :key="item.idMauSac"
            :label="item.tenMauSac"
            :value="item.idMauSac"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="Chất liệu" prop="sanPhamChiTiet.idChatLieu">
        <el-select
          v-model="newProduct.sanPhamChiTiet.idChatLieu"
          placeholder="Chọn chất liệu"
          :loading="loadingChatLieu"
          style="width: 100%;"
        >
          <el-option
            v-for="item in chatLieuList"
            :key="item.idChatLieu"
            :label="item.tenChatLieu"
            :value="item.idChatLieu"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="Kích thước" prop="sanPhamChiTiet.idSize">
        <el-select
          v-model="newProduct.sanPhamChiTiet.idSize"
          placeholder="Chọn kích thước"
          :loading="loadingSize"
          style="width: 100%;"
        >
          <el-option
            v-for="item in sizeList"
            :key="item.idSize"
            :label="item.tenSize"
            :value="item.idSize"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="Giá" prop="sanPhamChiTiet.gia">
        <el-input
          v-model.number="newProduct.sanPhamChiTiet.gia"
          placeholder="Nhập giá"
          type="number"
        ></el-input>
      </el-form-item>
      <el-form-item label="Số lượng" prop="sanPhamChiTiet.soLuong">
        <el-input
          v-model.number="newProduct.sanPhamChiTiet.soLuong"
          placeholder="Nhập số lượng"
          type="number"
        ></el-input>
      </el-form-item>
      <el-form-item label="Mô tả" prop="sanPhamChiTiet.moTa">
        <el-input
          v-model="newProduct.sanPhamChiTiet.moTa"
          placeholder="Nhập mô tả"
          type="textarea"
        ></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleCloseAddModal">Hủy</el-button>
        <el-button type="primary" @click="submitProduct" :loading="submitting">Thêm</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { ElMessage, ElMessageBox, ElForm } from 'element-plus';
import _ from 'lodash';

const products = ref([]);
const loading = ref(true);
const searchQuery = ref('');
const page = ref(1);
const pageSize = ref(10);
const totalElements = ref(0);
const showAddProductModal = ref(false);
const productForm = ref(null);
const submitting = ref(false);

// Dữ liệu cho các combobox
const danhMucList = ref([]);
const thuongHieuList = ref([]);
const mauSacList = ref([]);
const chatLieuList = ref([]);
const sizeList = ref([]);
const loadingDanhMuc = ref(false);
const loadingThuongHieu = ref(false);
const loadingMauSac = ref(false);
const loadingChatLieu = ref(false);
const loadingSize = ref(false);

// Dữ liệu cho sản phẩm mới
const newProduct = ref({
  tenSP: '',
  sanPhamChiTiet: {
    gia: null,
    soLuong: null,
    moTa: '',
    idDanhMuc: null,
    idThuongHieu: null,
    idMauSac: null,
    idChatLieu: null,
    idSize: null,
  },
});

// Quy tắc xác thực form
const rules = {
  tenSP: [{ required: true, message: 'Vui lòng nhập tên sản phẩm', trigger: 'blur' }],
  'sanPhamChiTiet.idDanhMuc': [{ required: true, message: 'Vui lòng chọn danh mục', trigger: 'change' }],
  'sanPhamChiTiet.idThuongHieu': [{ required: true, message: 'Vui lòng chọn thương hiệu', trigger: 'change' }],
  'sanPhamChiTiet.idMauSac': [{ required: true, message: 'Vui lòng chọn màu sắc', trigger: 'change' }],
  'sanPhamChiTiet.idChatLieu': [{ required: true, message: 'Vui lòng chọn chất liệu', trigger: 'change' }],
  'sanPhamChiTiet.idSize': [{ required: true, message: 'Vui lòng chọn kích thước', trigger: 'change' }],
  'sanPhamChiTiet.gia': [
    { required: true, message: 'Vui lòng nhập giá', trigger: 'blur' },
    { type: 'number', min: 0, message: 'Giá phải lớn hơn hoặc bằng 0', trigger: 'blur' },
  ],
  'sanPhamChiTiet.soLuong': [
    { required: true, message: 'Vui lòng nhập số lượng', trigger: 'blur' },
    { type: 'number', min: 0, message: 'Số lượng phải lớn hơn hoặc bằng 0', trigger: 'blur' },
  ],
};

// Định dạng ngày
const formatDate = (date) => {
  if (!date) return 'N/A';
  return new Intl.DateTimeFormat('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  }).format(new Date(date));
};

// Lấy danh sách sản phẩm
const fetchProducts = async () => {
  loading.value = true;
  try {
    const params = {
      page: page.value - 1,
      size: pageSize.value,
      tenSP: searchQuery.value || '',
    };

    const response = await axios.get('http://localhost:8080/admin/api/san-pham', { params });
    if (response.data && response.data.content) {
      products.value = response.data.content;
      totalElements.value = response.data.totalElements;
    } else {
      products.value = [];
      totalElements.value = 0;
      ElMessage.warning('Không tìm thấy sản phẩm.');
    }
  } catch (error) {
    console.error('Lỗi khi tải danh sách sản phẩm:', error);
    ElMessage.error('Không thể tải dữ liệu sản phẩm.');
  } finally {
    loading.value = false;
  }
};

// Lấy danh sách danh mục
const fetchDanhMuc = async () => {
  loadingDanhMuc.value = true;
  try {
    const response = await axios.get('http://localhost:8080/api/danhmuc');
    danhMucList.value = response.data || [];
    if (danhMucList.value.length === 0) {
      ElMessage.warning('Danh sách danh mục trống.');
    }
  } catch (error) {
    console.error('Lỗi khi lấy danh mục:', error);
    ElMessage.error('Không thể tải danh sách danh mục.');
  } finally {
    loadingDanhMuc.value = false;
  }
};

// Lấy danh sách thương hiệu
const fetchThuongHieu = async () => {
  loadingThuongHieu.value = true;
  try {
    const response = await axios.get('http://localhost:8080/api/thuonghieu');
    thuongHieuList.value = response.data || [];
    if (thuongHieuList.value.length === 0) {
      ElMessage.warning('Danh sách thương hiệu trống.');
    }
  } catch (error) {
    console.error('Lỗi khi lấy thương hiệu:', error);
    ElMessage.error('Không thể tải danh sách thương hiệu.');
  } finally {
    loadingThuongHieu.value = false;
  }
};

// Lấy danh sách màu sắc
const fetchMauSac = async () => {
  loadingMauSac.value = true;
  try {
    const response = await axios.get('http://localhost:8080/api/mausac');
    mauSacList.value = response.data || [];
    if (mauSacList.value.length === 0) {
      ElMessage.warning('Danh sách màu sắc trống.');
    }
  } catch (error) {
    console.error('Lỗi khi lấy màu sắc:', error);
    ElMessage.error('Không thể tải danh sách màu sắc.');
  } finally {
    loadingMauSac.value = false;
  }
};

// Lấy danh sách chất liệu
const fetchChatLieu = async () => {
  loadingChatLieu.value = true;
  try {
    const response = await axios.get('http://localhost:8080/api/chatlieu');
    chatLieuList.value = response.data || [];
    if (chatLieuList.value.length === 0) {
      ElMessage.warning('Danh sách chất liệu trống.');
    }
  } catch (error) {
    console.error('Lỗi khi lấy chất liệu:', error);
    ElMessage.error('Không thể tải danh sách chất liệu.');
  } finally {
    loadingChatLieu.value = false;
  }
};

// Lấy danh sách kích thước
const fetchSize = async () => {
  loadingSize.value = true;
  try {
    const response = await axios.get('http://localhost:8080/api/size');
    sizeList.value = response.data || [];
    if (sizeList.value.length === 0) {
      ElMessage.warning('Danh sách kích thước trống.');
    }
  } catch (error) {
    console.error('Lỗi khi lấy kích thước:', error);
    ElMessage.error('Không thể tải danh sách kích thước.');
  } finally {
    loadingSize.value = false;
  }
};

// Mở modal thêm sản phẩm
const openAddProductModal = () => {
  showAddProductModal.value = true;
  fetchDanhMuc();
  fetchThuongHieu();
  fetchMauSac();
  fetchChatLieu();
  fetchSize();
};

// Đóng modal thêm sản phẩm
const handleCloseAddModal = () => {
  showAddProductModal.value = false;
  productForm.value?.resetFields();
  newProduct.value = {
    tenSP: '',
    sanPhamChiTiet: {
      gia: null,
      soLuong: null,
      moTa: '',
      idDanhMuc: null,
      idThuongHieu: null,
      idMauSac: null,
      idChatLieu: null,
      idSize: null,
    },
  };
};

// Gửi yêu cầu thêm sản phẩm và sản phẩm chi tiết
const submitProduct = async () => {
  try {
    await ElMessageBox.confirm(
      'Bạn có chắc chắn muốn thêm sản phẩm này?',
      'Xác nhận',
      { confirmButtonText: 'Thêm', cancelButtonText: 'Hủy', type: 'warning' }
    );
    await productForm.value.validate(async (valid) => {
      if (valid) {
        submitting.value = true;

        // Tạo sản phẩm (SanPham)
        const sanPhamResponse = await axios.post('http://localhost:8080/admin/api/san-pham', {
          tenSP: newProduct.value.tenSP,
        });

        const idSP = sanPhamResponse.data.idSP;

        // Tạo sản phẩm chi tiết (SanPhamChiTiet)
        await axios.post('http://localhost:8080/admin/api/sanphamchitiet', {
          idSP: idSP,
          gia: newProduct.value.sanPhamChiTiet.gia,
          soLuong: newProduct.value.sanPhamChiTiet.soLuong,
          moTa: newProduct.value.sanPhamChiTiet.moTa,
          idDanhMuc: newProduct.value.sanPhamChiTiet.idDanhMuc,
          idThuongHieu: newProduct.value.sanPhamChiTiet.idThuongHieu,
          idMauSac: newProduct.value.sanPhamChiTiet.idMauSac,
          idChatLieu: newProduct.value.sanPhamChiTiet.idChatLieu,
          idSize: newProduct.value.sanPhamChiTiet.idSize,
        });

        ElMessage.success('Thêm sản phẩm và chi tiết thành công!');
        showAddProductModal.value = false;
        fetchProducts();
        handleCloseAddModal();
      }
    });
  } catch (error) {
    if (error === 'cancel') return;
    console.error('Lỗi khi thêm sản phẩm:', error);
    ElMessage.error(error.response?.data?.message || 'Lỗi khi thêm sản phẩm.');
  } finally {
    submitting.value = false;
  }
};

// Xử lý phân trang
const handlePageChange = (newPage) => {
  page.value = newPage;
  fetchProducts();
};

// Tìm kiếm debounce
const debouncedSearch = _.debounce(() => {
  page.value = 1;
  fetchProducts();
}, 500);

// Xác nhận xóa sản phẩm
const confirmDelete = (product) => {
  ElMessageBox.confirm(
    `Bạn có chắc chắn muốn xóa sản phẩm "<strong>${product.tenSP}</strong>"?`,
    'Xác nhận Xóa',
    { confirmButtonText: 'Xóa', cancelButtonText: 'Hủy', type: 'warning', dangerouslyUseHTMLString: true }
  )
    .then(async () => {
      try {
        await axios.delete(`http://localhost:8080/admin/api/san-pham/${product.idSP}`);
        ElMessage.success('Xóa thành công!');
        fetchProducts();
      } catch (error) {
        ElMessage.error('Có lỗi xảy ra khi xóa sản phẩm.');
      }
    })
    .catch(() => {
      ElMessage.info('Đã hủy thao tác xóa');
    });
};

// Khởi tạo
onMounted(() => {
  fetchProducts();
});
</script>

<style scoped>
.product-table .product-thumbnail {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}
</style>