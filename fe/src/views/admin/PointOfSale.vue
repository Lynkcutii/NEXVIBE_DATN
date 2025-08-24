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
                <div class="col-lg-8 mb-4 mb-lg-0">
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
                                  Mã: {{ sp.maCtSanPham }} <br> Size: {{ sp.tenKichThuoc }} | Màu: {{ sp.tenMauSac }}
                                </div>
                                <div v-if="sp.selectedVoucher" class="mt-1">
                                  <span class="badge bg-success">Voucher: {{ sp.selectedVoucher.tenVoucher }}</span>
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
                                <span class="fw-bold text-primary">{{ sp.giaBan != null ? (sp.giaBan * sp.soLuong - (sp.selectedVoucher ? tinhGiamGiaSanPham(sp) : 0)).toLocaleString() : '0' }} VND</span>
                              </td>
                              <td>
                                <button
                                  type="button"
                                  class="btn btn-primary btn-sm"
                                  @click="showVoucherModal(hd, sp)"
                                >
                                  <i class="bi bi-tag"></i> Voucher
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
                            class="btn btn-primary btn-sm"
                            @click="showCustomerModal(hd)"
                          >
                            <i class="bi bi-person-plus me-1"></i> Chọn
                          </button>
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
                        <div class="d-flex justify-content-between mb-2">
                          <button
                            type="button"
                            class="btn btn-warning btn-sm"
                            :disabled="hd.sanPhams.length === 0"
                            @click="openThanhToan(hd)"
                          >
                            Phương thức thanh toán
                          </button>
                          <span v-if="hd.thanhToan" class="text-primary">
                            {{ hd.thanhToan.soTien.toLocaleString() }} VND
                            <i :class="hd.thanhToan.phuongThuc === 'CHUYỂN KHOẢN' ? 'bi bi-bank' : 'bi bi-cash'" class="ms-2"></i>
                            <span v-if="hd.thanhToan.maGiaoDich" class="text-muted ms-1">({{ hd.thanhToan.maGiaoDich }})</span>
                          </span>
                          <span v-else>0 VND</span>
                        </div>
                      </div>
                      <button
                        type="button"
                        class="btn btn-success w-100"
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
                <select v-model="selectedColor" class="form-select" @change="debouncedSearchProducts">
                  <option value="" disabled>Chọn màu sắc</option>
                  <option v-for="color in colors" :key="color.value" :value="color.value">{{ color.label }}</option>
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
          <div class="modal-body">
            <div v-if="productVouchers.length === 0" class="text-center py-3">
              <p>Không có voucher áp dụng được</p>
            </div>
            <div v-else>
              <div v-for="voucher in productVouchers" :key="voucher.id" class="form-check">
                <input
                  class="form-check-input"
                  type="radio"
                  :value="voucher.id"
                  v-model="selectedProduct.selectedVoucherId"
                  :id="'voucher-' + voucher.id"
                  @change="chonVoucherSanPham(selectedHoaDon, selectedProduct)"
                />
                <label class="form-check-label" :for="'voucher-' + voucher.id">
                  {{ voucher.tenVoucher }} ({{ voucher.hinhThucGiam === '%' ? voucher.mucGiam + '%' : voucher.giaGiam.toLocaleString() + ' VND' }})
                  <span v-if="voucher.giaTriDonHangToiThieu"> | Tối thiểu: {{ voucher.giaTriDonHangToiThieu.toLocaleString() }} VND</span>
                </label>
              </div>
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
                    <td>{{ sp.giaBan != null ? (sp.giaBan * sp.soLuong - (sp.selectedVoucher ? tinhGiamGiaSanPham(sp) : 0)).toLocaleString() : '0' }} VND</td>
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
    <div v-if="modalThanhToan" class="modal fade show d-block" tabindex="-1" style="background: rgba(0,0,0,0.6);">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header bg-warning text-white">
            <h5 class="modal-title">THANH TOÁN</h5>
            <button type="button" class="btn-close btn-close-white" @click="modalThanhToan = false"></button>
          </div>
          <div class="modal-body text-center">
            <div class="mb-3">
              <h6>Tổng tiền cần thanh toán</h6>
              <p class="fw-bold text-danger">{{ tinhTongTien(thanhToanHoaDon).toLocaleString() }} VND</p>
            </div>
            <div class="d-flex justify-content-center gap-2 mb-3">
              <button
                v-for="ptt in phuongThucThanhToans"
                :key="ptt.idPTT"
                type="button"
                class="btn btn-outline-primary"
                :class="{ active: thanhToanPhuongThuc === ptt.ten }"
                @click="thanhToanPhuongThuc = ptt.ten"
              >
                {{ ptt.ten }}
              </button>
            </div>
            <div class="mb-3">
              <div class="row">
                <div class="col-6">
                  <div class="form-floating">
                    <input
                      v-model.number="thanhToanTienKhachDua"
                      type="number"
                      class="form-control"
                      id="floatingCash"
                      placeholder="Tiền khách đưa"
                      :min="0"
                      @input="validateTienKhachDua"
                    />
                    <label for="floatingCash">Tiền khách đưa</label>
                  </div>
                </div>
                <div class="col-6" v-if="thanhToanPhuongThuc === 'CHUYỂN KHOẢN'">
                  <div class="form-floating">
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
              <div class="text-end mt-2" v-if="thanhToanTienKhachDua">
                <p class="mb-0" v-if="thanhToanTienKhachDua < tinhTongTien(thanhToanHoaDon)">
                  <span class="text-danger">Tiền thiếu: {{ (tinhTongTien(thanhToanHoaDon) - thanhToanTienKhachDua).toLocaleString() }} VND</span>
                </p>
                <p class="mb-0" v-else-if="thanhToanTienKhachDua > tinhTongTien(thanhToanHoaDon)">
                  <span class="text-success">Tiền thừa trả lại: {{ (thanhToanTienKhachDua - tinhTongTien(thanhToanHoaDon)).toLocaleString() }} VND</span>
                </p>
                <p class="mb-0" v-else>
                  <span class="text-success">Đủ tiền thanh toán</span>
                </p>
              </div>
            </div>
            <table class="table table-striped" v-if="thanhToanLichSu.length > 0">
              <thead>
                <tr>
                  <th>STT</th>
                  <th>Mã giao dịch</th>
                  <th>Phương thức</th>
                  <th>Số tiền</th>
                  <th>Hành động</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(t, index) in thanhToanLichSu" :key="index">
                  <td>{{ index + 1 }}</td>
                  <td>{{ t.maGiaoDich || 'N/A' }}</td>
                  <td>{{ t.phuongThuc }}</td>
                  <td>{{ t.soTien.toLocaleString() }} VND</td>
                  <td>
                    <button
                      type="button"
                      class="btn btn-danger btn-sm"
                      @click="xoaThanhToan(index)"
                    >
                      <i class="bi bi-trash"></i>
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="modalThanhToan = false">Đóng</button>
            <button
              type="button"
              class="btn btn-success"
              :disabled="thanhToanTienKhachDua < tinhTongTien(thanhToanHoaDon)"
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
                    <td>{{ customer.hoTen }}</td>
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
      timeoutIds: new Map(), // Lưu trữ ID của setTimeout cho mỗi hóa đơn
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
    this.startCleanupInterval();
  },
  beforeUnmount() {
    this.timeoutIds.forEach((timeoutId) => clearTimeout(timeoutId));
    this.timeoutIds.clear();
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
        await this.checkAuth();
        const thirtyMinutesAgo = new Date(Date.now() - 30 * 60 * 1000).toISOString();
        const response = await axios.get('http://localhost:8080/admin/api/hoadon', {
          params: {
            trangThai: 'CHO_THANH_TOAN',
            fromDate: thirtyMinutesAgo,
          },
          withCredentials: true,
        });

        let invoices = [];
        if (Array.isArray(response.data)) {
          invoices = response.data;
        } else if (response.data && Array.isArray(response.data.content)) {
          invoices = response.data.content;
        } else {
          console.warn('Dữ liệu hóa đơn không hợp lệ hoặc rỗng:', response.data);
          this.hoaDons = [];
          ElMessage.info('Không có hóa đơn đang chờ thanh toán trong 30 phút qua.');
          return;
        }

        this.hoaDons = invoices
          .filter(hd => hd.trangThai === 'CHO_THANH_TOAN')
          .map(hd => {
            const createdAt = new Date(hd.ngayTao).getTime();
            const now = new Date().getTime();
            const elapsedSeconds = Math.floor((now - createdAt) / 1000);
            const initialCountdown = 300;
            const countdown = Math.max(0, initialCountdown - elapsedSeconds);

            return {
              idHD: hd.idHD,
              maHoaDon: hd.maHD,
              sanPhams: (hd.chiTietSanPham || []).map(ct => ({
                id: ct.idCtSanPham || ct.id,
                idHDCT: ct.idHDCT || null,
                maCtSanPham: ct.maSPCT,
                tenGiay: ct.tenSanPham || 'N/A',
                giaBan: Number(ct.donGia) || 0,
                soLuong: Number(ct.soLuong) || 1,
                soLuongTonKho: Number(ct.soLuongTonKho) || 0,
                tenKichThuoc: ct.tenSize || 'N/A',
                tenMauSac: ct.tenMauSac || 'N/A',
                tenThuongHieu: ct.tenThuongHieu || 'N/A',
                tenChatLieu: ct.tenChatLieu || 'N/A',
                anhGiay: ct.anhGiay || 'https://via.placeholder.com/60',
                selectedVoucher: ct.idKM ? { id: ct.idKM, tenVoucher: ct.tenVoucher, ...ct.voucher } : null,
                selectedVoucherId: ct.idKM || null,
              })),
              khachHang: hd.customerName || 'Khách lẻ',
              idKhachHang: hd.idKhachHang || null,
              ngayTao: hd.ngayTao || new Date().toISOString(),
              createdAt: createdAt,
              countdown: countdown,
              thanhToan: hd.idPT ? {
                phuongThuc: hd.phuongThucThanhToan?.ten || 'TIỀN MẶT',
                soTien: Number(hd.tongTien) || 0,
                maGiaoDich: hd.maGiaoDich || null,
                idPT: Number(hd.idPT) || null,
              } : null,
              trangThai: hd.trangThai || 'CHO_THANH_TOAN',
              idNhanVien: hd.idNhanVien || null,
            };
          });

        if (this.hoaDons.length > 0) {
          this.tabActive = this.hoaDons[0].maHoaDon;
        } else {
          this.tabActive = '';
          ElMessage.info('Không có hóa đơn đang chờ thanh toán trong 30 phút qua.');
        }
      } catch (error) {
        console.error('Lỗi khi tải hóa đơn:', error.response?.data || error.message);
        let errorMessage = 'Không thể tải danh sách hóa đơn.';
        if (error.response) {
          if (error.response.status === 401 || error.response.status === 403) {
            errorMessage = 'Phiên đăng nhập hết hạn hoặc không có quyền truy cập!';
            this.router.push('/login');
          } else {
            errorMessage = error.response.data?.message || error.message;
          }
        }
        ElMessage.error(errorMessage);
        this.hoaDons = [];
        this.tabActive = '';
      }
    },
    startCleanupInterval() {
      this.cleanupInterval = setInterval(() => {
        const now = new Date().getTime();
        this.hoaDons = this.hoaDons.filter(hd => {
          if (hd.trangThai === 'CHO_THANH_TOAN' && hd.countdown > 0) {
            hd.countdown -= 1;
            if (hd.countdown <= 0) {
              this.xoaHoaDon(hd);
              ElMessage.info(`Hóa đơn ${hd.maHoaDon} đã bị xóa do hết thời gian chờ.`);
              return false;
            }
            return true;
          }
          return true;
        });
        if (this.hoaDons.length === 0) {
          this.tabActive = '';
        }
      }, 1000);
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

        const hd = {
          idHD: response.data.idHD,
          maHoaDon: response.data.maHD,
          sanPhams: [],
          khachHang: response.data.customerName || 'Khách lẻ',
          idKhachHang: response.data.idKhachHang || null,
          ngayTao: response.data.ngayTao,
          createdAt: new Date().getTime(),
          countdown: 300,
          thanhToan: null,
          trangThai: response.data.trangThai,
          idNhanVien: response.data.idNhanVien,
        };

        this.hoaDons.push(hd);
        this.tabActive = hd.maHoaDon;
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
          minPrice: Number(this.priceRange[0]),
          maxPrice: Number(this.priceRange[1]),
        };

        const response = await axios.get('http://localhost:8080/admin/api/san-pham', {
          params,
          withCredentials: true,
        });

        console.log('Dữ liệu API sản phẩm:', response.data);

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
            anhGiay: item.imageLink || 'https://via.placeholder.com/60',
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

        console.log('Dữ liệu API sản phẩm chi tiết:', response.data);

        const dataContent = Array.isArray(response.data) ? response.data : [];

        this.listSanPhamChiTiet = {
          content: dataContent.map(item => ({
            id: item.id || null,
            maSPCT: item.maSPCT || 'N/A',
            tenSP: item.tenSP || 'Không xác định',
            tenGiay: item.tenSP || 'Không xác định',
            giaBan: item.gia != null ? Number(item.gia) : 0,
            soLuongTonKho: Number(item.soLuong) || 0,
            tenMauSac: item.tenMauSac || 'N/A',
            tenKichThuoc: item.tenSize || 'N/A',
            anhGiay: item.linkAnhDauTien || (item.imageLinks && item.imageLinks.length > 0 ? item.imageLinks[0] : 'https://via.placeholder.com/60'),
            moTa: item.moTa || '',
            trangThai: item.trangThai != null ? item.trangThai : true,
          })),
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
    async chonSanPhamChiTiet(spct) {
      if (spct.giaBan == null || isNaN(spct.giaBan) || spct.giaBan <= 0) {
        ElMessage.error(`Sản phẩm ${spct.tenSP} không có giá bán hợp lệ!`);
        return;
      }
      if (spct.soLuongTonKho == null || isNaN(spct.soLuongTonKho) || spct.soLuongTonKho <= 0) {
        ElMessage.error(`Sản phẩm ${spct.tenSP} không có tồn kho hợp lệ!`);
        return;
      }
      const existed = this.hoaDonChon.sanPhams.find(x => x.id === spct.id);
      const tonKho = Number(spct.soLuongTonKho);
      const productResponse = await axios.get(`http://localhost:8080/admin/api/sanphamchitiet/${spct.id}`, {
        withCredentials: true,
      });
      if (!productResponse.data || !productResponse.data.idSP) {
        ElMessage.error(`Không tìm thấy idSP cho sản phẩm chi tiết ${spct.id}`);
        return;
      }
      const idSP = Number(productResponse.data.idSP);
      console.log(`chonSanPhamChiTiet: idSP=${idSP}, idCtSanPham=${spct.id}`);

      if (existed) {
        if (existed.soLuong + 1 > tonKho) {
          ElMessage.error('Vượt quá số lượng tồn kho!');
        } else {
          existed.soLuong += 1;
          existed.idSP = idSP;
          await this.capNhatHoaDonChiTiet(existed, this.hoaDonChon);
        }
      } else {
        const newProduct = {
          id: spct.id,
          idSP: idSP,
          idHDCT: null,
          maCtSanPham: spct.maSPCT,
          tenGiay: spct.tenSP,
          giaBan: Number(spct.giaBan),
          soLuong: 1,
          soLuongTonKho: tonKho,
          tenKichThuoc: spct.tenKichThuoc,
          tenMauSac: spct.tenMauSac,
          anhGiay: spct.anhGiay,
          selectedVoucher: null,
          selectedVoucherId: null,
          moTa: spct.moTa,
        };
        this.hoaDonChon.sanPhams.push(newProduct);
        await this.themHoaDonChiTiet(newProduct, this.hoaDonChon);
      }
      this.productDetailModal = false;
      ElMessage.success(`Đã thêm sản phẩm ${spct.tenSP} (${spct.tenMauSac}, ${spct.tenKichThuoc}) vào hóa đơn`);
    },
    async themHoaDonChiTiet(sp, hd) {
      try {
        console.log(`themHoaDonChiTiet: Thêm chi tiết hóa đơn cho sản phẩm ${sp.tenGiay}, soLuong=${sp.soLuong}, idHD=${hd.idHD}`);
        const chiTietDTO = {
          idHD: Number(hd.idHD),
          idCtSanPham: Number(sp.id),
          idSP: Number(sp.idSP),
          soLuong: Number(sp.soLuong),
          donGia: Number(sp.giaBan).toFixed(2),
          thanhTien: Number(sp.giaBan * sp.soLuong).toFixed(2),
          ngayTao: new Date().toISOString(),
          ngaySua: new Date().toISOString(),
          trangThai: true,
          tenSanPham: sp.tenGiay,
          tenSize: sp.tenKichThuoc,
          tenMauSac: sp.tenMauSac,
        };
        console.log(`themHoaDonChiTiet: Dữ liệu gửi đi:`, JSON.stringify(chiTietDTO, null, 2));
        const response = await axios.post('http://localhost:8080/admin/api/hoadonchitiet', chiTietDTO, {
          withCredentials: true,
          headers: { 'Content-Type': 'application/json; charset=UTF-8' },
        });
        console.log(`themHoaDonChiTiet: Thành công, idHDCT=${response.data.idHDCT}`);
        sp.idHDCT = response.data.idHDCT;
      } catch (error) {
        console.error('Lỗi khi thêm chi tiết hóa đơn:', error.response?.data || error.message);
        ElMessage.error('Không thể thêm chi tiết hóa đơn!');
      }
    },
    async capNhatHoaDonChiTiet(sp, hd) {
      try {
        console.log(`capNhatHoaDonChiTiet: Cập nhật chi tiết hóa đơn cho sản phẩm ${sp.tenGiay}, soLuong=${sp.soLuong}, idHDCT=${sp.idHDCT}, idHD=${hd.idHD}`);
        const chiTietDTO = {
          idHDCT: sp.idHDCT || null,
          idHD: Number(hd.idHD),
          idCtSanPham: Number(sp.id),
          idSP: Number(sp.idSP),
          soLuong: Number(sp.soLuong),
          donGia: Number(sp.giaBan).toFixed(2),
          thanhTien: Number(sp.giaBan * sp.soLuong).toFixed(2),
          ngayTao: sp.ngayTao || new Date().toISOString(),
          ngaySua: new Date().toISOString(),
          trangThai: true,
          tenSanPham: sp.tenGiay,
          tenSize: sp.tenKichThuoc,
          tenMauSac: sp.tenMauSac,
        };
        console.log(`capNhatHoaDonChiTiet: Dữ liệu gửi đi:`, JSON.stringify(chiTietDTO, null, 2));
        const response = await axios.put(`http://localhost:8080/admin/api/hoadonchitiet/${sp.idHDCT || sp.id}`, chiTietDTO, {
          withCredentials: true,
          headers: { 'Content-Type': 'application/json; charset=UTF-8' },
        });
        console.log(`capNhatHoaDonChiTiet: Thành công, idHDCT=${response.data.idHDCT}`);
        sp.idHDCT = response.data.idHDCT;
      } catch (error) {
        console.error('Lỗi khi cập nhật chi tiết hóa đơn:', error.response?.data || error.message);
        ElMessage.error('Không thể cập nhật chi tiết hóa đơn!');
      }
    },
    async xacNhanThanhToan() {
      try {
        console.log('xacNhanThanhToan: Bắt đầu, maHoaDon=' + this.thanhToanHoaDon.maHoaDon);
        await this.checkAuth();
        if (!this.auth.isAuthenticated || !this.auth.user || !this.auth.isAdmin()) {
          ElMessage.error('Phiên đăng nhập hết hạn, vui lòng đăng nhập lại!');
          this.router.push('/login');
          return;
        }

        if (!this.thanhToanHoaDon.sanPhams || this.thanhToanHoaDon.sanPhams.length === 0) {
          ElMessage.error('Hóa đơn không có sản phẩm!');
          return;
        }

        if (this.thanhToanTienKhachDua < this.tinhTongTien(this.thanhToanHoaDon)) {
          ElMessage.error('Số tiền khách đưa không đủ!');
          return;
        }

        if (this.thanhToanPhuongThuc === 'CHUYỂN KHOẢN' && !this.thanhToanMaGiaoDich) {
          ElMessage.error('Vui lòng nhập mã giao dịch cho phương thức chuyển khoản!');
          return;
        }

        const selectedPTT = this.phuongThucThanhToans.find(ptt => ptt.ten === this.thanhToanPhuongThuc);
        if (!selectedPTT || !selectedPTT.idPTT || isNaN(selectedPTT.idPTT)) {
          ElMessage.error('Phương thức thanh toán không hợp lệ!');
          return;
        }

        const hoaDonUpdateDTO = {
          idHD: Number(this.thanhToanHoaDon.idHD),
          maHD: this.thanhToanHoaDon.maHoaDon,
          idNhanVien: Number(this.thanhToanHoaDon.idNhanVien),
          idKhachHang: this.thanhToanHoaDon.idKhachHang ? Number(this.thanhToanHoaDon.idKhachHang) : null,
          idPT: Number(selectedPTT.idPTT),
          maGiaoDich: this.thanhToanPhuongThuc === 'CHUYỂN KHOẢN' ? this.thanhToanMaGiaoDich : null,
          ngayTao: this.thanhToanHoaDon.ngayTao,
          ngaySua: new Date().toISOString(),
          trangThai: 'CHO_THANH_TOAN',
          loaiHoaDon: 'Tại quầy',
          tongTien: Number(this.tinhTongTien(this.thanhToanHoaDon)).toFixed(2),
          chiTietSanPham: this.thanhToanHoaDon.sanPhams.map(sp => ({
            idHDCT: sp.idHDCT || null,
            idCtSanPham: Number(sp.id),
            idSP: Number(sp.idSP) || null,
            soLuong: Number(sp.soLuong),
            donGia: Number(sp.giaBan).toFixed(2),
            thanhTien: Number(sp.giaBan * sp.soLuong - (sp.selectedVoucher ? this.tinhGiamGiaSanPham(sp) : 0)).toFixed(2),
            idKM: sp.selectedVoucherId ? Number(sp.selectedVoucherId) : null,
            ngayTao: sp.ngayTao || new Date().toISOString(),
            ngaySua: new Date().toISOString(),
            trangThai: true,
            tenSanPham: sp.tenGiay,
            tenSize: sp.tenKichThuoc,
            tenMauSac: sp.tenMauSac,
          })),
        };

        console.log('Cập nhật hóa đơn:', JSON.stringify(hoaDonUpdateDTO, null, 2));

        const response = await axios.put(
          `http://localhost:8080/admin/api/hoadon/${hoaDonUpdateDTO.idHD}`,
          hoaDonUpdateDTO,
          {
            withCredentials: true,
            headers: { 'Content-Type': 'application/json; charset=UTF-8' },
          }
        );

        console.log('Phản hồi cập nhật hóa đơn:', response.data);

        this.thanhToanHoaDon.thanhToan = {
          phuongThuc: this.thanhToanPhuongThuc,
          soTien: Number(this.thanhToanTienKhachDua),
          maGiaoDich: this.thanhToanPhuongThuc === 'CHUYỂN KHOẢN' ? this.thanhToanMaGiaoDich : null,
          idPT: Number(selectedPTT.idPTT),
        };

        this.thanhToanLichSu.push({ ...this.thanhToanHoaDon.thanhToan });
        this.modalThanhToan = false;
        ElMessage.success('Xác nhận thanh toán thành công! Vui lòng nhấn "Xác nhận đặt hàng & In hóa đơn" để tiếp tục.');
      } catch (error) {
        console.error('Lỗi khi xác nhận thanh toán:', error.response?.data || error.message);
        let errorMessage = 'Có lỗi xảy ra khi xác nhận thanh toán!';
        if (error.response?.status === 401) {
          errorMessage = 'Phiên đăng nhập hết hạn, vui lòng đăng nhập lại!';
          this.router.push('/login');
        } else if (error.response?.status === 400) {
          const errors = error.response?.data?.errors || error.response?.data?.message || error.response?.data?.error;
          errorMessage = Array.isArray(errors) ? errors.join('; ') : errors || 'Dữ liệu gửi lên không hợp lệ!';
        } else if (error.response?.status === 403) {
          errorMessage = 'Bạn không có quyền thực hiện hành động này!';
          this.router.push('/login');
        } else if (error.response?.data?.message) {
          errorMessage = error.response.data.message;
        } else if (error.request) {
          errorMessage = 'Không nhận được phản hồi từ server!';
        } else {
          errorMessage = error.message || 'Lỗi không xác định!';
        }
        ElMessage.error(errorMessage);
      }
    },
    async inHoaDon() {
        try {
            console.log('inHoaDon: Bắt đầu, maHoaDon=' + this.hoaDonIn?.maHoaDon);
            await this.checkAuth();
            if (!this.auth.isAuthenticated || !this.auth.user || !this.auth.isAdmin()) {
                ElMessage.error('Phiên đăng nhập hết hạn hoặc không phải admin!');
                this.router.push('/login');
                return;
            }

            if (!this.hoaDonIn || !this.hoaDonIn.thanhToan) {
                ElMessage.error('Hóa đơn chưa được thanh toán!');
                return;
            }

            if (this.hoaDonIn.thanhToan.soTien < this.tinhTongTien(this.hoaDonIn)) {
                ElMessage.error('Số tiền thanh toán không đủ!');
                return;
            }

            const hoaDonResponse = await axios.get(`http://localhost:8080/admin/api/hoadon/${this.hoaDonIn.idHD}`, {
                withCredentials: true,
            });

            if (!hoaDonResponse.data) {
                throw new Error(`Không tìm thấy hóa đơn ${this.hoaDonIn.idHD}`);
            }

            const pttResponse = await axios.get(`http://localhost:8080/admin/api/phuongthucthanhtoan/${this.hoaDonIn.thanhToan.idPT}`, {
                withCredentials: true,
            });

            if (!pttResponse.data) {
                throw new Error(`Phương thức thanh toán ${this.hoaDonIn.thanhToan.idPT} không tồn tại`);
            }

            const chiTietSanPhamServer = hoaDonResponse.data.chiTietSanPham || [];

            const chiTietSanPham = [];
            for (const sp of this.hoaDonIn.sanPhams) {
                if (!sp.id || isNaN(sp.id)) {
                    throw new Error(`ID sản phẩm chi tiết không hợp lệ cho sản phẩm ${sp.tenGiay}`);
                }
                if (sp.soLuong <= 0 || isNaN(sp.soLuong)) {
                    throw new Error(`Số lượng không hợp lệ cho sản phẩm ${sp.tenGiay}`);
                }
                if (sp.giaBan <= 0 || isNaN(sp.giaBan)) {
                    throw new Error(`Giá bán không hợp lệ cho sản phẩm ${sp.tenGiay}`);
                }

                // Lấy thông tin sản phẩm chi tiết mới nhất để kiểm tra tồn kho
                const productResponse = await axios.get(`http://localhost:8080/admin/api/sanphamchitiet/${sp.id}`, {
                    withCredentials: true,
                });

                if (!productResponse.data || !productResponse.data.idSP) {
                    throw new Error(`Không tìm thấy sản phẩm chi tiết ${sp.id} hoặc thiếu idSP`);
                }

                sp.idSP = Number(productResponse.data.idSP);
                sp.soLuongTonKho = Number(productResponse.data.soLuong); // Cập nhật số lượng tồn kho
                console.log(`inHoaDon: idSP=${sp.idSP}, soLuongTonKho=${sp.soLuongTonKho} cho sản phẩm ${sp.tenGiay}, idCtSanPham=${sp.id}`);

                // Kiểm tra tồn kho trước khi xác nhận
                if (sp.soLuongTonKho < sp.soLuong) {
                    throw new Error(`Số lượng tồn kho không đủ cho sản phẩm ${sp.tenGiay}. Tồn kho hiện tại: ${sp.soLuongTonKho}`);
                }

                const thanhTien = Number(sp.giaBan * sp.soLuong - (sp.selectedVoucher ? this.tinhGiamGiaSanPham(sp) : 0));
                if (thanhTien <= 0 || isNaN(thanhTien)) {
                    throw new Error(`Thành tiền không hợp lệ cho sản phẩm ${sp.tenGiay}`);
                }

                const existingChiTiet = chiTietSanPhamServer.find(ct => ct.idCtSanPham === Number(sp.id)) || {};

                chiTietSanPham.push({
                    idHDCT: sp.idHDCT || existingChiTiet.idHDCT || null,
                    idCtSanPham: Number(sp.id),
                    idSP: Number(sp.idSP),
                    idHD: Number(this.hoaDonIn.idHD),
                    soLuong: Number(sp.soLuong),
                    donGia: Number(sp.giaBan).toFixed(2),
                    thanhTien: thanhTien.toFixed(2),
                    idKM: sp.selectedVoucherId ? Number(sp.selectedVoucherId) : null,
                    ngayTao: sp.ngayTao || existingChiTiet.ngayTao || new Date().toISOString(),
                    ngaySua: new Date().toISOString(),
                    trangThai: true,
                    tenSanPham: sp.tenGiay,
                    tenSize: sp.tenKichThuoc,
                    tenMauSac: sp.tenMauSac,
                });
            }

            const hoaDonData = {
                idHD: Number(this.hoaDonIn.idHD),
                maHD: this.hoaDonIn.maHoaDon,
                idKhachHang: this.hoaDonIn.idKhachHang ? Number(this.hoaDonIn.idKhachHang) : null,
                idNhanVien: Number(this.auth.user.idNV),
                idKM: this.hoaDonIn.idKM ? Number(this.hoaDonIn.idKM) : null,
                ngayTao: this.hoaDonIn.ngayTao,
                ngaySua: new Date().toISOString(),
                tongTien: Number(this.tinhTongTien(this.hoaDonIn)).toFixed(2),
                trangThai: 'HOAN_THANH',
                loaiHoaDon: 'Tại quầy',
                customerName: this.hoaDonIn.khachHang || 'Khách lẻ',
                totalProducts: this.hoaDonIn.sanPhams.length,
                idPT: Number(this.hoaDonIn.thanhToan.idPT),
                maGiaoDich: this.hoaDonIn.thanhToan.maGiaoDich || null,
                chiTietSanPham: chiTietSanPham,
            };

            console.log('inHoaDon: Dữ liệu hóa đơn gửi đi:', JSON.stringify(hoaDonData, null, 2));

            // Cập nhật hóa đơn
            const response = await axios.put(`http://localhost:8080/admin/api/hoadon/${hoaDonData.idHD}`, hoaDonData, {
                withCredentials: true,
                headers: { 'Content-Type': 'application/json; charset=UTF-8' },
            });

            console.log('inHoaDon: Phản hồi từ server:', JSON.stringify(response.data, null, 2));

            // Gọi API để trừ tồn kho
            await axios.post(`http://localhost:8080/admin/api/hoadonchitiet/tru-ton-kho/${hoaDonData.idHD}`, null, {
                withCredentials: true,
                headers: { 'Content-Type': 'application/json; charset=UTF-8' },
            });

            window.print();
            ElMessage.success('Xác nhận và in hóa đơn thành công!');

            // Xóa hóa đơn đã hoàn thành khỏi danh sách
            this.hoaDons = this.hoaDons.filter(hd => hd.maHoaDon !== this.hoaDonIn.maHoaDon);
            if (this.hoaDons.length > 0) {
                this.tabActive = this.hoaDons[0].maHoaDon;
            } else {
                this.tabActive = '';
            }

            // Làm mới danh sách sản phẩm và sản phẩm chi tiết để cập nhật tồn kho
            await this.fetchSanPhamList();
            if (this.selectedSanPham) {
                await this.fetchSanPhamChiTietList(this.selectedSanPham.id);
            }

            this.modalHoaDon = false;
        } catch (error) {
            console.error('inHoaDon: Lỗi khi in và xác nhận hóa đơn:', error.response?.data || error.message);
            let errorMessage = 'Có lỗi xảy ra khi in và xác nhận hóa đơn!';
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
    async xoaHoaDon(hd, retryCount = 3) {
      try {
          await this.checkAuth();
          if (hd.idHD) {
              await axios.delete(`http://localhost:8080/admin/api/hoadon/${hd.idHD}`, {
                  withCredentials: true,
              });
              ElMessage.success(`Đã xóa hóa đơn ${hd.maHoaDon}`);
          }
          // Xóa timeout và cập nhật danh sách hóa đơn
          this.timeoutIds.delete(hd.idHD);
          this.hoaDons = this.hoaDons.filter(item => item.maHoaDon !== hd.maHoaDon);
          if (this.hoaDons.length > 0) {
              this.tabActive = this.hoaDons[0].maHoaDon;
          } else {
              this.tabActive = '';
          }
      } catch (error) {
          console.error('Lỗi khi xóa hóa đơn:', error.response?.data || error.message);
          if (retryCount > 0 && error.response?.status === 401) {
              console.log(`Thử lại xoaHoaDon cho ${hd.maHoaDon}, số lần thử còn lại: ${retryCount}`);
              await this.checkAuth(); // Xác thực lại
              return this.xoaHoaDon(hd, retryCount - 1);
          }
          ElMessage.error(`Không thể xóa hóa đơn ${hd.maHoaDon}: ${error.response?.data?.message || error.message}`);
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
        console.error('Lỗi khi lấy danh mục:', error.response?.data || error.message);
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
        console.error('Lỗi khi lấy thương hiệu:', error.response?.data || error.message);
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
        console.error('Lỗi khi lấy màu sắc:', error.response?.data || error.message);
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
        console.error('Lỗi khi lấy chất liệu:', error.response?.data || error.message);
        ElMessage.error('Không thể tải danh sách chất liệu');
      }
    },
    async fetchPhuongThucThanhToan() {
      try {
        await this.checkAuth();
        const response = await axios.get('http://localhost:8080/admin/api/phuongthucthanhtoan', {
          withCredentials: true,
        });
        this.phuongThucThanhToans = response.data.filter(ptt => ptt.ten === 'CHUYỂN KHOẢN' || ptt.ten === 'TIỀN MẶT');
        if (this.phuongThucThanhToans.length === 0) {
          console.warn('Không tìm thấy phương thức thanh toán, sử dụng mặc định');
          this.phuongThucThanhToans = [
            { idPTT: 1, ten: 'TIỀN MẶT' },
            { idPTT: 2, ten: 'CHUYỂN KHOẢN' },
          ];
        }
      } catch (error) {
        console.error('Lỗi khi lấy phương thức thanh toán:', error.response?.data || error.message);
        this.phuongThucThanhToans = [
          { idPTT: 1, ten: 'TIỀN MẶT' },
          { idPTT: 2, ten: 'CHUYỂN KHOẢN' },
        ];
        ElMessage.error('Không thể tải danh sách phương thức thanh toán');
      }
    },
    async showVoucherModal(hd, product) {
      this.selectedHoaDon = hd;
      this.selectedProduct = product;
      this.voucherModal = true;
      await this.fetchVouchersForProduct(product.id);
    },
    async fetchVouchersForProduct(idSPCT) {
      try {
        await this.checkAuth();
        const response = await axios.post('http://localhost:8080/api/voucher/applicable', [idSPCT], {
          withCredentials: true,
          headers: { 'Content-Type': 'application/json; charset=UTF-8' },
        });
        this.productVouchers = Array.isArray(response.data) ? response.data : [];
        if (this.productVouchers.length === 0) {
          ElMessage.info('Không có voucher áp dụng được cho sản phẩm này.');
        }
      } catch (error) {
        console.error('Lỗi khi lấy voucher cho sản phẩm:', error.response?.data || error.message);
        ElMessage.error('Không thể tải danh sách voucher cho sản phẩm');
        this.productVouchers = [];
      }
    },
    chonVoucherSanPham(hd, product) {
      product.selectedVoucher = this.productVouchers.find(v => v.id === product.selectedVoucherId) || null;
      if (product.selectedVoucher) {
        const tongTienSanPham = product.giaBan * product.soLuong;
        const minAmount = product.selectedVoucher.giaTriDonHangToiThieu || 0;
        if (tongTienSanPham < minAmount) {
          ElMessage.warning(`Tổng tiền sản phẩm ${product.tenGiay} (${tongTienSanPham.toLocaleString()} VND) không đủ điều kiện áp dụng voucher ${product.selectedVoucher.tenVoucher}`);
          product.selectedVoucher = null;
          product.selectedVoucherId = null;
        }
      }
    },
    tinhGiamGiaSanPham(sp) {
      if (!sp.selectedVoucher) return 0;
      const tongTien = sp.giaBan * sp.soLuong;
      if (sp.selectedVoucher.hinhThucGiam === '%') {
        let giamGia = Math.floor(tongTien * (sp.selectedVoucher.mucGiam / 100));
        if (sp.selectedVoucher.giamToiDa && giamGia > sp.selectedVoucher.giamToiDa) {
          giamGia = sp.selectedVoucher.giamToiDa;
        }
        return giamGia;
      } else if (sp.selectedVoucher.giaGiam) {
        return sp.selectedVoucher.giaGiam;
      } else if (sp.selectedVoucher.donGiaKhiGiam) {
        return tongTien - sp.selectedVoucher.donGiaKhiGiam;
      }
      return 0;
    },
    formatDate(date) {
      if (!date) return '';
      const d = new Date(date);
      return d.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
    },
    taiLaiDanhSachSanPham() {
      this.productSearchQuery = '';
      this.selectedCategory = '';
      this.selectedBrand = '';
      this.selectedColor = '';
      this.selectedMaterial = '';
      this.priceRange = [0, 5000000];
      this.pageSanPham = 1;
      this.fetchSanPhamList();
    },
    onProductPageChange(page) {
      this.pageSanPham = page;
      this.fetchSanPhamList();
    },
    debouncedSearchProducts: _.debounce(function() {
      this.pageSanPham = 1;
      this.fetchSanPhamList();
    }, 300),
    tinhTienHang(hd) {
      return hd.sanPhams.reduce((sum, sp) => sum + (Number(sp.giaBan) * Number(sp.soLuong)), 0);
    },
    tinhGiamGia(hd) {
      return hd.sanPhams.reduce((sum, sp) => sum + (sp.selectedVoucher ? this.tinhGiamGiaSanPham(sp) : 0), 0);
    },
    tinhTongTien(hd) {
      let tong = this.tinhTienHang(hd);
      tong -= this.tinhGiamGia(hd);
      return tong > 0 ? Number(tong) : 0;
    },
    xoaSanPham(hd, sp) {
      hd.sanPhams = hd.sanPhams.filter(item => item.id !== sp.id);
      ElMessage.success(`Đã xóa sản phẩm ${sp.tenGiay} khỏi hóa đơn`);
    },
    openThanhToan(hd) {
      this.thanhToanHoaDon = hd;
      this.thanhToanTongTien = this.tinhTongTien(hd);
      this.thanhToanPhuongThuc = this.phuongThucThanhToans[0]?.ten || 'TIỀN MẶT';
      this.thanhToanTienKhachDua = 0;
      this.thanhToanMaGiaoDich = '';
      this.thanhToanLichSu = hd.thanhToan ? [{ ...hd.thanhToan }] : [];
      this.modalThanhToan = true;
    },
    validateTienKhachDua() {
      if (this.thanhToanTienKhachDua < 0 || isNaN(this.thanhToanTienKhachDua)) {
        this.thanhToanTienKhachDua = 0;
        ElMessage.warning('Số tiền không được âm hoặc không hợp lệ!');
      }
    },
    xoaThanhToan(index) {
      this.thanhToanLichSu.splice(index, 1);
      if (this.thanhToanLichSu.length === 0) {
        this.thanhToanHoaDon.thanhToan = null;
      } else {
        this.thanhToanHoaDon.thanhToan = { ...this.thanhToanLichSu[this.thanhToanLichSu.length - 1] };
      }
      ElMessage.success('Đã xóa thanh toán!');
    },
    taoHoaDonMoiNgay() {
      this.modalHoaDon = false;
      this.taoHoaDon();
    },
    showCustomerModal(hd) {
      this.selectedHoaDon = hd;
      this.modalCustomer = true;
      this.customerSearch = '';
      this.currentPageCustomer = 1;
      this.fetchCustomers();
    },
    async fetchCustomers(page = 0) {
      this.loadingCustomers = true;
      try {
        await this.checkAuth();
        const params = {
          page: page,
          size: this.pageSizeCustomer,
          keyword: this.customerSearch || '',
        };
        const response = await axios.get('http://localhost:8080/admin/api/khachhang', {
          params,
          withCredentials: true,
        });
        this.customers = response.data.content || [];
        this.totalCustomers = response.data.totalElements || 0;
        this.currentPageCustomer = response.data.number + 1;
        if (this.customers.length === 0) {
          ElMessage.warning('Không tìm thấy khách hàng phù hợp.');
        }
      } catch (error) {
        console.error('Lỗi khi lấy danh sách khách hàng:', error.response?.data || error.message);
        ElMessage.error('Không thể tải danh sách khách hàng: ' + (error.response?.data?.message || error.message));
        this.customers = [];
        this.totalCustomers = 0;
      } finally {
        this.loadingCustomers = false;
      }
    },
    async selectCustomer(customer, hd) {
      try {
        if (!customer.id || isNaN(customer.id)) {
          throw new Error('ID khách hàng không hợp lệ');
        }
        hd.idKhachHang = Number(customer.id);
        hd.khachHang = customer.hoTen;
        this.modalCustomer = false;
        ElMessage.success(`Đã chọn khách hàng ${customer.hoTen} cho hóa đơn ${hd.maHoaDon}`);
      } catch (error) {
        console.error('Lỗi khi chọn khách hàng:', error.message);
        ElMessage.error(`Không thể chọn khách hàng: ${error.message}`);
      }
    },
    debounceSearchCustomers: _.debounce(function() {
      this.currentPageCustomer = 1;
      this.fetchCustomers();
    }, 300),
    async searchCustomers() {
      this.currentPageCustomer = 1;
      await this.fetchCustomers();
    },
    async xacNhanDatHang(hd) {
      try {
        await this.checkAuth();
        if (!hd.thanhToan || hd.thanhToan.soTien < this.tinhTongTien(hd)) {
          ElMessage.error('Hóa đơn chưa được thanh toán đủ!');
          return;
        }
        this.hoaDonIn = { ...hd };
        this.modalHoaDon = true;
        ElMessage.info('Vui lòng in hóa đơn để hoàn tất đặt hàng!');
      } catch (error) {
        console.error('Lỗi khi mở modal hóa đơn:', error.response?.data || error.message);
        ElMessage.error('Có lỗi xảy ra khi mở modal hóa đơn: ' + (error.response?.data?.message || error.message));
      }
    },
    getValidVouchersForProduct(sp) {
      const tongTien = sp.giaBan * sp.soLuong;
      return this.productVouchers.filter(v => {
        const minAmount = v.giaTriDonHangToiThieu || 0;
        return tongTien >= minAmount;
      });
    },
  },
};
</script>