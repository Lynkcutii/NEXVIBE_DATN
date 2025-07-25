<template>
  <nav id="sidebar">
    <div class="sidebar-header">
      <!-- Thay đổi đường dẫn này về dạng tương đối từ assets để Vite xử lý -->
      <img src="/img/logo/logo.jpeg" alt="Nexvibe Logo">
    </div>

    <ul class="nav flex-column">
      <li class="nav-item">
        <router-link to="/admin/dashboard" class="nav-link"><i class="fas fa-fw fa-tachometer-alt"></i> Dashboard</router-link>
      </li>
      <li class="nav-item">
        <router-link to="/admin/pos" class="nav-link"><i class="fas fa-fw fa-cash-register"></i> Bán hàng tại quầy</router-link>
      </li>
      
      <!-- Nhóm các chức năng chính -->
      <li class="nav-item-header">CỬA HÀNG</li>
      <li class="nav-item">
        <router-link to="/admin/products" class="nav-link"><i class="fas fa-fw fa-shirt"></i> Quản lý Sản phẩm</router-link>
      </li>
      <li class="nav-item">
        <router-link to="/admin/orders" class="nav-link"><i class="fas fa-fw fa-box-open"></i> Quản lý Đơn hàng</router-link>
      </li>
      <li class="nav-item">
        <router-link to="/admin/vouchers" class="nav-link"><i class="fas fa-fw fa-percent"></i> Quản lý Khuyến mại</router-link>
      </li>
      <li class="nav-item">
        <router-link to="/admin/attributes" class="nav-link"><i class="fas fa-fw fa-tags"></i> Quản lý Thuộc tính</router-link>
      </li>

      <!-- Nhóm các chức năng hệ thống -->
      <li class="nav-item-header">HỆ THỐNG</li>
      <li class="nav-item">
        <!-- Menu xổ xuống cho Tài khoản -->
        <a class="nav-link collapsed" data-bs-toggle="collapse" href="#accountsSubmenu" role="button">
          <i class="fas fa-fw fa-users-cog"></i> Quản lý Tài khoản
        </a>
        <div class="collapse sidebar-submenu" id="accountsSubmenu">
          <ul class="nav flex-column">
            <li class="nav-item">
              <router-link to="/admin/staff" class="nav-link">Nhân viên</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/admin/customers" class="nav-link">Khách hàng</router-link>
            </li>
          </ul>
        </div>
      </li>
      <li class="nav-item">
        <router-link to="/admin/posts" class="nav-link"><i class="fas fa-fw fa-newspaper"></i> Quản lý Bài viết</router-link>
      </li>
      <li class="nav-item">
        <router-link to="/admin/reviews" class="nav-link"><i class="fas fa-fw fa-star"></i> Quản lý Đánh giá</router-link>
      </li>
    </ul>
  </nav>
</template>

<style scoped>
/* Style cho mục menu đang được chọn (active) */
.nav-link.router-link-exact-active {
  background-color: rgba(56, 189, 248, 0.1); /* Màu nền highlight nhẹ nhàng */
  color: #fff;
  font-weight: 500;
  border-left: 4px solid #38bdf8;
}
.nav-link.router-link-exact-active i.fa-fw {
  color: #38bdf8;
}

/* Thêm một tiêu đề nhỏ để phân nhóm menu */
.nav-item-header {
    padding: 10px 25px;
    font-size: 0.75rem;
    font-weight: bold;
    color: #8f9ca9;
    text-transform: uppercase;
    letter-spacing: 1px;
    margin-top: 1rem;
}

/* Style cho menu con */
.sidebar-submenu {
    background-color: rgba(0, 0, 0, 0.2);
    padding-left: 15px;
}
.sidebar-submenu .nav-link {
    font-size: 0.9rem;
    padding-left: 2.5rem;
}
.sidebar-submenu .nav-link:hover {
    background-color: transparent;
    color: #38bdf8;
}
.sidebar-submenu .nav-link.router-link-exact-active {
    background-color: transparent;
    color: #fff;
    font-weight: bold;
}

/* Thêm mũi tên chỉ xuống cho menu cha */
.nav-link[data-bs-toggle="collapse"]::after {
    content: '\f078';
    font-family: 'Font Awesome 6 Free';
    font-weight: 900;
    margin-left: auto;
    transition: transform 0.3s;
}

/* Xoay mũi tên khi menu được mở */
.nav-link[data-bs-toggle="collapse"]:not(.collapsed)::after {
    transform: rotate(180deg);
}

/* Giữ cho menu cha active khi một trong các con của nó active */
/* Vue Router tự động thêm class 'router-link-active' (không exact) cho link cha */
.nav-item > .router-link-active {
    background-color: rgba(56, 189, 248, 0.1);
    color: #fff;
}
</style>