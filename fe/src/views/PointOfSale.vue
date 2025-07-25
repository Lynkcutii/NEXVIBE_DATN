<template>
  <div class="ban-hang-tai-quay container-fluid">
    <div class="card">
      <div class="card-body">
        <!-- Header -->
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h2 class="mb-0">Bán hàng</h2>
          <button
            type="button"
            class="btn btn-primary animated-btn"
            :disabled="hoaDons.length >= 5"
            @click="taoHoaDon"
          >
            <i class="bi bi-plus"></i> Tạo đơn hàng
          </button>
        </div>

        <!-- No data state -->
        <div v-if="hoaDons.length === 0" class="text-center py-5 bg-light rounded">
          <p class="mb-0">No Data Found</p>
        </div>

        <!-- Tabs for invoices -->
        <div v-else>
          <ul class="nav nav-tabs mb-3" role="tablist">
            <li class="nav-item" v-for="(hd, idx) in hoaDons" :key="hd.maHoaDon">
              <a
                class="nav-link animated-tab"
                :class="{ active: tabActive === hd.maHoaDon }"
                :id="`tab-${hd.maHoaDon}`"
                data-bs-toggle="tab"
                :href="`#pane-${hd.maHoaDon}`"
                role="tab"
                @click="tabActive = hd.maHoaDon"
              >
                Đơn hàng {{ idx + 1 }} - {{ hd.maHoaDon }}
              </a>
            </li>
          </ul>

          <div class="tab-content">
            <div
              v-for="hd in hoaDons"
              :key="hd.maHoaDon"
              :id="`pane-${hd.maHoaDon}`"
              class="tab-pane fade"
              :class="{ show: tabActive === hd.maHoaDon, active: tabActive === hd.maHoaDon }"
              role="tabpanel"
            >
              <div class="row">
                <!-- Left side - Product actions and list -->
                <div class=" mb-4 mb-lg3-0">
                  <div class="card h-100">
                    <div class="card-body">
                      <div class="d-flex gap-2 mb-3">
                        <button
                          type="button"
                          class="btn btn-success btn-sm animated-btn"
                          @click="showChonSanPham(hd)"
                        >
                          <i class="bi bi-plus-circle"></i> Thêm sản phẩm
                        </button>
                        <button
                          type="button"
                          class="btn btn-info btn-sm animated-btn"
                          @click="showChonSanPham(hd, true)"
                        >
                          <i class="bi bi-qr-code"></i> Quét QR sản phẩm
                        </button>
                        <button
                          type="button"
                          class="btn btn-danger btn-sm animated-btn"
                          @click="xoaHoaDon(hd.maHoaDon)"
                        >
                          <i class="bi bi-trash"></i> Xóa hóa đơn
                        </button>
                      </div>

                      <!-- Product section -->
                      <h5 class="card-title">Sản phẩm</h5>
                      <div class="product-list" style="max-height: 400px; overflow-y: auto;">
                        <div v-if="hd.sanPhams.length === 0" class="text-center py-5 bg-light rounded">
                          <p class="mb-0">Chưa có sản phẩm nào</p>
                        </div>
                        <el-table :data="hd.sanPhams" style="margin-top: 10px; border-radius: 10px; overflow: hidden;">
                          <el-table-column label="Ảnh" width="80">
                            <template #default="scope">
                              <img :src="scope.row.anhGiay" alt="Ảnh" style="width: 60px; height: 60px; object-fit: cover; border-radius: 8px; border: 1px solid #eee;" />
                            </template>
                          </el-table-column>
                          <el-table-column label="Thông tin sản phẩm">
                            <template #default="scope">
                              <div style="font-weight: 600; color: #1976d2;">{{ scope.row.tenGiay }}</div>
                              <div style="font-size: 13px; color: #888;">Mã: {{ scope.row.maCtSanPham }} | Size: {{ scope.row.tenKichThuoc }} | Màu: {{ scope.row.tenMauSac }}</div>
                              <div style="font-size: 13px; color: #888;">Thương hiệu: {{ scope.row.tenThuongHieu }} | Chất liệu: {{ scope.row.tenChatLieu }}</div>
                            </template>
                          </el-table-column>
                          <el-table-column label="Giá bán">
                            <template #default="scope">
                              <span style="font-weight: bold; color: #e53935;">{{ scope.row.giaBan.toLocaleString() }} VND</span>
                            </template>
                          </el-table-column>
                          <el-table-column label="Số lượng">
                            <template #default="scope">
                              <el-input-number
                                v-model="scope.row.soLuong"
                                :min="1"
                                :max="scope.row.soLuongTonKho || 1000"
                                size="small"
                                @change="onChangeSoLuong(scope.row, hd)"
                              />
                              <span v-if="scope.row.soLuong > scope.row.soLuongTonKho" style="color: red; font-size: 12px;">Vượt quá tồn kho!</span>
                            </template>
                          </el-table-column>
                          <el-table-column label="Thành tiền">
                            <template #default="scope">
                              <span style="font-weight: 600; color: #1976d2;">{{ (scope.row.giaBan * scope.row.soLuong).toLocaleString() }} VND</span>
                            </template>
                          </el-table-column>
                          <el-table-column label="Xóa" width="80">
                            <template #default="scope">
                              <button
                                type="button"
                                class="btn btn-danger btn-sm animated-btn"
                                @click="xoaSanPham(hd, scope.row)"
                              >
                                <i class="bi bi-trash"></i>
                              </button>
                            </template>
                          </el-table-column>
                        </el-table>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Right side - Customer info and payment -->
                <div class="col-lg-4">
                  <div class="card h-100">
                    <div class="card-body">
                      <!-- Customer section -->
                      <h5 class="card-title">
                        <i class="bi bi-person-circle me-2 text-primary"></i>
                        Thông tin khách hàng
                      </h5>
                      <div class="mb-3">
                        <label class="form-label">Tên khách hàng</label>
                        <div class="input-group">
                          <input
                            v-model="hd.khachHang"
                            type="text"
                            class="form-control"
                            placeholder="Khách lẻ"
                            readonly
                          />
                          <button
                            type="button"
                            class="btn btn-primary btn-sm animated-btn"
                            @click="showCustomerModal(hd)"
                          >
                            <i class="bi bi-person-plus me-1"></i>
                            Chọn khách hàng
                          </button>
                        </div>
                      </div>
                      <!-- Enhanced delivery toggle -->
                      <div class="delivery-section mb-3">
                        <div class="d-flex justify-content-between align-items-center p-3 bg-light rounded">
                          <div class="d-flex align-items-center">
                            <i class="bi bi-truck me-3 text-info"></i>
                            <label class="form-label mb-0 fw-semibold">Giao hàng</label>
                          </div>
                          <div class="form-check form-switch">
                            <input 
                              class="form-check-input delivery-switch" 
                              type="checkbox" 
                              role="switch" 
                              id="deliverySwitch"
                              v-model="hd.giaoHang"
                              @change="toggleDeliveryInfo(hd)"
                            >
                          </div>
                        </div>
                      </div> 
                      <!-- Customer delivery info - Show when delivery is enabled -->
                      <div v-if="hd.giaoHang" class="delivery-info mt-3 p-3 border border-info rounded bg-info bg-opacity-10">
                        <h6 class="text-info fw-semibold mb-3">
                          <i class="bi bi-geo-alt-fill me-2"></i>
                          Thông tin giao hàng
                        </h6>
                        
                        <div v-if="hd.customerInfo" class="customer-details">
                          <div class="row g-2 mb-3">
                            <div class="col-12">
                              <div class="form-group">
                                <label class="form-label text-info fw-semibold">Họ tên</label>
                                <input
                                  v-model="hd.customerInfo.hoTen"
                                  type="text"
                                  class="form-control"
                                  placeholder="Nhập họ tên"
                                  :disabled="!hd.customerInfo"
                                />
                              </div>
                            </div>
                            <div class="col-12">
                              <div class="form-group">
                                <label class="form-label text-info fw-semibold">Số điện thoại</label>
                                <input
                                  v-model="hd.customerInfo.sdt"
                                  type="text"
                                  class="form-control"
                                  placeholder="Nhập số điện thoại"
                                  :disabled="!hd.customerInfo"
                                />
                              </div>
                            </div>
                          </div>
                          
                          <!-- Delivery address selection -->
                          <div class="mb-3">
                            <label class="form-label text-info fw-semibold">
                              <i class="bi bi-pin-map me-1"></i>
                              Địa chỉ giao hàng
                            </label>
                            <select
                              v-model="hd.customerInfo.selectedDiaChi"
                              class="form-control"
                              @change="updateDiaChi(hd)"
                              :disabled="!hd.customerInfo || diaChiList.length === 0"
                            >
                              <option :value="null" disabled>Chọn địa chỉ</option>
                              <option v-for="diaChi in diaChiList" :key="diaChi.id" :value="diaChi">
                                {{ diaChi.tenDiaChi }} ({{ diaChi.maDiaChi }})
                              </option>
                              <option :value="{ id: null, tenDiaChi: '' }">Nhập địa chỉ mới</option>
                            </select>
                          </div>
                          
                          <!-- Delivery address input -->
                          <div class="mb-3" v-if="hd.customerInfo.selectedDiaChi && !hd.customerInfo.selectedDiaChi.id">
                            <label class="form-label text-info fw-semibold">
                              <i class="bi bi-pin-map me-1"></i>
                              Nhập địa chỉ mới
                            </label>
                            <textarea
                              v-model="hd.customerInfo.diaChiKhachHangs"
                              class="form-control"
                              rows="2"
                              placeholder="Nhập địa chỉ giao hàng"
                            ></textarea>
                          </div>
                          <div class="mb-3" v-else-if="hd.customerInfo.selectedDiaChi">
                            <label class="form-label text-info fw-semibold">
                              <i class="bi bi-pin-map me-1"></i>
                              Địa chỉ đã chọn
                            </label>
                            <input
                              v-model="hd.customerInfo.diaChiKhachHangs"
                              class="form-control"
                              readonly
                            />
                          </div>
                          
                          <!-- Delivery note -->
                          <div class="mb-3">
                            <label class="form-label text-info fw-semibold">
                              <i class="bi bi-chat-text me-1"></i>
                              Ghi chú giao hàng
                            </label>
                            <textarea
                              v-model="hd.ghiChuGiaoHang"
                              class="form-control"
                              rows="2"
                              placeholder="Ghi chú đặc biệt cho shipper..."
                            ></textarea>
                          </div>
                        </div>
                        
                        <!-- When no customer selected -->
                        <div v-else class="text-center py-3">
                          <i class="bi bi-exclamation-triangle text-warning fs-1"></i>
                          <p class="text-muted mt-2 mb-0">
                            Vui lòng chọn khách hàng để hiển thị thông tin giao hàng
                          </p>
                          <button
                            type="button"
                            class="btn btn-outline-primary btn-sm mt-2"
                            @click="showCustomerModal(hd)"
                          >
                            <i class="bi bi-person-plus me-1"></i>
                            Chọn khách hàng ngay
                          </button>
                        </div>
                      </div>
                      <hr> 

                      <!-- Voucher section -->
                      <div class="mb-3">
                        <label class="form-label">Voucher</label>
                        <el-select v-model="hd.selectedVoucherId" placeholder="Chọn voucher" filterable style="width: 100%" @change="chonVoucher(hd)">
                          <el-option 
                            v-for="v in getValidVouchers(hd)" 
                            :key="v.id" 
                            :label="`${v.tenVoucher} - ${v.hinhThucGiam === 'Phần trăm' ? v.mucGiam + '%' : v.mucGiam.toLocaleString() + ' VND'}`" 
                            :value="v.id"
                          >
                            <div>
                              <div class="fw-bold">{{ v.tenVoucher }}</div>
                              <div class="text-success">
                                <i class="bi bi-tag-fill me-1"></i>
                                {{ v.hinhThucGiam === 'Phần trăm' ? v.mucGiam + '%' : v.mucGiam.toLocaleString() + ' VND' }}
                              </div>
                              <small class="text-muted">
                                <i class="bi bi-currency-dollar me-1"></i>
                                Tối thiểu: {{ v.giaTriDonHangToiThieu.toLocaleString() }} VND
                              </small>
                            </div>
                          </el-option>
                        </el-select>
                        <div v-if="hd.selectedVoucher" class="mt-2 p-2 bg-light rounded">
                          <div class="d-flex justify-content-between">
                            <span class="fw-bold">{{ hd.selectedVoucher.tenVoucher }}</span>
                            <span class="text-success">
                              {{ hd.selectedVoucher.hinhThucGiam === 'Phần trăm' ? hd.selectedVoucher.mucGiam + '%' : hd.selectedVoucher.mucGiam.toLocaleString() + ' VND' }}
                            </span>
                          </div>
                          <small class="text-muted">
                            Điều kiện: Tổng tiền hàng ≥ {{ hd.selectedVoucher.giaTriDonHangToiThieu.toLocaleString() }} VND
                          </small>
                        </div>
                        <div v-if="getValidVouchers(hd).length === 0" class="mt-2 text-muted small">
                          <div class="alert alert-info py-2">
                            <i class="bi bi-info-circle me-1"></i>
                            Không có voucher phù hợp với tổng tiền hàng hiện tại ({{ tinhTienHang(hd).toLocaleString() }} VND)
                          </div>
                        </div>
                      </div>

                      <!-- Payment summary -->
                      <h5 class="card-title">Tổng kết thanh toán</h5>
                      <div class="bg-light p-3 rounded mb-3">
                        <div class="d-flex justify-content-between">
                          <span class="fw-bold">Tổng số tiền</span>
                          <span class="fw-bold text-danger">{{ tinhTongTien(hd).toLocaleString() }} VND</span>
                        </div>
                      </div>

                      <div class="mb-3">
                        <div class="d-flex justify-content-between mb-2">
                          <span>Tổng tiền hàng</span>
                          <span>{{ tinhTienHang(hd).toLocaleString() }} VND</span>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                          <span>Phí vận chuyển</span>
                          <span>{{ tinhPhiVanChuyen(hd).toLocaleString() }} VND</span>
                        </div>
                        <div v-if="hd.selectedVoucher" class="d-flex justify-content-between mb-2">
                          <span>
                            Giảm giá 
                            <span class="badge bg-success ms-1">{{ hd.selectedVoucher.tenVoucher }}</span>
                          </span>
                          <span class="text-success">-{{ tinhGiamGia(hd).toLocaleString() }} VND</span>
                        </div>
                        <div v-else class="d-flex justify-content-between mb-2">
                          <span>Giảm giá</span>
                          <span>0 VND</span>
                        </div>
                        <hr class="my-2">
                        <div class="d-flex justify-content-between mb-2">
                          <span class="fw-bold">Tổng cộng</span>
                          <span class="fw-bold text-danger">{{ tinhTongTien(hd).toLocaleString() }} VND</span>
                        </div>
                      </div>

                      <div class="mb-3">
                        <div class="d-flex justify-content-between mb-2">
                          <button
                            type="button"
                            class="btn btn-warning btn-sm animated-btn"
                            :disabled="hd.sanPhams.length === 0"
                            @click="openThanhToan(hd)"
                          >
                            Phương thức thanh toán
                          </button>
                          <span v-if="hd.thanhToan" class="text-primary">
                            {{ hd.thanhToan.soTien.toLocaleString() }} VND
                            <i :class="hd.thanhToan.phuongThuc === 'chuyen_khoan' ? 'bi bi-bank' : 'bi bi-cash'" class="ms-2"></i>
                            <span v-if="hd.thanhToan.maGiaoDich" class="text-muted ms-1">({{ hd.thanhToan.maGiaoDich }})</span>
                          </span>
                          <span v-else>0 VND</span>
                        </div>
                      </div>

                      <button
                        type="button"
                        class="btn btn-success w-100 animated-btn"
                        :disabled="!hd.thanhToan || hd.thanhToan.soTien < tinhTongTien(hd)"
                        @click="xacNhanDatHang(hd)"
                      >
                        Xác nhận đặt hàng & In hóa đơn
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Chon San Pham -->
    <el-dialog
      v-model="productSearchModal"
      title="Tìm kiếm sản phẩm"
      width="80%"
      top="5vh"
      class="product-search-dialog"
    >
      <div class="dialog-content-wrapper">
        <div class="search-bar-wrapper">
          <el-input
            v-model="productSearchQuery"
            placeholder="Tìm kiếm theo tên hoặc mã sản phẩm..."
            @input="debouncedSearchProducts"
            clearable
          >
            <template #prepend>
              <i class="bi bi-search"></i>
            </template>
          </el-input>
        </div>

        <div class="table-wrapper" v-loading="productLoading">
          <el-table
            :data="listSanPham.content"
            style="width: 100%"
            height="100%"
            @row-click="chonSanPham"
            class="product-table"
          >
            <el-table-column label="Ảnh" width="80">
              <template #default="{ row }">
                <img :src="row.anhGiay" alt="Ảnh sản phẩm" class="product-image" />
              </template>
            </el-table-column>
            <el-table-column prop="tenSP" label="Tên sản phẩm" width="180"></el-table-column>
            <el-table-column prop="maSPCT" label="Mã SP" width="120"></el-table-column>
            <el-table-column prop="tenDanhMuc" label="Danh mục"></el-table-column>
            <el-table-column prop="tenThuongHieu" label="Thương hiệu"></el-table-column>
            <el-table-column prop="tenMauSac" label="Màu sắc"></el-table-column>
            <el-table-column prop="tenChatLieu" label="Chất liệu"></el-table-column>
            <el-table-column prop="tenKichThuoc" label="Size"></el-table-column>
            <el-table-column label="Giá">
              <template #default="{ row }">
                {{ row.giaBan.toLocaleString() }} VND
              </template>
            </el-table-column>
            <el-table-column label="Thao tác" width="100" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click.stop="chonSanPham(row)">Chọn</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div class="pagination-wrapper">
          <el-pagination
            background
            layout="prev, pager, next, total"
            :current-page="listSanPham.number + 1"
            :page-size="listSanPham.size"
            :total="listSanPham.totalElements"
            @current-change="onProductPageChange"
          />
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="productSearchModal = false">Đóng</el-button>
          <el-button type="primary" @click="taiLaiDanhSachSanPham">
            <i class="bi bi-arrow-clockwise"></i> Tải lại
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- Modal hóa đơn -->
    <el-dialog :visible.sync="modalHoaDon" width="600px" title="Hóa đơn bán hàng">
      <div v-if="hoaDonIn">
        <div style="text-align:center;">
          <h3>F-Shoes</h3>
          <div>SĐT: 0123456789</div>
          <div>Email: fshoesweb@gmail.com</div>
          <div>Địa chỉ: FPT POLYTECHNIC Cơ Sở Trịnh Văn Bô, Nam Từ Li, Hà Nội</div>
        </div>
        <h4 style="text-align:center;">HÓA ĐƠN BÁN HÀNG</h4>
        <div>Mã hóa đơn: {{ hoaDonIn.maHoaDon }}</div>
        <div>Ngày tạo: {{ hoaDonIn.ngayTao }}</div>
        <div>Khách hàng: {{ hoaDonIn.khachHang || 'khách lẻ' }}</div>
        <el-table :data="hoaDonIn.sanPhams" style="margin-top: 10px;">
          <el-table-column prop="tenGiay" label="Tên sản phẩm"></el-table-column>
          <el-table-column prop="soLuong" label="Số lượng"></el-table-column>
          <el-table-column prop="giaBan" label="Đơn giá">
            <template #default="scope">{{ scope.row.giaBan.toLocaleString() }} VND</template>
          </el-table-column>
          <el-table-column label="Thành tiền">
            <template #default="scope">{{ (scope.row.giaBan * scope.row.soLuong).toLocaleString() }} VND</template>
          </el-table-column>
        </el-table>
        <div style="margin-top:10px;">Tổng tiền hàng: {{ tinhTienHang(hoaDonIn).toLocaleString() }} VND</div>
        <div>Phí vận chuyển: {{ tinhPhiVanChuyen(hoaDonIn).toLocaleString() }} VND</div>
        <div>Giảm giá: {{ tinhGiamGia(hoaDonIn).toLocaleString() }} VND</div>
        <div><b>Tổng tiền cần thanh toán: {{ tinhTongTien(hoaDonIn).toLocaleString() }} VND</b></div>
        <div style="margin-top: 15px; padding: 10px; background-color: #e8f5e8; border-radius: 5px; text-align: center; color: #2d5a2d;">
          <i class="bi bi-check-circle-fill me-2"></i>
          Hóa đơn đã được tạo thành công! Màn hình sẽ được reset sau 2 giây.
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="modalHoaDon = false">Đóng</el-button>
        <el-button type="success" @click="taoHoaDonMoiNgay">Tạo hóa đơn mới</el-button>
        <el-button type="primary" @click="inHoaDon">In hóa đơn</el-button>
      </span>
    </el-dialog>

    <!-- Modal thanh toán -->
    <div v-if="modalThanhToan" class="modal fade show d-block" tabindex="-1" style="background: rgba(0,0,0,0.6); display: flex; align-items: center; justify-content: center;">
      <div class="modal-dialog modal-lg">
        <div class="modal-content" style="height: 550px; width: 500px;">
          <div class="modal-header bg-warning text-white">
            <h5 class="modal-title">THANH TOÁN</h5>
            <button type="button" class="btn-close" @click="modalThanhToan = false"></button>
          </div>
          <div class="modal-body text-center">
            <div class="mb-3">
              <h6>Tổng tiền cần thanh toán</h6>
              <p class="fw-bold text-danger">{{ tinhTongTien(thanhToanHoaDon).toLocaleString() }} VND</p>
            </div>
            <div class="d-flex justify-content-center gap-2 mb-3">
              <button
                type="button"
                class="btn btn-outline-danger animated-btn"
                :class="{ 'active': thanhToanPhuongThuc === 'chuyen_khoan' }"
                @click="thanhToanPhuongThuc = 'chuyen_khoan'"
              >
                Chuyển khoản
              </button>
              <button
                type="button"
                class="btn btn-outline-success animated-btn"
                :class="{ 'active': thanhToanPhuongThuc === 'tien_mat' }"
                @click="thanhToanPhuongThuc = 'tien_mat'"
              >
                Tiền mặt
              </button>
            </div>
            <div class="mb-3">
              <div class="row" v-if="thanhToanPhuongThuc === 'tien_mat'">
                <div class="col-12">
                  <div class="form-floating mb-2">
                    <input
                      v-model.number="thanhToanTienKhachDua"
                      type="number"
                      class="form-control"
                      id="floatingCash"
                      placeholder="Tiền khách đưa"
                    />
                    <label for="floatingCash">Tiền khách đưa</label>
                  </div>
                </div>
              </div>
              <div class="row" v-if="thanhToanPhuongThuc === 'chuyen_khoan'">
                <div class="col-6">
                  <div class="form-floating mb-2">
                    <input
                      v-model.number="thanhToanTienKhachDua"
                      type="number"
                      class="form-control"
                      id="floatingCashTransfer"
                      placeholder="Tiền khách đưa"
                    />
                    <label for="floatingCashTransfer">Tiền khách đưa</label>
                  </div>
                </div>
                <div class="col-6">
                  <div class="form-floating mb-2">
                    <input
                      v-model="thanhToanMaGiaoDich"
                      type="text"
                      class="form-control"
                      id="floatingTransaction"
                      placeholder="Mã giao dịch"
                    />
                    <label for="floatingTransaction">Mã giao dịch</label>
                  </div>
                </div>
              </div>
              <div class="text-end" v-if="thanhToanTienKhachDua">
                <p class="mb-0">Tiền thiếu: {{ (tinhTongTien(thanhToanHoaDon) - tongTienDaThanhToan).toLocaleString() }} VND</p>
              </div>
            </div>
            <el-table :data="thanhToanLichSu" style="margin-bottom: 10px;">
              <el-table-column label="STT" type="index" width="50" />
              <el-table-column prop="maGiaoDich" label="Mã giao dịch" />
              <el-table-column prop="phuongThuc" label="Phương thức" />
              <el-table-column prop="soTien" label="Số tiền" />
              <el-table-column label="Hành động" width="80">
                <template #default="scope">
                  <button
                    type="button"
                    class="btn btn-danger btn-sm animated-btn"
                    @click="xoaThanhToan(scope.$index)"
                  >
                    <i class="bi bi-trash"></i>
                  </button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="modalThanhToan = false">Đóng</button>
            <button
              type="button"
              class="btn btn-success animated-btn"
              :disabled="(tinhTongTien(thanhToanHoaDon) - tongTienDaThanhToan > 0) && (thanhToanTienKhachDua < tinhTongTien(thanhToanHoaDon))"
              @click="xacNhanThanhToan"
            >
              Xác nhận
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal chọn khách hàng -->
    <div v-if="modalCustomer" class="modal fade show d-block" tabindex="-1" 
         style="background: rgba(0,0,0,0.6); position: fixed; top: 0; left: 0; width: 100%; height: 100%; z-index: 1050; display: flex; align-items: center; justify-content: center;">
      <div class="modal-dialog" style="margin: 0; max-width: 1000px; width: 100%;">
        <div class="modal-content" style="max-height: 90vh; overflow: hidden; display: flex; flex-direction: column;">
          <div class="modal-header bg-info text-white">
            <h5 class="modal-title">Chọn khách hàng</h5>
            <button type="button" class="btn-close btn-close-white" @click="modalCustomer = false"></button>
          </div>
          <div class="modal-body p-4" style="flex: 1; overflow: hidden; display: flex; flex-direction: column;">
            <div class="input-group mb-4">
              <input
                v-model="customerSearch"
                type="text"
                class="form-control shadow-sm"
                placeholder="Tìm kiếm theo email hoặc họ tên..."
                @input="debounceSearchCustomers"
              />
              <button class="btn btn-outline-secondary" type="button" @click="searchCustomers">
                <i class="bi bi-search"></i>
              </button>
            </div>
            <div class="customer-list" style="flex: 1; overflow-y: auto; max-height: 400px;">
              <div v-if="loadingCustomers" class="text-center py-3">
                <i class="bi bi-hourglass-split fs-1 text-primary"></i>
                <p>Đang tải danh sách khách hàng...</p>
              </div>
              <div v-else-if="filteredCustomers.length === 0" class="text-center py-3">
                <i class="bi bi-exclamation-triangle fs-1 text-warning"></i>
                <p>Không tìm thấy khách hàng</p>
              </div>
              <table v-else class="table table-striped table-hover custom-table">
                <thead class="table-light sticky-top">
                  <tr>
                    <th class="text-center">STT</th>
                    <th>Email</th>
                    <th>Họ tên</th>
                    <th>Ngày sinh</th>
                    <th>Số điện thoại</th>
                    <th>Giới tính</th>
                    <th>Trạng thái</th>
                    <th class="text-center">Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(customer, index) in filteredCustomers" :key="customer.id" class="table-row">
                    <td class="text-center">{{ (currentPageCustomer - 1) * pageSizeCustomer + index + 1 }}</td>
                    <td>{{ customer.email }}</td>
                    <td>{{ customer.hoTen }}</td>
                    <td>{{ customer.ngaySinh }}</td>
                    <td>{{ customer.sdt }}</td>
                    <td>{{ customer.gioiTinh ? 'Nam' : 'Nữ' }}</td>
                    <td>
                      <span v-if="customer.trangThai" class="badge bg-success rounded-pill">Hoạt động</span>
                      <span v-else class="badge bg-secondary rounded-pill">Ngừng hoạt động</span>
                    </td>
                    <td class="text-center">
                      <button
                        class="btn btn-primary btn-sm rounded-pill"
                        @click="selectCustomer(customer, selectedHoaDon)"
                      >
                        CHỌN
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <!-- Phân trang -->
            <div class="pagination-section mt-3" v-if="!loadingCustomers && totalCustomers > 0">
              <span>Trang {{ currentPageCustomer }} / {{ Math.ceil(totalCustomers / pageSizeCustomer) }}</span>
              <button
                @click="fetchCustomers(currentPageCustomer - 2)"
                :disabled="currentPageCustomer <= 1"
                class="btn btn-sm btn-outline-secondary me-2"
              >
                Trước
              </button>
              <button
                @click="fetchCustomers(currentPageCustomer)"
                :disabled="currentPageCustomer >= Math.ceil(totalCustomers / pageSizeCustomer)"
                class="btn btn-sm btn-outline-secondary"
              >
                Sau
              </button>
              <span>Tổng: <strong>{{ totalCustomers }}</strong> khách hàng</span>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="modalCustomer = false">Đóng</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { ElMessage } from 'element-plus';
import _ from 'lodash';

export default {
  name: 'BanHangTaiQuay',
  data() {
    return {
      hoaDons: [],
      tabActive: '',
      modalSanPham: false,
      sanPhamList: [],
      totalSanPham: 0,
      pageSanPham: 1,
      pageSizeSanPham: 10,
      searchSanPham: '',
      hoaDonChon: null,
      vouchers: [],
      modalHoaDon: false,
      hoaDonIn: null,
      modalThanhToan: false,
      thanhToanHoaDon: null,
      thanhToanTongTien: 0,
      thanhToanPhuongThuc: 'chuyen_khoan',
      thanhToanTienKhachDua: 0,
      thanhToanMaGiaoDich: '',
      thanhToanLichSu: [],
      modalCustomer: false,
      customerSearch: '',
      selectedHoaDon: null,
      customers: [],
      loadingCustomers: false,
      currentPageCustomer: 1,
      pageSizeCustomer: 10,
      totalCustomers: 0,
      productSearchModal: false,
      productSearchQuery: '',
      productLoading: false,
      listSanPham: { content: [], number: 0, size: 10, totalElements: 0 },
      diaChiList: [],
    };
  },
  created() {
    this.fetchVouchers();
  },
  computed: {
    tongTienDaThanhToan() {
      return this.thanhToanLichSu.reduce((sum, t) => sum + (t.soTien || 0), 0);
    },
    filteredCustomers() {
      return this.customers;
    },
    voucherInfo() {
      return (hd) => {
        if (!hd.selectedVoucher) return null;
        return {
          name: hd.selectedVoucher.tenVoucher,
          discount: hd.selectedVoucher.hinhThucGiam === 'Phần trăm' 
            ? `${hd.selectedVoucher.mucGiam}%` 
            : `${hd.selectedVoucher.mucGiam.toLocaleString()} VND`,
          minAmount: hd.selectedVoucher.giaTriDonHangToiThieu.toLocaleString(),
          currentAmount: this.tinhTienHang(hd).toLocaleString(),
          isValid: this.getValidVouchers(hd).some(v => v.id === hd.selectedVoucherId)
        };
      };
    }
  },
  methods: {
    taoHoaDon() {
      const ma = 'HD' + (Math.floor(Math.random() * 9000) + 1000);
      const hd = {
        maHoaDon: ma,
        sanPhams: [],
        khachHang: '',
        idKhachHang: null,
        voucherCode: '',
        selectedVoucher: null,
        selectedVoucherId: null,
        ngayTao: new Date().toISOString(),
        thanhToan: null,
        trangThai: '0',
        hinhThucThanhToan: 'null',
        idNhanVien: localStorage.getItem('userId') || 1,
        giaoHang: false,
        customerInfo: null,
        ghiChuGiaoHang: ''
      };
      this.hoaDons.push(hd);
      this.tabActive = ma;
    },
    xoaHoaDon(ma) {
      this.hoaDons = this.hoaDons.filter(hd => hd.maHoaDon !== ma);
      if (this.hoaDons.length > 0) this.tabActive = this.hoaDons[0].maHoaDon;
    },
    showChonSanPham(hd, isQR = false) {
      this.hoaDonChon = hd;
      this.productSearchModal = true;
      this.fetchSanPhamList();
      if (isQR) {
        ElMessage.info('Chức năng quét QR chưa được triển khai');
      }
    },
    async fetchSanPhamList() {
  this.productLoading = true;
  try {
    const params = {
      page: this.pageSanPham - 1,
      size: this.pageSizeSanPham,
      keyword: this.productSearchQuery || '',
    };
    const response = await axios.get('http://localhost:8080/api/sanphamchitiet', { params });
    if (Array.isArray(response.data)) {
      // Trường hợp API trả về danh sách trực tiếp
      this.listSanPham = {
        content: response.data.map(item => ({
          id: item.id,
          maSPCT: item.maSPCT,
          tenSP: item.tenSP || 'N/A', // Lấy từ bảng SanPham qua join hoặc mặc định
          maChiTietGiay: item.maSPCT, // Sử dụng maSPCT
          giaBan: item.gia, // Ánh xạ từ gia
          soLuongTonKho: item.soLuong, // Ánh xạ từ soLuong
          moTa: item.moTa,
          trangThai: item.trangThai,
          anhGiay: item.anhGiay || '/default-image.jpg', // Giá trị mặc định nếu backend chưa trả về
          tenDanhMuc: item.tenDanhMuc || 'N/A', // Cần join từ DanhMuc
          tenThuongHieu: item.tenThuongHieu || 'N/A', // Cần join từ ThuongHieu
          tenMauSac: item.tenMauSac || 'N/A', // Cần join từ MauSac
          tenChatLieu: item.tenChatLieu || 'N/A', // Cần join từ ChatLieu
          tenKichThuoc: item.tenKichThuoc || 'N/A', // Cần join từ Size
        })),
        number: params.page,
        size: params.size,
        totalElements: response.data.length, // Giả lập số lượng phần tử
      };
    } else if (response.data && response.data.content) {
      // Trường hợp API trả về đối tượng phân trang
      this.listSanPham = {
        content: response.data.content.map(item => ({
          id: item.id,
          maSPCT: item.maSPCT,
          tenSP: item.tenSP || 'N/A',
          maChiTietGiay: item.maSPCT,
          giaBan: item.gia,
          soLuongTonKho: item.soLuong,
          moTa: item.moTa,
          trangThai: item.trangThai,
          anhGiay: item.anhGiay || '/default-image.jpg',
          tenDanhMuc: item.tenDanhMuc || 'N/A',
          tenThuongHieu: item.tenThuongHieu || 'N/A',
          tenMauSac: item.tenMauSac || 'N/A',
          tenChatLieu: item.tenChatLieu || 'N/A',
          tenKichThuoc: item.tenKichThuoc || 'N/A',
        })),
        number: response.data.number,
        size: response.data.size,
        totalElements: response.data.totalElements,
      };
    } else {
      this.listSanPham = { content: [], number: 0, size: 10, totalElements: 0 };
      ElMessage.error('Dữ liệu sản phẩm không hợp lệ');
    }
  } catch (error) {
    console.error('Lỗi khi lấy danh sách sản phẩm:', error);
    this.listSanPham = { content: [], number: 0, size: 10, totalElements: 0 };
    ElMessage.error(`Không thể tải danh sách sản phẩm: ${error.message || 'Lỗi mạng'}`);
  } finally {
    this.productLoading = false;
  }
},
    chonSanPham(sp) {
      const existed = this.hoaDonChon.sanPhams.find(x => x.id === sp.id);
      const tonKho = sp.soLuong;
      if (existed) {
        if (existed.soLuong + 1 > tonKho) {
          ElMessage.error('Vượt quá số lượng tồn kho!');
        } else {
          existed.soLuong += 1;
        }
      } else {
        this.hoaDonChon.sanPhams.push({ ...sp, soLuong: 1, soLuongTonKho: tonKho });
      }
      this.productSearchModal = false;
    },
    onChangeSoLuong(row, hd) {
      if (row.soLuong > row.soLuongTonKho) {
        row.soLuong = row.soLuongTonKho;
        ElMessage.error('Vượt quá số lượng tồn kho!');
      } else if (row.soLuong < 1) {
        row.soLuong = 1;
      }
      
      const validIds = this.getValidVouchers(hd).map(v => v.id);
      if (hd.selectedVoucherId && !validIds.includes(hd.selectedVoucherId)) {
        const oldVoucher = hd.selectedVoucher;
        hd.selectedVoucherId = null;
        hd.selectedVoucher = null;
        
        if (oldVoucher) {
          const tongTien = this.tinhTienHang(hd);
          const minAmount = oldVoucher.giaTriDonHangToiThieu;
          ElMessage.warning(`Voucher "${oldVoucher.tenVoucher}" không còn hợp lệ! Tổng tiền hàng hiện tại: ${tongTien.toLocaleString()} VND, yêu cầu tối thiểu: ${minAmount.toLocaleString()} VND`);
        }
      }
    },
    fetchVouchers() {
      axios.get('/admin/voucher?page=0&size=50').then(res => {
        this.vouchers = res.data.content;
      }).catch(err => {
        console.error('Lỗi khi lấy danh sách voucher:', err);
        ElMessage.error('Không thể tải danh sách voucher');
      });
    },
    chonVoucher(hd) {
      hd.selectedVoucher = this.vouchers.find(v => v.id === hd.selectedVoucherId) || null;
    },
    tinhTienHang(hd) {
      return hd.sanPhams.reduce((sum, sp) => sum + (sp.giaBan * sp.soLuong), 0);
    },
    tinhPhiVanChuyen(hd) {
      return hd.giaoHang ? 34000 : 0;
    },
    tinhGiamGia(hd) {
      if (!hd.selectedVoucher) return 0;
      if (hd.selectedVoucher.hinhThucGiam === 'PERCENT' || hd.selectedVoucher.hinhThucGiam === 'Phần trăm') {
        return Math.floor(this.tinhTienHang(hd) * (hd.selectedVoucher.mucGiam / 100));
      } else {
        return hd.selectedVoucher.mucGiam;
      }
    },
    tinhTongTien(hd) {
      return this.tinhTienHang(hd) + this.tinhPhiVanChuyen(hd) - this.tinhGiamGia(hd);
    },
    async xacNhanDatHang(hd) {
      try {
        let idDiaChiKhachHang = null;

        // Handle new delivery address creation
        if (
          hd.giaoHang &&
          hd.customerInfo &&
          hd.customerInfo.selectedDiaChi &&
          !hd.customerInfo.selectedDiaChi.id &&
          hd.customerInfo.diaChiKhachHangs
        ) {
          const diaChiDTO = {
            tenDiaChi: hd.customerInfo.diaChiKhachHangs,
            ghiChu: hd.ghiChuGiaoHang || '',
          };
          const response = await axios.post(`/api/khachhang/${hd.idKhachHang}/dia-chi`, diaChiDTO);
          idDiaChiKhachHang = response.data.id;
          this.diaChiList.push(response.data);
          hd.customerInfo.selectedDiaChi = response.data;
        } else if (
          hd.giaoHang &&
          hd.customerInfo &&
          hd.customerInfo.selectedDiaChi &&
          hd.customerInfo.selectedDiaChi.id
        ) {
          idDiaChiKhachHang = hd.customerInfo.selectedDiaChi.id;
        }

        // Preparea invoice data according to the new HoaDonDTO
        const hoaDonData = {
          maHD: hd.maHoaDon || '',
          idKhachHang: hd.idKhachHang ? Number(hd.idKhachHang) : null,
          idNhanVien: Number(hd.idNhanVien) || 1,
          ngayTao: hd.ngayTao || new Date().toISOString(),
          ngaySua: new Date().toISOString(),
          tongTien: this.tinhTongTien(hd),
          trangThai: true,
          chiTietSanPham: (hd.sanPhams || []).map(sp => ({
            idCtSanPham: sp.id,
            soLuong: sp.soLuong,
            donGia: sp.giaBan
          }))
        };

        console.log('Dữ liệu gửi lên:', hoaDonData);

        // Send POST request to create invoice to the new endpoint
        const response = await axios.post('/api/hoadon', hoaDonData);
        if (response.data) {
          // Update inventory for each product
          for (const sp of hd.sanPhams) {
            try {
              await axios.put(`/api/sanphamchitiet/${sp.id}`, {
                soLuong: sp.soLuongTonKho - sp.soLuong
              });
            } catch (error) {
              console.error(`Lỗi khi cập nhật số lượng sản phẩm ${sp.id}:`, error);
              ElMessage.error(`Không thể cập nhật số lượng cho sản phẩm ${sp.tenGiay}`);
            }
          }

          // Update voucher quantity if applicable
          if (hd.selectedVoucherId) {
            try {
              await axios.put(`/admin/voucher/${hd.selectedVoucherId}`, {
                soLuong: hd.selectedVoucher.soLuong - 1
              });
            } catch (error) {
              console.error('Lỗi khi cập nhật số lượng voucher:', error);
              ElMessage.error('Không thể cập nhật số lượng voucher');
            }
          }

          // Create invoice details (HoaDonCT)
          for (const sp of hd.sanPhams) {
            const hoaDonCTData = {
              idSP: sp.id,
              idHD: response.data.idHD,
              idKM: hd.selectedVoucherId ? Number(hd.selectedVoucherId) : null,
              soLuong: sp.soLuong,
              donGia: sp.giaBan,
              thanhTien: sp.giaBan * sp.soLuong,
              trangThai: true,
              ngayTao: new Date().toISOString(),
              ngaySua: new Date().toISOString(),
              idPTT: hd.thanhToan && hd.thanhToan.phuongThuc === 'chuyen_khoan' ? 2 : 1 // Assuming 1: Tiền mặt, 2: Chuyển khoản
            };
            try {
              await axios.post('/api/hoadonct', hoaDonCTData);
            } catch (error) {
              console.error('Lỗi khi tạo chi tiết hóa đơn:', error);
              ElMessage.error('Không thể tạo chi tiết hóa đơn');
            }
          }

          ElMessage.success('Lưu hóa đơn thành công!');
          this.hoaDonIn = hd;
          this.modalHoaDon = true;
          this.resetHoaDon(hd);
        }
      } catch (error) {
        console.error('Lỗi khi lưu hóa đơn:', error);
        const errorMessage = error.response?.data?.message || 'Có lỗi xảy ra khi lưu hóa đơn!';
        ElMessage.error(errorMessage);
      }
    },
    inHoaDon() {
      window.print();
      ElMessage.success('Đã in hóa đơn thành công');
    },
    xoaSanPham(hd, sp) {
      hd.sanPhams = hd.sanPhams.filter(item => item.id !== sp.id);
    },
    getValidVouchers(hd) {
      const tongTien = this.tinhTienHang(hd);
      return this.vouchers.filter(v => {
        if (!v.trangThai) {
          return false;
        }
        if (v.soLuong <= 0) {
          return false;
        }
        if (v.giaTriDonHangToiThieu && tongTien < v.giaTriDonHangToiThieu) {
          return false;
        }
        const now = new Date();
        const ngayBatDau = new Date(v.ngayBatDau);
        const ngayKetThuc = new Date(v.ngayKetThuc);
        return now >= ngayBatDau && now <= ngayKetThuc;
      });
    },
    openThanhToan(hd) {
      this.modalThanhToan = true;
      this.thanhToanHoaDon = hd;
      this.thanhToanTongTien = this.tinhTongTien(hd);
      this.thanhToanPhuongThuc = 'chuyen_khoan';
      this.thanhToanTienKhachDua = 0;
      this.thanhToanMaGiaoDich = '';
      this.thanhToanLichSu = [];
    },
    xoaThanhToan(idx) {
      this.thanhToanLichSu.splice(idx, 1);
    },
    xacNhanThanhToan() {
      if (this.thanhToanLichSu.length === 0 && this.thanhToanTienKhachDua >= this.tinhTongTien(this.thanhToanHoaDon)) {
        this.thanhToanLichSu.push({
          soTien: this.thanhToanTienKhachDua,
          phuongThuc: this.thanhToanPhuongThuc,
          maGiaoDich: this.thanhToanMaGiaoDich
        });
      }
      if (this.thanhToanHoaDon) {
        const tongSoTien = this.thanhToanLichSu.reduce((sum, t) => sum + (t.soTien || 0), 0);
        const first = this.thanhToanLichSu[0] || {};
        this.thanhToanHoaDon.thanhToan = {
          soTien: tongSoTien,
          phuongThuc: first.phuongThuc || this.thanhToanPhuongThuc,
          maGiaoDich: first.maGiaoDich || this.thanhToanMaGiaoDich
        };
      }
      this.modalThanhToan = false;
      ElMessage.success('Thanh toán thành công!');
    },
    async fetchCustomers(page = 0, size = 10) {
      this.loadingCustomers = true;
      try {
        const response = await axios.get('/api/khachhang', {
          params: { page, size }
        });
        this.customers = response.data.content.map(customer => ({
          ...customer,
        }));
        this.totalCustomers = response.data.totalElements;
        this.currentPageCustomer = page + 1;
      } catch (error) {
        if (error.response?.status === 403 || error.response?.status === 401) {
          this.$message.error('Vui lòng đăng nhập với quyền ADMIN hoặc STAFF');
          this.$router.push('/login');
        } else {
          this.$message.error('Lỗi khi lấy danh sách khách hàng: ' + (error.response?.data || error.message));
        }
      } finally {
        this.loadingCustomers = false;
      }
    },
    debounceSearchCustomers() {
      this.debounce(this.searchCustomers, 300)();
    },
    showCustomerModal(hd) {
      this.selectedHoaDon = hd;
      this.customerSearch = '';
      this.modalCustomer = true;
      this.fetchCustomers();
    },
    async selectCustomer(customer, hd) {
      hd.khachHang = customer.hoTen;
      hd.idKhachHang = customer.id;
      hd.customerInfo = { 
        ...customer,
        gioiTinh: customer.gioiTinh ? 'Nam' : 'Nữ',
        trangThai: customer.trangThai ? 'Hoạt động' : 'Ngừng hoạt động',
        selectedDiaChi: null,
        diaChiKhachHangs: ''
      };
      
      try {
        const response = await axios.get(`/api/khachhang/${customer.id}/dia-chi`);
        this.diaChiList = response.data;
        if (this.diaChiList.length > 0) {
          hd.customerInfo.selectedDiaChi = this.diaChiList[0];
          hd.customerInfo.diaChiKhachHangs = this.diaChiList[0].tenDiaChi;
        }
      } catch (error) {
        console.error('Lỗi khi lấy danh sách địa chỉ:', error);
        this.$message.error('Lỗi khi lấy danh sách địa chỉ: ' + (error.response?.data?.message || error.message));
      }
      
      this.modalCustomer = false;
    },
    async searchCustomers() {
      if (!this.customerSearch) {
        this.fetchCustomers(this.currentPageCustomer - 1, this.pageSizeCustomer);
        return;
      }
      this.loadingCustomers = true;
      try {
        const response = await axios.get('/api/khachhang', {
          params: { 
            page: 0,
            size: this.pageSizeCustomer,
            search: this.customerSearch
          }
        });
        this.customers = response.data.content;
        this.totalCustomers = response.data.totalElements;
      } catch (error) {
        console.error('Lỗi khi tìm kiếm khách hàng:', error);
        this.$message.error('Lỗi khi tìm kiếm khách hàng: ' + (error.response?.data?.message || error.message));
      } finally {
        this.loadingCustomers = false;
      }
    },
    updateDiaChi(hd) {
      if (hd.customerInfo.selectedDiaChi && hd.customerInfo.selectedDiaChi.id) {
        hd.customerInfo.diaChiKhachHangs = hd.customerInfo.selectedDiaChi.tenDiaChi;
      } else {
        hd.customerInfo.diaChiKhachHangs = '';
      }
    },
    async toggleDeliveryInfo(hd) {
      if (!hd.giaoHang) {
        hd.customerInfo = null;
        hd.ghiChuGiaoHang = '';
        this.diaChiList = [];
      } else if (!hd.customerInfo && hd.khachHang) {
        const customer = this.customers.find(c => c.hoTen === hd.khachHang);
        if (customer) {
          hd.customerInfo = { 
            ...customer,
            gioiTinh: customer.gioiTinh ? 'Nam' : 'Nữ',
            trangThai: customer.trangThai ? 'Hoạt động' : 'Ngừng hoạt động',
            selectedDiaChi: null,
            diaChiKhachHangs: ''
          };
          hd.idKhachHang = customer.id;
          try {
            const response = await axios.get(`/api/khachhang/${customer.id}/dia-chi`);
            this.diaChiList = response.data;
            if (this.diaChiList.length > 0) {
              hd.customerInfo.selectedDiaChi = this.diaChiList[0];
              hd.customerInfo.diaChiKhachHangs = this.diaChiList[0].tenDiaChi;
            }
          } catch (error) {
            console.error('Lỗi khi lấy danh sách địa chỉ:', error);
            this.$message.error('Lỗi khi lấy danh sách địa chỉ: ' + (error.response?.data?.message || error.message));
          }
        }
      }
    },
    resetHoaDon(hd) {
      const ma = 'HD' + (Math.floor(Math.random() * 9000) + 1000);
      hd.maHoaDon = ma;
      hd.sanPhams = [];
      hd.khachHang = '';
      hd.idKhachHang = null;
      hd.customerInfo = null;
      hd.selectedVoucher = null;
      hd.selectedVoucherId = null;
      hd.thanhToan = null;
      hd.trangThai = 'CHO_THANH_TOAN';
      hd.hinhThucThanhToan = 'TIEN_MAT';
      hd.ngayTao = new Date().toISOString();
      hd.idNhanVien = localStorage.getItem('userId') || 1;
      hd.giaoHang = false;
      hd.ghiChuGiaoHang = '';
      
      setTimeout(() => {
        this.modalHoaDon = false;
      }, 2000);
    },
    taoHoaDonMoiNgay() {
      this.modalHoaDon = false;
      this.taoHoaDon();
      ElMessage.success('Đã tạo hóa đơn mới!');
    },
    debouncedSearchProducts: _.debounce(function() {
      this.pageSanPham = 1;
      this.fetchSanPhamList();
    }, 300),
    onProductPageChange(page) {
      this.pageSanPham = page;
      this.fetchSanPhamList();
    },
    taiLaiDanhSachSanPham() {
      this.productSearchQuery = '';
      this.pageSanPham = 1;
      this.fetchSanPhamList();
    },
  },
};
</script>


<style scoped>
.ban-hang-tai-quay {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.no-data {
  text-align: center;
  margin: 40px 0;
}

.main-content {
  display: flex;
  gap: 20px;
  height: 600px;
}

.left-section {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.actions {
  margin-bottom: 15px;
  display: flex;
  gap: 10px;
}

.product-section {
  flex: 1;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 15px;
  background-color: #fafafa;
}

.product-section h3 {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: bold;
}

.product-list {
  height: 400px;
  overflow-y: auto;
}
.product-list::-webkit-scrollbar-thumb {
  background-color: #888;
  border-radius: 4px;
}

.product-list::-webkit-scrollbar-track {
  background: #f1f1f1;
}

/* Hiệu ứng cho nút */
.animated-btn {
  position: relative;
  background-color: #ffffff; /* White background */
  border: 2px solid #8B4513; /* Brown border */
  color: #8B4513; /* Brown text/icon */
  transition: all 0.2s ease;
  border-radius: 6px; /* Consistent border radius */
  font-weight: 500;
  padding: 8px 16px; /* Consistent padding */
}

.animated-btn:hover {
  background-color: #8B4513; /* Brown background on hover */
  color: #ffffff; /* White text/icon on hover */
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(139, 69, 19, 0.2); /* Brown shadow */
}

.animated-btn:active {
  transform: scale(0.95);
  box-shadow: 0 2px 6px rgba(139, 69, 19, 0.1);
}

.animated-btn:disabled {
  background-color: #f9f9f9;
  border-color: #d3d3d3;
  color: #d3d3d3;
  transform: none;
  box-shadow: none;
  cursor: not-allowed;
}

/* Specific button overrides to maintain consistency */
.btn-primary.animated-btn,
.btn-success.animated-btn,
.btn-danger.animated-btn,
.btn-info.animated-btn,
.btn-warning.animated-btn,
.btn-outline-danger.animated-btn,
.btn-outline-success.animated-btn {
  background-color: #ffffff;
  border: 2px solid #8B4513;
  color: #8B4513;
}

.btn-primary.animated-btn:hover,
.btn-success.animated-btn:hover,
.btn-danger.animated-btn:hover,
.btn-info.animated-btn:hover,
.btn-warning.animated-btn:hover,
.btn-outline-danger.animated-btn:hover,
.btn-outline-success.animated-btn:hover {
  background-color: #8B4513;
  color: #ffffff;
  border-color: #8B4513;
}

/* Ensure active state for payment buttons maintains the brown theme */
.btn-outline-danger.animated-btn.active,
.btn-outline-success.animated-btn.active {
  background-color: #8B4513;
  color: #ffffff;
  border-color: #8B4513;
}

/* Hiệu ứng cho tab hóa đơn */
.nav-tabs .nav-link {
  transition: all 0.3s ease;
}

.nav-tabs .nav-link:hover {
  background-color: #f8f9fa;
  transform: translateY(-2px);
}

.tab-content .tab-pane {
  animation: fadeSlide 0.3s ease-out;
}

@keyframes fadeSlide {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* Hiệu ứng cho danh sách sản phẩm */
.animated-item {
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Tùy chỉnh để tích hợp Element UI với Bootstrap */
.el-input-number {
  width: 100px;
}

.el-switch {
  --el-switch-on-color: #8B4513; /* Brown for switch */
}

.empty-products {
  text-align: center;
  color: #999;
  margin-top: 50px;
}

.product-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  margin-bottom: 10px;
  background: white;
  border: 1px solid #eee;
  border-radius: 4px;
}

.product-info {
  flex: 1;
}

.product-name {
  display: block;
  font-weight: bold;
  margin-bottom: 5px;
}

.product-price {
  color: #666;
  font-size: 14px;
}

.product-quantity {
  display: flex;
  align-items: center;
  gap: 10px;
}

.product-quantity .el-input-number {
  width: 100px;
}

.right-section {
  width: 350px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.customer-section {
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 15px;
  background-color: #fafafa;
}

.customer-info {
  margin-bottom: 15px;
}

.customer-info label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.customer-input-group {
  display: flex;
  gap: 10px;
}

.customer-input-group .el-input {
  flex: 1;
}

.delivery-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.delivery-section label {
  font-weight: bold;
}

.voucher-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.payment-summary {
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 15px;
  background-color: #fafafa;
}

.total-display {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding: 10px;
  background-color: #f0f0f0;
  border-radius: 4px;
}

.total-label {
  font-weight: bold;
  font-size: 16px;
}

.total-amount {
  font-weight: bold;
  font-size: 18px;
  color: #e74c3c;
}

.payment-details {
  margin-bottom: 15px;
}

.payment-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.payment-row span:first-child {
  color: #666;
}

.final-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background-color: #f0f0f0;
  border-radius: 4px;
  margin-bottom: 15px;
  font-weight: bold;
}

.final-amount {
  color: #e74c3c;
  font-size: 16px;
}

.confirm-btn {
  width: 100%;
  height: 40px;
  font-size: 16px;
}

/* Modal chọn sản phẩm - Overlay */
.chonSP {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  backdrop-filter: blur(3px);
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* Modal container */
.chonSP .product-search-modal {
  background: white;
  width: 95%;
  max-width: 1400px;
  height: 85vh;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-50px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* Modal header */
.product-search-modal .modal-header {
  padding: 20px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px 12px 0 0;
}

.modal-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.modal-header .close-btn {
  position: absolute;
  top: 15px;
  right: 20px;
  background: none;
  border: none;
  color: white;
  font-size: 24px;
  cursor: pointer;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s;
}

.modal-header .close-btn:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

/* Modal body */
.product-search-modal .modal-body {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

/* Search section */
.search-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 20px;
  background: #f8fafc;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
}

.search-input .el-input {
  width: 350px;
}

.search-input .el-input__inner {
  height: 42px;
  border-radius: 8px;
  border: 2px solid #e2e8f0;
  padding-left: 45px;
  transition: all 0.3s ease;
}

.search-input .el-input__inner:focus {
  border-color: #8B4513; /* Brown border on focus */
  box-shadow: 0 0 0 3px rgba(139, 69, 19, 0.1);
}

.search-input .el-input__icon {
  line-height: 42px;
}

.price-range {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.price-range .el-slider {
  margin: 0 20px;
  width: 200px;
}

/* Filter section */
.filter-section {
  margin-bottom: 24px;
  padding: 20px;
  background: #f8fafc;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-wrap: nowrap;
}

.filter-label {
  font-size: 14px;
  color: #374151;
  font-weight: 600;
  min-width: 80px;
  flex-shrink: 0;
}

.filter-row select {
  padding: 8px 12px;
  border: 2px solid #e2e8f0;
  border-radius: 6px;
  font-size: 14px;
  color: #374151;
  background: white;
  flex: 1;
  min-width: 120px;
  transition: all 0.2s ease;
}

.filter-row select:focus {
  outline: none;
  border-color: #8B4513; /* Brown border on focus */
  box-shadow: 0 0 0 3px rgba(139, 69, 19, 0.1);
}

.filter-row .reset-button {
  padding: 8px 16px;
  background-color: #ffffff;
  border: 2px solid #8B4513;
  color: #8B4513;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  flex-shrink: 0;
  white-space: nowrap;
}

.filter-row .reset-button:hover {
  background-color: #8B4513;
  color: #ffffff;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(139, 69, 19, 0.4);
}

/* Responsive for filter section */
@media (max-width: 1200px) {
  .filter-row {
    flex-wrap: wrap;
    gap: 10px;
  }

  .filter-row select {
    min-width: 100px;
  }

  .filter-label {
    min-width: 70px;
  }
}

@media (max-width: 768px) {
  .filter-row {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-label {
    min-width: auto;
    margin-bottom: 5px;
  }

  .filter-row select,
  .filter-row .reset-button {
    width: 100%;
  }
}

/* Product table container */
.product-table {
  flex: 1;
  background: white;
  border-radius: 10px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  max-height: 500px;
  overflow-y: auto;
  position: relative;
}

/* Table styles */
.product-table table {
  width: 100%;
  border-collapse: collapse;
  background: white;
}

.product-table thead {
  position: sticky;
  top: 0;
  z-index: 10;
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
}

.product-table thead th {
  padding: 16px 12px;
  border: 1px solid #e2e8f0;
  font-weight: 600;
  font-size: 14px;
  color: #374151;
  text-align: left;
}

.product-table tbody td {
  padding: 16px 12px;
  border: 1px solid #f1f5f9;
  font-size: 14px;
  color: #374151;
  vertical-align: middle;
}

.product-table tbody tr {
  transition: all 0.2s ease;
}

.product-table tbody tr:hover {
  background-color: #f8fafc;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* Product image */
.product-table img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
  border: 2px solid #e2e8f0;
  transition: all 0.2s ease;
}

.product-table img:hover {
  transform: scale(1.1);
  border-color: #8B4513; /* Brown border on hover */
}

/* Price styling */
.product-table .price-cell {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.product-table .original-price {
  text-decoration: line-through;
  color: #9ca3af;
  font-size: 12px;
  margin-bottom: 2px;
}

.product-table .current-price {
  color: #dc2626;
  font-weight: 700;
  font-size: 14px;
}

/* Action button in product table */
.product-table .action-btn {
  background-color: #ffffff;
  border: 2px solid #8B4513;
  color: #8B4513;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  transition: all 0.2s ease;
}

.product-table .action-btn:hover {
  background-color: #8B4513;
  color: #ffffff;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(139, 69, 19, 0.4);
}

/* Loading and empty states */
.loading-state, .empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #6b7280;
}

.loading-state {
  background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
}

.loading-state::before {
  content: '';
  display: inline-block;
  width: 40px;
  height: 40px;
  border: 4px solid #e5e7eb;
  border-top: 4px solid #8B4513; /* Brown loading spinner */
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.empty-state {
  background: linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%);
  border-radius: 8px;
}

/* Pagination */
.pagination-section {
  text-align: center;
  margin-top: 24px;
  padding: 20px;
  background: #f8fafc;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
}

/* .pagination-section > div {
  display: inline-flex;
  align-items: center;
🙈  gap: 16px;
  background: white;
  padding: 12px 20px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
} */

.pagination-section span {
  font-size: 14px;
  color: #374151;
  font-weight: 500;
}

.pagination-section button {
  padding: 8px 16px;
  border: 2px solid #8B4513; /* Brown border */
  background: #ffffff;
  color: #8B4513;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.pagination-section button:not(:disabled):hover {
  background-color: #8B4513;
  color: #ffffff;
  border-color: #8B4513;
  transform: translateY(-1px);
}

.pagination-section button:disabled {
  background: #f9fafb;
  color: #9ca3af;
  border-color: #d3d3d3;
  cursor: not-allowed;
}

/* Modal footer */
.dialog-footer {
  padding: 20px 24px;
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.dialog-footer .el-button {
  min-width: 100px;
  height: 40px;
  border-radius: 8px;
  font-weight: 500;
  background-color: #ffffff;
  border: 2px solid #8B4513;
  color: #8B4513;
}

.dialog-footer .el-button:hover {
  background-color: #8B4513;
  color: #ffffff;
  border-color: #8B4513;
}

.dialog-footer .el-button[type="primary"] {
  background-color: #ffffff;
  border: 2px solid #8B4513;
  color: #8B4513;
}

.dialog-footer .el-button[type="primary"]:hover {
  background-color: #8B4513;
  color: #ffffff;
}

/* Style cho modal phương thức thanh toán */
.modal-lg .modal-header {
  background-color: #8B4513; /* Brown header */
  border-bottom: none;
}

.modal-lg .modal-title {
  font-size: 1.25rem;
  color: #ffffff;
}

.modal-lg .modal-body {
  padding: 20px;
  text-align: center;
}

.modal-lg .modal-body h6 {
  margin-bottom: 0.5rem;
}

.modal-lg .modal-body .btn {
  padding: 8px 20px;
  border-radius: 20px;
  font-weight: bold;
}

.modal-lg .btn-outline-danger {
  background-color: #ffffff;
  border: 2px solid #8B4513;
  color: #8B4513;
}

.modal-lg .btn-outline-danger:hover,
.modal-lg .btn-outline-danger.active {
  background-color: #8B4513;
  color: #ffffff;
  border-color: #8B4513;
}

.modal-lg .btn-outline-success {
  background-color: #ffffff;
  border: 2px solid #8B4513;
  color: #8B4513;
}

.modal-lg .btn-outline-success:hover,
.modal-lg .btn-outline-success.active {
  background-color: #8B4513;
  color: #ffffff;
  border-color: #8B4513;
}

.modal-lg .form-floating > .form-control:focus ~ label,
.modal-lg .form-floating > .form-control:not(:placeholder-shown) ~ label {
  transform: scale(0.85) translateY(-0.5rem) translateX(0.15rem);
  opacity: 1;
}

.modal-lg .form-floating > label {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  padding: 1rem 0.75rem;
  pointer-events: none;
  border: 1px solid transparent;
  transform-origin: 0 0;
  transition: opacity 0.1s ease-in-out, transform 0.1s ease-in-out;
  opacity: 0.65;
}

.modal-lg .form-floating > .form-control {
  padding-top: 1.625rem;
  padding-bottom: 0.625rem;
}

.modal-lg .input-group {
  margin-bottom: 0.5rem;
}

.modal-lg .input-group-text {
  background-color: #8B4513;
  color: #ffffff;
  border: none;
}

.modal-lg .form-control {
  border-color: #8B4513;
}

.modal-lg .form-control:focus {
  border-color: #8B4513;
  box-shadow: 0 0 0 0.2rem rgba(139, 69, 19, 0.25);
}

.modal-lg .modal-footer .btn-success {
  background-color: #ffffff;
  border: 2px solid #8B4513;
  color: #8B4513;
}

.modal-lg .modal-footer .btn-success:hover {
  background-color: #8B4513;
  color: #ffffff;
  border-color: #8B4513;
}

.modal-lg .table {
  margin-top: 1rem;
  width: 100%;
}

.modal-lg .table th,
.modal-lg .table td {
  text-align: center;
  vertical-align: middle;
}

.modal-lg .table th {
  background-color: #f8f9fa;
  font-weight: bold;
}

/* Style cho modal chọn khách hàng */
.modal.show {
  display: flex !important;
  align-items: center;
  justify-content: center;
}

.modal-dialog {
  margin: 0 !important;
}

/* Responsive cho màn hình nhỏ */
@media (max-width: 768px) {
  .modal-dialog {
    width: 95% !important;
    max-width: 95% !important;
  }

  .modal-content {
    max-height: 95vh !important;
  }
}

/* Cải thiện giao diện bảng */
.custom-table {
  margin-bottom: 0;
}

.custom-table th {
  border-top: none;
  font-weight: 600;
  font-size: 0.9rem;
}

.table-row:hover {
  background-color: rgba(139, 69, 19, 0.1) !important;
}

/* Cải thiện nút đóng */
.btn-close-white {
  filter: invert(1) grayscale(100%) brightness(200%);
}

.delivery-switch {
  transform: scale(1.2);
}

.delivery-info {
  animation: slideDown 0.3s ease-in-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.form-control:focus {
  border-color: #8B4513;
  box-shadow: 0 0 0 0.2rem rgba(139, 69, 19, 0.25);
}

.info-item {
  display: flex;
  align-items: center;
  padding: 0.5rem 0;
}
</style>