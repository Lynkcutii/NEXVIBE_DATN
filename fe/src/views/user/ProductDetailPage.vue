<template>
  <div v-if="loading" class="text-center py-5">
    <div class="spinner-border" style="width: 3rem; height: 3rem;"></div>
  </div>
  <div v-else-if="error" class="alert alert-danger">{{ error }}</div>
  
  <div v-else-if="product" class="container py-4">
    <div class="row g-5">
      <!-- Cột trái: Thư viện ảnh -->
      <div class="col-lg-7">
        <div class="product-gallery">
          <img :src="activeImageUrl" class="img-fluid main-image mb-3">
          <div class="thumbnail-list">
            <img v-for="image in product.gallery" :key="image.id" 
                 :src="image.url" @click="activeImageUrl = image.url"
                 class="thumbnail-item" :class="{ 'active': activeImageUrl === image.url }">
          </div>
        </div>
      </div>
      
      <!-- Cột phải: Thông tin và lựa chọn -->
      <div class="col-lg-5">
        <h1 class="fw-bolder display-5 mb-3">{{ product.name }}</h1>
        <div class="d-flex align-items-center mb-3">
            <div class="text-warning me-2">
                <i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star-half-alt"></i>
            </div>
            <span class="text-muted">(125 đánh giá)</span>
        </div>
        <p class="h3 fw-bold text-danger mb-4">{{ product.price.toLocaleString() }}đ</p>
        
        <p class="text-muted">{{ product.short_description }}</p>
        <hr class="my-4">
        
        <!-- Lựa chọn biến thể -->
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
                            @click="selectedVariant.size = size" 
                            class="btn me-2" 
                            :class="selectedVariant.size === size ? 'btn-dark' : 'btn-outline-dark'">{{ size }}</button>
                </div>
            </div>
        </div>
        
        <!-- Thêm vào giỏ hàng -->
        <div class="d-flex align-items-center mt-4">
          <input type="number" class="form-control me-3" style="width: 80px;" v-model.number="quantity" min="1">
          <button @click="handleAddToCart" class="btn btn-primary btn-lg flex-grow-1">
            <i class="fas fa-cart-plus me-2"></i>Thêm vào giỏ
          </button>
        </div>
      </div>
    </div>

    <!-- Tab Mô tả chi tiết & Đánh giá (Giữ nguyên) -->
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useCartStore } from '@/stores/cart';
import { useToast } from 'vue-toastification';
const props = defineProps(['slug']);
const cart = useCartStore();
const toast = useToast();

const product = ref(null);
const loading = ref(true);
const error = ref(null);
const activeImageUrl = ref('');
const quantity = ref(1);
const selectedVariant = reactive({ color: {}, size: '' });

const selectColor = (color) => {
    selectedVariant.color = color;
    const colorImage = product.value.gallery.find(img => img.color === color.name);
    if (colorImage) activeImageUrl.value = colorImage.url;
}

const handleAddToCart = () => {
    if (!selectedVariant.color.name || !selectedVariant.size) {
        toast.error('Vui lòng chọn đầy đủ màu sắc và size!');
        return;
    }
    cart.addToCart(product.value, quantity.value, {color: selectedVariant.color.name, size: selectedVariant.size});
    toast.success(`${quantity.value} "${product.value.name}" đã được thêm vào giỏ!`);
}

onMounted(async () => {
    loading.value = true;
    try {
        await new Promise(resolve => setTimeout(resolve, 500));
        const sampleProduct = {
            id: 1, name: 'Áo Thun Năng Động', price: 350000, slug: 'ao-thun-nang-dong',
            short_description: 'Dòng sản phẩm NEXVIBE Pro được thiết kế với chất liệu cao cấp, co dãn 4 chiều, mang lại sự thoải mái tối đa cho mọi hoạt động.',
            gallery: [
                { id: 1, url: 'https://picsum.photos/600/600?random=10', color: 'Đen' },
                { id: 2, url: 'https://picsum.photos/600/600?random=11', color: 'Trắng' },
                { id: 3, url: 'https://picsum.photos/600/600?random=12', color: 'Xanh Dương' },
                { id: 4, url: 'https://picsum.photos/600/600?random=13', color: null },
            ],
            colors: [{name: 'Đen', hex: '#000000'}, {name: 'Trắng', hex: '#FFFFFF'}, {name: 'Xanh Dương', hex: '#0d6efd'}], 
            sizes: ['S', 'M', 'L', 'XL']
        };
        product.value = sampleProduct;
        activeImageUrl.value = sampleProduct.gallery[0].url;
        selectedVariant.color = sampleProduct.colors[0];
        selectedVariant.size = sampleProduct.sizes[0];
    } catch (err) { error.value = "Không tìm thấy sản phẩm."; } 
    finally { loading.value = false; }
});
</script>