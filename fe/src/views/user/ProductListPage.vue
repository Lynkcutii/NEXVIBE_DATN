```vue
<template>
  <div class="container py-4">
    <div class="row">
      <!-- 1. Sidebar Lọc -->
      <div class="col-lg-3">
        <div class="filter-sidebar position-sticky" style="top: 100px;">
          <h4 class="fw-bold mb-3">Bộ lọc</h4>

          <!-- Lọc theo Danh mục -->
          <div class="mb-4">
            <h6 class="filter-title">Nhóm sản phẩm</h6>
            <div class="form-check" v-for="cat in categories" :key="cat.idDM">
              <input class="form-check-input" type="checkbox" :id="`cat-${cat.idDM}`" 
                     :checked="filters.selectedCategories.some(c => c.idDM === cat.idDM)"
                     @change="selectCategory(cat)">
              <label class="form-check-label" :for="`cat-${cat.idDM}`">{{ cat.tenDM }}</label>
            </div>
          </div>

          <!-- Lọc theo Thương hiệu -->
          <div class="mb-4">
            <h6 class="filter-title">Thương hiệu</h6>
            <div class="form-check" v-for="brand in brands" :key="brand.idThuongHieu">
              <input class="form-check-input" type="checkbox" :id="`brand-${brand.idThuongHieu}`" 
                     :checked="filters.selectedBrands.some(b => b.idThuongHieu === brand.idThuongHieu)"
                     @change="selectBrand(brand)">
              <label class="form-check-label" :for="`brand-${brand.idThuongHieu}`">{{ brand.tenThuongHieu }}</label>
            </div>
          </div>

          <!-- Lọc theo Màu sắc -->
          <div class="mb-4">
            <h6 class="filter-title">Màu sắc</h6>
            <div class="form-check" v-for="color in colors" :key="color.idMauSac">
              <input class="form-check-input" type="checkbox" :id="`color-${color.idMauSac}`" 
                     :checked="filters.selectedColors.some(c => c.idMauSac === color.idMauSac)"
                     @change="selectColor(color)">
              <label class="form-check-label" :for="`color-${color.idMauSac}`">{{ color.tenMauSac }}</label>
            </div>
          </div>

          <!-- Lọc theo Chất liệu -->
          <div class="mb-4">
            <h6 class="filter-title">Chất liệu</h6>
            <div class="form-check" v-for="material in materials" :key="material.idChatLieu">
              <input class="form-check-input" type="checkbox" :id="`material-${material.idChatLieu}`" 
                     :checked="filters.selectedMaterials.some(m => m.idChatLieu === material.idChatLieu)"
                     @change="selectMaterial(material)">
              <label class="form-check-label" :for="`material-${material.idChatLieu}`">{{ material.tenChatLieu }}</label>
            </div>
          </div>

          <!-- Lọc theo Size -->
          <div class="mb-4">
            <h6 class="filter-title">Kích cỡ</h6>
            <div class="d-flex flex-wrap gap-2">
              <button v-for="size in sizes" :key="size.idSize" 
                      class="btn btn-sm btn-outline-dark size-btn"
                      :class="{ 'active': filters.selectedSizes.includes(size.tenSize) }"
                      @click="selectSize(size.tenSize)">{{ size.tenSize }}</button>
            </div>
          </div>

          <!-- Lọc theo Giá -->
          <div>
            <h6 class="filter-title">Mức giá</h6>
            <input type="range" class="form-range" min="0" max="2000000" step="100000" 
                   v-model="filters.priceRange[1]">
            <div class="d-flex justify-content-between small text-muted">
              <span>0đ</span>
              <span>{{ filters.priceRange[1].toLocaleString('vi-VN') }}đ</span>
            </div>
          </div>

          <button class="btn btn-dark w-100 mt-4" @click="applyFilters">Áp dụng</button>
        </div>
      </div>

      <!-- 2 & 3. Khu vực chính: Sắp xếp và Lưới sản phẩm -->
      <div class="col-lg-9">
        <!-- Breadcrumb và Tiêu đề -->
        <nav aria-label="breadcrumb">
          <ol class="breadcrumb">
            <li class="breadcrumb-item"><router-link to="/">Trang chủ</router-link></li>
            <li class="breadcrumb-item active" aria-current="page">Sản phẩm</li>
          </ol>
        </nav>
        <h1 class="fw-bolder">Đồ Thể Thao</h1>

        <!-- Khu vực sắp xếp -->
        <div class="d-flex justify-content-between align-items-center mb-4">
          <span class="text-muted">{{ pagination.total }} kết quả</span>
          <div class="d-flex align-items-center">
            <label class="form-label me-2 mb-0">Sắp xếp theo</label>
            <select class="form-select form-select-sm" style="width: 150px;" v-model="filters.sortBy" @change="applyFilters">
              <option value="newest">Mới nhất</option>
              <option value="price_asc">Giá: Tăng dần</option>
              <option value="price_desc">Giá: Giảm dần</option>
              <option value="bestselling">Bán chạy nhất</option>
            </select>
          </div>
        </div>

        <!-- Loading state -->
        <div v-if="loading" class="text-center py-5">
          <div class="spinner-border" role="status">
            <span class="visually-hidden">Đang tải...</span>
          </div>
        </div>

        <!-- Error state -->
        <div v-else-if="error" class="alert alert-danger" role="alert">
          {{ error }}
        </div>

        <!-- Lưới sản phẩm -->
        <div v-else class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
          <div class="col" v-for="product in products" :key="product.id">
            <ProductCard :product="product" />
          </div>
        </div>

        <!-- Nút Xem thêm & Phân trang -->
        <div v-if="!loading && !error && products.length > 0" class="text-center mt-5">
          <button v-if="products.length < pagination.total" 
                  class="btn btn-outline-dark btn-lg" 
                  @click="loadMore">XEM THÊM</button>
          <p class="text-muted mt-2">Hiển thị {{ products.length }} trên tổng số {{ pagination.total }} sản phẩm</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import ProductCard from '@/components/user/ProductCard.vue';
import axios from 'axios';

// API Base URL
const API_BASE_URL = 'http://localhost:8080/api';

// State
const products = ref([]);
const categories = ref([]);
const brands = ref([]);
const colors = ref([]);
const materials = ref([]);
const sizes = ref([]);
const loading = ref(false);
const error = ref(null);

// Filters
const filters = reactive({
  selectedCategories: [],
  selectedBrands: [],
  selectedColors: [],
  selectedMaterials: [],
  selectedSizes: [],
  priceRange: [0, 2000000],
  sortBy: 'newest'
});

// Pagination
const pagination = reactive({
  page: 0,
  size: 12,
  total: 0
});

// Computed
const selectedCategoryIds = computed(() => filters.selectedCategories.map(cat => cat.idDM));

// Methods
const loadCategories = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/danhmuc`);
    categories.value = response.data;
  } catch (err) {
    error.value = 'Không thể tải danh mục: ' + (err.response?.data?.message || err.message);
    console.error('Lỗi khi tải danh mục:', err);
  }
};

const loadBrands = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/thuonghieu`);
    brands.value = response.data;
  } catch (err) {
    error.value = 'Không thể tải thương hiệu: ' + (err.response?.data?.message || err.message);
    console.error('Lỗi khi tải thương hiệu:', err);
  }
};

const loadColors = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/mausac`);
    colors.value = response.data;
  } catch (err) {
    error.value = 'Không thể tải màu sắc: ' + (err.response?.data?.message || err.message);
    console.error('Lỗi khi tải màu sắc:', err);
  }
};

const loadMaterials = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/chatlieu`);
    materials.value = response.data;
  } catch (err) {
    error.value = 'Không thể tải chất liệu: ' + (err.response?.data?.message || err.message);
    console.error('Lỗi khi tải chất liệu:', err);
  }
};

const loadSizes = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/size`);
    sizes.value = response.data;
  } catch (err) {
    error.value = 'Không thể tải kích cỡ: ' + (err.response?.data?.message || err.message);
    console.error('Lỗi khi tải kích cỡ:', err);
  }
};

const loadProducts = async () => {
  loading.value = true;
  error.value = null;

  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      danhMuc: selectedCategoryIds.value.join(',') || null,
      thuongHieu: filters.selectedBrands.map(b => b.tenThuongHieu).join(',') || null,
      mauSac: filters.selectedColors.map(c => c.tenMauSac).join(',') || null,
      chatLieu: filters.selectedMaterials.map(m => m.tenChatLieu).join(',') || null,
      tenSize: filters.selectedSizes.join(',') || null,
      minPrice: filters.priceRange[0],
      maxPrice: filters.priceRange[1]
    };

    const response = await axios.get(`${API_BASE_URL}/sanphamchitiet/filter`, { 
      params,
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token') || ''}` // Thêm token nếu cần xác thực
      }
    });
    
    products.value = pagination.page === 0 
      ? response.data.content 
      : [...products.value, ...response.data.content];
    pagination.total = response.data.totalElements;
  } catch (err) {
    error.value = 'Không thể tải danh sách sản phẩm: ' + (err.response?.data?.message || err.message);
    console.error('Lỗi khi tải sản phẩm:', err);
  } finally {
    loading.value = false;
  }
};

const applyFilters = () => {
  pagination.page = 0;
  loadProducts();
};

const loadMore = () => {
  pagination.page++;
  loadProducts();
};

const selectCategory = (category) => {
  const index = filters.selectedCategories.findIndex(cat => cat.idDM === category.idDM);
  if (index > -1) {
    filters.selectedCategories.splice(index, 1);
  } else {
    filters.selectedCategories.push(category);
  }
};

const selectBrand = (brand) => {
  const index = filters.selectedBrands.findIndex(b => b.idThuongHieu === brand.idThuongHieu);
  if (index > -1) {
    filters.selectedBrands.splice(index, 1);
  } else {
    filters.selectedBrands.push(brand);
  }
};

const selectColor = (color) => {
  const index = filters.selectedColors.findIndex(c => c.idMauSac === color.idMauSac);
  if (index > -1) {
    filters.selectedColors.splice(index, 1);
  } else {
    filters.selectedColors.push(color);
  }
};

const selectMaterial = (material) => {
  const index = filters.selectedMaterials.findIndex(m => m.idChatLieu === material.idChatLieu);
  if (index > -1) {
    filters.selectedMaterials.splice(index, 1);
  } else {
    filters.selectedMaterials.push(material);
  }
};

const selectSize = (size) => {
  const index = filters.selectedSizes.indexOf(size);
  if (index > -1) {
    filters.selectedSizes.splice(index, 1);
  } else {
    filters.selectedSizes.push(size);
  }
};

// Lifecycle
onMounted(() => {
  loadCategories();
  loadBrands();
  loadColors();
  loadMaterials();
  loadSizes();
  loadProducts();
});
</script>

<style scoped>
.filter-sidebar {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
}

.filter-title {
  font-weight: 600;
  margin-bottom: 10px;
}

.size-btn.active {
  background-color: #343a40;
  color: white;
}

.btn-outline-dark:hover {
  background-color: #343a40;
  color: white;
}
</style>
```