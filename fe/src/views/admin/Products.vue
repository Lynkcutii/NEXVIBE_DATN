<template>
  <div class="container-fluid">
    <!-- Tiêu đề và nút thêm sản phẩm -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h3 mb-0 text-dark">Quản lý Sản phẩm</h1>
      <el-button type="primary" @click="openAddProductModal">
        <i class="fas fa-plus me-2"></i>Thêm sản phẩm mới
      </el-button>
    </div>

    <!-- Thanh tìm kiếm -->
    <div class="card shadow-sm mb-4">
      <div class="card-body">
        <el-input
          v-model="searchQuery"
          placeholder="Tìm kiếm theo tên hoặc mã sản phẩm..."
          clearable
          @input="debouncedSearch"
          class="w-50"
        />
      </div>
    </div>

    <!-- Bảng danh sách sản phẩm -->
    <div class="card shadow-sm">
      <div class="card-body">
        <div v-loading="loading">
          <el-table
            :data="products"
            style="width: 100%"
            class="product-table"
            stripe
            :default-sort="{ prop: 'ngayTao', order: 'descending' }"
          >
            <el-table-column label="Mã sản phẩm" prop="maSP" width="120" sortable />
            <el-table-column label="Ảnh" width="100">
              <template #default="scope">
                <el-image
                  v-if="scope.row.imageLink"
                  :src="scope.row.imageLink"
                  class="product-thumbnail"
                  fit="cover"
                  :preview-src-list="[scope.row.imageLink]"
                />
                <span v-else class="text-muted">Chưa có ảnh</span>
              </template>
            </el-table-column>
            <el-table-column label="Tên sản phẩm" prop="tenSP" sortable>
              <template #default="scope">
                <span class="fw-bold">{{ scope.row.tenSP }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Giá trung bình" prop="gia" width="120" sortable>
              <template #default="scope">
                {{ formatPrice(scope.row.gia) || 'N/A' }}
              </template>
            </el-table-column>
            <el-table-column label="Mô tả" prop="moTa">
              <template #default="scope">
                {{ scope.row.moTa || 'N/A' }}
              </template>
            </el-table-column>
            <el-table-column label="Ngày tạo" prop="ngayTao" width="150" sortable>
              <template #default="scope">
                {{ formatDate(scope.row.ngayTao) }}
              </template>
            </el-table-column>
            <el-table-column label="Số lượng" prop="tongSoLuongSanPham" width="120" sortable />
            <el-table-column label="Trạng thái" width="120">
              <template #default="scope">
                <el-tag :type="scope.row.trangThai ? 'success' : 'danger'">
                  {{ scope.row.trangThai ? 'Đang bán' : 'Ngừng bán' }}
                </el-tag>
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
                <el-button
                  type="text"
                  class="text-danger p-1"
                  title="Xóa"
                  @click="confirmDelete(scope.row)"
                >
                  <i class="fas fa-trash-alt"></i>
                </el-button>
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
      <el-form
        :model="newProduct"
        :rules="rules"
        ref="productForm"
        label-position="top"
        class="px-3"
      >
        <!-- Thông tin sản phẩm -->
        <el-form-item label="Tên sản phẩm" prop="tenSP">
          <el-input v-model="newProduct.tenSP" placeholder="Nhập tên sản phẩm" />
        </el-form-item>
        <el-form-item label="Mô tả sản phẩm" prop="moTa">
          <el-input
            v-model="newProduct.moTa"
            placeholder="Nhập mô tả sản phẩm"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
        <el-form-item label="Danh mục" prop="idDanhMuc">
          <el-select
            v-model="newProduct.idDanhMuc"
            placeholder="Chọn danh mục"
            :loading="loadingDanhMuc"
            style="width: 100%"
          >
            <el-option
              v-for="item in danhMucList"
              :key="item.idDM"
              :label="item.tenDM"
              :value="item.idDM"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="Thương hiệu" prop="idThuongHieu">
          <el-select
            v-model="newProduct.idThuongHieu"
            placeholder="Chọn thương hiệu"
            :loading="loadingThuongHieu"
            style="width: 100%"
          >
            <el-option
              v-for="item in thuongHieuList"
              :key="item.idThuongHieu"
              :label="item.tenThuongHieu"
              :value="item.idThuongHieu"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="Chất liệu" prop="idChatLieu">
          <el-select
            v-model="newProduct.idChatLieu"
            placeholder="Chọn chất liệu"
            :loading="loadingChatLieu"
            style="width: 100%"
          >
            <el-option
              v-for="item in chatLieuList"
              :key="item.idChatLieu"
              :label="item.tenChatLieu"
              :value="item.idChatLieu"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="Ảnh sản phẩm (chọn 1 ảnh)" prop="productImage">
          <el-upload
            :auto-upload="false"
            :on-change="handleProductImageChange"
            :on-remove="handleProductImageRemove"
            :file-list="productImage"
            :limit="1"
            accept="image/*"
            :before-upload="beforeUpload"
          >
            <el-button type="primary">Chọn ảnh</el-button>
            <template #tip>
              <div class="el-upload__tip">Chọn 1 ảnh, kích thước < 5MB.</div>
            </template>
          </el-upload>
          <div class="thumbnail-list mt-2" v-if="productImage.length > 0">
            <el-image
              :src="productImage[0].url"
              class="thumbnail-item"
              fit="cover"
              :preview-src-list="[productImage[0].url]"
            />
          </div>
          <div v-else class="no-image">Chưa chọn ảnh</div>
        </el-form-item>

        <!-- Thông tin sản phẩm chi tiết -->
        <el-divider content-position="left">Thông tin chi tiết</el-divider>
        <el-form-item label="Màu sắc" prop="sanPhamChiTiet.idMauSac">
          <el-select
            v-model="newProduct.sanPhamChiTiet.idMauSac"
            placeholder="Chọn màu sắc"
            :loading="loadingMauSac"
            style="width: 100%"
          >
            <el-option
              v-for="item in mauSacList"
              :key="item.idMauSac"
              :label="item.tenMauSac"
              :value="item.idMauSac"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="Kích thước" prop="sanPhamChiTiet.idSize">
          <el-select
            v-model="newProduct.sanPhamChiTiet.idSize"
            placeholder="Chọn kích thước"
            :loading="loadingSize"
            style="width: 100%"
          >
            <el-option
              v-for="item in sizeList"
              :key="item.idSize"
              :label="item.tenSize"
              :value="item.idSize"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="Giá chi tiết" prop="sanPhamChiTiet.gia">
          <el-input
            v-model.number="newProduct.sanPhamChiTiet.gia"
            placeholder="Nhập giá chi tiết (VNĐ)"
            type="number"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="Số lượng" prop="sanPhamChiTiet.soLuong">
          <el-input
            v-model.number="newProduct.sanPhamChiTiet.soLuong"
            placeholder="Nhập số lượng"
            type="number"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="Ảnh chi tiết (có thể chọn nhiều)" prop="detailImages">
          <el-upload
            :auto-upload="false"
            :on-change="handleDetailImagesChange"
            :on-remove="handleDetailImagesRemove"
            :file-list="detailImages"
            :multiple="true"
            :limit="5"
            accept="image/*"
            :before-upload="beforeUpload"
          >
            <el-button type="primary">Chọn ảnh</el-button>
            <template #tip>
              <div class="el-upload__tip">Tối đa 5 ảnh, kích thước < 5MB.</div>
            </template>
          </el-upload>
          <div class="thumbnail-list mt-2" v-if="detailImages.length > 0">
            <el-image
              v-for="(file, index) in detailImages"
              :key="index"
              :src="file.url"
              class="thumbnail-item"
              fit="cover"
              :preview-src-list="previewDetailImages"
            />
          </div>
          <div v-else class="no-image">Chưa chọn ảnh</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleCloseAddModal">Hủy</el-button>
          <el-button type="primary" @click="submitProduct" :loading="submitting">Thêm</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';
import { ElMessage, ElMessageBox } from 'element-plus';
import _ from 'lodash';

// Trạng thái
const products = ref([]);
const loading = ref(false);
const searchQuery = ref('');
const page = ref(1);
const pageSize = ref(10);
const totalElements = ref(0);
const showAddProductModal = ref(false);
const productForm = ref(null);
const submitting = ref(false);
const productImage = ref([]); // Ảnh sản phẩm (chỉ 1 ảnh)
const detailImages = ref([]); // Ảnh chi tiết (nhiều ảnh)

// Dữ liệu cho combobox
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

// Khởi tạo form sản phẩm
const newProduct = ref({
  tenSP: '',
  gia: null,
  moTa: '',
  trangThai: true,
  idDanhMuc: null,
  idThuongHieu: null,
  idChatLieu: null,
  imageLink: null,
  sanPhamChiTiet: {
    gia: null,
    soLuong: null,
    idMauSac: null,
    idSize: null,
  },
});

// Danh sách ảnh xem trước
const previewProductImage = computed(() => productImage.value.map((file) => file.url));
const previewDetailImages = computed(() => detailImages.value.map((file) => file.url));

// Quy tắc xác thực form
const rules = ref({
  tenSP: [{ required: true, message: 'Vui lòng nhập tên sản phẩm', trigger: 'blur' }],
  idDanhMuc: [{ required: true, message: 'Vui lòng chọn danh mục', trigger: 'change' }],
  idThuongHieu: [{ required: true, message: 'Vui lòng chọn thương hiệu', trigger: 'change' }],
  idChatLieu: [{ required: true, message: 'Vui lòng chọn chất liệu', trigger: 'change' }],
  productImage: [
    {
      validator: (rule, value, callback) => {
        if (productImage.value.length === 0) {
          callback(new Error('Vui lòng chọn một ảnh cho sản phẩm'));
        } else {
          callback();
        }
      },
      trigger: 'change',
    },
  ],
  detailImages: [
    {
      validator: (rule, value, callback) => {
        if (detailImages.value.length === 0) {
          callback(new Error('Vui lòng chọn ít nhất một ảnh chi tiết'));
        } else {
          callback();
        }
      },
      trigger: 'change',
    },
  ],
  'sanPhamChiTiet.idMauSac': [{ required: true, message: 'Vui lòng chọn màu sắc', trigger: 'change' }],
  'sanPhamChiTiet.idSize': [{ required: true, message: 'Vui lòng chọn kích thước', trigger: 'change' }],
  'sanPhamChiTiet.gia': [
    { required: true, message: 'Vui lòng nhập giá chi tiết', trigger: 'blur' },
    { type: 'number', min: 0, message: 'Giá phải lớn hơn hoặc bằng 0', trigger: 'blur' },
  ],
  'sanPhamChiTiet.soLuong': [
    { required: true, message: 'Vui lòng nhập số lượng', trigger: 'blur' },
    { type: 'number', min: 0, message: 'Số lượng phải lớn hơn hoặc bằng 0', trigger: 'blur' },
  ],
});

// Định dạng ngày
const formatDate = (date) => {
  if (!date) return 'N/A';
  return new Intl.DateTimeFormat('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  }).format(new Date(date));
};

// Định dạng giá
const formatPrice = (price) => {
  if (!price) return 'N/A';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
};

// Xử lý chọn ảnh sản phẩm
const handleProductImageChange = (file, fileList) => {
  productImage.value = fileList.map((item) => ({
    raw: item.raw,
    url: URL.createObjectURL(item.raw),
  }));
};

// Xử lý xóa ảnh sản phẩm
const handleProductImageRemove = (file, fileList) => {
  productImage.value = fileList.map((item) => ({
    raw: item.raw,
    url: URL.createObjectURL(item.raw),
  }));
};

// Xử lý chọn ảnh chi tiết
const handleDetailImagesChange = (file, fileList) => {
  detailImages.value = fileList.map((item) => ({
    raw: item.raw,
    url: URL.createObjectURL(item.raw),
  }));
};

// Xử lý xóa ảnh chi tiết
const handleDetailImagesRemove = (file, fileList) => {
  detailImages.value = fileList.map((item) => ({
    raw: item.raw,
    url: URL.createObjectURL(item.raw),
  }));
};

// Kiểm tra ảnh trước khi upload
const beforeUpload = (file) => {
  console.log('File selected:', file.name, 'Type:', file.type, 'Size:', file.size);
  const isImage = ['image/jpeg', 'image/png', 'image/gif', 'image/bmp', 'image/webp'].includes(file.type.toLowerCase());
  const isLt5M = file.size / 1024 / 1024 < 5;
  if (!isImage) {
    ElMessage.error('Vui lòng chọn file ảnh (JPEG, PNG, GIF, BMP, WEBP)!');
    return false;
  }
  if (!isLt5M) {
    ElMessage.error('Kích thước ảnh phải nhỏ hơn 5MB!');
    return false;
  }
  return true;
};

// Lấy danh sách sản phẩm
const fetchProducts = async () => {
  loading.value = true;
  try {
    const response = await axios.get('http://localhost:8080/admin/api/san-pham', {
      params: {
        page: page.value - 1,
        size: pageSize.value,
        tenSP: searchQuery.value || '',
      },
    });
    products.value = response.data.content || [];
    totalElements.value = response.data.totalElements || 0;
  } catch (error) {
    ElMessage.error('Không thể tải danh sách sản phẩm.');
    console.error('Lỗi khi lấy sản phẩm:', error);
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
  } catch (error) {
    ElMessage.error('Không thể tải danh sách danh mục.');
    console.error('Lỗi khi lấy danh mục:', error);
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
  } catch (error) {
    ElMessage.error('Không thể tải danh sách thương hiệu.');
    console.error('Lỗi khi lấy thương hiệu:', error);
  } finally {
    loadingThuongHieu.value = false;
  }
};

// Lấy danh sách chất liệu
const fetchChatLieu = async () => {
  loadingChatLieu.value = true;
  try {
    const response = await axios.get('http://localhost:8080/api/chatlieu');
    chatLieuList.value = response.data || [];
  } catch (error) {
    ElMessage.error('Không thể tải danh sách chất liệu.');
    console.error('Lỗi khi lấy chất liệu:', error);
  } finally {
    loadingChatLieu.value = false;
  }
};

// Lấy danh sách màu sắc
const fetchMauSac = async () => {
  loadingMauSac.value = true;
  try {
    const response = await axios.get('http://localhost:8080/api/mausac');
    mauSacList.value = response.data || [];
  } catch (error) {
    ElMessage.error('Không thể tải danh sách màu sắc.');
    console.error('Lỗi khi lấy màu sắc:', error);
  } finally {
    loadingMauSac.value = false;
  }
};

// Lấy danh sách kích thước
const fetchSize = async () => {
  loadingSize.value = true;
  try {
    const response = await axios.get('http://localhost:8080/api/size');
    sizeList.value = response.data || [];
  } catch (error) {
    ElMessage.error('Không thể tải danh sách kích thước.');
    console.error('Lỗi khi lấy kích thước:', error);
  } finally {
    loadingSize.value = false;
  }
};

// Mở modal thêm sản phẩm
const openAddProductModal = () => {
  showAddProductModal.value = true;
  newProduct.value = {
    tenSP: '',
    gia: null,
    moTa: '',
    trangThai: true,
    idDanhMuc: null,
    idThuongHieu: null,
    idChatLieu: null,
    imageLink: null,
    sanPhamChiTiet: {
      gia: null,
      soLuong: null,
      idMauSac: null,
      idSize: null,
    },
  };
  productImage.value = [];
  detailImages.value = [];
  Promise.all([
    fetchDanhMuc(),
    fetchThuongHieu(),
    fetchChatLieu(),
    fetchMauSac(),
    fetchSize(),
  ]);
};

// Đóng modal
const handleCloseAddModal = () => {
  showAddProductModal.value = false;
  productForm.value?.resetFields();
  productImage.value = [];
  detailImages.value = [];
  newProduct.value = {
    tenSP: '',
    gia: null,
    moTa: '',
    trangThai: true,
    idDanhMuc: null,
    idThuongHieu: null,
    idChatLieu: null,
    imageLink: null,
    sanPhamChiTiet: {
      gia: null,
      soLuong: null,
      idMauSac: null,
      idSize: null,
    },
  };
};

// Gửi yêu cầu thêm sản phẩm
const submitProduct = async () => {
  try {
    await productForm.value.validate(async (valid) => {
      if (!valid) {
        ElMessage.error('Vui lòng kiểm tra lại thông tin sản phẩm.');
        return;
      }
      if (productImage.value.length === 0) {
        ElMessage.error('Vui lòng chọn một ảnh cho sản phẩm.');
        return;
      }
      if (detailImages.value.length === 0) {
        ElMessage.error('Vui lòng chọn ít nhất một ảnh chi tiết.');
        return;
      }

      await ElMessageBox.confirm(
        'Bạn có chắc chắn muốn thêm sản phẩm này?',
        'Xác nhận',
        { confirmButtonText: 'Thêm', cancelButtonText: 'Hủy', type: 'warning' }
      );
      submitting.value = true;

      // Tạo FormData cho SanPham
      const sanPhamFormData = new FormData();
      const sanPhamDTO = {
        tenSP: newProduct.value.tenSP,
        moTa: newProduct.value.moTa,
        trangThai: newProduct.value.trangThai,
        idDanhMuc: newProduct.value.idDanhMuc,
        idThuongHieu: newProduct.value.idThuongHieu,
        idChatLieu: newProduct.value.idChatLieu,
      };
      console.log('SanPhamDTO before sending:', sanPhamDTO);
      sanPhamFormData.append('data', new Blob([JSON.stringify(sanPhamDTO)], { type: 'application/json' }));
      if (productImage.value.length > 0) {
        sanPhamFormData.append('imageFile', productImage.value[0].raw);
      }
      console.log('SanPham FormData entries:', [...sanPhamFormData.entries()]);

      // Tạo sản phẩm
      const sanPhamResponse = await axios.post('http://localhost:8080/admin/api/san-pham', sanPhamFormData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });

      // Tạo FormData cho SanPhamChiTiet
      const chiTietFormData = new FormData();
      const chiTietDTO = {
        idSP: sanPhamResponse.data.idSP,
        gia: newProduct.value.sanPhamChiTiet.gia,
        soLuong: newProduct.value.sanPhamChiTiet.soLuong,
        idMauSac: newProduct.value.sanPhamChiTiet.idMauSac,
        idSize: newProduct.value.sanPhamChiTiet.idSize,
      };
      chiTietFormData.append('data', new Blob([JSON.stringify(chiTietDTO)], { type: 'application/json' }));
      console.log('Detail Images before sending:', detailImages.value);
      detailImages.value.forEach((image) => {
        chiTietFormData.append('imageFiles', image.raw);
      });
      console.log('ChiTiet FormData entries:', [...chiTietFormData.entries()]);

      // Tạo sản phẩm chi tiết
      await axios.post('http://localhost:8080/admin/api/sanphamchitiet', chiTietFormData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });

      ElMessage.success('Thêm sản phẩm thành công!');
      handleCloseAddModal();
      fetchProducts();
    });
  } catch (error) {
    if (error === 'cancel') return;

    let errorMessage = 'Lỗi khi thêm sản phẩm.';
    if (error.response?.data?.error) {
      errorMessage = error.response.data.error;
    } else if (error.response?.data?.message) {
      errorMessage = error.response.data.message;
    } else if (error.message) {
      errorMessage = error.message;
    }

    ElMessage.error(errorMessage);
    console.error('Lỗi khi thêm sản phẩm:', error, error.response?.data);
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
const confirmDelete = async (product) => {
  try {
    await ElMessageBox.confirm(
      `Bạn có chắc chắn muốn xóa sản phẩm "<strong>${product.tenSP}</strong>"?`,
      'Xác nhận Xóa',
      { confirmButtonText: 'Xóa', cancelButtonText: 'Hủy', type: 'warning', dangerouslyUseHTMLString: true }
    );
    await axios.delete(`http://localhost:8080/admin/api/san-pham/${product.idSP}`);
    ElMessage.success('Xóa sản phẩm thành công!');
    fetchProducts();
  } catch (error) {
    ElMessage.error('Lỗi khi xóa sản phẩm.');
    console.error('Lỗi khi xóa:', error);
  }
};

// Khởi tạo
onMounted(fetchProducts);
</script>

<style scoped>
.container-fluid {
  padding: 20px;
}

.product-table .product-thumbnail {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  object-fit: cover;
}

.thumbnail-list {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.thumbnail-item {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  border: 2px solid #e8ecef;
  object-fit: cover;
  cursor: pointer;
}

.thumbnail-item:hover {
  border-color: #409eff;
}

.no-image {
  color: #999;
  font-style: italic;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
```