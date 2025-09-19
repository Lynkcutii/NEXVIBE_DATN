```vue
<template>
  <div class="ban-hang-tai-quay container-fluid py-4">
    <div class="card shadow-sm">
      <div class="card-body">
        <!-- Header -->
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h2 class="mb-0">Bán hàng</h2>
          <button
            type="button"
            class="btn btn-primary"
            :disabled="hoaDons.length >= 5"
            @click="taoHoaDon"
          >
            <i class="bi bi-plus"></i> Tạo đơn hàng
          </button>
        </div>

        <!-- No data state -->
        <div v-if="hoaDons.length === 0" class="text-center py-5 bg-light rounded">
          <p class="mb-0">Chưa có đơn hàng nào</p>
        </div>

        <!-- Tabs for invoices -->
        <div v-else>
          <ul class="nav nav-tabs mb-3">
            <li class="nav-item" v-for="(hd, idx) in hoaDons" :key="hd.maHoaDon">
              <a
                class="nav-link d-flex align-items-center"
                :class="{ active: tabActive === hd.maHoaDon }"
                :href="`#pane-${hd.maHoaDon}`"
                data-bs-toggle="tab"
                @click="tabActive = hd.maHoaDon"
              >
                Đơn hàng {{ idx + 1 }} - {{ hd.maHoaDon }}
                <span
                  v-if="hd.trangThai === 'CHO_THANH_TOAN' && hd.countdown > 0"
                  class="ms-2 badge bg-warning text-dark"
                >
                  {{ formatCountdown(hd.countdown) }}
                </span>
                <span
                  v-else-if="hd.trangThai === 'CHO_THANH_TOAN'"
                  class="ms-2 badge bg-danger"
                >
                  Hết thời gian
                </span>
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
            >
              <div class="row">
                <!-- Left side - Product actions and list -->
                <div class="col-lg-9 mb-4 mb-lg-0">
                  <div class="card h-100">
                    <div class="card-body">
                      <div class="d-flex gap-2 mb-3">
                        <button
                          type="button"
                          class="btn btn-success btn-sm"
                          @click="showChonSanPham(hd)"
                        >
                          <i class="bi bi-plus-circle"></i> Thêm sản phẩm
                        </button>
                        <button
                          type="button"
                          class="btn btn-info btn-sm"
                          @click="showChonSanPham(hd, true)"
                        >
                          <i class="bi bi-qr-code"></i> Quét QR sản phẩm
                        </button>
                        <button
                          type="button"
                          class="btn btn-danger btn-sm"
                          @click="xoaHoaDon(hd)"
                        >
                          <i class="bi bi-trash"></i> Xóa hóa đơn
                        </button>
                      </div>

                      <!-- Product section -->
                      <h5 class="card-title">Sản phẩm</h5>
                      <div class="table-responsive" style="max-height: 400px;">
                        <div v-if="hd.sanPhams.length === 0" class="text-center py-5 bg-light rounded">
                          <p class="mb-0">Chưa có sản phẩm nào</p>
                        </div>
                        <table v-else class="table table-striped table-hover">
                          <thead>
                            <tr>
                              <th>Ảnh</th>
                              <th>Thông tin sản phẩm</th>
                              <th>Giá bán</th>
                              <th>Số lượng</th>
                              <th>Thành tiền</th>
                              <th>Voucher</th>
                              <th>Xóa</th>
                            </tr>
                          </thead>
                          <tbody>
                            <tr v-for="sp in hd.sanPhams" :key="sp.id">
                              <td>
                                <img :src="sp.anhGiay" alt="Ảnh" class="img-fluid rounded" style="width: 60px; height: 60px; object-fit: cover;" />
                              </td>
                              <td>
                                <div class="fw-bold text-primary">{{ sp.tenGiay }}</div>
                                <div class="small text-muted">
                                  Size: {{ sp.tenKichThuoc }} <br> Màu: {{ sp.tenMauSac }}
                                </div>
                                <!-- Hiển thị voucher hoặc giảm giá dựa trên thanhTien -->
                                <div v-if="sp.selectedVoucher || sp.thanhTien < sp.giaBan * sp.soLuong" class="mt-1">
                                  <span class="badge bg-success">
                                    <template v-if="sp.selectedVoucher">
                                      Voucher: {{ sp.selectedVoucher.tenVoucher }}
                                      (Giảm: {{ sp.selectedVoucher.hinhThucGiam === '%' ? sp.selectedVoucher.mucGiam + '%' : sp.selectedVoucher.giaGiam.toLocaleString() + ' VND' }})
                                    </template>
                                    <template v-else>
                                      Voucher: Unknown
                                      (Giảm: {{ (sp.giaBan * sp.soLuong - sp.thanhTien).toLocaleString() + ' VND' }})
                                    </template>
                                  </span>
                                </div>
                              </td>
                              <td>
                                <span class="fw-bold text-danger">{{ sp.giaBan != null ? sp.giaBan.toLocaleString() : '0' }} VND</span>
                              </td>
                              <td>
                                <input
                                  type="number"
                                  v-model="sp.soLuong"
                                  class="form-control form-control-sm w-50"
                                  :min="1"
                                  :max="sp.soLuongTonKho || 1000"
                                  @change="onChangeSoLuong(sp, hd)"
                                />
                                <small v-if="sp.soLuong > sp.soLuongTonKho" class="text-danger">Vượt quá tồn kho!</small>
                              </td>
                              <td>
                                <span class="fw-bold text-primary">{{ sp.thanhTien != null ? sp.thanhTien.toLocaleString() : '0' }} VND</span>
                              </td>
                              <td>
                                <button
                                  type="button"
                                  class="btn btn-primary btn-sm"
                                  @click="showVoucherModal(hd, sp)"
                                >
                                  <i class="bi bi-tag"></i> Voucher
                                </button>
                                <button
                                  v-if="sp.selectedVoucher || sp.thanhTien < sp.giaBan * sp.soLuong"
                                  type="button"
                                  class="btn btn-danger btn-sm mt-1"
                                  @click="huyChonVoucher(hd, sp)"
                                >
                                  <i class="bi bi-x-circle"></i> Hủy
                                </button>
                              </td>
                              <td>
                                <button
                                  type="button"
                                  class="btn btn-danger btn-sm"
                                  @click="xoaSanPham(hd, sp)"
                                >
                                  <i class="bi bi-trash"></i>
                                </button>
                              </td>
                            </tr>
                          </tbody>
                        </table>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Right side - Customer info and payment -->
                <div class="col-lg-3">
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
                            class="btn btn-primary btn-sm"
                            @click="showCustomerModal(hd)"
                          >
                            <i class="bi bi-person-plus me-1"></i> Chọn
                          </button>
                        </div>
                      </div>
                      <el-form-item label="Khuyến mãi">
                        <el-select
                          v-model="selectedVoucherId"
                          placeholder="Chọn khuyến mãi"
                          :disabled="!hd.idKhachHang || applicableVouchers.length === 0"
                          @change="chonVoucherHoaDon(hd)"
                          clearable
                        >
                          <el-option
                            v-for="voucher in applicableVouchers"
                            :key="voucher.id"
                            :label="`${voucher.tenVoucher} (${voucher.hinhThucGiam === 'PERCENTAGE' ? voucher.mucGiam + '%' : voucher.giaGiam.toLocaleString() + ' VND'})`"
                            :value="voucher.id"
                          />
                        </el-select>
                      </el-form-item>
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
                          <span>Giảm giá (Voucher)</span>
                          <span class="text-success">-{{ tinhGiamGia(hd).toLocaleString() }} VND</span>
                        </div>
                        <hr class="my-2">
                        <div class="d-flex justify-content-between mb-2">
                          <span class="fw-bold">Tổng cộng</span>
                          <span class="fw-bold text-danger">{{ tinhTongTien(hd).toLocaleString() }} VND</span>
                        </div>
                      </div>
                      <div class="mb-3">
                        <div class="d-flex justify-content-between mb-2 align-items-center">
                          <button
                            type="button"
                            class="btn btn-warning btn-sm"
                            :disabled="hd.sanPhams.length === 0"
                            @click="openThanhToan(hd)"
                          >
                            Phương thức thanh toán
                          </button>
                          <span v-if="hd.trangThai === 'DA_THANH_TOAN' && hd.thanhToan && hd.thanhToan.soTien > 0" class="text-primary">
                            {{ Number(hd.thanhToan.soTien).toLocaleString('vi-VN') }} VND
                            <i :class="hd.thanhToan.phuongThuc === 'MOMO' ? 'bi bi-qr-code' : 'bi bi-cash'" class="ms-2"></i>
                            <span v-if="hd.thanhToan.maGiaoDich" class="text-muted ms-1">({{ hd.thanhToan.maGiaoDich }})</span>
                          </span>
                          <span v-else-if="hd.trangThai === 'DA_THANH_TOAN'" class="text-warning">
                            Thông tin thanh toán không hợp lệ
                          </span>
                          <span v-else>Chưa thanh toán</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Chọn Sản Phẩm -->
    <div v-if="productSearchModal" class="modal fade show d-block" tabindex="-1" style="background: rgba(0,0,0,0.6);">
      <div class="modal-dialog modal-xl">
        <div class="modal-content">
          <div class="modal-header bg-primary text-white">
            <h5 class="modal-title">Tìm kiếm sản phẩm</h5>
            <button type="button" class="btn-close btn-close-white" @click="productSearchModal = false"></button>
          </div>
          <div class="modal-body">
            <!-- Search bar -->
            <div class="input-group mb-3">
              <span class="input-group-text"><i class="bi bi-search"></i></span>
              <input
                v-model="productSearchQuery"
                type="text"
                class="form-control"
                placeholder="Tìm kiếm theo tên hoặc mã sản phẩm..."
                @input="debouncedSearchProducts"
              />
            </div>

            <!-- Filters -->
            <div class="row g-3 mb-3">
              <div class="col-md-3">
                <select v-model="selectedCategory" class="form-select" @change="debouncedSearchProducts">
                  <option value="" disabled>Chọn danh mục</option>
                  <option v-for="cat in categories" :key="cat.value" :value="cat.value">{{ cat.label }}</option>
                </select>
              </div>
              <div class="col-md-3">
                <select v-model="selectedBrand" class="form-select" @change="debouncedSearchProducts">
                  <option value="" disabled>Chọn thương hiệu</option>
                  <option v-for="brand in brands" :key="brand.value" :value="brand.value">{{ brand.label }}</option>
                </select>
              </div>
              <div class="col-md-3">
                <select v-model="selectedMaterial" class="form-select" @change="debouncedSearchProducts">
                  <option value="" disabled>Chọn chất liệu</option>
                  <option v-for="material in materials" :key="material.value" :value="material.value">{{ material.label }}</option>
                </select>
              </div>
            </div>
            <div class="mb-3">
              <label class="form-label">Khoảng giá: {{ priceRange[0].toLocaleString() }} - {{ priceRange[1].toLocaleString() }} VND</label>
              <input
                type="range"
                v-model="priceRange[0]"
                class="form-range"
                :min="0"
                :max="5000000"
                :step="10000"
                @change="debouncedSearchProducts"
              />
              <input
                type="range"
                v-model="priceRange[1]"
                class="form-range"
                :min="0"
                :max="5000000"
                :step="10000"
                @change="debouncedSearchProducts"
              />
            </div>

            <!-- Product table -->
            <div class="table-responsive" v-loading="productLoading">
              <table class="table table-striped table-hover">
                <thead>
                  <tr>
                    <th>Ảnh</th>
                    <th>Tên sản phẩm</th>
                    <th>Mã SP</th>
                    <th>Danh mục</th>
                    <th>Thương hiệu</th>
                    <th>Chất liệu</th>
                    <th>Giá</th>
                    <th>Tồn kho</th>
                    <th>Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="row in listSanPham.content" :key="row.id" @click="chonSanPham(row)">
                    <td><img :src="row.anhGiay" alt="Ảnh" class="img-fluid rounded" style="width: 60px; height: 60px; object-fit: cover;" /></td>
                    <td>{{ row.tenSP }}</td>
                    <td>{{ row.maSPCT }}</td>
                    <td>{{ row.tenDanhMuc }}</td>
                    <td>{{ row.tenThuongHieu }}</td>
                    <td>{{ row.tenChatLieu }}</td>
                    <td>{{ row.giaBan != null ? row.giaBan.toLocaleString() : '0' }} VND</td>
                    <td>{{ row.soLuongTonKho }}</td>
                    <td>
                      <button type="button" class="btn btn-primary btn-sm" @click.stop="chonSanPham(row)">Chọn</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- Pagination -->
            <nav class="mt-3">
              <ul class="pagination justify-content-center">
                <li class="page-item" :class="{ disabled: listSanPham.number === 0 }">
                  <button class="page-link" @click="onProductPageChange(listSanPham.number)">Trước</button>
                </li>
                <li class="page-item">
                  <span class="page-link">{{ listSanPham.number + 1 }} / {{ Math.ceil(listSanPham.totalElements / listSanPham.size) }}</span>
                </li>
                <li class="page-item" :class="{ disabled: listSanPham.number + 1 >= Math.ceil(listSanPham.totalElements / listSanPham.size) }">
                  <button class="page-link" @click="onProductPageChange(listSanPham.number + 2)">Sau</button>
                </li>
                <li class="page-item disabled">
                  <span class="page-link">Tổng: {{ listSanPham.totalElements }}</span>
                </li>
              </ul>
            </nav>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="productSearchModal = false">Đóng</button>
            <button type="button" class="btn btn-primary" @click="taiLaiDanhSachSanPham">
              <i class="bi bi-arrow-clockwise"></i> Tải lại
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Chọn Sản Phẩm Chi Tiết -->
    <div v-if="productDetailModal" class="modal fade show d-block" tabindex="-1" style="background: rgba(0,0,0,0.6);">
      <div class="modal-dialog modal-xl">
        <div class="modal-content">
          <div class="modal-header bg-info text-white">
            <h5 class="modal-title">Chọn sản phẩm chi tiết - {{ selectedSanPham?.tenSP }}</h5>
            <button type="button" class="btn-close btn-close-white" @click="productDetailModal = false"></button>
          </div>
          <div class="modal-body">
            <div class="table-responsive" v-loading="productLoading">
              <table class="table table-striped table-hover">
                <thead>
                  <tr>
                    <th>Ảnh</th>
                    <th>Tên sản phẩm</th>
                    <th>Mã SPCT</th>
                    <th>Màu sắc</th>
                    <th>Kích thước</th>
                    <th>Giá</th>
                    <th>Tồn kho</th>
                    <th>Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="row in listSanPhamChiTiet.content" :key="row.id" @click="chonSanPhamChiTiet(row)">
                    <td><img :src="row.anhGiay" alt="Ảnh" class="img-fluid rounded" style="width: 60px; height: 60px; object-fit: cover;" /></td>
                    <td>{{ row.tenSP }}</td>
                    <td>{{ row.maSPCT }}</td>
                    <td>{{ row.tenMauSac }}</td>
                    <td>{{ row.tenKichThuoc }}</td>
                    <td>{{ row.giaBan != null ? row.giaBan.toLocaleString() : '0' }} VND</td>
                    <td>{{ row.soLuongTonKho }}</td>
                    <td>
                      <button type="button" class="btn btn-primary btn-sm" @click.stop="chonSanPhamChiTiet(row)">Chọn</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- Pagination -->
            <nav class="mt-3">
              <ul class="pagination justify-content-center">
                <li class="page-item" :class="{ disabled: listSanPhamChiTiet.number === 0 }">
                  <button class="page-link" @click="onProductDetailPageChange(listSanPhamChiTiet.number)">Trước</button>
                </li>
                <li class="page-item">
                  <span class="page-link">{{ listSanPhamChiTiet.number + 1 }} / {{ Math.ceil(listSanPhamChiTiet.totalElements / listSanPhamChiTiet.size) }}</span>
                </li>
                <li class="page-item" :class="{ disabled: listSanPhamChiTiet.number + 1 >= Math.ceil(listSanPhamChiTiet.totalElements / listSanPhamChiTiet.size) }">
                  <button class="page-link" @click="onProductDetailPageChange(listSanPhamChiTiet.number + 2)">Sau</button>
                </li>
                <li class="page-item disabled">
                  <span class="page-link">Tổng: {{ listSanPhamChiTiet.totalElements }}</span>
                </li>
              </ul>
            </nav>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="productDetailModal = false">Đóng</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Chọn Voucher Sản Phẩm -->
    <div v-if="voucherModal" class="modal fade show d-block" tabindex="-1" style="background: rgba(0,0,0,0.6);">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header bg-primary text-white">
            <h5 class="modal-title">Chọn Voucher cho {{ selectedProduct?.tenGiay }}</h5>
            <button type="button" class="btn-close btn-close-white" @click="voucherModal = false"></button>
          </div>
          <div class="modal-body" v-loading="voucherLoading">
            <div v-if="productVouchers.length === 0 && !voucherLoading" class="text-center py-3">
              <p>Không có voucher áp dụng được</p>
            </div>
            <div v-else-if="productVouchers.length > 0">
              <table class="table table-striped table-hover">
                <thead>
                  <tr>
                    <th>Tên Voucher</th>
                    <th>Giảm giá</th>
                    <th>Tối thiểu</th>
                    <th>Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  <tr
                    v-for="voucher in productVouchers"
                    :key="voucher.id"
                    @click="chonVoucherSanPham(selectedHoaDon, selectedProduct, voucher.id)"
                    style="cursor: pointer;"
                  >
                    <td>{{ voucher.tenVoucher }}</td>
                    <td>
                      {{ voucher.hinhThucGiam === '%' ? `${voucher.mucGiam}%` : `${voucher.giaGiam.toLocaleString()} VND` }}
                    </td>
                    <td>{{ voucher.giaTriDonHangToiThieu ? voucher.giaTriDonHangToiThieu.toLocaleString() : '0' }} VND</td>
                    <td>
                      <button
                        type="button"
                        class="btn btn-primary btn-sm"
                        @click.stop="chonVoucherSanPham(selectedHoaDon, selectedProduct, voucher.id)"
                      >
                        Chọn
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="voucherModal = false">Đóng</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Hóa Đơn -->
    <div v-if="modalHoaDon" class="modal fade show d-block" tabindex="-1" style="background: rgba(0,0,0,0.6);">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header bg-primary text-white">
            <h5 class="modal-title">Hóa đơn bán hàng</h5>
            <button type="button" class="btn-close btn-close-white" @click="modalHoaDon = false"></button>
          </div>
          <div class="modal-body">
            <div v-if="hoaDonIn">
              <div class="text-center">
                <h3>NEXVIBE</h3>
                <div>SĐT: 0123456789</div>
                <div>Email: nexvibeweb@gmail.com</div>
                <div>Địa chỉ: FPT POLYTECHNIC Cơ Sở Trịnh Văn Bô, Nam Từ Liêm, Hà Nội</div>
              </div>
              <h4 class="text-center mt-3">HÓA ĐƠN BÁN HÀNG</h4>
              <div>Mã hóa đơn: {{ hoaDonIn.maHoaDon }}</div>
              <div>Ngày tạo: {{ formatDate(hoaDonIn.ngayTao) }}</div>
              <div>Khách hàng: {{ hoaDonIn.khachHang || 'Khách lẻ' }}</div>
              <div v-if="hoaDonIn.thanhToan">Phương thức thanh toán: {{ hoaDonIn.thanhToan.phuongThuc }}</div>
              <div v-if="hoaDonIn.thanhToan && hoaDonIn.thanhToan.maGiaoDich">Mã giao dịch: {{ hoaDonIn.thanhToan.maGiaoDich }}</div>
              <table class="table table-striped mt-3">
                <thead>
                  <tr>
                    <th>Tên sản phẩm</th>
                    <th>Số lượng</th>
                    <th>Đơn giá</th>
                    <th>Thành tiền</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="sp in hoaDonIn.sanPhams" :key="sp.id">
                    <td>{{ sp.tenGiay }}</td>
                    <td>{{ sp.soLuong }}</td>
                    <td>{{ sp.giaBan != null ? sp.giaBan.toLocaleString() : '0' }} VND</td>
                    <td>{{ sp.thanhTien != null ? sp.thanhTien.toLocaleString() : '0' }} VND</td>
                  </tr>
                </tbody>
              </table>
              <div class="mt-3">
                <div>Tổng tiền hàng: {{ tinhTienHang(hoaDonIn).toLocaleString() }} VND</div>
                <div>Giảm giá: {{ tinhGiamGia(hoaDonIn).toLocaleString() }} VND</div>
                <div><b>Tổng tiền cần thanh toán: {{ tinhTongTien(hoaDonIn).toLocaleString() }} VND</b></div>
                <div v-if="hoaDonIn.thanhToan && hoaDonIn.thanhToan.soTien > tinhTongTien(hoaDonIn)">
                  <b>Tiền thừa trả lại: {{ (hoaDonIn.thanhToan.soTien - tinhTongTien(hoaDonIn)).toLocaleString() }} VND</b>
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="modalHoaDon = false">Đóng</button>
            <button type="button" class="btn btn-success" @click="taoHoaDonMoiNgay">Tạo hóa đơn mới</button>
            <button type="button" class="btn btn-primary" @click="inHoaDon">In hóa đơn</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Thanh Toán -->
    <div v-if="modalThanhToan" class="modal fade show d-block" tabindex="-1" style="background: rgba(0,0,0,0.6); z-index: 3000; pointer-events: auto;">
      <div class="modal-dialog" style="z-index: 3001; pointer-events: auto;">
        <div class="modal-content" style="position: relative; z-index: 3002; pointer-events: auto;">
          <div class="modal-header bg-warning text-white">
            <h5 class="modal-title">THANH TOÁN</h5>
            <button type="button" class="btn-close btn-close-white" @click="closeThanhToanModal"></button>
          </div>
          <div class="modal-body text-center">
            <div class="mb-3">
              <h6>Tổng tiền cần thanh toán</h6>
              <p class="fw-bold text-danger">{{ tinhTongTien(thanhToanHoaDon).toLocaleString() }} VND</p>
            </div>
            <div class="d-flex justify-content-center gap-2 mb-3">
              <button
                type="button"
                class="btn btn-outline-success"
                :class="{ active: thanhToanPhuongThuc === 'MOMO' }"
                @click="thanhToanPhuongThuc = 'MOMO'"
              >
                <i class="bi bi-qr-code"></i> MOMO
              </button>
              <button
                type="button"
                class="btn btn-outline-primary"
                :class="{ active: thanhToanPhuongThuc === 'TIỀN MẶT' }"
                @click="thanhToanPhuongThuc = 'TIỀN MẶT'"
              >
                <i class="bi bi-cash"></i> TIỀN MẶT
              </button>
            </div>
            <!-- MoMo QR Payment Section -->
            <div v-if="thanhToanPhuongThuc === 'MOMO'" class="mt-4 mb-4">
              <div class="text-center">
                <div class="bg-light p-4 rounded mb-3">
                  <h6 class="text-muted mb-2">
                    <i class="bi bi-qr-code me-2"></i>Thanh toán bằng MoMo
                  </h6>
                  <p class="mb-0 small text-muted">
                    Nhấn nút để mở trang thanh toán MoMo trong tab mới
                  </p>
                </div>
                <div v-if="momoLoading">
                  <div class="spinner-border text-success" role="status">
                    <span class="visually-hidden">Đang tạo thanh toán...</span>
                  </div>
                  <p class="mt-2 text-muted">Đang tạo thanh toán MoMo...</p>
                </div>
                <div v-else>
                  <button 
                    type="button" 
                    class="btn btn-success btn-lg px-5"
                    @click="openMoMoPayment"
                  >
                    <i class="bi bi-qr-code me-2"></i>Thanh toán bằng MoMo
                  </button>
                </div>
              </div>
            </div>
            <!-- Tiền mặt -->
            <div v-if="thanhToanPhuongThuc === 'TIỀN MẶT'" class="mt-4 mb-4">
              <div class="mb-3">
                <label class="form-label">Số tiền khách đưa</label>
                <input
                  type="number"
                  class="form-control text-center"
                  v-model.number="thanhToanTienKhachDua"
                  @input="validateTienKhachDua"
                />
              </div>
              <div v-if="thanhToanTienKhachDua > tinhTongTien(thanhToanHoaDon)" class="mb-3">
                <label class="form-label">Tiền trả lại</label>
                <p class="fw-bold text-success">
                  {{ (thanhToanTienKhachDua - tinhTongTien(thanhToanHoaDon)).toLocaleString('vi-VN') }} VND
                </p>
              </div>
            </div>
          </div>
          <div class="modal-footer" style="position: relative; z-index: 3003; pointer-events: auto;">
            <button type="button" class="btn btn-secondary" @click="closeThanhToanModal">
              Hủy
            </button>
            <button
              type="button"
              class="btn btn-success"
              :disabled="thanhToanPhuongThuc === 'TIỀN MẶT' && thanhToanTienKhachDua < tinhTongTien(thanhToanHoaDon)"
              @click="xacNhanThanhToan"
            >
              Xác nhận
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Chọn Khách Hàng -->
    <div v-if="modalCustomer" class="modal fade show d-block" tabindex="-1" style="background: rgba(0,0,0,0.6);">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header bg-info text-white">
            <h5 class="modal-title">Chọn khách hàng</h5>
            <button type="button" class="btn-close btn-close-white" @click="modalCustomer = false"></button>
          </div>
          <div class="modal-body">
            <div class="input-group mb-3">
              <input
                v-model="customerSearch"
                type="text"
                class="form-control"
                placeholder="Tìm kiếm theo số điện thoại hoặc họ tên..."
                @input="debounceSearchCustomers"
              />
              <button class="btn btn-outline-secondary" type="button" @click="searchCustomers">
                <i class="bi bi-search"></i>
              </button>
            </div>
            <div class="table-responsive" style="max-height: 400px;">
              <div v-if="loadingCustomers" class="text-center py-3">
                <i class="bi bi-hourglass-split fs-1 text-primary"></i>
                <p>Đang tải danh sách khách hàng...</p>
              </div>
              <div v-else-if="filteredCustomers.length === 0" class="text-center py-3">
                <i class="bi bi-exclamation-triangle fs-1 text-warning"></i>
                <p>Không tìm thấy khách hàng</p>
              </div>
              <table v-else class="table table-striped table-hover">
                <thead>
                  <tr>
                    <th class="text-center">STT</th>
                    <th>Họ tên</th>
                    <th>Số điện thoại</th>
                    <th>Giới tính</th>
                    <th>Trạng thái</th>
                    <th class="text-center">Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(customer, index) in filteredCustomers" :key="customer.id">
                    <td class="text-center">{{ (currentPageCustomer - 1) * pageSizeCustomer + index + 1 }}</td>
                    <td>{{ customer.tenKH }}</td>
                    <td>{{ customer.sdt }}</td>
                    <td>{{ customer.gioiTinh ? 'Nam' : 'Nữ' }}</td>
                    <td>
                      <span v-if="customer.trangThai" class="badge bg-success">Hoạt động</span>
                      <span v-else class="badge bg-secondary">Ngừng hoạt động</span>
                    </td>
                    <td class="text-center">
                      <button
                        class="btn btn-primary btn-sm"
                        @click="selectCustomer(customer, selectedHoaDon)"
                      >
                        CHỌN
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div class="d-flex justify-content-between align-items-center mt-3" v-if="!loadingCustomers && totalCustomers > 0">
              <span>Trang {{ currentPageCustomer }} / {{ Math.ceil(totalCustomers / pageSizeCustomer) }}</span>
              <div>
                <button
                  class="btn btn-outline-secondary btn-sm me-2"
                  :disabled="currentPageCustomer <= 1"
                  @click="fetchCustomers(currentPageCustomer - 2)"
                >
                  Trước
                </button>
                <button
                  class="btn btn-outline-secondary btn-sm"
                  :disabled="currentPageCustomer >= Math.ceil(totalCustomers / pageSizeCustomer)"
                  @click="fetchCustomers(currentPageCustomer)"
                >
                  Sau
                </button>
              </div>
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
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';

export default {
  name: 'BanHangTaiQuay',
  setup() {
    const auth = useAuthStore();
    const router = useRouter();
    return { auth, router };
  },
  data() {
    return {
      hoaDons: [],
      tabActive: '',
      productSearchModal: false,
      productSearchQuery: '',
      productLoading: false,
      listSanPham: { content: [], number: 0, size: 5, totalElements: 0 },
      hoaDonChon: null,
      productDetailModal: false,
      listSanPhamChiTiet: { content: [], number: 0, size: 5, totalElements: 0 },
      selectedSanPham: null,
      vouchers: [],
      modalHoaDon: false,
      hoaDonIn: null,
      modalThanhToan: false,
      thanhToanHoaDon: null,
      thanhToanTongTien: 0,
      thanhToanPhuongThuc: 'CHUYỂN KHOẢN',
      thanhToanTienKhachDua: 0,
      thanhToanMaGiaoDich: '',
      thanhToanLichSu: [],
      phuongThucThanhToans: [],
      modalCustomer: false,
      customerSearch: '',
      selectedHoaDon: null,
      customers: [],
      loadingCustomers: false,
      currentPageCustomer: 1,
      pageSizeCustomer: 10,
      totalCustomers: 0,
      categories: [],
      brands: [],
      colors: [],
      materials: [],
      priceRange: [0, 5000000],
      selectedCategory: '',
      selectedBrand: '',
      selectedColor: '',
      selectedMaterial: '',
      voucherModal: false,
      selectedProduct: null,
      productVouchers: [],
      pageSanPham: 1,
      pageSizeSanPham: 5,
      pageSanPhamChiTiet: 1,
      pageSizeSanPhamChiTiet: 5,
      cleanupInterval: null,
      timeoutIds: new Map(),
      momoCheckInterval: null,
      momoLoading: false,
      momoTransactionStatus: null,
      momoOrderId: null,
      momoQrCodeUrl: null,
      momoPayUrl: null,
      applicableVouchers: [],
      selectedVoucherId: null,
      voucherLoading: false,
    };
  },
  created() {
    this.checkAuth();
    this.hoaDons = [];
    this.tabActive = '';
    this.fetchCategories();
    this.fetchBrands();
    this.fetchColors();
    this.fetchMaterials();
    this.fetchPhuongThucThanhToan();
    this.fetchPendingInvoices();
    
    const momoOrderId = localStorage.getItem('momo_last_order_id');
    if (momoOrderId) {
      console.log(`[CREATED] Tìm thấy momo_last_order_id: ${momoOrderId}`);
      ElMessage.info('Đang kiểm tra trạng thái thanh toán MoMo...');
      localStorage.removeItem('momo_last_order_id');
      this.handleMoMoReturn(momoOrderId);
    }

    // Khởi tạo interval để cập nhật countdown và xóa hóa đơn
    this.startInvoiceCleanup();
  },
  beforeUnmount() {
    this.timeoutIds.forEach((timeoutId) => clearTimeout(timeoutId));
    this.timeoutIds.clear();
    this.stopCheckingMomoStatus();
  },
  computed: {
    tongTienDaThanhToan() {
      return this.thanhToanLichSu.reduce((sum, t) => sum + (Number(t.soTien) || 0), 0);
    },
    filteredCustomers() {
      return this.customers;
    },
  },
  methods: {
    async openMoMoPayment() {
      console.log('[MOMO] Bắt đầu tạo thanh toán MoMo.');
      
      if (!this.thanhToanHoaDon.sanPhams || this.thanhToanHoaDon.sanPhams.length === 0) {
        ElMessage.error('Hóa đơn không có sản phẩm để thanh toán!');
        return;
      }

      try {
        this.momoLoading = true;
        const orderId = `POS${Date.now()}${Math.floor(Math.random() * 1000)}`;
        this.momoOrderId = orderId;

        localStorage.setItem('momo_pending_hoadon', JSON.stringify(this.thanhToanHoaDon));
        localStorage.setItem('momo_last_order_id', orderId);
        localStorage.setItem('pos_return_url', '/admin/pos');

        const requestData = {
          orderId: orderId,
          amount: this.tinhTongTien(this.thanhToanHoaDon),
          orderInfo: `Thanh toán đơn hàng ${this.thanhToanHoaDon.maHoaDon}`,
          extraData: JSON.stringify({
            hoaDonId: this.thanhToanHoaDon.maHoaDon,
            type: 'POS'
          }),
          redirectUrl: `${window.location.origin}/momo-return`,
          ipnUrl: `http://localhost:8080/admin/api/momo/notify`
        };

        const response = await axios.post('http://localhost:8080/admin/api/momo/create-payment', requestData, {
          withCredentials: true,
          headers: { 'Content-Type': 'application/json' }
        });

        if (String(response.data.resultCode) === '0') {
          console.log('[MOMO] Tạo payment thành công, mở tab mới...');
          window.open(response.data.payUrl, '_blank');
          this.startCheckingMomoStatus(orderId);
          this.modalThanhToan = false;
          ElMessage.info('Đã mở trang thanh toán MoMo. Vui lòng quét mã QR để thanh toán.');
        } else {
          throw new Error(response.data.message || 'Không thể tạo thanh toán MoMo từ backend');
        }
      } catch (error) {
        console.error('[MOMO] Lỗi khi tạo thanh toán:', error);
        ElMessage.error('Không thể tạo thanh toán MoMo: ' + (error.response?.data?.message || error.message));
      } finally {
        this.momoLoading = false;
      }
    },

    startCheckingMomoStatus(orderId) {
      console.log('[MOMO] Bắt đầu kiểm tra trạng thái MoMo với orderId:', orderId);
      this.momoTransactionStatus = 'PENDING';
      this.momoCheckInterval = setInterval(async () => {
        try {
          const response = await axios.get(`http://localhost:8080/admin/api/momo/check-status/${orderId}`, {
            withCredentials: true
          });

          if (response.data.transactionStatus === 'SUCCESS') {
            console.log('[MOMO] Thanh toán thành công!');
            clearInterval(this.momoCheckInterval);
            this.momoTransactionStatus = 'SUCCESS';
            this.thanhToanHoaDon.thanhToan = {
              phuongThuc: 'MOMO',
              soTien: this.tinhTongTien(this.thanhToanHoaDon),
              maGiaoDich: response.data.transId || orderId,
              idPT: this.phuongThucThanhToans.find(pt => pt.ten === 'CHUYỂN KHOẢN')?.idPTT || 1,
            };
            ElMessage.success('Thanh toán MoMo thành công! Đang lưu hóa đơn...');
            await this.xacNhanThanhToan();
            localStorage.removeItem('momo_pending_hoadon');
            localStorage.removeItem('momo_last_order_id');
          } else if (response.data.transactionStatus === 'FAILED') {
            console.log('[MOMO] Thanh toán thất bại.');
            clearInterval(this.momoCheckInterval);
            this.momoTransactionStatus = 'FAILED';
            ElMessage.error('Thanh toán MoMo thất bại!');
            this.thanhToanHoaDon.thanhToan = null;
            localStorage.removeItem('momo_pending_hoadon');
            localStorage.removeItem('momo_last_order_id');
          }
        } catch (error) {
          console.error('[MOMO] Lỗi khi kiểm tra trạng thái:', error);
        }
      }, 3000);
      setTimeout(() => {
        if (this.momoCheckInterval) {
          clearInterval(this.momoCheckInterval);
          this.momoTransactionStatus = 'TIMEOUT';
          ElMessage.warning('Hết thời gian kiểm tra thanh toán MoMo.');
          this.thanhToanHoaDon.thanhToan = null;
          localStorage.removeItem('momo_pending_hoadon');
          localStorage.removeItem('momo_last_order_id');
        }
      }, 300000);
    },

    async xacNhanThanhToan() {
      try {
        console.log('Bắt đầu xác nhận thanh toán:', this.thanhToanHoaDon.maHoaDon);

        // Kiểm tra danh sách phương thức thanh toán
        if (!this.phuongThucThanhToans || this.phuongThucThanhToans.length === 0) {
          throw new Error('Danh sách phương thức thanh toán rỗng! Vui lòng kiểm tra API /phuongthucthanhtoan.');
        }

        // Kiểm tra số lượng tồn kho trước khi xác nhận thanh toán
        for (const sp of this.thanhToanHoaDon.sanPhams) {
          const response = await axios.get(`http://localhost:8080/admin/api/sanphamchitiet/${sp.id}`, {
            withCredentials: true,
          });
          const sanPhamChiTiet = response.data;
          console.log(`Kiểm tra tồn kho sản phẩm ${sp.tenGiay}: Còn lại ${sanPhamChiTiet.soLuong}, cần ${sp.soLuong}`);
          if (sanPhamChiTiet.soLuong < sp.soLuong) {
            throw new Error(`Số lượng tồn kho không đủ cho sản phẩm ${sp.tenGiay}! Còn lại: ${sanPhamChiTiet.soLuong}`);
          }
        }

        // Chuẩn bị chi tiết sản phẩm
        const sanPhamChiTietPromises = this.thanhToanHoaDon.sanPhams.map(sp =>
          axios.get(`http://localhost:8080/admin/api/sanphamchitiet/${sp.id}`, {
            withCredentials: true,
          }).then(response => ({
            sp,
            sanPhamChiTiet: response.data,
          }))
        );
        const sanPhamChiTietResults = await Promise.all(sanPhamChiTietPromises);

        this.thanhToanHoaDon.chiTietSanPham = sanPhamChiTietResults.map(({ sp, sanPhamChiTiet }) => ({
          idHDCT: sp.idHDCT || null,
          idCtSanPham: sp.id,
          idSP: sp.idSP,
          maSPCT: sp.maCtSanPham,
          tenSanPham: sp.tenGiay,
          donGia: Number(sp.giaBan.toFixed(2)),
          soLuong: Number(sp.soLuong),
          thanhTien: Number(sp.thanhTien.toFixed(2)),
          tenSize: sp.tenKichThuoc,
          tenMauSac: sp.tenMauSac,
          tenThuongHieu: sp.tenThuongHieu,
          tenChatLieu: sp.tenChatLieu,
          anhGiay: sp.anhGiay,
          soLuongTonKho: Number(sanPhamChiTiet.soLuong),
          ngayTao: sp.ngayTao || new Date().toISOString(),
          idVoucher: sp.selectedVoucherId || null,
        }));

        // Thiết lập thông tin thanh toán
        let phuongThucForId = this.thanhToanPhuongThuc;
        if (this.thanhToanPhuongThuc === 'MOMO') {
          phuongThucForId = 'CHUYỂN KHOẢN';
        }
        const selectedPhuongThuc = this.phuongThucThanhToans.find(pt => pt.ten === phuongThucForId);
        if (!selectedPhuongThuc) {
          throw new Error(`Phương thức thanh toán '${phuongThucForId}' không tồn tại trong danh sách phương thức!`);
        }

        const thanhToanInfo = {
          phuongThuc: this.thanhToanPhuongThuc,
          soTien: this.thanhToanPhuongThuc === 'TIỀN MẶT' ? this.thanhToanTienKhachDua : this.thanhToanTongTien,
          maGiaoDich: this.thanhToanMaGiaoDich || null,
          ngayThanhToan: new Date().toISOString(),
          idPT: selectedPhuongThuc.idPTT,
        };
        console.log('Thông tin thanh toán:', JSON.stringify(thanhToanInfo, null, 2));

        // Gán thông tin thanh toán và trạng thái vào hóa đơn
        this.thanhToanHoaDon.thanhToan = thanhToanInfo;
        this.thanhToanHoaDon.trangThai = 'HOAN_THANH';
        this.thanhToanHoaDon.maGiaoDich = thanhToanInfo.maGiaoDich;
        this.thanhToanHoaDon.idPT = selectedPhuongThuc.idPTT;

        // Cập nhật tồn kho sản phẩm
        for (const sp of this.thanhToanHoaDon.sanPhams) {
          try {
            const productResponse = await axios.get(`http://localhost:8080/admin/api/sanphamchitiet/${sp.id}`, {
              withCredentials: true,
            });
            const currentProduct = productResponse.data;
            await axios.put(`http://localhost:8080/admin/api/sanphamchitiet/${sp.id}`, {
              soLuong: currentProduct.soLuong - sp.soLuong,
              maSPCT: currentProduct.maSPCT,
              gia: currentProduct.gia,
              moTa: currentProduct.moTa,
              tenSP: currentProduct.tenSP,
              tenDanhMuc: currentProduct.tenDanhMuc,
              tenThuongHieu: currentProduct.tenThuongHieu,
              tenMauSac: currentProduct.tenMauSac,
              tenChatLieu: currentProduct.tenChatLieu,
              tenKichThuoc: currentProduct.tenKichThuoc,
              trangThai: currentProduct.trangThai,
              anhGiay: currentProduct.anhGiay,
            }, {
              withCredentials: true,
              headers: { 'Content-Type': 'application/json; charset=UTF-8' },
            });
            console.log(`Đã cập nhật tồn kho sản phẩm ${sp.tenGiay}: Giảm ${sp.soLuong}`);
          } catch (error) {
            console.error(`Lỗi cập nhật sản phẩm ${sp.id}:`, error);
            ElMessage.error(`Không thể cập nhật tồn kho cho sản phẩm ${sp.tenGiay}`);
            throw error;
          }
        }

        // Cập nhật số lượng voucher
        for (const sp of this.thanhToanHoaDon.sanPhams) {
          if (sp.selectedVoucherId && sp.selectedVoucher) {
            try {
              const newSoLuong = Number(sp.selectedVoucher.soLuong) - 1;
              await axios.put(`http://localhost:8080/api/voucher/${sp.selectedVoucherId}`, { soLuong: newSoLuong }, {
                withCredentials: true,
                headers: { 'Content-Type': 'application/json; charset=UTF-8' },
              });
              console.log(`Đã cập nhật số lượng voucher ${sp.selectedVoucher.tenVoucher}: Còn ${newSoLuong}`);
            } catch (error) {
              console.error(`Lỗi cập nhật voucher ${sp.selectedVoucherId}:`, error);
              ElMessage.error(`Không thể cập nhật số lượng voucher ${sp.selectedVoucher.tenVoucher}`);
              throw error;
            }
          }
        }

        // Cập nhật voucher hóa đơn nếu có
        if (this.thanhToanHoaDon.idKM) {
          try {
            const voucherResponse = await axios.get(`http://localhost:8080/api/voucher/${this.thanhToanHoaDon.idKM}`, {
              withCredentials: true,
            });
            const newSoLuong = Number(voucherResponse.data.soLuong) - 1;
            await axios.put(`http://localhost:8080/api/voucher/${this.thanhToanHoaDon.idKM}`, { soLuong: newSoLuong }, {
              withCredentials: true,
              headers: { 'Content-Type': 'application/json; charset=UTF-8' },
            });
            console.log(`Đã cập nhật số lượng voucher hóa đơn ${voucherResponse.data.tenVoucher}: Còn ${newSoLuong}`);
          } catch (error) {
            console.error(`Lỗi cập nhật voucher hóa đơn ${this.thanhToanHoaDon.idKM}:`, error);
            ElMessage.error('Không thể cập nhật số lượng voucher hóa đơn');
            throw error;
          }
        }

        // Gọi API cập nhật hóa đơn
        await this.capNhatHoaDon(this.thanhToanHoaDon, 'HOAN_THANH');

        // Lưu giao dịch MoMo nếu sử dụng MoMo
        if (this.thanhToanPhuongThuc === 'MOMO' && this.momoOrderId) {
          try {
            const momoData = {
              idHD: this.thanhToanHoaDon.idHD,
              orderId: this.momoOrderId,
              requestId: this.momoOrderId,
              amount: this.tinhTongTien(this.thanhToanHoaDon),
              orderInfo: `Thanh toán đơn hàng ${this.thanhToanHoaDon.maHoaDon}`,
              transactionStatus: 'SUCCESS',
              message: 'Thanh toán thành công',
              localMessage: 'Thanh toán thành công',
              responseCode: '0',
            };
            await axios.post('http://localhost:8080/admin/api/momo/save-transaction', momoData, {
              withCredentials: true,
              headers: { 'Content-Type': 'application/json' },
            });
            console.log('Đã lưu giao dịch MoMo.');
          } catch (momoError) {
            console.error('Lỗi lưu giao dịch MoMo:', momoError);
            ElMessage.error('Không thể lưu giao dịch MoMo');
          }
        }

        // Cập nhật giao diện
        this.thanhToanLichSu.push(thanhToanInfo);
        this.modalThanhToan = false;
        this.hoaDonIn = { ...this.thanhToanHoaDon };
        this.modalHoaDon = true;
        this.stopCheckingMomoStatus();
        localStorage.removeItem('momo_last_order_id');
        localStorage.removeItem('momo_pending_hoadon');
        this.hoaDons = this.hoaDons.filter(hd => hd.maHoaDon !== this.thanhToanHoaDon.maHoaDon);
        this.tabActive = this.hoaDons.length > 0 ? this.hoaDons[0].maHoaDon : '';
        if (this.hoaDonChon && this.hoaDonChon.maHoaDon === this.thanhToanHoaDon.maHoaDon) {
          this.hoaDonChon = null;
        }
        ElMessage.success('Thanh toán thành công!');
        await this.fetchSanPhamList();
      } catch (error) {
        console.error('xacNhanThanhToan:', error);
        ElMessage.error('Không thể xác nhận thanh toán: ' + (error.response?.data?.message || error.message));
      }
    },

    startInvoiceCleanup() {
      this.cleanupInterval = setInterval(() => {
        this.hoaDons = this.hoaDons.map(hd => {
          if (hd.trangThai === 'CHO_THANH_TOAN' && hd.countdown > 0) {
            hd.countdown -= 1;
          }
          return hd;
        }).filter(hd => {
          if (hd.trangThai === 'CHO_THANH_TOAN' && hd.countdown <= 0) {
            try {
              axios.delete(`http://localhost:8080/admin/api/hoadon/${hd.idHD}`, {
                withCredentials: true,
              });
              ElMessage.info(`Hóa đơn ${hd.maHoaDon} đã bị xóa do hết thời gian.`);
            } catch (error) {
              console.error(`Lỗi khi xóa hóa đơn ${hd.maHoaDon}:`, error.response?.data || error.message);
              ElMessage.error(`Không thể xóa hóa đơn ${hd.maHoaDon}: ${error.response?.data?.message || error.message}`);
            }
            return false;
          }
          return true;
        });

        if (this.hoaDons.length > 0 && !this.hoaDons.some(hd => hd.maHoaDon === this.tabActive)) {
          this.tabActive = this.hoaDons[0].maHoaDon;
        } else if (this.hoaDons.length === 0) {
          this.tabActive = '';
        }
        this.$forceUpdate();
      }, 1000);
    },
    stopCheckingMomoStatus() {
      if (this.momoCheckInterval) {
        clearInterval(this.momoCheckInterval);
        this.momoCheckInterval = null;
      }
    },
    formatCountdown(seconds) {
      if (seconds <= 0) return '00:00';
      const minutes = Math.floor(seconds / 60);
      const secs = seconds % 60;
      return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    },
    async checkAuth() {
      try {
        console.log('checkAuth: Kiểm tra trạng thái đăng nhập');
        await this.auth.checkAuth();
        if (!this.auth.isAuthenticated || !this.auth.user || !this.auth.isAdmin()) {
          ElMessage.error('Vui lòng đăng nhập với tài khoản admin!');
          this.router.push('/login');
        }
      } catch (error) {
        console.error('checkAuth: Lỗi kiểm tra đăng nhập -', error.response?.data || error.message);
        ElMessage.error('Lỗi kiểm tra đăng nhập, vui lòng đăng nhập lại!');
        this.router.push('/login');
      }
    },
    async fetchPendingInvoices() {
  try {
    const response = await axios.get('http://localhost:8080/admin/api/hoadon', {
      params: {
        trangThai: 'CHO_THANH_TOAN,DA_THANH_TOAN'
      },
      withCredentials: true,
      paramsSerializer: params => {
        return Object.entries(params)
          .filter(([_, value]) => value != null)
          .map(([key, value]) => `${key}=${encodeURIComponent(value)}`)
          .join('&');
      },
    });
    console.log('Raw API response:', JSON.stringify(response.data, null, 2));
    this.hoaDons = response.data.content
      .filter(hd => {
        if (!hd.idHD || isNaN(hd.idHD)) {
          console.warn(`Hóa đơn không hợp lệ, thiếu idHD:`, hd);
          return false;
        }
        return ['CHO_THANH_TOAN', 'DA_THANH_TOAN'].includes(hd.trangThai);
      })
      .map(hd => {
        const createdAt = new Date(hd.ngayTao).getTime();
        const now = Date.now();
        const elapsedSeconds = Math.floor((now - createdAt) / 1000);
        const initialCountdown = 600;
        const countdown = hd.trangThai === 'CHO_THANH_TOAN' ? Math.max(0, initialCountdown - elapsedSeconds) : 0;
        const sanPhams = (hd.chiTietSanPham || []).map(async ct => {
          if (!ct.idCtSanPham || !ct.maSPCT || !ct.tenSanPham) {
            console.warn(`Dữ liệu chi tiết sản phẩm không hợp lệ cho hóa đơn ${hd.maHD}:`, ct);
            return null;
          }
          let voucher = null;
          if (ct.idVoucher) {
            try {
              // Gọi API để lấy thông tin voucher
              const voucherResponse = await axios.get(`http://localhost:8080/api/voucher/${ct.idVoucher}`, {
                withCredentials: true,
              });
              voucher = {
                id: ct.idVoucher,
                tenVoucher: voucherResponse.data.tenVoucher || 'Unknown Voucher',
                hinhThucGiam: voucherResponse.data.hinhThucGiam || 'FIXED',
                mucGiam: Number(voucherResponse.data.mucGiam) || 0,
                giaGiam: Number(voucherResponse.data.giaGiam) || 0,
                giaTriDonHangToiThieu: Number(voucherResponse.data.giaTriDonHangToiThieu) || 0,
                soLuong: Number(voucherResponse.data.soLuong) || 0,
              };
            } catch (error) {
              console.error(`Lỗi khi lấy thông tin voucher ${ct.idVoucher}:`, error.response?.data || error.message);
            }
          }
          let thanhTien = parseFloat(ct.thanhTien) || (parseFloat(ct.donGia) * Number(ct.soLuong));
          if (voucher && thanhTien === ct.donGia * ct.soLuong) {
            if (voucher.hinhThucGiam === '%') {
              thanhTien -= (thanhTien * voucher.mucGiam) / 100;
            } else {
              thanhTien -= voucher.giaGiam;
            }
            thanhTien = Math.max(thanhTien, 0);
          }
          return {
            id: ct.idCtSanPham,
            idSP: ct.idSP,
            idHDCT: ct.idHDCT || null,
            maCtSanPham: ct.maSPCT,
            tenGiay: ct.tenSanPham,
            giaBan: parseFloat(ct.donGia) || 0,
            soLuong: Number(ct.soLuong) || 1,
            soLuongTonKho: Number(ct.soLuongTonKho) || 0,
            thanhTien: thanhTien,
            tenKichThuoc: ct.tenSize || 'N/A',
            tenMauSac: ct.tenMauSac || 'N/A',
            tenThuongHieu: ct.tenThuongHieu || 'N/A',
            tenChatLieu: ct.tenChatLieu || 'N/A',
            anhGiay: ct.anhGiay || 'https://placehold.co/60x60',
            selectedVoucherId: ct.idVoucher || null,
            selectedVoucher: voucher,
          };
        });
        // Chờ tất cả các promise trong sanPhams hoàn thành
        return Promise.all(sanPhams).then(resolvedSanPhams => {
          const thanhToan = hd.thanhToan
            ? {
                phuongThuc: hd.thanhToan.phuongThuc || 'N/A',
                soTien: parseFloat(hd.thanhToan.soTien) || 0,
                maGiaoDich: hd.thanhToan.maGiaoDich || '',
                idPT: hd.thanhToan.idPT || null,
              }
            : null;
          console.log(`Hóa đơn ${hd.maHD} thanhToan:`, thanhToan);
          return {
            idHD: hd.idHD,
            maHoaDon: hd.maHD,
            sanPhams: resolvedSanPhams.filter(sp => sp !== null),
            countdown,
            createdAt,
            ngayTao: hd.ngayTao,
            ngaySua: hd.ngaySua,
            tongTien: parseFloat(hd.tongTien) || 0,
            trangThai: hd.trangThai,
            loaiHoaDon: hd.loaiHoaDon,
            totalProducts: hd.totalProducts || resolvedSanPhams.filter(sp => sp !== null).length,
            idKhachHang: hd.idKhachHang || null,
            khachHang: hd.customerName || 'Khách lẻ',
            idNhanVien: hd.idNhanVien,
            idKM: hd.idKM || null,
            idPT: hd.idPT,
            maGiaoDich: hd.maGiaoDich,
            thanhToan,
          };
        });
      });
    // Chờ tất cả các hóa đơn được ánh xạ
    this.hoaDons = await Promise.all(this.hoaDons);
    console.log('Danh sách hóa đơn sau ánh xạ:', JSON.stringify(this.hoaDons, null, 2));
    if (this.hoaDons.length === 0) {
      ElMessage.warning('Không tìm thấy hóa đơn đang chờ hoặc đã thanh toán.');
    } else if (this.hoaDons.length > 0 && !this.tabActive) {
      this.tabActive = this.hoaDons[0].maHoaDon;
      if (this.hoaDons[0].idKhachHang) {
        await this.fetchApplicableVouchers(this.hoaDons[0].idKhachHang);
        this.selectedVoucherId = this.hoaDons[0].idKM || null;
      }
    }
  } catch (error) {
    console.error('Lỗi khi tải hóa đơn:', error.response?.data || error.message);
    ElMessage.error('Không thể tải danh sách hóa đơn!');
  }
},
    async taoHoaDon() {
      try {
        await this.checkAuth();
        if (!this.auth.isAuthenticated || !this.auth.user?.idNV || !this.auth.isAdmin()) {
          ElMessage.error('Vui lòng đăng nhập với tài khoản admin!');
          this.router.push('/login');
          return;
        }
        if (!this.auth.user.idNV || isNaN(this.auth.user.idNV)) {
          ElMessage.error('ID nhân viên không hợp lệ!');
          return;
        }
        const hoaDonDTO = {
          maHD: `HD${Date.now()}${Math.floor(Math.random() * 1000)}`,
          idNhanVien: Number(this.auth.user.idNV),
          idKhachHang: null,
          idPT: null,
          idKM: null,
          ngayTao: new Date().toISOString(),
          ngaySua: new Date().toISOString(),
          trangThai: 'CHO_THANH_TOAN',
          loaiHoaDon: 'Tại quầy',
          tongTien: 0,
          chiTietSanPham: [],
          maGiaoDich: null,
        };
        console.log('Tạo hóa đơn mới:', JSON.stringify(hoaDonDTO, null, 2));
        const response = await axios.post('http://localhost:8080/admin/api/hoadon', hoaDonDTO, {
          withCredentials: true,
          headers: { 'Content-Type': 'application/json; charset=UTF-8' },
        });
        console.log('Phản hồi từ POST /hoadon:', JSON.stringify(response.data, null, 2));
        if (!response.data.idHD || isNaN(response.data.idHD)) {
          console.error('Lỗi: Phản hồi API không chứa idHD hợp lệ:', response.data);
          ElMessage.error('Không thể tạo hóa đơn: ID hóa đơn không hợp lệ từ server!');
          return;
        }
        const hd = {
          idHD: response.data.idHD,
          maHoaDon: response.data.maHD,
          sanPhams: [],
          khachHang: response.data.customerName || 'Khách lẻ',
          idKhachHang: response.data.idKhachHang || null,
          ngayTao: response.data.ngayTao,
          createdAt: new Date().getTime(),
          countdown: 600,
          thanhToan: null,
          trangThai: response.data.trangThai,
          idNhanVien: response.data.idNhanVien,
        };
        this.hoaDons.push(hd);
        this.tabActive = hd.maHoaDon;
        this.hoaDonChon = hd;
        console.log('Hóa đơn đã chọn:', JSON.stringify(this.hoaDonChon, null, 2));
        ElMessage.success('Tạo hóa đơn mới thành công!');
      } catch (error) {
        console.error('Lỗi khi tạo hóa đơn:', error.response?.data || error.message);
        let errorMessage = 'Không thể tạo hóa đơn!';
        if (error.response) {
          if (error.response.status === 401 || error.response.status === 403) {
            errorMessage = 'Phiên đăng nhập hết hạn hoặc không có quyền truy cập!';
            this.router.push('/login');
          } else if (error.response.status === 400) {
            errorMessage = error.response.data?.message || error.response.data?.error || 'Dữ liệu gửi lên không hợp lệ!';
          } else if (error.response.data?.message) {
            errorMessage = error.response.data.message;
          }
        } else if (error.message) {
          errorMessage = error.message;
        }
        ElMessage.error(errorMessage);
      }
    },
    async fetchSanPhamList() {
      this.productLoading = true;
      try {
        await this.checkAuth();
        const params = {
          page: this.pageSanPham - 1,
          size: this.pageSizeSanPham,
          tenSP: this.productSearchQuery || '',
          danhMuc: this.selectedCategory ? [this.selectedCategory] : null,
          thuongHieu: this.selectedBrand ? [this.selectedBrand] : null,
          chatLieu: this.selectedMaterial ? [this.selectedMaterial] : null,
          minPrice: Number(this.priceRange[0]) || 0,
          maxPrice: Number(this.priceRange[1]) || 5000000,
        };
        console.log('Tham số gửi API:', JSON.stringify(params, null, 2));
        const response = await axios.get('http://localhost:8080/admin/api/san-pham', {
          params,
          withCredentials: true,
          paramsSerializer: params => {
            return Object.entries(params)
              .filter(([_, value]) => value != null)
              .map(([key, value]) => {
                if (Array.isArray(value)) {
                  return value.map(v => `${key}=${encodeURIComponent(v)}`).join('&');
                }
                return `${key}=${encodeURIComponent(value)}`;
              })
              .join('&');
          },
        });
        console.log('Dữ liệu API sản phẩm:', JSON.stringify(response.data, null, 2));
        this.listSanPham = {
          content: response.data.content.map(item => ({
            id: item.idSP || null,
            maSPCT: item.maSP || 'N/A',
            maCtSanPham: item.maSP || 'N/A',
            tenSP: item.tenSP || 'Không xác định',
            tenGiay: item.tenSP || 'Không xác định',
            giaBan: item.gia != null ? Number(item.gia) : 0,
            soLuongTonKho: Number(item.tongSoLuongSanPham) || 0,
            moTa: item.moTa || '',
            trangThai: item.trangThai != null ? item.trangThai : true,
            tenDanhMuc: item.idDanhMuc ? this.categories.find(cat => cat.value === item.idDanhMuc)?.label || 'N/A' : 'N/A',
            tenThuongHieu: item.idThuongHieu ? this.brands.find(brand => brand.value === item.idThuongHieu)?.label || 'N/A' : 'N/A',
            tenChatLieu: item.idChatLieu ? this.materials.find(mat => mat.value === item.idChatLieu)?.label || 'N/A' : 'N/A',
            anhGiay: item.imageLink || 'https://placehold.co/60x60',
          })),
          number: response.data.number || 0,
          size: response.data.size || 5,
          totalElements: response.data.totalElements || 0,
        };
        if (this.listSanPham.totalElements === 0) {
          ElMessage.warning('Không tìm thấy sản phẩm nào phù hợp.');
        }
      } catch (error) {
        console.error('Lỗi khi lấy danh sách sản phẩm:', error.response?.data || error.message);
        this.listSanPham = { content: [], number: 0, size: 5, totalElements: 0 };
        ElMessage.error(`Không thể tải danh sách sản phẩm: ${error.response?.data?.message || error.message}`);
      } finally {
        this.productLoading = false;
      }
    },
    async fetchSanPhamChiTietList(idSP) {
      this.productLoading = true;
      try {
        await this.checkAuth();
        const response = await axios.get(`http://localhost:8080/admin/api/sanphamchitiet/bySanPham/${idSP}`, {
          withCredentials: true,
        });
        console.log('Dữ liệu API sản phẩm chi tiết:', JSON.stringify(response.data, null, 2));
        const dataContent = Array.isArray(response.data) ? response.data : [];
        this.listSanPhamChiTiet = {
          content: dataContent.map(item => {
            if (!item.id || !item.maSPCT) {
              console.warn('Sản phẩm chi tiết không hợp lệ:', item);
              return null;
            }
            return {
              id: item.id || null,
              idSP: item.idSP || idSP,
              maSPCT: item.maSPCT || 'N/A',
              maCtSanPham: item.maSPCT || 'N/A',
              tenSP: item.tenSP || 'Không xác định',
              tenGiay: item.tenSP || 'Không xác định',
              giaBan: item.gia != null ? Number(item.gia) : 0,
              soLuongTonKho: Number(item.soLuong) || 0,
              tenMauSac: item.tenMauSac || 'N/A',
              tenKichThuoc: item.tenSize || 'N/A',
              anhGiay: item.linkAnhDauTien || (item.imageLinks && item.imageLinks.length > 0 ? item.imageLinks[0] : 'https://placehold.co/60x60'),
              moTa: item.moTa || '',
              trangThai: item.trangThai != null ? item.trangThai : true,
            };
          }).filter(item => item !== null),
          number: 0,
          size: dataContent.length,
          totalElements: dataContent.length,
        };
        if (this.listSanPhamChiTiet.totalElements === 0) {
          ElMessage.warning('Không tìm thấy sản phẩm chi tiết nào cho sản phẩm này.');
        }
      } catch (error) {
        console.error('Lỗi khi lấy danh sách sản phẩm chi tiết:', error.response?.data || error.message);
        this.listSanPhamChiTiet = { content: [], number: 0, size: 5, totalElements: 0 };
        ElMessage.error(`Không thể tải danh sách sản phẩm chi tiết: ${error.response?.data?.message || error.message}`);
      } finally {
        this.productLoading = false;
      }
    },
    async showChonSanPham(hd, isQR = false) {
      this.hoaDonChon = hd;
      this.productSearchModal = true;
      this.productSearchQuery = '';
      this.selectedCategory = '';
      this.selectedBrand = '';
      this.selectedColor = '';
      this.selectedMaterial = '';
      this.priceRange = [0, 5000000];
      this.pageSanPham = 1;
      await this.fetchSanPhamList();
      if (isQR) {
        ElMessage.info('Chức năng quét QR chưa được triển khai');
      }
    },
    async chonSanPham(sp) {
      this.selectedSanPham = sp;
      this.productSearchModal = false;
      this.productDetailModal = true;
      this.pageSanPhamChiTiet = 1;
      await this.fetchSanPhamChiTietList(sp.id);
    },
    async themSanPhamVaoHoaDon(hd, sp) {
      try {
        await this.checkAuth();
        const response = await axios.get(`http://localhost:8080/api/sanphamchitiet/${sp.idCtSanPham}`, {
          withCredentials: true,
        });
        const sanPhamChiTiet = response.data;
        if (sanPhamChiTiet.soLuong <= 0) {
          ElMessage.error(`Sản phẩm ${sp.tenSanPham} đã hết hàng!`);
          return;
        }
        if (sanPhamChiTiet.soLuong < sp.soLuong) {
          ElMessage.error(`Số lượng tồn kho không đủ cho sản phẩm ${sp.tenSanPham}! Còn lại: ${sanPhamChiTiet.soLuong}`);
          return;
        }

        const sanPham = {
          idCtSanPham: sp.idCtSanPham,
          tenSanPham: sp.tenSanPham,
          soLuong: sp.soLuong || 1,
          donGia: sp.donGia,
          thanhTien: (sp.donGia || 0) * (sp.soLuong || 1),
          selectedVoucherId: sp.selectedVoucherId || null,
          selectedVoucher: sp.selectedVoucher || null,
        };
        if (!hd.sanPhams) hd.sanPhams = [];
        const index = hd.sanPhams.findIndex(item => item.idCtSanPham === sp.idCtSanPham);
        if (index !== -1) {
          hd.sanPhams[index].soLuong += sp.soLuong || 1;
          hd.sanPhams[index].thanhTien = hd.sanPhams[index].soLuong * hd.sanPhams[index].donGia;
        } else {
          hd.sanPhams.push(sanPham);
        }
        await this.capNhatHoaDonChiTiet(sanPham, hd);
        this.tinhTongTien(hd);
        ElMessage.success(`Đã thêm sản phẩm ${sp.tenSanPham} vào hóa đơn ${hd.maHoaDon}`);
      } catch (error) {
        console.error('themSanPhamVaoHoaDon:', error.response?.data || error.message);
        ElMessage.error('Không thể thêm sản phẩm: ' + (error.response?.data?.error || error.message));
      }
    },
    async fetchApplicableVouchers(idKH) {
      if (!idKH) {
        this.applicableVouchers = [];
        this.selectedVoucherId = null;
        return;
      }
      try {
        this.voucherLoading = true;
        const response = await axios.get(`http://localhost:8080/api/khuyenmai/applicable/${idKH}`, {
          withCredentials: true,
        });
        this.applicableVouchers = response.data.map(voucher => ({
          id: voucher.idKM,
          tenVoucher: voucher.tenKM,
          hinhThucGiam: voucher.hinhThucGiam,
          mucGiam: Number(voucher.mucGiam) || 0,
          giaGiam: Number(voucher.giamToiDa) || 0,
          giaTriDonHangToiThieu: Number(voucher.giaTriDonHangToiThieu) || 0,
        }));
        console.log('Danh sách khuyến mãi áp dụng:', JSON.stringify(this.applicableVouchers, null, 2));
        if (this.applicableVouchers.length === 0) {
          ElMessage.warning('Không tìm thấy khuyến mãi nào áp dụng cho khách hàng này.');
        }
      } catch (error) {
        console.error('Lỗi khi lấy danh sách khuyến mãi:', error.response?.data || error.message);
        this.applicableVouchers = [];
        this.selectedVoucherId = null;
        ElMessage.error('Không thể tải danh sách khuyến mãi!');
      } finally {
        this.voucherLoading = false;
      }
    },
    tinhGiamGiaKM(hd, voucher) {
      if (!voucher) return 0;
      const tongTienHang = this.tinhTienHang(hd);
      let giamGia = 0;
      if (voucher.hinhThucGiam === 'PERCENTAGE') {
        giamGia = Math.min((tongTienHang * voucher.mucGiam) / 100, voucher.giaGiam || Infinity);
      } else if (voucher.hinhThucGiam === 'FIXED') {
        giamGia = voucher.giaGiam || 0;
      }
      console.log(`[DEBUG] tinhGiamGiaKM: idKM=${voucher.id}, tongTienHang=${tongTienHang}, giamGia=${giamGia}`);
      return Math.max(giamGia, 0); // Đảm bảo giảm giá không âm
    },
    tinhGiamGiaSanPhamAll(hd) {
      return hd.sanPhams.reduce((sum, sp) => sum + this.tinhGiamGiaSanPham(sp), 0);
    },
    async chonVoucherHoaDon(hd) {
  try {
    if (!this.selectedVoucherId) {
      hd.idKM = null;
      await this.capNhatHoaDon(hd, hd.trangThai);
      ElMessage.info('Đã hủy áp dụng khuyến mãi cho hóa đơn');
      return;
    }

    const selectedVoucher = this.applicableVouchers.find(v => v.id === this.selectedVoucherId);
    if (!selectedVoucher) {
      throw new Error('Voucher được chọn không hợp lệ!');
    }

    const tongTienHang = this.tinhTienHang(hd);
    if (tongTienHang < (selectedVoucher.giaTriDonHangToiThieu || 0)) {
      hd.idKM = null;
      this.selectedVoucherId = null;
      await this.capNhatHoaDon(hd, hd.trangThai);
      ElMessage.warning(`Hóa đơn không đủ điều kiện áp dụng khuyến mãi ${selectedVoucher.tenVoucher}. Yêu cầu tối thiểu: ${selectedVoucher.giaTriDonHangToiThieu.toLocaleString()} VND`);
      return;
    }

    hd.idKM = this.selectedVoucherId;
    console.log(`[DEBUG] Áp dụng idKM: ${hd.idKM} cho hóa đơn ${hd.maHoaDon}, tongTienHang: ${tongTienHang}`);

    // Tính giảm giá từ khuyến mãi hóa đơn
    const giamGiaKM = this.tinhGiamGiaKM(hd, selectedVoucher);
    console.log(`[DEBUG] Giảm giá dự kiến từ KM: ${giamGiaKM}`);

    // Cập nhật hóa đơn qua API
    const response = await this.capNhatHoaDon(hd, hd.trangThai);

    // Kiểm tra xem backend có lưu idKM thành công không
    if (response?.idKM === null) {
      console.warn('[DEBUG] Backend không lưu idKM, sử dụng tính toán cục bộ');
      hd.tongTien = tongTienHang - (this.tinhGiamGiaSanPhamAll(hd) + giamGiaKM);
      ElMessage.warning('Backend không hỗ trợ lưu khuyến mãi, giảm giá được tính cục bộ');
    } else {
      hd.tongTien = response.tongTien || (tongTienHang - (this.tinhGiamGiaSanPhamAll(hd) + giamGiaKM));
    }

    ElMessage.success(`Đã áp dụng khuyến mãi ${selectedVoucher.tenVoucher} (giảm ${giamGiaKM.toLocaleString()} VND)`);
  } catch (error) {
    console.error('chonVoucherHoaDon:', error);
    ElMessage.error(`Không thể áp dụng khuyến mãi: ${error.message}`);
    hd.idKM = null;
    this.selectedVoucherId = null;
    await this.capNhatHoaDon(hd, hd.trangThai);
  }
},
    async capNhatHoaDon(hd, trangThai) {
      try {
        const calculatedTongTien = this.tinhTongTien(hd);
        const hoaDonDTO = {
          idHD: hd.idHD,
          maHD: hd.maHoaDon,
          idKhachHang: hd.idKhachHang || null,
          customerName: hd.khachHang || 'Khách lẻ',
          idNhanVien: hd.idNhanVien,
          ngayTao: hd.ngayTao,
          ngaySua: new Date().toISOString(),
          tongTien: Number(calculatedTongTien.toFixed(2)),
          trangThai: trangThai, // Đảm bảo trạng thái được gửi đúng
          loaiHoaDon: hd.loaiHoaDon || 'Tại quầy',
          totalProducts: hd.sanPhams ? hd.sanPhams.reduce((sum, sp) => sum + sp.soLuong, 0) : 0,
          idKM: hd.idKM || null,
          idPT: hd.idPT || null,
          maGiaoDich: hd.maGiaoDich || null,
          chiTietSanPham: hd.sanPhams ? hd.sanPhams.map(sp => ({
            idHDCT: sp.idHDCT || null,
            idCtSanPham: Number(sp.id),
            idSP: Number(sp.idSP),
            maSPCT: sp.maCtSanPham,
            tenSanPham: sp.tenGiay,
            donGia: Number(sp.giaBan.toFixed(2)),
            soLuong: Number(sp.soLuong),
            thanhTien: Number(sp.thanhTien.toFixed(2)),
            tenSize: sp.tenKichThuoc,
            tenMauSac: sp.tenMauSac,
            tenThuongHieu: sp.tenThuongHieu,
            tenChatLieu: sp.tenChatLieu,
            anhGiay: sp.anhGiay,
            soLuongTonKho: Number(sp.soLuongTonKho),
            ngayTao: sp.ngayTao || new Date().toISOString(),
            trangThai: true,
            idVoucher: sp.selectedVoucherId || null,
          })) : [],
        };
        console.log('Gửi hoaDonDTO đến backend:', JSON.stringify(hoaDonDTO, null, 2));

        const response = await axios.put(`http://localhost:8080/admin/api/hoadon/${hd.idHD}`, hoaDonDTO, {
          withCredentials: true,
          headers: { 'Content-Type': 'application/json; charset=UTF-8' },
        });
        console.log('Phản hồi từ PUT /hoadon:', JSON.stringify(response.data, null, 2));

        // Cập nhật lại dữ liệu cục bộ từ phản hồi
        hd.tongTien = Number(response.data.tongTien) || calculatedTongTien;
        hd.trangThai = response.data.trangThai || trangThai;
        hd.idKM = response.data.idKM || null;
      } catch (error) {
        console.error('Lỗi khi cập nhật hóa đơn:', error.response?.data || error.message);
        ElMessage.error(`Không thể cập nhật hóa đơn: ${error.response?.data?.message || error.message}`);
        throw error;
      }
    },
    async chonSanPhamChiTiet(sanPhamChiTiet) {
      try {
        console.log('Sản phẩm chi tiết được chọn:', JSON.stringify(sanPhamChiTiet, null, 2));
        if (!this.hoaDonChon || !this.hoaDonChon.idHD || isNaN(this.hoaDonChon.idHD)) {
          console.error('Hóa đơn đang chọn không hợp lệ:', this.hoaDonChon);
          ElMessage.error('Vui lòng chọn hoặc tạo một hóa đơn trước khi thêm sản phẩm!');
          return;
        }
        const newProduct = {
          id: sanPhamChiTiet.id,
          idSP: sanPhamChiTiet.idSP,
          idHDCT: null,
          maCtSanPham: sanPhamChiTiet.maSPCT || sanPhamChiTiet.maCtSanPham || 'N/A',
          tenGiay: sanPhamChiTiet.tenGiay,
          giaBan: parseFloat(sanPhamChiTiet.giaBan) || 0,
          soLuong: Number(sanPhamChiTiet.soLuong) || 1,
          soLuongTonKho: Number(sanPhamChiTiet.soLuongTonKho) || 0,
          thanhTien: parseFloat(sanPhamChiTiet.giaBan) || 0,
          tenKichThuoc: sanPhamChiTiet.tenKichThuoc || 'N/A',
          tenMauSac: sanPhamChiTiet.tenMauSac || 'N/A',
          tenThuongHieu: sanPhamChiTiet.tenThuongHieu || 'N/A',
          tenChatLieu: sanPhamChiTiet.tenChatLieu || 'N/A',
          anhGiay: sanPhamChiTiet.anhGiay || 'https://placehold.co/60x60',
          selectedVoucher: null,
          selectedVoucherId: null,
        };
        console.log('Thêm sản phẩm mới:', JSON.stringify(newProduct, null, 2));
        await this.themHoaDonChiTiet(newProduct, this.hoaDonChon);
        ElMessage.success('Thêm sản phẩm vào hóa đơn thành công!');
        this.productDetailModal = false;
      } catch (error) {
        console.error('Lỗi khi thêm sản phẩm:', error);
        ElMessage.error(error.message || 'Không thể thêm sản phẩm vào hóa đơn!');
      }
    },
    async huyChonVoucher(hd, sp) {
  try {
    // Xóa voucher khỏi sản phẩm
    sp.selectedVoucherId = null;
    sp.selectedVoucher = null;

    // Cập nhật chi tiết hóa đơn
    await this.capNhatHoaDonChiTiet(sp, hd);
    ElMessage.success(`Đã hủy voucher cho sản phẩm ${sp.tenGiay}`);

    // Không gọi fetchVouchersForProduct vì không cần làm mới số lượng
  } catch (error) {
    console.error('huyChonVoucher:', error.response?.data || error.message);
    ElMessage.error('Không thể hủy voucher: ' + (error.response?.data?.error || error.message));
  }
},
    async themHoaDonChiTiet(newProduct, hd) {
      try {
        console.log('Hóa đơn được chọn:', JSON.stringify(hd, null, 2));
        console.log('Sản phẩm mới:', JSON.stringify(newProduct, null, 2));
        if (!hd || !hd.idHD || isNaN(hd.idHD)) {
          console.error('Hóa đơn không hợp lệ:', hd);
          throw new Error('ID hóa đơn không hợp lệ');
        }
        if (!newProduct.id || !newProduct.maCtSanPham || isNaN(newProduct.giaBan)) {
          console.error('Sản phẩm không hợp lệ:', newProduct);
          throw new Error('Dữ liệu sản phẩm không hợp lệ');
        }
        const chiTietDTO = {
          idHD: Number(hd.idHD),
          idCtSanPham: newProduct.id,
          idSP: newProduct.idSP,
          donGia: newProduct.giaBan,
          soLuong: newProduct.soLuong || 1,
          thanhTien: (newProduct.giaBan * (newProduct.soLuong || 1)).toString(),
          tenSize: newProduct.tenKichThuoc || 'N/A',
          tenMauSac: newProduct.tenMauSac || 'N/A',
          tenThuongHieu: newProduct.tenThuongHieu || 'N/A',
          tenChatLieu: newProduct.tenChatLieu || 'N/A',
          anhGiay: newProduct.anhGiay || 'https://placehold.co/60x60',
          soLuongTonKho: newProduct.soLuongTonKho || 0,
          ngayTao: new Date().toISOString(),
          ngaySua: new Date().toISOString(),
          trangThai: true,
          idVoucher: newProduct.selectedVoucherId || null,
        };
        console.log('chiTietDTO:', JSON.stringify(chiTietDTO, null, 2));
        const response = await axios.post('http://localhost:8080/admin/api/hoadonchitiet', chiTietDTO, {
          withCredentials: true,
          headers: { 'Content-Type': 'application/json; charset=UTF-8' },
        });
        console.log('Phản hồi từ POST /hoadonchitiet:', JSON.stringify(response.data, null, 2));
        hd.sanPhams.push({
          ...newProduct,
          idHDCT: response.data.idHDCT,
        });
        this.$forceUpdate();
      } catch (error) {
        console.error('Lỗi khi thêm chi tiết hóa đơn:', error.response?.data || error.message);
        const errorMessage = error.response?.data?.message || error.message || 'Không thể thêm chi tiết hóa đơn';
        throw new Error(errorMessage);
      }
    },
    async capNhatHoaDonChiTiet(sp, hd) {
  try {
    if (!hd || !hd.idHD || isNaN(hd.idHD)) {
      throw new Error('ID hóa đơn không hợp lệ');
    }
    if (!sp.id || isNaN(sp.giaBan) || isNaN(sp.soLuong)) {
      throw new Error('Dữ liệu sản phẩm không hợp lệ');
    }

    // Tính toán thanhTien với giảm giá từ voucher
    let thanhTien = sp.giaBan * sp.soLuong;
    if (sp.selectedVoucher) {
      if (sp.selectedVoucher.hinhThucGiam === '%') {
        thanhTien -= (thanhTien * sp.selectedVoucher.mucGiam) / 100;
      } else {
        thanhTien -= sp.selectedVoucher.giaGiam;
      }
      thanhTien = Math.max(thanhTien, 0);
    }

    const chiTietDTO = {
      idHDCT: sp.idHDCT || null,
      idHD: Number(hd.idHD),
      idCtSanPham: Number(sp.id),
      idSP: Number(sp.idSP),
      maSPCT: sp.maCtSanPham,
      tenSanPham: sp.tenGiay,
      donGia: Number(sp.giaBan.toFixed(2)),
      soLuong: Number(sp.soLuong),
      thanhTien: Number(thanhTien.toFixed(2)),
      tenSize: sp.tenKichThuoc,
      tenMauSac: sp.tenMauSac,
      tenThuongHieu: sp.tenThuongHieu,
      tenChatLieu: sp.tenChatLieu,
      anhGiay: sp.anhGiay,
      soLuongTonKho: Number(sp.soLuongTonKho),
      ngayTao: sp.ngayTao || new Date().toISOString(),
      ngaySua: new Date().toISOString(),
      trangThai: true,
      idVoucher: sp.selectedVoucherId || null,
      tenVoucher: sp.selectedVoucher ? sp.selectedVoucher.tenVoucher : null,
      hinhThucGiam: sp.selectedVoucher ? sp.selectedVoucher.hinhThucGiam : null,
      mucGiam: sp.selectedVoucher ? Number(sp.selectedVoucher.mucGiam) : null,
      giaGiam: sp.selectedVoucher ? Number(sp.selectedVoucher.giaGiam) : null,
      giaTriDonHangToiThieu: sp.selectedVoucher ? Number(sp.selectedVoucher.giaTriDonHangToiThieu) : null,
      soLuongVoucher: sp.selectedVoucher ? Number(sp.selectedVoucher.soLuong) : null,
    };

    console.log('Cập nhật chi tiết hóa đơn:', JSON.stringify(chiTietDTO, null, 2));
    const response = await axios.put(`http://localhost:8080/admin/api/hoadonchitiet/${sp.idHDCT || ''}`, chiTietDTO, {
      withCredentials: true,
      headers: { 'Content-Type': 'application/json; charset=UTF-8' },
    });
    console.log('Phản hồi từ PUT /hoadonchitiet:', JSON.stringify(response.data, null, 2));

    // Cập nhật dữ liệu sản phẩm cục bộ
    sp.thanhTien = chiTietDTO.thanhTien;
    sp.selectedVoucher = sp.selectedVoucherId
      ? {
          id: sp.selectedVoucherId,
          tenVoucher: response.data.tenVoucher || chiTietDTO.tenVoucher || 'Unknown Voucher',
          hinhThucGiam: response.data.hinhThucGiam || chiTietDTO.hinhThucGiam || 'FIXED',
          mucGiam: Number(response.data.mucGiam) || Number(chiTietDTO.mucGiam) || 0,
          giaGiam: Number(response.data.giaGiam) || Number(chiTietDTO.giaGiam) || 0,
          giaTriDonHangToiThieu: Number(response.data.giaTriDonHangToiThieu) || Number(chiTietDTO.giaTriDonHangToiThieu) || 0,
          soLuong: Number(response.data.soLuongVoucher) || Number(chiTietDTO.soLuongVoucher) || 0,
        }
      : null;

    ElMessage.success('Cập nhật sản phẩm trong hóa đơn thành công!');
    this.tinhTongTien(hd);
  } catch (error) {
    console.error('Lỗi khi cập nhật chi tiết hóa đơn:', error.response?.data || error.message);
    ElMessage.error(`Không thể cập nhật sản phẩm: ${error.response?.data?.message || error.message}`);
    throw error;
  }
},
    async xoaHoaDon(hd, retryCount = 3) {
      try {
        await this.checkAuth();
        if (hd.idHD) {
          await axios.delete(`http://localhost:8080/admin/api/hoadon/${hd.idHD}`, {
            withCredentials: true,
          });
          ElMessage.success(`Đã xóa hóa đơn ${hd.maHoaDon}`);
        }
        this.timeoutIds.delete(hd.idHD);
        this.hoaDons = this.hoaDons.filter(item => item.maHoaDon !== hd.maHoaDon);
        this.tabActive = this.hoaDons.length > 0 ? this.hoaDons[0].maHoaDon : '';
      } catch (error) {
        console.error('xoaHoaDon:', error);
        if (retryCount > 0 && error.response?.status === 401) {
          await this.checkAuth();
          return this.xoaHoaDon(hd, retryCount - 1);
        }
        ElMessage.error(`Không thể xóa hóa đơn ${hd.maHoaDon}`);
      }
    },
    async onChangeSoLuong(row, hd) {
      if (row.soLuong > row.soLuongTonKho || isNaN(row.soLuong)) {
        row.soLuong = row.soLuongTonKho;
        ElMessage.error('Vượt quá số lượng tồn kho!');
      } else if (row.soLuong < 1) {
        row.soLuong = 1;
      } else {
        await this.capNhatHoaDonChiTiet(row, hd);
      }
      if (row.selectedVoucherId) {
        const validVouchers = this.getValidVouchersForProduct(row);
        if (!validVouchers.some(v => v.id === row.selectedVoucherId)) {
          const oldVoucher = row.selectedVoucher;
          row.selectedVoucherId = null;
          row.selectedVoucher = null;
          if (oldVoucher) {
            const tongTien = row.giaBan * row.soLuong;
            const minAmount = oldVoucher.giaTriDonHangToiThieu || 0;
            ElMessage.warning(`Voucher "${oldVoucher.tenVoucher}" không còn hợp lệ cho sản phẩm ${row.tenGiay}! Tổng tiền: ${tongTien.toLocaleString()} VND, yêu cầu tối thiểu: ${minAmount.toLocaleString()} VND`);
          }
        }
      }
    },
    async fetchCategories() {
      try {
        await this.checkAuth();
        const response = await axios.get('http://localhost:8080/api/danhmuc', {
          withCredentials: true,
        });
        this.categories = response.data.map(item => ({
          value: item.idDM,
          label: item.tenDM,
        }));
      } catch (error) {
        console.error('fetchCategories:', error);
        ElMessage.error('Không thể tải danh sách danh mục');
      }
    },
    async fetchBrands() {
      try {
        await this.checkAuth();
        const response = await axios.get('http://localhost:8080/api/thuonghieu', {
          withCredentials: true,
        });
        this.brands = response.data.map(item => ({
          value: item.idThuongHieu,
          label: item.tenThuongHieu,
        }));
      } catch (error) {
        console.error('fetchBrands:', error);
        ElMessage.error('Không thể tải danh sách thương hiệu');
      }
    },
    async fetchColors() {
      try {
        await this.checkAuth();
        const response = await axios.get('http://localhost:8080/api/mausac', {
          withCredentials: true,
        });
        this.colors = response.data.map(item => ({
          value: item.tenMauSac,
          label: item.tenMauSac,
        }));
      } catch (error) {
        console.error('fetchColors:', error);
        ElMessage.error('Không thể tải danh sách màu sắc');
      }
    },
    async fetchMaterials() {
      try {
        await this.checkAuth();
        const response = await axios.get('http://localhost:8080/api/chatlieu', {
          withCredentials: true,
        });
        this.materials = response.data.map(item => ({
          value: item.idChatLieu,
          label: item.tenChatLieu,
        }));
      } catch (error) {
        console.error('fetchMaterials:', error);
        ElMessage.error('Không thể tải danh sách chất liệu');
      }
    },
    async fetchPhuongThucThanhToan() {
      try {
        await this.checkAuth();
        const response = await axios.get('http://localhost:8080/admin/api/phuongthucthanhtoan', {
          withCredentials: true,
        });
        this.phuongThucThanhToans = response.data;
        console.log('Danh sách phương thức thanh toán:', JSON.stringify(this.phuongThucThanhToans, null, 2));
        if (this.phuongThucThanhToans.length === 0) {
          ElMessage.error('Không có phương thức thanh toán nào được tải từ server!');
        }
      } catch (error) {
        console.error('fetchPhuongThucThanhToan:', error.response?.data || error.message);
        ElMessage.error('Không thể tải danh sách phương thức thanh toán: ' + (error.response?.data?.message || error.message));
        this.phuongThucThanhToans = [];
      }
    },
    async fetchCustomers(page = 0) {
      this.loadingCustomers = true;
      try {
        await this.checkAuth();
        const response = await axios.get('http://localhost:8080/admin/api/khachhang', {
          params: {
            page: page,
            size: this.pageSizeCustomer,
            search: this.customerSearch || '',
          },
          withCredentials: true,
        });
        this.customers = response.data.content.map(item => ({
          id: item.idKH,
          tenKH: item.tenKH || 'N/A',
          sdt: item.sdt || 'N/A',
          gioiTinh: item.gioiTinh,
          trangThai: item.trangThai,
        }));
        this.totalCustomers = response.data.totalElements;
        this.currentPageCustomer = page + 1;
      } catch (error) {
        console.error('fetchCustomers:', error);
        ElMessage.error('Không thể tải danh sách khách hàng');
        this.customers = [];
        this.totalCustomers = 0;
      } finally {
        this.loadingCustomers = false;
      }
    },
    debounceSearchCustomers: _.debounce(function () {
      this.fetchCustomers(0);
    }, 300),
    searchCustomers() {
      this.debounceSearchCustomers();
    },
    async selectCustomer(customer, hd) {
      try {
        hd.idKhachHang = customer.id;
        hd.khachHang = customer.tenKH;
        hd.idKM = null;
        await this.capNhatHoaDon(hd, hd.trangThai);
        await this.fetchApplicableVouchers(customer.id);
        ElMessage.success(`Đã chọn khách hàng ${customer.tenKH}`);
        this.modalCustomer = false;
      } catch (error) {
        console.error('selectCustomer:', error);
        ElMessage.error('Không thể cập nhật khách hàng cho hóa đơn');
      }
    },
    showCustomerModal(hd) {
      this.selectedHoaDon = hd;
      this.modalCustomer = true;
      this.customerSearch = '';
      this.applicableVouchers = []; // Reset danh sách khuyến mãi
      this.selectedVoucherId = null; // Reset combobox
      this.fetchCustomers(0);
    },
    debouncedSearchProducts: _.debounce(function () {
      this.fetchSanPhamList();
    }, 300),
    async taiLaiDanhSachSanPham() {
      this.productSearchQuery = '';
      this.selectedCategory = '';
      this.selectedBrand = '';
      this.selectedColor = '';
      this.selectedMaterial = '';
      this.priceRange = [0, 5000000];
      this.pageSanPham = 1;
      await this.fetchSanPhamList();
    },
    async onProductPageChange(page) {
      this.pageSanPham = page;
      await this.fetchSanPhamList();
    },
    async onProductDetailPageChange(page) {
      this.pageSanPhamChiTiet = page;
      await this.fetchSanPhamChiTietList(this.selectedSanPham.id);
    },
    async xoaSanPham(hd, sp) {
      try {
        if (sp.idHDCT) {
          await axios.delete(`http://localhost:8080/admin/api/hoadonchitiet/${sp.idHDCT}`, {
            withCredentials: true,
          });
          ElMessage.success(`Đã xóa sản phẩm ${sp.tenGiay}`);
        }
        hd.sanPhams = hd.sanPhams.filter(item => item.id !== sp.id);
        this.$forceUpdate();
      } catch (error) {
        console.error('xoaSanPham:', error);
        ElMessage.error(`Không thể xóa sản phẩm ${sp.tenGiay}`);
      }
    },
    async showVoucherModal(hd, sp) {
      this.selectedHoaDon = hd;
      this.selectedProduct = sp;
      this.voucherModal = true;
      sp.selectedVoucherId = null; // Reset voucher khi mở modal
      sp.selectedVoucher = null;
      await this.fetchVouchersForProduct(sp);
    },
    async fetchVouchersForProduct(sp) {
  try {
    this.voucherLoading = true;
    const idSPCTs = [sp.id];
    const response = await axios.post('http://localhost:8080/api/voucher/applicable', idSPCTs, {
      withCredentials: true,
      headers: { 'Content-Type': 'application/json' },
    });
    console.log('Danh sách voucher áp dụng:', response.data);

    let vouchers = Array.isArray(response.data) ? response.data : response.data.content || [];
    this.productVouchers = vouchers
      .filter(voucher => {
        const now = new Date();
        const startDate = new Date(voucher.ngayBatDau);
        const endDate = new Date(voucher.ngayKetThuc);
        const tongTien = sp.giaBan * sp.soLuong;
        const minAmount = voucher.giaTriDonHangToiThieu || 0;
        return voucher.trangThai && now >= startDate && now <= endDate && tongTien >= minAmount;
      })
      .map(voucher => ({
        id: voucher.id,
        tenVoucher: voucher.tenVoucher,
        hinhThucGiam: voucher.hinhThucGiam,
        mucGiam: voucher.mucGiam,
        giaGiam: voucher.giaGiam,
        giaTriDonHangToiThieu: voucher.giaTriDonHangToiThieu,
        soLuong: voucher.soLuong, // Thêm soLuong vào ánh xạ
      }));

    if (this.productVouchers.length === 0) {
      ElMessage.warning('Không có voucher áp dụng được cho sản phẩm này.');
    }
  } catch (error) {
    console.error('fetchVouchersForProduct:', error.response?.data || error.message);
    ElMessage.error('Không thể tải danh sách voucher: ' + (error.response?.data?.error || error.message));
    this.productVouchers = [];
  } finally {
    this.voucherLoading = false;
  }
},
    getValidVouchersForProduct(sp) {
      return this.productVouchers;
    },
    async chonVoucherSanPham(hd, sp, voucherId) {
  try {
    const selectedVoucher = this.productVouchers.find(v => v.id === voucherId);
    if (!selectedVoucher) {
      ElMessage.error('Voucher được chọn không hợp lệ!');
      sp.selectedVoucherId = null;
      sp.selectedVoucher = null;
      this.voucherModal = false;
      return;
    }
    if (selectedVoucher.soLuong <= 0) {
      ElMessage.error('Voucher đã hết số lượng!');
      sp.selectedVoucherId = null;
      sp.selectedVoucher = null;
      this.voucherModal = false;
      return;
    }

    // Hoàn voucher cũ nếu có (chỉ cập nhật cục bộ, không gửi server)
    if (sp.selectedVoucherId) {
      const oldVoucher = this.productVouchers.find(v => v.id === sp.selectedVoucherId);
      if (oldVoucher) {
        console.log(`Hủy voucher cũ ${oldVoucher.tenVoucher} cục bộ`);
      }
    }

    // Áp dụng voucher mới
    sp.selectedVoucherId = selectedVoucher.id;
    sp.selectedVoucher = {
      id: selectedVoucher.id,
      tenVoucher: selectedVoucher.tenVoucher,
      hinhThucGiam: selectedVoucher.hinhThucGiam,
      mucGiam: selectedVoucher.mucGiam,
      giaGiam: selectedVoucher.giaGiam,
      giaTriDonHangToiThieu: selectedVoucher.giaTriDonHangToiThieu,
      soLuong: selectedVoucher.soLuong, // Giữ nguyên số lượng, không giảm
    };

    // Cập nhật chi tiết hóa đơn
    await this.capNhatHoaDonChiTiet(sp, hd);
    ElMessage.success(`Đã áp dụng voucher ${sp.selectedVoucher.tenVoucher}`);
    this.voucherModal = false;

    // Không gọi fetchVouchersForProduct vì không cần làm mới số lượng
  } catch (error) {
    console.error('chonVoucherSanPham:', error.response?.data || error.message);
    ElMessage.error('Không thể áp dụng voucher: ' + (error.response?.data?.error || error.message));
  }
},
    tinhGiamGiaSanPham(sp) {
      if (!sp.selectedVoucher) return 0;
      const tongTien = sp.giaBan * sp.soLuong;
      if (sp.selectedVoucher.hinhThucGiam === '%') {
        return Math.min(tongTien * (sp.selectedVoucher.mucGiam / 100), sp.selectedVoucher.giaGiam || Infinity);
      }
      return sp.selectedVoucher.giaGiam || 0;
    },
    tinhTienHang(hd) {
      return hd.sanPhams.reduce((sum, sp) => sum + (sp.giaBan * sp.soLuong), 0);
    },
    tinhGiamGia(hd) {
      let totalDiscount = 0;

      // Tính giảm giá từ voucher của sản phẩm
      totalDiscount += hd.sanPhams.reduce((sum, sp) => {
        if (sp.selectedVoucher) {
          const tongTienSanPham = sp.giaBan * sp.soLuong;
          if (sp.selectedVoucher.hinhThucGiam === '%') {
            return sum + Math.min((tongTienSanPham * sp.selectedVoucher.mucGiam) / 100, sp.selectedVoucher.giaGiam || Infinity);
          } else {
            return sum + (sp.selectedVoucher.giaGiam || 0);
          }
        } else if (sp.thanhTien < sp.giaBan * sp.soLuong) {
          // Tính giảm giá dựa trên thanhTien nếu không có selectedVoucher
          return sum + (sp.giaBan * sp.soLuong - sp.thanhTien);
        }
        return sum;
      }, 0);

      // Tính giảm giá từ voucher của hóa đơn
      if (hd.idKM) {
        const voucher = this.applicableVouchers.find(v => v.id === hd.idKM);
        if (voucher) {
          const tongTienHang = this.tinhTienHang(hd);
          if (tongTienHang >= (voucher.giaTriDonHangToiThieu || 0)) {
            if (voucher.hinhThucGiam === 'PERCENTAGE') {
              totalDiscount += Math.min((tongTienHang * voucher.mucGiam) / 100, voucher.giaGiam || Infinity);
            } else if (voucher.hinhThucGiam === 'FIXED') {
              totalDiscount += voucher.giaGiam || 0;
            }
          } else {
            hd.idKM = null;
            this.selectedVoucherId = null;
            ElMessage.warning(`Hóa đơn không đủ điều kiện áp dụng khuyến mãi ${voucher.tenVoucher}. Yêu cầu tối thiểu: ${voucher.giaTriDonHangToiThieu.toLocaleString()} VND`);
          }
        }
      }

      return Math.max(totalDiscount, 0); // Đảm bảo giảm giá không âm
    },
    tinhTongTien(hd) {
      let tongTien = this.tinhTienHang(hd);
      let giamGia = this.tinhGiamGia(hd);
      
      // Áp dụng giảm giá từ voucher hóa đơn
      if (hd.idKM) {
        const voucher = this.applicableVouchers.find(v => v.id === hd.idKM);
        if (voucher) {
          const tongTienHang = this.tinhTienHang(hd);
          if (tongTienHang >= (voucher.giaTriDonHangToiThieu || 0)) {
            if (voucher.hinhThucGiam === 'PERCENTAGE') {
              const giamGiaVoucher = Math.min((tongTienHang * voucher.mucGiam) / 100, voucher.giaGiam || Infinity);
              giamGia += giamGiaVoucher;
            } else if (voucher.hinhThucGiam === 'FIXED') {
              giamGia += voucher.giaGiam || 0;
            }
          } else {
            console.warn(`Hóa đơn ${hd.maHoaDon} không đủ điều kiện áp dụng khuyến mãi ${voucher.tenVoucher}`);
            hd.idKM = null;
            this.selectedVoucherId = null;
            ElMessage.warning(`Hóa đơn không đủ điều kiện áp dụng khuyến mãi ${voucher.tenVoucher}. Yêu cầu tối thiểu: ${voucher.giaTriDonHangToiThieu.toLocaleString()} VND`);
          }
        } else {
          console.warn(`Không tìm thấy voucher với idKM ${hd.idKM}`);
          hd.idKM = null;
          this.selectedVoucherId = null;
        }
      }

      tongTien -= giamGia;
      return Math.max(tongTien, 0); // Đảm bảo tổng tiền không âm
    },
    async openThanhToan(hd) {
      this.thanhToanHoaDon = hd;
      this.thanhToanTongTien = this.tinhTongTien(hd);
      this.thanhToanPhuongThuc = 'MOMO';
      this.thanhToanTienKhachDua = 0;
      this.thanhToanMaGiaoDich = '';
      this.momoQrCodeUrl = null;
      this.momoPayUrl = null;
      this.momoLoading = false;
      this.modalThanhToan = true;
    },
    validateTienKhachDua() {
      if (this.thanhToanTienKhachDua < 0) {
        this.thanhToanTienKhachDua = 0;
      }
    },
    async taoMoMoQR() {
      this.momoLoading = true;
      try {
        const orderId = `MOMO${Date.now()}${Math.floor(Math.random() * 1000)}`;
        const requestBody = {
          orderId: orderId,
          amount: this.thanhToanTongTien,
          orderInfo: `Thanh toán hóa đơn ${this.thanhToanHoaDon.maHoaDon}`,
          returnUrl: window.location.origin + '/momo-return',
          notifyUrl: 'http://localhost:8080/api/momo/notify',
        };
        console.log('Tạo MoMo QR:', requestBody);
        const response = await axios.post('http://localhost:8080/api/momo/create', requestBody, {
          withCredentials: true,
        });
        console.log('Phản hồi từ MoMo:', response.data);
        this.momoQrCodeUrl = response.data.qrCodeUrl;
        this.momoPayUrl = response.data.payUrl;
        this.momoOrderId = orderId;
        localStorage.setItem('momo_last_order_id', orderId);
        this.startCheckingMomoStatus();
      } catch (error) {
        console.error('taoMoMoQR:', error);
        ElMessage.error('Không thể tạo mã thanh toán MoMo');
      } finally {
        this.momoLoading = false;
      }
    },
    async handleMoMoReturn(orderId) {
      try {
        const response = await axios.get(`http://localhost:8080/api/momo/status/${orderId}`, {
          withCredentials: true,
        });
        console.log('handleMoMoReturn:', response.data);
        if (response.data.resultCode === 0) {
          this.thanhToanMaGiaoDich = response.data.transId;
          await this.xacNhanThanhToan();
        } else {
          ElMessage.error('Thanh toán MoMo không thành công');
        }
      } catch (error) {
        console.error('handleMoMoReturn:', error);
        ElMessage.error('Lỗi khi xử lý thanh toán MoMo');
      }
    },
    async xacNhanThanhToan() {
  try {
    // Kiểm tra danh sách phương thức thanh toán
    console.log('Danh sách phương thức thanh toán:', JSON.stringify(this.phuongThucThanhToans, null, 2));
    if (!this.phuongThucThanhToans || this.phuongThucThanhToans.length === 0) {
      throw new Error('Danh sách phương thức thanh toán rỗng! Vui lòng kiểm tra API /phuongthucthanhtoan.');
    }

    // Kiểm tra số lượng tồn kho trước khi xác nhận thanh toán
    for (const sp of this.thanhToanHoaDon.sanPhams) {
      const response = await axios.get(`http://localhost:8080/admin/api/sanphamchitiet/${sp.id}`, {
        withCredentials: true,
      });
      const sanPhamChiTiet = response.data;
      console.log(`Kiểm tra tồn kho sản phẩm ${sp.tenGiay}: Còn lại ${sanPhamChiTiet.soLuong}, cần ${sp.soLuong}`);
      if (sanPhamChiTiet.soLuong < sp.soLuong) {
        throw new Error(`Số lượng tồn kho không đủ cho sản phẩm ${sp.tenGiay}! Còn lại: ${sanPhamChiTiet.soLuong}`);
      }
    }

    // Chuẩn bị chi tiết sản phẩm
    const sanPhamChiTietPromises = this.thanhToanHoaDon.sanPhams.map(sp =>
      axios.get(`http://localhost:8080/admin/api/sanphamchitiet/${sp.id}`, {
        withCredentials: true,
      }).then(response => ({
        sp,
        sanPhamChiTiet: response.data,
      }))
    );
    const sanPhamChiTietResults = await Promise.all(sanPhamChiTietPromises);

    this.thanhToanHoaDon.chiTietSanPham = sanPhamChiTietResults.map(({ sp, sanPhamChiTiet }) => ({
      idHDCT: sp.idHDCT || null,
      idCtSanPham: sp.id,
      idSP: sp.idSP,
      maSPCT: sp.maCtSanPham,
      tenSanPham: sp.tenGiay,
      donGia: Number(sp.giaBan.toFixed(2)),
      soLuong: Number(sp.soLuong),
      thanhTien: Number(sp.thanhTien.toFixed(2)),
      tenSize: sp.tenKichThuoc,
      tenMauSac: sp.tenMauSac,
      tenThuongHieu: sp.tenThuongHieu,
      tenChatLieu: sp.tenChatLieu,
      anhGiay: sp.anhGiay,
      soLuongTonKho: Number(sanPhamChiTiet.soLuong),
      ngayTao: sp.ngayTao || new Date().toISOString(),
      idVoucher: sp.selectedVoucherId || null,
    }));

    // Thiết lập thông tin thanh toán
    let phuongThucForId = this.thanhToanPhuongThuc;
    if (this.thanhToanPhuongThuc === 'MOMO') {
      phuongThucForId = 'CHUYỂN KHOẢN';
    }
    const selectedPhuongThuc = this.phuongThucThanhToans.find(pt => pt.ten === phuongThucForId);
    if (!selectedPhuongThuc) {
      throw new Error(`Phương thức thanh toán '${phuongThucForId}' không tồn tại trong danh sách phương thức!`);
    }
    const thanhToanInfo = {
      phuongThuc: this.thanhToanPhuongThuc,
      soTien: this.thanhToanPhuongThuc === 'TIỀN MẶT' ? this.thanhToanTienKhachDua : this.thanhToanTongTien,
      maGiaoDich: this.thanhToanMaGiaoDich || null,
      ngayThanhToan: new Date().toISOString(),
      idPT: selectedPhuongThuc.idPTT, // SỬA Ở ĐÂY: Dùng idPTT thay vì id
    };
    console.log('Thông tin thanh toán:', JSON.stringify(thanhToanInfo, null, 2));

    // Gán idPT và các thông tin khác vào thanhToanHoaDon
    this.thanhToanHoaDon.thanhToan = thanhToanInfo;
    this.thanhToanHoaDon.trangThai = 'HOAN_THANH';
    this.thanhToanHoaDon.maGiaoDich = thanhToanInfo.maGiaoDich;
    this.thanhToanHoaDon.idPT = selectedPhuongThuc.idPTT; // SỬA Ở ĐÂY: Dùng idPTT thay vì id

    // Log trước khi gọi capNhatHoaDon
    console.log('thanhToanHoaDon trước khi cập nhật:', JSON.stringify(this.thanhToanHoaDon, null, 2));

    // Gọi cập nhật hóa đơn
    await this.capNhatHoaDon(this.thanhToanHoaDon, 'HOAN_THANH');
    this.thanhToanLichSu.push(thanhToanInfo);
    this.modalThanhToan = false;
    this.hoaDonIn = { ...this.thanhToanHoaDon };
    this.modalHoaDon = true;
    this.stopCheckingMomoStatus();
    localStorage.removeItem('momo_last_order_id');
    this.hoaDons = this.hoaDons.filter(hd => hd.maHoaDon !== this.thanhToanHoaDon.maHoaDon);
    this.tabActive = this.hoaDons.length > 0 ? this.hoaDons[0].maHoaDon : '';
    if (this.hoaDonChon && this.hoaDonChon.maHoaDon === this.thanhToanHoaDon.maHoaDon) {
      this.hoaDonChon = null;
    }
    ElMessage.success('Thanh toán thành công!');
  } catch (error) {
    console.error('xacNhanThanhToan:', error);
    ElMessage.error('Không thể xác nhận thanh toán: ' + (error.response?.data?.message || error.message));
  }
},
    closeThanhToanModal() {
      this.modalThanhToan = false;
      this.stopCheckingMomoStatus();
      localStorage.removeItem('momo_last_order_id');
    },
    formatDate(date) {
      return new Date(date).toLocaleString('vi-VN', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
      });
    },
    async taoHoaDonMoiNgay() {
      this.modalHoaDon = false;
      await this.taoHoaDon();
    },
    inHoaDon() {
      // Tạo nội dung HTML của hóa đơn
      const invoiceContent = `
        <html>
          <head>
            <title>Hóa đơn ${this.hoaDonIn.maHoaDon}</title>
            <style>
              body { font-family: Arial, sans-serif; margin: 20px; }
              .invoice { max-width: 800px; margin: auto; }
              .header { text-align: center; }
              .table { width: 100%; border-collapse: collapse; margin-top: 20px; }
              .table th, .table td { border: 1px solid #ddd; padding: 8px; text-align: left; }
              .table th { background-color: #f2f2f2; }
            </style>
          </head>
          <body>
            <div class="invoice">
              <div class="header">
                <h3>NEXVIBE</h3>
                <div>SĐT: 0123456789</div>
                <div>Email: nexvibeweb@gmail.com</div>
                <div>Địa chỉ: FPT POLYTECHNIC Cơ Sở Trịnh Văn Bô, Nam Từ Liêm, Hà Nội</div>
              </div>
              <h4 style="text-align: center; margin-top: 20px;">HÓA ĐƠN BÁN HÀNG</h4>
              <div>Mã hóa đơn: ${this.hoaDonIn.maHoaDon}</div>
              <div>Ngày tạo: ${this.formatDate(this.hoaDonIn.ngayTao)}</div>
              <div>Khách hàng: ${this.hoaDonIn.khachHang || 'Khách lẻ'}</div>
              <div>Phương thức thanh toán: ${this.hoaDonIn.thanhToan?.phuongThuc || 'N/A'}</div>
              ${this.hoaDonIn.thanhToan?.maGiaoDich ? `<div>Mã giao dịch: ${this.hoaDonIn.thanhToan.maGiaoDich}</div>` : ''}
              <table class="table">
                <thead>
                  <tr>
                    <th>Tên sản phẩm</th>
                    <th>Số lượng</th>
                    <th>Đơn giá</th>
                    <th>Thành tiền</th>
                  </tr>
                </thead>
                <tbody>
                  ${this.hoaDonIn.sanPhams.map(sp => `
                    <tr>
                      <td>${sp.tenGiay}</td>
                      <td>${sp.soLuong}</td>
                      <td>${sp.giaBan != null ? sp.giaBan.toLocaleString() : '0'} VND</td>
                      <td>${sp.giaBan != null ? (sp.giaBan * sp.soLuong - (sp.selectedVoucher ? this.tinhGiamGiaSanPham(sp) : 0)).toLocaleString() : '0'} VND</td>
                    </tr>
                  `).join('')}
                </tbody>
              </table>
              <div style="margin-top: 20px;">
                <div>Tổng tiền hàng: ${this.tinhTienHang(this.hoaDonIn).toLocaleString()} VND</div>
                <div>Giảm giá: ${this.tinhGiamGia(this.hoaDonIn).toLocaleString()} VND</div>
                <div><b>Tổng tiền cần thanh toán: ${this.tinhTongTien(this.hoaDonIn).toLocaleString()} VND</b></div>
                ${this.hoaDonIn.thanhToan && this.hoaDonIn.thanhToan.soTien > this.tinhTongTien(this.hoaDonIn) ? `
                  <div><b>Tiền thừa trả lại: ${(this.hoaDonIn.thanhToan.soTien - this.tinhTongTien(this.hoaDonIn)).toLocaleString()} VND</b></div>
                ` : ''}
              </div>
            </div>
          </body>
        </html>
      `;

      // Tạo iframe ẩn để in
      const iframe = document.createElement('iframe');
      iframe.style.display = 'none';
      document.body.appendChild(iframe);
      
      // Viết nội dung hóa đơn vào iframe
      const iframeDoc = iframe.contentDocument || iframe.contentWindow.document;
      iframeDoc.open();
      iframeDoc.write(invoiceContent);
      iframeDoc.close();

      // Tự động lưu file HTML
      try {
        const blob = new Blob([invoiceContent], { type: 'text/html' });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `${this.hoaDonIn.maHoaDon}.html`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
      } catch (error) {
        console.error('Lỗi khi lưu hóa đơn:', error);
        ElMessage.error('Không thể lưu hóa đơn!');
      }

      // In hóa đơn và đóng modal
      iframe.onload = () => {
        try {
          iframe.contentWindow.print();
          this.modalHoaDon = false; // Đóng modal sau khi in
          // Xóa iframe sau khi in
          setTimeout(() => {
            document.body.removeChild(iframe);
          }, 1000);
        } catch (error) {
          console.error('Lỗi khi in hóa đơn:', error);
          ElMessage.error('Không thể in hóa đơn!');
          this.modalHoaDon = false; // Đóng modal ngay cả khi có lỗi
          document.body.removeChild(iframe);
        }
      };
    },
  },
};
</script>

<style scoped>
.ban-hang-tai-quay {
  max-width: 100%;
}
.card {
  border-radius: 8px;
}
.table-responsive {
  overflow-x: auto;
}
.modal-dialog {
  max-width: 90%;
}
.modal-content {
  border-radius: 8px;
}
.btn-sm {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
}
.form-control-sm {
  height: calc(1.5em + 0.5rem + 2px);
}
.badge {
  font-size: 0.75rem;
}
</style>
```