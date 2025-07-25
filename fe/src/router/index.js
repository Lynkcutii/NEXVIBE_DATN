import { createRouter, createWebHistory } from 'vue-router';

// =================================================================
// ===           LAYOUTS (Khung giao diện chung)                 ===
// =================================================================
import AdminLayout from '../components/layouts/AdminLayout.vue';
import UserLayout from '../components/layouts/UserLayout.vue';

// =================================================================
// ===                 ADMIN VIEWS (Các trang Admin)             ===
// =================================================================
// --- Trang danh sách ---
import Dashboard from '../views/Dashboard.vue';
import PointOfSale from '../views/PointOfSale.vue';
import Products from '../views/Products.vue';
import Orders from '../views/Orders.vue';
import OrderDetail from '../views/OrderDetail.vue';
import Attributes from '../views/Attributes.vue';
import Staff from '../views/Staff.vue';
import Customers from '../views/Customers.vue';
import Vouchers from '../views/Vouchers.vue';
import Posts from '../views/Posts.vue';
import Reviews from '../views/Reviews.vue';

// --- Trang form ---
import ProductForm from '../views/forms/ProductForm.vue';
import AccountForm from '../views/forms/AccountForm.vue';
import PostForm from '../views/forms/PostForm.vue';
import VoucherForm from '../views/forms/VoucherForm.vue';

// =================================================================
// ===                  USER VIEWS (Các trang User)              ===
// =================================================================
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
import OrderDetailPage from '../views/user/account/OrderDetailPage.vue';

const routes = [
    // --- NHÁNH ROUTE CHO TRANG NGƯỜI DÙNG (USER) ---
    {
        path: '/',
        component: UserLayout,
        children: [
            { path: '', name: 'home', component: HomePage },
            { path: 'products', name: 'products.list', component: ProductListPage },
            { path: 'products/:slug', name: 'products.detail', component: ProductDetailPage, props: true },
            { path: 'cart', name: 'cart', component: CartPage },
            { path: 'checkout', name: 'checkout', component: CheckoutPage },
            { path: 'order-success', name: 'order.success', component: OrderSuccessPage },
            { path: 'login', name: 'login', component: LoginPage },
            { path: 'register', name: 'register', component: RegisterPage },
            { path: 'profile', name: 'profile', component: ProfilePage },
            { path: 'order-history', name: 'order.history', component: OrderHistoryPage },
            { path: 'orders/:id', name: 'order.detail', component: OrderDetailPage, props: true },
            { path: 'about', name: 'about', component: AboutPage },
            { path: 'blog', name: 'blog', component: BlogPage },
            { path: 'contact', name: 'contact', component: ContactPage },
        ]
    },

    // --- NHÁNH ROUTE CHO TRANG QUẢN TRỊ (ADMIN) ---
    {
        path: '/admin',
        component: AdminLayout,
        children: [
            // Route mặc định
            { path: '', redirect: '/admin/dashboard' },
            { path: 'dashboard', name: 'admin.dashboard', component: Dashboard },
            
            // Bán hàng tại quầy
            { path: 'pos', name: 'admin.pos', component: PointOfSale },
            
            // Quản lý Sản phẩm
           { path: 'products', name: 'admin.products.list', component: Products },
            { path: 'products/create', name: 'admin.products.create', component: ProductForm },
            { path: 'products/:id/edit', name: 'admin.products.edit', component: ProductForm },
            // Quản lý Đơn hàng
            { path: 'orders', name: 'admin.orders.list', component: Orders },
            { path: 'orders/:id', name: 'admin.orders.detail', component: OrderDetail },
            
            // Quản lý Thuộc tính
            { path: 'attributes', name: 'admin.attributes', component: Attributes },
            
            // Quản lý Nhân viên (Staff)
            { path: 'staff', name: 'admin.staff.list', component: Staff },
            { path: 'staff/create', name: 'admin.staff.create', component: AccountForm },
            { path: 'staff/:id/edit', name: 'admin.staff.edit', component: AccountForm },
            
            // Quản lý Khách hàng (Customers)
            { path: 'customers', name: 'admin.customers.list', component: Customers },
            { path: 'customers/:id/edit', name: 'admin.customers.edit', component: AccountForm },

            // Quản lý Khuyến mại (Vouchers)
            { path: 'vouchers', name: 'admin.vouchers.list', component: Vouchers },
            { path: 'vouchers/create', name: 'admin.vouchers.create', component: VoucherForm },
            { path: 'vouchers/:id/edit', name: 'admin.vouchers.edit', component: VoucherForm },

            // Quản lý Bài viết
            { path: 'posts', name: 'admin.posts.list', component: Posts },
            { path: 'posts/create', name: 'admin.posts.create', component: PostForm },
            { path: 'posts/:id/edit', name: 'admin.posts.edit', component: PostForm },
            
            // Quản lý Đánh giá
            { path: 'reviews', name: 'admin.reviews.list', component: Reviews },
        ]
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;