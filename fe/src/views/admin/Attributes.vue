```vue
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
            {{ color.tenMauSac }}
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
            <div class="mb-3">
              <label for="attributeName" class="form-label">Tên thuộc tính</label>
              <input
                type="text"
                class="form-control"
                id="attributeName"
                v-model="currentAttribute.name"
                required
              >
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

// Dữ liệu cho các thuộc tính
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
});
const itemToDelete = reactive({
  type: '',
  id: null,
});

// Lấy danh sách danh mục
const fetchCategories = async () => {
  loadingCategories.value = true;
  try {
    const response = await axios.get('http://localhost:8080/api/danhmuc');
    categories.value = Array.isArray(response.data) ? response.data : [];
    if (categories.value.length === 0) {
      ElMessage.warning('Danh sách danh mục trống.');
    }
  } catch (error) {
    console.error('Lỗi khi lấy danh mục:', error);
    ElMessage.error('Không thể tải danh sách danh mục.');
    categories.value = [];
  } finally {
    loadingCategories.value = false;
  }
};

// Lấy danh sách màu sắc
const fetchColors = async () => {
  loadingColors.value = true;
  try {
    const response = await axios.get('http://localhost:8080/api/mausac');
    colors.value = Array.isArray(response.data) ? response.data : [];
    if (colors.value.length === 0) {
      ElMessage.warning('Danh sách màu sắc trống.');
    }
  } catch (error) {
    console.error('Lỗi khi lấy màu sắc:', error);
    ElMessage.error('Không thể tải danh sách màu sắc.');
    colors.value = [];
  } finally {
    loadingColors.value = false;
  }
};

// Lấy danh sách kích thước
const fetchSizes = async () => {
  loadingSizes.value = true;
  try {
    const response = await axios.get('http://localhost:8080/api/size');
    sizes.value = Array.isArray(response.data) ? response.data : [];
    if (sizes.value.length === 0) {
      ElMessage.warning('Danh sách kích thước trống.');
    }
  } catch (error) {
    console.error('Lỗi khi lấy kích thước:', error);
    ElMessage.error('Không thể tải danh sách kích thước.');
    sizes.value = [];
  } finally {
    loadingSizes.value = false;
  }
};

// Lấy danh sách thương hiệu
const fetchBrands = async () => {
  loadingBrands.value = true;
  try {
    const response = await axios.get('http://localhost:8080/api/thuonghieu');
    brands.value = Array.isArray(response.data) ? response.data : [];
    if (brands.value.length === 0) {
      ElMessage.warning('Danh sách thương hiệu trống.');
    }
  } catch (error) {
    console.error('Lỗi khi lấy thương hiệu:', error);
    ElMessage.error('Không thể tải danh sách thương hiệu.');
    brands.value = [];
  } finally {
    loadingBrands.value = false;
  }
};

// Lấy danh sách chất liệu
const fetchMaterials = async () => {
  loadingMaterials.value = true;
  try {
    const response = await axios.get('http://localhost:8080/api/chatlieu');
    materials.value = Array.isArray(response.data) ? response.data : [];
    if (materials.value.length === 0) {
      ElMessage.warning('Danh sách chất liệu trống.');
    }
  } catch (error) {
    console.error('Lỗi khi lấy chất liệu:', error);
    ElMessage.error('Không thể tải danh sách chất liệu.');
    materials.value = [];
  } finally {
    loadingMaterials.value = false;
  }
};

// Tải tất cả dữ liệu thuộc tính
const fetchAllAttributes = async () => {
  try {
    await Promise.all([
      fetchCategories(),
      fetchColors(),
      fetchSizes(),
      fetchBrands(),
      fetchMaterials(),
    ]);
  } catch (error) {
    console.error('Lỗi khi tải dữ liệu thuộc tính:', error);
    ElMessage.error('Không thể tải dữ liệu thuộc tính.');
  }
};

// Mở modal thêm/sửa thuộc tính
const openAttributeModal = (type, title, attribute = null) => {
  modalTitle.value = title;
  currentAttributeType.value = type;
  if (attribute) {
    // Chế độ sửa
    currentAttribute.value = {
      id: attribute.idDM || attribute.idMauSac || attribute.idSize || attribute.idThuongHieu || attribute.idChatLieu,
      name: attribute.tenDM || attribute.tenMauSac || attribute.tenSize || attribute.tenThuongHieu || attribute.tenChatLieu,
    };
  } else {
    // Chế độ thêm mới
    currentAttribute.value = { id: null, name: '' };
  }
  attributeModalInstance?.show();
};

// Lưu thuộc tính
const saveAttribute = async () => {
  if (!currentAttribute.value.name) {
    ElMessage.error('Vui lòng nhập tên thuộc tính.');
    return;
  }

  savingAttribute.value = true;
  try {
    const payload = {
      ...(currentAttribute.value.id && { id: currentAttribute.value.id }),
      ...(currentAttributeType.value === 'category' && { tenDM: currentAttribute.value.name }),
      ...(currentAttributeType.value === 'color' && { tenMauSac: currentAttribute.value.name }),
      ...(currentAttributeType.value === 'size' && { tenSize: currentAttribute.value.name }),
      ...(currentAttributeType.value === 'brand' && { tenThuongHieu: currentAttribute.value.name }),
      ...(currentAttributeType.value === 'material' && { tenChatLieu: currentAttribute.value.name }),
      trangThai: true, // Mặc định trạng thái là true
    };

    let url;
    let method;
    if (currentAttribute.value.id) {
      // Cập nhật
      method = 'put';
      switch (currentAttributeType.value) {
        case 'category':
          url = `http://localhost:8080/api/danhmuc/${currentAttribute.value.id}`;
          break;
        case 'color':
          url = `http://localhost:8080/api/mausac/${currentAttribute.value.id}`;
          break;
        case 'size':
          url = `http://localhost:8080/api/size/${currentAttribute.value.id}`;
          break;
        case 'brand':
          url = `http://localhost:8080/api/thuonghieu/${currentAttribute.value.id}`;
          break;
        case 'material':
          url = `http://localhost:8080/api/chatlieu/${currentAttribute.value.id}`;
          break;
      }
    } else {
      // Thêm mới
      method = 'post';
      switch (currentAttributeType.value) {
        case 'category':
          url = 'http://localhost:8080/api/danhmuc';
          break;
        case 'color':
          url = 'http://localhost:8080/api/mausac';
          break;
        case 'size':
          url = 'http://localhost:8080/api/size';
          break;
        case 'brand':
          url = 'http://localhost:8080/api/thuonghieu';
          break;
        case 'material':
          url = 'http://localhost:8080/api/chatlieu';
          break;
      }
    }

    await axios[method](url, payload);
    ElMessage.success(
      currentAttribute.value.id
        ? `Cập nhật ${currentAttributeType.value} thành công!`
        : `Thêm ${currentAttributeType.value} thành công!`
    );
    await fetchAllAttributes();
    attributeModalInstance?.hide();
  } catch (error) {
    console.error(`Lỗi khi lưu ${currentAttributeType.value}:`, error);
    ElMessage.error(
      error.response?.data?.message || `Lỗi khi lưu ${currentAttributeType.value}.`
    );
  } finally {
    savingAttribute.value = false;
  }
};

// Mở modal xóa
const openDeleteModal = (type, id) => {
  itemToDelete.type = type;
  itemToDelete.id = id;
  deleteModalInstance?.show();
};

// Xác nhận xóa thuộc tính
const confirmDelete = async () => {
  deletingAttribute.value = true;
  try {
    let url;
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
    ElMessage.success(`Xóa ${itemToDelete.type} thành công!`);
    await fetchAllAttributes();
    deleteModalInstance?.hide();
  } catch (error) {
    console.error(`Lỗi khi xóa ${itemToDelete.type}:`, error);
    ElMessage.error(
      error.response?.data?.message || `Lỗi khi xóa ${itemToDelete.type}.`
    );
  } finally {
    deletingAttribute.value = false;
  }
};

// Quản lý vòng đời Modal
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
```