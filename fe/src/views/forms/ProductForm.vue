<template>
  <div class="container-fluid">
    <!-- Tiêu đề và nút quay lại -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h3 mb-0">Sửa sản phẩm: {{ sanPham.tenSP || 'Loading...' }}</h1>
      <router-link to="/admin/products" class="btn btn-secondary">
        <i class="fas fa-arrow-left me-2"></i>Quay lại
      </router-link>
    </div>

    <!-- Form sửa sản phẩm -->
    <div class="card shadow-sm mb-4">
      <div class="card-body">
        <h4 class="card-title">Thông tin sản phẩm</h4>
        <el-form :model="sanPham" :rules="rules" ref="sanPhamForm" label-position="top">
          <el-form-item label="Tên sản phẩm" prop="tenSP">
            <el-input v-model="sanPham.tenSP" placeholder="Nhập tên sản phẩm"></el-input>
          </el-form-item>
          <el-form-item label="Ngày tạo">
            <el-input :value="formatDate(sanPham.ngayTao)" disabled></el-input>
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

    <!-- Bảng sản phẩm chi tiết -->
    <div class="card shadow-sm">
      <div class="card-body">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h4 class="card-title mb-0">Danh sách sản phẩm chi tiết</h4>
          <el-button
            type="primary"
            @click="openAddSanPhamChiTietModal"
            :disabled="isComboboxLoading || !hasValidComboboxData"
          >
            <i class="fas fa-plus me-2"></i>Thêm sản phẩm chi tiết
          </el-button>
        </div>
        <div v-if="!hasValidComboboxData" class="alert alert-warning">
          Vui lòng thêm dữ liệu cho danh mục, thương hiệu, màu sắc, chất liệu hoặc kích thước để tiếp tục.
        </div>
        <div v-loading="loadingSanPhamChiTiet">
          <el-table :data="sanPhamChiTietList" style="width: 100%" class="product-detail-table">
            <el-table-column label="Mã SPCT" width="120">
              <template #default="scope">
                {{ scope.row.maSPCT || 'N/A' }}
              </template>
            </el-table-column>
            <el-table-column label="Danh mục" width="150">
              <template #default="scope">
                {{ scope.row.tenDanhMuc || 'N/A' }}
              </template>
            </el-table-column>
            <el-table-column label="Thương hiệu" width="150">
              <template #default="scope">
                {{ scope.row.tenThuongHieu || 'N/A' }}
              </template>
            </el-table-column>
            <el-table-column label="Màu sắc" width="120">
              <template #default="scope">
                {{ scope.row.tenMauSac || 'N/A' }}
              </template>
            </el-table-column>
            <el-table-column label="Chất liệu" width="120">
              <template #default="scope">
                {{ scope.row.tenChatLieu || 'N/A' }}
              </template>
            </el-table-column>
            <el-table-column label="Kích thước" width="120">
              <template #default="scope">
                {{ scope.row.tenSize || 'N/A' }}
              </template>
            </el-table-column>
            <el-table-column label="Giá" width="120">
              <template #default="scope">
                {{ formatPrice(scope.row.gia) || 'N/A' }}
              </template>
            </el-table-column>
            <el-table-column label="Số lượng" width="100">
              <template #default="scope">
                {{ scope.row.soLuong || '0' }}
              </template>
            </el-table-column>
            <el-table-column label="Mô tả">
              <template #default="scope">
                {{ scope.row.moTa || 'N/A' }}
              </template>
            </el-table-column>
            <el-table-column label="Hành động" width="120" align="center">
              <template #default="scope">
                <el-button
                  type="primary"
                  size="small"
                  @click="openEditSanPhamChiTietModal(scope.row)"
                  title="Sửa"
                >
                  <i class="fas fa-edit"></i>
                </el-button>
                <el-button
                  type="danger"
                  size="small"
                  @click="confirmDeleteSanPhamChiTiet(scope.row)"
                  title="Xóa"
                >
                  <i class="fas fa-trash-alt"></i>
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>

    <!-- Modal thêm/sửa sản phẩm chi tiết -->
    <el-dialog
      :title="isEditingSanPhamChiTiet ? 'Sửa sản phẩm chi tiết' : 'Thêm sản phẩm chi tiết'"
      v-model="showSanPhamChiTietModal"
      width="40%"
      :before-close="handleCloseSanPhamChiTietModal"
    >
      <el-form :model="sanPhamChiTietData" :rules="spctRules" ref="sanPhamChiTietForm" label-position="top" :validate-on-rule-change="false">
        <el-form-item label="Danh mục" prop="idDanhMuc">
          <el-select
            v-model="sanPhamChiTietData.idDanhMuc"
            placeholder="Chọn danh mục"
            :loading="loadingDanhMuc"
            style="width: 100%;"
            :disabled="loadingDanhMuc || danhMucList.length === 0"
          >
            <el-option
              v-for="item in danhMucList"
              :key="item.idDM"
              :label="item.tenDM"
              :value="item.idDM"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Thương hiệu" prop="idThuongHieu">
          <el-select
            v-model="sanPhamChiTietData.idThuongHieu"
            placeholder="Chọn thương hiệu"
            :loading="loadingThuongHieu"
            style="width: 100%;"
            :disabled="loadingThuongHieu || thuongHieuList.length === 0"
          >
            <el-option
              v-for="item in thuongHieuList"
              :key="item.idThuongHieu"
              :label="item.tenThuongHieu"
              :value="item.idThuongHieu"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Màu sắc" prop="idMauSac">
          <el-select
            v-model="sanPhamChiTietData.idMauSac"
            placeholder="Chọn màu sắc"
            :loading="loadingMauSac"
            style="width: 100%;"
            :disabled="loadingMauSac || mauSacList.length === 0"
          >
            <el-option
              v-for="item in mauSacList"
              :key="item.idMauSac"
              :label="item.tenMauSac"
              :value="item.idMauSac"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Chất liệu" prop="idChatLieu">
          <el-select
            v-model="sanPhamChiTietData.idChatLieu"
            placeholder="Chọn chất liệu"
            :loading="loadingChatLieu"
            style="width: 100%;"
            :disabled="loadingChatLieu || chatLieuList.length === 0"
          >
            <el-option
              v-for="item in chatLieuList"
              :key="item.idChatLieu"
              :label="item.tenChatLieu"
              :value="item.idChatLieu"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Kích thước" prop="idSize">
          <el-select
            v-model="sanPhamChiTietData.idSize"
            placeholder="Chọn kích thước"
            :loading="loadingSize"
            style="width: 100%;"
            :disabled="loadingSize || sizeList.length === 0"
          >
            <el-option
              v-for="item in sizeList"
              :key="item.idSize"
              :label="item.tenSize"
              :value="item.idSize"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Giá" prop="gia">
          <el-input
            v-model.number="sanPhamChiTietData.gia"
            placeholder="Nhập giá"
            type="number"
          ></el-input>
        </el-form-item>
        <el-form-item label="Số lượng" prop="soLuong">
          <el-input
            v-model.number="sanPhamChiTietData.soLuong"
            placeholder="Nhập số lượng"
            type="number"
          ></el-input>
        </el-form-item>
        <el-form-item label="Mô tả" prop="moTa">
          <el-input
            v-model="sanPhamChiTietData.moTa"
            placeholder="Nhập mô tả"
            type="textarea"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCloseSanPhamChiTietModal">Hủy</el-button>
          <el-button
            type="primary"
            @click="submitSanPhamChiTiet"
            :loading="submittingSanPhamChiTiet"
            :disabled="isComboboxLoading || !hasValidComboboxData"
          >
            {{ isEditingSanPhamChiTiet ? 'Lưu' : 'Thêm' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';
import { ElMessage, ElMessageBox, ElForm } from 'element-plus';

const route = useRoute();
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
const sanPhamChiTietData = ref({
  id: null,
  idSP: null,
  idDanhMuc: null,
  idThuongHieu: null,
  idMauSac: null,
  idChatLieu: null,
  idSize: null,
  gia: null,
  soLuong: null,
  moTa: '',
});

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

// Kiểm tra dữ liệu combobox hợp lệ
const hasValidComboboxData = computed(() => {
  return (
    danhMucList.value.length > 0 &&
    thuongHieuList.value.length > 0 &&
    mauSacList.value.length > 0 &&
    chatLieuList.value.length > 0 &&
    sizeList.value.length > 0
  );
});

// Trạng thái tổng hợp cho combobox loading
const isComboboxLoading = computed(() => {
  return loadingDanhMuc.value || loadingThuongHieu.value || loadingMauSac.value || loadingChatLieu.value || loadingSize.value;
});

// Quy tắc xác thực form SanPham
const rules = {
  tenSP: [{ required: true, message: 'Vui lòng nhập tên sản phẩm', trigger: 'blur' }],
};

// Quy tắc xác thực form SanPhamChiTiet
const spctRules = {
  idDanhMuc: [{ required: true, message: 'Vui lòng chọn danh mục', trigger: 'change' }],
  idThuongHieu: [{ required: true, message: 'Vui lòng chọn thương hiệu', trigger: 'change' }],
  idMauSac: [{ required: true, message: 'Vui lòng chọn màu sắc', trigger: 'change' }],
  idChatLieu: [{ required: true, message: 'Vui lòng chọn chất liệu', trigger: 'change' }],
  idSize: [{ required: true, message: 'Vui lòng chọn kích thước', trigger: 'change' }],
  gia: [
    { required: true, message: 'Vui lòng nhập giá', trigger: 'blur' },
    { type: 'number', min: 0, message: 'Giá phải lớn hơn hoặc bằng 0', trigger: 'blur' },
  ],
  soLuong: [
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

// Định dạng giá
const formatPrice = (price) => {
  if (!price) return 'N/A';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
};

// Lấy thông tin sản phẩm
const fetchSanPham = async () => {
  loading.value = true;
  try {
    const response = await axios.get(`http://localhost:8080/admin/api/san-pham/${route.params.id}`);
    sanPham.value = response.data || {};
  } catch (error) {
    console.error('Lỗi khi lấy sản phẩm:', error);
    ElMessage.error('Không thể tải thông tin sản phẩm.');
  } finally {
    loading.value = false;
  }
};

// Lấy danh sách sản phẩm chi tiết
const fetchSanPhamChiTiet = async () => {
  loadingSanPhamChiTiet.value = true;
  try {
    const response = await axios.get(`http://localhost:8080/admin/api/sanphamchitiet/bySanPham/${route.params.id}`);
    sanPhamChiTietList.value = response.data || [];
  } catch (error) {
    console.error('Lỗi khi lấy danh sách sản phẩm chi tiết:', error);
    ElMessage.error('Không thể tải danh sách sản phẩm chi tiết.');
  } finally {
    loadingSanPhamChiTiet.value = false;
  }
};

// Lấy danh sách danh mục
const fetchDanhMuc = async () => {
  loadingDanhMuc.value = true;
  try {
    const response = await axios.get('http://localhost:8080/admin/api/danhmuc');
    danhMucList.value = Array.isArray(response.data) ? response.data : [];
    if (danhMucList.value.length === 0) {
      ElMessage.warning('Danh sách danh mục trống.');
    }
  } catch (error) {
    console.error('Lỗi khi lấy danh mục:', error);
    ElMessage.error('Không thể tải danh sách danh mục.');
    danhMucList.value = [];
  } finally {
    loadingDanhMuc.value = false;
  }
};

// Lấy danh sách thương hiệu
const fetchThuongHieu = async () => {
  loadingThuongHieu.value = true;
  try {
    const response = await axios.get('http://localhost:8080/admin/api/thuonghieu');
    thuongHieuList.value = Array.isArray(response.data) ? response.data : [];
    if (thuongHieuList.value.length === 0) {
      ElMessage.warning('Danh sách thương hiệu trống.');
    }
  } catch (error) {
    console.error('Lỗi khi lấy thương hiệu:', error);
    ElMessage.error('Không thể tải danh sách thương hiệu.');
    thuongHieuList.value = [];
  } finally {
    loadingThuongHieu.value = false;
  }
};

// Lấy danh sách màu sắc
const fetchMauSac = async () => {
  loadingMauSac.value = true;
  try {
    const response = await axios.get('http://localhost:8080/admin/api/mausac');
    mauSacList.value = Array.isArray(response.data) ? response.data : [];
    if (mauSacList.value.length === 0) {
      ElMessage.warning('Danh sách màu sắc trống.');
    }
  } catch (error) {
    console.error('Lỗi khi lấy màu sắc:', error);
    ElMessage.error('Không thể tải danh sách màu sắc.');
    mauSacList.value = [];
  } finally {
    loadingMauSac.value = false;
  }
};

// Lấy danh sách chất liệu
const fetchChatLieu = async () => {
  loadingChatLieu.value = true;
  try {
    const response = await axios.get('http://localhost:8080/admin/api/chatlieu');
    chatLieuList.value = Array.isArray(response.data) ? response.data : [];
    if (chatLieuList.value.length === 0) {
      ElMessage.warning('Danh sách chất liệu trống.');
    }
  } catch (error) {
    console.error('Lỗi khi lấy chất liệu:', error);
    ElMessage.error('Không thể tải danh sách chất liệu.');
    chatLieuList.value = [];
  } finally {
    loadingChatLieu.value = false;
  }
};

// Lấy danh sách kích thước
const fetchSize = async () => {
  loadingSize.value = true;
  try {
    const response = await axios.get('http://localhost:8080/admin/api/size');
    sizeList.value = Array.isArray(response.data) ? response.data : [];
    if (sizeList.value.length === 0) {
      ElMessage.warning('Danh sách kích thước trống.');
    }
  } catch (error) {
    console.error('Lỗi khi lấy kích thước:', error);
    ElMessage.error('Không thể tải danh sách kích thước.');
    sizeList.value = [];
  } finally {
    loadingSize.value = false;
  }
};

// Tải tất cả dữ liệu combobox
const fetchAllComboboxData = async () => {
  try {
    await Promise.all([
      fetchDanhMuc(),
      fetchThuongHieu(),
      fetchMauSac(),
      fetchChatLieu(),
      fetchSize(),
    ]);
  } catch (error) {
    console.error('Lỗi khi tải dữ liệu combobox:', error);
    ElMessage.error('Không thể tải dữ liệu cho combobox.');
  }
};

// Mở modal thêm sản phẩm chi tiết
const openAddSanPhamChiTietModal = async () => {
  if (!hasValidComboboxData.value) {
    ElMessage.error('Không thể mở modal: Vui lòng kiểm tra dữ liệu danh mục, thương hiệu, màu sắc, chất liệu hoặc kích thước.');
    return;
  }
  isEditingSanPhamChiTiet.value = false;
  sanPhamChiTietData.value = {
    id: null,
    idSP: parseInt(route.params.id),
    idDanhMuc: danhMucList.value.length > 0 ? danhMucList.value[0].idDM : null,
    idThuongHieu: thuongHieuList.value.length > 0 ? thuongHieuList.value[0].idThuongHieu : null,
    idMauSac: mauSacList.value.length > 0 ? mauSacList.value[0].idMauSac : null,
    idChatLieu: chatLieuList.value.length > 0 ? chatLieuList.value[0].idChatLieu : null,
    idSize: sizeList.value.length > 0 ? sizeList.value[0].idSize : null,
    gia: null,
    soLuong: null,
    moTa: '',
  };
  await fetchAllComboboxData();
  if (!hasValidComboboxData.value) {
    ElMessage.error('Không thể mở modal: Dữ liệu combobox không hợp lệ.');
    return;
  }
  showSanPhamChiTietModal.value = true;
};

// Mở modal sửa sản phẩm chi tiết
const openEditSanPhamChiTietModal = async (spct) => {
  isEditingSanPhamChiTiet.value = true;
  sanPhamChiTietData.value = {
    id: spct.id,
    idSP: parseInt(route.params.id),
    idDanhMuc: spct.idDanhMuc || null,
    idThuongHieu: spct.idThuongHieu || null,
    idMauSac: spct.idMauSac || null,
    idChatLieu: spct.idChatLieu || null,
    idSize: spct.idSize || null,
    gia: spct.gia,
    soLuong: spct.soLuong,
    moTa: spct.moTa || '',
  };
  await fetchAllComboboxData();
  if (!hasValidComboboxData.value) {
    ElMessage.error('Không thể mở modal: Dữ liệu combobox không hợp lệ.');
    return;
  }
  showSanPhamChiTietModal.value = true;
};

// Đóng modal sản phẩm chi tiết
const handleCloseSanPhamChiTietModal = () => {
  showSanPhamChiTietModal.value = false;
  sanPhamChiTietData.value = {
    id: null,
    idSP: parseInt(route.params.id),
    idDanhMuc: null,
    idThuongHieu: null,
    idMauSac: null,
    idChatLieu: null,
    idSize: null,
    gia: null,
    soLuong: null,
    moTa: '',
  };
  if (sanPhamChiTietForm.value) {
    sanPhamChiTietForm.value.resetFields();
  }
};

// Lưu thay đổi sản phẩm
const submitSanPham = async () => {
  try {
    await ElMessageBox.confirm(
      'Bạn có chắc chắn muốn lưu thay đổi sản phẩm này?',
      'Xác nhận',
      { confirmButtonText: 'Lưu', cancelButtonText: 'Hủy', type: 'warning' }
    );
    await sanPhamForm.value.validate(async (valid) => {
      if (valid) {
        submittingSanPham.value = true;
        await axios.put(`http://localhost:8080/admin/api/san-pham/${route.params.id}`, {
          tenSP: sanPham.value.tenSP,
        });
        ElMessage.success('Cập nhật sản phẩm thành công!');
        fetchSanPham();
      }
    });
  } catch (error) {
    if (error === 'cancel') return;
    console.error('Lỗi khi cập nhật sản phẩm:', error);
    ElMessage.error(error.response?.data?.message || 'Lỗi khi cập nhật sản phẩm.');
  } finally {
    submittingSanPham.value = false;
  }
};

// Lưu hoặc thêm sản phẩm chi tiết
const submitSanPhamChiTiet = async () => {
  try {
    await ElMessageBox.confirm(
      `Bạn có chắc chắn muốn ${isEditingSanPhamChiTiet.value ? 'lưu thay đổi' : 'thêm'} sản phẩm chi tiết này?`,
      'Xác nhận',
      { confirmButtonText: isEditingSanPhamChiTiet.value ? 'Lưu' : 'Thêm', cancelButtonText: 'Hủy', type: 'warning' }
    );
    await sanPhamChiTietForm.value.validate(async (valid) => {
      if (valid) {
        submittingSanPhamChiTiet.value = true;
        const payload = {
          idSP: parseInt(route.params.id),
          idDanhMuc: sanPhamChiTietData.value.idDanhMuc,
          idThuongHieu: sanPhamChiTietData.value.idThuongHieu,
          idMauSac: sanPhamChiTietData.value.idMauSac,
          idChatLieu: sanPhamChiTietData.value.idChatLieu,
          idSize: sanPhamChiTietData.value.idSize,
          gia: sanPhamChiTietData.value.gia,
          soLuong: sanPhamChiTietData.value.soLuong,
          moTa: sanPhamChiTietData.value.moTa,
        };

        if (isEditingSanPhamChiTiet.value) {
          await axios.put(`http://localhost:8080/admin/api/sanphamchitiet/${sanPhamChiTietData.value.id}`, payload);
          ElMessage.success('Cập nhật sản phẩm chi tiết thành công!');
        } else {
          await axios.post('http://localhost:8080/admin/api/sanphamchitiet', payload);
          ElMessage.success('Thêm sản phẩm chi tiết thành công!');
        }

        showSanPhamChiTietModal.value = false;
        fetchSanPhamChiTiet();
        fetchSanPham(); // Cập nhật tổng số lượng sản phẩm
        handleCloseSanPhamChiTietModal();
      }
    });
  } catch (error) {
    if (error === 'cancel') return;
    console.error('Lỗi khi xử lý sản phẩm chi tiết:', error);
    ElMessage.error(error.response?.data?.message || 'Lỗi khi xử lý sản phẩm chi tiết.');
  } finally {
    submittingSanPhamChiTiet.value = false;
  }
};

// Xác nhận xóa sản phẩm chi tiết
const confirmDeleteSanPhamChiTiet = (spct) => {
  ElMessageBox.confirm(
    `Bạn có chắc chắn muốn xóa sản phẩm chi tiết "<strong>${spct.maSPCT}</strong>"?`,
    'Xác nhận Xóa',
    { confirmButtonText: 'Xóa', cancelButtonText: 'Hủy', type: 'warning', dangerouslyUseHTMLString: true }
  )
    .then(async () => {
      try {
        await axios.delete(`http://localhost:8080/admin/api/sanphamchitiet/${spct.id}`);
        ElMessage.success('Xóa sản phẩm chi tiết thành công!');
        fetchSanPhamChiTiet();
        fetchSanPham(); // Cập nhật tổng số lượng sản phẩm
      } catch (error) {
        ElMessage.error('Có lỗi xảy ra khi xóa sản phẩm chi tiết.');
      }
    })
    .catch(() => {
      ElMessage.info('Đã hủy thao tác xóa');
    });
};

// Khởi tạo
onMounted(() => {
  fetchSanPham();
  fetchSanPhamChiTiet();
  fetchAllComboboxData(); // Tải trước dữ liệu combobox khi component được mount
});
</script>

<style scoped>
.product-detail-table {
  width: 100%;
}
.alert-warning {
  margin-bottom: 1rem;
}
</style>