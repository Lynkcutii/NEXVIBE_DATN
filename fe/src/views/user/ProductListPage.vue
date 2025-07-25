<template>
  <div class="container py-4">
    <div class="row">
      <!-- 1. Sidebar Lọc (Filter Sidebar) -->
      <div class="col-lg-3">
        <div class="filter-sidebar position-sticky" style="top: 100px;">
          <h4 class="fw-bold mb-3">Bộ lọc</h4>
          
          <!-- Lọc theo Danh mục -->
          <div class="mb-4">
            <h6 class="filter-title">Nhóm sản phẩm</h6>
            <div class="form-check" v-for="cat in filters.categories" :key="cat.id">
              <input class="form-check-input" type="checkbox" :id="`cat-${cat.id}`">
              <label class="form-check-label" :for="`cat-${cat.id}`">{{ cat.name }}</label>
            </div>
          </div>
          
          <!-- Lọc theo Size -->
          <div class="mb-4">
            <h6 class="filter-title">Kích cỡ</h6>
            <div class="d-flex flex-wrap gap-2">
                <button v-for="size in filters.sizes" :key="size" class="btn btn-sm btn-outline-dark size-btn">{{ size }}</button>
            </div>
          </div>

          <!-- Lọc theo Giá -->
          <div>
            <h6 class="filter-title">Mức giá</h6>
            <input type="range" class="form-range" min="0" max="2000000" step="100000">
            <div class="d-flex justify-content-between small text-muted">
              <span>0đ</span>
              <span>2.000.000đ</span>
            </div>
          </div>

          <button class="btn btn-dark w-100 mt-4">Áp dụng</button>
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
     

        <!-- Khu vực sắp xếp -->
        <div class="d-flex justify-content-between align-items-center mb-4">
          <span class="text-muted">{{ products.length }} kết quả</span>
          <div class="d-flex align-items-center">
            <label class="form-label me-2 mb-0">Sắp xếp theo</label>
            <select class="form-select form-select-sm" style="width: 150px;">
              <option selected>Mới nhất</option>
              <option value="1">Giá: Tăng dần</option>
              <option value="2">Giá: Giảm dần</option>
              <option value="3">Bán chạy nhất</option>
            </select>
          </div>
        </div>

        <!-- Lưới sản phẩm -->
        <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
          <div class="col" v-for="product in products" :key="product.id">
            <ProductCard :product="product" />
          </div>
        </div>
        
        <!-- Nút Xem thêm & Phân trang -->
        <div class="text-center mt-5">
            <button class="btn btn-outline-dark btn-lg">XEM THÊM</button>
            <p class="text-muted mt-2">Hiển thị 12 trên tổng số 258 sản phẩm</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import ProductCard from '@/components/user/ProductCard.vue';

// Dữ liệu mẫu cho bộ lọc
const filters = reactive({
  categories: [
    { id: 1, name: 'Áo Thun' }, { id: 2, name: 'Áo Polo' }, { id: 3, name: 'Quần Short' },
    { id: 4, name: 'Quần Lót' }, { id: 5, name: 'Áo Khoác' }, { id: 6, name: 'Quần Dài' },
  ],
  sizes: ['S', 'M', 'L', 'XL', '2XL', '3XL'],
});

// Dữ liệu mẫu cho sản phẩm
const products = ref([
    { id: 1, name: 'Áo Thun Thể Thao Coolmate Basics', price: 99000, slug: 'ao-thun-coolmate-basics', imageUrl: 'https://picsum.photos/400/400?random=10' },
    { id: 2, name: 'Quần Short 7-inch Đa Năng', price: 169000, slug: 'quan-short-7-inch', imageUrl: 'https://picsum.photos/400/400?random=11' },
    { id: 3, name: 'Áo Thun Cotton 220GSM Compact', price: 159000, slug: 'ao-thun-220gsm', imageUrl: 'https://picsum.photos/400/400?random=12' },
    { id: 4, name: 'Combo 3 Quần Lót Nam Trunk Excool', price: 289000, slug: 'combo-3-quan-lot-excool', imageUrl: 'https://picsum.photos/400/400?random=13' },
    { id: 5, name: 'Quần Short Thể Thao 5" Moving', price: 149000, slug: 'quan-short-5-moving', imageUrl: 'https://picsum.photos/400/400?random=14' },
    { id: 6, name: 'Áo Thun Thể Thao Melange Exdry', price: 189000, slug: 'ao-thun-melange-exdry', imageUrl: 'https://picsum.photos/400/400?random=15' },
]);
</script>

<style scoped>
.filter-title {
    font-weight: 600;
    margin-bottom: 1rem;
    font-size: 1rem;
}
.form-check-label {
    cursor: pointer;
}
.size-btn {
    width: 45px;
    height: 45px;
    display: flex;
    align-items: center;
    justify-content: center;
}
.breadcrumb-item a {
    text-decoration: none;
    color: var(--bs-primary);
}
.breadcrumb-item a:hover {
    text-decoration: underline;
}
</style>