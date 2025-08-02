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
            <form class="checkout-form">
              <div class="row g-3">
                <div class="col-md-6"><input type="text" class="form-control" placeholder="Họ" v-model="shippingInfo.firstName" required></div>
                <div class="col-md-6"><input type="text" class="form-control" placeholder="Tên" v-model="shippingInfo.lastName" required></div>
                <div class="col-12"><input type="email" class="form-control" placeholder="Email" v-model="shippingInfo.email"></div>
                <div class="col-12"><input type="text" class="form-control" placeholder="Số điện thoại" v-model="shippingInfo.phone" required></div>
                <div class="col-12"><input type="text" class="form-control" placeholder="Địa chỉ" v-model="shippingInfo.address" required></div>
                <div class="col-12"><textarea class="form-control" rows="2" placeholder="Ghi chú (tùy chọn)" v-model="shippingInfo.notes"></textarea></div>
              </div>
            </form>
          </div>

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

        <!-- CỘT PHẢI: GIỎ HÀNG & THANH TOÁN -->
        <div class="col-lg-7">
          <div class="checkout-card position-sticky" style="top: 20px;">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <div class="form-check">
                <input type="checkbox" class="form-check-input" id="selectAll" :checked="isAllSelected" @change="toggleSelectAll">
                <label for="selectAll" class="form-check-label ms-2 fw-bold">Tất cả ({{ selectedItemIds.length }} sản phẩm)</label>
              </div>
              <button v-if="selectedItems.length > 0" @click="clearSelectedItems" class="btn btn-sm btn-link text-danger">Xóa tất cả</button>
            </div>
            <div class="cart-summary-list">
              <div v-if="selectedItems.length > 0">
                <div v-for="item in selectedItems" :key="item.idGHCT" class="cart-summary-item">
                  <input type="checkbox" class="form-check-input mt-1 me-3" :checked="selectedItemIds.includes(item.idGHCT)" @change="toggleSelectItem(item.idGHCT)">
                  <img :src="item.imageUrl || 'https://placehold.co/150'" class="cart-summary-img" :alt="item.name">
                  <div class="cart-summary-details">
                    <h6>{{ item.name }}</h6>
                    <p>Phân loại: {{ item.mauSac }}, {{ item.kichThuoc }}</p>
                  </div>
                  <div class="cart-summary-price text-end">
                    <div class="fw-bold">{{ (item.donGia * item.soLuong).toLocaleString('vi-VN') }}đ</div>
                  </div>
                </div>
              </div>
              <div v-else class="text-center text-muted p-5">
                <p>Không có sản phẩm nào được chọn.</p>
                <router-link to="/cart" class="btn btn-dark">Quay lại giỏ hàng</router-link>
              </div>
            </div>

            <div class="checkout-summary-box mt-4">
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
              <div class="payment-summary">
                <div class="summary-row"><span>Tạm tính</span><span>{{ subTotal.toLocaleString('vi-VN') }}đ</span></div>
                <div class="summary-row"><span>Phí giao hàng</span><span>+ {{ shippingFee.toLocaleString('vi-VN') }}đ</span></div>
                <div v-if="appliedVoucher" class="summary-row">
                  <span>Voucher ({{ appliedVoucher.code }})</span>
                  <span class="text-success">-{{ discount.toLocaleString('vi-VN') }}đ</span>
                </div>
                <div class="summary-row total">
                  <span>Thành tiền</span>
                  <span class="text-danger">{{ total.toLocaleString('vi-VN') }}đ</span>
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
            <div class="h4 fw-bolder text-danger mb-0">{{ total.toLocaleString('vi-VN') }}đ</div>
          </div>
          <button @click="placeOrder" class="btn btn-primary btn-lg" style="min-width: 200px;" :disabled="selectedItemIds.length === 0">
            ĐẶT HÀNG
          </button>
        </div>
      </div>
    </div>

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
              <div v-for="voucher in allVouchers" :key="voucher.id" class="voucher-item p-3 mb-3" :class="{ 'disabled': !isVoucherApplicable(voucher) }">
                <div class="d-flex">
                  <div class="voucher-info flex-grow-1">
                    <h6 class="fw-bold mb-1">{{ voucher.name }} ({{ voucher.code }})</h6>
                    <p class="mb-1 small">{{ voucher.description }}</p>
                    <p class="text-muted small mb-0">HSD: {{ formatDate(voucher.endDate) }}</p>
                  </div>
                  <div class="voucher-select d-flex align-items-center">
                    <input type="radio" class="form-check-input" name="voucherRadio" :value="voucher.id" :checked="appliedVoucher?.id === voucher.id" :disabled="!isVoucherApplicable(voucher)" @change="selectVoucher(voucher)">
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

const selectedPaymentMethod = ref('COD');
const shippingFee = ref(25000);
const voucherCodeInput = ref('');
const appliedVoucher = ref(null);
const selectedItems = ref([]);
const selectedItemIds = ref([]);
const allVouchers = ref([]);

const paymentMethods = ref([
  { id: 'COD', name: 'Thanh toán khi nhận hàng', description: 'Thanh toán bằng tiền mặt', icon: 'fas fa-truck', value: 1 },
  { id: 'VNPAY', name: 'Ví điện tử VNPAY', description: 'Quét QR để thanh toán', logo: '/img/payment/vnpay.png', value: 2 }
]);

// Hàm định dạng ngày
const formatDate = (date) => {
  return new Date(date).toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
};

// Lấy danh sách voucher áp dụng được
const loadApplicableVouchers = async () => {
  console.log('loadApplicableVouchers: Starting');
  if (!selectedItems.value.length) {
    console.log('loadApplicableVouchers: No selected items');
    toast.warning('Không có sản phẩm nào để tải voucher.');
    return;
  }

  try {
    const idSPCTs = selectedItems.value.map(item => item.idSPCT);
    console.log('loadApplicableVouchers: Fetching vouchers for idSPCTs', idSPCTs);

    const response = await axios.post(`${API_BASE_URL}/client/api/voucher/applicable`, idSPCTs, {
      withCredentials: true
    });

    allVouchers.value = response.data;
    console.log('loadApplicableVouchers: Vouchers loaded', allVouchers.value);
  } catch (err) {
    console.error('loadApplicableVouchers: Error', {
      message: err.message,
      stack: err.stack,
      response: err.response ? {
        status: err.response.status,
        statusText: err.response.statusText,
        data: err.response.data
      } : null
    });
    toast.error('Lỗi khi tải danh sách voucher: ' + (err.response?.data?.message || err.message));
  }
};

// Lấy thông tin khách hàng từ API
const loadCustomerInfo = async () => {
  console.log('loadCustomerInfo: Starting, auth.user', auth.user);
  if (!auth.isAuthenticated || !auth.user?.idTK) {
    console.log('loadCustomerInfo: User not authenticated or idTK missing');
    toast.warning('Vui lòng đăng nhập để tiếp tục.');
    router.push('/login');
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
    shippingInfo.address = customer.diaChi || '';
    shippingInfo.notes = '';
    shippingInfo.email = customer.email || '';
  } catch (err) {
    console.error('loadCustomerInfo: Error', {
      message: err.message,
      stack: err.stack,
      response: err.response ? {
        status: err.response.status,
        statusText: err.response.statusText,
        data: err.response.data
      } : null
    });
    if (err.response?.status === 404) {
      console.log('loadCustomerInfo: Customer not found for idTK=', auth.user.idTK);
      toast.warning('Không tìm thấy thông tin khách hàng. Vui lòng điền thông tin vận chuyển.');
    } else if (err.response?.status === 401 || err.response?.status === 403) {
      console.log('loadCustomerInfo: Unauthorized, logging out');
      auth.logout();
      router.push('/login');
    } else {
      toast.error('Lỗi khi tải thông tin khách hàng: ' + (err.response?.data?.message || err.message));
    }
  }
};

// Tính toán tổng phụ
const subTotal = computed(() => {
  return selectedItems.value
    .filter(item => selectedItemIds.value.includes(item.idGHCT))
    .reduce((total, item) => total + item.donGia * item.soLuong, 0);
});

// Kiểm tra xem tất cả sản phẩm có được chọn không
const isAllSelected = computed(() => {
  return selectedItems.value.length > 0 && selectedItemIds.value.length === selectedItems.value.length;
});

// Chọn/t bỏ chọn tất cả sản phẩm
const toggleSelectAll = () => {
  console.log('toggleSelectAll: isAllSelected', isAllSelected.value);
  if (isAllSelected.value) {
    selectedItemIds.value = [];
  } else {
    selectedItemIds.value = selectedItems.value.map(item => item.idGHCT);
  }
  console.log('toggleSelectAll: selectedItemIds', selectedItemIds.value);
};

// Chọn/t bỏ chọn sản phẩm riêng lẻ
const toggleSelectItem = (idGHCT) => {
  console.log('toggleSelectItem: idGHCT', idGHCT);
  const index = selectedItemIds.value.indexOf(idGHCT);
  if (index > -1) {
    selectedItemIds.value.splice(index, 1);
  } else {
    selectedItemIds.value.push(idGHCT);
  }
  console.log('toggleSelectItem: selectedItemIds', selectedItemIds.value);
};

// Xóa tất cả sản phẩm đã chọn
const clearSelectedItems = () => {
  console.log('clearSelectedItems: Starting');
  if (confirm('Bạn có chắc chắn muốn xóa tất cả sản phẩm đã chọn?')) {
    selectedItems.value = [];
    selectedItemIds.value = [];
    sessionStorage.removeItem('selectedCheckoutItems');
    toast.success('Đã xóa tất cả sản phẩm!');
    router.push('/cart');
  }
};

// Kiểm tra voucher có áp dụng được không
const isVoucherApplicable = (voucher) => {
  return subTotal.value >= (voucher.minOrder || 0);
};

// Lọc danh sách voucher áp dụng được
const availableVouchers = computed(() => {
  return allVouchers.value.filter(isVoucherApplicable);
});

// Tính toán giảm giá
const discount = computed(() => {
  if (!appliedVoucher.value) return 0;
  if (appliedVoucher.value.type === 'percentage') {
    const discountAmount = subTotal.value * (appliedVoucher.value.value / 100);
    return Math.min(discountAmount, appliedVoucher.value.maxDiscount || Infinity);
  }
  return appliedVoucher.value.value || 0;
});

// Tính tổng tiền
const total = computed(() => {
  const finalTotal = subTotal.value + shippingFee.value - discount.value;
  return finalTotal > 0 ? finalTotal : 0;
});

// Mở modal voucher
const openVoucherModal = () => {
  console.log('openVoucherModal: Starting');
  if (voucherModalInstance) {
    voucherModalInstance.show();
  } else {
    console.error('openVoucherModal: voucherModalInstance is not initialized');
    toast.error('Lỗi khi mở ví voucher!');
  }
};

// Chọn voucher
const selectVoucher = (voucher) => {
  console.log('selectVoucher: Voucher selected', voucher);
  appliedVoucher.value = voucher;
  voucherCodeInput.value = voucher.code;
  voucherModalInstance.hide();
  toast.success(`Đã áp dụng voucher: ${voucher.name}`);
};

// Áp dụng voucher bằng mã
const applyVoucherByCode = () => {
  console.log('applyVoucherByCode: Voucher code', voucherCodeInput.value);
  const foundVoucher = allVouchers.value.find(v => v.code.toLowerCase() === voucherCodeInput.value.toLowerCase());
  if (foundVoucher) {
    if (isVoucherApplicable(foundVoucher)) {
      selectVoucher(foundVoucher);
    } else {
      toast.error(`Đơn hàng của bạn cần đạt tối thiểu ${foundVoucher.minOrder.toLocaleString('vi-VN')}đ để áp dụng voucher này.`);
    }
  } else {
    toast.error('Mã voucher không hợp lệ.');
  }
};

// Đặt hàng
const placeOrder = async () => {
  console.log('placeOrder: Starting', {
    selectedItemIds: selectedItemIds.value,
    shippingInfo,
    paymentMethod: selectedPaymentMethod.value,
    voucher: appliedVoucher.value,
    total: total.value
  });

  if (!selectedItemIds.value.length) {
    console.error('placeOrder: Validation failed - No items selected', {
      selectedItems: selectedItems.value,
      selectedItemIds: selectedItemIds.value
    });
    toast.error('Vui lòng chọn ít nhất một sản phẩm để đặt hàng!');
    return;
  }

  if (!shippingInfo.firstName || !shippingInfo.lastName || !shippingInfo.phone || !shippingInfo.address) {
    const missingFields = [];
    if (!shippingInfo.firstName) missingFields.push('Họ');
    if (!shippingInfo.lastName) missingFields.push('Tên');
    if (!shippingInfo.phone) missingFields.push('Số điện thoại');
    if (!shippingInfo.address) missingFields.push('Địa chỉ');
    console.error('placeOrder: Validation failed - Missing shipping info', {
      missingFields,
      shippingInfo
    });
    toast.error(`Vui lòng điền đầy đủ thông tin: ${missingFields.join(', ')}`);
    return;
  }

  if (!auth.isAuthenticated || !auth.user?.idTK) {
    console.error('placeOrder: Validation failed - User not authenticated', {
      isAuthenticated: auth.isAuthenticated,
      user: auth.user
    });
    toast.error('Vui lòng đăng nhập để đặt hàng!');
    router.push('/login');
    return;
  }

  let orderData = null;
  try {
    orderData = {
      idTK: auth.user.idTK,
      shippingInfo: {
        firstName: shippingInfo.firstName,
        lastName: shippingInfo.lastName,
        phone: shippingInfo.phone,
        address: shippingInfo.address,
        notes: shippingInfo.notes || '',
        email: shippingInfo.email || ''
      },
      paymentMethod: paymentMethods.value.find(method => method.id === selectedPaymentMethod.value).value,
      voucherCode: appliedVoucher.value?.code || null,
      total: Number(total.value),
      items: selectedItems.value
        .filter(item => selectedItemIds.value.includes(item.idGHCT))
        .map(item => ({
          idGHCT: item.idGHCT,
          idSPCT: item.idSPCT,
          soLuong: item.soLuong,
          donGia: Number(item.donGia)
        }))
    };

    if (isNaN(orderData.total) || orderData.items.some(item => isNaN(item.donGia))) {
      console.error('placeOrder: Invalid numeric values', { orderData });
      toast.error('Giá trị đơn hàng hoặc sản phẩm không hợp lệ!');
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
    toast.success('Đặt hàng thành công!');
    sessionStorage.removeItem('selectedCheckoutItems');
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

    let errorMessage = 'Lỗi khi đặt hàng';
    if (err.response) {
      errorMessage = err.response.data?.message || err.response.statusText;
      if (err.response.status === 400) {
        errorMessage = `Dữ liệu không hợp lệ: ${err.response.data?.message || 'Vui lòng kiểm tra thông tin đơn hàng!'}`;
      } else if (err.response.status === 401 || err.response.status === 403) {
        errorMessage = 'Bạn cần đăng nhập lại để tiếp tục!';
        auth.logout();
        router.push('/login');
      }
    } else if (err.request) {
      errorMessage = 'Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng!';
    } else {
      errorMessage = err.message;
    }
    toast.error(errorMessage);
  }
};

onMounted(async () => {
  console.log('onMounted: Initializing CheckoutPage, auth.user', auth.user);

  // Khởi tạo modal
  const modalElement = document.getElementById('voucherModal');
  if (modalElement) {
    voucherModalInstance = new Modal(modalElement);
    console.log('onMounted: voucherModalInstance initialized');
  } else {
    console.error('onMounted: voucherModal element not found');
    toast.error('Lỗi khi khởi tạo modal voucher!');
  }

  // Tải thông tin khách hàng
  await loadCustomerInfo();

  // Tải sản phẩm được chọn từ sessionStorage
  const storedItems = sessionStorage.getItem('selectedCheckoutItems');
  console.log('onMounted: Stored items from sessionStorage', storedItems);

  if (storedItems) {
    try {
      selectedItems.value = JSON.parse(storedItems);
      selectedItemIds.value = selectedItems.value.map(item => item.idGHCT);
      console.log('onMounted: Loaded selectedItems', selectedItems.value);

      // Tải danh sách voucher áp dụng
      await loadApplicableVouchers();
    } catch (err) {
      console.error('onMounted: Error parsing selectedCheckoutItems', {
        message: err.message,
        stack: err.stack
      });
      toast.error('Lỗi khi tải sản phẩm từ giỏ hàng!');
      router.push('/cart');
    }
  } else {
    console.log('onMounted: No selected items found, redirecting to /cart');
    toast.error('Không tìm thấy sản phẩm nào được chọn.');
    router.push('/cart');
  }
});

onUnmounted(() => {
  console.log('onUnmounted: Disposing voucherModalInstance');
  if (voucherModalInstance) {
    voucherModalInstance.dispose();
  }
});

let voucherModalInstance = null;
</script>

<style scoped>
.cart-summary-list {
  max-height: 45vh;
  overflow-y: auto;
}
.voucher-list {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 10px;
}
.voucher-item {
  border: 1px solid #e9ecef;
  border-left: 5px solid #0d6efd;
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
```