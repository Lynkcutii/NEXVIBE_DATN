<template>
  <h1 class="h3 mb-4 text-gray-800">{{ isEditing ? 'Chỉnh sửa Khuyến mại' : 'Tạo Khuyến mại mới' }}</h1>
  <form @submit.prevent="saveVoucher">
    <div class="card shadow mb-4">
      <div class="card-header">
        <h6 class="m-0 fw-bold text-primary">Thông tin cơ bản</h6>
      </div>
      <div class="card-body">
        <div class="row">
          <div class="col-md-6 mb-3">
            <label for="voucherCode" class="form-label">Mã Code <span class="text-danger">*</span></label>
            <input type="text" class="form-control text-uppercase" id="voucherCode" v-model="voucher.code" required>
          </div>
          <div class="col-md-6 mb-3">
            <label for="voucherDesc" class="form-label">Mô tả ngắn</label>
            <input type="text" class="form-control" id="voucherDesc" v-model="voucher.description">
          </div>
        </div>
      </div>
    </div>

    <div class="card shadow mb-4">
      <div class="card-header">
        <h6 class="m-0 fw-bold text-primary">Giá trị & Điều kiện</h6>
      </div>
      <div class="card-body">
        <div class="row">
          <div class="col-md-6 mb-3">
            <label for="voucherType" class="form-label">Loại giảm giá</label>
            <select class="form-select" id="voucherType" v-model="voucher.type">
              <option value="percentage">Theo phần trăm (%)</option>
              <option value="fixed">Số tiền cố định (VND)</option>
            </select>
          </div>
          <div class="col-md-6 mb-3">
            <label for="voucherValue" class="form-label">Giá trị giảm</label>
            <input type="number" class="form-control" id="voucherValue" v-model.number="voucher.value" required>
          </div>
          <div class="col-md-6 mb-3">
            <label for="minOrderValue" class="form-label">Giá trị đơn hàng tối thiểu</label>
            <input type="number" class="form-control" id="minOrderValue" v-model.number="voucher.min_order_value" placeholder="Để trống nếu không áp dụng">
          </div>
          <div class="col-md-6 mb-3">
            <label for="maxUses" class="form-label">Số lần sử dụng tối đa</label>
            <input type="number" class="form-control" id="maxUses" v-model.number="voucher.max_uses" placeholder="Để trống nếu không giới hạn">
          </div>
        </div>
      </div>
    </div>
    
    <div class="card shadow mb-4">
      <div class="card-header">
        <h6 class="m-0 fw-bold text-primary">Thời gian hiệu lực</h6>
      </div>
      <div class="card-body">
        <div class="row">
          <div class="col-md-6 mb-3">
            <label for="startDate" class="form-label">Ngày bắt đầu</label>
            <input type="date" class="form-control" id="startDate" v-model="voucher.start_date">
          </div>
          <div class="col-md-6 mb-3">
            <label for="endDate" class="form-label">Ngày kết thúc</label>
            <input type="date" class="form-control" id="endDate" v-model="voucher.end_date">
          </div>
        </div>
      </div>
    </div>

    <div class="d-flex justify-content-end gap-2">
      <router-link :to="{ name: 'admin.vouchers.list' }" class="btn btn-secondary">Hủy</router-link>
      <button type="submit" class="btn btn-primary">Lưu khuyến mại</button>
    </div>
  </form>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const isEditing = computed(() => !!route.params.id);

const voucher = ref({
  code: '',
  description: '',
  type: 'percentage',
  value: 10,
  min_order_value: 0,
  max_uses: 100,
  start_date: '',
  end_date: ''
});

onMounted(() => {
  if (isEditing.value) {
    console.log(`Lấy dữ liệu cho voucher ID: ${route.params.id}`);
    // Giả lập việc lấy dữ liệu từ API để điền vào form
    const sampleVoucher = { id: 1, code: 'SALE50', description: 'Giảm 50% cho đơn hàng đầu tiên', type: 'percentage', value: 50, min_order_value: 0, max_uses: 100, start_date: '2024-07-01', end_date: '2024-07-31' };
    voucher.value = { ...sampleVoucher };
  }
});

const saveVoucher = () => {
  if (isEditing.value) {
    console.log('Đang cập nhật voucher:', voucher.value);
  } else {
    console.log('Đang tạo mới voucher:', voucher.value);
  }
  router.push({ name: 'admin.vouchers.list' }); 
};
</script>