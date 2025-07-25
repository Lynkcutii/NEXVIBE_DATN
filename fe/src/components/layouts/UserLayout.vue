<template>
  <div>
    <!-- 1. Thanh thông báo trên cùng -->
    <div class="top-bar text-center">
      <div class="container">
        <span>Miễn phí vận chuyển cho đơn hàng từ 200k</span>
      </div>
    </div>

    <!-- 2. Header chính (component riêng) -->
    <Header />

  


    <!-- 4. Nội dung chính của từng trang -->
    <main style="min-height: 70vh;">
      <router-view />
    </main>

    <!-- 5. Footer (component riêng) -->
    <Footer />
  </div>
</template>
<script setup>
import Header from '../user/Header.vue';
import Footer from '../user/Footer.vue';
</script>