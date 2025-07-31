<template>
  <div v-if="loading" class="text-center py-5">
    <div class="spinner-border" style="width: 3rem; height: 3rem;"></div>
  </div>
  <div v-else-if="error" class="alert alert-danger">{{ error }}</div>
  
  <div v-else-if="product" class="container py-4">
    <div class="row g-5">
      <div class="col-lg-7">
        <div class="product-gallery">
          <img :src="activeImageUrl" class="img-fluid main-image mb-3" alt="Hình ảnh sản phẩm">
          <div class="thumbnail-list">
            <img v-for="image in product.gallery" :key="image.id" 
                 :src="image.url" @click="activeImageUrl = image.url"
                 class="thumbnail-item" :class="{ 'active': activeImageUrl === image.url }" 
                 alt="Hình ảnh thu nhỏ">
          </div>
        </div>
      </div>
      
      <div class="col-lg-5">
        <h1 class="fw-bolder display-5 mb-3">{{ product.tenSP }}</h1>
        <div class="d-flex align-items-center mb-3">
          <div class="text-warning me-2">
            <i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star-half-alt"></i>
          </div>
          <span class="text-muted">(125 đánh giá)</span>
        </div>
        <p class="h3 fw-bold text-danger mb-4">{{ product.gia.toLocaleString('vi-VN') }}đ</p>
        
        <p class="text-muted">{{ product.moTa }}</p>
        <hr class="my-4">
        
        <div class="variant-selector">
          <div class="mb-4">
            <div class="variant-label">Màu sắc: <span class="text-dark fw-normal">{{ selectedVariant.color.name }}</span></div>
            <div class="btn-group">
              <button v-for="color in product.colors" :key="color.name" 
                      @click="selectColor(color)" 
                      class="color-option me-2" 
                      :class="{ 'active': selectedVariant.color.name === color.name }"
                      :style="{ backgroundColor: color.hex }"></button>
            </div>
          </div>
          <div class="mb-4">
            <div class="variant-label">Size:</div>
            <div class="btn-group">
              <button v-for="size in product.sizes" :key="size" 
                      @click="selectSize(size)" 
                      class="btn me-2" 
                      :class="selectedVariant.size === size ? 'btn-dark' : 'btn-outline-dark'">{{ size }}</button>
            </div>
          </div>
        </div>
        
        <div class="d-flex align-items-center mt-4">
          <input type="number" class="form-control me-3" style="width: 80px;" v-model.number="quantity" min="1">
          <button @click="handleAddToCart" class="btn btn-primary btn-lg flex-grow-1">
            <i class="fas fa-cart-plus me-2"></i>Thêm vào giỏ
          </button>
        </div>
      </div>
    </div>
  </div>

  <!-- Modal hiển thị khi thêm vào giỏ hàng thành công -->
  <div class="modal fade" id="addToCartModal" tabindex="-1" aria-labelledby="addToCartModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="addToCartModalLabel">Thêm vào giỏ hàng thành công!</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <p class="mb-2"><strong>Sản phẩm:</strong> {{ cartSuccessMessage.tenSP }}</p>
          <p class="mb-2"><strong>Số lượng:</strong> {{ cartSuccessMessage.soLuong }}</p>
          <p class="mb-2"><strong>Màu sắc:</strong> {{ cartSuccessMessage.mauSac }}</p>
          <p class="mb-2"><strong>Kích cỡ:</strong> {{ cartSuccessMessage.kichThuoc }}</p>
          <p class="text-success">Đã được thêm vào giỏ hàng của bạn!</p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Tiếp tục mua sắm</button>
          <router-link to="/cart" class="btn btn-primary">Xem giỏ hàng</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useCartStore } from '@/stores/cart';
import { useAuthStore } from '@/stores/auth';
import { useToast } from 'vue-toastification';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import * as bootstrap from 'bootstrap';

const route = useRoute();
const router = useRouter();
const cart = useCartStore();
const auth = useAuthStore();
const toast = useToast();

const product = ref(null);
const loading = ref(true);
const error = ref(null);
const activeImageUrl = ref('');
const quantity = ref(1);
const selectedVariant = reactive({ color: {}, size: '' });
const cartSuccessMessage = ref({ tenSP: '', soLuong: 0, mauSac: '', kichThuoc: '' });

const API_BASE_URL = 'http://localhost:8080';

const selectColor = (color) => {
  console.log('selectColor: Selected color=' + color.name);
  selectedVariant.color = color;
  const colorImage = product.value.gallery.find(img => img.color === color.name);
  if (colorImage) activeImageUrl.value = colorImage.url;
};

const selectSize = (size) => {
  console.log('selectSize: Selected size=' + size);
  selectedVariant.size = size;
};

const showAddToCartModal = () => {
  const modalElement = document.getElementById('addToCartModal');
  const modal = new bootstrap.Modal(modalElement);
  modal.show();
};

const handleAddToCart = async () => {
  console.log('handleAddToCart: Starting, isAuthenticated=' + auth.isAuthenticated);
  if (!auth.isAuthenticated) {
    console.log('handleAddToCart: User not authenticated, redirecting to login');
    toast.error('Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng!');
    router.push({ name: 'login', query: { redirect: route.fullPath } });
    return;
  }
  if (!selectedVariant.color.name || !selectedVariant.size) {
    console.log('handleAddToCart: Missing color or size');
    toast.error('Vui lòng chọn đầy đủ màu sắc và size!');
    return;
  }
  try {
    if (typeof auth.checkAuth !== 'function') {
      console.error('handleAddToCart: auth.checkAuth is not a function, auth=', auth);
      toast.error('Lỗi hệ thống: Không thể kiểm tra trạng thái đăng nhập!');
      return;
    }
    console.log('handleAddToCart: Checking auth status');
    await auth.checkAuth();

    if (!auth.isAuthenticated) {
      console.log('handleAddToCart: Session expired, redirecting to login');
      toast.error('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!');
      router.push({ name: 'login', query: { redirect: route.fullPath } });
      return;
    }

    const selectedSPCT = product.value.variants.find(v => 
      v.tenMauSac === selectedVariant.color.name && v.tenSize === selectedVariant.size
    );
    if (!selectedSPCT) {
      console.log('handleAddToCart: Variant not found for color=' + selectedVariant.color.name + ', size=' + selectedVariant.size);
      toast.error('Biến thể không tồn tại!');
      return;
    }

    console.log('handleAddToCart: Sending request to /client/api/giohang/addToCart, idSpct=' + selectedSPCT.id + ', soLuong=' + quantity.value);
    const response = await axios.post(`${API_BASE_URL}/client/api/giohang/addToCart`, {
      idSpct: selectedSPCT.id,
      soLuong: quantity.value,
      mauSac: selectedVariant.color.name,
      kichThuoc: selectedVariant.size
    }, {
      withCredentials: true
    });

    console.log('handleAddToCart: Success, response=' + JSON.stringify(response.data));
    
    // Cập nhật thông tin cho modal
    cartSuccessMessage.value = {
      tenSP: product.value.tenSP,
      soLuong: quantity.value,
      mauSac: selectedVariant.color.name,
      kichThuoc: selectedVariant.size
    };
    
    // Hiển thị toast và modal
    toast.success(`${quantity.value} "${product.value.tenSP}" đã được thêm vào giỏ!`);
    showAddToCartModal();
    
  } catch (error) {
    console.error('handleAddToCart: Error -', error);
    if (error.response) {
      console.error('handleAddToCart: Response error - status=' + error.response.status + ', data=' + JSON.stringify(error.response.data));
      if (error.response.status === 401) {
        toast.error('Chưa đăng nhập hoặc phiên đăng nhập hết hạn!');
        router.push({ name: 'login', query: { redirect: route.fullPath } });
      } else if (error.response.status === 403) {
        toast.error('Bạn không có quyền thực hiện hành động này!');
      } else {
        toast.error(error.response.data || 'Lỗi khi thêm vào giỏ hàng!');
      }
    } else if (error.request) {
      console.error('handleAddToCart: No response received, possible CORS or network issue');
      toast.error('Lỗi mạng hoặc CORS, vui lòng kiểm tra kết nối!');
    } else {
      console.error('handleAddToCart: Error setting up request - ' + error.message);
      toast.error('Lỗi khi thêm vào giỏ hàng: ' + error.message);
    }
  }
};

const loadProduct = async () => {
  if (!route.params.id) {
    console.error('loadProduct: Invalid product ID');
    error.value = 'ID sản phẩm không hợp lệ.';
    loading.value = false;
    return;
  }

  loading.value = true;
  error.value = null;
  try {
    console.log('loadProduct: Fetching product with id=' + route.params.id);
    const response = await axios.get(`${API_BASE_URL}/api/sanphamchitiet/${route.params.id}`, {
      withCredentials: true
    });
    const data = response.data;
    console.log('loadProduct: Product fetched, idSP=' + data.idSP);

    console.log('loadProduct: Fetching variants for idSP=' + data.idSP);
    const variantsResponse = await axios.get(`${API_BASE_URL}/api/sanphamchitiet/bySanPham/${data.idSP}`, {
      withCredentials: true
    });
    const variants = variantsResponse.data;
    console.log('loadProduct: Variants fetched, count=' + variants.length);

    product.value = {
      id: data.id,
      idSP: data.idSP,
      tenSP: data.tenSP || 'Không có tên',
      gia: data.gia || 0,
      moTa: data.moTa || 'Không có mô tả',
      variants: variants,
      gallery: variants.map((v, index) => ({
        id: index + 1,
        url: v.link || 'https://placehold.co/600x400',
        color: v.tenMauSac || 'Mặc định'
      })),
      colors: [...new Set(variants.map(v => ({
        name: v.tenMauSac || 'Mặc định',
        hex: getColorHex(v.tenMauSac)
      })))],
      sizes: [...new Set(variants.map(v => v.tenSize || 'M'))]
    };
    activeImageUrl.value = product.value.gallery[0]?.url || 'https://placehold.co/600x400';
    selectedVariant.color = product.value.colors[0] || {};
    selectedVariant.size = product.value.sizes[0] || '';
    console.log('loadProduct: Product loaded successfully');
  } catch (err) {
    console.error('loadProduct: Error -', err);
    error.value = 'Không tìm thấy sản phẩm: ' + (err.response?.data || err.message);
    toast.error(error.value);
  } finally {
    loading.value = false;
  }
};

const getColorHex = (colorName) => {
  const colorMap = {
    'Đen': '#000000',
    'Trắng': '#FFFFFF',
    'Xanh Dương': '#0d6efd',
    'Đỏ': '#dc3545',
    'Mặc định': '#000000'
  };
  return colorMap[colorName] || '#000000';
};

onMounted(() => {
  console.log('onMounted: route.params.id=' + route.params.id);
  loadProduct();
});
</script>

<style scoped>
.product-gallery {
  max-width: 100%;
}

.main-image {
  border-radius: 8px;
  object-fit: cover;
}

.thumbnail-list {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.thumbnail-item {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
  border: 2px solid transparent;
}

.thumbnail-item.active {
  border-color: #0d6efd;
}

.variant-label {
  font-weight: 600;
  margin-bottom: 8px;
}

.color-option {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  border: 2px solid #dee2e6;
  cursor: pointer;
}

.color-option.active {
  border-color: #0d6efd;
}

.btn-group .btn {
  min-width: 50px;
}
</style>