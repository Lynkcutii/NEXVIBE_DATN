<template>
  <header class="main-header  border-bottom shadow-sm sticky-top">
    <div class="container">
      <nav class="navbar navbar-expand-lg">
        <!-- Logo -->
        <router-link to="/" class="navbar-brand">
          <img src="/img/logo/logo.jpeg" alt="Nexvibe Logo" height="100">
        </router-link>

        <!-- Menu chính -->
        <div class="collapse navbar-collapse" id="mainNav">
          <ul class="navbar-nav mx-auto">
            <li class="nav-item">
              <router-link to="/products?category=outlet" class="nav-link outlet-link">
                Sản Phẩm
              </router-link>
            </li>
            <li class="nav-item">
              <router-link to="/blog" class="nav-link">TIN TỨC</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/about" class="nav-link">VỀ NEXVIBE</router-link>
            </li>
          </ul>
        </div>
        
        <!-- Các nút chức năng bên phải -->
        <div class="d-flex align-items-center ms-lg-auto">
          <!-- Ô tìm kiếm -->
          <div class="search-box me-3">
            <input type="text" class="form-control" placeholder="Tìm kiếm sản phẩm...">
            <i class="fas fa-search search-icon"></i>
          </div>

          <!-- Icon Tài khoản -->
          <div class="dropdown text-end me-3">
            <a href="#" class="d-block link-dark text-decoration-none" data-bs-toggle="dropdown" aria-expanded="false">
              <i class="fas fa-user fs-4"></i>
            </a>
            <ul class="dropdown-menu text-small">
              <template v-if="auth.isAuthenticated">
                <li><span class="dropdown-item-text">Chào, <strong>{{ auth.userFullName }}</strong></span></li>
                <li><hr class="dropdown-divider"></li>
                <li><router-link class="dropdown-item" to="/order-history">Đơn hàng của tôi</router-link></li>
                <li><router-link class="dropdown-item" to="/profile">Tài khoản</router-link></li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item" href="#" @click.prevent="handleLogout">Đăng xuất</a></li>
              </template>
              <template v-else>
                <li><router-link to="/login" class="dropdown-item">Đăng nhập</router-link></li>
                <li><router-link to="/register" class="dropdown-item">Đăng ký</router-link></li>
              </template>
            </ul>
          </div>

          <!-- Icon Giỏ hàng -->
          <router-link to="/cart" class="position-relative link-dark">
            <i class="fas fa-shopping-bag fs-4"></i>
            <span v-if="cart.totalItems > 0" class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
              {{ cart.totalItems }}
            </span>
          </router-link>
        </div>

        <!-- Nút Toggler cho màn hình nhỏ -->
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav">
          <span class="navbar-toggler-icon"></span>
        </button>
      </nav>
    </div>
  </header>
</template>

<script setup>
import { useCartStore } from '@/stores/cart';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';
const cart = useCartStore();
const auth = useAuthStore();
const router = useRouter();
const handleLogout = () => {
    auth.logout();
    router.push({ name: 'home' });
};
</script>