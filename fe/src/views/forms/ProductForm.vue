<!-- src/views/forms/ProductForm.vue -->
<template>
  <h1 class="h3 mb-4 text-gray-800">{{ isEditing ? `Chỉnh sửa: ${product.name}` : 'Thêm Sản phẩm mới' }}</h1>
  
  <form @submit.prevent="saveProduct">
    <div class="row">
      <!-- Cột trái: Thông tin chung & Biến thể -->
      <div class="col-lg-8">
        <!-- Card Thông tin chung -->
        <div class="card shadow-sm mb-4">
          <div class="card-header"><h6 class="m-0 fw-bold text-primary">Thông tin chung</h6></div>
          <div class="card-body">
            <div class="row">
                <div class="col-md-8">
                    <div class="mb-3">
                        <label for="productName" class="form-label">Tên sản phẩm</label>
                        <input type="text" class="form-control" id="productName" v-model="product.name" required>
                    </div>
                </div>
                <!-- ========================================================== -->
                <!-- THÊM TRƯỜNG "MÃ SẢN PHẨM" VÀO ĐÂY -->
                <!-- ========================================================== -->
                <div class="col-md-4">
                    <div class="mb-3">
                        <label for="productCode" class="form-label">Mã sản phẩm</label>
                        <input type="text" class="form-control" id="productCode" v-model="product.code" placeholder="Ví dụ: AO-001">
                    </div>
                </div>
            </div>
            <div class="mb-3">
              <label for="productDescription" class="form-label">Mô tả</label>
              <textarea class="form-control" id="productDescription" rows="5" v-model="product.description"></textarea>
            </div>
          </div>
        </div>

        <!-- Card Biến thể Sản phẩm -->
        <div class="card shadow-sm mb-4">
          <!-- ... nội dung card biến thể giữ nguyên ... -->
        </div>
      </div>
      
      <!-- Cột phải: Tổ chức sản phẩm & Trạng thái -->
      <div class="col-lg-4">
        <!-- ... nội dung cột phải giữ nguyên ... -->
      </div>
    </div>
    
    <div class="d-flex justify-content-end gap-2 mt-3">
      <router-link :to="{ name: 'admin.products.list' }" class="btn btn-secondary">Hủy</router-link>
      <button type="submit" class="btn btn-primary">Lưu sản phẩm</button>
    </div>
  </form>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const isEditing = computed(() => !!route.params.id);

// Thêm trường "code" vào dữ liệu mẫu
const product = ref({
  name: '',
  code: '', // <-- THÊM VÀO ĐÂY
  description: '',
  isActive: true,
  categoryId: null,
  brandId: null,
  variants: [
    { color: 'Đen', size: 'M', price: 350000, stock: 100, sku: 'NEX-T01-BL-M', imageUrl: '' }
  ]
});

// ... các hàm addVariant, removeVariant giữ nguyên ...

onMounted(() => {
  if (isEditing.value) {
    console.log(`Lấy dữ liệu cho sản phẩm ID: ${route.params.id}`);
    // Giả lập dữ liệu cho sản phẩm đã có
    product.value = {
      name: 'Áo Thun Năng Động',
      code: 'NEX-T01', // <-- THÊM VÀO ĐÂY
      description: 'Mô tả chi tiết...',
      isActive: true,
      categoryId: 1,
      brandId: 1,
      variants: [
        { color: 'Đen', size: 'M', price: 350000, stock: 100, sku: 'NEX-T01-BL-M', imageUrl: '' },
        { color: 'Đen', size: 'L', price: 350000, stock: 80, sku: 'NEX-T01-BL-L', imageUrl: '' },
        { color: 'Trắng', size: 'M', price: 350000, stock: 120, sku: 'NEX-T01-WH-M', imageUrl: '' },
      ]
    };
  }
});

const saveProduct = () => {
  console.log('Đang lưu sản phẩm:', product.value);
  router.push({ name: 'admin.products.list' });
};
</script>