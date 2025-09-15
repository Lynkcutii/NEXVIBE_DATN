```vue
<template>
  <div v-if="loading" class="text-center py-5">
    <div class="spinner-border" style="width: 3rem; height: 3rem;"></div>
  </div>
  <div v-else-if="error" class="alert alert-danger">{{ error }}</div>
  
  <div v-else-if="product" class="container py-4">
    <div class="row g-5">
      <div class="col-lg-7">
        <div class="product-gallery">
          <img :src="activeImageUrl" class="img-fluid main-image mb-3" :alt="product.tenSP || 'Hình ảnh sản phẩm'">
          <div class="thumbnail-list">
            <img v-for="image in product.gallery" :key="image.idImg" 
                 :src="image.link" @click="activeImageUrl = image.link"
                 class="thumbnail-item" :class="{ 'active': activeImageUrl === image.link }" 
                 :alt="image.name || 'Hình ảnh thu nhỏ'">
          </div>
        </div>
      </div>
      
      <div class="col-lg-5">
        <h1 class="fw-bolder display-5 mb-3">{{ product.tenSP }}</h1>
        <p class="h3 fw-bold text-danger mb-4">{{ selectedPrice }}</p>
        
        <p class="text-muted">{{ product.moTa }}</p>
        <hr class="my-4">
        
        <div class="variant-selector">
          <div class="mb-4">
            <div class="variant-label">Màu sắc: <span class="text-dark fw-normal">{{ selectedVariant.color.name || 'Chưa chọn' }}</span></div>
            <div class="btn-group">
              <button v-for="color in product.colors" :key="color.name" 
                      @click="selectColor(color)" 
                      class="color-option btn me-2" 
                      :class="selectedVariant.color.name === color.name ? 'btn-dark' : 'btn-outline-dark'"
                      :disabled="!isColorAvailable(color.name)">{{ color.name }}</button>
            </div>
          </div>
          <div class="mb-4">
            <div class="variant-label">Size: <span class="text-dark fw-normal">{{ selectedVariant.size || 'Chưa chọn' }}</span></div>
            <div class="btn-group">
              <button v-for="size in product.sizes" :key="size" 
                      @click="selectSize(size)" 
                      class="btn me-2" 
                      :class="selectedVariant.size === size ? 'btn-dark' : 'btn-outline-dark'"
                      :disabled="!isSizeAvailable(size)">{{ size }}</button>
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
          <button @click="goToCart" class="btn btn-primary">Xem giỏ hàng</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useCartStore } from '@/stores/cart';
import { useAuthStore } from '@/stores/auth';
import { useToast } from 'vue-toastification';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import * as bootstrap from 'bootstrap';

// Khai báo props để xử lý thuộc tính id từ RouterView
defineProps({
  id: {
    type: [String, Number],
    required: false
  }
});

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

const formatPrice = (price) => !price ? 'N/A' : new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);

const selectColor = (color) => {
  console.log('selectColor: Selected color=' + color.name);
  selectedVariant.color = color;
  if (!isSizeAvailable(selectedVariant.size)) {
    selectedVariant.size = '';
  }
  updateActiveImage();
};

const selectSize = (size) => {
  console.log('selectSize: Selected size=' + size);
  selectedVariant.size = size;
  if (!isColorAvailable(selectedVariant.color.name)) {
    selectedVariant.color = {};
  }
  updateActiveImage();
};

const isSizeAvailable = (size) => {
  if (!selectedVariant.color.name) return true;
  return product.value.variants.some(v => v.tenMauSac === selectedVariant.color.name && v.tenSize === size);
};

const isColorAvailable = (colorName) => {
  if (!selectedVariant.size) return true;
  return product.value.variants.some(v => v.tenMauSac === colorName && v.tenSize === selectedVariant.size);
};

const updateActiveImage = () => {
  if (filteredGallery.value.length > 0) {
    activeImageUrl.value = filteredGallery.value[0].link;
  } else {
    activeImageUrl.value = product.value.gallery[0]?.link || 'https://placehold.co/600x400';
  }
};

const filteredGallery = computed(() => {
  let filtered = product.value.gallery;
  if (selectedVariant.color.name) {
    filtered = filtered.filter(img => img.color === selectedVariant.color.name);
  }
  if (selectedVariant.size) {
    filtered = filtered.filter(img => img.size === selectedVariant.size);
  }
  return filtered;
});

const selectedSPCT = computed(() => {
  if (selectedVariant.color.name && selectedVariant.size) {
    return product.value.variants.find(v => 
      v.tenMauSac === selectedVariant.color.name && v.tenSize === selectedVariant.size
    );
  }
  return null;
});

const selectedPrice = computed(() => {
  return selectedSPCT.value ? formatPrice(selectedSPCT.value.gia) : formatPrice(product.value.gia);
});

const showAddToCartModal = () => {
  const modalElement = document.getElementById('addToCartModal');
  const modal = new bootstrap.Modal(modalElement);
  modal.show();
};

const goToCart = () => {
  const modalElement = document.getElementById('addToCartModal');
  const modal = bootstrap.Modal.getInstance(modalElement);
  if (modal) {
    modal.hide();
  }
  router.push('/cart').then(() => {
    console.log('goToCart: Navigation to /cart successful');
  }).catch(err => {
    console.error('goToCart: Navigation error', err);
    toast.error('Lỗi khi chuyển hướng đến giỏ hàng!');
  });
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

    const selectedSPCTVal = selectedSPCT.value;
    if (!selectedSPCTVal) {
      console.log('handleAddToCart: Variant not found for color=' + selectedVariant.color.name + ', size=' + selectedVariant.size);
      toast.error('Biến thể không tồn tại!');
      return;
    }

    console.log('handleAddToCart: Sending request to /client/api/giohang/addToCart, idSpct=' + selectedSPCTVal.id + ', soLuong=' + quantity.value);
    const response = await axios.post(`${API_BASE_URL}/client/api/giohang/addToCart`, {
      idSpct: selectedSPCTVal.id,
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
    // Lấy thông tin sản phẩm
    console.log('loadProduct: Fetching product with id=' + route.params.id);
    const productResponse = await axios.get(`${API_BASE_URL}/api/sanpham/${route.params.id}`, {
      withCredentials: true
    });
    const productData = productResponse.data;
    console.log('loadProduct: Product fetched, idSP=' + productData.idSP + ', tenSP=' + productData.tenSP);

    // Lấy tất cả sản phẩm chi tiết của sản phẩm
    console.log('loadProduct: Fetching variants for idSP=' + productData.idSP);
    const variantsResponse = await axios.get(`${API_BASE_URL}/api/sanphamchitiet/bySanPham/${productData.idSP}`, {
      withCredentials: true
    });
    let variants = Array.isArray(variantsResponse.data) ? variantsResponse.data : [];
    console.log('loadProduct: Variants fetched, count=' + variants.length);
    console.log('loadProduct: Variants data=', JSON.stringify(variants));

    // Lọc sản phẩm chi tiết có soLuong > 0
    variants = variants.filter(v => v.soLuong > 0);
    console.log('loadProduct: Filtered variants (soLuong > 0), count=' + variants.length);

    // Lấy tất cả ảnh cho từng sản phẩm chi tiết
    let allImages = [];
    for (const variant of variants) {
      console.log('loadProduct: Fetching images for idSPCT=' + variant.id);
      const imagesResponse = await axios.get(`${API_BASE_URL}/api/img/bySanPhamChiTiet/${variant.id}`, {
        withCredentials: true
      });
      const images = Array.isArray(imagesResponse.data) ? imagesResponse.data : [];
      console.log('loadProduct: Images fetched for idSPCT=' + variant.id + ', count=' + images.length);
      console.log('loadProduct: Images data=', JSON.stringify(images));

      // Thêm ảnh vào danh sách với thông tin màu sắc và kích thước tương ứng
      const variantImages = images.map((img, index) => ({
        idImg: img.idImg || `spct_${variant.id}_${index}`,
        link: img.link || 'https://placehold.co/600x400',
        name: img.name || `Hình ảnh sản phẩm chi tiết ${variant.id}`,
        color: variant.tenMauSac || 'Mặc định',
        size: variant.tenSize || 'M'
      }));
      allImages.push(...variantImages);
    }

    // Nếu không có ảnh, thêm ảnh mặc định
    if (allImages.length === 0) {
      console.log('loadProduct: No images found, adding default image');
      allImages.push({
        idImg: 'default_0',
        link: 'https://placehold.co/600x400',
        name: 'Hình ảnh mặc định',
        color: 'Mặc định',
        size: 'M'
      });
    }
    console.log('loadProduct: Total images collected, count=' + allImages.length);
    console.log('loadProduct: All images data=', JSON.stringify(allImages));

    // Lấy danh sách màu sắc duy nhất
    const uniqueColors = [...new Set(variants.map(v => v.tenMauSac || 'Mặc định'))].map(colorName => ({
      name: colorName
    }));
    console.log('loadProduct: Unique colors=', JSON.stringify(uniqueColors));

    // Lấy danh sách kích thước duy nhất
    const uniqueSizes = [...new Set(variants.map(v => v.tenSize || 'M'))];
    console.log('loadProduct: Unique sizes=', JSON.stringify(uniqueSizes));

    // Cấu trúc dữ liệu sản phẩm
    product.value = {
      id: productData.idSP,
      idSP: productData.idSP,
      tenSP: productData.tenSP || 'Không có tên',
      gia: productData.gia || 0,
      moTa: productData.moTa || 'Không có mô tả',
      variants: variants,
      gallery: allImages,
      colors: uniqueColors,
      sizes: uniqueSizes
    };
    activeImageUrl.value = product.value.gallery[0]?.link || 'https://placehold.co/600x400';
    console.log('loadProduct: Product loaded successfully, product=', JSON.stringify(product.value));
  } catch (err) {
    console.error('loadProduct: Error -', err);
    error.value = 'Không tìm thấy sản phẩm: ' + (err.response?.data?.message || err.message);
    toast.error(error.value);
  } finally {
    loading.value = false;
  }
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

.color-option.btn {
  min-width: 60px;
  text-align: center;
}

.btn-group .btn {
  min-width: 50px;
}

.btn-group .btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
```