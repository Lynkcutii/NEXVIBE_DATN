<template>
  <div>
    <!-- 1. Hero Banner: Khối này giờ sẽ chiếm toàn bộ chiều rộng -->
    <swiper :modules="[Navigation, Pagination, Autoplay]" :slides-per-view="1" navigation loop :autoplay="{ delay: 4000, disableOnInteraction: false }">
      <swiper-slide v-for="banner in heroBanners" :key="banner.id">
        <div class="hero-banner" :style="{ backgroundImage: `url(${banner.imageUrl})` }">
          <div class="container text-white">
            <div class="row">
              <div class="col-lg-8 text-center text-lg-start">
                  <h1 class="display-2 fw-bolder">{{ banner.title }}</h1>
                  <p class="lead">{{ banner.subtitle }}</p>
                  <router-link to="/products" class="btn btn-light btn-lg mt-3">
                      MUA NGAY <i class="fas fa-arrow-right ms-2"></i>
                  </router-link>
              </div>
            </div>
          </div>
        </div>
      </swiper-slide>
    </swiper>
  
    <!-- 2. Thanh hiệu ứng chạy chữ -->
    <div class="marquee-container bg-primary text-white">
        <div class="marquee-content">
            <span>NEXVIBE - PERFORMANCE WEAR</span><i class="fas fa-bolt mx-3"></i>
            <span>COOL & ACTIVE</span><i class="fas fa-bolt mx-3"></i>
            <span>NEXVIBE - PERFORMANCE WEAR</span><i class="fas fa-bolt mx-3"></i>
            <span>COOL & ACTIVE</span><i class="fas fa-bolt mx-3"></i>
        </div>
    </div>
    
    <div class="container py-5">
      <!-- 3. KHỐI DANH MỤC MÔN THỂ THAO (THÊM VÀO ĐÂY) -->
    <!-- ========================================================== -->
    <div class="container text-center py-5">
      <!-- <h2 class="mb-5 fw-bold">MUA SẮM THEO MÔN THỂ THAO</h2> -->
      <div class="row">
        <!-- Dùng v-for để lặp qua mảng categories -->
        <div class="col" v-for="category in categories" :key="category.name">
          <router-link to="/products" class="text-dark text-decoration-none category-item">
            <!-- Thẻ div này là khung tròn có ảnh nền -->
            <div 
              class="category-circle" 
              :style="{ backgroundImage: `url(${category.iconUrl})` }">
            </div>
            <!-- Tên danh mục -->
            <span class="category-name">{{ category.name }}</span>
          </router-link>
        </div>
      </div>
    </div>
      
      <!-- 4. Khối quảng cáo theo giới tính -->
      <div class="row g-4 mb-5">
          <div class="col-md-6">
              <div class="promo-banner-small" style="background-image: url('/img/banner/a1.jpg')">
                  <div class="content"><h3>MEN WEAR</h3><router-link to="/products" class="btn btn-light">KHÁM PHÁ</router-link></div>
              </div>
          </div>
          <div class="col-md-6">
              <div class="promo-banner-small" style="background-image: url('/img/banner/a2.jpg')">
                   <div class="content"><h3>WOMEN ACTIVE</h3><router-link to="/products" class="btn btn-light">KHÁM PHÁ</router-link></div>
              </div>
          </div>
      </div>

      <!-- 5. Khối quảng cáo theo bộ sưu tập -->
      <div class="promo-banner-large mb-5" style="background-image: url('/img/banner/b5.jpg')">
          <div class="content text-white">
              <h2 class="display-4 fw-bold">CASUAL WEAR COLLECTION</h2>
              <p>Thoải mái mỗi ngày, phong cách mọi lúc.</p>
              <router-link to="/products" class="btn btn-light">MUA NGAY</router-link>
          </div>
      </div>

      <!-- 6. Lưới sản phẩm -->
      <h2 class="text-center mb-4 fw-bold">SẢN PHẨM MỚI NHẤT</h2>
      <div class="row row-cols-1 row-cols-md-2 row-cols-lg-4 g-4">
        <div class="col" v-for="product in products" :key="product.id">
            <ProductCard :product="product" />
        </div>
      </div>

      <!-- 7. Khối Cam kết/Giá trị cốt lõi
      <div class="row g-0 bg-light my-5 rounded-4 overflow-hidden align-items-center">
          <div class="col-md-6"><img src="https://picsum.photos/800/600?random=35" class="img-fluid"></div>
          <div class="col-md-6 p-5">
              <h2 class="fw-bold">GÓP PHẦN MANG LẠI<br>CUỘC SỐNG TƯƠI ĐẸP</h2>
              <p class="text-muted">Mỗi sản phẩm bạn mua tại NEXVIBE sẽ góp một phần vào quỹ hỗ trợ trẻ em vùng cao có một mùa đông ấm áp hơn.</p>
              <button class="btn btn-dark">TÌM HIỂU THÊM</button>
          </div>
      </div> -->

      <!-- 8. Khối Ưu đãi thành viên -->
      <h2 class="text-center m-5 fw-bold">ĐẶC QUYỀN THÀNH VIÊN NEXCLUB</h2>
      <div class="row g-4 text-center">
          <div class="col-md-4"><div class="p-4 bg-primary-subtle rounded-4"><h4>Hoàn tiền đến 10%</h4><p>Tích lũy NexCoin cho mọi đơn hàng.</p></div></div>
          <div class="col-md-4"><div class="p-4 bg-primary-subtle rounded-4"><h4>Quà tặng sinh nhật</h4><p>Nhận ngay voucher đặc biệt trong tháng sinh nhật.</p></div></div>
          <div class="col-md-4"><div class="p-4 bg-primary-subtle rounded-4"><h4>Miễn phí vận chuyển</h4><p>Cho tất cả các đơn hàng từ 199k.</p></div></div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { Swiper, SwiperSlide } from 'swiper/vue';
import { Navigation, Pagination, Autoplay } from 'swiper/modules';
import ProductCard from '@/components/user/ProductCard.vue';
const heroBanners = ref([
    { id: 1, title: 'RA MẮT BỘ SƯU TẬP BÓNG ĐÁ', subtitle: 'Tự tin trên mọi mặt sân.', imageUrl: '/img/banner/b1.png' },
    { id: 2, title: 'RUNNING COLLECTION', subtitle: 'Chinh phục mọi cung đường.', imageUrl: '/img/banner/b4.jpg' },
    { id: 3, title: 'RA MẮT BỘ SƯU TẬP CẦU LÔNG', subtitle: 'Linh hoạt ghi điểm mọi cú đánh.', imageUrl: '/img/banner/b2.jpg' },
    { id: 4,  imageUrl: '/img/banner/b3.jpg' },
]);
const categories = ref([
    { name: 'Bóng đá', iconUrl: '/img/banner/b9.jpg'}, 
    { name: 'Cầu lông', iconUrl: '/img/banner/b10.jpg' },
    { name: 'Chạy bộ', iconUrl: '/img/banner/b8.jpg' },
    { name: 'Pickleball', iconUrl: '/img/banner/b7.jpg' },
    { name: 'Tập gym', iconUrl: '/img/banner/b6.jpg' },
]);
const products = ref([
    { id: 1, name: 'Áo Thun Năng Động', price: 350000, slug: 'ao-thun-nang-dong', imageUrl: 'https://picsum.photos/400/400?random=10' },
    { id: 2, name: 'Quần Jogger Pro', price: 550000, slug: 'quan-jogger-pro', imageUrl: 'https://picsum.photos/400/400?random=11' },
    { id: 3, name: 'Áo Khoác Gió Ultra', price: 790000, slug: 'ao-khoac-gio-ultra', imageUrl: 'https://picsum.photos/400/400?random=12' },
    { id: 4, name: 'Giày Chạy Bộ Flash', price: 1250000, slug: 'giay-chay-bo-flash', imageUrl: 'https://picsum.photos/400/400?random=13' },
]);
</script>

<style scoped>
.hero-banner { height: 80vh; min-height: 500px; display: flex; align-items: center; justify-content: start; background-size: cover; background-position: center; }
.marquee-container { overflow: hidden; white-space: nowrap; }
.marquee-content { display: inline-block; animation: marquee 20s linear infinite; }
.marquee-content span, .marquee-content i { font-weight: bold; font-size: 1.2rem; letter-spacing: 2px; }
@keyframes marquee { 0% { transform: translateX(0); } 100% { transform: translateX(-50%); } }
.category-item .category-icon-wrapper { background-color: #f1f3f5; width: 100px; height: 100px; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin: 0 auto; transition: all .3s ease; }
.category-item:hover .category-icon-wrapper { background-color: #e9ecef; transform: scale(1.05); }
.promo-banner-small, .promo-banner-large { position: relative; background-size: cover; background-position: center; border-radius: 1rem; padding: 3rem; min-height: 300px; display: flex; align-items: flex-end; }
.promo-banner-small .content, .promo-banner-large .content { position: relative; z-index: 2; }
.promo-banner-small::after, .promo-banner-large::after { content: ''; position: absolute; inset: 0; border-radius: 1rem; background: linear-gradient(to top, rgba(0,0,0,0.6), transparent); }
</style>