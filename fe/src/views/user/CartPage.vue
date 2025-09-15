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
                class="btn btn-sm btn-outline-danger float-end me-2"
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
                       v-model.number="item.soLuong" 
                       min="1" 
                       :max="item.sanPhamCTSoLuong"
                       @change="updateQuantity(item)"
                       :class="{ 'is-invalid': item.soLuong > item.sanPhamCTSoLuong || item.soLuong <= 0 }">
                <div v-if="item.soLuong > item.sanPhamCTSoLuong" class="invalid-feedback">
                  Số lượng vượt quá tồn kho ({{ item.sanPhamCTSoLuong }})
                </div>
                <div v-if="item.soLuong <= 0" class="invalid-feedback">
                  Số lượng không hợp lệ
                </div>
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
    
    <!-- Hiển thị khi không có sản phẩm hợp lệ -->
    <div v-else class="text-center p-5 bg-light rounded">
      <i class="fas fa-shopping-cart fa-4x text-muted mb-3"></i>
      <h4>Giỏ hàng của bạn đang trống hoặc không có sản phẩm hợp lệ</h4>
      <p>Hãy khám phá các sản phẩm có sẵn trong kho!</p>
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
const selectedItems = ref([]);

const API_BASE_URL = 'http://localhost:8080';

// Computed cho tổng tiền tất cả sản phẩm
const subTotal = computed(() => {
  return items.value.reduce((total, item) => total + item.donGia * item.soLuong, 0);
});

// Computed cho tổng tiền các chi tiết giỏ hàng được chọn
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
  console.log('toggleSelectAll: Current selectedItems=', selectedItems.value);
  if (isAllSelected.value) {
    selectedItems.value = [];
  } else {
    selectedItems.value = items.value.map(item => item.idGHCT);
  }
  console.log('toggleSelectAll: Updated selectedItems=', selectedItems.value);
};

// Hàm chọn/bỏ chọn từng chi tiết giỏ hàng
const toggleSelectItem = (idGHCT) => {
  console.log('toggleSelectItem: idGHCT=', idGHCT);
  const index = selectedItems.value.indexOf(idGHCT);
  if (index > -1) {
    selectedItems.value.splice(index, 1);
  } else {
    selectedItems.value.push(idGHCT);
  }
  console.log('toggleSelectItem: Updated selectedItems=', selectedItems.value);
};

// Hàm kiểm tra trạng thái xóa
const checkDeleteStatus = async (idGHCTs) => {
  try {
    console.log('checkDeleteStatus: Checking status for idGHCTs=', idGHCTs);
    const response = await axios.get(`${API_BASE_URL}/client/api/giohang/status/after-order`, {
      params: { idGHCTs: idGHCTs.join(',') },
      withCredentials: true
    });
    console.log('checkDeleteStatus: Response=', response.data);
    return response.data;
  } catch (err) {
    console.error('checkDeleteStatus: Error -', err);
    let errorMessage = 'Lỗi khi kiểm tra trạng thái xóa!';
    if (err.response) {
      console.error('checkDeleteStatus: Response error - status=', err.response.status, ', data=', err.response.data);
      errorMessage = typeof err.response.data === 'string' ? err.response.data : `Lỗi server: ${err.response.statusText} (mã ${err.response.status})`;
    } else if (err.request) {
      console.error('checkDeleteStatus: No response received, possible CORS or network issue');
      errorMessage = 'Lỗi mạng hoặc CORS: Không nhận được phản hồi từ server!';
    } else {
      console.error('checkDeleteStatus: Error setting up request -', err.message);
      errorMessage = `Lỗi thiết lập yêu cầu: ${err.message}`;
    }
    toast.error(errorMessage);
    return null;
  }
};

// Hàm xóa các chi tiết giỏ hàng đã chọn
const removeSelectedItems = async () => {
  if (selectedItems.value.length === 0) {
    console.warn('removeSelectedItems: No items selected');
    toast.warning('Vui lòng chọn ít nhất một sản phẩm để xóa!');
    return;
  }

  if (!confirm(`Bạn có chắc chắn muốn xóa ${selectedItems.value.length} sản phẩm đã chọn?`)) {
    console.log('removeSelectedItems: User cancelled deletion');
    return;
  }

  try {
    console.log('removeSelectedItems: Starting, idGHCTs=', selectedItems.value);
    const response = await axios.delete(`${API_BASE_URL}/client/api/giohang/giohangct/batch`, {
      data: selectedItems.value,
      withCredentials: true,
      headers: {
        'Content-Type': 'application/json'
      }
    });

    console.log('removeSelectedItems: Response status=', response.status, ', data=', response.data);

    if (response.status === 200 || response.status === 207) {
      // Kiểm tra trạng thái xóa
      const status = await checkDeleteStatus(selectedItems.value);
      if (status && status.stillExists > 0) {
        console.warn('removeSelectedItems: Some items were not deleted, stillExists=', status.stillExists);
        toast.warning(`Có ${status.stillExists}/${status.totalChecked} sản phẩm không được xóa. Vui lòng thử lại.`);
        await loadCartItems();
      } else {
        console.log('removeSelectedItems: All items deleted successfully');
        await loadCartItems();
        toast.success(response.data || 'Đã xóa các sản phẩm đã chọn khỏi giỏ hàng!');
      }
    } else {
      console.error('removeSelectedItems: Unexpected response status=', response.status, ', data=', response.data);
      toast.error(response.data || `Lỗi khi xóa sản phẩm: Trạng thái phản hồi không mong đợi (${response.status})`);
    }
  } catch (err) {
    console.error('removeSelectedItems: Error -', err);
    let errorMessage = 'Lỗi khi xóa các sản phẩm!';
    if (err.response) {
      console.error('removeSelectedItems: Response error - status=', err.response.status, ', data=', err.response.data);
      errorMessage = typeof err.response.data === 'string' ? err.response.data : `Lỗi server: ${err.response.statusText} (mã ${err.response.status})`;
      if (err.response.status === 401 || err.response.status === 403) {
        console.warn('removeSelectedItems: Unauthorized or forbidden, logging out');
        auth.logout();
        router.push('/login');
      }
    } else if (err.request) {
      console.error('removeSelectedItems: No response received, possible CORS or network issue');
      errorMessage = 'Lỗi mạng hoặc CORS: Không nhận được phản hồi từ server!';
    } else {
      console.error('removeSelectedItems: Error setting up request -', err.message);
      errorMessage = `Lỗi thiết lập yêu cầu: ${err.message}`;
    }
    toast.error(errorMessage);
  }
};

// Hàm tiến hành thanh toán
const proceedToCheckout = () => {
  if (selectedItems.value.length === 0) {
    console.warn('proceedToCheckout: No items selected');
    toast.warning('Vui lòng chọn ít nhất một sản phẩm để thanh toán!');
    return;
  }

  try {
    console.log('proceedToCheckout: Starting, selectedItems=', selectedItems.value);
    const selectedProducts = items.value.filter(item => selectedItems.value.includes(item.idGHCT));
    console.log('proceedToCheckout: Selected products=', selectedProducts);

    sessionStorage.setItem('selectedCheckoutItems', JSON.stringify(selectedProducts));
    console.log('proceedToCheckout: Saved to sessionStorage=', sessionStorage.getItem('selectedCheckoutItems'));

    router.push('/checkout').then(() => {
      console.log('proceedToCheckout: Navigation to /checkout successful');
    }).catch(err => {
      console.error('proceedToCheckout: Navigation error=', err);
      toast.error('Lỗi khi chuyển hướng đến trang thanh toán: ' + err.message);
    });
  } catch (err) {
    console.error('proceedToCheckout: Error=', err);
    toast.error('Lỗi khi chuẩn bị thanh toán: ' + err.message);
  }
};

// Hàm tải danh sách chi tiết giỏ hàng
const loadCartItems = async () => {
  console.log('loadCartItems: Starting, isAuthenticated=', auth.isAuthenticated);
  if (!auth.isAuthenticated) {
    console.warn('loadCartItems: User not authenticated');
    error.value = 'Vui lòng đăng nhập để xem giỏ hàng!';
    loading.value = false;
    router.push('/login');
    return;
  }

  try {
    console.log('loadCartItems: Checking auth status');
    await auth.checkAuth();

    if (!auth.isAuthenticated) {
      console.warn('loadCartItems: Session expired, redirecting to login');
      error.value = 'Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!';
      loading.value = false;
      router.push('/login');
      return;
    }

    const idTK = auth.user?.idTK;
    if (!idTK) {
      console.error('loadCartItems: Không tìm thấy idTK trong auth.user=', auth.user);
      error.value = 'Không tìm thấy thông tin tài khoản người dùng!';
      loading.value = false;
      router.push('/login');
      return;
    }

    console.log('loadCartItems: Fetching cart for idTK=', idTK);
    const response = await axios.get(`${API_BASE_URL}/client/api/giohang/byTaiKhoanId/${idTK}`, {
      withCredentials: true
    });

    console.log('loadCartItems: Response status=', response.status, ', data=', response.data);
    const gioHang = response.data;
    console.log('loadCartItems: GioHang fetched, idGH=', gioHang.idGH);

    const chiTiets = gioHang.chiTiets || [];
    console.log('loadCartItems: ChiTiets count=', chiTiets.length);

    if (chiTiets.length === 0) {
      console.log('loadCartItems: No items in cart');
      items.value = [];
      selectedItems.value = [];
      toast.info('Giỏ hàng của bạn đang trống.');
      loading.value = false;
      return;
    }

    // Lọc các item có sanPhamCTSoLuong > 0
    items.value = chiTiets
      .filter(item => item.sanPhamCTSoLuong > 0)
      .map(item => ({
        idGHCT: item.idGHCT,
        idGH: gioHang.idGH,
        idSPCT: item.idSPCT,
        name: item.tenSP || 'Sản phẩm không xác định',
        imageUrl: item.link || 'https://placehold.co/150',
        mauSac: item.mauSac || 'Mặc định',
        kichThuoc: item.kichThuoc || 'M',
        soLuong: Math.min(item.soLuong, item.sanPhamCTSoLuong), // Đảm bảo số lượng không vượt quá tồn kho
        donGia: item.donGia || 0,
        sanPhamCTSoLuong: item.sanPhamCTSoLuong || 0
      }));

    if (items.value.length === 0) {
      console.log('loadCartItems: No items with sanPhamCTSoLuong > 0');
      toast.info('Không có sản phẩm nào trong giỏ hàng có số lượng tồn kho lớn hơn 0.');
    } else {
      selectedItems.value = items.value.map(item => item.idGHCT);
      console.log('loadCartItems: Items loaded, count=', items.value.length, ', selectedItems=', selectedItems.value);
    }
  } catch (err) {
    console.error('loadCartItems: Error -', err);
    let errorMessage = 'Lỗi khi tải giỏ hàng!';
    if (err.response) {
      console.error('loadCartItems: Response error - status=', err.response.status, ', data=', err.response.data);
      errorMessage = typeof err.response.data === 'string' ? err.response.data : `Lỗi server: ${err.response.statusText} (mã ${err.response.status})`;
      if (err.response.status === 401 || err.response.status === 403) {
        error.value = 'Chưa đăng nhập hoặc phiên đăng nhập hết hạn!';
        auth.logout();
        router.push('/login');
      }
    } else if (err.request) {
      console.error('loadCartItems: No response received, possible CORS or network issue');
      errorMessage = 'Lỗi mạng hoặc CORS: Không nhận được phản hồi từ server!';
    } else {
      console.error('loadCartItems: Error setting up request -', err.message);
      errorMessage = `Lỗi thiết lập yêu cầu: ${err.message}`;
    }
    error.value = errorMessage;
    toast.error(errorMessage);
  } finally {
    loading.value = false;
    console.log('loadCartItems: Completed, loading=', loading.value);
  }
};

// Hàm cập nhật số lượng chi tiết giỏ hàng
const updateQuantity = async (item) => {
  console.log('updateQuantity: Starting, idGHCT=', item.idGHCT, ', soLuong=', item.soLuong);
  if (item.soLuong <= 0) {
    toast.error('Số lượng phải lớn hơn 0!');
    item.soLuong = 1;
    return;
  }
  if (item.soLuong > item.sanPhamCTSoLuong) {
    toast.error(`Số lượng vượt quá tồn kho (${item.sanPhamCTSoLuong})!`);
    item.soLuong = item.sanPhamCTSoLuong;
    return;
  }
  try {
    const response = await axios.put(`${API_BASE_URL}/client/api/giohangct/${item.idGHCT}`, {
      idGH: item.idGH,
      idSPCT: item.idSPCT,
      soLuong: item.soLuong,
      donGia: item.donGia
    }, {
      withCredentials: true
    });
    console.log('updateQuantity: Success, response=', response.data);
    toast.success(response.data || 'Cập nhật số lượng thành công!');
  } catch (err) {
    console.error('updateQuantity: Error -', err);
    let errorMessage = 'Lỗi khi cập nhật số lượng!';
    if (err.response) {
      console.error('updateQuantity: Response error - status=', err.response.status, ', data=', err.response.data);
      errorMessage = typeof err.response.data === 'string' ? err.response.data : `Lỗi server: ${err.response.statusText} (mã ${err.response.status})`;
      if (err.response.status === 401 || err.response.status === 403) {
        auth.logout();
        router.push('/login');
      }
    } else if (err.request) {
      console.error('updateQuantity: No response received, possible CORS or network issue');
      errorMessage = 'Lỗi mạng hoặc CORS: Không nhận được phản hồi từ server!';
    } else {
      console.error('updateQuantity: Error setting up request -', err.message);
      errorMessage = `Lỗi thiết lập yêu cầu: ${err.message}`;
    }
    toast.error(errorMessage);
  }
};

// Hàm xóa một chi tiết giỏ hàng
const removeFromCart = async (idGHCT) => {
  console.log('removeFromCart: Starting, idGHCT=', idGHCT);
  try {
    const response = await axios.delete(`${API_BASE_URL}/client/api/giohangct/${idGHCT}`, {
      withCredentials: true
    });
    console.log('removeFromCart: Response status=', response.status, ', data=', response.data);
    if (response.status === 200 || response.status === 204) {
      console.log('removeFromCart: Success');
      await loadCartItems();
      toast.success(response.data || 'Đã xóa sản phẩm khỏi giỏ hàng!');
    } else {
      console.error('removeFromCart: Unexpected response status=', response.status, ', data=', response.data);
      toast.error(response.data || `Lỗi khi xóa sản phẩm: Trạng thái phản hồi không mong đợi (${response.status})`);
    }
  } catch (err) {
    console.error('removeFromCart: Error -', err);
    let errorMessage = 'Lỗi khi xóa sản phẩm!';
    if (err.response) {
      console.error('removeFromCart: Response error - status=', err.response.status, ', data=', err.response.data);
      errorMessage = typeof err.response.data === 'string' ? err.response.data : `Lỗi server: ${err.response.statusText} (mã ${err.response.status})`;
      if (err.response.status === 401 || err.response.status === 403) {
        console.warn('removeFromCart: Unauthorized or forbidden, logging out');
        auth.logout();
        router.push('/login');
      }
    } else if (err.request) {
      console.error('removeFromCart: No response received, possible CORS or network issue');
      errorMessage = 'Lỗi mạng hoặc CORS: Không nhận được phản hồi từ server!';
    } else {
      console.error('removeFromCart: Error setting up request -', err.message);
      errorMessage = `Lỗi thiết lập yêu cầu: ${err.message}`;
    }
    toast.error(errorMessage);
  }
};

onMounted(() => {
  console.log('onMounted: Loading cart items');
  loadCartItems();
});
</script>

<style scoped>
.card {
  border-radius: 0.5rem;
}
.img-fluid {
  max-height: 100px;
  object-fit: cover;
}
.form-check-input {
  cursor: pointer;
}
.list-group-item {
  border: none;
}
</style>