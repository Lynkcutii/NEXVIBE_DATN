-- Tạo database
CREATE DATABASE BanQuanAoTT;
GO
USE BanQuanAoTT;
GO

-- Danh mục
CREATE TABLE DanhMuc (
                         IdDM INT IDENTITY(1,1) PRIMARY KEY,
                         TenDM NVARCHAR(100),
                         MaDM VARCHAR(50) UNIQUE;
                         TrangThai BIT
);

-- Thương hiệu
CREATE TABLE ThuongHieu (
                            IdThuongHieu INT IDENTITY(1,1) PRIMARY KEY,
                            MaThuongHieu VARCHAR(50),
                            TenThuongHieu NVARCHAR(100),
                            TrangThai BIT
);

-- Chất liệu
CREATE TABLE ChatLieu (
                          IdChatLieu INT IDENTITY(1,1) PRIMARY KEY,
                          MaChatLieu VARCHAR(50),
                          TenChatLieu NVARCHAR(100),
                          TrangThai BIT
);

-- Màu sắc
CREATE TABLE MauSac (
                        IdMauSac INT IDENTITY(1,1) PRIMARY KEY,
                        MaMauSac VARCHAR(50),
                        TenMauSac NVARCHAR(100),
                        TrangThai BIT
);

-- Size
CREATE TABLE Size (
                      IdSize INT IDENTITY(1,1) PRIMARY KEY,
                      MaSize VARCHAR(50),
                      TenSize NVARCHAR(50),
                      TrangThai BIT
);

-- Sản phẩm
CREATE TABLE SanPham (
                         IdSP INT IDENTITY(1,1) PRIMARY KEY,
                         MaSP VARCHAR(50),
                         TenSP NVARCHAR(100),
                         NgayTao Date,
                         TongSoLuongSanPham INT,
                         TrangThai BIT,
);

-- Chi tiết sản phẩm
CREATE TABLE SanPhamCT (
                           IdSPCT INT IDENTITY(1,1) PRIMARY KEY,
                           MaSPCT VARCHAR(50),
                           IdSP INT,
                           IdChatLieu INT,
                           IdThuongHieu INT,
                           IdSize INT,
                           IdMauSac INT,
                           IdDM INT,
                           SoLuong INT,
                           Gia DECIMAL(18,2),
                           MoTa NVARCHAR(MAX),
                           TrangThai BIT,
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
                     IdSPCT INT,
                     link NVARCHAR(MAX),
                     name NVARCHAR(255),
                     size NVARCHAR(50),
                     FOREIGN KEY (IdSPCT) REFERENCES SanPhamCT(IdSPCT)
);

-- Tài khoản
CREATE TABLE TaiKhoan (
                          IdTK INT IDENTITY(1,1) PRIMARY KEY,
                          TaiKhoan NVARCHAR(100),
                          MatKhau NVARCHAR(100),
                          ChucVu NVARCHAR(50),
                          TrangThai BIT
);

-- Khách hàng
CREATE TABLE KhachHang (
                           IdKH INT IDENTITY(1,1) PRIMARY KEY,
                           MaKH VARCHAR(50),
                           TenKH NVARCHAR(100),
                           GioiTinh NVARCHAR(10),
                           NgaySinh Date,
                           Email VARCHAR(100),
                           SDT VARCHAR(20),
                           DiaChi NVARCHAR(255),
                           IdTK INT,
                           TrangThai BIT,
                           FOREIGN KEY (IdTK) REFERENCES TaiKhoan(IdTK)
);

-- Nhân viên
CREATE TABLE NhanVien (
                          IdNV INT IDENTITY(1,1) PRIMARY KEY,
                          MaNV VARCHAR(50),
                          TenNV NVARCHAR(100),
                          GioiTinh NVARCHAR(10),
                          NgaySinh DATE,
                          SDT VARCHAR(20),
                          Email NVARCHAR(100),
                          DiaChi NVARCHAR(255),
                          IdTK INT,
                          TrangThai BIT,
                          FOREIGN KEY (IdTK) REFERENCES TaiKhoan(IdTK)
);

-- Phương thức thanh toán
CREATE TABLE PhuongThucThanhToan (
                                     IdPT INT IDENTITY(1,1) PRIMARY KEY,
                                     Ten NVARCHAR(50)
);

-- Khuyến mãi
CREATE TABLE KhuyenMai (
                           IdKM INT IDENTITY(1,1) PRIMARY KEY,
                           MaKM VARCHAR(50) UNIQUE NOT NULL,
                           TenKM NVARCHAR(255) NOT NULL,
                           HinhThucGiam VARCHAR(20) NOT NULL,
                           MucGiam DECIMAL(18, 2) NOT NULL,
                           GiaTriDonHangToiThieu DECIMAL(18, 2) DEFAULT 0,
                           SoLuong INT NOT NULL,
                           DaSuDung INT DEFAULT 0,
                           NgayBatDau DATETIME NOT NULL,
                           NgayKetThuc DATETIME NOT NULL,
                           TrangThai BIT DEFAULT 1,
                           IdNV INT,
                           FOREIGN KEY (IdNV) REFERENCES NhanVien(IdNV)
);

-- Voucher
CREATE TABLE Voucher (
                         IdVoucher INT PRIMARY KEY IDENTITY(1,1),
                         MaVoucher VARCHAR(50) NOT NULL,
                         TenVoucher NVARCHAR(100),
                         NgayBatDau DATE,
                         NgayKetThuc DATE,
                         TrangThai TINYINT DEFAULT 1,
                         MucGiam DECIMAL(5,2),
                         DonGiaKhiGiam DECIMAL(15,2),
                         GiaGiam DECIMAL(15,2)
);

-- Voucher sản phẩm
CREATE TABLE Voucher_SP (
                            IdVoucher_SP INT PRIMARY KEY IDENTITY(1,1),
                            IdVoucher INT,
                            IdSPCT INT,
                            TrangThai TINYINT DEFAULT 1,
                            FOREIGN KEY (IdVoucher) REFERENCES Voucher(IdVoucher),
                            FOREIGN KEY (IdSPCT) REFERENCES SanPhamCT(IdSPCT)
);

-- Hóa đơn
CREATE TABLE HoaDon (
                        IdHD INT IDENTITY(1,1) PRIMARY KEY,
                        MaHD VARCHAR(50),
                        IdKH INT,
                        IdNV INT,
                        NgayTao DATETIME,
                        NgaySua DATETIME,
                        TongTien DECIMAL(18,2),
                        TrangThai NVARCHAR(50),
                        FOREIGN KEY (IdKH) REFERENCES KhachHang(IdKH),
                        FOREIGN KEY (IdNV) REFERENCES NhanVien(IdNV)
);

-- Chi tiết hóa đơn
CREATE TABLE HoaDonCT (
                          IdHDCT INT IDENTITY(1,1) PRIMARY KEY,
                          IdSPCT INT,
                          IdHD INT,
                          IdKM INT,
                          IdPT INT,
                          SoLuong INT,
                          DonGia DECIMAL(18,2),
                          ThanhTien DECIMAL(18,2),
                          TongTien DECIMAL(18,2),
                          TrangThai NVARCHAR(50),
                          NgayTao DATETIME,
                          NgaySua DATETIME,
                          FOREIGN KEY (IdHD) REFERENCES HoaDon(IdHD),
                          FOREIGN KEY (IdSPCT) REFERENCES SanPhamCT(IdSPCT),
                          FOREIGN KEY (IdPT) REFERENCES PhuongThucThanhToan(IdPT),
                          FOREIGN KEY (IdKM) REFERENCES KhuyenMai(IdKM)
);

-- Giỏ hàng
CREATE TABLE GioHang (
                         IdGH INT IDENTITY(1,1) PRIMARY KEY,
                         MaGH VARCHAR(50),
                         IdKH INT,
                         TrangThai BIT,
                         NgayTao DATETIME,
                         NgaySua DATETIME,
                         IdTK INT,
                         FOREIGN KEY (IdKH) REFERENCES KhachHang(IdKH),
                         FOREIGN KEY (IdTK) REFERENCES TaiKhoan(IdTK)
);

-- Chi tiết giỏ hàng
CREATE TABLE GioHangCT (
                           IdGHCT INT IDENTITY(1,1) PRIMARY KEY,
                           IdGH INT,
                           IdSPCT INT,
                           SoLuong INT,
                           DonGia DECIMAL(18,2),
                           FOREIGN KEY (IdGH) REFERENCES GioHang(IdGH),
                           FOREIGN KEY (IdSPCT) REFERENCES SanPhamCT(IdSPCT)
);
INSERT INTO DanhMuc (TenDM, TrangThai)
VALUES
    (N'Áo thun', 1),
    (N'Quần jeans', 1),
    (N'Áo khoác', 1);
INSERT INTO ThuongHieu (MaThuongHieu, TenThuongHieu, TrangThai)
VALUES
    ('TH001', N'Nike', 1),
    ('TH002', N'Adidas', 1),
    ('TH003', N'Puma', 1);
INSERT INTO ChatLieu (MaChatLieu, TenChatLieu, TrangThai)
VALUES
    ('CL001', N'Cotton', 1),
    ('CL002', N'Polyester', 1),
    ('CL003', N'Jean', 1);
INSERT INTO MauSac (MaMauSac, TenMauSac, TrangThai)
VALUES
    ('MS001', N'Đen', 1),
    ('MS002', N'Trắng', 1),
    ('MS003', N'Xanh dương', 1);
INSERT INTO Size (MaSize, TenSize, TrangThai)
VALUES
    ('S', 'Small', 1),
    ('M', 'Medium', 1),
    ('L', 'Large', 1);
INSERT INTO SanPham (MaSP, TenSP, NgayTao, TongSoLuongSanPham, TrangThai) VALUES
                                                                              ('SP001', N'Áo Thun Nam Basic', GETDATE(), 100, 1),
                                                                              ('SP002', N'Quần Jogger Thể Thao', GETDATE(), 80, 1),
                                                                              ('SP003', N'Áo Khoác Dù', GETDATE(), 60, 1);

INSERT INTO SanPhamCT (MaSPCT, IdSP, IdChatLieu, IdThuongHieu, IdSize, IdMauSac, IdDM, SoLuong, Gia, MoTa, TrangThai) VALUES
                                                                                                                          ('SPCT001', 1, 1, 1, 1, 1, 1, 30, 199000, N'Áo thun chất cotton co giãn', 1),
                                                                                                                          ('SPCT002', 2, 2, 2, 2, 2, 2, 25, 299000, N'Jogger co giãn năng động', 1),
                                                                                                                          ('SPCT003', 3, 3, 3, 3, 3, 3, 20, 399000, N'Áo khoác chống nước', 1);

