<template>
  <h1 class="h3 mb-4 text-gray-800">Bán Hàng Tại Quầy (POS)</h1>
  <div class="row">
    <!-- Cột trái: Tìm và chọn sản phẩm -->
    <div class="col-lg-7">
      <div class="card shadow mb-4">
        <div class="card-header">
          <div class="input-group">
            <span class="input-group-text"><i class="fas fa-search"></i></span>
            <input type="text" class="form-control" placeholder="Tìm kiếm sản phẩm (tên hoặc SKU)..." v-model="searchQuery">
          </div>
        </div>
        <div class="card-body" style="max-height: 65vh; overflow-y: auto;">
          <!-- Danh sách sản phẩm đã lọc -->
          <div class="list-group">
            <a href="#" v-for="product in filteredProducts" :key="product.id" @click.prevent="addToCart(product)" class="list-group-item list-group-item-action d-flex gap-3 py-3">
              <img :src="product.imageUrl" width="50" height="50" class="rounded-circle flex-shrink-0">
              <div class="d-flex gap-2 w-100 justify-content-between">
                <div>
                  <h6 class="mb-0">{{ product.name }}</h6>
                  <p class="mb-0 opacity-75">{{ formatCurrency(product.price) }} - Tồn: {{ product.stock }}</p>
                </div>
                <button class="btn btn-sm" :class="product.stock > 0 ? 'btn-outline-primary' : 'btn-outline-secondary'" :disabled="product.stock <= 0">
                  {{ product.stock > 0 ? 'Thêm' : 'Hết hàng' }}
                </button>
              </div>
            </a>
            <div v-if="filteredProducts.length === 0" class="text-center p-5">
                Không tìm thấy sản phẩm.
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Cột phải: Hóa đơn và thanh toán -->
    <div class="col-lg-5">
      <div class="card shadow">
        <div class="card-header d-flex justify-content-between align-items-center">
          <h5 class="m-0">Đơn Hàng #{{ orderId }}</h5>
          <button @click="clearCart" class="btn btn-sm btn-outline-danger" :disabled="cart.length === 0">Xóa đơn</button>
        </div>
        <div class="card-body" style="min-height: 45vh;">
          <table class="table" v-if="cart.length > 0">
            <thead>
              <tr>
                <th>Sản phẩm</th>
                <th style="width: 80px;">SL</th>
                <th class="text-end">Thành tiền</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in cart" :key="item.id">
                <td>{{ item.name }}</td>
                <td><input type="number" v-model.number="item.quantity" @change="updateQuantity(item)" min="1" :max="item.stock" class="form-control form-control-sm"></td>
                <td class="text-end">{{ formatCurrency(item.price * item.quantity) }}</td>
                <td><button @click="removeFromCart(item.id)" class="btn btn-link text-danger p-0"><i class="fas fa-times"></i></button></td>
              </tr>
            </tbody>
          </table>
          <div v-else class="text-center text-muted d-flex align-items-center justify-content-center h-100">
            Chưa có sản phẩm trong giỏ hàng.
          </div>
        </div>
        <div class="card-footer p-3">
          <div class="d-flex justify-content-between"><span>Tạm tính</span><strong>{{ formatCurrency(subTotal) }}</strong></div>
          <div class="d-flex justify-content-between"><span>Giảm giá</span><strong>{{ formatCurrency(discount) }}</strong></div>
          <hr class="my-2">
          <div class="d-flex justify-content-between h4"><span>Tổng cộng</span><strong>{{ formatCurrency(total) }}</strong></div>
          <button @click="processPayment" class="btn btn-success w-100 btn-lg mt-2" :disabled="cart.length === 0">Thanh Toán</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

// --- DỮ LIỆU MẪU ---
const allProducts = ref([
  { id: 1, name: 'Áo Thun Thể Thao', price: 250000, stock: 150, imageUrl: 'https://i.imgur.com/3pWjC0a.png' },
  { id: 2, name: 'Áo Hoodie Nỉ Bông', price: 550000, stock: 8, imageUrl: 'https://i.imgur.com/fG0m92F.png' },
  { id: 3, name: 'Quần Jogger Thể Thao', price: 320000, stock: 0, imageUrl: 'https://i.imgur.com/xT5A0if.png' },
]);
const orderId = ref(1053);

// --- LOGIC TÌM KIẾM ---
const searchQuery = ref('');
const filteredProducts = computed(() => {
  if (!searchQuery.value) {
    return allProducts.value;
  }
  return allProducts.value.filter(p => 
    p.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
});

// --- LOGIC GIỎ HÀNG ---
const cart = ref([]);

const addToCart = (product) => {
  if (product.stock <= 0) return;

  const existingItem = cart.value.find(item => item.id === product.id);

  if (existingItem) {
    if (existingItem.quantity < product.stock) {
      existingItem.quantity++;
    } else {
      alert('Số lượng trong giỏ đã đạt tồn kho tối đa!');
    }
  } else {
    cart.value.push({ ...product, quantity: 1 });
  }
};

const removeFromCart = (productId) => {
  cart.value = cart.value.filter(item => item.id !== productId);
};

const updateQuantity = (item) => {
  if (item.quantity > item.stock) {
    alert('Số lượng vượt quá tồn kho!');
    item.quantity = item.stock; // Reset về số lượng tối đa
  }
  if (item.quantity < 1) {
    item.quantity = 1; // Số lượng tối thiểu là 1
  }
};

const clearCart = () => {
    if(confirm('Bạn có chắc muốn xóa toàn bộ đơn hàng này?')) {
        cart.value = [];
    }
}

// --- LOGIC TÍNH TOÁN ---
const subTotal = computed(() => cart.value.reduce((sum, item) => sum + (item.price * item.quantity), 0));
const discount = ref(0); // Có thể thêm logic nhập mã giảm giá sau
const total = computed(() => subTotal.value - discount.value);

// --- HÀM TIỆN ÍCH ---
const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

// --- XỬ LÝ THANH TOÁN ---
const processPayment = () => {
    if (cart.value.length === 0) return;
    
    // Logic xử lý thanh toán
    console.log('Đang xử lý thanh toán cho đơn hàng:', {
        orderId: orderId.value,
        items: cart.value,
        total: total.value
    });

    alert(`Thanh toán thành công đơn hàng #${orderId.value} với tổng tiền ${formatCurrency(total.value)}`);

    // Sau khi thanh toán, tạo đơn hàng mới
    cart.value = [];
    orderId.value++; // Tăng mã đơn hàng cho lần tiếp theo
}
</script>