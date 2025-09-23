<template>
  <div>
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h3 mb-0 text-gray-800">Quản lý đơn hàng</h1>
    </div>

    <!-- Search and Filter Section -->
    <div class="card mb-4">
      <div class="card-body">
        <div class="row g-3">
          <!-- Search Box -->
          <div class="col-md-4">
            <div class="input-group">
              <span class="input-group-text">
                <i class="fas fa-search"></i>
              </span>
              <input
                type="text"
                class="form-control"
                placeholder="Tìm kiếm hoá đơn"
                v-model="searchQuery"
                @input="handleSearch"
              />
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="col-md-4">
            <div class="d-flex gap-2">
              <button class="btn btn-primary" @click="createInvoice">
                <i class="fas fa-plus me-2"></i>Tạo hoá đơn
              </button>
            </div>
          </div>

          <!-- Date Range -->
          <div class="col-md-4">
            <div class="row g-2">
              <div class="col-6">
                <input
                  type="date"
                  class="form-control"
                  v-model="dateFrom"
                  @change="handleDateFilter"
                />
              </div>
              <div class="col-6">
                <input
                  type="date"
                  class="form-control"
                  v-model="dateTo"
                  @change="handleDateFilter"
                />
              </div>
            </div>
          </div>
        </div>

        <!-- Filter Options -->
        <div class="row mt-3">
          <div class="col-md-6">
            <div class="btn-group" role="group">
              <input type="radio" class="btn-check" id="type-all" v-model="orderType" value="all" />
              <label class="btn btn-outline-secondary" for="type-all">Tất cả</label>
              
              <input type="radio" class="btn-check" id="type-online" v-model="orderType" value="Trực tuyến" />
              <label class="btn btn-outline-secondary" for="type-online">Trực tuyến</label>
              
              <input type="radio" class="btn-check" id="type-offline" v-model="orderType" value="Tại quầy" />
              <label class="btn btn-outline-secondary" for="type-offline">Tại quầy</label>
            </div>
          </div>
          
          <div class="col-md-6 text-end">
            <button class="btn btn-warning" @click="exportExcel">
              <i class="fas fa-file-excel me-2"></i>Export Excel
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Status Tabs -->
    <div class="card mb-4">
      <div class="card-body p-0">
        <ul class="nav nav-tabs nav-fill" role="tablist">
          <li class="nav-item" v-for="tab in statusTabs" :key="tab.value">
            <button
              class="nav-link"
              :class="{ active: selectedStatus === tab.value }"
              @click="selectedStatus = tab.value"
            >
              {{ tab.label }}
            </button>
          </li>
        </ul>
      </div>
    </div>

    <!-- Orders Table -->
    <div class="card">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover mb-0">
            <thead class="table-light">
              <tr>
                <th>#</th>
                <th>Mã</th>
                <th>Tổng SP</th>
                <th>Tổng số tiền</th>
                <th>Tên khách hàng</th>
                <th>Ngày tạo</th>
                <th>Loại hoá đơn</th>
                <th>Trạng thái</th>
                <th>Hành động</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(order, index) in filteredOrders" :key="order.idHD">
                <td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>
                <td>
                  <strong>{{ order.maHD }}</strong>
                </td>
                <td>{{ order.totalProducts || 0 }} SP</td>
                <td>
                  <strong class="text-primary">{{ formatCurrency(order.tongTien) }} ₫</strong>
                </td>
                <td>{{ order.customerName || 'Khách lẻ' }}</td>
                <td>{{ formatDateTime(order.ngayTao) }}</td>
                <td>
                  <span :class="getOrderTypeClass(order.loaiHoaDon)">
                    {{ order.loaiHoaDon }}
                  </span>
                </td>
                <td>
                  <span :class="getStatusClass(order.trangThai)">
                    {{ order.trangThai }}
                  </span>
                </td>
                <td>
                  <div class="btn-group">
                    <button
                      class="btn btn-sm btn-outline-primary"
                      @click="viewOrderDetail(order.idHD)"
                      title="Xem chi tiết"
                    >
                      <i class="fas fa-eye"></i>
                    </button>
                    <button
                      v-if="canEditStatus(order.trangThai)"
                      class="btn btn-sm btn-outline-warning"
                      @click="openStatusEditModal(order)"
                      title="Chỉnh sửa trạng thái"
                    >
                      <i class="fas fa-edit"></i>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination -->
        <div class="card-footer d-flex justify-content-between align-items-center">
          <div>
            Hiển thị {{ (currentPage - 1) * pageSize + 1 }} đến {{ Math.min(currentPage * pageSize, totalOrders) }} 
            trong tổng số {{ totalOrders }} đơn hàng
          </div>
          <nav>
            <ul class="pagination pagination-sm mb-0">
              <li class="page-item" :class="{ disabled: currentPage === 1 }">
                <button class="page-link" @click="changePage(currentPage - 1)">
                  <i class="fas fa-chevron-left"></i>
                </button>
              </li>
              <li 
                class="page-item" 
                v-for="page in visiblePages" 
                :key="page"
                :class="{ active: page === currentPage }"
              >
                <button class="page-link" @click="changePage(page)">{{ page }}</button>
              </li>
              <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                <button class="page-link" @click="changePage(currentPage + 1)">
                  <i class="fas fa-chevron-right"></i>
                </button>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </div>

    <!-- Modal chi tiết hóa đơn -->
    <div v-if="showDetailModal" class="modal fade show d-block" tabindex="-1" style="background:rgba(0,0,0,0.3);">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Chi tiết hóa đơn: {{ selectedOrder?.maHD }}</h5>
            <button type="button" class="btn-close" @click="closeDetailModal"></button>
          </div>
          <div class="modal-body">
            <div v-if="selectedOrder">
              <div class="row mb-2">
                <div class="col-md-6"><b>Khách hàng:</b> {{ selectedOrder.customerName }}</div>
                <div class="col-md-6"><b>Ngày tạo:</b> {{ formatDateTime(selectedOrder.ngayTao) }}</div>
              </div>
              <div class="row mb-2">
                <div class="col-md-6"><b>Trạng thái:</b> <span :class="getStatusClass(selectedOrder.trangThai)">{{ selectedOrder.trangThai }}</span></div>
                <div class="col-md-6"><b>Loại hóa đơn:</b> <span :class="getOrderTypeClass(selectedOrder.loaiHoaDon)">{{ selectedOrder.loaiHoaDon }}</span></div>
              </div>
              <div class="row mb-2">
                <div class="col-md-6"><b>Tổng tiền:</b> <span class="text-primary">{{ formatCurrency(selectedOrder.tongTien) }} ₫</span></div>
                <div class="col-md-6"><b>Tổng sản phẩm:</b> {{ selectedOrder.totalProducts }}</div>
              </div>
              <hr/>
              <h6>Danh sách sản phẩm</h6>
              <table class="table table-bordered">
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Tên sản phẩm</th>
                    <th>Size</th>
                    <th>Màu</th>
                    <th>Số lượng</th>
                    <th>Đơn giá</th>
                    <th>Thành tiền</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(item, idx) in orderDetails" :key="item.idHDCT">
                    <td>{{ idx + 1 }}</td>
                    <td>{{ item.tenSanPham || '-' }}</td>
                    <td>{{ item.tenSize || '-' }}</td>
                    <td>{{ item.tenMauSac || '-' }}</td>
                    <td>{{ item.soLuong }}</td>
                    <td>{{ formatCurrency(item.donGia) }}</td>
                    <td>{{ formatCurrency(item.thanhTien) }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div v-else>Đang tải dữ liệu...</div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" @click="closeDetailModal">Đóng</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal chỉnh sửa trạng thái -->
    <div v-if="showStatusEditModal" class="modal fade show d-block" tabindex="-1" style="background:rgba(0,0,0,0.3);">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Chỉnh sửa trạng thái - {{ editingOrder?.maHD }}</h5>
            <button type="button" class="btn-close" @click="closeStatusEditModal"></button>
          </div>
          <div class="modal-body">
            <div v-if="editingOrder">
              <div class="mb-3">
                <label class="form-label">Trạng thái hiện tại:</label>
                <span :class="getStatusClass(editingOrder.trangThai)" class="badge ms-2">
                  {{ editingOrder.trangThai }}
                </span>
              </div>
              
              <div class="mb-3">
                <label class="form-label">Chọn trạng thái mới:</label>
                <div class="d-flex flex-wrap gap-2">
                  <button
                    v-for="status in getAvailableStatuses(editingOrder.trangThai)"
                    :key="status"
                    class="btn"
                    :class="selectedNewStatus === status ? 'btn-primary' : 'btn-outline-primary'"
                    @click="selectedNewStatus = status"
                  >
                    {{ status }}
                  </button>
                </div>
              </div>

              <div v-if="selectedNewStatus === 'Hủy'" class="mb-3">
                <label class="form-label">Lý do hủy:</label>
                <textarea 
                  v-model="cancelReason" 
                  class="form-control" 
                  rows="3" 
                  placeholder="Nhập lý do hủy đơn hàng..."
                ></textarea>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" @click="closeStatusEditModal">Hủy</button>
            <button 
              class="btn btn-primary" 
              @click="updateOrderStatus"
              :disabled="!selectedNewStatus"
            >
              Cập nhật
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import * as XLSX from 'xlsx';

const router = useRouter();

// Reactive state
const orders = ref([]);
const searchQuery = ref('');
const dateFrom = ref('');
const dateTo = ref('');
const orderType = ref('all');
const selectedStatus = ref('all');
const currentPage = ref(1);
const pageSize = ref(5);
const totalOrders = ref(0);

// Status edit modal
const showStatusEditModal = ref(false);
const editingOrder = ref(null);
const selectedNewStatus = ref('');
const cancelReason = ref('');

// Status tabs configuration
const statusTabs = ref([
  { label: 'TẤT CẢ', value: 'all' },
  { label: 'CHỜ XÁC NHẬN', value: 'Chờ xác nhận' },
  { label: 'GIAO HÀNG', value: 'Giao hàng' },
  { label: 'ĐANG VẬN CHUYỂN', value: 'Đang vận chuyển' },
  { label: 'HOÀN THÀNH', value: 'Hoàn thành' },
  { label: 'HỦY', value: 'Hủy' }
]);

// Modal state
const showDetailModal = ref(false);
const selectedOrder = ref(null);
const orderDetails = ref([]);

// API base URL
const API_BASE_URL = 'http://localhost:8080/admin/api';

// Computed properties
const filteredOrders = computed(() => {
  let filtered = orders.value;

  // Filter by search query
  if (searchQuery.value) {
    filtered = filtered.filter(order => 
      order.maHD?.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      order.customerName?.toLowerCase().includes(searchQuery.value.toLowerCase())
    );
  }

  // Filter by order type
  if (orderType.value !== 'all') {
    filtered = filtered.filter(order => order.loaiHoaDon === orderType.value);
  }

  // Filter by status
  if (selectedStatus.value !== 'all') {
    filtered = filtered.filter(order => order.trangThai === selectedStatus.value);
  }

  // Filter by date range
  if (dateFrom.value || dateTo.value) {
    filtered = filtered.filter(order => {
      const orderDate = new Date(order.ngayTao);
      const fromDate = dateFrom.value ? new Date(dateFrom.value) : null;
      const toDate = dateTo.value ? new Date(dateTo.value) : null;
      
      if (fromDate && orderDate < fromDate) return false;
      if (toDate && orderDate > toDate) return false;
      return true;
    });
  }

  return filtered;
});

const totalPages = computed(() => Math.ceil(totalOrders.value / pageSize.value));

const visiblePages = computed(() => {
  const pages = [];
  const start = Math.max(1, currentPage.value - 2);
  const end = Math.min(totalPages.value, currentPage.value + 2);
  
  for (let i = start; i <= end; i++) {
    pages.push(i);
  }
  return pages;
});

// Methods
const fetchOrders = async () => {
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      keyword: searchQuery.value,
      status: selectedStatus.value !== 'all' ? selectedStatus.value : undefined,
      type: orderType.value !== 'all' ? orderType.value : undefined,
      dateFrom: dateFrom.value || undefined,
      dateTo: dateTo.value || undefined
    };
    const response = await axios.get(`${API_BASE_URL}/hoadon/filter`, { params });
    const pageData = response.data;
    const hoaDonList = pageData.content || [];
    orders.value = hoaDonList.map(order => ({
      ...order,
      customerName: order.customerName || 'Khách lẻ',
      totalProducts: order.totalProducts || 0
    }));
    totalOrders.value = pageData.totalElements || hoaDonList.length;
  } catch (error) {
    console.error('Error fetching orders:', error);
    orders.value = [];
    totalOrders.value = 0;
  }
};

const handleSearch = () => {
  currentPage.value = 1;
};

const handleDateFilter = () => {
  currentPage.value = 1;
};

const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
};

const viewOrderDetail = async (orderId) => {
  const order = orders.value.find(o => o.idHD === orderId);
  selectedOrder.value = order;
  showDetailModal.value = true;
  // Gọi API lấy chi tiết hóa đơn
  try {
    const res = await axios.get(`${API_BASE_URL}/hoadonchitiet/hoadonct/${orderId}`);
    orderDetails.value = res.data;
  } catch (e) {
    console.error('Error fetching order details:', e);
    orderDetails.value = [];
  }
};

const scanCode = () => {
  // Implement barcode scanning functionality
  console.log('Scan code clicked');
};

const createInvoice = () => {
  router.push({ name: 'admin.pos' });
};

const exportExcel = async () => {
  try {
    // Lấy tất cả dữ liệu hóa đơn (không phân trang)
    const response = await axios.get(`${API_BASE_URL}/hoadon/filter`, {
      params: {
        page: 0,
        size: 10000, // Lấy tối đa 10000 record
        keyword: searchQuery.value,
        status: selectedStatus.value !== 'all' ? selectedStatus.value : undefined,
        type: orderType.value !== 'all' ? orderType.value : undefined,
        dateFrom: dateFrom.value || undefined,
        dateTo: dateTo.value || undefined
      }
    });
    
    const allOrders = response.data.content || [];
    
    // Chuẩn bị dữ liệu để xuất Excel
    const excelData = allOrders.map((order, index) => ({
      'STT': index + 1,
      'Mã hóa đơn': order.maHD,
      'Khách hàng': order.customerName || 'Khách lẻ',
      'Tổng sản phẩm': order.totalProducts || 0,
      'Tổng tiền (VND)': order.tongTien,
      'Ngày tạo': formatDateTime(order.ngayTao),
      'Loại hóa đơn': order.loaiHoaDon,
      'Trạng thái': order.trangThai
    }));
    
    // Tạo workbook và worksheet
    const wb = XLSX.utils.book_new();
    const ws = XLSX.utils.json_to_sheet(excelData);
    
    // Thiết lập độ rộng cột
    const columnWidths = [
      { wch: 5 },   // STT
      { wch: 15 },  // Mã hóa đơn
      { wch: 20 },  // Khách hàng
      { wch: 12 },  // Tổng sản phẩm
      { wch: 15 },  // Tổng tiền
      { wch: 20 },  // Ngày tạo
      { wch: 15 },  // Loại hóa đơn
      { wch: 15 }   // Trạng thái
    ];
    ws['!cols'] = columnWidths;
    
    // Thêm worksheet vào workbook
    XLSX.utils.book_append_sheet(wb, ws, 'Danh sách hóa đơn');
    
    // Tạo tên file với ngày hiện tại
    const currentDate = new Date();
    const dateStr = currentDate.toISOString().split('T')[0]; // YYYY-MM-DD
    const fileName = `Danh_sach_hoa_don_${dateStr}.xlsx`;
    
    // Xuất file
    XLSX.writeFile(wb, fileName);
    
    console.log('Xuất Excel thành công!');
  } catch (error) {
    console.error('Lỗi khi xuất Excel:', error);
    alert('Có lỗi xảy ra khi xuất file Excel. Vui lòng thử lại!');
  }
};

const closeDetailModal = () => {
  showDetailModal.value = false;
  selectedOrder.value = null;
  orderDetails.value = [];
};

// Utility functions
const formatCurrency = (amount) => {
  if (!amount) return '0';
  return new Intl.NumberFormat('vi-VN').format(amount);
};

const formatDateTime = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const getOrderTypeText = (type) => {
  return type === 'online' ? 'Trực tuyến' : 'Tại quầy';
};

// Thêm các hàm phân biệt màu sắc rõ ràng hơn cho trạng thái và loại hóa đơn
const getOrderTypeClass = (type) => {
  if (type === 'Trực tuyến') return 'badge bg-primary';
  if (type === 'Tại quầy') return 'badge bg-success';
  return 'badge bg-secondary';
};

const getStatusClass = (status) => {
  switch (status) {
    case 'Chờ xác nhận': return 'badge bg-warning text-dark';
    case 'Giao hàng': return 'badge bg-info';
    case 'Đang vận chuyển': return 'badge bg-primary';
    case 'Hoàn thành': return 'badge bg-success';
    case 'Hủy': return 'badge bg-danger';
    default: return 'badge bg-secondary';
  }
};

// Status edit functions
const canEditStatus = (currentStatus) => {
  // Chỉ cho phép chỉnh sửa những trạng thái có thể thay đổi
  const editableStatuses = ['Chờ xác nhận', 'Giao hàng', 'Đang vận chuyển'];
  return editableStatuses.includes(currentStatus);
};

const getAvailableStatuses = (currentStatus) => {
  const statusFlow = {
    'Chờ xác nhận': ['Giao hàng', 'Hủy'],
    'Giao hàng': ['Đang vận chuyển', 'Hủy'],
    'Đang vận chuyển': ['Hoàn thành', 'Hủy']
  };
  return statusFlow[currentStatus] || [];
};

const openStatusEditModal = (order) => {
  editingOrder.value = { ...order };
  selectedNewStatus.value = '';
  cancelReason.value = '';
  showStatusEditModal.value = true;
};

const closeStatusEditModal = () => {
  showStatusEditModal.value = false;
  editingOrder.value = null;
  selectedNewStatus.value = '';
  cancelReason.value = '';
};

const updateOrderStatus = async () => {
  if (!selectedNewStatus.value || !editingOrder.value) return;
  
  try {
    const updateData = {
      idHD: editingOrder.value.idHD,
      trangThai: selectedNewStatus.value,
      ghiChu: selectedNewStatus.value === 'Hủy' ? cancelReason.value : undefined
    };

    await axios.put(`${API_BASE_URL}/hoadon/updateStatus`, updateData);
    
    // Cập nhật danh sách
    await fetchOrders();
    
    // Đóng modal
    closeStatusEditModal();
    
    alert(`Đã cập nhật trạng thái thành: ${selectedNewStatus.value}`);
  } catch (error) {
    console.error('Error updating order status:', error);
    alert('Có lỗi xảy ra khi cập nhật trạng thái!');
  }
};

// Watchers
watch([orderType, selectedStatus, searchQuery, dateFrom, dateTo, currentPage], () => {
  fetchOrders();
});

// Lifecycle
onMounted(() => {
  fetchOrders();
});
</script>

<style scoped>
.nav-tabs .nav-link {
  border: none;
  color: #6c757d;
  font-weight: 500;
  padding: 0.75rem 1rem;
}

.nav-tabs .nav-link.active {
  color: #0d6efd;
  border-bottom: 3px solid #0d6efd;
  background: none;
}

.nav-tabs .nav-link:hover {
  border-color: transparent;
  color: #0d6efd;
}

.btn-check:checked + .btn-outline-secondary {
  background-color: #6c757d;
  border-color: #6c757d;
  color: white;
}

.table th {
  border-top: none;
  font-weight: 600;
  color: #495057;
}

.badge {
  font-family: 'Segoe UI', 'Arial', 'Tahoma', 'sans-serif';
  font-size: 0.95rem;
  letter-spacing: 0.01em;
}
.table {
  font-family: 'Segoe UI', 'Arial', 'Tahoma', 'sans-serif';
}

.pagination .page-link {
  color: #0d6efd;
  border-color: #dee2e6;
}

.pagination .page-item.active .page-link {
  background-color: #0d6efd;
  border-color: #0d6efd;
}

.pagination .page-item.disabled .page-link {
  color: #6c757d;
}
</style>