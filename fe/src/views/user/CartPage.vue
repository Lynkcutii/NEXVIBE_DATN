<template>
  <div>
    <h1 class="mb-4 fw-bold">Giỏ hàng của bạn</h1>
    
    <!-- Kiểm tra trạng thái tải -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border" style="width: 3rem; height: 3rem;"></div>
    </div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>
    
    <!-- Kiểm tra nếu có sản phẩm trong giỏ hàng -->
    <div v-else-if="items.length > 0" class="row g-5">
      <!-- Cột trái: Danh sách sản phẩm -->
      <div class="col-lg-8">
        <!-- Checkbox chọn tất cả -->
        <div class="card mb-3 bg-light">
          <div class="card-body py-2">
            <div class="form-check">
              <input 
                class="form-check-input" 
                type="checkbox" 
                id="selectAll"
                :checked="isAllSelected"
                @change="toggleSelectAll"
              >
              <label class="form-check-label fw-bold" for="selectAll">
                Chọn tất cả ({{ selectedItems.length }}/{{ items.length }})
              </label>
              <button 
                v-if="selectedItems.length > 0" 
                @click="removeSelectedItems" 
                class="btn btn-sm btn-outline-danger float-end"
              >
                <i class="fas fa-trash-alt me-1"></i> Xóa đã chọn ({{ selectedItems.length }})
              </button>
            </div>
          </div>
        </div>

        <!-- Danh sách sản phẩm -->
        <div v-for="item in items" :key="item.idGHCT" class="card mb-3 shadow-sm">
          <div class="card-body">
            <div class="row align-items-center">
              <!-- Checkbox chọn sản phẩm -->
              <div class="col-md-1">
                <div class="form-check">
                  <input 
                    class="form-check-input" 
                    type="checkbox" 
                    :id="'item-' + item.idGHCT"
                    :checked="selectedItems.includes(item.idGHCT)"
                    @change="toggleSelectItem(item.idGHCT)"
                  >
                </div>
              </div>
              
              <div class="col-md-2">
                <img :src="item.imageUrl" class="img-fluid rounded" :alt="item.name">
              </div>
              
              <div class="col-md-4">
                <h5 class="mb-1">{{ item.name }}</h5>
                <p class="text-muted small mb-2">
                  Màu: <strong>{{ item.mauSac }}</strong> / Size: <strong>{{ item.kichThuoc }}</strong>
                </p>
                <button @click="removeFromCart(item.idGHCT)" class="btn btn-sm btn-outline-danger">
                  <i class="fas fa-trash-alt me-1"></i> Xóa
                </button>
              </div>
              
              <div class="col-md-2 mt-3 mt-md-0">
                <input type="number" class="form-control form-control-sm text-center" 
                       v-model.number="item.soLuong" min="1" @change="updateQuantity(item)">
              </div>
              
              <div class="col-md-3 text-md-end mt-3 mt-md-0 fw-bold">
                {{ (item.donGia * item.soLuong).toLocaleString('vi-VN') }}đ
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
                <span>Sản phẩm đã chọn</span>
                <span>{{ selectedItems.length }} sản phẩm</span>
              </li>
              <li class="list-group-item d-flex justify-content-between px-0">
                <span>Tạm tính</span>
                <span>{{ selectedSubTotal.toLocaleString('vi-VN') }}đ</span>
              </li>
              <li class="list-group-item d-flex justify-content-between fw-bold h5 px-0 mt-2">
                <span>Tổng cộng</span>
                <span>{{ selectedSubTotal.toLocaleString('vi-VN') }}đ</span>
              </li>
            </ul>
            <button 
              @click="proceedToCheckout"
              :disabled="selectedItems.length === 0"
              class="btn btn-primary w-100 mt-3 btn-lg"
              :class="{ 'btn-secondary': selectedItems.length === 0 }"
            >
              {{ selectedItems.length > 0 ? 'Tiến hành thanh toán' : 'Chọn sản phẩm để thanh toán' }}
            </button>
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
import { ref, computed, onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useToast } from 'vue-toastification';
import { useRouter } from 'vue-router';
import axios from 'axios';

const auth = useAuthStore();
const toast = useToast();
const router = useRouter();
const items = ref([]);
const loading = ref(true);
const error = ref(null);
const selectedItems = ref([]); // Danh sách ID các sản phẩm được chọn

const API_BASE_URL = 'http://localhost:8080';

// Computed cho tổng tiền tất cả sản phẩm
const subTotal = computed(() => {
  return items.value.reduce((total, item) => total + item.donGia * item.soLuong, 0);
});

// Computed cho tổng tiền các sản phẩm được chọn
const selectedSubTotal = computed(() => {
  return items.value
    .filter(item => selectedItems.value.includes(item.idGHCT))
    .reduce((total, item) => total + item.donGia * item.soLuong, 0);
});

// Computed kiểm tra có chọn tất cả không
const isAllSelected = computed(() => {
  return items.value.length > 0 && selectedItems.value.length === items.value.length;
});

// Hàm chọn/bỏ chọn tất cả
const toggleSelectAll = () => {
  if (isAllSelected.value) {
    selectedItems.value = [];
  } else {
    selectedItems.value = items.value.map(item => item.idGHCT);
  }
};

// Hàm chọn/bỏ chọn từng sản phẩm
const toggleSelectItem = (idGHCT) => {
  const index = selectedItems.value.indexOf(idGHCT);
  if (index > -1) {
    selectedItems.value.splice(index, 1);
  } else {
    selectedItems.value.push(idGHCT);
  }
};

// Hàm xóa các sản phẩm đã chọn
const removeSelectedItems = async () => {
  if (selectedItems.value.length === 0) {
    toast.warning('Vui lòng chọn ít nhất một sản phẩm để xóa!');
    return;
  }

  if (!confirm(`Bạn có chắc chắn muốn xóa ${selectedItems.value.length} sản phẩm đã chọn?`)) {
    return;
  }

  try {
    // Sử dụng endpoint batch delete mới
    const response = await axios.delete(`${API_BASE_URL}/client/api/giohang/giohangct/batch`, {
      data: selectedItems.value,
      withCredentials: true
    });

    console.log('removeSelectedItems: Batch delete response:', response.data);

    // Cập nhật UI
    items.value = items.value.filter(item => !selectedItems.value.includes(item.idGHCT));
    selectedItems.value = [];

    toast.success('Đã xóa các sản phẩm đã chọn khỏi giỏ hàng!');
  } catch (err) {
    console.error('removeSelectedItems: Error -', err);
    if (err.response && (err.response.status === 401 || err.response.status === 403)) {
      auth.logout();
      router.push('/login');
    } else {
      toast.error('Lỗi khi xóa sản phẩm: ' + (err.response?.data || err.message));
    }
  }
};

// Hàm tiến hành thanh toán
const proceedToCheckout = () => {
  if (selectedItems.value.length === 0) {
    toast.warning('Vui lòng chọn ít nhất một sản phẩm để thanh toán!');
    return;
  }

  try {
    // Lấy danh sách sản phẩm được chọn
    const selectedProducts = items.value.filter(item => selectedItems.value.includes(item.idGHCT));
    console.log('proceedToCheckout: Selected products', selectedProducts);

    // Lưu vào sessionStorage
    sessionStorage.setItem('selectedCheckoutItems', JSON.stringify(selectedProducts));
    console.log('proceedToCheckout: Saved to sessionStorage', sessionStorage.getItem('selectedCheckoutItems'));

    // Chuyển hướng sang /checkout
    router.push('/checkout').then(() => {
      console.log('proceedToCheckout: Navigation to /checkout successful');
    }).catch(err => {
      console.error('proceedToCheckout: Navigation error', err);
      toast.error('Lỗi khi chuyển hướng đến trang thanh toán!');
    });
  } catch (err) {
    console.error('proceedToCheckout: Error', err);
    toast.error('Lỗi khi chuẩn bị thanh toán: ' + err.message);
  }
};

const loadCartItems = async () => {
  console.log('loadCartItems: Starting, isAuthenticated=' + auth.isAuthenticated);
  if (!auth.isAuthenticated) {
    console.log('loadCartItems: User not authenticated');
    error.value = 'Vui lòng đăng nhập để xem giỏ hàng!';
    loading.value = false;
    router.push('/login');
    return;
  }

  try {
    console.log('loadCartItems: Checking auth status');
    await auth.checkAuth();

    if (!auth.isAuthenticated) {
      console.log('loadCartItems: Session expired, redirecting to login');
      error.value = 'Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!';
      loading.value = false;
      router.push('/login');
      return;
    }

    const idTK = auth.user?.idTK;
    if (!idTK) {
      console.error('loadCartItems: Không tìm thấy idTK trong auth.user', auth.user);
      error.value = 'Không tìm thấy thông tin tài khoản người dùng.';
      loading.value = false;
      router.push('/login');
      return;
    }

    console.log('loadCartItems: Fetching cart for idTK=' + idTK);
    const response = await axios.get(`${API_BASE_URL}/client/api/giohang/byTaiKhoanId/${idTK}`, {
      withCredentials: true
    });

    const gioHang = response.data;
    console.log('loadCartItems: GioHang fetched, idGH=' + gioHang.idGH);

    const chiTiets = gioHang.chiTiets || [];
    console.log('loadCartItems: ChiTiets count=' + chiTiets.length);

    items.value = chiTiets.map(item => ({
      idGHCT: item.idGHCT,
      idGH: gioHang.idGH,
      idSPCT: item.idSPCT,
      name: item.tenSP || 'Sản phẩm không xác định',
      imageUrl: item.link || 'https://placehold.co/150',
      mauSac: item.mauSac || 'Mặc định',
      kichThuoc: item.kichThuoc || 'M',
      soLuong: item.soLuong || 1,
      donGia: item.donGia || 0
    }));
    
    // Mặc định chọn tất cả sản phẩm khi load
    selectedItems.value = items.value.map(item => item.idGHCT);
    
    console.log('loadCartItems: Items loaded, count=' + items.value.length);
  } catch (err) {
    console.error('loadCartItems: Error -', err);
    if (err.response) {
      console.error('loadCartItems: Response error - status=' + err.response.status + ', data=' + JSON.stringify(err.response.data));
      error.value = err.response.data || 'Lỗi khi tải giỏ hàng!';
      if (err.response.status === 401 || err.response.status === 403) {
        error.value = 'Chưa đăng nhập hoặc phiên đăng nhập hết hạn!';
        auth.logout();
        router.push('/login');
      }
    } else if (err.request) {
      console.error('loadCartItems: No response received, possible CORS or network issue');
      error.value = 'Lỗi mạng hoặc CORS, vui lòng kiểm tra kết nối!';
    } else {
      console.error('loadCartItems: Error setting up request - ' + err.message);
      error.value = 'Lỗi khi tải giỏ hàng: ' + err.message;
    }
    toast.error(error.value);
  } finally {
    loading.value = false;
  }
};

const updateQuantity = async (item) => {
  console.log('updateQuantity: Starting, idGHCT=' + item.idGHCT + ', soLuong=' + item.soLuong);
  try {
    await axios.put(`${API_BASE_URL}/client/api/giohangct/${item.idGHCT}`, {
      idGH: item.idGH,
      idSPCT: item.idSPCT,
      soLuong: item.soLuong,
      donGia: item.donGia
    }, {
      withCredentials: true
    });
    console.log('updateQuantity: Success');
    toast.success('Cập nhật số lượng thành công!');
  } catch (err) {
    console.error('updateQuantity: Error -', err);
    if (err.response) {
      console.error('updateQuantity: Response error - status=' + err.response.status + ', data=' + JSON.stringify(err.response.data));
      toast.error(err.response.data || 'Lỗi khi cập nhật số lượng!');
      if (err.response.status === 401 || err.response.status === 403) {
        auth.logout();
        router.push('/login');
      }
    } else {
      toast.error('Lỗi khi cập nhật số lượng: ' + err.message);
    }
  }
};

const removeFromCart = async (idGHCT) => {
  console.log('removeFromCart: Starting, idGHCT=' + idGHCT);
  try {
    await axios.delete(`${API_BASE_URL}/client/api/giohangct/${idGHCT}`, {
      withCredentials: true
    });
    items.value = items.value.filter(item => item.idGHCT !== idGHCT);
    selectedItems.value = selectedItems.value.filter(id => id !== idGHCT);
    console.log('removeFromCart: Success');
    toast.success('Đã xóa sản phẩm khỏi giỏ hàng!');
  } catch (err) {
    console.error('removeFromCart: Error -', err);
    if (err.response) {
      console.error('removeFromCart: Response error - status=' + err.response.status + ', data=' + JSON.stringify(err.response.data));
      toast.error(err.response.data || 'Lỗi khi xóa sản phẩm!');
      if (err.response.status === 401 || err.response.status === 403) {
        auth.logout();
        router.push('/login');
      }
    } else {
      toast.error('Lỗi khi xóa sản phẩm: ' + err.message);
    }
  }
};

onMounted(() => {
  console.log('onMounted: Loading cart items');
  loadCartItems();
});
</script>