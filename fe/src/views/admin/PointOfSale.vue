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
                class="nav-link"
                :class="{ active: tabActive === hd.maHoaDon }"
                :href="`#pane-${hd.maHoaDon}`"
                data-bs-toggle="tab"
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
                          @click="xoaHoaDon(hd.maHoaDon)"
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
                                  Mã: {{ sp.maCtSanPham }} | Size: {{ sp.tenKichThuoc }} | Màu: {{ sp.tenMauSac }}<br>
                                  Thương hiệu: {{ sp.tenThuongHieu }} | Chất liệu: {{ sp.tenChatLieu }}
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
                      <!-- Delivery toggle -->
                      <div class="d-flex justify-content-between align-items-center p-3 bg-light rounded mb-3">
                        <div class="d-flex align-items-center">
                          <i class="bi bi-truck me-3 text-info"></i>
                          <label class="form-label mb-0 fw-semibold">Giao hàng</label>
                        </div>
                        <div class="form-check form-switch">
                          <input
                            class="form-check-input"
                            type="checkbox"
                            v-model="hd.giaoHang"
                            @change="toggleDeliveryInfo(hd)"
                          />
                        </div>
                      </div>
                      <!-- Delivery info -->
                      <div v-if="hd.giaoHang" class="p-3 border border-info rounded bg-info bg-opacity-10 mb-3">
                        <h6 class="text-info fw-semibold mb-3">
                          <i class="bi bi-geo-alt-fill me-2"></i>
                          Thông tin giao hàng
                        </h6>
                        <div v-if="hd.customerInfo">
                          <div class="mb-3">
                            <label class="form-label text-info fw-semibold">Họ tên</label>
                            <input
                              v-model="hd.customerInfo.hoTen"
                              type="text"
                              class="form-control"
                              placeholder="Nhập họ tên"
                              :disabled="!hd.customerInfo"
                            />
                          </div>
                          <div class="mb-3">
                            <label class="form-label text-info fw-semibold">Số điện thoại</label>
                            <input
                              v-model="hd.customerInfo.sdt"
                              type="text"
                              class="form-control"
                              placeholder="Nhập số điện thoại"
                              :disabled="!hd.customerInfo"
                            />
                          </div>
                          <div class="mb-3">
                            <label class="form-label text-info fw-semibold">
                              <i class="bi bi-pin-map me-1"></i>
                              Địa chỉ giao hàng
                            </label>
                            <select
                              v-model="hd.customerInfo.selectedDiaChi"
                              class="form-select"
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
                        <div v-else class="text-center py-3">
                          <i class="bi bi-exclamation-triangle text-warning fs-1"></i>
                          <p class="text-muted mt-2 mb-0">Vui lòng chọn khách hàng để hiển thị thông tin giao hàng</p>
                          <button
                            type="button"
                            class="btn btn-outline-primary btn-sm mt-2"
                            @click="showCustomerModal(hd)"
                          >
                            <i class="bi bi-person-plus me-1"></i> Chọn khách hàng
                          </button>
                        </div>
                      </div>
                      <hr>

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

    <!-- Modal Chọn Voucher Sản Phẩm -->
    <div v-if="voucherModal" class="modal fade show d-block" tabindex="-1" style="background: rgba(0,0,0,0.6);">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header bg-primary text-white">
            <h5 class="modal-title">Chọn Voucher cho Sản Phẩm</h5>
            <button type="button" class="btn-close btn-close-white" @click="voucherModal = false"></button>
          </div>
          <div class="modal-body">
            <div v-if="selectedProduct && selectedHoaDon">
              <div v-if="productVouchers.length === 0" class="alert alert-info text-center">
                <i class="bi bi-info-circle me-2"></i>
                Không có voucher phù hợp với sản phẩm này
              </div>
              <div v-else>
                <select
                  v-model="selectedProduct.selectedVoucherId"
                  class="form-select mb-3"
                  @change="chonVoucherSanPham(selectedHoaDon, selectedProduct)"
                >
                  <option value="" disabled>Chọn voucher</option>
                  <option
                    v-for="v in productVouchers"
                    :key="v.id"
                    :value="v.id"
                  >
                    {{ v.tenVoucher }} - {{ v.hinhThucGiam === '%' ? `${v.mucGiam}% (Tối đa ${v.giamToiDa.toLocaleString()} VND)` : v.giaGiam ? `${v.giaGiam.toLocaleString()} VND` : `Giảm còn ${v.donGiaKhiGiam.toLocaleString()} VND` }}
                  </option>
                </select>
                <div v-if="selectedProduct.selectedVoucher" class="card mt-3">
                  <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                      <span class="fw-bold">{{ selectedProduct.selectedVoucher.tenVoucher }}</span>
                      <span class="text-success">
                        {{ selectedProduct.selectedVoucher.hinhThucGiam === '%' ? `${selectedProduct.selectedVoucher.mucGiam}% (Tối đa ${selectedProduct.selectedVoucher.giamToiDa.toLocaleString()} VND)` : selectedProduct.selectedVoucher.giaGiam ? `${selectedProduct.selectedVoucher.giaGiam.toLocaleString()} VND` : `Giảm còn ${selectedProduct.selectedVoucher.donGiaKhiGiam.toLocaleString()} VND` }}
                      </span>
                    </div>
                    <small class="text-muted d-block mt-2">
                      <i class="bi bi-calendar me-1"></i>
                      Hiệu lực: {{ formatDate(selectedProduct.selectedVoucher.ngayBatDau) }} - {{ formatDate(selectedProduct.selectedVoucher.ngayKetThuc) }}
                    </small>
                    <small class="text-muted d-block">
                      Giảm: {{ tinhGiamGiaSanPham(selectedProduct).toLocaleString() }} VND
                    </small>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="voucherModal = false">Đóng</button>
            <button type="button" class="btn btn-primary" @click="voucherModal = false">Xác nhận</button>
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
                    <th>Màu sắc</th>
                    <th>Chất liệu</th>
                    <th>Size</th>
                    <th>Giá</th>
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
                    <td>{{ row.tenMauSac }}</td>
                    <td>{{ row.tenChatLieu }}</td>
                    <td>{{ row.tenKichThuoc }}</td>
                    <td>{{ row.giaBan != null ? row.giaBan.toLocaleString() : '0' }} VND</td>
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
                <div>Phí vận chuyển: {{ tinhPhiVanChuyen(hoaDonIn).toLocaleString() }} VND</div>
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
              <button
                type="button"
                class="btn btn-outline-success"
                :class="{ active: thanhToanPhuongThuc === 'MOMO' }"
                @click="thanhToanPhuongThuc = 'MOMO'"
              >
                <i class="bi bi-qr-code"></i> MoMo QR
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
              
              <!-- MoMo QR Payment Section -->
              <div v-if="thanhToanPhuongThuc === 'MOMO'" class="mt-3">
                <div class="text-center">
                  <div v-if="momoLoading">
                    <div class="spinner-border text-success" role="status">
                      <span class="visually-hidden">Đang tạo thanh toán...</span>
                    </div>
                    <p class="mt-2">Đang tạo thanh toán MoMo...</p>
                  </div>
                  <div v-else>
                    <button 
                      type="button" 
                      class="btn btn-success btn-lg"
                      @click="taoMoMoQR"
                    >
                      <i class="bi bi-qr-code"></i> Thanh toán bằng MoMo
                    </button>
                    <p class="text-muted mt-2">Sẽ chuyển đến trang thanh toán MoMo</p>
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
              :disabled="thanhToanPhuongThuc !== 'MOMO' && thanhToanTienKhachDua < tinhTongTien(thanhToanHoaDon)"
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

```vue
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
      listSanPham: { content: [], number: 0, size: 10, totalElements: 0 },
      hoaDonChon: null,
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
      momoLoading: false,
      momoTransactionStatus: null,
      momoOrderId: null,
      momoCheckInterval: null,
      modalCustomer: false,
      customerSearch: '',
      selectedHoaDon: null,
      customers: [],
      loadingCustomers: false,
      currentPageCustomer: 1,
      pageSizeCustomer: 10,
      totalCustomers: 0,
      diaChiList: [],
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
      pageSizeSanPham: 10,
    };
  },
  created() {
    this.checkAuth();
    this.fetchCategories();
    this.fetchBrands();
    this.fetchColors();
    this.fetchMaterials();
    this.fetchPhuongThucThanhToan();
    
    console.log('[CREATED] Component được tạo.');
    // Khôi phục trạng thái hóa đơn đã lưu (tránh mất khi F5)
    this.restoreHoaDonFromStorage();
    
    // **QUAN TRỌNG: Kiểm tra orderId từ localStorage do trang MoMoReturn để lại**
    const momoOrderId = localStorage.getItem('momo_last_order_id');
    if (momoOrderId) {
        console.log(`[CREATED] Tìm thấy momo_last_order_id: ${momoOrderId} từ localStorage.`);
        ElMessage.info('Đang kiểm tra lại trạng thái thanh toán MoMo...');
        // Xóa ngay để tránh xử lý lại
        localStorage.removeItem('momo_last_order_id');
        
        // Khôi phục hóa đơn và bắt đầu kiểm tra
        this.handleMoMoReturn(momoOrderId);
    }
  },
  
  beforeUnmount() {
    // Dừng kiểm tra MoMo khi rời khỏi component
    this.stopCheckingMomoStatus();
  },
  watch: {
    // Tự động lưu mọi thay đổi của danh sách hóa đơn
    hoaDons: {
      deep: true,
      handler() {
        this.saveHoaDonsToStorage();
      }
    },
    // Lưu tab đang chọn
    tabActive() {
      this.saveHoaDonsToStorage();
    }
  },
  computed: {
    tongTienDaThanhToan() {
      return this.thanhToanLichSu.reduce((sum, t) => sum + (t.soTien || 0), 0);
    },
    filteredCustomers() {
      return this.customers;
    },
  },
  methods: {
    saveHoaDonsToStorage() {
      try {
        const payload = {
          hoaDons: this.hoaDons,
          tabActive: this.tabActive,
          timestamp: Date.now()
        };
        localStorage.setItem('pos_open_orders', JSON.stringify(payload));
      } catch (e) {
        console.error('Không thể lưu hóa đơn vào localStorage:', e);
      }
    },
    restoreHoaDonFromStorage() {
      try {
        const raw = localStorage.getItem('pos_open_orders');
        if (!raw) return;
        const payload = JSON.parse(raw);
        if (!payload || !Array.isArray(payload.hoaDons)) return;
        this.hoaDons = payload.hoaDons;
        this.tabActive = payload.tabActive || (this.hoaDons[0]?.maHoaDon || '');
        console.log('Đã khôi phục', this.hoaDons.length, 'hóa đơn mở từ localStorage');
      } catch (e) {
        console.error('Không thể khôi phục hóa đơn từ localStorage:', e);
      }
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
    async fetchPhuongThucThanhToan() {
      try {
        await this.checkAuth();
        const response = await axios.get('http://localhost:8080/admin/api/phuongthucthanhtoan', {
          withCredentials: true,
        });
        this.phuongThucThanhToans = response.data.filter(ptt => 
          ptt.ten === 'CHUYỂN KHOẢN' || ptt.ten === 'TIỀN MẶT'
        );
        
        console.log('[fetchPhuongThucThanhToan] Danh sách phương thức thanh toán từ API:', response.data);
        console.log('[fetchPhuongThucThanhToan] Phương thức đã lọc:', this.phuongThucThanhToans);
        
        if (this.phuongThucThanhToans.length === 0) {
          console.warn('Không tìm thấy phương thức thanh toán, sử dụng mặc định');
          this.phuongThucThanhToans = [
            { idPTT: 1, ten: 'TIỀN MẶT' },
            { idPTT: 2, ten: 'CHUYỂN KHOẢN' }
          ];
        }
        
        // Đảm bảo luôn có phương thức chuyển khoản cho MoMo
        const hasChuyenKhoan = this.phuongThucThanhToans.some(ptt => ptt.ten === 'CHUYỂN KHOẢN');
        if (!hasChuyenKhoan) {
          this.phuongThucThanhToans.push({ idPTT: 2, ten: 'CHUYỂN KHOẢN' });
          console.log('[fetchPhuongThucThanhToan] Đã thêm phương thức CHUYỂN KHOẢN cho MoMo');
        }
        
      } catch (error) {
        console.error('Lỗi khi lấy phương thức thanh toán:', error.response?.data || error.message);
        this.phuongThucThanhToans = [
          { idPTT: 1, ten: 'TIỀN MẶT' },
          { idPTT: 2, ten: 'CHUYỂN KHOẢN' }
        ];
        ElMessage.error('Không thể tải danh sách phương thức thanh toán');
      }
    },
    taoHoaDon() {
      if (!this.auth.isAuthenticated || !this.auth.user?.idNV || !this.auth.isAdmin()) {
        ElMessage.error('Vui lòng đăng nhập với tài khoản admin!');
        this.router.push('/login');
        return;
      }
      const ma = `HD${Date.now()}${Math.floor(Math.random() * 1000)}`;
      const hd = {
        maHoaDon: ma,
        sanPhams: [],
        khachHang: '',
        idKhachHang: null,
        ngayTao: new Date().toISOString(),
        thanhToan: null,
        trangThai: 'CHO_THANH_TOAN',
        idNhanVien: this.auth.user.idNV,
        giaoHang: false,
        customerInfo: null,
        ghiChuGiaoHang: ''
      };
      this.hoaDons.push(hd);
      this.tabActive = ma;
      console.log('[taoHoaDon] Đã tạo hóa đơn mới:', ma);
      ElMessage.success('Tạo hóa đơn mới thành công!');
      this.saveHoaDonsToStorage();
    },
    resetHoaDon(hd) {
      if (!this.auth.isAuthenticated || !this.auth.user?.idNV || !this.auth.isAdmin()) {
        ElMessage.error('Vui lòng đăng nhập với tài khoản admin!');
        this.router.push('/login');
        return;
      }
      const ma = `HD${Date.now()}${Math.floor(Math.random() * 1000)}`;
      hd.maHoaDon = ma;
      hd.sanPhams = [];
      hd.khachHang = '';
      hd.idKhachHang = null;
      hd.customerInfo = null;
      hd.thanhToan = null;
      hd.trangThai = 'CHO_THANH_TOAN';
      hd.idNhanVien = this.auth.user.idNV;
      hd.giaoHang = false;
      hd.ghiChuGiaoHang = '';
      this.saveHoaDonsToStorage();
    },
    async xacNhanDatHang(hd) {
  try {
    console.log('xacNhanDatHang: Bắt đầu, maHoaDon=' + hd.maHoaDon);
    await this.checkAuth();
    if (!this.auth.isAuthenticated || !this.auth.user || !this.auth.isAdmin()) {
      ElMessage.error('Phiên đăng nhập hết hạn, vui lòng đăng nhập lại!');
      this.router.push('/login');
      return;
    }

    if (!this.auth.user?.idNV || isNaN(this.auth.user.idNV)) {
      ElMessage.error('Không tìm thấy thông tin nhân viên, vui lòng đăng nhập lại!');
      this.router.push('/login');
      return;
    }

    if (!hd.thanhToan || hd.thanhToan.soTien < this.tinhTongTien(hd)) {
      ElMessage.error('Thanh toán chưa hoàn tất hoặc số tiền không đủ!');
      return;
    }

    if (!hd.sanPhams || hd.sanPhams.length === 0) {
      ElMessage.error('Hóa đơn không có sản phẩm!');
      return;
    }

    let selectedPTT;
    if (hd.thanhToan.phuongThuc === 'MOMO') {
      // Sử dụng phương thức chuyển khoản cho MoMo
      selectedPTT = this.phuongThucThanhToans.find(ptt => ptt.ten === 'CHUYỂN KHOẢN');
      if (!selectedPTT) {
        // Fallback với ID mặc định cho chuyển khoản
        selectedPTT = { idPTT: 2, ten: 'CHUYỂN KHOẢN' };
      }
    } else {
      selectedPTT = this.phuongThucThanhToans.find(ptt => ptt.ten === hd.thanhToan.phuongThuc);
    }
    
    if (!selectedPTT || !selectedPTT.idPTT || isNaN(selectedPTT.idPTT)) {
      console.error('Không tìm thấy phương thức thanh toán:', hd.thanhToan.phuongThuc);
      console.error('Danh sách phương thức có sẵn:', this.phuongThucThanhToans);
      ElMessage.error('Phương thức thanh toán không hợp lệ!');
      return;
    }
    
    console.log('[xacNhanDatHang] Sử dụng phương thức thanh toán:', selectedPTT);

    // Thanh toán tại quầy - không cần xử lý địa chỉ giao hàng

    // Tạo hoaDonData theo cấu trúc database hiện tại (chỉ cho thanh toán tại quầy)
    const hoaDonData = {
      maHD: `HD${Date.now()}${Math.floor(Math.random() * 1000)}`,
      idKhachHang: hd.idKhachHang ? Number(hd.idKhachHang) : null,
      idNhanVien: Number(this.auth.user.idNV),
      ngayTao: new Date().toISOString(),
      ngaySua: new Date().toISOString(),
      tongTien: Number(this.tinhTongTien(hd)).toString(),
      trangThai: 'Hoàn thành',
      loaiHoaDon: 'Tại quầy',
      chiTietSanPham: (hd.sanPhams || []).map(sp => {
        if (!sp.id || isNaN(sp.id) || sp.soLuong <= 0 || isNaN(sp.soLuong) || sp.giaBan <= 0 || isNaN(sp.giaBan)) {
          throw new Error(`Sản phẩm ${sp.tenGiay} có dữ liệu không hợp lệ: id=${sp.id}, soLuong=${sp.soLuong}, giaBan=${sp.giaBan}`);
        }
        if (sp.soLuong > sp.soLuongTonKho) {
          throw new Error(`Số lượng sản phẩm ${sp.tenGiay} vượt quá tồn kho: ${sp.soLuong} > ${sp.soLuongTonKho}`);
        }
        return {
          idCtSanPham: Number(sp.id),
          soLuong: Number(sp.soLuong),
          donGia: Number(sp.giaBan).toString(),
          idKM: sp.selectedVoucherId ? Number(sp.selectedVoucherId) : null
        };
      })
    };

    // Kiểm tra và log hoaDonData
    console.log('Kiểm tra dữ liệu hoaDonData:');
    console.log(`- maHD: ${hoaDonData.maHD} (${typeof hoaDonData.maHD})`);
    console.log(`- idKhachHang: ${hoaDonData.idKhachHang} (${typeof hoaDonData.idKhachHang})`);
    console.log(`- idNhanVien: ${hoaDonData.idNhanVien} (${typeof hoaDonData.idNhanVien})`);
    console.log(`- ngayTao: ${hoaDonData.ngayTao} (${typeof hoaDonData.ngayTao})`);
    console.log(`- ngaySua: ${hoaDonData.ngaySua} (${typeof hoaDonData.ngaySua})`);
    console.log(`- tongTien: ${hoaDonData.tongTien} (${typeof hoaDonData.tongTien})`);
    console.log(`- trangThai: ${hoaDonData.trangThai} (${typeof hoaDonData.trangThai})`);
    console.log(`- loaiHoaDon: ${hoaDonData.loaiHoaDon} (${typeof hoaDonData.loaiHoaDon})`);
    console.log(`- chiTietSanPham: ${JSON.stringify(hoaDonData.chiTietSanPham, null, 2)}`);

    // Validation hoaDonData
    if (!hoaDonData.maHD || typeof hoaDonData.maHD !== 'string') {
      throw new Error('Mã hóa đơn không hợp lệ');
    }
    if (!hoaDonData.idNhanVien || isNaN(hoaDonData.idNhanVien)) {
      throw new Error('ID nhân viên không hợp lệ');
    }
    if (!hoaDonData.tongTien || isNaN(Number(hoaDonData.tongTien)) || Number(hoaDonData.tongTien) <= 0) {
      throw new Error('Tổng tiền không hợp lệ');
    }
    if (!hoaDonData.chiTietSanPham.length) {
      throw new Error('Danh sách chi tiết sản phẩm không được rỗng');
    }
    if (!hoaDonData.trangThai || typeof hoaDonData.trangThai !== 'string') {
      throw new Error('Trạng thái hóa đơn không hợp lệ');
    }

    // Gửi yêu cầu tạo hóa đơn
    console.log('Dữ liệu gửi lên API /admin/api/hoadon:', JSON.stringify(hoaDonData, null, 2));
    const response = await axios.post('http://localhost:8080/admin/api/hoadon', hoaDonData, {
      withCredentials: true,
      headers: { 'Content-Type': 'application/json; charset=UTF-8' }
    });

    if (response.data) {
      console.log('Hóa đơn tạo thành công, idHD=' + response.data.idHD);
      
      // Nếu là thanh toán MoMo, lưu thông tin vào bảng MoMoTransaction
      if (hd.thanhToan.phuongThuc === 'MOMO') {
        try {
          console.log('[MOMO] Đang lưu thông tin giao dịch MoMo vào database...');
          const momoData = {
            idHD: response.data.idHD,
            orderId: hd.thanhToan.maGiaoDich,
            requestId: hd.thanhToan.maGiaoDich,
            amount: this.tinhTongTien(hd),
            orderInfo: `Thanh toán đơn hàng ${hd.maHoaDon}`,
            transactionStatus: 'SUCCESS',
            message: 'Thanh toán thành công',
            localMessage: 'Thanh toán thành công',
            responseCode: '0'
          };
          
          await axios.post('http://localhost:8080/admin/api/momo/save-transaction', momoData, {
            withCredentials: true,
            headers: { 'Content-Type': 'application/json' }
          });
          console.log('[MOMO] Đã lưu thông tin giao dịch MoMo thành công.');
        } catch (momoError) {
          console.error('[MOMO] Lỗi khi lưu thông tin giao dịch MoMo:', momoError);
          // Không làm gián đoạn quy trình vì hóa đơn đã được tạo thành công
        }
      }
      // Cập nhật tồn kho sản phẩm
      for (const sp of hd.sanPhams) {
        try {
          const productResponse = await axios.get(`http://localhost:8080/admin/api/sanphamchitiet/${sp.id}`, {
            withCredentials: true,
          });
          const currentProduct = productResponse.data;
          console.log(`Cập nhật tồn kho sản phẩm ${sp.id}, số lượng mới: ${currentProduct.soLuong - sp.soLuong}`);
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
            anhGiay: currentProduct.anhGiay
            // Loại bỏ idPT
          }, {
            withCredentials: true,
            headers: { 'Content-Type': 'application/json; charset=UTF-8' }
          });
        } catch (error) {
          console.error(`Lỗi khi cập nhật sản phẩm ${sp.id}:`, error.response?.data || error.message);
          ElMessage.error(`Không thể cập nhật sản phẩm ${sp.tenGiay}: ${error.response?.data?.message || error.message}`);
        }
      }

      // Cập nhật số lượng voucher
      for (const sp of hd.sanPhams) {
        if (sp.selectedVoucherId && sp.selectedVoucher) {
          try {
            const newSoLuong = Number(sp.selectedVoucher.soLuong) - 1;
            if (isNaN(newSoLuong) || newSoLuong < 0) {
              ElMessage.error(`Voucher ${sp.selectedVoucher.tenVoucher} có số lượng không hợp lệ hoặc đã hết!`);
              continue;
            }
            console.log(`Cập nhật số lượng voucher ${sp.selectedVoucherId}, số lượng mới: ${newSoLuong}`);
            await axios.put(`http://localhost:8080/api/voucher/${sp.selectedVoucherId}`, newSoLuong, {
              withCredentials: true,
              headers: { 'Content-Type': 'application/json; charset=UTF-8' }
            });
          } catch (error) {
            console.error('Lỗi khi cập nhật số lượng voucher:', error.response?.data || error.message);
            ElMessage.error('Không thể cập nhật số lượng voucher: ' + (error.response?.data?.message || error.message));
          }
        }
      }

      // Tạo chi tiết hóa đơn
      for (const sp of hd.sanPhams) {
        const hoaDonCTData = {
          idSP: Number(sp.id),
          idHD: response.data.idHD,
          idKM: sp.selectedVoucherId ? Number(sp.selectedVoucherId) : null,
          soLuong: Number(sp.soLuong),
          donGia: Number(sp.giaBan),
          thanhTien: Number(sp.giaBan * sp.soLuong - (sp.selectedVoucher ? this.tinhGiamGiaSanPham(sp) : 0)),
          idPT: Number(selectedPTT.idPTT)
        };
        console.log('Dữ liệu gửi lên API /admin/api/hoadonchitiet:', JSON.stringify(hoaDonCTData, null, 2));
        try {
          await axios.post('http://localhost:8080/admin/api/hoadonchitiet', hoaDonCTData, {
            withCredentials: true,
            headers: { 'Content-Type': 'application/json; charset=UTF-8' }
          });
        } catch (error) {
          console.error('Lỗi khi tạo chi tiết hóa đơn:', error.response?.data || error.message);
          ElMessage.error('Không thể tạo chi tiết hóa đơn: ' + (error.response?.data?.errors?.join('; ') || error.response?.data?.message || error.message));
        }
      }

      // Xóa hóa đơn khỏi danh sách hóa đơn đang mở
      console.log('[xacNhanDatHang] Bắt đầu xử lý hóa đơn:', hd.maHoaDon);
      console.log('[xacNhanDatHang] Danh sách hóa đơn hiện tại:', JSON.stringify(this.hoaDons.map(h => h.maHoaDon)));
      
      const index = this.hoaDons.findIndex(item => item.maHoaDon === hd.maHoaDon);
      console.log('[xacNhanDatHang] Tìm thấy hóa đơn tại index:', index);
      
      if (index !== -1) {
        this.hoaDons.splice(index, 1);
        console.log('[xacNhanDatHang] Đã xóa hóa đơn khỏi danh sách.');
      } else {
        console.warn('[xacNhanDatHang] Không tìm thấy hóa đơn để xóa trong danh sách.');
      }
      
      // Nếu không còn hóa đơn nào, tạo hóa đơn mới
      if (this.hoaDons.length === 0) {
        console.log('[xacNhanDatHang] Tạo hóa đơn mới vì danh sách rỗng.');
        this.taoHoaDon();
      } else {
        // Chọn tab hóa đơn khác nếu có
        this.tabActive = this.hoaDons[0].maHoaDon;
        console.log('[xacNhanDatHang] Chuyển sang tab:', this.tabActive);
      }
      
      // Lưu lại trạng thái
      this.saveHoaDonsToStorage();
      console.log('[xacNhanDatHang] Đã lưu trạng thái mới vào localStorage.');
      
      // Hiển thị thông báo và modal in hóa đơn
      ElMessage.success('Lưu hóa đơn thành công!');
      this.hoaDonIn = { ...hd };
      this.modalHoaDon = true;
      
      // Cập nhật danh sách sản phẩm (tồn kho đã thay đổi)
      await this.fetchSanPhamList();
    }
  } catch (error) {
    console.error('Lỗi khi lưu hóa đơn:', error.response?.data || error.message);
    let errorMessage = 'Có lỗi xảy ra khi lưu hóa đơn!';
    if (error.response?.status === 401) {
      errorMessage = 'Phiên đăng nhập hết hạn, vui lòng đăng nhập lại!';
      this.router.push('/login');
    } else if (error.response?.status === 400) {
      const errors = error.response?.data?.errors || error.response?.data?.message || error.response?.data?.error;
      if (Array.isArray(errors)) {
        errorMessage = 'Dữ liệu không hợp lệ: ' + errors.join('; ');
      } else if (typeof errors === 'string') {
        errorMessage = errors;
      } else if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      } else {
        errorMessage = 'Dữ liệu gửi lên không hợp lệ! Vui lòng kiểm tra console log để biết thêm chi tiết.';
      }
    } else if (error.response?.status === 403) {
      errorMessage = 'Bạn không có quyền thực hiện hành động này!';
      this.router.push('/login');
    } else if (error.request) {
      errorMessage = 'Không nhận được phản hồi từ server, vui lòng kiểm tra kết nối!';
    } else {
      errorMessage = error.message || 'Lỗi không xác định!';
    }
    console.error('Chi tiết lỗi:', JSON.stringify(error.response?.data || error.message, null, 2));
    ElMessage.error(errorMessage);
  }
},
    async fetchSanPhamList() {
      this.productLoading = true;
      try {
        await this.checkAuth();
        const hasFilters = this.productSearchQuery || this.selectedCategory || this.selectedBrand ||
                          this.selectedColor || this.selectedMaterial ||
                          this.priceRange[0] !== 0 || this.priceRange[1] !== 5000000;

        if (!hasFilters) {
          const response = await axios.get('http://localhost:8080/admin/api/sanphamchitiet', {
            withCredentials: true,
          });
          this.listSanPham = {
            content: response.data.map(item => ({
              id: item.id,
              maSPCT: item.maSPCT,
              maCtSanPham: item.maSPCT,
              tenSP: item.tenSP,
              tenGiay: item.tenSP,
              giaBan: item.gia != null ? Number(item.gia) : 0,
              soLuongTonKho: Number(item.soLuong),
              moTa: item.moTa,
              trangThai: item.trangThai,
              tenDanhMuc: item.tenDanhMuc,
              tenThuongHieu: item.tenThuongHieu,
              tenMauSac: item.tenMauSac,
              tenChatLieu: item.tenChatLieu,
              tenKichThuoc: item.tenKichThuoc,
              anhGiay: item.anhGiay
            })),
            number: 0,
            size: response.data.length,
            totalElements: response.data.length
          };
          if (this.listSanPham.totalElements === 0) {
            ElMessage.warning('Không tìm thấy sản phẩm nào.');
          }
        } else {
          const params = {
            page: this.pageSanPham - 1,
            size: this.pageSizeSanPham,
            keyword: this.productSearchQuery || '',
            danhMuc: this.selectedCategory || '',
            thuongHieu: this.selectedBrand || '',
            mauSac: this.selectedColor || '',
            chatLieu: this.selectedMaterial || '',
            minPrice: Number(this.priceRange[0]),
            maxPrice: Number(this.priceRange[1])
          };
          const response = await axios.get('http://localhost:8080/admin/api/sanphamchitiet/filter', {
            params,
            withCredentials: true,
          });
          this.listSanPham = {
            content: response.data.content.map(item => ({
              id: item.id,
              maSPCT: item.maSPCT,
              maCtSanPham: item.maSPCT,
              tenSP: item.tenSP,
              tenGiay: item.tenSP,
              giaBan: item.gia != null ? Number(item.gia) : 0,
              soLuongTonKho: Number(item.soLuong),
              moTa: item.moTa,
              trangThai: item.trangThai,
              tenDanhMuc: item.tenDanhMuc,
              tenThuongHieu: item.tenThuongHieu,
              tenMauSac: item.tenMauSac,
              tenChatLieu: item.tenChatLieu,
              tenKichThuoc: item.tenKichThuoc,
              anhGiay: item.anhGiay
            })),
            number: response.data.number,
            size: response.data.size,
            totalElements: response.data.totalElements
          };
          if (this.listSanPham.totalElements === 0) {
            ElMessage.warning('Không tìm thấy sản phẩm nào phù hợp với bộ lọc.');
          }
        }
      } catch (error) {
        console.error('Lỗi khi lấy danh sách sản phẩm:', error.response?.data || error.message);
        this.listSanPham = { content: [], number: 0, size: 10, totalElements: 0 };
        ElMessage.error(`Không thể tải danh sách sản phẩm: ${error.response?.data?.message || error.message}`);
      } finally {
        this.productLoading = false;
      }
    },
    xoaHoaDon(ma) {
      this.hoaDons = this.hoaDons.filter(hd => hd.maHoaDon !== ma);
      if (this.hoaDons.length > 0) this.tabActive = this.hoaDons[0].maHoaDon;
    },
    showChonSanPham(hd, isQR = false) {
      this.hoaDonChon = hd;
      this.productSearchModal = true;
      this.productSearchQuery = '';
      this.selectedCategory = '';
      this.selectedBrand = '';
      this.selectedColor = '';
      this.selectedMaterial = '';
      this.priceRange = [0, 5000000];
      this.fetchSanPhamList();
      if (isQR) {
        ElMessage.info('Chức năng quét QR chưa được triển khai');
      }
    },
    chonSanPham(sp) {
      if (sp.giaBan == null || isNaN(sp.giaBan) || sp.giaBan <= 0) {
        ElMessage.error(`Sản phẩm ${sp.tenSP} không có giá bán hợp lệ!`);
        return;
      }
      if (sp.soLuongTonKho == null || isNaN(sp.soLuongTonKho) || sp.soLuongTonKho <= 0) {
        ElMessage.error(`Sản phẩm ${sp.tenSP} không có tồn kho hợp lệ!`);
        return;
      }
      const existed = this.hoaDonChon.sanPhams.find(x => x.id === sp.id);
      const tonKho = Number(sp.soLuongTonKho);
      if (existed) {
        if (existed.soLuong + 1 > tonKho) {
          ElMessage.error('Vượt quá số lượng tồn kho!');
        } else {
          existed.soLuong += 1;
        }
      } else {
        this.hoaDonChon.sanPhams.push({
          ...sp,
          soLuong: 1,
          soLuongTonKho: tonKho,
          selectedVoucher: null,
          selectedVoucherId: null,
          maCtSanPham: sp.maSPCT,
          giaBan: Number(sp.giaBan),
          moTa: sp.moTa
        });
      }
      this.productSearchModal = false;
    },
    onChangeSoLuong(row, hd) {
      if (row.soLuong > row.soLuongTonKho || isNaN(row.soLuong)) {
        row.soLuong = row.soLuongTonKho;
        ElMessage.error('Vượt quá số lượng tồn kho!');
      } else if (row.soLuong < 1) {
        row.soLuong = 1;
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
          label: item.tenDM
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
          value: item.tenThuongHieu,
          label: item.tenThuongHieu
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
          label: item.tenMauSac
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
          value: item.tenChatLieu,
          label: item.tenChatLieu
        }));
      } catch (error) {
        console.error('Lỗi khi lấy chất liệu:', error.response?.data || error.message);
        ElMessage.error('Không thể tải danh sách chất liệu');
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
          headers: { 'Content-Type': 'application/json; charset=UTF-8' }
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
    tinhPhiVanChuyen(hd) {
      return hd.giaoHang ? 34000 : 0;
    },
    tinhGiamGia(hd) {
      return hd.sanPhams.reduce((sum, sp) => sum + (sp.selectedVoucher ? this.tinhGiamGiaSanPham(sp) : 0), 0);
    },
    tinhTongTien(hd) {
      let tong = this.tinhTienHang(hd);
      tong -= this.tinhGiamGia(hd);
      tong += this.tinhPhiVanChuyen(hd);
      return tong > 0 ? Number(tong) : 0;
    },
    inHoaDon() {
      window.print();
      ElMessage.success('Đã in hóa đơn thành công');
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
    xacNhanThanhToan() {
      if (this.thanhToanTienKhachDua < this.tinhTongTien(this.thanhToanHoaDon) || isNaN(this.thanhToanTienKhachDua)) {
        ElMessage.error('Số tiền khách đưa không đủ hoặc không hợp lệ!');
        return;
      }
      if (this.thanhToanPhuongThuc === 'CHUYỂN KHOẢN' && !this.thanhToanMaGiaoDich) {
        ElMessage.error('Vui lòng nhập mã giao dịch cho phương thức chuyển khoản!');
        return;
      }
      const thanhToan = {
        phuongThuc: this.thanhToanPhuongThuc,
        soTien: Number(this.thanhToanTienKhachDua),
        maGiaoDich: this.thanhToanPhuongThuc === 'CHUYỂN KHOẢN' ? this.thanhToanMaGiaoDich : null
      };
      this.thanhToanLichSu.push(thanhToan);
      this.thanhToanHoaDon.thanhToan = { ...thanhToan };
      this.modalThanhToan = false;
      ElMessage.success('Xác nhận thanh toán thành công!');
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
    
    // MoMo Payment Methods
    async taoMoMoQR() {
      console.log('[MOMO] Bắt đầu tạo thanh toán MoMo.');
      
      // Kiểm tra hóa đơn có sản phẩm không
      if (!this.thanhToanHoaDon.sanPhams || this.thanhToanHoaDon.sanPhams.length === 0) {
        ElMessage.error('Hóa đơn không có sản phẩm để thanh toán!');
        return;
      }

      try {
        this.momoLoading = true;
        const orderId = `POS${Date.now()}${Math.floor(Math.random() * 1000)}`;
        this.momoOrderId = orderId;
        
        // Lưu thông tin thanh toán tạm thời
        const tempPaymentInfo = {
          phuongThuc: 'MOMO',
          soTien: this.tinhTongTien(this.thanhToanHoaDon),
          maGiaoDich: orderId
        };
        
        // Gán thông tin thanh toán ngay lập tức để UI hiển thị
        this.thanhToanHoaDon.thanhToan = { ...tempPaymentInfo };
        
        // Đóng modal thanh toán
        this.modalThanhToan = false;
        
        // Lưu hóa đơn vào localStorage trước khi chuyển trang
        try {
          localStorage.setItem('momo_pending_hoadon', JSON.stringify(this.thanhToanHoaDon));
          localStorage.setItem('pos_return_url', '/admin/pos');
          console.log('[MOMO] Đã lưu hóa đơn và return URL vào localStorage');
        } catch (e) {
          console.error('[MOMO] Lỗi khi lưu vào localStorage:', e);
        }

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
        
        console.log('[MOMO] Dữ liệu gửi đến backend:', requestData);
        
        const response = await axios.post('http://localhost:8080/admin/api/momo/create-payment', requestData, {
          withCredentials: true,
          headers: { 'Content-Type': 'application/json' }
        });
        
        console.log('[MOMO] Phản hồi từ backend:', response.data);
        
        if (String(response.data.resultCode) === '0') {
          console.log('[MOMO] Tạo payment thành công, lưu hóa đơn vào DB và chuyển trang...');
          
          await this.xacNhanDatHangSilent(this.thanhToanHoaDon);
          
          ElMessage.info('Đang chuyển đến trang thanh toán MoMo...');
          
          setTimeout(() => {
            window.location.href = response.data.payUrl;
          }, 1000);

        } else {
          console.error('[MOMO] Backend trả về lỗi:', response.data);
          // Xóa thông tin thanh toán khi thất bại
          this.thanhToanHoaDon.thanhToan = null;
          throw new Error(response.data.message || 'Không thể tạo thanh toán MoMo từ backend');
        }
      } catch (error) {
        console.error('Lỗi khi tạo MoMo payment:', error);
        // Xóa thông tin thanh toán khi có lỗi
        this.thanhToanHoaDon.thanhToan = null;
        ElMessage.error('Không thể tạo thanh toán MoMo: ' + (error.response?.data?.message || error.message));
      } finally {
        this.momoLoading = false;
      }
    },
    
    async kiemTraTrangThaiMoMo() {
      try {
        // Gọi API kiểm tra trạng thái thanh toán
        const response = await axios.get(`http://localhost:8080/admin/api/momo/check-status/${this.momoOrderId}`, {
          withCredentials: true
        });
        
        this.momoTransactionStatus = response.data.transactionStatus;
        
        if (this.momoTransactionStatus === 'SUCCESS') {
          ElMessage.success('Thanh toán MoMo thành công!');
          this.thanhToanTienKhachDua = this.tinhTongTien(this.thanhToanHoaDon);
          this.thanhToanMaGiaoDich = response.data.transId || this.momoOrderId;
        } else if (this.momoTransactionStatus === 'FAILED') {
          ElMessage.error('Thanh toán MoMo thất bại!');
        } else {
          ElMessage.info('Thanh toán đang chờ xử lý...');
        }
      } catch (error) {
        console.error('Lỗi khi kiểm tra trạng thái MoMo:', error);
        ElMessage.error('Không thể kiểm tra trạng thái thanh toán');
      }
    },
    
    startCheckingMomoStatus(orderId, pendingHoaDon) {
      // Nếu có pendingHoaDon, đảm bảo nó được gán vào thanhToanHoaDon
      if (pendingHoaDon && pendingHoaDon.maHoaDon) {
        // Tìm tab hóa đơn tương ứng
        const tabIndex = this.hoaDons.findIndex(hd => hd.maHoaDon === pendingHoaDon.maHoaDon);
        if (tabIndex !== -1) {
          // Nếu tìm thấy, gán lại từ hoaDons để đảm bảo reference đúng
          this.thanhToanHoaDon = this.hoaDons[tabIndex];
          this.tabActive = pendingHoaDon.maHoaDon;
        } else {
          // Nếu không tìm thấy, thêm vào danh sách hoaDons
          this.hoaDons.push(pendingHoaDon);
          this.thanhToanHoaDon = pendingHoaDon;
          this.tabActive = pendingHoaDon.maHoaDon;
        }
        this.saveHoaDonsToStorage();
      }

      // Kiểm tra trạng thái mỗi 3 giây
      this.momoCheckInterval = setInterval(async () => {
        try {
          const response = await axios.get(`http://localhost:8080/admin/api/momo/check-status/${orderId}`, {
            withCredentials: true
          });
          
          if (response.data.transactionStatus === 'SUCCESS') {
            this.momoTransactionStatus = 'SUCCESS';
            clearInterval(this.momoCheckInterval);
            
            // Tự động hoàn thành thanh toán
            this.thanhToanTienKhachDua = this.tinhTongTien(this.thanhToanHoaDon);
            this.thanhToanMaGiaoDich = response.data.transId || orderId;
            this.thanhToanPhuongThuc = 'MOMO';
            
            ElMessage.success('Thanh toán MoMo thành công! Đang tự động hoàn thành đơn hàng...');
            
            // Tự động xác nhận thanh toán và lưu hóa đơn ngay lập tức
            this.thanhToanHoaDon.thanhToan = {
              phuongThuc: 'MOMO',
              soTien: this.thanhToanTienKhachDua,
              maGiaoDich: this.thanhToanMaGiaoDich
            };
            
            // Lưu hóa đơn vào database ngay lập tức
            this.xacNhanDatHang(this.thanhToanHoaDon);
            
          } else if (response.data.transactionStatus === 'FAILED') {
            this.momoTransactionStatus = 'FAILED';
            clearInterval(this.momoCheckInterval);
            ElMessage.error('Thanh toán MoMo thất bại!');
          }
        } catch (error) {
          console.error('Lỗi khi kiểm tra trạng thái MoMo:', error);
        }
      }, 3000);
      
      // Dừng kiểm tra sau 5 phút
      setTimeout(() => {
        if (this.momoCheckInterval) {
          clearInterval(this.momoCheckInterval);
          if (this.momoTransactionStatus === 'PENDING') {
            ElMessage.warning('Hết thời gian kiểm tra thanh toán MoMo. Vui lòng kiểm tra thủ công.');
          }
        }
      }, 300000); // 5 phút
    },
    
    stopCheckingMomoStatus() {
      if (this.momoCheckInterval) {
        clearInterval(this.momoCheckInterval);
        this.momoCheckInterval = null;
      }
    },
    
    getMoMoStatusText(status) {
      switch (status) {
        case 'SUCCESS':
          return 'Thanh toán thành công';
        case 'PENDING':
          return 'Đang chờ thanh toán';
        case 'FAILED':
          return 'Thanh toán thất bại';
        default:
          return 'Trạng thái không xác định';
      }
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
          keyword: this.customerSearch || ''
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
        hd.customerInfo = {
          hoTen: customer.hoTen,
          sdt: customer.sdt,
          selectedDiaChi: null,
          diaChiKhachHangs: ''
        };
        await this.fetchDiaChiKhachHang(customer.id, hd);
        this.modalCustomer = false;
        ElMessage.success(`Đã chọn khách hàng ${customer.hoTen}`);
      } catch (error) {
        console.error('Lỗi khi chọn khách hàng:', error.response?.data || error.message);
        ElMessage.error('Không thể chọn khách hàng: ' + (error.response?.data?.message || error.message));
      }
    },
    async fetchDiaChiKhachHang(idKhachHang, hd) {
      try {
        const response = await axios.get(`http://localhost:8080/admin/api/khachhang/${idKhachHang}/dia-chi`, {
          withCredentials: true,
          headers: { 'Content-Type': 'application/json; charset=UTF-8' }
        });
        this.diaChiList = response.data || [];
        if (this.diaChiList.length > 0) {
          hd.customerInfo.selectedDiaChi = this.diaChiList[0];
          hd.customerInfo.diaChiKhachHangs = this.diaChiList[0].tenDiaChi;
        }
      } catch (error) {
        console.error('Lỗi khi lấy danh sách địa chỉ:', error.response?.data || error.message);
        ElMessage.error('Không thể tải danh sách địa chỉ: ' + (error.response?.data?.message || error.message));
        this.diaChiList = [];
      }
    },
    updateDiaChi(hd) {
      if (hd.customerInfo.selectedDiaChi && hd.customerInfo.selectedDiaChi.id) {
        hd.customerInfo.diaChiKhachHangs = hd.customerInfo.selectedDiaChi.tenDiaChi;
      } else {
        hd.customerInfo.diaChiKhachHangs = '';
      }
    },
    toggleDeliveryInfo(hd) {
      if (!hd.giaoHang) {
        hd.customerInfo = null;
        hd.ghiChuGiaoHang = '';
      } else if (!hd.customerInfo && hd.idKhachHang) {
        this.fetchDiaChiKhachHang(hd.idKhachHang, hd);
      }
    },
    debounceSearchCustomers: _.debounce(function() {
      this.currentPageCustomer = 1;
      this.fetchCustomers();
    }, 300),
    searchCustomers() {
      this.currentPageCustomer = 1;
      this.fetchCustomers();
    },
    getValidVouchersForProduct(product) {
      if (!product || !this.productVouchers) return [];
      const tongTien = product.giaBan * product.soLuong;
      return this.productVouchers.filter(voucher => {
        const minAmount = voucher.giaTriDonHangToiThieu || 0;
        return tongTien >= minAmount;
      });
    },
    async handleMoMoReturn(orderId) {
      console.log('[MOMO_RETURN] Bắt đầu xử lý khi quay về từ MoMo với orderId:', orderId);
      
      // Kiểm tra xem hóa đơn đã được xử lý chưa
      let pendingHoaDon;
      try {
        const rawPendingData = localStorage.getItem('momo_pending_hoadon');
        if (!rawPendingData) {
          console.log('[MOMO_RETURN] Không có hóa đơn pending trong localStorage. Có thể đã được xử lý.');
          ElMessage.info('Hóa đơn đã được xử lý thành công!');
          return;
        }
        
        pendingHoaDon = JSON.parse(rawPendingData);
        console.log('[MOMO_RETURN] Đã khôi phục hóa đơn từ localStorage:', pendingHoaDon);
        
        if (!pendingHoaDon || !pendingHoaDon.maHoaDon) {
          console.error('[MOMO_RETURN] Hóa đơn trong localStorage không hợp lệ.');
          localStorage.removeItem('momo_pending_hoadon');
          ElMessage.error('Dữ liệu hóa đơn không hợp lệ.');
          return;
        }
      } catch (e) {
        console.error('[MOMO_RETURN] Lỗi khi đọc hóa đơn từ localStorage:', e);
        localStorage.removeItem('momo_pending_hoadon');
        ElMessage.error('Lỗi khi đọc dữ liệu hóa đơn.');
        return;
      }
      
      // Kiểm tra trạng thái thanh toán
      try {
        const response = await axios.get(`http://localhost:8080/admin/api/momo/check-status/${orderId}`, {
          withCredentials: true
        });
        
        console.log('[MOMO_RETURN] Kết quả kiểm tra trạng thái:', response.data);
        
        if (response.data.transactionStatus === 'SUCCESS') {
          console.log('[MOMO_RETURN] Thanh toán thành công! Hóa đơn đã được lưu trước đó.');
          ElMessage.success('Thanh toán MoMo thành công! Hóa đơn đã được lưu.');
          
          // Xóa dữ liệu tạm thời
          localStorage.removeItem('momo_pending_hoadon');
          
          // Không cần làm gì thêm vì hóa đơn đã được lưu vào DB trước khi chuyển trang
          
        } else if (response.data.transactionStatus === 'PENDING') {
          console.log('[MOMO_RETURN] Thanh toán vẫn đang chờ xử lý.');
          
          // Thêm hóa đơn vào danh sách nếu chưa có
          const tabIndex = this.hoaDons.findIndex(hd => hd.maHoaDon === pendingHoaDon.maHoaDon);
          if (tabIndex === -1) {
            this.hoaDons.push(pendingHoaDon);
            this.tabActive = pendingHoaDon.maHoaDon;
            this.saveHoaDonsToStorage();
          }
          
          ElMessage.info('Thanh toán MoMo đang chờ xử lý. Hệ thống sẽ tự động kiểm tra...');
          this.startCheckingMomoStatus(orderId, pendingHoaDon);
          
        } else {
          console.log('[MOMO_RETURN] Thanh toán thất bại.');
          ElMessage.error(`Thanh toán MoMo thất bại: ${response.data.message || 'Lỗi không xác định'}`);
          
          // Thêm lại hóa đơn vào danh sách để người dùng có thể thử lại
          const tabIndex = this.hoaDons.findIndex(hd => hd.maHoaDon === pendingHoaDon.maHoaDon);
          if (tabIndex === -1) {
            // Xóa thông tin thanh toán và thêm lại hóa đơn
            pendingHoaDon.thanhToan = null;
            this.hoaDons.push(pendingHoaDon);
            this.tabActive = pendingHoaDon.maHoaDon;
            this.saveHoaDonsToStorage();
          }
          
          localStorage.removeItem('momo_pending_hoadon');
        }
      } catch (error) {
        console.error('[MOMO_RETURN] Lỗi khi kiểm tra trạng thái:', error);
        ElMessage.error('Không thể kiểm tra trạng thái thanh toán MoMo.');
        
        // Thêm lại hóa đơn vào danh sách
        const tabIndex = this.hoaDons.findIndex(hd => hd.maHoaDon === pendingHoaDon.maHoaDon);
        if (tabIndex === -1) {
          this.hoaDons.push(pendingHoaDon);
          this.tabActive = pendingHoaDon.maHoaDon;
          this.saveHoaDonsToStorage();
        }
      }
    },
    
    async xacNhanDatHangSilent(hd) {
      try {
        console.log('[xacNhanDatHangSilent] Bắt đầu lưu hóa đơn âm thầm:', hd.maHoaDon);
        await this.checkAuth();
        
        if (!this.auth.isAuthenticated || !this.auth.user || !this.auth.isAdmin()) {
          console.error('[xacNhanDatHangSilent] Không có quyền admin');
          return;
        }

        if (!this.auth.user?.idNV || isNaN(this.auth.user.idNV)) {
          console.error('[xacNhanDatHangSilent] Không tìm thấy thông tin nhân viên');
          return;
        }

        if (!hd.thanhToan || hd.thanhToan.soTien < this.tinhTongTien(hd)) {
          console.error('[xacNhanDatHangSilent] Thanh toán chưa hoàn tất');
          return;
        }

        if (!hd.sanPhams || hd.sanPhams.length === 0) {
          console.error('[xacNhanDatHangSilent] Hóa đơn không có sản phẩm');
          return;
        }

        let selectedPTT;
        if (hd.thanhToan.phuongThuc === 'MOMO') {
          selectedPTT = this.phuongThucThanhToans.find(ptt => ptt.ten === 'CHUYỂN KHOẢN');
          if (!selectedPTT) {
            selectedPTT = { idPTT: 2, ten: 'CHUYỂN KHOẢN' };
          }
        } else {
          selectedPTT = this.phuongThucThanhToans.find(ptt => ptt.ten === hd.thanhToan.phuongThuc);
        }
        
        if (!selectedPTT || !selectedPTT.idPTT || isNaN(selectedPTT.idPTT)) {
          console.error('[xacNhanDatHangSilent] Phương thức thanh toán không hợp lệ');
          return;
        }

        // Tạo hoaDonData theo cấu trúc database hiện tại
        const hoaDonData = {
          maHD: `HD${Date.now()}${Math.floor(Math.random() * 1000)}`,
          idKhachHang: hd.idKhachHang ? Number(hd.idKhachHang) : null,
          idNhanVien: Number(this.auth.user.idNV),
          ngayTao: new Date().toISOString(),
          ngaySua: new Date().toISOString(),
          tongTien: Number(this.tinhTongTien(hd)).toString(),
          trangThai: 'Hoàn thành',
          loaiHoaDon: 'Tại quầy',
          chiTietSanPham: (hd.sanPhams || []).map(sp => ({
            idCtSanPham: Number(sp.id),
            soLuong: Number(sp.soLuong),
            donGia: Number(sp.giaBan).toString(),
            idKM: sp.selectedVoucherId ? Number(sp.selectedVoucherId) : null
          }))
        };

        console.log('[xacNhanDatHangSilent] Gửi dữ liệu lên API:', JSON.stringify(hoaDonData, null, 2));
        const response = await axios.post('http://localhost:8080/admin/api/hoadon', hoaDonData, {
          withCredentials: true,
          headers: { 'Content-Type': 'application/json; charset=UTF-8' }
        });

        if (response.data) {
          console.log('[xacNhanDatHangSilent] Hóa đơn tạo thành công, idHD=' + response.data.idHD);
          
          // Lưu thông tin MoMo transaction
          if (hd.thanhToan.phuongThuc === 'MOMO') {
            try {
              const momoData = {
                idHD: response.data.idHD,
                orderId: hd.thanhToan.maGiaoDich,
                requestId: hd.thanhToan.maGiaoDich,
                amount: this.tinhTongTien(hd),
                orderInfo: `Thanh toán đơn hàng ${hd.maHoaDon}`,
                transactionStatus: 'SUCCESS',
                message: 'Thanh toán thành công',
                localMessage: 'Thanh toán thành công',
                responseCode: '0'
              };
              
              await axios.post('http://localhost:8080/admin/api/momo/save-transaction', momoData, {
                withCredentials: true,
                headers: { 'Content-Type': 'application/json' }
              });
              console.log('[xacNhanDatHangSilent] Đã lưu MoMo transaction thành công');
            } catch (momoError) {
              console.error('[xacNhanDatHangSilent] Lỗi khi lưu MoMo transaction:', momoError);
            }
          }

          // Cập nhật tồn kho và voucher (âm thầm)
          for (const sp of hd.sanPhams) {
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
                anhGiay: currentProduct.anhGiay
              }, {
                withCredentials: true,
                headers: { 'Content-Type': 'application/json; charset=UTF-8' }
              });
            } catch (error) {
              console.error(`[xacNhanDatHangSilent] Lỗi cập nhật sản phẩm ${sp.id}:`, error);
            }
          }

          // Tạo chi tiết hóa đơn
          for (const sp of hd.sanPhams) {
            const hoaDonCTData = {
              idSP: Number(sp.id),
              idHD: response.data.idHD,
              idKM: sp.selectedVoucherId ? Number(sp.selectedVoucherId) : null,
              soLuong: Number(sp.soLuong),
              donGia: Number(sp.giaBan),
              thanhTien: Number(sp.giaBan * sp.soLuong - (sp.selectedVoucher ? this.tinhGiamGiaSanPham(sp) : 0)),
              idPT: Number(selectedPTT.idPTT)
            };
            try {
              await axios.post('http://localhost:8080/admin/api/hoadonchitiet', hoaDonCTData, {
                withCredentials: true,
                headers: { 'Content-Type': 'application/json; charset=UTF-8' }
              });
            } catch (error) {
              console.error('[xacNhanDatHangSilent] Lỗi tạo chi tiết hóa đơn:', error);
            }
          }

          // Xóa hóa đơn khỏi danh sách và tạo hóa đơn mới (âm thầm)
          const index = this.hoaDons.findIndex(item => item.maHoaDon === hd.maHoaDon);
          if (index !== -1) {
            this.hoaDons.splice(index, 1);
            console.log('[xacNhanDatHangSilent] Đã xóa hóa đơn khỏi danh sách.');
          }
          
          if (this.hoaDons.length === 0) {
            this.taoHoaDon();
          } else {
            this.tabActive = this.hoaDons[0].maHoaDon;
          }
          
          this.saveHoaDonsToStorage();
          console.log('[xacNhanDatHangSilent] Hoàn thành lưu hóa đơn âm thầm.');
        }
      } catch (error) {
        console.error('[xacNhanDatHangSilent] Lỗi khi lưu hóa đơn âm thầm:', error);
        // Không hiển thị thông báo lỗi để không làm gián đoạn quy trình MoMo
      }
    },
  }
};
</script>