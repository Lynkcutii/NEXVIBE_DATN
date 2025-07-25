<template>
  <div class="container py-5">
    <h1 class="text-center mb-5 fw-bold">Tin Tức & Cảm Hứng</h1>
    <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
      <div class="col" v-for="post in posts" :key="post.id">
        <div class="card h-100 shadow-sm border-0 blog-card">
          <img :src="post.imageUrl" class="card-img-top" :alt="post.title">
          <div class="card-body">
            <p class="text-muted small">{{ post.date }}</p>
            <h5 class="card-title fw-bold">{{ post.title }}</h5>
            <p class="card-text">{{ post.excerpt }}</p>
          </div>
          <div class="card-footer bg-transparent border-0 pb-3">
              <a href="#" class="btn btn-outline-dark">Đọc thêm</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref } from 'vue';
const posts = ref([
  { id: 1, title: '5 Bài Tập Đốt Mỡ Hiệu Quả Tại Nhà', excerpt: 'Không cần đến phòng gym, bạn vẫn có thể sở hữu vóc dáng săn chắc với những bài tập này.', imageUrl: 'https://images.unsplash.com/photo-1517836357463-d25dfeac3438?q=80&w=800', date: '29/06/2024' },
  { id: 2, title: 'Xu Hướng Thời Trang Athleisure 2024', excerpt: 'Khám phá cách phối đồ vừa thoải mái luyện tập, vừa tự tin xuống phố.', imageUrl: 'https://images.unsplash.com/photo-1541534741688-6078c6bfb5c5?q=80&w=800', date: '28/06/2024' },
  { id: 3, title: 'Bí Quyết Chọn Giày Chạy Bộ Phù Hợp', excerpt: 'Đừng để một đôi giày sai làm ảnh hưởng đến hành trình chinh phục những cung đường.', imageUrl: 'https://images.unsplash.com/photo-1473445391949-ac9065f5a89e?w=800', date: '25/06/2024' },
]);
</script>
<style scoped>
.blog-card { transition: all .3s ease; }
.blog-card:hover { transform: translateY(-5px); box-shadow: 0 .5rem 1rem rgba(0,0,0,.15)!important; }
</style>