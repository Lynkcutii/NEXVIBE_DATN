<template>
  <div class="container py-4">
    <!-- 1. Trạng thái Loading -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" style="width: 3rem; height: 3rem;" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p class="mt-3">Đang tải chi tiết đơn hàng...</p>
    </div>

    <!-- 2. Trạng thái Lỗi -->
    <div v-else-if="error" class="alert alert-danger text-center">
      <h4>Lỗi!</h4>
      <p>{{ error }}</p>
      <router-link to="/order-history" class="btn btn-primary">Quay lại Lịch sử đơn hàng</router-link>
    </div>
    
    <!-- 3. Giao diện chính: Chỉ hiển thị khi đã có dữ liệu `order` -->
    <div v-else-if="order">
      <div class="card border-0 shadow-sm">
        <div class="card-body p-0">
          <!-- Header -->
          <div class="d-flex justify-content-between align-items-center p-3 border-bottom">
            <router-link to="/order-history" class="btn btn-link text-dark text-decoration-none ps-0"><i class="fas fa-arrow-left me-2"></i> TRỞ LẠI</router-link>
            <div>
              <span class="text-muted me-3">MÃ ĐƠN HÀNG: {{ order.id }}</span>
              <button @click="copyOrderId" class="btn btn-sm btn-outline-secondary me-3" title="Sao chép mã đơn hàng"><i class="far fa-copy"></i></button>
              <span class="text-danger fw-bold text-uppercase">{{ order.statusText }}</span>
            </div>
          </div>

          <!-- Timeline -->
          <div class="p-4 border-bottom">
            <div class="order-timeline">
              <div v-for="(step, index) in timelineSteps" :key="step.status" class="timeline-step" :class="{ 'completed': animatedStepIndex >= index }">
                <div class="timeline-icon"><i :class="step.icon"></i></div>
                <div class="timeline-step-name">{{ step.name }}</div>
                <div v-if="order.statusHistory[step.status]" class="timeline-step-time">{{ formatDateTime(order.statusHistory[step.status]) }}</div>
              </div>
            </div>
          </div>
          
          <!-- Đường kẻ sọc -->
          <div class="shipping-progress-bar"></div>

          <!-- Địa chỉ & Lịch sử vận chuyển -->
          <div class="p-4 border-bottom">
            <div class="row">
              <div class="col-md-5">
                <h5 class="mb-3">Địa chỉ Nhận Hàng</h5>
                <p class="fw-bold mb-1">{{ order.customerInfo.name }}</p>
                <p class="text-muted mb-1">{{ order.customerInfo.phone }}</p>
                <p class="text-muted mb-0">{{ order.customerInfo.address }}</p>
              </div>
              <div class="col-md-7 ps-md-5 mt-4 mt-md-0">
                <ul class="shipping-history-list">
                  <li v-for="event in shippingHistory" :key="event.time" class="timeline-item" :class="{ 'completed': event.completed }">
                    <div class="timeline-item-content">
                      <p class="time-text mb-1">{{ formatDateTime(event.time) }}</p>
                      <p class="status-text mb-1">{{ event.status }}</p>
                      <p class="description-text text-muted mb-0">{{ event.description }}</p>
                    </div>
                  </li>
                </ul>
              </div>
            </div>
          </div>

          <!-- Thông tin sản phẩm -->
          <div class="p-3">
             <div v-for="item in order.products" :key="item.id" class="product-item-row px-2">
              <img :src="item.imageUrl" class="product-item-img">
              <div class="product-item-info"><h6>{{ item.name }}</h6><p>Phân loại: {{ item.variant.color }}, {{ item.variant.size }}</p></div>
              <div class="product-item-price"><h6 class="text-dark fw-bold mb-0">{{ item.price.toLocaleString() }}đ</h6></div>
            </div>
          </div>

          <!-- Tổng kết thanh toán -->
          <div class="payment-summary-card">
              <div class="price-summary-row"><span>Tổng tiền hàng</span><span>{{ order.summary.subtotal.toLocaleString() }}đ</span></div>
              <div class="price-summary-row"><span>Phí vận chuyển</span><span>{{ order.summary.shippingFee.toLocaleString() }}đ</span></div>
              <div class="price-summary-row"><span>Giảm giá</span><span class="text-success">-{{ order.summary.shippingDiscount.toLocaleString() }}đ</span></div>
              <div class="price-summary-row total h5"><span>Thành tiền</span><span class="fw-bolder text-danger">{{ order.summary.total.toLocaleString() }}đ</span></div>
          </div>
          
          <!-- Nút hành động -->
          <div class="p-3 text-end border-top">
            <button class="btn btn-outline-secondary me-2">Liên hệ Người bán</button>
            <button v-if="order.canCancel" class="btn btn-danger">Hủy Đơn Hàng</button>
            <button v-else class="btn btn-primary">Mua Lại</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useToast } from 'vue-toastification';

const props = defineProps(['id']);
const orderId = ref(props.id);
const toast = useToast();

const order = ref(null);
const loading = ref(true);
const error = ref(null);
const animatedStepIndex = ref(-1);

// Định nghĩa các bước và thứ tự của chúng
const timelineSteps = ref([
    { status: 'PENDING', name: 'Đơn hàng đã đặt', icon: 'fas fa-receipt' },
    { status: 'PAID', name: 'Đã thanh toán', icon: 'fas fa-dollar-sign' },
    { status: 'PROCESSING', name: 'Đang chuẩn bị', icon: 'fas fa-box-open' },
    { status: 'SHIPPING', name: 'Đang vận chuyển', icon: 'fas fa-truck' },
    { status: 'COMPLETED', name: 'Đã giao', icon: 'fas fa-check-circle' },
]);

const shippingHistory = ref([]);

// Tính toán index của bước hiện tại
const currentStepIndex = computed(() => {
    if (!order.value) return -1;
    return timelineSteps.value.findIndex(step => step.status === order.value.currentStatus);
});

// Hàm chạy animation cho timeline
const runTimelineAnimation = () => {
    animatedStepIndex.value = -1;
    const totalSteps = currentStepIndex.value;
    if (totalSteps >= 0) {
        let i = 0;
        const interval = setInterval(() => {
            if (i <= totalSteps) {
                animatedStepIndex.value = i++;
            } else {
                clearInterval(interval);
            }
        }, 200);
    }
};

// Hàm định dạng ngày giờ
const formatDateTime = (dateTimeString) => {
    if (!dateTimeString) return '';
    const date = new Date(dateTimeString);
    return `${date.toLocaleTimeString('vi-VN', {hour: '2-digit', minute:'2-digit'})} ${date.toLocaleDateString('vi-VN')}`;
};

// Hàm sao chép mã đơn hàng
const copyOrderId = () => {
    navigator.clipboard.writeText(order.value.id);
    toast.success("Đã sao chép mã đơn hàng!");
};

// Vòng đời component - Gọi khi component được tạo
onMounted(async () => {
    loading.value = true;
    try {
        // Giả lập gọi API, thay thế bằng axios khi có backend
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // Dữ liệu mẫu trả về từ API, thay đổi currentStatus để test
        order.value = {
            id: '250711S8KVXJU0',
            currentStatus: 'PROCESSING', // <-- THAY ĐỔI TRẠNG THÁI Ở ĐÂY ĐỂ TEST
            statusText: 'NGƯỜI GỬI ĐANG CHUẨN BỊ HÀNG',
            canCancel: true,
            statusHistory: { 
                PENDING: '2025-07-11T00:37:00', 
                PAID: '2025-07-11T00:37:00', 
                PROCESSING: '2025-07-11T06:53:00'
            },
            customerInfo: { name: 'Hoàng Nghĩa', phone: '(+84) 816169895', address: '239 ngõ 253 đường Xuân Phương, Hà Nội' },
            products: [{ id: 1, name: 'Áo Thun Năng Động', variant: { color: 'Đen', size: 'M' }, quantity: 1, price: 350000, imageUrl: 'https://picsum.photos/100/100?random=1' }],
            summary: { subtotal: 350000, shippingFee: 12800, shippingDiscount: 12800, total: 350000 }
        };
        
        shippingHistory.value = [
            { time: '2025-07-11T06:53:00', status: 'Đang được chuẩn bị', description: 'Người gửi đang chuẩn bị hàng', completed: true },
            { time: '2025-07-11T00:37:00', status: 'Đặt hàng thành công', description: 'Đơn hàng đã được đặt', completed: false }
        ];

        // Chạy animation sau khi có dữ liệu
        runTimelineAnimation();
    } catch (err) { 
        error.value = "Không thể tải chi tiết đơn hàng."; 
    } finally { 
        loading.value = false; 
    }
});
</script>

<style scoped>
/* Timeline ngang */
.order-timeline { display: flex; align-items: flex-start; padding: 1.5rem 0.5rem; }
.timeline-step { text-align: center; position: relative; flex: 1; }
.timeline-icon { width: 40px; height: 40px; border-radius: 50%; background-color: #e9ecef; color: #adb5bd; display: inline-flex; align-items: center; justify-content: center; font-size: 1.5rem; position: relative; z-index: 2; transition: all 0.4s ease; border: 2px solid #e9ecef; }
.timeline-step-name { margin-top: 0.5rem; font-size: 0.8rem; font-weight: 500; color: #6c757d; }
.timeline-step-time { font-size: 0.75rem; color: #adb5bd; }
.timeline-step:not(:last-child)::after { content: ''; position: absolute; top: 19px; left: 50%; width: 100%; height: 2px; background-color: #e9ecef; z-index: 1; transition: background-color 0.4s ease 0.2s; }
.timeline-step.completed .timeline-icon { background-color: #198754; border-color: #198754; color: #fff; }
.timeline-step.completed .timeline-step-name { color: #198754; font-weight: 700; }
.timeline-step.completed::after { background-color: #198754; }

/* Timeline dọc */
.shipping-history-list { list-style: none; padding-left: 1.25rem; position: relative; }
.shipping-history-list::before { content: ''; position: absolute; left: 0; top: 10px; bottom: 10px; width: 2px; background-color: #dee2e6; }
.shipping-history-item { position: relative; padding-left: 1.5rem; padding-bottom: 1.75rem; }
.shipping-history-item:last-child { padding-bottom: 0; }
.shipping-history-item::before { content: ''; position: absolute; left: -5px; top: 8px; width: 12px; height: 12px; border-radius: 50%; background-color: #adb5bd; border: 2px solid #fff; z-index: 1; }
.shipping-history-item.completed::before { content: '\f00c'; font-family: 'Font Awesome 6 Free'; font-weight: 900; background-color: #198754; color: white; display: flex; align-items: center; justify-content: center; font-size: 0.6rem; width: 16px; height: 16px; left: -9px; top: 5px; }

/* Các thành phần khác */
.shipping-progress-bar { height: 6px; background-image: repeating-linear-gradient(-45deg, #dbeafe, #dbeafe 10px, #fee2e2 10px, #fee2e2 20px); border-top: 1px solid #e9ecef; border-bottom: 1px solid #e9ecef; }
.info-card { background-color: #fff; border-radius: 0; }
.product-item-row { display: flex; align-items: center; padding: 1.5rem 0; border-bottom: 1px solid #f0f0f0; }
.product-item-row:last-child { border-bottom: none; }
.product-item-img { width: 80px; height: 80px; object-fit: cover; border-radius: 0.25rem; margin-right: 1rem; }
.product-item-info h6 { font-weight: 600; margin-bottom: 0.25rem; }
.product-item-info p { font-size: 0.85rem; color: #6c757d; margin-bottom: 0; }
.product-item-price { margin-left: auto; text-align: right; }
.payment-summary-card { background-color: #fff8f5; border-top: 1px solid #e9ecef; }
.price-summary-row { display: flex; justify-content: space-between; padding: 0.75rem 1.5rem; }
.price-summary-row span:first-child { color: #6c757d; }
.price-summary-row.total { border-top: 1px solid #f0e0da; }
</style>