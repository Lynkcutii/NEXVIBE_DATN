<template>
  <div class="container-fluid">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h3 mb-0">Sửa sản phẩm: {{ sanPham.tenSP || 'Loading...' }}</h1>
      <router-link to="/admin/products" class="btn btn-secondary">
        <i class="fas fa-arrow-left me-2"></i>Quay lại
      </router-link>
    </div>

    <div class="card shadow-sm mb-4">
      <div class="card-body">
        <h4 class="card-title">Thông tin sản phẩm</h4>
        <el-form :model="sanPham" :rules="rules" ref="sanPhamForm" label-position="top">
          <el-form-item label="Tên sản phẩm" prop="tenSP">
            <el-input v-model="sanPham.tenSP" placeholder="Nhập tên sản phẩm"></el-input>
          </el-form-item>
          <el-form-item label="Danh mục" prop="idDanhMuc">
            <el-select v-model="sanPham.idDanhMuc" placeholder="Chọn danh mục" :loading="loadingDanhMuc" style="width: 100%">
              <el-option v-for="item in danhMucList" :key="item.idDM" :label="item.tenDM" :value="item.idDM" />
            </el-select>
          </el-form-item>
          <el-form-item label="Thương hiệu" prop="idThuongHieu">
            <el-select v-model="sanPham.idThuongHieu" placeholder="Chọn thương hiệu" :loading="loadingThuongHieu" style="width: 100%">
              <el-option v-for="item in thuongHieuList" :key="item.idThuongHieu" :label="item.tenThuongHieu" :value="item.idThuongHieu" />
            </el-select>
          </el-form-item>
          <el-form-item label="Chất liệu" prop="idChatLieu">
            <el-select v-model="sanPham.idChatLieu" placeholder="Chọn chất liệu" :loading="loadingChatLieu" style="width: 100%">
              <el-option v-for="item in chatLieuList" :key="item.idChatLieu" :label="item.tenChatLieu" :value="item.idChatLieu" />
            </el-select>
          </el-form-item>
          <el-form-item label="Ảnh sản phẩm (chọn 1 ảnh nếu muốn thay đổi)">
            <el-upload :auto-upload="false" :on-change="handleProductImageChange" :on-remove="handleProductImageRemove"
                       :file-list="productImage" :limit="1" accept="image/*" :before-upload="beforeUpload">
              <el-button type="primary">Chọn ảnh</el-button>
              <template #tip><div class="el-upload__tip">Chọn 1 ảnh, kích thước < 5MB. Nếu không chọn, ảnh hiện tại sẽ được giữ.</div></template>
            </el-upload>
            <div class="thumbnail-list mt-2" v-if="productImage.length > 0">
              <div class="thumbnail-wrapper">
                <el-image :src="productImage[0].url" class="thumbnail-item" fit="cover" :preview-src-list="[productImage[0].url]" />
                <el-button type="danger" size="small" class="delete-button" @click="removeProductImage" title="Xóa ảnh">
                  <i class="fas fa-trash-alt"></i>
                </el-button>
              </div>
            </div>
            <div v-else-if="sanPham.imageLink" class="thumbnail-list mt-2">
              <div class="thumbnail-wrapper">
                <el-image :src="sanPham.imageLink" class="thumbnail-item" fit="cover" :preview-src-list="[sanPham.imageLink]" />
                <el-button type="danger" size="small" class="delete-button" @click="removeExistingProductImage" title="Xóa ảnh">
                  <i class="fas fa-trash-alt"></i>
                </el-button>
              </div>
            </div>
            <div v-else class="no-image">Chưa có ảnh</div>
          </el-form-item>
          <el-form-item label="Ngày tạo">
            <el-input :value="formatDate(sanPham.ngayTao)" disabled></el-input>
          </el-form-item>
          <el-form-item label="Giá trung bình">
            <el-input :value="formatPrice(sanPham.gia) || 'N/A'" disabled></el-input>
          </el-form-item>
          <el-form-item label="Tổng số lượng">
            <el-input :value="sanPham.tongSoLuongSanPham || 0" disabled></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitSanPham" :loading="submittingSanPham">Lưu thay đổi</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div class="card shadow-sm">
      <div class="card-body">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h4 class="card-title mb-0">Danh sách sản phẩm chi tiết</h4>
          <el-button type="primary" @click="openAddSanPhamChiTietModal" :disabled="isComboboxLoading || !hasValidComboboxData">
            <i class="fas fa-plus me-2"></i>Thêm sản phẩm chi tiết
          </el-button>
        </div>
        <div v-if="!hasValidComboboxData" class="alert alert-warning">
          Vui lòng thêm dữ liệu cho màu sắc hoặc kích thước để tiếp tục.
        </div>
        <div v-loading="loadingSanPhamChiTiet" style="width: 100%">
          <el-table :data="sanPhamChiTietList" class="product-detail-table">
            <el-table-column label="Mã SPCT" width="auto">
              <template #default="scope">{{ scope.row.maSPCT || 'N/A' }}</template>
            </el-table-column>
            <el-table-column label="Màu sắc" width="auto">
              <template #default="scope">{{ scope.row.tenMauSac || 'N/A' }}</template>
            </el-table-column>
            <el-table-column label="Kích thước" width="auto">
              <template #default="scope">{{ scope.row.tenSize || 'N/A' }}</template>
            </el-table-column>
            <el-table-column label="Giá" width="auto">
              <template #default="scope">{{ formatPrice(scope.row.gia) || 'N/A' }}</template>
            </el-table-column>
            <el-table-column label="Số lượng" width="auto">
              <template #default="scope">{{ scope.row.soLuong || '0' }}</template>
            </el-table-column>
            <el-table-column label="Ảnh chi tiết" width="auto">
              <template #default="scope">
                <div class="thumbnail-list">
                  <el-image v-for="(img, index) in scope.row.images" :key="index" :src="img" class="thumbnail-item" fit="cover" :preview-src-list="scope.row.images" />
                </div>
                <div v-if="!scope.row.images || scope.row.images.length === 0" class="no-image">Chưa có ảnh</div>
              </template>
            </el-table-column>
            <el-table-column label="Hành động" width="auto" align="center">
              <template #default="scope">
                <el-button type="primary" size="small" @click="openEditSanPhamChiTietModal(scope.row)" title="Sửa">
                  <i class="fas fa-edit"></i>
                </el-button>
                <el-button type="danger" size="small" @click="confirmDeleteSanPhamChiTiet(scope.row)" title="Xóa">
                  <i class="fas fa-trash-alt"></i>
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>

    <el-dialog :title="isEditingSanPhamChiTiet ? 'Sửa sản phẩm chi tiết' : 'Thêm sản phẩm chi tiết'"
               v-model="showSanPhamChiTietModal" width="40%" :before-close="handleCloseSanPhamChiTietModal">
      <el-form :model="sanPhamChiTietData" :rules="spctRules" ref="sanPhamChiTietForm" label-position="top" :validate-on-rule-change="false">
        <el-form-item label="Màu sắc" prop="idMauSac">
          <el-select v-model="sanPhamChiTietData.idMauSac" placeholder="Chọn màu sắc" :loading="loadingMauSac" style="width: 100%"
                     :disabled="loadingMauSac || mauSacList.length === 0">
            <el-option v-for="item in mauSacList" :key="item.idMauSac" :label="item.tenMauSac" :value="item.idMauSac" />
          </el-select>
        </el-form-item>
        <el-form-item label="Kích thước" prop="idSize">
          <el-select v-model="sanPhamChiTietData.idSize" placeholder="Chọn kích thước" :loading="loadingSize" style="width: 100%"
                     :disabled="loadingSize || sizeList.length === 0">
            <el-option v-for="item in sizeList" :key="item.idSize" :label="item.tenSize" :value="item.idSize" />
          </el-select>
        </el-form-item>
        <el-form-item label="Giá" prop="gia">
          <el-input v-model.number="sanPhamChiTietData.gia" placeholder="Nhập giá" type="number" />
        </el-form-item>
        <el-form-item label="Số lượng" prop="soLuong">
          <el-input v-model.number="sanPhamChiTietData.soLuong" placeholder="Nhập số lượng" type="number" />
        </el-form-item>
        <el-form-item label="Ảnh chi tiết (có thể chọn nhiều)" prop="images">
          <el-upload :auto-upload="false" :on-change="handleDetailImagesChange" :on-remove="handleDetailImagesRemove"
                     :file-list="detailImages" :multiple="true" :limit="5" accept="image/*" :before-upload="beforeUpload">
            <el-button type="primary">Chọn ảnh</el-button>
            <template #tip><div class="el-upload__tip">Tối đa 5 ảnh, kích thước < 5MB.</div></template>
          </el-upload>
          <div class="thumbnail-list mt-2" v-if="detailImages.length > 0">
            <div v-for="(file, index) in detailImages" :key="index" class="thumbnail-wrapper">
              <el-image :src="file.url" class="thumbnail-item" fit="cover" :preview-src-list="previewDetailImages" />
              <el-button type="danger" size="small" class="delete-button" @click="removeImage(index)" title="Xóa ảnh">
                <i class="fas fa-trash-alt"></i>
              </el-button>
            </div>
          </div>
          <div v-else class="no-image">Chưa chọn ảnh</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCloseSanPhamChiTietModal">Hủy</el-button>
          <el-button type="primary" @click="submitSanPhamChiTiet" :loading="submittingSanPhamChiTiet"
                     :disabled="isComboboxLoading || !hasValidComboboxData">
            {{ isEditingSanPhamChiTiet ? 'Lưu' : 'Thêm' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { ElMessage, ElMessageBox } from 'element-plus';

const route = useRoute();
const router = useRouter();
const sanPham = ref({});
const sanPhamChiTietList = ref([]);
const loading = ref(true);
const loadingSanPhamChiTiet = ref(false);
const submittingSanPham = ref(false);
const submittingSanPhamChiTiet = ref(false);
const sanPhamForm = ref(null);
const sanPhamChiTietForm = ref(null);
const showSanPhamChiTietModal = ref(false);
const isEditingSanPhamChiTiet = ref(false);
const productImage = ref([]);
const deletedProductImage = ref(null); // Lưu trữ URL ảnh sản phẩm bị xóa
const detailImages = ref([]);
const sanPhamChiTietData = ref({
  id: null,
  idSP: null,
  idMauSac: null,
  idSize: null,
  gia: null,
  soLuong: null,
  images: [],
});
const deletedImages = ref([]);

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

const hasValidComboboxData = computed(() => mauSacList.value.length > 0 && sizeList.value.length > 0);
const isComboboxLoading = computed(() => loadingMauSac.value || loadingSize.value);
const previewDetailImages = computed(() => detailImages.value.map((file) => file.url));

const rules = ref({
  tenSP: [{ required: true, message: 'Vui lòng nhập tên sản phẩm', trigger: 'blur' }],
  idDanhMuc: [{ required: true, message: 'Vui lòng chọn danh mục', trigger: 'change' }],
  idThuongHieu: [{ required: true, message: 'Vui lòng chọn thương hiệu', trigger: 'change' }],
  idChatLieu: [{ required: true, message: 'Vui lòng chọn chất liệu', trigger: 'change' }],
});

const spctRules = ref({
  idMauSac: [{ required: true, message: 'Vui lòng chọn màu sắc', trigger: 'change' }],
  idSize: [{ required: true, message: 'Vui lòng chọn kích thước', trigger: 'change' }],
  gia: [
    { required: true, message: 'Vui lòng nhập giá', trigger: 'blur' },
    { type: 'number', min: 0, message: 'Giá phải lớn hơn hoặc bằng 0', trigger: 'blur' },
  ],
  soLuong: [
    { required: true, message: 'Vui lòng nhập số lượng', trigger: 'blur' },
    { type: 'number', min: 0, message: 'Số lượng phải lớn hơn hoặc bằng 0', trigger: 'blur' },
  ],
  images: [
    { validator: (rule, value, callback) => {
        if (detailImages.value.length === 0 && (!sanPhamChiTietData.value.images || sanPhamChiTietData.value.images.length === 0)) {
          callback(new Error('Vui lòng chọn ít nhất một ảnh chi tiết'));
        } else {
          callback();
        }
      }, trigger: 'change' },
  ],
});

const formatDate = (date) => !date ? 'N/A' : new Intl.DateTimeFormat('vi-VN', { year: 'numeric', month: '2-digit', day: '2-digit' }).format(new Date(date));
const formatPrice = (price) => !price ? 'N/A' : new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);

const handleProductImageChange = (file, fileList) => {
  productImage.value = fileList.map(item => ({ raw: item.raw, url: URL.createObjectURL(item.raw) }));
  deletedProductImage.value = null; // Reset ảnh bị xóa khi chọn ảnh mới
};
const handleProductImageRemove = (file, fileList) => {
  productImage.value = fileList.map(item => ({ raw: item.raw, url: URL.createObjectURL(item.raw) }));
};
const removeProductImage = () => {
  if (productImage.value.length > 0) {
    deletedProductImage.value = productImage.value[0].url;
    productImage.value = [];
  }
};
const removeExistingProductImage = () => {
  if (sanPham.value.imageLink) {
    deletedProductImage.value = sanPham.value.imageLink;
    sanPham.value.imageLink = null;
  }
};
const handleDetailImagesChange = (file, fileList) => detailImages.value = fileList.map(item => ({ raw: item.raw, url: URL.createObjectURL(item.raw) }));
const handleDetailImagesRemove = (file, fileList) => {
  detailImages.value = fileList.map(item => ({ raw: item.raw, url: URL.createObjectURL(item.raw) }));
  if (file.url && !file.raw) deletedImages.value.push(file.url);
};
const removeImage = (index) => {
  const file = detailImages.value[index];
  detailImages.value.splice(index, 1);
  if (file.url && !file.raw) deletedImages.value.push(file.url);
  sanPhamChiTietForm.value.validateField('images');
};
const beforeUpload = (file) => {
  const isImage = ['image/jpeg', 'image/png', 'image/gif', 'image/bmp', 'image/webp'].includes(file.type.toLowerCase());
  const isLt5M = file.size / 1024 / 1024 < 5;
  if (!isImage) { ElMessage.error('Vui lòng chọn file ảnh (JPEG, PNG, GIF, BMP, WEBP)!'); return false; }
  if (!isLt5M) { ElMessage.error('Kích thước ảnh phải nhỏ hơn 5MB!'); return false; }
  return true;
};

const fetchSanPham = async () => {
  loading.value = true;
  try {
    const response = await axios.get(`http://localhost:8080/admin/api/san-pham/${route.params.id}`);
    sanPham.value = response.data || {};
    productImage.value = sanPham.value.imageLink ? [{ url: sanPham.value.imageLink }] : [];
  } catch (error) {
    ElMessage.error('Không thể tải thông tin sản phẩm.');
  } finally { loading.value = false; }
};

const fetchSanPhamChiTiet = async () => {
  loadingSanPhamChiTiet.value = true;
  try {
    const response = await axios.get(`http://localhost:8080/admin/api/sanphamchitiet/bySanPham/${route.params.id}`);
    sanPhamChiTietList.value = response.data.map(item => ({
      ...item,
      images: item.imageLinks || [],
    })) || [];
  } catch (error) {
    ElMessage.error('Không thể tải danh sách sản phẩm chi tiết.');
  } finally { loadingSanPhamChiTiet.value = false; }
};

const fetchDanhMuc = async () => {
  loadingDanhMuc.value = true;
  try { const response = await axios.get('http://localhost:8080/api/danhmuc'); danhMucList.value = response.data || []; }
  catch (error) { ElMessage.error('Không thể tải danh sách danh mục.'); danhMucList.value = []; }
  finally { loadingDanhMuc.value = false; }
};
const fetchThuongHieu = async () => {
  loadingThuongHieu.value = true;
  try { const response = await axios.get('http://localhost:8080/api/thuonghieu'); thuongHieuList.value = response.data || []; }
  catch (error) { ElMessage.error('Không thể tải danh sách thương hiệu.'); thuongHieuList.value = []; }
  finally { loadingThuongHieu.value = false; }
};
const fetchMauSac = async () => {
  loadingMauSac.value = true;
  try { const response = await axios.get('http://localhost:8080/api/mausac'); mauSacList.value = response.data || []; }
  catch (error) { ElMessage.error('Không thể tải danh sách màu sắc.'); mauSacList.value = []; }
  finally { loadingMauSac.value = false; }
};
const fetchChatLieu = async () => {
  loadingChatLieu.value = true;
  try { const response = await axios.get('http://localhost:8080/api/chatlieu'); chatLieuList.value = response.data || []; }
  catch (error) { ElMessage.error('Không thể tải danh sách chất liệu.'); chatLieuList.value = []; }
  finally { loadingChatLieu.value = false; }
};
const fetchSize = async () => {
  loadingSize.value = true;
  try { const response = await axios.get('http://localhost:8080/api/size'); sizeList.value = response.data || []; }
  catch (error) { ElMessage.error('Không thể tải danh sách kích thước.'); sizeList.value = []; }
  finally { loadingSize.value = false; }
};

const fetchAllComboboxData = async () => Promise.all([fetchDanhMuc(), fetchThuongHieu(), fetchChatLieu(), fetchMauSac(), fetchSize()]);

const openAddSanPhamChiTietModal = async () => {
  if (!hasValidComboboxData.value) { ElMessage.error('Không thể mở modal: Vui lòng kiểm tra dữ liệu màu sắc hoặc kích thước.'); return; }
  isEditingSanPhamChiTiet.value = false;
  sanPhamChiTietData.value = { id: null, idSP: parseInt(route.params.id), idMauSac: mauSacList.value[0]?.idMauSac || null, idSize: sizeList.value[0]?.idSize || null, gia: null, soLuong: null, images: [] };
  detailImages.value = []; deletedImages.value = [];
  await fetchAllComboboxData();
  showSanPhamChiTietModal.value = true;
};

const openEditSanPhamChiTietModal = async (spct) => {
  isEditingSanPhamChiTiet.value = true;
  sanPhamChiTietData.value = { id: spct.id, idSP: parseInt(route.params.id), idMauSac: spct.idMauSac, idSize: spct.idSize, gia: spct.gia, soLuong: spct.soLuong, images: spct.imageLinks || [] };
  detailImages.value = spct.imageLinks ? spct.imageLinks.map(url => ({ url })) : [];
  deletedImages.value = [];
  await fetchAllComboboxData();
  showSanPhamChiTietModal.value = true;
};

const handleCloseSanPhamChiTietModal = () => {
  showSanPhamChiTietModal.value = false;
  sanPhamChiTietData.value = { id: null, idSP: parseInt(route.params.id), idMauSac: null, idSize: null, gia: null, soLuong: null, images: [] };
  detailImages.value = []; deletedImages.value = [];
  sanPhamChiTietForm.value?.resetFields();
};

const submitSanPham = async () => {
  try {
    await ElMessageBox.confirm('Bạn có chắc chắn muốn lưu thay đổi sản phẩm này?', 'Xác nhận', { confirmButtonText: 'Lưu', cancelButtonText: 'Hủy', type: 'warning' });
    await sanPhamForm.value.validate(async (valid) => {
      if (valid) {
        submittingSanPham.value = true;
        const formData = new FormData();
        const sanPhamDTO = {
          idSP: parseInt(route.params.id),
          tenSP: sanPham.value.tenSP,
          idDanhMuc: sanPham.value.idDanhMuc,
          idThuongHieu: sanPham.value.idThuongHieu,
          idChatLieu: sanPham.value.idChatLieu,
          imageLink: sanPham.value.imageLink // Gửi imageLink hiện tại
        };
        console.log('SanPhamDTO before sending:', sanPhamDTO);
        formData.append('data', new Blob([JSON.stringify(sanPhamDTO)], { type: 'application/json' }));
        if (productImage.value.length > 0 && productImage.value[0].raw) {
          formData.append('imageFile', productImage.value[0].raw);
        }
        if (deletedProductImage.value) {
          formData.append('deletedImage', deletedProductImage.value);
        }
        console.log('FormData entries:', [...formData.entries()]);
        await axios.put(`http://localhost:8080/admin/api/san-pham/${route.params.id}`, formData, { headers: { 'Content-Type': 'multipart/form-data' } });
        ElMessage.success('Cập nhật sản phẩm thành công!');
        if (router.hasRoute('admin.products')) {
          router.push({ name: 'admin.products' });
        } else {
          router.push('/admin/products');
        }
      }
    });
  } catch (error) {
    if (error !== 'cancel') {
      let errorMessage = 'Lỗi khi cập nhật sản phẩm.';
      if (error.response?.data?.error) {
        errorMessage = error.response.data.error;
      } else if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      } else if (error.message) {
        errorMessage = error.message;
      }
      ElMessage.error(errorMessage);
      console.error('Lỗi khi cập nhật sản phẩm:', error, error.response?.data);
    }
  } finally {
    submittingSanPham.value = false;
  }
};

const submitSanPhamChiTiet = async () => {
  try {
    // Kiểm tra trùng lặp màu và size
    const isDuplicate = sanPhamChiTietList.value.some(
      item =>
        item.id !== sanPhamChiTietData.value.id && // Bỏ qua sản phẩm chi tiết đang chỉnh sửa
        item.idMauSac === sanPhamChiTietData.value.idMauSac &&
        item.idSize === sanPhamChiTietData.value.idSize
    );
    if (isDuplicate) {
      ElMessage.error('Đã tồn tại sản phẩm chi tiết với màu và kích thước này.');
      return;
    }

    // Xác nhận từ người dùng
    await ElMessageBox.confirm(
      `Bạn có chắc chắn muốn ${isEditingSanPhamChiTiet.value ? 'lưu thay đổi' : 'thêm'} sản phẩm chi tiết này?`,
      'Xác nhận',
      {
        confirmButtonText: isEditingSanPhamChiTiet.value ? 'Lưu' : 'Thêm',
        cancelButtonText: 'Hủy',
        type: 'warning',
      }
    );

    // Kiểm tra form hợp lệ
    await sanPhamChiTietForm.value.validate(async (valid) => {
      if (valid) {
        submittingSanPhamChiTiet.value = true;
        const formData = new FormData();
        formData.append(
          'data',
          new Blob(
            [
              JSON.stringify({
                idSP: parseInt(route.params.id),
                idMauSac: sanPhamChiTietData.value.idMauSac,
                idSize: sanPhamChiTietData.value.idSize,
                gia: sanPhamChiTietData.value.gia,
                soLuong: sanPhamChiTietData.value.soLuong,
              }),
            ],
            { type: 'application/json' }
          )
        );
        detailImages.value.forEach(image => {
          if (image.raw) formData.append('imageFiles', image.raw);
        });
        if (deletedImages.value.length > 0) {
          formData.append('deletedImages', JSON.stringify(deletedImages.value));
        }

        let response;
        if (isEditingSanPhamChiTiet.value) {
          response = await axios.put(
            `http://localhost:8080/admin/api/sanphamchitiet/${sanPhamChiTietData.value.id}`,
            formData,
            { headers: { 'Content-Type': 'multipart/form-data' } }
          );
          ElMessage.success('Cập nhật sản phẩm chi tiết thành công!');
        } else {
          response = await axios.post(
            'http://localhost:8080/admin/api/sanphamchitiet',
            formData,
            { headers: { 'Content-Type': 'multipart/form-data' } }
          );
          ElMessage.success('Thêm sản phẩm chi tiết thành công!');
        }

        showSanPhamChiTietModal.value = false;
        fetchSanPhamChiTiet();
        fetchSanPham();
        handleCloseSanPhamChiTietModal();
      }
    });
  } catch (error) {
    if (error !== 'cancel') {
      let errorMessage = 'Lỗi khi xử lý sản phẩm chi tiết.';
      if (error.response?.data?.error) {
        errorMessage = error.response.data.error;
      } else if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      } else if (error.message) {
        errorMessage = error.message;
      }
      ElMessage.error(errorMessage);
      console.error('Lỗi khi xử lý sản phẩm chi tiết:', error, error.response?.data);
    }
  } finally {
    submittingSanPhamChiTiet.value = false;
  }
};

const confirmDeleteSanPhamChiTiet = (spct) => {
  ElMessageBox.confirm(`Bạn có chắc chắn muốn xóa sản phẩm chi tiết "<strong>${spct.maSPCT}</strong>"?`, 'Xác nhận Xóa',
    { confirmButtonText: 'Xóa', cancelButtonText: 'Hủy', type: 'warning', dangerouslyUseHTMLString: true })
    .then(async () => {
      try {
        await axios.delete(`http://localhost:8080/admin/api/sanphamchitiet/${spct.id}`);
        ElMessage.success('Xóa sản phẩm chi tiết thành công!');
        fetchSanPhamChiTiet();
        fetchSanPham();
      } catch (error) {
        ElMessage.error('Có lỗi xảy ra khi xóa sản phẩm chi tiết.');
        console.error('Lỗi khi xóa sản phẩm chi tiết:', error);
      }
    })
    .catch(() => ElMessage.info('Đã hủy thao tác xóa'));
};

onMounted(() => {
  fetchSanPham();
  fetchSanPhamChiTiet();
  fetchAllComboboxData();
});
</script>

<style scoped>
.thumbnail-wrapper { position: relative; display: inline-block; margin-right: 10px; }
.thumbnail-item { width: 100px; height: 100px; }
.delete-button { position: absolute; top: 5px; right: 5px; padding: 2px 6px; font-size: 12px; }
.delete-button i { margin: 0; }
.no-image { color: #999; }
</style>