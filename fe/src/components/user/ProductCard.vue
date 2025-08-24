```vue
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
    </div>
    <div class="card-footer bg-transparent border-0 pb-3">
      <button @click="handleAddToCart" class="btn btn-dark w-100">Thêm vào giỏ</button>
    </div>
  </div>
</template>

<script setup>
import { useCartStore } from '@/stores/cart';

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
});

const cart = useCartStore();

const handleAddToCart = () => {
  cart.addToCart(props.product, 1);
  alert(`Đã thêm "${props.product.tenSP || 'Không có tên'}" vào giỏ hàng!`);
};
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
</style>
```