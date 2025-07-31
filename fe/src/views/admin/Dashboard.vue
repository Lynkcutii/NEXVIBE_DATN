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
    <div class="col-xl-3 col-md-6 mb-4">
      <div class="card border-start-primary shadow h-100 py-2">
        <div class="card-body">
          <div class="row g-0 align-items-center">
            <div class="col">
              <div class="text-xs fw-bold text-primary text-uppercase mb-1">Doanh thu</div>
              <div class="h5 mb-0 fw-bold text-gray-800">{{ formatCurrency(stats.revenue) }}</div>
            </div>
            <div class="col-auto"><i class="fas fa-dollar-sign fa-2x text-gray-300"></i></div>
          </div>
        </div>
      </div>
    </div>
    <!-- ... các thẻ thống kê khác ... -->
    <div class="col-xl-3 col-md-6 mb-4"><div class="card border-start-success shadow h-100 py-2"><div class="card-body"><div class="row g-0 align-items-center"><div class="col"><div class="text-xs fw-bold text-success text-uppercase mb-1">Đơn hàng mới</div><div class="h5 mb-0 fw-bold text-gray-800">{{ stats.newOrders }}</div></div><div class="col-auto"><i class="fas fa-box fa-2x text-gray-300"></i></div></div></div></div></div>
    <div class="col-xl-3 col-md-6 mb-4"><div class="card border-start-info shadow h-100 py-2"><div class="card-body"><div class="row g-0 align-items-center"><div class="col"><div class="text-xs fw-bold text-info text-uppercase mb-1">Khách hàng mới</div><div class="h5 mb-0 fw-bold text-gray-800">{{ stats.newCustomers }}</div></div><div class="col-auto"><i class="fas fa-users fa-2x text-gray-300"></i></div></div></div></div></div>
    <div class="col-xl-3 col-md-6 mb-4"><div class="card border-start-warning shadow h-100 py-2"><div class="card-body"><div class="row g-0 align-items-center"><div class="col"><div class="text-xs fw-bold text-warning text-uppercase mb-1">Sản phẩm sắp hết</div><div class="h5 mb-0 fw-bold text-gray-800">{{ stats.lowStockProducts }}</div></div><div class="col-auto"><i class="fas fa-exclamation-triangle fa-2x text-gray-300"></i></div></div></div></div></div>
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
          <ul class="list-group list-group-flush">
            <li v-for="product in topSellingProducts" :key="product.id" class="list-group-item d-flex justify-content-between align-items-center">
              {{ product.name }}
              <span class="badge bg-primary rounded-pill">{{ product.sold }}</span>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { Line } from 'vue-chartjs';
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend } from 'chart.js';
import { ElMessage } from 'element-plus'; // Import ElMessage
import axios from 'axios'; // Import axios

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend);

// --- TRẠNG THÁI VÀ DỮ LIỆU ---
const stats = ref({ revenue: 0, newOrders: 0, newCustomers: 0, lowStockProducts: 0 });
const topSellingProducts = ref([]);
const chartData = ref({ labels: [], datasets: 
  [{ label: 'Doanh thu',
   borderColor: 'rgba(78, 115, 223, 1)',
      backgroundColor: 'rgba(78, 115, 223, 0.1)',
      pointBackgroundColor: 'rgba(78, 115, 223, 1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(78, 115, 223, 1)', data: [] ,
     fill: true,
      tension: 0.4}] });
const chartOptions = ref({ responsive: true, maintainAspectRatio: false });

const loadingStats = ref(true);
const loadingChart = ref(true);
const chartLoaded = ref(false);

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
        const params = {
            from: dateRange.value[0],
            to: dateRange.value[1]
        };

        const [statsResponse, chartResponse, topProductsResponse] = await Promise.all([
            // Thay thế bằng lời gọi API thật của bạn
            // axios.get('/api/dashboard/stats', { params }),
            // axios.get('/api/dashboard/revenue-chart', { params }),
            // axios.get('/api/dashboard/top-selling', { params }),

            // --- Dữ liệu giả lập ---
            Promise.resolve({ data: { revenue: Math.random() * 100000000, newOrders: Math.floor(Math.random() * 50), newCustomers: 30, lowStockProducts: 5 } }),
            Promise.resolve({ data: { labels: ['Tuần 1', 'Tuần 2', 'Tuần 3', 'Tuần 4'], data: Array.from({length: 4}, () => Math.random() * 20000000) } }),
            Promise.resolve({ data: [ { id: 1, name: 'Áo Thun Năng Động', sold: 140 }, { id: 2, name: 'Quần Jogger Pro', sold: 98 } ] })
        ]);

        stats.value = statsResponse.data;
        topSellingProducts.value = topProductsResponse.data;
        chartData.value.labels = chartResponse.data.labels;
        chartData.value.datasets[0].data = chartResponse.data.data;
        chartLoaded.value = true;

    } catch (error) {
        console.error("Lỗi khi tải dữ liệu dashboard:", error);
        ElMessage.error('Không thể tải dữ liệu dashboard.');
    } finally {
        loadingStats.value = false;
        loadingChart.value = false;
    }
};

const formatCurrency = (value) => {
  if (typeof value !== 'number') return '0 đ';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

onMounted(() => {
    setDefaultDateRange();
    fetchDashboardData();
});
</script>

<style scoped>
/* Giữ nguyên các style cũ */
.card .border-start-primary { border-left: .25rem solid #4e73df!important; }
.card .border-start-success { border-left: .25rem solid #1cc88a!important; }
.card .border-start-info { border-left: .25rem solid #36b9cc!important; }
.card .border-start-warning { border-left: .25rem solid #f6c23e!important; }
.text-gray-300 { color: #6f7594!important; }
</style>