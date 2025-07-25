<template>
  <div class="footer-container text-white mt-5">
    <div class="container">
      <footer class="py-5">
        <div class="row">
          <div class="col-lg-4 mb-4 mb-lg-0">
            <a href="/" class="d-inline-block mb-3">
              <img src="/img/logo/logo.jpeg" alt="Nexvibe Logo White" height="100">
            </a>
            <p class="text-white-50">NEXVIBE - Nâng tầm phong cách, bứt phá giới hạn.</p>
          </div>
          <div class="col-6 col-lg-2 offset-lg-1 mb-3">
            <h5 class="fw-bold">Về NEXVIBE</h5>
            <ul class="nav flex-column"><li class="nav-item mb-2"><router-link to="/about" class="nav-link p-0">Giới thiệu</router-link></li><li class="nav-item mb-2"><router-link to="/blog" class="nav-link p-0">Tin tức</router-link></li></ul>
          </div>
          <div class="col-6 col-lg-2 mb-3">
            <h5 class="fw-bold">Hỗ trợ</h5>
            <ul class="nav flex-column"><li class="nav-item mb-2"><router-link to="/contact" class="nav-link p-0">Liên hệ</router-link></li><li class="nav-item mb-2"><a href="#" class="nav-link p-0">Chính sách đổi trả</a></li></ul>
          </div>
          <div class="col-lg-3 mb-3">
             <h5 class="fw-bold">Kết nối</h5>
             <ul class="list-unstyled d-flex"><li class="me-3"><a class="fs-4" href="#"><i class="fab fa-facebook"></i></a></li><li class="me-3"><a class="fs-4" href="#"><i class="fab fa-instagram"></i></a></li></ul>
          </div>
        </div>
        <div class="d-flex justify-content-between py-4 my-4 border-top">
          <p>© 2024 Nexvibe, Inc. All rights reserved.</p>
        </div>
      </footer>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.footer-container {
    background-color: #0c2444; /* Màu nền xanh đen */
}
.footer-container .nav-link, 
.footer-container a {
    color: rgba(255, 255, 255, 0.7);
    transition: color 0.2s ease-in-out;
}
.footer-container .nav-link:hover,
.footer-container a:hover {
    color: #fff;
}
.footer-container .border-top {
    border-color: rgba(255, 255, 255, 0.1) !important;
}
</style>