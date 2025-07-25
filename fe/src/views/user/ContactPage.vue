<template>
  <div class="container py-5">
    <div class="text-center mb-5">
      <h1 class="fw-bold">Liên hệ với chúng tôi</h1>
      <p class="lead text-muted">Chúng tôi luôn sẵn sàng lắng nghe bạn. Hãy để lại lời nhắn nhé!</p>
    </div>
    <div class="row">
      <div class="col-lg-6">
        <h4 class="mb-3">Thông tin liên hệ</h4>
        <p><i class="fas fa-map-marker-alt me-2"></i>123 Đường ABC, Quận 1, TP. Hồ Chí Minh</p>
        <p><i class="fas fa-phone me-2"></i>(028) 3812 3456</p>
        <p><i class="fas fa-envelope me-2"></i>support@nexvibe.com</p>
        <hr>
        <h4 class="mb-3">Mạng xã hội</h4>
        <a href="#" class="btn btn-outline-dark me-2 fs-4"><i class="fab fa-facebook-f"></i></a>
        <a href="#" class="btn btn-outline-dark me-2 fs-4"><i class="fab fa-instagram"></i></a>
        <a href="#" class="btn btn-outline-dark fs-4"><i class="fab fa-tiktok"></i></a>
      </div>
      <div class="col-lg-6">
        <form>
          <div class="mb-3">
            <label for="name" class="form-label">Họ và tên</label>
            <input type="text" class="form-control" id="name">
          </div>
          <div class="mb-3">
            <label for="email" class="form-label">Email</label>
            <input type="email" class="form-control" id="email">
          </div>
          <div class="mb-3">
            <label for="message" class="form-label">Lời nhắn</label>
            <textarea class="form-control" id="message" rows="5"></textarea>
          </div>
          <button type="submit" class="btn btn-primary w-100">Gửi lời nhắn</button>
        </form>
      </div>
    </div>
  </div>
</template>