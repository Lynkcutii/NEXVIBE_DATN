<template>
  <div class="checkout-page">
    <div class="container-fluid px-lg-5 py-4">
      <div class="row g-4">

        <!-- ========================== -->
        <!-- CỘT TRÁI: THÔNG TIN VẬN CHUYỂN & HÌNH THỨC THANH TOÁN -->
        <!-- ========================== -->
        <div class="col-lg-5">
          <!-- Card Thông tin vận chuyển -->
          <div class="checkout-card">
            <div class="d-flex justify-content-between align-items-center mb-4">
              <h5 class="mb-0 fw-bold">Thông tin vận chuyển</h5>
              <a href="#" class="btn btn-sm btn-outline-primary"><i class="fas fa-book me-1"></i> Chọn từ sổ địa chỉ</a>
            </div>
            <form class="checkout-form">
              <div class="row g-3">
                <div class="col-md-6"><input type="text" class="form-control" placeholder="Họ" v-model="shippingInfo.firstName"></div>
                <div class="col-md-6"><input type="text" class="form-control" placeholder="Tên" v-model="shippingInfo.lastName"></div>
                <div class="col-12"><input type="email" class="form-control" placeholder="Email" v-model="shippingInfo.email"></div>
                <div class="col-12"><input type="text" class="form-control" placeholder="Số điện thoại" v-model="shippingInfo.phone"></div>
                <div class="col-12"><input type="text" class="form-control" placeholder="Địa chỉ" v-model="shippingInfo.address"></div>
                <div class="col-md-6"><select class="form-select"><option selected>Chọn tỉnh/thành phố</option></select></div>
                <div class="col-md-6"><select class="form-select"><option selected>Chọn quận/huyện</option></select></div>
                <div class="col-12"><textarea class="form-control" rows="2" placeholder="Ghi chú (tùy chọn)" v-model="shippingInfo.notes"></textarea></div>
              </div>
            </form>
          </div>

          <!-- Card Hình thức thanh toán -->
          <div class="checkout-card">
            <h5 class="mb-4 fw-bold">Hình thức thanh toán</h5>
            <div class="d-grid gap-3">
              <label v-for="method in paymentMethods" :key="method.id" class="payment-option" :class="{ 'active': selectedPaymentMethod === method.id }">
                <div class="d-flex align-items-center">
                    <input type="radio" class="form-check-input" name="paymentMethod" :value="method.id" v-model="selectedPaymentMethod">
                    <img v-if="method.logo" :src="method.logo" :alt="method.name" class="payment-logo mx-3">
                    <i v-else :class="method.icon" class="fa-lg mx-3 text-muted"></i>
                    <div><strong class="d-block">{{ method.name }}</strong><small class="text-muted" v-if="method.description">{{ method.description }}</small></div>
                </div>
              </label>
            </div>
          </div>
        </div>

        <!-- ========================== -->
        <!-- CỘT PHẢI: GIỎ HÀNG & THANH TOÁN -->
        <!-- ========================== -->
        <div class="col-lg-7">
          <div class="checkout-card position-sticky" style="top: 20px;">
            <!-- Giỏ hàng -->
            <div class="d-flex justify-content-between align-items-center mb-3">
              <div class="form-check"><input type="checkbox" class="form-check-input" id="selectAll"><label for="selectAll" class="form-check-label ms-2 fw-bold">Tất cả ({{ cart.totalItems }} sản phẩm)</label></div>
              <button v-if="cart.items.length > 0" @click="cart.clearCart()" class="btn btn-sm btn-link text-danger">Xóa tất cả</button>
            </div>
            <div class="cart-summary-list">
              <div v-if="cart.items.length > 0">
                <div v-for="item in cart.items" :key="item.uniqueId" class="cart-summary-item">
                  <input type="checkbox" class="form-check-input mt-1 me-3">
                  <img :src="item.imageUrl" class="cart-summary-img">
                  <div class="cart-summary-details">
                    <h6>{{ item.name }}</h6>
                    <p>Phân loại: {{ item.variant.color }}, {{ item.variant.size }}</p>
                  </div>
                  <div class="cart-summary-price text-end">
                    <div class="fw-bold">{{ (item.price * item.quantity).toLocaleString() }}đ</div>
                    <small class="text-muted text-decoration-line-through">399.000đ</small>
                  </div>
                </div>
              </div>
            </div>

           <!-- TÓM TẮT THANH TOÁN (Thay thế nội dung khối này) -->
<div class="checkout-summary-box mt-4">
  <!-- Nút Ví Voucher và Ô nhập mã -->
  <div class="d-flex justify-content-between align-items-center mb-3">
    <button @click="openVoucherModal" class="btn btn-sm btn-outline-primary">
      <i class="fas fa-ticket-alt me-2"></i>Ví Voucher
      <span v-if="availableVouchers.length > 0" class="badge bg-danger ms-2">{{ availableVouchers.length }}</span>
    </button>
    <div class="input-group w-50">
      <input type="text" class="form-control" placeholder="Nhập mã" v-model="voucherCodeInput">
      <button class="btn btn-dark" @click="applyVoucherByCode">Áp dụng</button>
    </div>
  </div>
  <hr>
  <!-- Chi tiết thanh toán -->
  <div class="payment-summary">
    <div class="summary-row"><span>Tạm tính</span><span>{{ cart.subTotal.toLocaleString() }}đ</span></div>
    <div class="summary-row"><span>Phí giao hàng</span><span>+ {{ shippingFee.toLocaleString() }}đ</span></div>
    <!-- Hiển thị voucher đã áp dụng -->
    <div v-if="appliedVoucher" class="summary-row">
      <span>Voucher ({{ appliedVoucher.code }})</span>
      <span class="text-success">-{{ discount.toLocaleString() }}đ</span>
    </div>
    <div class="summary-row total">
      <span>Thành tiền</span>
      <span class="text-danger">{{ total.toLocaleString() }}đ</span>
    </div>
  </div>
</div>
          </div>
        </div>

      </div>
    </div>
    
    <!-- THANH ĐẶT HÀNG CỐ ĐỊNH Ở DƯỚI -->
    <div class="checkout-footer">
        <div class="container-fluid px-lg-5 d-flex justify-content-between align-items-center">
            <p class="mb-0 small text-muted">Nếu bạn không hài lòng, bạn có thể trả lại sản phẩm.</p>
            <div class="d-flex align-items-center">
                <div class="me-4 text-end">
                    <small class="text-muted">Tổng thanh toán:</small>
                    <div class="h4 fw-bolder text-danger mb-0">{{ total.toLocaleString() }}đ</div>
                </div>
                <button @click="placeOrder" class="btn btn-primary btn-lg" style="min-width: 200px;">ĐẶT HÀNG</button>
            </div>
        </div>
    </div>
    <!-- ========================================================== -->
<!-- MODAL VÍ VOUCHER (Dán khối này vào cuối template) -->
<!-- ========================================================== -->
<div class="modal fade" id="voucherModal" tabindex="-1" aria-labelledby="voucherModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-scrollable modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="voucherModalLabel">Ví voucher của bạn</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
          <div class="alert alert-info small">Mỗi đơn hàng chỉ có thể sử dụng 1 mã giảm giá.</div>
          <div v-if="availableVouchers.length === 0" class="text-center text-muted p-5">
              <p>Bạn không có voucher nào phù hợp.</p>
          </div>
          <div v-else class="voucher-list">
              <!-- Lặp qua danh sách voucher -->
              <div v-for="voucher in allVouchers" :key="voucher.id" 
                   class="voucher-item p-3 mb-3" 
                   :class="{ 'disabled': !isVoucherApplicable(voucher) }">
                  <div class="d-flex">
                      <div class="voucher-info flex-grow-1">
                          <h6 class="fw-bold mb-1">{{ voucher.name }} ({{ voucher.code }})</h6>
                          <p class="mb-1 small">{{ voucher.description }}</p>
                          <p class="text-muted small mb-0">HSD: {{ voucher.endDate }}</p>
                      </div>
                      <div class="voucher-select d-flex align-items-center">
                          <input 
                              type="radio" 
                              class="form-check-input" 
                              name="voucherRadio" 
                              :value="voucher.id"
                              :checked="appliedVoucher?.id === voucher.id"
                              :disabled="!isVoucherApplicable(voucher)"
                              @change="selectVoucher(voucher)">
                      </div>
                  </div>
              </div>
          </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
      </div>
    </div>
  </div>
</div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue';
import { Modal } from 'bootstrap'; // Import Modal của Bootstrap
import { useCartStore } from '@/stores/cart';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';

const cart = useCartStore();
const auth = useAuthStore();
const router = useRouter();
const toast = useToast();

const shippingInfo = reactive({ name: auth.user?.name || '', email: auth.user?.email || '', phone: '', address: '', notes: '' });
const selectedPaymentMethod = ref('COD');
const shippingFee = ref(25000);
const voucherCodeInput = ref('');
const appliedVoucher = ref(null); // Lưu voucher đang được áp dụng

const paymentMethods = ref([
    { id: 'COD', name: 'Thanh toán khi nhận hàng', description: 'Thanh toán bằng tiền mặt', icon: 'fas fa-truck' },
    { id: 'VNPAY', name: 'Ví điện tử VNPAY', description: 'Quét QR để thanh toán', logo: '/img/payment/vnpay.png' },
]);

// --- LOGIC VOUCHER ---
let voucherModalInstance = null;
const allVouchers = ref([
    { id: 1, code: 'WELCOME50K', name: 'Giảm 50K cho đơn đầu tiên', description: 'Giảm 50,000đ cho đơn từ 200,000đ', type: 'fixed', value: 50000, minOrder: 200000, endDate: '31/07/2025' },
    { id: 2, code: 'NEXVIBE10', name: 'Giảm 10% toàn cửa hàng', description: 'Giảm tối đa 30,000đ', type: 'percentage', value: 10, maxDiscount: 30000, minOrder: 0, endDate: '31/07/2025' },
    { id: 3, code: 'FREESHIP', name: 'Miễn phí vận chuyển', description: 'Giảm tối đa 25,000đ phí ship', type: 'fixed', value: 25000, minOrder: 150000, endDate: '31/07/2025' },
    { id: 4, code: 'BIGSALE', name: 'Voucher không đủ điều kiện', description: 'Giảm 100K cho đơn từ 2.000.000đ', type: 'fixed', value: 100000, minOrder: 2000000, endDate: '31/07/2025' },
]);

const isVoucherApplicable = (voucher) => {
    return cart.subTotal >= voucher.minOrder;
};

const availableVouchers = computed(() => {
    return allVouchers.value.filter(isVoucherApplicable);
});

const discount = computed(() => {
    if (!appliedVoucher.value) return 0;
    if (appliedVoucher.value.type === 'percentage') {
        const discountAmount = cart.subTotal * (appliedVoucher.value.value / 100);
        return Math.min(discountAmount, appliedVoucher.value.maxDiscount || Infinity);
    }
    return appliedVoucher.value.value;
});

const total = computed(() => {
    const finalTotal = cart.subTotal + shippingFee.value - discount.value;
    return finalTotal > 0 ? finalTotal : 0;
});

const openVoucherModal = () => voucherModalInstance?.show();

const selectVoucher = (voucher) => {
    appliedVoucher.value = voucher;
    voucherCodeInput.value = voucher.code;
    voucherModalInstance.hide();
    toast.success(`Đã áp dụng voucher: ${voucher.name}`);
};

const applyVoucherByCode = () => {
    const foundVoucher = allVouchers.value.find(v => v.code.toLowerCase() === voucherCodeInput.value.toLowerCase());
    if (foundVoucher) {
        if (isVoucherApplicable(foundVoucher)) {
            selectVoucher(foundVoucher);
        } else {
            toast.error(`Đơn hàng của bạn cần đạt tối thiểu ${foundVoucher.minOrder.toLocaleString()}đ để áp dụng voucher này.`);
        }
    } else {
        toast.error("Mã voucher không hợp lệ.");
    }
};

const placeOrder = () => { /* ... */ };

onMounted(() => {
    const modalElement = document.getElementById('voucherModal');
    if (modalElement) voucherModalInstance = new Modal(modalElement);
});

onUnmounted(() => {
    voucherModalInstance?.dispose();
});
</script>

<style scoped>
.cart-summary-list {
    max-height: 45vh;
    overflow-y: auto;
}
/* Dán vào trong <style scoped> */
.voucher-list {
    max-height: 60vh;
    overflow-y: auto;
    padding-right: 10px; /* Tạo khoảng trống cho thanh cuộn */
}
.voucher-item {
    border: 1px solid #e9ecef;
    border-left: 5px solid #0d6efd; /* Đường kẻ màu xanh bên trái */
    border-radius: 0.5rem;
    background-color: #fff;
    cursor: pointer;
    transition: all 0.2s ease;
}
.voucher-item:hover {
    border-color: var(--bs-primary);
    box-shadow: 0 4px 15px rgba(0,0,0,0.07);
}
.voucher-item.disabled {
    background-color: #f8f9fa;
    opacity: 0.6;
    cursor: not-allowed;
    border-left-color: #ced4da;
}
.voucher-item.disabled:hover {
    border-color: #e9ecef;
    box-shadow: none;
}
.voucher-item .form-check-input {
    width: 1.5em;
    height: 1.5em;
}
</style>