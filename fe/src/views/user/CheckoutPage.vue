<template>
  <div class="checkout-page bg-light">
    <div class="container py-4">
      
      <!-- 1. ĐỊA CHỈ NHẬN HÀNG -->
      <div class="card shadow-sm mb-3">
        <div class="card-body p-4">
          <div class="shipping-address-box">
            <div class="d-flex align-items-center mb-3 text-danger">
              <i class="fas fa-map-marker-alt fa-lg me-2"></i>
              <h5 class="mb-0 fw-bold">Địa Chỉ Nhận Hàng</h5>
            </div>
            <div v-if="isAddressesLoading" class="text-muted">Đang tải địa chỉ...</div>
            <div v-else-if="selectedAddress" class="d-flex justify-content-between align-items-center">
              <div>
                <span class="fw-bold me-3">{{ selectedAddress.customerName }} | (+84) {{ selectedAddress.phone }}</span>
                <span class="text-muted">{{ selectedAddress.fullAddress }}</span>
                <span v-if="selectedAddress.isDefault" class="badge bg-light text-danger border border-danger ms-2">Mặc Định</span>
              </div>
              <button class="btn btn-link text-decoration-none" data-bs-toggle="modal" data-bs-target="#addressModal">
                Thay Đổi
              </button>
            </div>
            <div v-else class="text-muted">Vui lòng chọn hoặc thêm địa chỉ giao hàng.</div>
          </div>
        </div>
      </div>

      <!-- 2. DANH SÁCH SẢN PHẨM -->
      <div class="card shadow-sm mb-3">
        <div class="card-header bg-white py-3">
          <div class="row fw-bold text-muted">
            <div class="col-md-6">Sản phẩm</div>
            <div class="col-md-2 text-center">Đơn giá</div>
            <div class="col-md-2 text-center">Số lượng</div>
            <div class="col-md-2 text-end">Thành tiền</div>
          </div>
        </div>
        <div class="card-body">
          <div v-for="item in selectedItems" :key="item.idGHCT" class="row align-items-center py-3 border-bottom">
            <div class="col-md-6 d-flex align-items-center">
              <img :src="item.imageUrl || 'https://placehold.co/150'" class="cart-product-img me-3" :alt="item.name">
              <div>
                <h6 class="mb-1">{{ item.name }}</h6>
                <p class="small text-muted mb-0">Phân loại: {{ item.mauSac }}, {{ item.kichThuoc }}</p>
              </div>
            </div>
            <div class="col-md-2 text-center text-muted">{{ item.donGia.toLocaleString('vi-VN') }}đ</div>
            <div class="col-md-2 text-center text-muted">{{ item.soLuong }}</div>
            <div class="col-md-2 text-end fw-bold">{{ (item.donGia * item.soLuong).toLocaleString('vi-VN') }}đ</div>
          </div>
        </div>
      </div>

      <!-- 3. HÌNH THỨC THANH TOÁN VÀ TỔNG KẾT -->
      <div class="card shadow-sm">
        <div class="card-body">
          <div class="row align-items-end">
            <div class="col-md-7">
              <h5 class="mb-3 fw-bold">Hình thức thanh toán</h5>
              <div class="d-grid gap-2">
                <label v-for="method in paymentMethods" :key="method.id" class="payment-option" :class="{ 'active': selectedPaymentMethod === method.id }">
                  <input type="radio" class="form-check-input" name="paymentMethod" :value="method.id" v-model="selectedPaymentMethod">
                  <span class="ms-2">{{ method.description }}</span>
                </label>
              </div>
            </div>
            <div class="col-md-5 mt-4 mt-md-0">
              <div class="payment-summary text-end">
                <div class="summary-row"><span>Tổng tiền hàng</span><span>{{ subTotal.toLocaleString('vi-VN') }}đ</span></div>
                <div class="summary-row"><span>Phí vận chuyển</span><span>0đ</span></div>
                <div class="summary-row total">
                    <span class="h5">Thành tiền</span>
                    <span class="text-danger h4 fw-bolder">{{ totalDisplay.toLocaleString('vi-VN') }}đ</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
    </div>

    <!-- NÚT ĐẶT HÀNG (FOOTER) -->
    <div class="checkout-footer">
      <div class="container d-flex justify-content-end align-items-center">
        <div class="d-flex align-items-center">
          <div class="me-4 text-end">
            <small class="text-muted">Tổng thanh toán:</small>
            <div class="h4 fw-bolder text-danger mb-0">{{ totalDisplay.toLocaleString('vi-VN') }}đ</div>
          </div>
          <button @click="placeOrder" class="btn btn-primary btn-lg" style="min-width: 200px;" :disabled="!canPlaceOrder">
            <span v-if="isSubmitting" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
            {{ isSubmitting ? 'Đang xử lý...' : 'Đặt Hàng' }}
          </button>
        </div>
      </div>
    </div>

    <!-- MODAL CHỌN ĐỊA CHỈ -->
    <div class="modal fade" id="addressModal" tabindex="-1">
      <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content">
          <div class="modal-header"><h5 class="modal-title">Địa Chỉ Của Tôi</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
          <div class="modal-body address-modal-body">
            <div v-if="addresses.length > 0" class="list-group list-group-flush">
              <label v-for="addr in addresses" :key="addr.idDiaChi" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center p-3">
                <div class="d-flex align-items-start">
                  <input class="form-check-input me-3 mt-1" type="radio" name="modalAddress" :value="addr.idDiaChi" v-model="selectedAddressIdInModal">
                  <div>
                    <span class="fw-bold me-2">{{ customerInfo.name }}</span><span>(+84) {{ addr.soDienThoai }}</span>
                    <p class="mb-1 text-muted small">{{ addr.diaChiCuThe }}</p>
                    <p class="mb-0 text-muted small">{{ [addr.phuongXa, addr.tinhThanh].filter(Boolean).join(', ') }}</p>
                  </div>
                </div>
                <button class="btn btn-link text-decoration-none" @click.prevent="openUpdateAddressModal(addr)">Cập nhật</button>
              </label>
            </div>
            <button class="btn btn-outline-secondary w-100 mt-3" @click="openAddAddressModal">
              <i class="fas fa-plus me-2"></i>Thêm Địa Chỉ Mới
            </button>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-light" data-bs-dismiss="modal">Hủy</button>
            <button type="button" class="btn btn-primary" @click="confirmAddressSelection" data-bs-dismiss="modal">Xác nhận</button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- MODAL THÊM/SỬA ĐỊA CHỈ -->
    <div class="modal fade" id="addUpdateAddressModal" tabindex="-1">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ isEditingAddress ? 'Cập nhật địa chỉ' : 'Thêm địa chỉ mới' }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveAddress">
              <div class="mb-3"><label class="form-label">Địa chỉ cụ thể</label><input type="text" class="form-control" v-model="addressForm.diaChiCuThe" required></div>
              <div class="mb-3"><label class="form-label">Phường/Xã</label><input type="text" class="form-control" v-model="addressForm.phuongXa" required></div>
              <div class="mb-3"><label class="form-label">Tỉnh/Thành phố</label><input type="text" class="form-control" v-model="addressForm.tinhThanh" required></div>
              <div class="mb-3"><label class="form-label">Số điện thoại</label><input type="text" class="form-control" v-model="addressForm.soDienThoai" required></div>
              <div class="form-check">
                <input class="form-check-input" type="checkbox" v-model="addressForm.isDefault" id="isDefaultCheck">
                <label class="form-check-label" for="isDefaultCheck">Đặt làm địa chỉ mặc định</label>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-light" data-bs-dismiss="modal">Hủy</button>
            <button type="button" class="btn btn-primary" @click="saveAddress">Lưu thay đổi</button>
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

// --- STATE ---
const selectedItems = ref([]);
const paymentMethods = ref([]);
const selectedPaymentMethod = ref(null);
const isSubmitting = ref(false);
const addresses = ref([]);
const selectedAddressId = ref(null);
const selectedAddressIdInModal = ref(null);
const isAddressesLoading = ref(true);
const customerInfo = reactive({ name: '', email: '' });
const isEditingAddress = computed(() => !!addressForm.idDiaChi);
const addressForm = reactive({
  idDiaChi: null, diaChiCuThe: '', phuongXa: '', tinhThanh: '', soDienThoai: '', isDefault: false,
});

let addUpdateAddressModalInstance = null;
let addressModalInstance = null;

// --- COMPUTED ---
const subTotal = computed(() => selectedItems.value.reduce((total, item) => total + item.donGia * item.soLuong, 0));
const totalDisplay = computed(() => subTotal.value > 0 ? subTotal.value : 0);
const canPlaceOrder = computed(() => selectedItems.value.length > 0 && !isSubmitting.value && selectedPaymentMethod.value && selectedAddressId.value);
const selectedAddress = computed(() => {
  if (!selectedAddressId.value || !addresses.value.length) return null;
  const addr = addresses.value.find(a => a.idDiaChi === selectedAddressId.value);
  if (!addr) return null;
  return {
    ...addr,
    customerName: customerInfo.name,
    phone: addr.soDienThoai,
    fullAddress: [addr.diaChiCuThe, addr.phuongXa, addr.tinhThanh].filter(Boolean).join(', '),
    isDefault: addr.trangThai === 1
  };
});

// --- API & LOGIC ---
const loadPaymentMethods = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/api/phuongtt`);
    paymentMethods.value = response.data.map(method => ({
      id: method.idPTT,
      description: method.ten === 'THANH TOAN KHI NHAN HANG' ? 'Thanh toán khi nhận hàng' : 'Thanh toán Online'
    }));
    if (paymentMethods.value.length > 0) selectedPaymentMethod.value = paymentMethods.value[0].id;
  } catch (err) { toast.error('Lỗi khi tải phương thức thanh toán.'); }
};

const loadCustomerInfo = async () => {
  // Kiểm tra idTK thay vì idKH
  if (!auth.isAuthenticated || !auth.user?.idTK) return;
  
  try {
    // GỌI ĐÚNG API THEO idTK
    const response = await axios.get(`${API_BASE_URL}/client/api/khachhang/byTaiKhoanId/${auth.user.idTK}`);
    
    const customer = response.data;
    customerInfo.name = customer.tenKH;
    customerInfo.phone = customer.sdt;
  } catch (err) {
    console.error("Lỗi khi tải thông tin khách hàng:", err);
    toast.error("Không thể tải thông tin khách hàng.");
  }
};

const loadAddresses = async () => {
  if (!auth.isAuthenticated || !auth.user?.idKH) {
    isAddressesLoading.value = false; return;
  }
  try {
    isAddressesLoading.value = true;
    const response = await axios.get(`${API_BASE_URL}/client/api/diachi/${auth.user.idKH}`);
    addresses.value = response.data;
    const defaultAddress = addresses.value.find(a => a.trangThai === 1) || addresses.value[0];
    if (defaultAddress) {
      selectedAddressId.value = defaultAddress.idDiaChi;
      selectedAddressIdInModal.value = defaultAddress.idDiaChi;
    }
  } catch (err) {
    toast.error("Không thể tải danh sách địa chỉ.");
  } finally {
    isAddressesLoading.value = false;
  }
};

const confirmAddressSelection = () => {
  selectedAddressId.value = selectedAddressIdInModal.value;
  toast.success("Đã cập nhật địa chỉ giao hàng.");
};

const placeOrder = async () => {
  if (!canPlaceOrder.value) {
    toast.error('Vui lòng chọn đầy đủ thông tin.'); return;
  }
  isSubmitting.value = true;
  try {
    const currentAddress = selectedAddress.value;
    const orderData = {
      idTK: auth.user.idTK,
      shippingInfo: { 
        addressId: currentAddress.idDiaChi,
        firstName: customerInfo.name.split(' ')[0] || '',
        lastName: customerInfo.name.split(' ').slice(1).join(' ') || '',
        phone: currentAddress.phone,
        address: currentAddress.fullAddress,
        email: customerInfo.email
      },
      paymentMethod: selectedPaymentMethod.value,
      total: totalDisplay.value,
      items: selectedItems.value.map(item => ({
        idGHCT: item.idGHCT, idSPCT: item.idSPCT,
        soLuong: item.soLuong, donGia: Number(item.donGia)
      }))
    };
    await axios.post(`${API_BASE_URL}/client/api/hoadon`, orderData);
    toast.success('Đặt hàng thành công!');
    sessionStorage.removeItem('selectedCheckoutItems');
    router.push('/order-success');
  } catch (err) {
    const errorMessage = err.response?.data?.message || 'Lỗi khi đặt hàng';
    toast.error(errorMessage);
  } finally {
    isSubmitting.value = false;
  }
};

const openAddAddressModal = () => {
  Object.assign(addressForm, { idDiaChi: null, diaChiCuThe: '', phuongXa: '', quanHuyen: '', tinhThanh: '', soDienThoai: '', isDefault: false });
  if (addUpdateAddressModalInstance) addUpdateAddressModalInstance.show();
};

const openUpdateAddressModal = (address) => {
  Object.assign(addressForm, { ...address, isDefault: address.trangThai === 1 });
  if (addUpdateAddressModalInstance) addUpdateAddressModalInstance.show();
};

const saveAddress = async () => {
  if(!auth.user?.idKH){
    toast.error("Không tìm thấy thông tin khách hàng."); return;
  }
  const addressPayload = {
    idKH: auth.user.idKH,
    diaChiCuThe: addressForm.diaChiCuThe,
    phuongXa: addressForm.phuongXa,
    quanHuyen: addressForm.quanHuyen,
    tinhThanh: addressForm.tinhThanh,
    soDienThoai: addressForm.soDienThoai,
    trangThai: addressForm.isDefault ? 1 : 0
  };
  
  try {
    if (isEditingAddress.value) {
      await axios.put(`${API_BASE_URL}/client/api/diachi/${addressForm.idDiaChi}`, addressPayload);
      toast.success('Cập nhật địa chỉ thành công!');
    } else {
      await axios.post(`${API_BASE_URL}/client/api/diachi`, addressPayload);
      toast.success('Thêm địa chỉ mới thành công!');
    }
    await loadAddresses();
    if (addUpdateAddressModalInstance) addUpdateAddressModalInstance.hide();
  } catch (err) {
    toast.error(err.response?.data?.message || 'Lỗi khi lưu địa chỉ.');
  }
};

// --- LIFECYCLE ---
onMounted(async () => {
  const addressModalElement = document.getElementById('addressModal');
  const addUpdateModalElement = document.getElementById('addUpdateAddressModal');
  if (addressModalElement) addressModalInstance = new Modal(addressModalElement);
  if (addUpdateModalElement) addUpdateAddressModalInstance = new Modal(addUpdateModalElement);
  
  const storedItems = sessionStorage.getItem('selectedCheckoutItems');
  if (storedItems) {
    try {
      selectedItems.value = JSON.parse(storedItems);
    } catch (e) { router.push('/cart'); return; }
  } else {
    router.push('/cart'); return;
  }
  
  await Promise.all([loadPaymentMethods(), loadCustomerInfo(), loadAddresses()]);
});

onUnmounted(() => {
  if (addressModalInstance) addressModalInstance.dispose();
  if (addUpdateAddressModalInstance) addUpdateAddressModalInstance.dispose();
});
</script>

<style scoped>
/* Style của bạn giữ nguyên */
.checkout-page { background-color: #f5f5f5; padding-bottom: 120px; }
.card { border-radius: 0.25rem; border: none; }
.shipping-address-box { padding: 1rem 0; }
.cart-product-img { width: 50px; height: 50px; object-fit: cover; border-radius: 0.25rem; }
.payment-option { padding: 0.75rem 1rem; border: 1px solid #e9ecef; border-radius: 0.5rem; cursor: pointer; transition: all 0.2s; }
.payment-option.active { border-color: var(--bs-primary); background-color: #e7f1ff; }
.payment-summary .summary-row { display: flex; justify-content: space-between; margin-bottom: 1rem; color: #6c757d; align-items: center; }
.payment-summary .total { font-weight: bold; font-size: 1.2rem; color: #212529; border-top: 1px solid #e9ecef; padding-top: 1rem; margin-top: 0.5rem; }
.checkout-footer { background: #fff; border-top: 1px solid #e9ecef; padding: 1rem; position: fixed; bottom: 0; left: 0; width: 100%; z-index: 1000; box-shadow: 0 -2px 10px rgba(0,0,0,0.05); }
.list-group-item { cursor: pointer; }
.address-modal-body { max-height: 60vh; overflow-y: auto; }
</style>