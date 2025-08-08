```vue
<template>
  <div class="checkout-page">
    <div class="container-fluid px-lg-5 py-4">
      <div class="row g-4">
        <!-- CỘT TRÁI: THÔNG TIN VẬN CHUYỂN & HÌNH THỨC THANH TOÁN -->
        <div class="col-lg-5">
          <div class="checkout-card">
            <div class="d-flex justify-content-between align-items-center mb-4">
              <h5 class="mb-0 fw-bold">Thông tin vận chuyển</h5>
              <a href="#" class="btn btn-sm btn-outline-primary"><i class="fas fa-book me-1"></i> Chọn từ sổ địa chỉ</a>
            </div>
            <form class="checkout-form" @submit.prevent="validateAndPlaceOrder">
              <div class="row g-3">
                <div class="col-md-6">
                  <input type="text" class="form-control" placeholder="Họ" v-model="shippingInfo.firstName" required
                         :class="{ 'is-invalid': errors.firstName }">
                  <div v-if="errors.firstName" class="invalid-feedback">{{ errors.firstName }}</div>
                </div>
                <div class="col-md-6">
                  <input type="text" class="form-control" placeholder="Tên" v-model="shippingInfo.lastName" required
                         :class="{ 'is-invalid': errors.lastName }">
                  <div v-if="errors.lastName" class="invalid-feedback">{{ errors.lastName }}</div>
                </div>
                <div class="col-12">
                  <input type="email" class="form-control" placeholder="Email" v-model="shippingInfo.email" required
                         :class="{ 'is-invalid': errors.email }">
                  <div v-if="errors.email" class="invalid-feedback">{{ errors.email }}</div>
                </div>
                <div class="col-12">
                  <input type="text" class="form-control" placeholder="Số điện thoại" v-model="shippingInfo.phone" required
                         :class="{ 'is-invalid': errors.phone }">
                  <div v-if="errors.phone" class="invalid-feedback">{{ errors.phone }}</div>
                </div>
                <div class="col-12">
                  <input type="text" class="form-control" placeholder="Địa chỉ" v-model="shippingInfo.address" required
                         :class="{ 'is-invalid': errors.address }">
                  <div v-if="errors.address" class="invalid-feedback">{{ errors.address }}</div>
                </div>
                <div class="col-12">
                  <textarea class="form-control" rows="2" placeholder="Ghi chú (tùy chọn)" v-model="shippingInfo.notes"></textarea>
                </div>
              </div>
            </form>
          </div>

          <div class="checkout-card">
            <h5 class="mb-4 fw-bold">Hình thức thanh toán</h5>
            <div class="d-grid gap-3">
              <label v-for="method in paymentMethods" :key="method.id" class="payment-option"
                     :class="{ 'active': selectedPaymentMethod === method.id }">
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

        <!-- CỘT PHẢI: GIỎ HÀNG & THANH TOÁN -->
        <div class="col-lg-7">
          <div class="checkout-card position-sticky" style="top: 20px;">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h5 class="fw-bold">Giỏ hàng ({{ selectedItems.length }} sản phẩm)</h5>
              <button v-if="selectedItems.length > 0" @click="clearSelectedItems" class="btn btn-sm btn-link text-danger">Xóa tất cả</button>
            </div>
            <div class="cart-summary-list">
              <div v-if="selectedItems.length > 0">
                <div v-for="item in selectedItems" :key="item.idGHCT" class="cart-summary-item">
                  <img :src="item.imageUrl || 'https://placehold.co/150'" class="cart-summary-img" :alt="item.name">
                  <div class="cart-summary-details">
                    <h6>{{ item.name }}</h6>
                    <p>Phân loại: {{ item.mauSac }}, {{ item.kichThuoc }}</p>
                    <!-- Dropdown voucher cho từng sản phẩm -->
                    <div class="voucher-selection mt-2">
                      <select class="form-select form-select-sm" v-model="item.selectedVoucherId" @change="applyVoucherToItem(item)">
                        <option value="null" selected>Chọn voucher</option>
                        <option v-for="voucher in item.applicableVouchers" :key="voucher.id" :value="voucher.id">
                          {{ voucher.name }} ({{ voucher.code }}) - {{ voucher.description }}
                        </option>
                      </select>
                    </div>
                    <!-- Điều chỉnh số lượng -->
                    <div class="quantity-control mt-2 d-flex align-items-center">
                      <button class="btn btn-sm btn-outline-secondary" @click="updateItemQuantity(item, -1)" :disabled="item.soLuong <= 1">-</button>
                      <input type="number" class="form-control form-control-sm mx-2" v-model.number="item.soLuong" style="width: 60px;" min="1" @change="validateQuantity(item)">
                      <button class="btn btn-sm btn-outline-secondary" @click="updateItemQuantity(item, 1)">+</button>
                    </div>
                  </div>
                  <div class="cart-summary-price text-end">
                    <div class="fw-bold">{{ calculateItemTotal(item).toLocaleString('vi-VN') }}đ</div>
                    <div v-if="item.selectedVoucherId" class="text-success small">
                      Giảm: {{ calculateItemDiscount(item).toLocaleString('vi-VN') }}đ
                    </div>
                  </div>
                </div>
              </div>
              <div v-else class="text-center text-muted p-5">
                <p>Không có sản phẩm nào được chọn.</p>
                <router-link to="/cart" class="btn btn-dark">Quay lại giỏ hàng</router-link>
              </div>
            </div>

            <div class="checkout-summary-box mt-4">
              <div class="promotion-selection mt-3">
                <label for="promotionSelect" class="form-label fw-bold">Chọn khuyến mãi cho đơn hàng:</label>
                <select id="promotionSelect" class="form-select" v-model="selectedPromotionId" @change="applyPromotionToOrder">
                  <option value="null" selected>Không áp dụng khuyến mãi</option>
                  <option v-for="promotion in applicablePromotions" :key="promotion.id" :value="promotion.id">
                    {{ promotion.name }} ({{ promotion.code }}) - {{ promotion.description }}
                  </option>
                </select>
                <div v-if="selectedPromotionId" class="text-success small mt-2">
                  Giảm: {{ calculateOrderPromotionDiscount().toLocaleString('vi-VN') }}đ
                </div>
              </div>
              <div class="payment-summary mt-3">
                <div class="summary-row"><span>Tạm tính</span><span>{{ subTotal.toLocaleString('vi-VN') }}đ</span></div>
                <div class="summary-row"><span>Phí giao hàng</span><span>+ {{ shippingFee.toLocaleString('vi-VN') }}đ</span></div>
                <div class="summary-row">
                  <span>Tổng giảm giá</span>
                  <span class="text-success">-{{ totalDiscount.toLocaleString('vi-VN') }}đ</span>
                </div>
                <div class="summary-row total">
                  <span>Thành tiền</span>
                  <span class="text-danger">{{ totalDisplay.toLocaleString('vi-VN') }}đ</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="checkout-footer">
      <div class="container-fluid px-lg-5 d-flex justify-content-between align-items-center">
        <p class="mb-0 small text-muted">Nếu bạn không hài lòng, bạn có thể trả lại sản phẩm.</p>
        <div class="d-flex align-items-center">
          <div class="me-4 text-end">
            <small class="text-muted">Tổng thanh toán:</small>
            <div class="h4 fw-bolder text-danger mb-0">{{ totalDisplay.toLocaleString('vi-VN') }}đ</div>
          </div>
          <button @click="validateAndPlaceOrder" class="btn btn-primary btn-lg" style="min-width: 200px;"
                  :disabled="selectedItems.length === 0 || isSubmitting || !selectedPaymentMethod">
            {{ isSubmitting ? 'Đang xử lý...' : 'ĐẶT HÀNG' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Modal hiển thị lỗi -->
    <div class="modal fade" id="errorModal" tabindex="-1" aria-labelledby="errorModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="errorModalLabel">Lỗi khi đặt hàng</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <p class="text-danger">{{ errorMessage }}</p>
            <ul v-if="errorDetails.length > 0" class="mb-0">
              <li v-for="(detail, index) in errorDetails" :key="index" class="text-muted">{{ detail }}</li>
            </ul>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
            <button v-if="errorRequiresLogin" @click="redirectToLogin" class="btn btn-primary">Đăng nhập</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue';
import { Modal } from 'bootstrap';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';
import axios from 'axios';

const auth = useAuthStore();
const router = useRouter();
const toast = useToast();

const API_BASE_URL = 'http://localhost:8080';

const shippingInfo = reactive({
  firstName: '',
  lastName: '',
  phone: '',
  address: '',
  notes: '',
  email: ''
});

const errors = reactive({
  firstName: '',
  lastName: '',
  phone: '',
  address: '',
  email: ''
});

const selectedPaymentMethod = ref(null);
const shippingFee = ref(25000);
const selectedItems = ref([]);
const paymentMethods = ref([]);
const applicablePromotions = ref([]);
const selectedPromotionId = ref(null);
const isSubmitting = ref(false);
const errorMessage = ref('');
const errorDetails = ref([]);
const errorRequiresLogin = ref(false);

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
};

// Tính toán tổng phụ (tổng giá sản phẩm trước giảm giá)
const subTotal = computed(() => {
  return selectedItems.value.reduce((total, item) => total + item.donGia * item.soLuong, 0);
});

// Tải danh sách phương thức thanh toán từ backend
const loadPaymentMethods = async () => {
  try {
    console.log('loadPaymentMethods: Starting');
    const response = await axios.get(`${API_BASE_URL}/api/phuongtt`, {
      withCredentials: true
    });
    paymentMethods.value = response.data
      .filter(method => [1, 2].includes(method.idPTT))
      .map(method => ({
        id: method.idPTT,
        name: method.ten,
        description: method.ten === 'THANH TOAN KHI NHAN HANG' ? 'Thanh toán khi nhận hàng' : 'Quét QR để thanh toán',
        icon: method.ten === 'THANH TOAN KHI NHAN HANG' ? 'fas fa-truck' : null,
        logo: method.ten === 'CHUYEN KHOAN' ? '/img/payment/vnpay.png' : null,
        value: method.idPTT
      }));
    if (paymentMethods.value.length > 0) {
      selectedPaymentMethod.value = paymentMethods.value[0].id;
    }
    console.log('loadPaymentMethods: Loaded', paymentMethods.value);
  } catch (err) {
    console.error('loadPaymentMethods: Error', {
      message: err.message,
      response: err.response ? {
        status: err.response.status,
        data: err.response.data
      } : null
    });
    errorMessage.value = 'Lỗi khi tải phương thức thanh toán';
    errorDetails.value = [err.response?.data?.message || err.message];
    showErrorModal();
  }
};

// Tải thông tin khách hàng từ API
const loadCustomerInfo = async () => {
  console.log('loadCustomerInfo: Starting, auth.user', auth.user);
  if (!auth.isAuthenticated || !auth.user?.idTK) {
    console.log('loadCustomerInfo: User not authenticated or idTK missing');
    errorMessage.value = 'Vui lòng đăng nhập để tiếp tục';
    errorDetails.value = ['Bạn cần đăng nhập để tải thông tin khách hàng.'];
    errorRequiresLogin.value = true;
    showErrorModal();
    return;
  }

  try {
    console.log('loadCustomerInfo: Fetching customer info for idTK=', auth.user.idTK);
    const response = await axios.get(`${API_BASE_URL}/client/api/khachhang/byTaiKhoanId/${auth.user.idTK}`, {
      withCredentials: true
    });

    const customer = response.data;
    console.log('loadCustomerInfo: Customer data fetched', customer);

    if (customer.tenKH) {
      const nameParts = customer.tenKH.trim().split(' ');
      shippingInfo.firstName = nameParts[0] || '';
      shippingInfo.lastName = nameParts.slice(1).join(' ') || '';
    }
    shippingInfo.phone = customer.sdt || '';
    shippingInfo.address = customer.diaChiList?.[0]?.diaChiCuThe || '';
    shippingInfo.notes = customer.diaChiList?.[0]?.ghiChu || '';
    shippingInfo.email = customer.email || '';
  } catch (err) {
    console.error('loadCustomerInfo: Error', {
      message: err.message,
      response: err.response ? {
        status: err.response.status,
        data: err.response.data
      } : null
    });
    if (err.response?.status === 404) {
      console.log('loadCustomerInfo: Customer not found for idTK=', auth.user.idTK);
      toast.warning('Không tìm thấy thông tin khách hàng. Vui lòng điền thông tin vận chuyển.');
    } else if (err.response?.status === 401 || err.response?.status === 403) {
      console.log('loadCustomerInfo: Unauthorized, logging out');
      errorMessage.value = 'Phiên đăng nhập đã hết hạn';
      errorDetails.value = ['Vui lòng đăng nhập lại để tiếp tục.'];
      errorRequiresLogin.value = true;
      showErrorModal();
    } else {
      errorMessage.value = 'Lỗi khi tải thông tin khách hàng';
      errorDetails.value = [err.response?.data?.message || err.message];
      showErrorModal();
    }
  }
};

// Tải danh sách khuyến mãi áp dụng theo IdKH và subTotal
const loadApplicablePromotions = async () => {
  console.log('loadApplicablePromotions: Starting');
  if (!auth.isAuthenticated || !auth.user?.idKH) {
    console.log('loadApplicablePromotions: User not authenticated or idKH missing');
    toast.warning('Vui lòng đăng nhập để xem khuyến mãi.');
    return;
  }

  try {
    console.log('loadApplicablePromotions: Fetching promotions for idKH', auth.user.idKH, 'with subTotal', subTotal.value);
    const response = await axios.get(`${API_BASE_URL}/api/khuyenmai/applicable/${auth.user.idKH}`, {
      withCredentials: true
    });
    applicablePromotions.value = response.data
      .filter(promotion => promotion.giaTriDonHangToiThieu <= subTotal.value)
      .map(promotion => ({
        id: promotion.idKM,
        code: promotion.maKM,
        name: promotion.tenKM,
        description: `${promotion.hinhThucGiam === 'PERCENTAGE' ? `${promotion.mucGiam}%` : `${promotion.mucGiam.toLocaleString('vi-VN')}đ`} (Tối thiểu ${promotion.giaTriDonHangToiThieu.toLocaleString('vi-VN')}đ, Tối đa ${promotion.giamToiDa ? promotion.giamToiDa.toLocaleString('vi-VN') : 'Không giới hạn'}đ)`,
        type: promotion.hinhThucGiam.toLowerCase(),
        value: Number(promotion.mucGiam),
        minOrder: Number(promotion.giaTriDonHangToiThieu),
        maxDiscount: promotion.giamToiDa ? Number(promotion.giamToiDa) : null,
        endDate: promotion.ngayKetThuc
      }));
    console.log('loadApplicablePromotions: Promotions loaded', applicablePromotions.value);
  } catch (err) {
    console.error('loadApplicablePromotions: Error', {
      message: err.message,
      response: err.response ? {
        status: err.response.status,
        data: err.response.data
      } : null
    });
    toast.error('Lỗi khi tải danh sách khuyến mãi: ' + (err.response?.data?.message || err.message));
  }
};

// Tải danh sách voucher áp dụng theo danh sách IdSPCT và subTotal của từng sản phẩm
const loadApplicableVouchers = async () => {
  console.log('loadApplicableVouchers: Starting');
  if (!selectedItems.value.length) {
    console.log('loadApplicableVouchers: No selected items');
    toast.warning('Không có sản phẩm nào để tải voucher.');
    return;
  }

  const idSPCTs = selectedItems.value.map(item => item.idSPCT);
  try {
    console.log('loadApplicableVouchers: Fetching vouchers for idSPCTs', idSPCTs);
    const response = await axios.post(`${API_BASE_URL}/api/voucher/applicable`, idSPCTs, {
      withCredentials: true,
      headers: { 'Content-Type': 'application/json' }
    });
    const vouchers = Array.isArray(response.data) ? response.data : [];
    console.log('loadApplicableVouchers: Vouchers loaded', vouchers);

    for (const item of selectedItems.value) {
      const itemTotal = item.donGia * item.soLuong;
      item.applicableVouchers = vouchers
        .filter(voucher => voucher.applicableProductIds.includes(item.idSPCT) && voucher.giaTriDonHangToiThieu <= itemTotal)
        .map(voucher => ({
          id: voucher.id,
          code: voucher.maVoucher,
          name: voucher.tenVoucher,
          description: `${voucher.hinhThucGiam === 'PERCENTAGE' ? `${voucher.mucGiam}%` : `${voucher.mucGiam.toLocaleString('vi-VN')}đ`} (Tối thiểu ${voucher.giaTriDonHangToiThieu.toLocaleString('vi-VN')}đ, Tối đa ${voucher.giamToiDa ? voucher.giamToiDa.toLocaleString('vi-VN') : 'Không giới hạn'}đ)`,
          type: voucher.hinhThucGiam.toLowerCase(),
          value: Number(voucher.mucGiam),
          minOrder: Number(voucher.giaTriDonHangToiThieu),
          maxDiscount: voucher.giamToiDa ? Number(voucher.giamToiDa) : null,
          endDate: voucher.ngayKetThuc
        }));
      item.selectedVoucherId = null;
    }
    console.log('loadApplicableVouchers: Vouchers assigned to items', selectedItems.value);
  } catch (err) {
    console.error('loadApplicableVouchers: Error', {
      message: err.message,
      response: err.response ? {
        status: err.response.status,
        data: err.response.data
      } : null
    });
    toast.error('Lỗi khi tải danh sách voucher: ' + (err.response?.data?.message || err.message));
  }
};

// Cập nhật số lượng sản phẩm
const updateItemQuantity = async (item, change) => {
  console.log('updateItemQuantity: Starting', { idGHCT: item.idGHCT, change });
  const newQuantity = item.soLuong + change;
  if (newQuantity < 1) {
    toast.error('Số lượng không thể nhỏ hơn 1!');
    return;
  }

  try {
    console.log('updateItemQuantity: Updating quantity for idGHCT', item.idGHCT, 'to', newQuantity);
    await axios.put(`${API_BASE_URL}/client/api/giohangct/${item.idGHCT}`, { idGHCT: item.idGHCT, idGioHang: item.idGioHang, idSPCT: item.idSPCT, soLuong: newQuantity }, {
      withCredentials: true,
      headers: { 'Content-Type': 'application/json' }
    });
    item.soLuong = newQuantity;
    sessionStorage.setItem('selectedCheckoutItems', JSON.stringify(selectedItems.value));
    toast.success(`Cập nhật số lượng sản phẩm ${item.name} thành công!`);
    // Tải lại voucher và khuyến mãi vì tổng giá có thể thay đổi
    await Promise.all([loadApplicablePromotions(), loadApplicableVouchers()]);
    await applyPromotionToOrder();
  } catch (err) {
    console.error('updateItemQuantity: Error', {
      message: err.message,
      response: err.response ? {
        status: err.response.status,
        data: err.response.data
      } : null
    });
    toast.error('Lỗi khi cập nhật số lượng: ' + (err.response?.data?.message || err.message));
  }
};

// Xác thực số lượng khi nhập trực tiếp
const validateQuantity = (item) => {
  console.log('validateQuantity: Validating', { idGHCT: item.idGHCT, soLuong: item.soLuong });
  if (!Number.isInteger(item.soLuong) || item.soLuong < 1) {
    toast.error('Số lượng phải là số nguyên và lớn hơn 0!');
    item.soLuong = 1;
  }
  updateItemQuantity(item, 0); // Gửi số lượng mới về backend
};

// Tính toán giảm giá cho từng sản phẩm (từ voucher) - Không làm tròn
const calculateItemDiscount = (item) => {
  if (!item.selectedVoucherId || !item.applicableVouchers) return 0;
  const voucher = item.applicableVouchers.find(v => v.id === item.selectedVoucherId);
  if (!voucher) return 0;
  const baseTotal = item.donGia * item.soLuong;
  if (baseTotal < voucher.minOrder) return 0;
  let discountAmount = 0;
  if (voucher.type === 'percentage') {
    discountAmount = baseTotal * (voucher.value / 100);
    discountAmount = Math.min(discountAmount, voucher.maxDiscount || Infinity);
  } else {
    discountAmount = voucher.value || 0;
  }
  return discountAmount; // Không làm tròn
};

// Tính toán tổng tiền cho từng sản phẩm sau khi áp dụng voucher - Không làm tròn
const calculateItemTotal = (item) => {
  const baseTotal = item.donGia * item.soLuong;
  const discount = calculateItemDiscount(item);
  return baseTotal - discount > 0 ? baseTotal - discount : 0; // Không làm tròn
};

// Tính toán giảm giá từ khuyến mãi áp dụng cho toàn đơn hàng - Không làm tròn
const calculateOrderPromotionDiscount = () => {
  if (!selectedPromotionId.value || !applicablePromotions.value.length) return 0;
  const promotion = applicablePromotions.value.find(p => p.id === selectedPromotionId.value);
  if (!promotion) return 0;
  if (subTotal.value < promotion.minOrder) return 0;
  let discountAmount = 0;
  if (promotion.type === 'percentage') {
    discountAmount = subTotal.value * (promotion.value / 100);
    discountAmount = Math.min(discountAmount, promotion.maxDiscount || Infinity);
  } else {
    discountAmount = promotion.value || 0;
  }
  return discountAmount; // Không làm tròn
};

// Tính tổng giảm giá (voucher + khuyến mãi) - Không làm tròn
const totalDiscount = computed(() => {
  const voucherDiscount = selectedItems.value.reduce((total, item) => total + calculateItemDiscount(item), 0);
  const promotionDiscount = calculateOrderPromotionDiscount();
  return voucherDiscount + promotionDiscount; // Không làm tròn
});

// Tính tổng tiền gửi đi (không bao gồm phí giao hàng) - Không làm tròn
const total = computed(() => {
  const finalTotal = subTotal.value - totalDiscount.value;
  return finalTotal > 0 ? finalTotal : 0; // Không làm tròn
});

// Tính tổng tiền hiển thị (bao gồm phí giao hàng, chỉ dùng để hiển thị)
const totalDisplay = computed(() => {
  const finalTotal = subTotal.value + shippingFee.value - totalDiscount.value;
  return finalTotal > 0 ? finalTotal : 0; // Không làm tròn
});

// Áp dụng voucher cho sản phẩm
const applyVoucherToItem = (item) => {
  console.log('applyVoucherToItem: Applying voucher to item', item.idGHCT, item.selectedVoucherId);
  const voucher = item.applicableVouchers.find(v => v.id === item.selectedVoucherId);
  if (voucher && (item.donGia * item.soLuong) < voucher.minOrder) {
    toast.error(`Sản phẩm ${item.name} cần đạt tối thiểu ${voucher.minOrder.toLocaleString('vi-VN')}đ để áp dụng voucher này.`);
    item.selectedVoucherId = null;
  } else if (voucher) {
    toast.success(`Đã áp dụng voucher ${voucher.name} cho sản phẩm ${item.name}`);
  }
};

// Áp dụng khuyến mãi cho toàn đơn hàng
const applyPromotionToOrder = () => {
  console.log('applyPromotionToOrder: Applying promotion', selectedPromotionId.value);
  const promotion = applicablePromotions.value.find(p => p.id === selectedPromotionId.value);
  if (promotion && subTotal.value < promotion.minOrder) {
    toast.error(`Đơn hàng cần đạt tối thiểu ${promotion.minOrder.toLocaleString('vi-VN')}đ để áp dụng khuyến mãi này.`);
    selectedPromotionId.value = null;
  } else if (promotion) {
    toast.success(`Đã áp dụng khuyến mãi ${promotion.name} cho đơn hàng`);
  }
};

// Xóa tất cả sản phẩm
const clearSelectedItems = () => {
  console.log('clearSelectedItems: Starting');
  if (confirm('Bạn có chắc chắn muốn xóa tất cả sản phẩm?')) {
    selectedItems.value = [];
    sessionStorage.removeItem('selectedCheckoutItems');
    toast.success('Đã xóa tất cả sản phẩm!');
    router.push('/cart');
  }
};

// Kiểm tra trạng thái giỏ hàng sau khi thanh toán
const checkCartStatusAfterOrder = async (idGHCTs) => {
  try {
    console.log('checkCartStatusAfterOrder: Checking status for', idGHCTs);
    const response = await axios.get(`${API_BASE_URL}/client/api/giohang/status/after-order`, {
      params: { idGHCTs: idGHCTs.join(',') },
      withCredentials: true
    });
    
    const status = response.data;
    console.log('checkCartStatusAfterOrder: Status', status);
    
    if (status.stillExists > 0) {
      console.warn('checkCartStatusAfterOrder: Some cart items still exist', status);
      // Có thể gửi thông báo cho admin hoặc log để theo dõi
    } else {
      console.log('checkCartStatusAfterOrder: All cart items successfully removed');
    }
  } catch (err) {
    console.error('checkCartStatusAfterOrder: Error checking status', err);
    // Không hiển thị lỗi cho user vì đây chỉ là kiểm tra tùy chọn
  }
};

// Validate dữ liệu trước khi đặt hàng
const validateAndPlaceOrder = () => {
  console.log('validateAndPlaceOrder: Starting');
  errors.firstName = shippingInfo.firstName ? '' : 'Vui lòng nhập họ';
  errors.lastName = shippingInfo.lastName ? '' : 'Vui lòng nhập tên';
  errors.phone = shippingInfo.phone ? (/^[0-9]{10}$/.test(shippingInfo.phone) ? '' : 'Số điện thoại phải có 10 chữ số') : 'Vui lòng nhập số điện thoại';
  errors.address = shippingInfo.address ? '' : 'Vui lòng nhập địa chỉ';
  errors.email = shippingInfo.email ? (/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(shippingInfo.email) ? '' : 'Email không hợp lệ') : 'Vui lòng nhập email';
  if (!selectedPaymentMethod.value) {
    toast.error('Vui lòng chọn phương thức thanh toán!');
    return;
  }

  if (Object.values(errors).some(error => error)) {
    console.error('validateAndPlaceOrder: Validation failed', errors);
    toast.error('Vui lòng kiểm tra thông tin vận chuyển!');
    return;
  }

  placeOrder();
};

// Đặt hàng và xóa sản phẩm chi tiết trong giỏ hàng
const placeOrder = async () => {
  console.log('placeOrder: Starting', {
    selectedItems: selectedItems.value,
    shippingInfo,
    paymentMethod: selectedPaymentMethod.value,
    total: total.value,
    selectedPromotionId: selectedPromotionId.value
  });

  if (!selectedItems.value.length) {
    console.error('placeOrder: Validation failed - No items', { selectedItems: selectedItems.value });
    errorMessage.value = 'Vui lòng thêm sản phẩm để đặt hàng!';
    errorDetails.value = [];
    showErrorModal();
    return;
  }

  if (!auth.isAuthenticated || !auth.user?.idTK) {
    console.error('placeOrder: Validation failed - User not authenticated', {
      isAuthenticated: auth.isAuthenticated,
      user: auth.user
    });
    errorMessage.value = 'Bạn cần đăng nhập để đặt hàng!';
    errorDetails.value = ['Vui lòng đăng nhập và thử lại.'];
    errorRequiresLogin.value = true;
    showErrorModal();
    return;
  }

  let orderData = null;
  isSubmitting.value = true;
  try {
    orderData = {
      idTK: auth.user.idTK,
      loaiHoaDon: 'Trực tuyến',
      shippingInfo: {
        firstName: shippingInfo.firstName,
        lastName: shippingInfo.lastName,
        phone: shippingInfo.phone,
        address: shippingInfo.address,
        notes: shippingInfo.notes || '',
        email: shippingInfo.email
      },
      paymentMethod: paymentMethods.value.find(method => method.id === selectedPaymentMethod.value)?.value,
      total: total.value,
      idKM: selectedPromotionId.value,
      items: selectedItems.value.map(item => ({
        idGHCT: item.idGHCT,
        idSPCT: item.idSPCT,
        soLuong: item.soLuong,
        donGia: Number(item.donGia),
        idVoucher: item.selectedVoucherId || null
      }))
    };

    // Kiểm tra dữ liệu trước khi gửi
    if (!orderData.paymentMethod) {
      errorMessage.value = 'Phương thức thanh toán không hợp lệ!';
      errorDetails.value = ['Vui lòng chọn lại phương thức thanh toán.'];
      showErrorModal();
      return;
    }

    // Kiểm tra giá trị số
    if (isNaN(orderData.total) || orderData.items.some(item => isNaN(item.donGia) || item.soLuong <= 0)) {
      console.error('placeOrder: Invalid numeric values', { orderData });
      errorMessage.value = 'Giá trị đơn hàng hoặc sản phẩm không hợp lệ!';
      errorDetails.value = ['Vui lòng kiểm tra giá trị đơn hàng và số lượng sản phẩm.'];
      showErrorModal();
      return;
    }

    // Kiểm tra tổng tiền
    const calculatedTotal = orderData.items.reduce((sum, item) => sum + item.donGia * item.soLuong, 0) - totalDiscount.value;
    if (Math.abs(calculatedTotal - orderData.total) > 1) {
      console.error('placeOrder: Total mismatch', { calculatedTotal, sentTotal: orderData.total });
      // errorMessage.value = `Tổng tiền không khớp. Tổng tính toán (sản phẩm - khuyến mãi): ${calculatedTotal}, Tổng gửi: ${orderData.total}`;
      errorDetails.value = ['Vui lòng kiểm tra lại tổng tiền và khuyến mãi.'];
      showErrorModal();
      return;
    }

    console.log('placeOrder: Sending order data to API', {
      url: `${API_BASE_URL}/client/api/hoadon`,
      orderData: JSON.stringify(orderData, null, 2)
    });

    const response = await axios.post(`${API_BASE_URL}/client/api/hoadon`, orderData, {
      withCredentials: true
    });

    console.log('placeOrder: Order placed successfully', {
      responseData: response.data,
      status: response.status,
      statusText: response.statusText
    });

    // Xóa các sản phẩm chi tiết trong giỏ hàng - Cải thiện logic
    // Backend đã xử lý việc xóa, frontend chỉ cần cập nhật UI
    try {
      console.log('placeOrder: Backend has already handled cart item deletion');
      // Không cần gọi API xóa nữa vì backend đã xử lý trong transaction
      // Chỉ cần cập nhật UI và xóa sessionStorage
      
      // Kiểm tra trạng thái giỏ hàng sau khi thanh toán (tùy chọn)
      await checkCartStatusAfterOrder(orderData.items.map(item => item.idGHCT));
    } catch (err) {
      console.error('placeOrder: Error in frontend cart cleanup', {
        message: err.message,
        response: err.response ? {
          status: err.response.status,
          data: err.response.data
        } : null
      });
      // Không hiển thị lỗi cho user vì backend đã xử lý thành công
    }

    toast.success('Đặt hàng thành công!');
    sessionStorage.removeItem('selectedCheckoutItems');
    selectedItems.value = [];
    router.push('/order-success');
  } catch (err) {
    console.error('placeOrder: Error placing order', {
      message: err.message,
      stack: err.stack,
      response: err.response ? {
        status: err.response.status,
        statusText: err.response.statusText,
        data: err.response.data,
        headers: err.response.headers
      } : null,
      requestData: {
        url: `${API_BASE_URL}/client/api/hoadon`,
        orderData: orderData ? JSON.stringify(orderData, null, 2) : 'Not defined'
      }
    });

    errorMessage.value = 'Lỗi khi đặt hàng';
    errorDetails.value = [];
    errorRequiresLogin.value = false;

    if (err.response) {
      errorMessage.value = err.response.data?.message || err.response.statusText;
      if (err.response.status === 400) {
        errorMessage.value = 'Dữ liệu không hợp lệ!';
        errorDetails.value = err.response.data?.details || [err.response.data?.message || 'Vui lòng kiểm tra thông tin đơn hàng.'];
      } else if (err.response.status === 401 || err.response.status === 403) {
        errorMessage.value = 'Bạn cần đăng nhập lại để tiếp tục!';
        errorDetails.value = ['Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.'];
        errorRequiresLogin.value = true;
      } else {
        errorDetails.value = ['Lỗi server: ' + (err.response.data?.message || err.response.statusText)];
      }
    } else if (err.request) {
      errorMessage.value = 'Không thể kết nối đến server!';
      errorDetails.value = ['Vui lòng kiểm tra kết nối mạng và thử lại.'];
    } else {
      errorMessage.value = err.message;
      errorDetails.value = ['Lỗi không xác định. Vui lòng thử lại sau.'];
    }
    showErrorModal();
  } finally {
    isSubmitting.value = false;
  }
};

// Hiển thị modal lỗi
const showErrorModal = () => {
  if (errorModalInstance) {
    errorModalInstance.show();
  } else {
    console.error('showErrorModal: errorModalInstance is not initialized');
    toast.error(errorMessage.value);
  }
};

// Chuyển hướng đến trang đăng nhập
const redirectToLogin = () => {
  auth.logout();
  router.push('/login');
  errorModalInstance.hide();
};

onMounted(async () => {
  console.log('onMounted: Initializing CheckoutPage, auth.user', auth.user);

  // Khởi tạo modal
  const errorModalElement = document.getElementById('errorModal');
  if (errorModalElement) {
    errorModalInstance = new Modal(errorModalElement);
    console.log('onMounted: errorModalInstance initialized');
  } else {
    console.error('onMounted: errorModal element not found');
    toast.error('Lỗi khi khởi tạo modal lỗi!');
  }

  // Tải sản phẩm được chọn từ sessionStorage
  const storedItems = sessionStorage.getItem('selectedCheckoutItems');
  console.log('onMounted: Stored items from sessionStorage', storedItems);

  if (storedItems) {
    try {
      selectedItems.value = JSON.parse(storedItems);
      console.log('onMounted: Loaded selectedItems', selectedItems.value);
    } catch (err) {
      console.error('onMounted: Error parsing selectedCheckoutItems', {
        message: err.message,
        stack: err.stack
      });
      toast.error('Lỗi khi tải sản phẩm từ giỏ hàng!');
      router.push('/cart');
      return;
    }
  } else {
    console.log('onMounted: No selected items found, redirecting to /cart');
    toast.error('Không tìm thấy sản phẩm nào được chọn.');
    router.push('/cart');
    return;
  }

  // Tải phương thức thanh toán và thông tin khách hàng
  await Promise.all([loadPaymentMethods(), loadCustomerInfo()]);

  // Tải danh sách khuyến mãi và voucher
  await Promise.all([loadApplicablePromotions(), loadApplicableVouchers()]);
});

onUnmounted(() => {
  console.log('onUnmounted: Disposing modal instances');
  if (errorModalInstance) {
    errorModalInstance.dispose();
  }
});

let errorModalInstance = null;
</script>

<style scoped>
.quantity-control .form-control {
  text-align: center;
}
.quantity-control .btn {
  width: 30px;
  height: 30px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}
.cart-summary-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}
.cart-summary-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  margin-right: 15px;
}
.cart-summary-details {
  flex-grow: 1;
}
.cart-summary-price {
  min-width: 150px;
}

.checkout-page {
  background-color: #f8f9fa;
}
.checkout-card {
  background: #fff;
  border-radius: 0.5rem;
  padding: 1.5rem;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}
.cart-summary-list {
  max-height: 45vh;
  overflow-y: auto;
}
.cart-summary-item {
  display: flex;
  align-items: center;
  padding: 1rem 0;
  border-bottom: 1px solid #e9ecef;
}
.cart-summary-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 0.25rem;
  margin-right: 1rem;
}
.cart-summary-details {
  flex-grow: 1;
}
.payment-option {
  padding: 1rem;
  border: 1px solid #e9ecef;
  border-radius: 0.5rem;
  cursor: pointer;
}
.payment-option.active {
  border-color: var(--bs-primary);
  background-color: #f8f9fa;
}
.payment-logo {
  width: 40px;
  height: 40px;
}
.checkout-summary-box {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 0.5rem;
}
.payment-summary .summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}
.payment-summary .total {
  font-weight: bold;
  font-size: 1.2rem;
}
.checkout-footer {
  background: #fff;
  border-top: 1px solid #e9ecef;
  padding: 1rem 0;
  position: sticky;
  bottom: 0;
}
.voucher-selection {
  max-width: 300px;
}
</style>