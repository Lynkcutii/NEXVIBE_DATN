<template>
  <div class="card h-100 product-card border-0 shadow-sm">
    <router-link :to="`/products/${product.slug}`">
      <img :src="product.imageUrl" class="card-img-top" :alt="product.name">
    </router-link>
    <div class="card-body d-flex flex-column">
      <h5 class="card-title flex-grow-1">
        <router-link :to="`/products/${product.slug}`" class="text-dark text-decoration-none product-name">
          {{ product.name }}
        </router-link>
      </h5>
      <p class="card-text fw-bold text-danger fs-5 mb-2">{{ product.price.toLocaleString() }}đ</p>
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
    // Giả sử thêm biến thể mặc định khi click nhanh
    cart.addToCart(props.product, 1, { size: 'M', color: 'Đen' });
    alert(`Đã thêm "${props.product.name}" vào giỏ hàng!`);
}
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
    color: #0d6efd !important; /* Màu xanh primary của Bootstrap */
}
.card-img-top {
    aspect-ratio: 1 / 1; /* Giữ ảnh luôn vuông */
    object-fit: cover;
}
</style>