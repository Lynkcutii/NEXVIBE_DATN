<template>
  <div class="card h-100 product-card border-0 shadow-sm">
    <router-link :to="`/products/${product.idSP}`">
      <img 
        :src="product.imageLink || '@/assets/placeholder-image.png'" 
        class="card-img-top" 
        :alt="product.tenSP || 'Không có tên'"
      >
    </router-link>
    <div class="card-body d-flex flex-column">
      <h5 class="card-title flex-grow-1">
        <router-link :to="`/products/${product.idSP}`" class="text-dark text-decoration-none product-name">
          {{ product.tenSP || 'Không có tên' }}
        </router-link>
      </h5>
      
      <p class="card-text fw-bold text-danger fs-5 mb-2">
        <span v-if="product.gia !== undefined && product.gia !== null">
          {{ product.gia.toLocaleString('vi-VN') }}đ
        </span>
        <span v-else>Liên hệ</span>
      </p>

      <!-- Available Colors -->
      <div v-if="availableColors.length > 0" class="mb-2">
        <small class="text-muted d-block mb-1">Màu sắc:</small>
        <div class="d-flex flex-wrap gap-1">
          <span 
            v-for="color in availableColors.slice(0, 4)" 
            :key="color"
            class="badge bg-light text-dark border small-badge"
            :title="color"
          >
            {{ color }}
          </span>
          <span v-if="availableColors.length > 4" class="badge bg-secondary small-badge">
            +{{ availableColors.length - 4 }}
          </span>
        </div>
      </div>

      <!-- Available Sizes -->
      <div v-if="availableSizes.length > 0" class="mb-2">
        <small class="text-muted d-block mb-1">Kích thước:</small>
        <div class="d-flex flex-wrap gap-1">
          <span 
            v-for="size in availableSizes.slice(0, 4)" 
            :key="size"
            class="badge bg-light text-dark border small-badge"
            :title="size"
          >
            {{ size }}
          </span>
          <span v-if="availableSizes.length > 4" class="badge bg-secondary small-badge">
            +{{ availableSizes.length - 4 }}
          </span>
        </div>
      </div>

      <!-- Stock Status -->
      <div class="mb-2">
        <small class="text-muted">Tồn kho: </small>
        <span :class="stockClass">{{ stockText }}</span>
      </div>
    </div>
    
    
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useCartStore } from '@/stores/cart';
import axios from 'axios';

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
});

const cart = useCartStore();
const variants = ref([]);
const loading = ref(false);

// API Base URL
const API_BASE_URL = 'http://localhost:8080/api';

// Computed properties for colors and sizes
const availableColors = computed(() => {
  const colors = variants.value
    .filter(variant => variant.trangThai && variant.soLuong > 0)
    .map(variant => variant.tenMauSac)
    .filter(color => color);
  return [...new Set(colors)]; // Remove duplicates
});

const availableSizes = computed(() => {
  const sizes = variants.value
    .filter(variant => variant.trangThai && variant.soLuong > 0)
    .map(variant => variant.tenSize)
    .filter(size => size);
  return [...new Set(sizes)]; // Remove duplicates
});

const totalStock = computed(() => {
  return variants.value
    .filter(variant => variant.trangThai)
    .reduce((total, variant) => total + (variant.soLuong || 0), 0);
});

const hasStock = computed(() => totalStock.value > 0);

const stockText = computed(() => {
  if (totalStock.value === 0) return 'Hết hàng';
  if (totalStock.value < 10) return `${totalStock.value} sản phẩm`;
  return 'Còn hàng';
});

const stockClass = computed(() => {
  if (totalStock.value === 0) return 'text-danger small';
  if (totalStock.value < 10) return 'text-warning small';
  return 'text-success small';
});

// Load product variants
const loadVariants = async () => {
  if (!props.product?.idSP) return;
  
  loading.value = true;
  try {
    const response = await axios.get(`${API_BASE_URL}/sanphamchitiet/bySanPham/${props.product.idSP}`);
    variants.value = response.data || [];
  } catch (error) {
    console.error('Error loading product variants:', error);
    variants.value = [];
  } finally {
    loading.value = false;
  }
};

// Load variants when component mounts
onMounted(() => {
  loadVariants();
});
</script>

<style scoped>
.product-card { 
  transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out; 
}
.product-card:hover { 
  transform: translateY(-5px); 
  box-shadow: 0 .5rem 1rem rgba(0,0,0,.15)!important; 
}
.product-name {
  transition: color 0.2s ease-in-out;
}
.product-name:hover {
  color: #0d6efd !important;
}
.card-img-top {
  aspect-ratio: 1 / 1;
  object-fit: cover;
}

.small-badge {
  font-size: 0.7rem;
  padding: 0.25em 0.5em;
}

.btn:disabled {
  cursor: not-allowed;
}

/* Loading state */
.loading {
  opacity: 0.7;
}
</style>