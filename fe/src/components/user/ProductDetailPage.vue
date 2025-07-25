<template>
  <div class="row">
    <div class="col-md-6"><img :src="product.imageUrl" class="img-fluid rounded"></div>
    <div class="col-md-6">
        <h1>{{ product.name }}</h1>
        <p class="h3 fw-bold text-danger">{{ product.price.toLocaleString() }}đ</p>
        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>
        <hr>
        <div class="mb-3">
            <label class="form-label fw-bold">Màu sắc</label>
            <div>
                <button v-for="color in product.colors" :key="color" class="btn btn-outline-dark me-2">{{ color }}</button>
            </div>
        </div>
        <div class="mb-3">
            <label class="form-label fw-bold">Size</label>
            <div>
                <button v-for="size in product.sizes" :key="size" class="btn btn-outline-dark me-2">{{ size }}</button>
            </div>
        </div>
        <div class="d-flex align-items-center mt-4">
            <input type="number" class="form-control me-3" style="width: 80px;" value="1" min="1">
            <button @click="handleAddToCart" class="btn btn-primary btn-lg flex-grow-1"><i class="fas fa-cart-plus me-2"></i>Thêm vào giỏ</button>
        </div>
    </div>
  </div>
</template>
<script setup>
import { ref } from 'vue';
import { useCartStore } from '@/stores/cart';
const cart = useCartStore();
const props = defineProps(['slug']);
const product = ref({ // Dữ liệu này sẽ được lấy từ API dựa trên props.slug
    id: 1, name: 'Áo Thun Thể Thao', price: 250000, slug: 'ao-thun-the-thao', imageUrl: 'https://i.imgur.com/3pWjC0a.png',
    colors: ['Đen', 'Trắng', 'Xanh'], sizes: ['S', 'M', 'L', 'XL']
});
const handleAddToCart = () => {
    cart.addToCart(product.value);
    alert('Đã thêm sản phẩm vào giỏ hàng!');
}
</script>