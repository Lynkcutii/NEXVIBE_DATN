import { createRouter, createWebHistory } from 'vue-router';
import AdminLayout from '../components/layouts/AdminLayout.vue';
import UserLayout from '../components/layouts/UserLayout.vue';
import HomePage from '../views/user/HomePage.vue';
import ProductListPage from '../views/user/ProductListPage.vue';
import ProductDetailPage from '../views/user/ProductDetailPage.vue';
import CartPage from '../views/user/CartPage.vue';
import CheckoutPage from '../views/user/CheckoutPage.vue';
import LoginPage from '../views/user/auth/LoginPage.vue';
import RegisterPage from '../views/user/auth/RegisterPage.vue';
import ProfilePage from '../views/user/account/ProfilePage.vue';
import OrderHistoryPage from '../views/user/account/OrderHistoryPage.vue';
import OrderSuccessPage from '../views/user/OrderSuccessPage.vue';
import AboutPage from '../views/user/AboutPage.vue';
import BlogPage from '../views/user/BlogPage.vue';
import ContactPage from '../views/user/ContactPage.vue';
import Dashboard from '../views/admin/Dashboard.vue';
import PointOfSale from '../views/admin/PointOfSale.vue';
import Products from '../views/admin/Products.vue';
import Orders from '../views/admin/Orders.vue';
import OrderDetail from '../views/admin/OrderDetail.vue';
import Attributes from '../views/admin/Attributes.vue';
import Staff from '../views/admin/Staff.vue';
import Customers from '../views/admin/Customers.vue';
import Vouchers from '../views/admin/Vouchers.vue';
import Posts from '../views/admin/Posts.vue';
import Reviews from '../views/admin/Reviews.vue';
import ProductForm from '../views/forms/ProductForm.vue';
import AccountForm from '../views/forms/AccountForm.vue';
import PostForm from '../views/forms/PostForm.vue';
import VoucherForm from '../views/forms/VoucherForm.vue';
import OrderDetailPage from '../views/user/account/OrderDetailPage.vue';
import { useAuthStore } from '@/stores/auth';
import { useToast } from 'vue-toastification';

const routes = [
  // --- NHÁNH ROUTE CHO TRANG NGƯỜI DÙNG (USER) ---
  {
    path: '/',
    component: UserLayout,
    children: [
      { path: '', name: 'home', component: HomePage },
      { path: 'products', name: 'products.list', component: ProductListPage },
      { path: 'products/:id', name: 'products.detail', component: ProductDetailPage, props: true },
      { path: 'cart', name: 'cart', component: CartPage },
      { path: 'checkout', name: 'checkout', component: CheckoutPage },
      { path: 'order-success', name: 'order.success', component: OrderSuccessPage },
      { path: 'login', name: 'login', component: LoginPage },
      { path: 'register', name: 'register', component: RegisterPage },
      { path: 'profile', name: 'profile', component: ProfilePage, meta: { requiresAuth: true } },
      { path: 'order-history', name: 'order.history', component: OrderHistoryPage, meta: { requiresAuth: true } },
{
  path: '/order-detail/:idHD', 
  name: 'order.detail',
  component: OrderDetailPage,
  props: true 
},
      { path: 'about', name: 'about', component: AboutPage },
      { path: 'blog', name: 'blog', component: BlogPage },
      { path: 'contact', name: 'contact', component: ContactPage },
    ]
  },

  // --- NHÁNH ROUTE CHO TRANG QUẢN TRỊ (ADMIN) ---
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', name: 'admin.dashboard', component: Dashboard },
      { path: 'pos', name: 'admin.pos', component: PointOfSale },
      { path: 'products', name: 'admin.products.list', component: Products },
      { path: 'products/create', name: 'admin.products.create', component: ProductForm },
      { path: 'products/:id/edit', name: 'admin.products.edit', component: ProductForm },
      { path: 'orders', name: 'admin.orders.list', component: Orders },
      { path: 'orders/:id', name: 'admin.orders.detail', component: OrderDetail, props: true },
      { path: 'attributes', name: 'admin.attributes', component: Attributes },
      { path: 'staff', name: 'admin.staff.list', component: Staff },
      { path: 'staff/create', name: 'admin.staff.create', component: AccountForm },
      { path: 'staff/:id/edit', name: 'admin.staff.edit', component: AccountForm },
      { path: 'customers', name: 'admin.customers.list', component: Customers },
      { path: 'customers/:id/edit', name: 'admin.customers.edit', component: AccountForm },
      { path: 'vouchers', name: 'admin.vouchers.list', component: Vouchers },
      { path: 'vouchers/create', name: 'admin.vouchers.create', component: VoucherForm },
      { path: 'vouchers/:id/edit', name: 'admin.vouchers.edit', component: VoucherForm },
      { path: 'posts', name: 'admin.posts.list', component: Posts },
      { path: 'posts/create', name: 'admin.posts.create', component: PostForm },
      { path: 'posts/:id/edit', name: 'admin.posts.edit', component: PostForm },
      { path: 'reviews', name: 'admin.reviews.list', component: Reviews },
    ]
  },
  // Catch-all route
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    } else {
      return { top: 0 };
    }
  }
});

// Middleware kiểm tra xác thực và vai trò
router.beforeEach((to, from, next) => {
  const auth = useAuthStore();
  const toast = useToast();

  // Kiểm tra các route yêu cầu xác thực
  if (to.meta.requiresAuth) {
    if (!auth.isAuthenticated || !auth.user) {
      toast.error('Vui lòng đăng nhập để truy cập!');
      return next({ name: 'login', query: { redirect: to.fullPath } });
    }

    // Kiểm tra các route yêu cầu vai trò Admin
    if (to.meta.requiresAdmin && !auth.isAdmin()) {
      toast.error('Bạn không có quyền truy cập trang này!');
      return next({ name: 'home' });
    }
  }

  next();
});

export default router;