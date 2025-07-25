<template>
  <div>
    <h1 class="mb-4 fw-bold">Giỏ hàng của bạn</h1>
    
    <!-- Kiểm tra nếu có sản phẩm trong giỏ hàng -->
    <div v-if="cart.items.length > 0" class="row g-5">
      <!-- Cột trái: Danh sách sản phẩm -->
      <div class="col-lg-8">
        <div v-for="item in cart.items" :key="item.uniqueId" class="card mb-3 shadow-sm">
          <div class="card-body">
            <div class="row align-items-center">
              <div class="col-md-2">
                <img :src="item.imageUrl" class="img-fluid rounded" :alt="item.name">
              </div>
              <div class="col-md-5">
                <h5 class="mb-1">{{ item.name }}</h5>
                <p class="text-muted small mb-2">
                  Màu: <strong>{{ item.variant.color }}</strong> / Size: <strong>{{ item.variant.size }}</strong>
                </p>
                <button @click="cart.removeFromCart(item.uniqueId)" class="btn btn-sm btn-outline-danger">
                  <i class="fas fa-trash-alt me-1"></i> Xóa
                </button>
              </div>
              <div class="col-md-2 mt-3 mt-md-0">
                <input type="number" class="form-control form-control-sm text-center" v-model.number="item.quantity" min="1">
              </div>
              <div class="col-md-3 text-md-end mt-3 mt-md-0 fw-bold">
                {{ (item.price * item.quantity).toLocaleString() }}đ
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Cột phải: Tóm tắt đơn hàng -->
      <div class="col-lg-4">
        <div class="card position-sticky" style="top: 100px;">
          <div class="card-body">
            <h5 class="card-title mb-3">Tóm tắt đơn hàng</h5>
            <ul class="list-group list-group-flush">
              <li class="list-group-item d-flex justify-content-between px-0">
                <span>Tạm tính</span>
                <span>{{ cart.subTotal.toLocaleString() }}đ</span>
              </li>
              <li class="list-group-item d-flex justify-content-between px-0">
                <span>Phí vận chuyển</span>
                <span>Miễn phí</span>
              </li>
              <li class="list-group-item d-flex justify-content-between fw-bold h5 px-0 mt-2">
                <span>Tổng cộng</span>
                <span>{{ cart.subTotal.toLocaleString() }}đ</span>
              </li>
            </ul>
            <router-link to="/checkout" class="btn btn-primary w-100 mt-3 btn-lg">Tiến hành thanh toán</router-link>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Hiển thị khi giỏ hàng trống -->
    <div v-else class="text-center p-5 bg-light rounded">
      <i class="fas fa-shopping-cart fa-4x text-muted mb-3"></i>
      <h4>Giỏ hàng của bạn đang trống</h4>
      <p>Hãy khám phá các sản phẩm tuyệt vời của chúng tôi!</p>
      <router-link to="/products" class="btn btn-dark">Tiếp tục mua sắm</router-link>
    </div>
  </div>
</template>

<script setup>
// 1. Import store quản lý giỏ hàng
import { useCartStore } from '@/stores/cart';

// 2. Khởi tạo store để có thể truy cập dữ liệu
const cart = useCartStore();
</script>