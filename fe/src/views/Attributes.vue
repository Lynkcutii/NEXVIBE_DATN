<template>
  <h1 class="h3 mb-4 text-gray-800">Quản lý Thuộc tính</h1>
  <div class="row">
    <!-- Card Màu sắc -->
    <div class="col-md-6 col-lg-4 mb-4">
      <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
          <span>Màu sắc</span>
          <!-- Nút Thêm, gọi hàm mở modal -->
          <button @click="openAttributeModal('color', 'Thêm màu sắc mới')" class="btn btn-sm btn-primary"><i class="fas fa-plus"></i></button>
        </div>
        <ul class="list-group list-group-flush">
          <li v-for="color in colors" :key="color.id" class="list-group-item d-flex justify-content-between align-items-center">
            <div>
              <span class="badge me-2" :style="{ backgroundColor: color.hex }"> </span>
              {{ color.name }}
            </div>
            <div>
              <button @click="openAttributeModal('color', 'Sửa màu sắc', color)" class="btn btn-sm btn-link text-primary"><i class="fas fa-edit"></i></button>
              <button @click="openDeleteModal('màu sắc', color.id)" class="btn btn-sm btn-link text-danger"><i class="fas fa-trash-alt"></i></button>
            </div>
          </li>
        </ul>
      </div>
    </div>

    <!-- Card Size -->
    <div class="col-md-6 col-lg-4 mb-4">
       <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
          <span>Size</span>
          <button @click="openAttributeModal('size', 'Thêm size mới')" class="btn btn-sm btn-primary"><i class="fas fa-plus"></i></button>
        </div>
        <ul class="list-group list-group-flush">
          <li v-for="size in sizes" :key="size.id" class="list-group-item d-flex justify-content-between align-items-center">
            {{ size.name }}
            <div>
              <button @click="openAttributeModal('size', 'Sửa size', size)" class="btn btn-sm btn-link text-primary"><i class="fas fa-edit"></i></button>
              <button @click="openDeleteModal('size', size.id)" class="btn btn-sm btn-link text-danger"><i class="fas fa-trash-alt"></i></button>
            </div>
          </li>
        </ul>
      </div>
    </div>

    <!-- Card Thương hiệu -->
    <div class="col-md-6 col-lg-4 mb-4">
       <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
          <span>Thương hiệu</span>
          <button @click="openAttributeModal('brand', 'Thêm thương hiệu mới')" class="btn btn-sm btn-primary"><i class="fas fa-plus"></i></button>
        </div>
        <ul class="list-group list-group-flush">
          <li v-for="brand in brands" :key="brand.id" class="list-group-item d-flex justify-content-between align-items-center">
            {{ brand.name }}
            <div>
              <button @click="openAttributeModal('brand', 'Sửa thương hiệu', brand)" class="btn btn-sm btn-link text-primary"><i class="fas fa-edit"></i></button>
              <button @click="openDeleteModal('thương hiệu', brand.id)" class="btn btn-sm btn-link text-danger"><i class="fas fa-trash-alt"></i></button>
            </div>
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
              <input type="text" class="form-control" id="attributeName" v-model="currentAttribute.name" required>
            </div>
            <!-- Hiển thị thêm ô nhập mã màu nếu là thuộc tính màu sắc -->
            <div v-if="currentAttributeType === 'color'" class="mb-3">
              <label for="attributeHex" class="form-label">Mã màu (Hex)</label>
              <input type="text" class="form-control" id="attributeHex" v-model="currentAttribute.hex" placeholder="#FFFFFF">
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
          <button type="button" class="btn btn-primary" @click="saveAttribute">Lưu</button>
        </div>
      </div>
    </div>
  </div>
  
  <!-- Modal xác nhận Xóa -->
  <div class="modal fade" id="deleteAttributeModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header"><h5 class="modal-title">Xác nhận Xóa</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
            <div class="modal-body">Bạn có chắc chắn muốn xóa {{ itemToDelete.type }} này không?</div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                <button type="button" class="btn btn-danger" @click="confirmDelete">Xóa</button>
            </div>
        </div>
    </div>
  </div>

</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue';
import { Modal } from 'bootstrap';

// Dữ liệu mẫu
const colors = ref([
  { id: 1, name: 'Xanh Dương', hex: '#0d6efd' },
  { id: 2, name: 'Đen', hex: '#000000' },
  { id: 3, name: 'Trắng', hex: '#ffffff' },
]);
const sizes = ref([
  { id: 1, name: 'S' }, { id: 2, name: 'M' }, { id: 3, name: 'L' }, { id: 4, name: 'XL' }
]);
const brands = ref([
  { id: 1, name: 'Nexvibe' }, { id: 2, name: 'Nike' }, { id: 3, name: 'Adidas' }
]);

// --- Logic cho Modal Thêm/Sửa ---
let attributeModalInstance = null;
const modalTitle = ref('');
const currentAttributeType = ref('');
const currentAttribute = ref({ id: null, name: '', hex: '' });

const openAttributeModal = (type, title, attribute = null) => {
  modalTitle.value = title;
  currentAttributeType.value = type;
  if (attribute) {
    // Chế độ sửa
    currentAttribute.value = { ...attribute };
  } else {
    // Chế độ thêm mới
    currentAttribute.value = { id: null, name: '', hex: '' };
  }
  attributeModalInstance?.show();
};

const saveAttribute = () => {
  if (currentAttribute.value.id) {
    // Logic cập nhật (sau này gọi API)
    console.log(`Updating ${currentAttributeType.value}:`, currentAttribute.value);
  } else {
    // Logic thêm mới (sau này gọi API)
    console.log(`Adding new ${currentAttributeType.value}:`, currentAttribute.value);
  }
  attributeModalInstance?.hide();
};

// --- Logic cho Modal Xóa ---
let deleteModalInstance = null;
const itemToDelete = reactive({ type: '', id: null });

const openDeleteModal = (type, id) => {
    itemToDelete.type = type;
    itemToDelete.id = id;
    deleteModalInstance?.show();
}

const confirmDelete = () => {
    console.log(`Deleting ${itemToDelete.type} with id: ${itemToDelete.id}`);
    // Logic gọi API xóa
    deleteModalInstance?.hide();
}


// --- Quản lý vòng đời Modal ---
onMounted(() => {
  attributeModalInstance = new Modal(document.getElementById('attributeModal'));
  deleteModalInstance = new Modal(document.getElementById('deleteAttributeModal'));
});

onUnmounted(() => {
  attributeModalInstance?.dispose();
  deleteModalInstance?.dispose();
});

</script>