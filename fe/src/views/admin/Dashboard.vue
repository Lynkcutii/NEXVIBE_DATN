<template>
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h3 mb-0">Dashboard</h1>
    <!-- ================================== -->
    <!-- KHỐI BỘ LỌC NGÀY THÁNG -->
    <!-- ================================== -->
    <div class="d-flex align-items-center gap-2">
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="Đến"
        start-placeholder="Ngày bắt đầu"
        end-placeholder="Ngày kết thúc"
        format="DD/MM/YYYY"
        value-format="YYYY-MM-DD"
        :disabled-date="disabledDate"
      />
      <button @click="applyDateFilter" class="btn btn-primary" :disabled="loadingStats || loadingChart">Áp dụng</button>
    </div>
  </div>
  
  <!-- Hàng các thẻ thống kê nhanh -->
  <div class="row" v-loading="loadingStats">
    <!-- Thẻ Tổng Doanh Thu -->
    <div class="col-xl-3 col-md-6 mb-4">
      <div class="card border-start-success shadow h-100 py-2">
        <div class="card-body">
          <div class="row g-0 align-items-center">
            <div class="col">
              <div class="text-xs fw-bold text-success text-uppercase mb-1">Tổng Doanh Thu</div>
              <div class="h5 mb-0 fw-bold text-gray-800">{{ formatCurrency(dashboardStats.tongQuanDoanhThu?.tongTatCa || 0) }}</div>
              <div class="text-xs text-success">Hôm nay: {{ formatCurrency(dashboardStats.tongQuanDoanhThu?.homNay || 0) }}</div>
            </div>
            <div class="col-auto"><i class="fas fa-dollar-sign fa-2x text-gray-300"></i></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Thẻ Tổng Hóa Đơn -->
    <div class="col-xl-3 col-md-6 mb-4">
      <div class="card border-start-primary shadow h-100 py-2">
        <div class="card-body">
          <div class="row g-0 align-items-center">
            <div class="col">
              <div class="text-xs fw-bold text-primary text-uppercase mb-1">Tổng Hóa Đơn</div>
              <div class="h5 mb-0 fw-bold text-gray-800">{{ dashboardStats.tongQuanHoaDon?.tongSo || 0 }}</div>
              <div class="text-xs text-muted">Hôm nay: {{ dashboardStats.tongQuanHoaDon?.homNay || 0 }}</div>
            </div>
            <div class="col-auto"><i class="fas fa-receipt fa-2x text-gray-300"></i></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Thẻ Khách Hàng -->
    <div class="col-xl-3 col-md-6 mb-4">
      <div class="card border-start-info shadow h-100 py-2">
        <div class="card-body">
          <div class="row g-0 align-items-center">
            <div class="col">
              <div class="text-xs fw-bold text-info text-uppercase mb-1">Khách Hàng</div>
              <div class="h5 mb-0 fw-bold text-gray-800">{{ dashboardStats.tongQuanKhachHang?.tongSo || 0 }}</div>
              <div class="text-xs text-info">Hoạt động: {{ dashboardStats.tongQuanKhachHang?.hoatDong || 0 }}</div>
            </div>
            <div class="col-auto"><i class="fas fa-users fa-2x text-gray-300"></i></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Thẻ Sản Phẩm Sắp Hết -->
    <div class="col-xl-3 col-md-6 mb-4">
      <div class="card border-start-warning shadow h-100 py-2">
        <div class="card-body">
          <div class="row g-0 align-items-center">
            <div class="col">
              <div class="text-xs fw-bold text-warning text-uppercase mb-1">Sắp Hết Hàng</div>
              <div class="h5 mb-0 fw-bold text-gray-800">{{ dashboardStats.tongQuanSanPham?.sapHetHang || 0 }}</div>
              <div class="text-xs text-danger">Hết hàng: {{ dashboardStats.tongQuanSanPham?.hetHang || 0 }}</div>
            </div>
            <div class="col-auto"><i class="fas fa-exclamation-triangle fa-2x text-gray-300"></i></div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Hàng thứ hai - Thông tin chi tiết sản phẩm -->
  <div class="row" v-loading="loadingStats">
    <div class="col-xl-3 col-md-6 mb-4">
      <div class="card border-start-info shadow h-100 py-2">
        <div class="card-body">
          <div class="row g-0 align-items-center">
            <div class="col">
              <div class="text-xs fw-bold text-info text-uppercase mb-1">Tổng Sản Phẩm</div>
              <div class="h5 mb-0 fw-bold text-gray-800">{{ dashboardStats.tongQuanSanPham?.tongSo || 0 }}</div>
              <div class="text-xs text-info">Còn hàng: {{ dashboardStats.tongQuanSanPham?.conHang || 0 }}</div>
            </div>
            <div class="col-auto"><i class="fas fa-box fa-2x text-gray-300"></i></div>
          </div>
        </div>
      </div>
    </div>

    <div class="col-xl-3 col-md-6 mb-4">
      <div class="card border-start-secondary shadow h-100 py-2">
        <div class="card-body">
          <div class="row g-0 align-items-center">
            <div class="col">
              <div class="text-xs fw-bold text-secondary text-uppercase mb-1">Doanh Thu Tháng</div>
              <div class="h5 mb-0 fw-bold text-gray-800">{{ formatCurrency(dashboardStats.tongQuanDoanhThu?.trongThang || 0) }}</div>
              <div class="text-xs text-muted">Tháng hiện tại</div>
            </div>
            <div class="col-auto"><i class="fas fa-chart-line fa-2x text-gray-300"></i></div>
          </div>
        </div>
      </div>
    </div>

    <div class="col-xl-3 col-md-6 mb-4">
      <div class="card border-start-dark shadow h-100 py-2">
        <div class="card-body">
          <div class="row g-0 align-items-center">
            <div class="col">
              <div class="text-xs fw-bold text-dark text-uppercase mb-1">Hóa Đơn Tháng</div>
              <div class="h5 mb-0 fw-bold text-gray-800">{{ dashboardStats.tongQuanHoaDon?.trongThang || 0 }}</div>
              <div class="text-xs text-muted">Tháng hiện tại</div>
            </div>
            <div class="col-auto"><i class="fas fa-file-invoice fa-2x text-gray-300"></i></div>
          </div>
        </div>
      </div>
    </div>

    <!-- <div class="col-xl-3 col-md-6 mb-4">
      <div class="card border-start-light shadow h-100 py-2" style="border-left-color: #6c757d !important;">
        <div class="card-body">
          <div class="row g-0 align-items-center">
            <div class="col">
              <div class="text-xs fw-bold text-muted text-uppercase mb-1">Khách Hàng Mới</div>
              <div class="h5 mb-0 fw-bold text-gray-800">{{ dashboardStats.khachHangMoi?.length || 0 }}</div>
              <div class="text-xs text-muted">Đăng ký gần đây</div>
            </div>
            <div class="col-auto"><i class="fas fa-user-plus fa-2x text-gray-300"></i></div>
          </div>
        </div>
      </div>
    </div> -->
  </div>

  <!-- Hàng chứa Biểu đồ và danh sách -->
  <div class="row">
    <div class="col-lg-7 mb-4">
      <div class="card shadow" v-loading="loadingChart">
        <div class="card-header py-3">
          <h6 class="m-0 fw-bold text-primary">Biểu đồ Doanh thu</h6>
        </div>
        <div class="card-body">
          <div class="chart-container" style="position: relative; height:320px">
            <Line v-if="chartLoaded" :data="chartData" :options="chartOptions" />
            <div v-else class="text-center p-5">Đang tải dữ liệu...</div>
          </div>
        </div>
      </div>
    </div>
    <div class="col-lg-5 mb-4">
      <div class="card shadow">
        <div class="card-header py-3">
          <h6 class="m-0 fw-bold text-primary">Sản phẩm Bán chạy</h6>
        </div>
        <div class="card-body">
          <div v-if="!dashboardStats.topSanPhamBanChay || dashboardStats.topSanPhamBanChay.length === 0" class="text-center text-muted py-4">
            <i class="fas fa-box-open fa-3x mb-3"></i>
            <p>Chưa có dữ liệu sản phẩm bán chạy</p>
          </div>
          <ul v-else class="list-group list-group-flush">
            <li v-for="(product, index) in dashboardStats.topSanPhamBanChay" :key="index" class="list-group-item d-flex justify-content-between align-items-center">
              <div>
                <strong>{{ product.tenSanPham }}</strong>
                <small class="text-muted d-block">{{ formatCurrency(product.doanhThu) }}</small>
              </div>
              <span class="badge bg-primary rounded-pill">{{ Math.round(product.soLuongBan) }}</span>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>

  <!-- Thống kê chi tiết với tabs -->
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { Line } from 'vue-chartjs';
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend } from 'chart.js';
import { ElMessage } from 'element-plus'; // Import ElMessage
import axios from 'axios'; // Import axios

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend);

// --- TRẠNG THÁI VÀ DỮ LIỆU ---
const dashboardStats = ref({});
const chartData = ref({ 
  labels: [], 
  datasets: [{
    label: 'Doanh thu',
    borderColor: 'rgba(78, 115, 223, 1)',
    backgroundColor: 'rgba(78, 115, 223, 0.1)',
    pointBackgroundColor: 'rgba(78, 115, 223, 1)',
    pointBorderColor: '#fff',
    pointHoverBackgroundColor: '#fff',
    pointHoverBorderColor: 'rgba(78, 115, 223, 1)',
    data: [],
    fill: true,
    tension: 0.4
  }]
});
const chartOptions = ref({ 
  responsive: true, 
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: true,
      position: 'top'
    },
    title: {
      display: true,
      text: 'Doanh thu theo tháng'
    }
  },
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        callback: function(value) {
          return new Intl.NumberFormat('vi-VN', { 
            style: 'currency', 
            currency: 'VND',
            minimumFractionDigits: 0
          }).format(value);
        }
      }
    }
  }
});

const loadingStats = ref(true);
const loadingChart = ref(true);
const chartLoaded = ref(false);

// Thống kê chi tiết
const productStats = ref({ sanPhamBanChay: [], theoDanhMuc: [], tonKho: [] });
const customerStats = ref({ tongQuan: {}, khachHangMoi: [], khachHangVIP: [], theoThang: [] });
const inventoryStats = ref([]);

const loadingProductStats = ref(false);
const loadingCustomerStats = ref(false);
const loadingInventoryStats = ref(false);

// --- LOGIC BỘ LỌC NGÀY THÁNG ---
const dateRange = ref([]); // Dùng mảng cho el-date-picker type="daterange"

// Hàm để đặt ngày mặc định (tháng này)
const setDefaultDateRange = () => {
    const end = new Date();
    const start = new Date(end.getFullYear(), end.getMonth(), 1); // Ngày đầu tiên của tháng hiện tại
    
    // Định dạng lại thành YYYY-MM-DD
    const formatDate = (d) => d.toISOString().split('T')[0];
    dateRange.value = [formatDate(start), formatDate(end)];
};

// Vô hiệu hóa các ngày trong tương lai
const disabledDate = (time) => {
  return time.getTime() > Date.now();
};

// Hàm được gọi khi nhấn nút "Áp dụng"
const applyDateFilter = () => {
    if (!dateRange.value || dateRange.value.length !== 2) {
        ElMessage.warning('Vui lòng chọn đầy đủ khoảng thời gian.');
        return;
    }
    fetchDashboardData();
};

// --- HÀM GỌI API ---
const fetchDashboardData = async () => {
    loadingStats.value = true;
    loadingChart.value = true;
    chartLoaded.value = false;
    
    try {
        console.log('[Dashboard] Đang tải dữ liệu thống kê...');
        
        // Gọi API thống kê dashboard với bộ lọc ngày tháng
        const params = {};
        if (dateRange.value && dateRange.value.length === 2) {
            params.startDate = dateRange.value[0];
            params.endDate = dateRange.value[1];
        }
        
        const response = await axios.get('http://localhost:8080/admin/api/thongke/dashboard', {
            withCredentials: true,
            headers: { 'Content-Type': 'application/json' },
            params: params
        });
        
        console.log('[Dashboard] Dữ liệu thống kê:', response.data);
        dashboardStats.value = response.data;
        
        // Cập nhật biểu đồ doanh thu theo tháng
        if (response.data.doanhThuTheoThang && response.data.doanhThuTheoThang.length > 0) {
            const chartLabels = response.data.doanhThuTheoThang.map(item => `${item.thang}/${item.nam}`);
            const chartDataValues = response.data.doanhThuTheoThang.map(item => item.doanhThu);
            
            chartData.value.labels = chartLabels;
            chartData.value.datasets[0].data = chartDataValues;
        } else {
            // Fallback data nếu không có dữ liệu
            chartData.value.labels = ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4'];
            chartData.value.datasets[0].data = [0, 0, 0, 0];
        }
        
        chartLoaded.value = true;
        
    } catch (error) {
        console.error('[Dashboard] Lỗi khi tải dữ liệu:', error);
        ElMessage.error('Không thể tải dữ liệu dashboard: ' + (error.response?.data?.error || error.message));
        
        // Hiển thị dữ liệu trống khi có lỗi
        dashboardStats.value = {
            tongQuanHoaDon: { tongSo: 0, homNay: 0 },
            tongQuanKhachHang: { tongSo: 0, hoatDong: 0 },
            tongQuanSanPham: { tongSo: 0, conHang: 0, hetHang: 0, sapHetHang: 0 },
            topSanPhamBanChay: [],
            doanhThuTheoThang: []
        };
        
        chartData.value.labels = [];
        chartData.value.datasets[0].data = [];
        chartLoaded.value = true;
        
    } finally {
        loadingStats.value = false;
        loadingChart.value = false;
    }
};

const formatCurrency = (value) => {
  if (typeof value !== 'number') return '0 đ';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  return new Date(dateString).toLocaleDateString('vi-VN');
};

// Hàm fetch thống kê sản phẩm chi tiết
const fetchThongKeSanPham = async () => {
  loadingProductStats.value = true;
  try {
    const response = await axios.get('http://localhost:8080/admin/api/thongke/san-pham', {
      withCredentials: true,
      params: { soNgay: 30 }
    });
    productStats.value = response.data;
    console.log('[Dashboard] Thống kê sản phẩm:', response.data);
  } catch (error) {
    console.error('[Dashboard] Lỗi thống kê sản phẩm:', error);
    ElMessage.error('Không thể tải thống kê sản phẩm');
  } finally {
    loadingProductStats.value = false;
  }
};

// Hàm fetch thống kê khách hàng chi tiết
const fetchThongKeKhachHang = async () => {
  loadingCustomerStats.value = true;
  try {
    const response = await axios.get('http://localhost:8080/admin/api/thongke/khach-hang', {
      withCredentials: true,
      params: { soNgay: 30 }
    });
    customerStats.value = response.data;
    console.log('[Dashboard] Thống kê khách hàng:', response.data);
  } catch (error) {
    console.error('[Dashboard] Lỗi thống kê khách hàng:', error);
    ElMessage.error('Không thể tải thống kê khách hàng');
  } finally {
    loadingCustomerStats.value = false;
  }
};

// Hàm fetch thống kê tồn kho
const fetchThongKeTonKho = async () => {
  loadingInventoryStats.value = true;
  try {
    const response = await axios.get('http://localhost:8080/admin/api/thongke/san-pham', {
      withCredentials: true
    });
    inventoryStats.value = response.data.tonKho || [];
    console.log('[Dashboard] Thống kê tồn kho:', response.data.tonKho);
  } catch (error) {
    console.error('[Dashboard] Lỗi thống kê tồn kho:', error);
    ElMessage.error('Không thể tải thống kê tồn kho');
  } finally {
    loadingInventoryStats.value = false;
  }
};

onMounted(() => {
    setDefaultDateRange();
    fetchDashboardData();
    // Tải thống kê chi tiết ban đầu
    fetchThongKeSanPham();
    fetchThongKeKhachHang();
    fetchThongKeTonKho();
});
</script>

<style scoped>
/* Giữ nguyên các style cũ */
.card .border-start-primary { border-left: .25rem solid #4e73df!important; }
.card .border-start-success { border-left: .25rem solid #1cc88a!important; }
.card .border-start-info { border-left: .25rem solid #36b9cc!important; }
.card .border-start-warning { border-left: .25rem solid #f6c23e!important; }
.card .border-start-secondary { border-left: .25rem solid #6c757d!important; }
.card .border-start-dark { border-left: .25rem solid #343a40!important; }
.card .border-start-light { border-left: .25rem solid #6c757d!important; }
.text-gray-300 { color: #6f7594!important; }
</style>