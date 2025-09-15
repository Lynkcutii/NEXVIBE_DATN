<template>
  <div class="container py-4">
    <!-- 1. Trạng thái Loading -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" style="width: 3rem; height: 3rem;" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p class="mt-3 text-muted">Đang tải chi tiết đơn hàng...</p>
    </div>

    <!-- 2. Trạng thái Lỗi -->
    <div v-else-if="error" class="alert alert-danger text-center">
      <h4>Lỗi!</h4>
      <p>{{ error }}</p>
      <router-link :to="{ name: 'order.history' }" class="btn btn-primary">Quay lại Lịch sử đơn hàng</router-link>
    </div>
    
    <!-- 3. Giao diện chính -->
    <div v-else-if="order" class="card border-0 shadow-sm">
      <div class="card-body p-0">
        <!-- Header -->
        <div class="d-flex justify-content-between align-items-center p-3 border-bottom">
        <router-link :to="{ name: 'order.history' }" class="btn btn-link text-dark text-decoration-none ps-0">
          <i class="fas fa-arrow-left me-2"></i> TRỞ LẠI
        </router-link>
        <div>
        <span class="text-muted me-3">MÃ ĐƠN HÀNG: {{ order.id }}</span>
        <button @click="copyOrderId" class="btn btn-sm btn-outline-secondary me-3" title="Sao chép mã đơn hàng">
          <i class="far fa-copy"></i>
        </button>
        <span class="fw-bold text-uppercase" :class="getStatusTextColor(order.currentStatus)">{{ order.statusText }}</span>
      </div>
      </div>

      <!-- Timeline hoặc Thông báo Hủy -->
      <div class="p-4 border-bottom">
        <!-- 1. Hiển thị timeline bình thường nếu KHÔNG bị hủy -->
        <div v-if="order.currentStatus !== 'DA_HUY'" class="order-timeline">
          <div v-for="(step, index) in timelineSteps" :key="step.status" 
               class="timeline-step" :class="{ 'completed': animatedStepIndex >= index }">
            <div class="timeline-icon"><i :class="step.icon"></i></div>
            <div class="timeline-step-name">{{ step.name }}</div>
          </div>
        </div>
        <!-- 2. Hiển thị thông báo đặc biệt NẾU bị hủy -->
        <div v-else class="cancelled-notice">
          <h4 class="cancelled-title text-danger">Đã hủy đơn hàng</h4>
          <p class="cancelled-time text-muted">vào {{ formatDateTime(order.updatedAt) }}</p>
        </div>
      </div>
      
        <div class="shipping-progress-bar"></div>

        <!-- Địa chỉ -->
        <div class="p-4 border-bottom">
          <h5 class="mb-3">Địa chỉ Nhận Hàng</h5>
          <p class="fw-bold mb-1">{{ order.customerInfo.name }}</p>
          <p class="text-muted mb-1">{{ order.customerInfo.phone }}</p>
          <p class="text-muted mb-0">{{ order.customerInfo.address }}</p>
        </div>

<!-- Thông tin sản phẩm -->
<div class="p-3">
   <div v-for="item in order.products" :key="item.id" class="product-item-row px-2">
    <img :src="item.imageUrl" class="product-item-img" @error="handleImageError">
    <div class="product-item-info">
      <!-- THÊM TÊN SẢN PHẨM VÀO ĐÂY -->
      <h6 class="mb-1 fw-bold">{{ item.name }}</h6> 
      <p class="small text-muted mb-1">Phân loại: {{ item.variant.color }}, {{ item.variant.size }}</p>
      <p class="small text-muted mb-0">Số lượng: x{{ item.quantity }}</p>
    </div>
    <div class="product-item-price"><h6 class="text-dark fw-bold mb-0">{{ item.price.toLocaleString() }}đ</h6></div>
  </div>
</div>
        <!-- Tổng kết thanh toán -->
        <div class="payment-summary-card">
            <div class="price-summary-row"><span>Tổng tiền hàng</span><span>{{ order.summary.subtotal.toLocaleString() }}đ</span></div>
            <div class="price-summary-row"><span>Phí vận chuyển</span><span>{{ order.summary.shippingFee.toLocaleString() }}đ</span></div>
            <div class="price-summary-row total h5"><span>Thành tiền</span><span class="fw-bolder text-danger">{{ order.summary.total.toLocaleString() }}đ</span></div>
        </div>
        
        <!-- Nút hành động -->
        <div class="p-3 text-end border-top">
          <button v-if="order.canCancel" class="btn btn-danger" @click="cancelOrder" :disabled="isCancelling">
            <span v-if="isCancelling" class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
            {{ isCancelling ? 'Đang xử lý...' : 'Hủy Đơn Hàng' }}
          </button>
          <button v-else class="btn btn-primary">Mua Lại</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useToast } from 'vue-toastification';
import axios from 'axios';

// --- KHỞI TẠO ---
const props = defineProps({
  idHD: {
    type: [String, Number],
    required: true
  }
});
const toast = useToast();
const order = ref(null);
const loading = ref(true);
const error = ref(null);
const animatedStepIndex = ref(-1);
const isCancelling = ref(false);
const API_BASE_URL = 'http://localhost:8080';

// --- CẤU HÌNH GIAO DIỆN ---
const timelineSteps = ref([
    { status: 'CHO_XAC_NHAN', name: 'Đã đặt', icon: 'fas fa-receipt' },
    { status: 'DA_XAC_NHAN', name: 'Đã xác nhận', icon: 'fas fa-check' },
    { status: 'DANG_GIAO', name: 'Đang giao', icon: 'fas fa-truck' },
    { status: 'DA_GIAO', name: 'Đã giao', icon: 'fas fa-check-circle' },
]);

const currentStepIndex = computed(() => {
    if (!order.value) return -1;
    return timelineSteps.value.findIndex((step) => step.status === order.value.currentStatus);
});

// --- LOGIC GỌI API VÀ XỬ LÝ DỮ LIỆU ---
const mapBackendDataToFrontend = (backendData) => {
    if (!backendData || !backendData.hoaDon || !backendData.chiTiet) {
        throw new Error("Cấu trúc dữ liệu từ API không hợp lệ.");
    }
    const hoaDon = backendData.hoaDon;
    const chiTiet = backendData.chiTiet;
    const products = chiTiet.map(item => ({
        id: item.idHDCT, 
        // Thử gán một giá trị cứng để xem giao diện có thay đổi không
        name: item.tenSanPham, 
        variant: { color: item.tenMauSac, size: item.tenSize },
        quantity: item.soLuong, 
        price: item.donGia, 
        imageUrl: item.hinhAnh || 'https://placehold.co/100'
    }));
    const subtotal = products.reduce((total, item) => total + (item.price * item.quantity), 0);
    return {
        id: hoaDon.maHD, currentStatus: hoaDon.trangThai, statusText: getStatusText(hoaDon.trangThai),
        canCancel: hoaDon.trangThai === 'CHO_XAC_NHAN',
        customerInfo: { name: hoaDon.tenKH, phone: hoaDon.sdt, address: hoaDon.diaChiGiao },
        products: products, summary: { subtotal: subtotal, shippingFee: 0, total: hoaDon.tongTien },
           createdAt: hoaDon.ngayTao,
        updatedAt: hoaDon.ngaySua,
    };
};

// --- VÒNG ĐỜI COMPONENT VÀ WATCHER ---
onMounted(async () => {
  if (!props.idHD) {
    error.value = "Mã đơn hàng không hợp lệ.";
    loading.value = false;
    return;
  }
  try {
    loading.value = true;
    const response = await axios.get(`${API_BASE_URL}/client/api/hoadon/${props.idHD}`);
    order.value = mapBackendDataToFrontend(response.data);
  } catch (err) {
    console.error("Lỗi khi tải chi tiết đơn hàng:", err);
    error.value = err.response?.data?.message || "Không thể tải chi tiết đơn hàng.";
  } finally {
    loading.value = false;
  }
});
const formatDateTime = (dateTimeString) => {
    if (!dateTimeString) return ''; // Trả về chuỗi rỗng nếu không có dữ liệu
    const date = new Date(dateTimeString);
    if (isNaN(date)) return 'Ngày không hợp lệ'; // Xử lý trường hợp ngày sai định dạng

    const time = date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
    const day = date.toLocaleDateString('vi-VN');
    return `${time} ${day}`;
};

watch(order, (newOrder) => {
  if (newOrder) {
    runTimelineAnimation();
  }
}, { immediate: true });

// --- CÁC HÀM HELPER ---
const runTimelineAnimation = () => {
    animatedStepIndex.value = -1;
    const totalSteps = currentStepIndex.value;
    if (totalSteps >= 0) {
        for (let i = 0; i <= totalSteps; i++) {
            setTimeout(() => {
                animatedStepIndex.value = i;
            }, i * 200);
        }
    }
};

const copyOrderId = () => {
    if (order.value?.id) {
        navigator.clipboard.writeText(order.value.id);
        toast.success('Đã sao chép mã đơn hàng!');
    }
};

const getStatusText = (status) => {
    const statusMap = { 'CHO_XAC_NHAN': 'Chờ xác nhận', 'DA_XAC_NHAN': 'Đã xác nhận', 'DANG_GIAO': 'Đang giao hàng', 'DA_GIAO': 'Đã giao', 'DA_HUY': 'Đã hủy' };
    return statusMap[status] || 'Không xác định';
};

const getStatusTextColor = (status) => {
    const classMap = { 'CHO_XAC_NHAN': 'text-secondary', 'DA_XAC_NHAN': 'text-info', 'DANG_GIAO': 'text-primary', 'DA_GIAO': 'text-success', 'DA_HUY': 'text-danger' };
    return classMap[status] || 'text-dark';
};

const handleImageError = (event) => {
    event.target.src = 'https://placehold.co/100';
};

const cancelOrder = async () => {
  if (!order.value) return;
  if (!confirm(`Bạn có chắc chắn muốn hủy đơn hàng #${order.value.id}?`)) return;

  isCancelling.value = true;
  try {
    const response = await axios.put(`${API_BASE_URL}/client/api/hoadon/${props.idHD}/cancel`);
    order.value.currentStatus = 'DA_HUY';
    order.value.statusText = getStatusText('DA_HUY');
    order.value.canCancel = false;
    toast.success(response.data.message || "Hủy đơn hàng thành công!");
  } catch (err) {
    console.error("Lỗi khi hủy đơn hàng:", err);
    toast.error(err.response?.data?.message || "Đã xảy ra lỗi khi hủy đơn hàng.");
  } finally {
    isCancelling.value = false;
  }
};
</script>

<style scoped>
/* Style của bạn giữ nguyên, đã bổ sung style cho trạng thái hủy */
.order-timeline { display: flex; align-items: flex-start; padding: 1.5rem 0.5rem; }
.timeline-step { text-align: center; position: relative; flex: 1; }
.timeline-icon { width: 40px; height: 40px; border-radius: 50%; background-color: #e9ecef; color: #adb5bd; display: inline-flex; align-items: center; justify-content: center; font-size: 1.5rem; position: relative; z-index: 2; transition: all 0.4s ease; border: 2px solid #e9ecef; }
.timeline-step-name { margin-top: 0.5rem; font-size: 0.8rem; font-weight: 500; color: #6c757d; }
.timeline-step:not(:last-child)::after { content: ''; position: absolute; top: 19px; left: 50%; width: 100%; height: 2px; background-color: #e9ecef; z-index: 1; transition: background-color 0.4s ease 0.2s; }
.timeline-step.completed .timeline-icon { background-color: #198754; border-color: #198754; color: #fff; }
.timeline-step.completed .timeline-step-name { color: #198754; font-weight: 700; }
.timeline-step.completed::after { background-color: #198754; }
.timeline-step.cancelled .timeline-icon { background-color: #dc3545; border-color: #dc3545; color: #fff; }
.timeline-step.cancelled .timeline-step-name { color: #dc3545; font-weight: 700; }
.order-timeline.is-cancelled .timeline-step.completed::after { background-color: #e9ecef; }
.order-timeline.is-cancelled .timeline-step:first-child.completed::after { background-color: #198754; }
.shipping-progress-bar { height: 6px; background-image: repeating-linear-gradient(-45deg, #dbeafe, #dbeafe 10px, #e9ecef 10px, #e9ecef 20px); border-top: 1px solid #dee2e6; border-bottom: 1px solid #dee2e6; }
.product-item-row { display: flex; align-items: center; padding: 1.25rem 0; border-bottom: 1px solid #f0f0f0; }
.product-item-row:last-child { border-bottom: none; }
.product-item-img { width: 80px; height: 80px; object-fit: cover; border-radius: 0.375rem; margin-right: 1.5rem; border: 1px solid #dee2e6; }
.product-item-info h6 { font-weight: 600; margin-bottom: 0.25rem; }
.product-item-info p { font-size: 0.85rem; color: #6c757d; margin-bottom: 0; }
.product-item-price { margin-left: auto; text-align: right; white-space: nowrap; }
.payment-summary-card { background-color: #f8f9fa; border-top: 1px solid #dee2e6; padding: 0.5rem 1.5rem; }
.price-summary-row { display: flex; justify-content: space-between; padding: 0.75rem 0; }
.price-summary-row span:first-child { color: #6c757d; }
.price-summary-row.total { border-top: 1px dashed #ced4da; padding-top: 1rem; margin-top: 0.5rem; }
.cancelled-notice { text-align: left; background-color: #fffaf0; padding: 1.5rem; border: 1px solid #ffe5b8; border-radius: 0.25rem; }
.cancelled-title { font-weight: 600; margin-bottom: 0.25rem; font-size: 1.1rem; }
.cancelled-time { font-size: 0.9rem; }
</style>