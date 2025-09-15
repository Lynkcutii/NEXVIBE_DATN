```vue
<template>
  <div class="mb-4">
    <h1 class="h3 mb-3 text-gray-800">Quản lý Nhân viên & Admin</h1>
    <div class="d-flex justify-content-between align-items-center mb-3">
      <div class="d-flex gap-3">
        <el-select v-model="filterStatus" placeholder="Lọc theo trạng thái" clearable style="width: 200px">
          <el-option label="Tất cả" :value="null"></el-option>
          <el-option label="Hoạt động" :value="true"></el-option>
          <el-option label="Không hoạt động" :value="false"></el-option>
        </el-select>
        <el-input v-model="searchKeyword" placeholder="Tìm kiếm theo tên hoặc số điện thoại" style="width: 300px" clearable></el-input>
      </div>
      <button class="btn btn-primary" @click="openAddStaffModal">
        <i class="fas fa-plus me-2"></i>Thêm nhân viên
      </button>
    </div>
    <div class="card">
      <div class="card-body">
        <table class="table table-hover align-middle">
          <thead>
            <tr>
              <th>STT</th>
              <th>Họ tên</th>
              <th>Số điện thoại</th>
              <th>Email</th>
              <th>Vai trò</th>
              <th>Trạng thái</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(account, index) in staffAndAdmins" :key="account.idNV">
              <th>{{ index + 1 }}</th>
              <td>{{ account.tenNV || 'Không xác định' }}</td>
              <td>{{ account.sdt || 'Không xác định' }}</td>
              <td>{{ account.email || 'Không xác định' }}</td>
              <td>
                <span class="badge" :class="getRoleClass(account.taiKhoan ? account.taiKhoan.chucVu : 'UNKNOWN')">
                  {{ account.taiKhoan && account.taiKhoan.chucVu ? (account.taiKhoan.chucVu === 'ADMIN' ? 'Admin' : 'Nhân viên') : 'Không xác định' }}
                </span>
              </td>
              <td>
                <span class="badge" :class="account.trangThai ? 'bg-success' : 'bg-danger'">
                  {{ account.trangThai ? 'Hoạt động' : 'Không hoạt động' }}
                </span>
              </td>
              <td>
                <button
                  class="btn btn-link text-primary p-1"
                  title="Xem và chỉnh sửa"
                  @click="openEditStaffModal(account)"
                >
                  <i class="fas fa-eye"></i>
                </button>
                <button
                  class="btn btn-link p-1 text-warning"
                  :disabled="account.idNV === loggedInUserId"
                  title="Chuyển trạng thái"
                  @click="toggleStatus(account.idNV)"
                >
                  <i class="fas fa-sync-alt"></i>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>

  <!-- Modal thêm nhân viên -->
  <el-dialog
    title="Thêm nhân viên mới"
    v-model="showAddStaffModal"
    width="30%"
    :before-close="handleCloseModal"
  >
    <el-form :model="newStaff" :rules="rules" ref="staffForm" label-position="top">
      <el-form-item label="Tên đăng nhập" prop="taiKhoan">
        <el-input v-model="newStaff.taiKhoan" placeholder="Nhập tên đăng nhập"></el-input>
      </el-form-item>
      <el-form-item label="Mật khẩu" prop="matKhau">
        <el-input v-model="newStaff.matKhau" type="password" placeholder="Nhập mật khẩu"></el-input>
      </el-form-item>
      <el-form-item label="Họ tên" prop="tenKH">
        <el-input v-model="newStaff.tenKH" placeholder="Nhập họ tên"></el-input>
      </el-form-item>
      <el-form-item label="Giới tính" prop="gioiTinh">
        <el-select v-model="newStaff.gioiTinh" placeholder="Chọn giới tính">
          <el-option label="Nam" value="Nam"></el-option>
          <el-option label="Nữ" value="Nữ"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="Ngày sinh" prop="ngaySinh">
        <el-date-picker
          v-model="newStaff.ngaySinh"
          type="date"
          placeholder="Chọn ngày sinh"
          format="DD/MM/YYYY"
          value-format="YYYY-MM-DD"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="Số điện thoại" prop="sdt">
        <el-input v-model="newStaff.sdt" placeholder="Nhập số điện thoại"></el-input>
      </el-form-item>
      <el-form-item label="Địa chỉ" prop="diaChi">
        <el-input v-model="newStaff.diaChi" placeholder="Nhập địa chỉ"></el-input>
      </el-form-item>
      <el-form-item label="Email" prop="email">
        <el-input v-model="newStaff.email" placeholder="Nhập email"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleCloseModal">Hủy</el-button>
        <el-button type="primary" @click="submitStaff">Thêm</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- Modal chỉnh sửa thông tin nhân viên -->
  <el-dialog
    title="Chỉnh sửa thông tin nhân viên"
    v-model="showEditStaffModal"
    width="30%"
    :before-close="handleCloseEditModal"
  >
    <el-form v-if="selectedStaff" :model="selectedStaff" :rules="editRules" ref="editStaffForm" label-position="top">
      <el-form-item label="Tên đăng nhập" prop="taiKhoan.taiKhoan">
        <el-input :value="selectedStaff.taiKhoan?.taiKhoan || 'Không xác định'" readonly></el-input>
      </el-form-item>
      <el-form-item label="Họ tên" prop="tenNV">
        <el-input v-model="selectedStaff.tenNV" placeholder="Nhập họ tên"></el-input>
      </el-form-item>
      <el-form-item label="Giới tính" prop="gioiTinh">
        <el-select v-model="selectedStaff.gioiTinh" placeholder="Chọn giới tính">
          <el-option label="Nam" value="Nam"></el-option>
          <el-option label="Nữ" value="Nữ"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="Ngày sinh" prop="ngaySinh">
        <el-date-picker
          v-model="selectedStaff.ngaySinh"
          type="date"
          placeholder="Chọn ngày sinh"
          format="DD/MM/YYYY"
          value-format="YYYY-MM-DD"
          :disabled-date="disableFutureDates"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="Số điện thoại" prop="sdt">
        <el-input v-model="selectedStaff.sdt" placeholder="Nhập số điện thoại"></el-input>
      </el-form-item>
      <el-form-item label="Email" prop="email">
        <el-input v-model="selectedStaff.email" placeholder="Nhập email"></el-input>
      </el-form-item>
      <el-form-item label="Địa chỉ" prop="diaChi">
        <el-input v-model="selectedStaff.diaChi" placeholder="Nhập địa chỉ"></el-input>
      </el-form-item>
    </el-form>
    <div v-else>
      <p>Không có dữ liệu nhân viên để hiển thị.</p>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleCloseEditModal">Hủy</el-button>
        <el-button type="primary" @click="saveStaff" :disabled="!selectedStaff">Lưu</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import axios from 'axios';
import { ElMessage, ElForm, ElMessageBox } from 'element-plus';

const loggedInUserId = ref(1); // Thay thế bằng logic lấy ID người dùng thực tế
const staffAndAdmins = ref([]);
const showAddStaffModal = ref(false);
const showEditStaffModal = ref(false);
const selectedStaff = ref(null);
const filterStatus = ref(null); // null: Tất cả, true: Hoạt động, false: Không hoạt động
const searchKeyword = ref('');
const newStaff = ref({
  taiKhoan: '',
  matKhau: '',
  tenKH: '',
  gioiTinh: '',
  ngaySinh: '',
  sdt: '',
  diaChi: '',
  email: ''
});
const staffForm = ref(null);
const editStaffForm = ref(null);

// Quy tắc xác thực form thêm nhân viên
const rules = {
  taiKhoan: [{ required: true, message: 'Vui lòng nhập tên đăng nhập', trigger: 'blur' }],
  matKhau: [
    { required: true, message: 'Vui lòng nhập mật khẩu', trigger: 'blur' },
    { min: 6, message: 'Mật khẩu phải có ít nhất 6 ký tự', trigger: 'blur' }
  ],
  tenKH: [{ required: true, message: 'Vui lòng nhập họ tên', trigger: 'blur' }],
  gioiTinh: [{ required: true, message: 'Vui lòng chọn giới tính', trigger: 'change' }],
  ngaySinh: [
    { required: true, message: 'Vui lòng chọn ngày sinh', trigger: 'change' },
    {
      validator: (rule, value, callback) => {
        if (!value || value === '') {
          callback(new Error('Ngày sinh không được để trống'));
        } else if (!/^\d{4}-\d{2}-\d{2}$/.test(value)) {
          callback(new Error('Ngày sinh không đúng định dạng (YYYY-MM-DD)'));
        } else {
          callback();
        }
      },
      trigger: 'change'
    }
  ],
  sdt: [
    { required: true, message: 'Vui lòng nhập số điện thoại', trigger: 'blur' },
    { pattern: /^[0-9]{10}$/, message: 'Số điện thoại phải có 10 chữ số', trigger: 'blur' }
  ],
  diaChi: [{ required: true, message: 'Vui lòng nhập địa chỉ', trigger: 'blur' }],
  email: [
    { required: true, message: 'Vui lòng nhập email', trigger: 'blur' },
    { type: 'email', message: 'Email không hợp lệ', trigger: 'blur' }
  ]
};

// Quy tắc xác thực form chỉnh sửa nhân viên
const editRules = {
  maNV: [{ required: true, message: 'Mã nhân viên không được để trống', trigger: 'blur' }],
  tenNV: [{ required: true, message: 'Vui lòng nhập họ tên', trigger: 'blur' }],
  gioiTinh: [{ required: true, message: 'Vui lòng chọn giới tính', trigger: 'change' }],
  ngaySinh: [
    { required: true, message: 'Vui lòng chọn ngày sinh', trigger: 'change' },
    {
      validator: (rule, value, callback) => {
        if (!value || value === '') {
          callback(new Error('Ngày sinh không được để trống'));
        } else if (!/^\d{4}-\d{2}-\d{2}$/.test(value)) {
          callback(new Error('Ngày sinh không đúng định dạng (YYYY-MM-DD)'));
        } else {
          callback();
        }
      },
      trigger: 'change'
    }
  ],
  sdt: [
    { required: true, message: 'Vui lòng nhập số điện thoại', trigger: 'blur' },
    { pattern: /^[0-9]{10}$/, message: 'Số điện thoại phải có 10 chữ số', trigger: 'blur' }
  ],
  diaChi: [{ required: true, message: 'Vui lòng nhập địa chỉ', trigger: 'blur' }],
  email: [
    { required: true, message: 'Vui lòng nhập email', trigger: 'blur' },
    { type: 'email', message: 'Email không hợp lệ', trigger: 'blur' }
  ],
  trangThai: [{ required: true, message: 'Vui lòng chọn trạng thái', trigger: 'change' }]
};

// Vô hiệu hóa các ngày trong tương lai
const disableFutureDates = (time) => {
  return time.getTime() > Date.now();
};

// Hàm gọi API để lấy danh sách nhân viên và admin
const fetchStaffAndAdmins = async () => {
  try {
    const params = {};
    if (filterStatus.value !== null) {
      params.trangThai = filterStatus.value;
    }
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value;
    }
    const response = await axios.get('http://localhost:8080/admin/api/nhanvien', { params });
    // Kiểm tra và xử lý dữ liệu trả về
    staffAndAdmins.value = response.data.map(item => ({
      ...item,
      maNV: item.maNV || 'Không xác định',
      tenNV: item.tenNV || 'Không xác định',
      sdt: item.sdt || 'Không xác định',
      email: item.email || 'Không xác định',
      diaChi: item.diaChi || 'Không xác định',
      gioiTinh: item.gioiTinh || 'Không xác định',
      ngaySinh: item.ngaySinh || '', // Đảm bảo ngaySinh không null
      trangThai: item.trangThai !== undefined ? item.trangThai : true,
      taiKhoan: item.taiKhoan || { taiKhoan: 'Không xác định', chucVu: 'UNKNOWN' }
    }));
  } catch (error) {
    console.error('Lỗi khi lấy danh sách nhân viên:', error);
    ElMessage.error('Không thể tải danh sách nhân viên.');
  }
};

// Hàm chuyển đổi trạng thái
const toggleStatus = async (id) => {
  try {
    await ElMessageBox.confirm(
      'Bạn có chắc chắn muốn chuyển trạng thái nhân viên này?',
      'Xác nhận',
      { confirmButtonText: 'Đồng ý', cancelButtonText: 'Hủy', type: 'warning' }
    );
    await axios.put(`http://localhost:8080/admin/api/nhanvien/${id}/toggle-status`);
    ElMessage.success('Chuyển trạng thái thành công!');
    fetchStaffAndAdmins();
  } catch (error) {
    if (error === 'cancel') return;
    console.error('Lỗi khi chuyển trạng thái:', error);
    ElMessage.error('Lỗi khi chuyển trạng thái nhân viên.');
  }
};

// Hàm xác định lớp CSS cho vai trò
const getRoleClass = (role) => {
  if (role === 'ADMIN') return 'bg-primary';
  if (role === 'NHAN_VIEN') return 'bg-info text-dark';
  return 'bg-secondary';
};

// Mở modal thêm nhân viên
const openAddStaffModal = () => {
  showAddStaffModal.value = true;
};

// Đóng modal thêm nhân viên và reset form
const handleCloseModal = () => {
  showAddStaffModal.value = false;
  staffForm.value?.resetFields();
  newStaff.value = {
    taiKhoan: '',
    matKhau: '',
    tenKH: '',
    gioiTinh: '',
    ngaySinh: '',
    sdt: '',
    diaChi: '',
    email: ''
  };
};

// Mở modal chỉnh sửa thông tin nhân viên
const openEditStaffModal = (staff) => {
  if (!staff || !staff.idNV) {
    ElMessage.error('Không thể mở thông tin nhân viên: Dữ liệu không hợp lệ.');
    return;
  }
  console.log('Dữ liệu nhân viên:', JSON.stringify(staff, null, 2));
  selectedStaff.value = JSON.parse(JSON.stringify({
    ...staff,
    maNV: staff.maNV || 'Không xác định',
    tenNV: staff.tenNV || 'Không xác định',
    sdt: staff.sdt || 'Không xác định',
    email: staff.email || 'Không xác định',
    diaChi: staff.diaChi || 'Không xác định',
    gioiTinh: staff.gioiTinh || 'Không xác định',
    ngaySinh: staff.ngaySinh || '', // Đảm bảo ngaySinh có giá trị mặc định
    trangThai: staff.trangThai !== undefined ? staff.trangThai : true,
    taiKhoan: staff.taiKhoan || { taiKhoan: 'Không xác định', chucVu: 'UNKNOWN' }
  }));
  showEditStaffModal.value = true;
};

// Đóng modal chỉnh sửa thông tin nhân viên
const handleCloseEditModal = () => {
  showEditStaffModal.value = false;
  selectedStaff.value = null;
  editStaffForm.value?.resetFields();
};

// Lưu thông tin nhân viên đã chỉnh sửa
const saveStaff = async () => {
  if (!selectedStaff.value || !selectedStaff.value.idNV) {
    ElMessage.error('Không thể lưu: Dữ liệu nhân viên không hợp lệ.');
    return;
  }
  try {
    await ElMessageBox.confirm(
      'Bạn có chắc chắn muốn lưu thay đổi?',
      'Xác nhận',
      { confirmButtonText: 'Đồng ý', cancelButtonText: 'Hủy', type: 'warning' }
    );
    await editStaffForm.value.validate(async (valid) => {
      if (valid) {
        console.log('Payload gửi đi:', JSON.stringify(selectedStaff.value, null, 2));
        if (!selectedStaff.value.ngaySinh) {
          ElMessage.error('Ngày sinh không được để trống.');
          return;
        }
        await axios.put(`http://localhost:8080/admin/api/nhanvien/${selectedStaff.value.idNV}`, selectedStaff.value);
        ElMessage.success('Cập nhật thông tin nhân viên thành công!');
        showEditStaffModal.value = false;
        fetchStaffAndAdmins();
        handleCloseEditModal();
      }
    });
  } catch (error) {
    if (error === 'cancel') return;
    console.error('Lỗi khi cập nhật nhân viên:', error);
    const errorMessage = error.response?.data?.message || 'Lỗi khi cập nhật nhân viên. Vui lòng kiểm tra lại.';
    ElMessage.error(errorMessage);
  }
};

// Gửi yêu cầu thêm nhân viên
const submitStaff = async () => {
  try {
    await staffForm.value.validate(async (valid) => {
      if (valid) {
        await axios.post('http://localhost:8080/admin/api/nhanvien/registerAdmin', newStaff.value);
        ElMessage.success('Thêm nhân viên thành công!');
        showAddStaffModal.value = false;
        fetchStaffAndAdmins();
        handleCloseModal();
      }
    });
  } catch (error) {
    console.error('Lỗi khi thêm nhân viên:', error);
    ElMessage.error(error.response?.data?.message || 'Lỗi khi thêm nhân viên.');
  }
};

// Theo dõi thay đổi bộ lọc và tìm kiếm
watch([filterStatus, searchKeyword], () => {
  fetchStaffAndAdmins();
});

// Gọi API khi component được mount
onMounted(() => {
  fetchStaffAndAdmins();
});
</script>
```