<template>
  <div>
    <!-- Banner -->
    <div class="p-5 mb-4 bg-light rounded-3">
        <div class="container-fluid py-5">
            <h1 class="display-5 fw-bold">NEXVIBE Summer Collection</h1>
            <p class="col-md-8 fs-4">Nâng tầm phong cách, bứt phá giới hạn. Khám phá ngay bộ sưu tập mới nhất dành cho mùa hè năng động.</p>
            <router-link to="/products" class="btn btn-primary btn-lg">Mua sắm ngay</router-link>
        </div>
    </div>
    <!-- Sản phẩm nổi bật -->
    <h2 class="mb-4">Sản phẩm nổi bật</h2>
    <div class="row row-cols-1 row-cols-md-2 row-cols-lg-4 g-4">
        <div class="col" v-for="product in featuredProducts" :key="product.id">
            <div class="card h-100">
                <img :src="product.imageUrl" class="card-img-top" alt="...">
                <div class="card-body">
                    <h5 class="card-title">{{ product.name }}</h5>
                    <p class="card-text fw-bold text-danger">{{ product.price.toLocaleString() }}đ</p>
                </div>
                <div class="card-footer bg-transparent border-0 pb-3">
                    <router-link :to="`/products/${product.slug}`" class="btn btn-dark w-100">Xem chi tiết</router-link>
                </div>
            </div>
        </div>
    </div>
  </div>
</template>
<script setup>
import { ref } from 'vue';
const featuredProducts = ref([
    { id: 1, name: 'Áo Thun Thể Thao', price: 250000, slug: 'ao-thun-the-thao', imageUrl: 'https://i.imgur.com/3pWjC0a.png' },
    { id: 2, name: 'Áo Hoodie Nỉ Bông', price: 550000, slug: 'ao-hoodie-ni-bong', imageUrl: 'https://i.imgur.com/fG0m92F.png' },
    { id: 3, name: 'Quần Jogger', price: 320000, slug: 'quan-jogger', imageUrl: 'https://i.imgur.com/xT5A0if.png' },
    { id: 4, name: 'Áo Ba Lỗ Gym', price: 180000, slug: 'ao-ba-lo-gym', imageUrl: 'https://i.imgur.com/GHYG6pP.png' },
]);
</script>