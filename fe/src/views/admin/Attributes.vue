<template>
  <h1 class="h3 mb-4 text-gray-800">Quản lý Thuộc tính</h1>
  <div class="row">
    <!-- Card Danh mục -->
    <div class="col-md-6 col-lg-4 mb-4">
      <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
          <span>Danh mục</span>
          <button
            @click="openAttributeModal('category', 'Thêm danh mục mới')"
            class="btn btn-sm btn-primary"
            :disabled="loadingCategories"
          >
            <i class="fas fa-plus"></i>
          </button>
        </div>
        <div v-if="loadingCategories" class="text-center p-3">
          <div class="spinner-border" role="status">
            <span class="visually-hidden">Đang tải...</span>
          </div>
        </div>
        <ul v-else class="list-group list-group-flush">
          <li
            v-for="category in categories"
            :key="category.idDM"
            class="list-group-item d-flex justify-content-between align-items-center"
          >
            {{ category.tenDM }}
            <div>
              <button
                @click="openAttributeModal('category', 'Sửa danh mục', category)"
                class="btn btn-sm btn-link text-primary"
              >
                <i class="fas fa-edit"></i>
              </button>
              <button
                @click="openDeleteModal('danh mục', category.idDM)"
                class="btn btn-sm btn-link text-danger"
              >
                <i class="fas fa-trash-alt"></i>
              </button>
            </div>
          </li>
          <li v-if="categories.length === 0" class="list-group-item text-center">
            Không có danh mục nào.
          </li>
        </ul>
      </div>
    </div>

    <!-- Card Màu sắc -->
    <div class="col-md-6 col-lg-4 mb-4">
      <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
          <span>Màu sắc</span>
          <button
            @click="openAttributeModal('color', 'Thêm màu sắc mới')"
            class="btn btn-sm btn-primary"
            :disabled="loadingColors"
          >
            <i class="fas fa-plus"></i>
          </button>
        </div>
        <div v-if="loadingColors" class="text-center p-3">
          <div class="spinner-border" role="status">
            <span class="visually-hidden">Đang tải...</span>
          </div>
        </div>
        <ul v-else class="list-group list-group-flush">
          <li
            v-for="color in colors"
            :key="color.idMauSac"
            class="list-group-item d-flex justify-content-between align-items-center"
          >
            {{ color.tenMauSac }} <span class="badge bg-secondary">{{ color.maMauSac }}</span>
            <div>
              <button
                @click="openAttributeModal('color', 'Sửa màu sắc', color)"
                class="btn btn-sm btn-link text-primary"
              >
                <i class="fas fa-edit"></i>
              </button>
              <button
                @click="openDeleteModal('màu sắc', color.idMauSac)"
                class="btn btn-sm btn-link text-danger"
              >
                <i class="fas fa-trash-alt"></i>
              </button>
            </div>
          </li>
          <li v-if="colors.length === 0" class="list-group-item text-center">
            Không có màu sắc nào.
          </li>
        </ul>
      </div>
    </div>

    <!-- Card Size -->
    <div class="col-md-6 col-lg-4 mb-4">
      <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
          <span>Size</span>
          <button
            @click="openAttributeModal('size', 'Thêm size mới')"
            class="btn btn-sm btn-primary"
            :disabled="loadingSizes"
          >
            <i class="fas fa-plus"></i>
          </button>
        </div>
        <div v-if="loadingSizes" class="text-center p-3">
          <div class="spinner-border" role="status">
            <span class="visually-hidden">Đang tải...</span>
          </div>
        </div>
        <ul v-else class="list-group list-group-flush">
          <li
            v-for="size in sizes"
            :key="size.idSize"
            class="list-group-item d-flex justify-content-between align-items-center"
          >
            {{ size.tenSize }}
            <div>
              <button
                @click="openAttributeModal('size', 'Sửa size', size)"
                class="btn btn-sm btn-link text-primary"
              >
                <i class="fas fa-edit"></i>
              </button>
              <button
                @click="openDeleteModal('size', size.idSize)"
                class="btn btn-sm btn-link text-danger"
              >
                <i class="fas fa-trash-alt"></i>
              </button>
            </div>
          </li>
          <li v-if="sizes.length === 0" class="list-group-item text-center">
            Không có kích thước nào.
          </li>
        </ul>
      </div>
    </div>

    <!-- Card Thương hiệu -->
    <div class="col-md-6 col-lg-4 mb-4">
      <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
          <span>Thương hiệu</span>
          <button
            @click="openAttributeModal('brand', 'Thêm thương hiệu mới')"
            class="btn btn-sm btn-primary"
            :disabled="loadingBrands"
          >
            <i class="fas fa-plus"></i>
          </button>
        </div>
        <div v-if="loadingBrands" class="text-center p-3">
          <div class="spinner-border" role="status">
            <span class="visually-hidden">Đang tải...</span>
          </div>
        </div>
        <ul v-else class="list-group list-group-flush">
          <li
            v-for="brand in brands"
            :key="brand.idThuongHieu"
            class="list-group-item d-flex justify-content-between align-items-center"
          >
            {{ brand.tenThuongHieu }}
            <div>
              <button
                @click="openAttributeModal('brand', 'Sửa thương hiệu', brand)"
                class="btn btn-sm btn-link text-primary"
              >
                <i class="fas fa-edit"></i>
              </button>
              <button
                @click="openDeleteModal('thương hiệu', brand.idThuongHieu)"
                class="btn btn-sm btn-link text-danger"
              >
                <i class="fas fa-trash-alt"></i>
              </button>
            </div>
          </li>
          <li v-if="brands.length === 0" class="list-group-item text-center">
            Không có thương hiệu nào.
          </li>
        </ul>
      </div>
    </div>

    <!-- Card Chất liệu -->
    <div class="col-md-6 col-lg-4 mb-4">
      <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
          <span>Chất liệu</span>
          <button
            @click="openAttributeModal('material', 'Thêm chất liệu mới')"
            class="btn btn-sm btn-primary"
            :disabled="loadingMaterials"
          >
            <i class="fas fa-plus"></i>
          </button>
        </div>
        <div v-if="loadingMaterials" class="text-center p-3">
          <div class="spinner-border" role="status">
            <span class="visually-hidden">Đang tải...</span>
          </div>
        </div>
        <ul v-else class="list-group list-group-flush">
          <li
            v-for="material in materials"
            :key="material.idChatLieu"
            class="list-group-item d-flex justify-content-between align-items-center"
          >
            {{ material.tenChatLieu }}
            <div>
              <button
                @click="openAttributeModal('material', 'Sửa chất liệu', material)"
                class="btn btn-sm btn-link text-primary"
              >
                <i class="fas fa-edit"></i>
              </button>
              <button
                @click="openDeleteModal('chất liệu', material.idChatLieu)"
                class="btn btn-sm btn-link text-danger"
              >
                <i class="fas fa-trash-alt"></i>
              </button>
            </div>
          </li>
          <li v-if="materials.length === 0" class="list-group-item text-center">
            Không có chất liệu nào.
          </li>
        </ul>
      </div>
    </div>
  </div>

  <!-- Modal cho Thêm/Sửa thuộc tính -->
  <div class="modal fade" id="attributeModal" tabindex="-1" aria-labelledby="attributeModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="attributeModalLabel">{{ modalTitle }}</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveAttribute">
            <div v-if="['color','size','brand','material'].includes(currentAttributeType)" class="mb-3">
              <label for="attributeCode" class="form-label">Mã {{ currentAttributeType }}</label>
              <input
                type="text"
                class="form-control"
                id="attributeCode"
                v-model="currentAttribute.code"
                required
              >
            </div>
            <div class="mb-3">
              <label for="attributeName" class="form-label">Tên thuộc tính</label>
              <input
                type="text"
                class="form-control"
                id="attributeName"
                v-model="currentAttribute.name"
                required
              />
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
          <button
            type="button"
            class="btn btn-primary"
            @click="saveAttribute"
            :disabled="savingAttribute"
          >
            {{ savingAttribute ? 'Đang lưu...' : 'Lưu' }}
          </button>
        </div>
      </div>
    </div>
  </div>

  <!-- Modal xác nhận Xóa -->
  <div class="modal fade" id="deleteAttributeModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Xác nhận Xóa</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
          Bạn có chắc chắn muốn xóa {{ itemToDelete.type }} này không?
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
          <button
            type="button"
            class="btn btn-danger"
            @click="confirmDelete"
            :disabled="deletingAttribute"
          >
            {{ deletingAttribute ? 'Đang xóa...' : 'Xóa' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue';
import { Modal } from 'bootstrap';
import axios from 'axios';
import { ElMessage } from 'element-plus';

// Dữ liệu
const categories = ref([]);
const colors = ref([]);
const sizes = ref([]);
const brands = ref([]);
const materials = ref([]);

const loadingCategories = ref(false);
const loadingColors = ref(false);
const loadingSizes = ref(false);
const loadingBrands = ref(false);
const loadingMaterials = ref(false);

const savingAttribute = ref(false);
const deletingAttribute = ref(false);

// Modal state
let attributeModalInstance = null;
let deleteModalInstance = null;
const modalTitle = ref('');
const currentAttributeType = ref('');
const currentAttribute = ref({
  id: null,
  name: '',
  code: '', // chỉ dùng cho màu sắc
});
const itemToDelete = reactive({
  type: '',
  id: null,
});

// Fetch functions
const fetchCategories = async () => {
  loadingCategories.value = true;
  try {
    const res = await axios.get('http://localhost:8080/api/danhmuc');
    categories.value = res.data || [];
  } catch {
    categories.value = [];
    ElMessage.error('Không thể tải danh mục');
  } finally {
    loadingCategories.value = false;
  }
};
const fetchColors = async () => {
  loadingColors.value = true;
  try {
    const res = await axios.get('http://localhost:8080/api/mausac');
    colors.value = res.data || [];
  } catch {
    colors.value = [];
    ElMessage.error('Không thể tải màu sắc');
  } finally {
    loadingColors.value = false;
  }
};
const fetchSizes = async () => {
  loadingSizes.value = true;
  try {
    const res = await axios.get('http://localhost:8080/api/size');
    sizes.value = res.data || [];
  } catch {
    sizes.value = [];
    ElMessage.error('Không thể tải size');
  } finally {
    loadingSizes.value = false;
  }
};
const fetchBrands = async () => {
  loadingBrands.value = true;
  try {
    const res = await axios.get('http://localhost:8080/api/thuonghieu');
    brands.value = res.data || [];
  } catch {
    brands.value = [];
    ElMessage.error('Không thể tải thương hiệu');
  } finally {
    loadingBrands.value = false;
  }
};
const fetchMaterials = async () => {
  loadingMaterials.value = true;
  try {
    const res = await axios.get('http://localhost:8080/api/chatlieu');
    materials.value = res.data || [];
  } catch {
    materials.value = [];
    ElMessage.error('Không thể tải chất liệu');
  } finally {
    loadingMaterials.value = false;
  }
};

const fetchAllAttributes = () =>
  Promise.all([fetchCategories(), fetchColors(), fetchSizes(), fetchBrands(), fetchMaterials()]);

// Modal open
const openAttributeModal = (type, title, attribute = null) => {
  modalTitle.value = title;
  currentAttributeType.value = type;
  if (attribute) {
    currentAttribute.value = {
      id: attribute?.idDM || attribute?.idMauSac || attribute?.idSize || attribute?.idThuongHieu || attribute?.idChatLieu || null,
      name: attribute?.tenDM || attribute?.tenMauSac || attribute?.tenSize || attribute?.tenThuongHieu || attribute?.tenChatLieu || '',
      code: attribute?.maMauSac || attribute?.maSize || attribute?.maThuongHieu || attribute?.maChatLieu || '',
    };
  } else {
    currentAttribute.value = { id: null, name: '', code: '' };
  }
  attributeModalInstance?.show();
};

// Save attribute
const saveAttribute = async () => {
  if (!currentAttribute.value.name) {
    ElMessage.error('Vui lòng nhập tên');
    return;
  }

  savingAttribute.value = true;
  try {
    let payload = {
      ...(currentAttribute.value.id && { id: currentAttribute.value.id }),
      trangThai: true,
    };

    switch (currentAttributeType.value) {
      case 'category':
        payload.tenDM = currentAttribute.value.name;
        break;
      case 'color':
        payload.maMauSac = currentAttribute.value.code;
        payload.tenMauSac = currentAttribute.value.name;
        break;
      case 'size':
        payload.maSize = currentAttribute.value.code;
        payload.tenSize = currentAttribute.value.name;
        break;
      case 'brand':
        payload.maThuongHieu = currentAttribute.value.code;
        payload.tenThuongHieu = currentAttribute.value.name;
        break;
      case 'material':
        payload.maChatLieu = currentAttribute.value.code;
        payload.tenChatLieu = currentAttribute.value.name;
        break;
    }

    let url = '';
    let method = currentAttribute.value.id ? 'put' : 'post';

    switch (currentAttributeType.value) {
      case 'category':
        url = `http://localhost:8080/api/danhmuc${currentAttribute.value.id ? '/' + currentAttribute.value.id : ''}`;
        break;
      case 'color':
        url = `http://localhost:8080/api/mausac${currentAttribute.value.id ? '/' + currentAttribute.value.id : ''}`;
        break;
      case 'size':
        url = `http://localhost:8080/api/size${currentAttribute.value.id ? '/' + currentAttribute.value.id : ''}`;
        break;
      case 'brand':
        url = `http://localhost:8080/api/thuonghieu${currentAttribute.value.id ? '/' + currentAttribute.value.id : ''}`;
        break;
      case 'material':
        url = `http://localhost:8080/api/chatlieu${currentAttribute.value.id ? '/' + currentAttribute.value.id : ''}`;
        break;
    }

    await axios[method](url, payload);
    ElMessage.success(currentAttribute.value.id ? 'Cập nhật thành công!' : 'Thêm mới thành công!');
    await fetchAllAttributes();
    attributeModalInstance?.hide();
  } catch (err) {
    ElMessage.error(err.response?.data?.message || 'Lỗi khi lưu');
    console.error(err);
  } finally {
    savingAttribute.value = false;
  }
};


// Delete
const openDeleteModal = (type, id) => {
  itemToDelete.type = type;
  itemToDelete.id = id;
  deleteModalInstance?.show();
};
const confirmDelete = async () => {
  deletingAttribute.value = true;
  try {
    let url = '';
    switch (itemToDelete.type) {
      case 'danh mục':
        url = `http://localhost:8080/api/danhmuc/${itemToDelete.id}`;
        break;
      case 'màu sắc':
        url = `http://localhost:8080/api/mausac/${itemToDelete.id}`;
        break;
      case 'size':
        url = `http://localhost:8080/api/size/${itemToDelete.id}`;
        break;
      case 'thương hiệu':
        url = `http://localhost:8080/api/thuonghieu/${itemToDelete.id}`;
        break;
      case 'chất liệu':
        url = `http://localhost:8080/api/chatlieu/${itemToDelete.id}`;
        break;
    }
    await axios.delete(url);
    ElMessage.success('Xóa thành công!');
    await fetchAllAttributes();
    deleteModalInstance?.hide();
  } catch (err) {
    ElMessage.error('Lỗi khi xóa');
    console.error(err);
  } finally {
    deletingAttribute.value = false;
  }
};

onMounted(() => {
  attributeModalInstance = new Modal(document.getElementById('attributeModal'));
  deleteModalInstance = new Modal(document.getElementById('deleteAttributeModal'));
  fetchAllAttributes();
});
onUnmounted(() => {
  attributeModalInstance?.dispose();
  deleteModalInstance?.dispose();
});
</script>

<style scoped>
.list-group-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
