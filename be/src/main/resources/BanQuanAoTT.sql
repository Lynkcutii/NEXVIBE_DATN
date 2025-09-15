-- Tạo database
CREATE DATABASE BanQuanAoTT;
GO
USE BanQuanAoTT;
GO

-- ========================
-- TẠO CÁC BẢNG
-- ========================

-- Danh mục
CREATE TABLE DanhMuc (
                         IdDM INT IDENTITY(1,1) PRIMARY KEY,
                         TenDM NVARCHAR(100) NOT NULL,
                         MaDM VARCHAR(50) NOT NULL UNIQUE,
                         TrangThai BIT DEFAULT 1
);

-- Thương hiệu
CREATE TABLE ThuongHieu (
                            IdThuongHieu INT IDENTITY(1,1) PRIMARY KEY,
                            MaThuongHieu VARCHAR(50) NOT NULL,
                            TenThuongHieu NVARCHAR(100) NOT NULL,
                            TrangThai BIT DEFAULT 1
);

-- Chất liệu
CREATE TABLE ChatLieu (
                          IdChatLieu INT IDENTITY(1,1) PRIMARY KEY,
                          MaChatLieu VARCHAR(50) NOT NULL,
                          TenChatLieu NVARCHAR(100) NOT NULL,
                          TrangThai BIT DEFAULT 1
);

-- Màu sắc
CREATE TABLE MauSac (
                        IdMauSac INT IDENTITY(1,1) PRIMARY KEY,
                        MaMauSac VARCHAR(50) NOT NULL,
                        TenMauSac NVARCHAR(100) NOT NULL,
                        TrangThai BIT DEFAULT 1
);

-- Size
CREATE TABLE Size (
                      IdSize INT IDENTITY(1,1) PRIMARY KEY,
                      MaSize VARCHAR(50) NOT NULL,
                      TenSize NVARCHAR(50) NOT NULL,
                      TrangThai BIT DEFAULT 1
);

-- Sản phẩm
CREATE TABLE SanPham (
                         IdSP INT IDENTITY(1,1) PRIMARY KEY,
                         MaSP VARCHAR(50) NOT NULL,
                         TenSP NVARCHAR(100) NOT NULL,
                         NgayTao DATE NOT NULL,
                         TongSoLuongSanPham INT NOT NULL,
                         TrangThai BIT DEFAULT 1
);

-- Chi tiết sản phẩm
CREATE TABLE SanPhamCT (
                           IdSPCT INT IDENTITY(1,1) PRIMARY KEY,
                           MaSPCT VARCHAR(50) NOT NULL,
                           IdSP INT NOT NULL,
                           IdChatLieu INT NOT NULL,
                           IdThuongHieu INT NOT NULL,
                           IdSize INT NOT NULL,
                           IdMauSac INT NOT NULL,
                           IdDM INT NOT NULL,
                           SoLuong INT NOT NULL,
                           Gia DECIMAL(18,2) NOT NULL,
                           MoTa NVARCHAR(MAX),
                           TrangThai BIT DEFAULT 1,
                           FOREIGN KEY (IdSP) REFERENCES SanPham(IdSP),
                           FOREIGN KEY (IdChatLieu) REFERENCES ChatLieu(IdChatLieu),
                           FOREIGN KEY (IdThuongHieu) REFERENCES ThuongHieu(IdThuongHieu),
                           FOREIGN KEY (IdSize) REFERENCES Size(IdSize),
                           FOREIGN KEY (IdMauSac) REFERENCES MauSac(IdMauSac),
                           FOREIGN KEY (IdDM) REFERENCES DanhMuc(IdDM)
);

-- Hình ảnh sản phẩm
CREATE TABLE Img (
                     IdImg INT IDENTITY(1,1) PRIMARY KEY,
                     IdSPCT INT NOT NULL,
                     link NVARCHAR(MAX) NOT NULL,
                     name NVARCHAR(255) NOT NULL,
                     size NVARCHAR(50),
                     FOREIGN KEY (IdSPCT) REFERENCES SanPhamCT(IdSPCT)
);

-- Tài khoản
CREATE TABLE TaiKhoan (
                          IdTK INT IDENTITY(1,1) PRIMARY KEY,
                          TaiKhoan NVARCHAR(100) NOT NULL,
                          MatKhau NVARCHAR(100) NOT NULL,
                          ChucVu NVARCHAR(50) NOT NULL,
                          TrangThai BIT DEFAULT 1
);

-- Khách hàng
CREATE TABLE KhachHang (
                           IdKH INT IDENTITY(1,1) PRIMARY KEY,
                           MaKH VARCHAR(50) NOT NULL,
                           TenKH NVARCHAR(100) NOT NULL,
                           GioiTinh NVARCHAR(10) NOT NULL,
                           NgaySinh DATE NOT NULL,
                           Email VARCHAR(100) NOT NULL,
                           SDT VARCHAR(20) NOT NULL,
                           IdTK INT NOT NULL,
                           TrangThai BIT DEFAULT 1,
                           FOREIGN KEY (IdTK) REFERENCES TaiKhoan(IdTK)
);

-- Địa chỉ khách hàng
CREATE TABLE DiaChiKhachHang (
                                 IdDiaChi INT IDENTITY(1,1) PRIMARY KEY,
                                 IdKH INT NOT NULL,
                                 DiaChiCuThe NVARCHAR(255) NOT NULL,
                                 TinhThanh NVARCHAR(100) NOT NULL,
                                 PhuongXa NVARCHAR(100) NOT NULL,
                                 SoDienThoai VARCHAR(20),
                                 GhiChu NVARCHAR(255),
                                 TrangThai BIT DEFAULT 1,
                                 FOREIGN KEY (IdKH) REFERENCES KhachHang(IdKH)
);

-- Nhân viên
CREATE TABLE NhanVien (
                          IdNV INT IDENTITY(1,1) PRIMARY KEY,
                          MaNV VARCHAR(50) NOT NULL,
                          TenNV NVARCHAR(100) NOT NULL,
                          GioiTinh NVARCHAR(10) NOT NULL,
                          NgaySinh DATE NOT NULL,
                          SDT VARCHAR(20) NOT NULL,
                          Email NVARCHAR(100) NOT NULL,
                          DiaChi NVARCHAR(255) NOT NULL,
                          IdTK INT NOT NULL,
                          TrangThai BIT DEFAULT 1,
                          FOREIGN KEY (IdTK) REFERENCES TaiKhoan(IdTK)
);

-- Phương thức thanh toán
CREATE TABLE PhuongThucThanhToan (
                                     IdPT INT IDENTITY(1,1) PRIMARY KEY,
                                     Ten NVARCHAR(50) NOT NULL
);

-- Khuyến mãi
CREATE TABLE KhuyenMai (
                           IdKM INT IDENTITY(1,1) PRIMARY KEY,
                           MaKM VARCHAR(50) NOT NULL UNIQUE,
                           TenKM NVARCHAR(255) NOT NULL,
                           HinhThucGiam VARCHAR(20) NOT NULL,
                           MucGiam DECIMAL(18,2) NOT NULL,
                           GiaTriDonHangToiThieu DECIMAL(18,2) DEFAULT 0,
                           GiamToiDa DECIMAL(15,2),
                           SoLuong INT NOT NULL,
                           DaSuDung INT DEFAULT 0,
                           NgayBatDau DATETIME NOT NULL,
                           NgayKetThuc DATETIME NOT NULL,
                           TrangThai BIT DEFAULT 1,
                           IdKH INT,
                           FOREIGN KEY (IdKH) REFERENCES KhachHang(IdKH)
);

-- Voucher
CREATE TABLE Voucher (
                         IdVoucher INT PRIMARY KEY IDENTITY(1,1),
                         MaVoucher VARCHAR(50) NOT NULL,
                         TenVoucher NVARCHAR(100) NOT NULL,
                         NgayBatDau DATE NOT NULL,
                         NgayKetThuc DATE NOT NULL,
                         SoLuong INT NOT NULL,
                         HinhThucGiam VARCHAR(20) NOT NULL,
                         GiamToiDa DECIMAL(15,2),
                         MucGiam DECIMAL(5,2),
                         DonGiaKhiGiam DECIMAL(15,2),
                         GiaGiam DECIMAL(15,2),
                         TrangThai TINYINT DEFAULT 1
);

-- Voucher sản phẩm
CREATE TABLE Voucher_SP (
                            IdVoucher_SP INT PRIMARY KEY IDENTITY(1,1),
                            IdVoucher INT NOT NULL,
                            IdSPCT INT NOT NULL,
                            TrangThai TINYINT DEFAULT 1,
                            FOREIGN KEY (IdVoucher) REFERENCES Voucher(IdVoucher),
                            FOREIGN KEY (IdSPCT) REFERENCES SanPhamCT(IdSPCT)
);

-- Hóa đơn
CREATE TABLE HoaDon (
                        IdHD INT IDENTITY(1,1) PRIMARY KEY,
                        MaHD VARCHAR(50) NOT NULL,
                        IdKH INT,
                        IdNV INT,
                        IdKM INT,
                        NgayTao DATETIME NOT NULL,
                        NgaySua DATETIME,
                        TongTien DECIMAL(18,2) NOT NULL,
                        TrangThai NVARCHAR(50) NOT NULL,
                        FOREIGN KEY (IdKH) REFERENCES KhachHang(IdKH),
                        FOREIGN KEY (IdNV) REFERENCES NhanVien(IdNV),
                        FOREIGN KEY (IdKM) REFERENCES KhuyenMai(IdKM)
);

-- Chi tiết hóa đơn
CREATE TABLE HoaDonCT (
                          IdHDCT INT IDENTITY(1,1) PRIMARY KEY,
                          IdSPCT INT NOT NULL,
                          IdHD INT NOT NULL,
                          IdPT INT NOT NULL,
                          SoLuong INT NOT NULL,
                          DonGia DECIMAL(18,2) NOT NULL,
                          ThanhTien DECIMAL(18,2) NOT NULL,
                          NgayTao DATETIME NOT NULL,
                          NgaySua DATETIME,
                          FOREIGN KEY (IdHD) REFERENCES HoaDon(IdHD),
                          FOREIGN KEY (IdSPCT) REFERENCES SanPhamCT(IdSPCT),
                          FOREIGN KEY (IdPT) REFERENCES PhuongThucThanhToan(IdPT)
);

-- Giỏ hàng
CREATE TABLE GioHang (
                         IdGH INT IDENTITY(1,1) PRIMARY KEY,
                         MaGH VARCHAR(50) NOT NULL,
                         IdKH INT NOT NULL,
                         TrangThai BIT DEFAULT 1,
                         NgayTao DATETIME NOT NULL,
                         NgaySua DATETIME,
                         IdTK INT NOT NULL,
                         FOREIGN KEY (IdKH) REFERENCES KhachHang(IdKH),
                         FOREIGN KEY (IdTK) REFERENCES TaiKhoan(IdTK)
);

-- Chi tiết giỏ hàng
CREATE TABLE GioHangCT (
                           IdGHCT INT IDENTITY(1,1) PRIMARY KEY,
                           IdGH INT NOT NULL,
                           IdSPCT INT NOT NULL,
                           SoLuong INT NOT NULL,
                           DonGia DECIMAL(18,2) NOT NULL,
                           FOREIGN KEY (IdGH) REFERENCES GioHang(IdGH),
                           FOREIGN KEY (IdSPCT) REFERENCES SanPhamCT(IdSPCT)
);

-- ========================
-- CHÈN DỮ LIỆU MẪU
-- ========================

-- Phương thức thanh toán
INSERT INTO PhuongThucThanhToan (Ten) VALUES
                                          (N'THANH TOÁN KHI NHẬN HÀNG'),
                                          (N'CHUYỂN KHOẢN'),
                                          (N'TIỀN MẶT');

-- Danh mục
INSERT INTO DanhMuc (TenDM, MaDM, TrangThai) VALUES
                                                 (N'Áo thun', 'DM001', 1),
                                                 (N'Quần jeans', 'DM002', 1),
                                                 (N'Áo khoác', 'DM003', 1);

-- Thương hiệu
INSERT INTO ThuongHieu (MaThuongHieu, TenThuongHieu, TrangThai) VALUES
                                                                    ('TH001', N'Nike', 1),
                                                                    ('TH002', N'Adidas', 1),
                                                                    ('TH003', N'Puma', 1);

-- Chất liệu
INSERT INTO ChatLieu (MaChatLieu, TenChatLieu, TrangThai) VALUES
                                                              ('CL001', N'Cotton', 1),
                                                              ('CL002', N'Polyester', 1),
                                                              ('CL003', N'Jean', 1);

-- Màu sắc
INSERT INTO MauSac (MaMauSac, TenMauSac, TrangThai) VALUES
                                                        ('MS001', N'Đen', 1),
                                                        ('MS002', N'Trắng', 1),
                                                        ('MS003', N'Xanh dương', 1);

-- Size
INSERT INTO Size (MaSize, TenSize, TrangThai) VALUES
                                                  ('S', 'Small', 1),
                                                  ('M', 'Medium', 1),
                                                  ('L', 'Large', 1);

-- Sản phẩm
INSERT INTO SanPham (MaSP, TenSP, NgayTao, TongSoLuongSanPham, TrangThai) VALUES
                                                                              ('SP001', N'Áo Thun Nam Basic', GETDATE(), 100, 1),
                                                                              ('SP002', N'Quần Jogger Thể Thao', GETDATE(), 80, 1),
                                                                              ('SP003', N'Áo Khoác Dù', GETDATE(), 60, 1);

-- Chi tiết sản phẩm
INSERT INTO SanPhamCT (MaSPCT, IdSP, IdChatLieu, IdThuongHieu, IdSize, IdMauSac, IdDM, SoLuong, Gia, MoTa, TrangThai) VALUES
                                                                                                                          ('SPCT001', 1, 1, 1, 1, 1, 1, 30, 199000, N'Áo thun chất cotton co giãn', 1),
                                                                                                                          ('SPCT002', 2, 2, 2, 2, 2, 2, 25, 299000, N'Jogger co giãn năng động', 1),
                                                                                                                          ('SPCT003', 3, 3, 3, 3, 3, 3, 20, 399000, N'Áo khoác chống nước', 1);

-- Voucher
INSERT INTO Voucher (
    MaVoucher, TenVoucher, NgayBatDau, NgayKetThuc, SoLuong,
    HinhThucGiam, GiamToiDa, MucGiam, DonGiaKhiGiam, GiaGiam, TrangThai
) VALUES
      ('VC001', N'Giảm 10%', '2025-08-01', '2025-09-01', 100, '%', 50000, 10.00, 0, 0, 1),
      ('VC002', N'Giảm còn 250K', '2025-08-01', '2025-09-01', 100, 'VND', 0, 0.00, 250000, 0, 1),
      ('VC003', N'Giảm 50K', '2025-08-01', '2025-09-01', 100, 'VND', 0, 0.00, 0, 50000, 1);

-- Voucher sản phẩm
INSERT INTO Voucher_SP (IdVoucher, IdSPCT, TrangThai) VALUES
                                                          (1, 1, 1),
                                                          (2, 2, 1),
                                                          (3, 3, 1);