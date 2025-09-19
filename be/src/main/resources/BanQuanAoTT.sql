-- Tạo database mới
CREATE DATABASE BanQuanAoTT;
GO
USE BanQuanAoTT;
GO

/* ========================
   TẠO CÁC BẢNG (đảm bảo thứ tự FK)
   ======================== */

-- 1. Danh mục
CREATE TABLE DanhMuc (
                         IdDM INT IDENTITY(1,1) PRIMARY KEY,
                         TenDM NVARCHAR(100) NOT NULL,
                         MaDM VARCHAR(50) NOT NULL UNIQUE,
                         TrangThai BIT DEFAULT 1
);

-- 2. Thương hiệu
CREATE TABLE ThuongHieu (
                            IdThuongHieu INT IDENTITY(1,1) PRIMARY KEY,
                            MaThuongHieu VARCHAR(50) NOT NULL,
                            TenThuongHieu NVARCHAR(100) NOT NULL,
                            TrangThai BIT DEFAULT 1
);

-- 3. Chất liệu
CREATE TABLE ChatLieu (
                          IdChatLieu INT IDENTITY(1,1) PRIMARY KEY,
                          MaChatLieu VARCHAR(50) NOT NULL,
                          TenChatLieu NVARCHAR(100) NOT NULL,
                          TrangThai BIT DEFAULT 1
);

-- 4. Màu sắc
CREATE TABLE MauSac (
                        IdMauSac INT IDENTITY(1,1) PRIMARY KEY,
                        MaMauSac VARCHAR(50) NOT NULL,
                        TenMauSac NVARCHAR(100) NOT NULL,
                        TrangThai BIT DEFAULT 1
);

-- 5. Size
CREATE TABLE Size (
                      IdSize INT IDENTITY(1,1) PRIMARY KEY,
                      MaSize VARCHAR(50) NOT NULL,
                      TenSize NVARCHAR(50) NOT NULL,
                      TrangThai BIT DEFAULT 1
);

-- 6. Sản phẩm
CREATE TABLE SanPham (
                         IdSP INT IDENTITY(1,1) PRIMARY KEY,
                         MaSP VARCHAR(50) NOT NULL,
                         TenSP NVARCHAR(100) NOT NULL,
                         IdThuongHieu INT NOT NULL,
                         IdDM INT NOT NULL,
                         IdChatLieu INT NOT NULL,
                         NgayTao DATE NOT NULL,
                         TongSoLuongSanPham INT NOT NULL,
                         MoTa NVARCHAR(MAX),
                         Img NVARCHAR(MAX) NOT NULL,
                         Gia DECIMAL(18,2) NOT NULL,
                         TrangThai BIT DEFAULT 1,
                         FOREIGN KEY (IdThuongHieu) REFERENCES ThuongHieu(IdThuongHieu),
                         FOREIGN KEY (IdChatLieu) REFERENCES ChatLieu(IdChatLieu),
                         FOREIGN KEY (IdDM) REFERENCES DanhMuc(IdDM)
);

-- 7. Chi tiết sản phẩm
CREATE TABLE SanPhamCT (
                           IdSPCT INT IDENTITY(1,1) PRIMARY KEY,
                           MaSPCT VARCHAR(50) NOT NULL,
                           IdSP INT NOT NULL,
                           IdSize INT NOT NULL,
                           IdMauSac INT NOT NULL,
                           SoLuong INT NOT NULL,
                           Gia DECIMAL(18,2) NOT NULL,
                           TrangThai BIT DEFAULT 1,
                           FOREIGN KEY (IdSP) REFERENCES SanPham(IdSP),
                           FOREIGN KEY (IdSize) REFERENCES Size(IdSize),
                           FOREIGN KEY (IdMauSac) REFERENCES MauSac(IdMauSac)
);

-- 8. Hình ảnh sản phẩm
CREATE TABLE Img (
                     IdImg INT IDENTITY(1,1) PRIMARY KEY,
                     IdSPCT INT NOT NULL,
                     link NVARCHAR(MAX) NOT NULL,
                     FOREIGN KEY (IdSPCT) REFERENCES SanPhamCT(IdSPCT)
);

-- 9. Tài khoản
CREATE TABLE TaiKhoan (
                          IdTK INT IDENTITY(1,1) PRIMARY KEY,
                          TaiKhoan NVARCHAR(100) NOT NULL,
                          MatKhau NVARCHAR(100) NOT NULL,
                          ChucVu NVARCHAR(50) NOT NULL,
                          TrangThai BIT DEFAULT 1
);

-- 10. Khách hàng
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

-- 11. Địa chỉ khách hàng
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

-- 12. Nhân viên
CREATE TABLE NhanVien (
                          IdNV INT IDENTITY(1,1) PRIMARY KEY,
                          MaNV VARCHAR(50) NOT NULL,
                          TenNV NVARCHAR(100) NOT NULL,
                          GioiTinh NVARCHAR(10) ,
                          NgaySinh DATE ,
                          SDT VARCHAR(20) NOT NULL,
                          Email NVARCHAR(100) ,
                          DiaChi NVARCHAR(255) ,
                          IdTK INT ,
                          TrangThai BIT DEFAULT 1,
                          FOREIGN KEY (IdTK) REFERENCES TaiKhoan(IdTK)
);

-- 13. Phương thức thanh toán
CREATE TABLE PhuongThucThanhToan (
                                     IdPT INT IDENTITY(1,1) PRIMARY KEY,
                                     Ten NVARCHAR(50) NOT NULL
);

-- 14. Khuyến mãi
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
                           IdKH INT NULL,
                           FOREIGN KEY (IdKH) REFERENCES KhachHang(IdKH)
);

-- 15. Voucher
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

-- 16. Voucher sản phẩm
CREATE TABLE Voucher_SP (
                            IdVoucher_SP INT PRIMARY KEY IDENTITY(1,1),
                            IdVoucher INT NOT NULL,
                            IdSPCT INT NOT NULL,
                            TrangThai TINYINT DEFAULT 1,
                            FOREIGN KEY (IdVoucher) REFERENCES Voucher(IdVoucher),
                            FOREIGN KEY (IdSPCT) REFERENCES SanPhamCT(IdSPCT)
);

-- 17. Hóa đơn
CREATE TABLE HoaDon (
                        IdHD INT IDENTITY(1,1) PRIMARY KEY,
                        MaHD VARCHAR(50) NOT NULL,
                        IdKH INT,
                        IdNV INT,
                        IdKH INT,
                        NgayTao DATETIME NOT NULL,
                        NgaySua DATETIME NULL,
                        TongTien DECIMAL(18,2) NOT NULL,
                        LoaiHoaDon NVARCHAR(50) NOT NULL,
                        TrangThai NVARCHAR(50) NOT NULL,
                        FOREIGN KEY (IdKH) REFERENCES KhachHang(IdKH),
                        FOREIGN KEY (IdNV) REFERENCES NhanVien(IdNV),
                        FOREIGN KEY (IdKM) REFERENCES KhuyenMai(IdKM),
                        FOREIGN KEY (IdPT) REFERENCES PhuongThucThanhToan(IdPT)
);

-- 18. Chi tiết hóa đơn
CREATE TABLE HoaDonCT (
                          IdHDCT INT IDENTITY(1,1) PRIMARY KEY,
                          IdSPCT INT NOT NULL,
                          IdHD INT NOT NULL,
                          IdVoucher INT NULL,
                          SoLuong INT NOT NULL,
                          DonGia DECIMAL(18,2) NOT NULL,
                          ThanhTien DECIMAL(18,2) NOT NULL,
                          NgayTao DATETIME NOT NULL,
                          NgaySua DATETIME NULL,
                          FOREIGN KEY (IdHD) REFERENCES HoaDon(IdHD),
                          FOREIGN KEY (IdSPCT) REFERENCES SanPhamCT(IdSPCT),
                          FOREIGN KEY (IdVoucher) REFERENCES Voucher(IdVoucher),
);

-- 19. Giỏ hàng
CREATE TABLE GioHang (
                         IdGH INT IDENTITY(1,1) PRIMARY KEY,
                         MaGH VARCHAR(50) NOT NULL,
                         IdKH INT NOT NULL,
                         TrangThai BIT DEFAULT 1,
                         NgayTao DATETIME NOT NULL,
                         NgaySua DATETIME NULL,
                         IdTK INT NOT NULL,
                         FOREIGN KEY (IdKH) REFERENCES KhachHang(IdKH),
                         FOREIGN KEY (IdTK) REFERENCES TaiKhoan(IdTK)
);

-- 20. Chi tiết giỏ hàng
CREATE TABLE GioHangCT (
                           IdGHCT INT IDENTITY(1,1) PRIMARY KEY,
                           IdGH INT NOT NULL,
                           IdSPCT INT NOT NULL,
                           SoLuong INT NOT NULL,
                           DonGia DECIMAL(18,2) NOT NULL,
                           FOREIGN KEY (IdGH) REFERENCES GioHang(IdGH),
                           FOREIGN KEY (IdSPCT) REFERENCES SanPhamCT(IdSPCT)
);

CREATE TABLE MoMoTransaction (
                                 IdMoMo INT IDENTITY(1,1) PRIMARY KEY,
                                 IdHD INT NULL, -- Liên kết với hóa đơn (NULL khi chưa hoàn thành)
                                 OrderId NVARCHAR(100) NOT NULL UNIQUE, -- Mã đơn hàng từ hệ thống
                                 RequestId NVARCHAR(100) NOT NULL UNIQUE, -- Request ID từ MoMo
                                 Amount DECIMAL(18,2) NOT NULL, -- Số tiền giao dịch
                                 OrderInfo NVARCHAR(255) NOT NULL, -- Thông tin đơn hàng
                                 RedirectUrl NVARCHAR(500), -- URL redirect sau thanh toán
                                 IpnUrl NVARCHAR(500), -- URL nhận thông báo từ MoMo
                                 PayUrl NVARCHAR(1000), -- URL thanh toán QR từ MoMo
                                 QrCodeUrl NVARCHAR(1000), -- URL QR code
                                 DeepLink NVARCHAR(1000), -- Deep link mở app MoMo
                                 TransId NVARCHAR(100), -- Transaction ID từ MoMo (sau khi thanh toán)
                                 ResponseCode NVARCHAR(10), -- Mã phản hồi từ MoMo
                                 Message NVARCHAR(255), -- Thông báo từ MoMo
                                 LocalMessage NVARCHAR(255), -- Thông báo địa phương hóa
                                 PayType NVARCHAR(50), -- Loại thanh toán (qr, webpay, etc.)
                                 TransactionStatus NVARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, SUCCESS, FAILED, CANCELLED
                                 NgayTao DATETIME NOT NULL DEFAULT GETDATE(),
                                 NgayCapNhat DATETIME,
                                 ExtraData NVARCHAR(MAX), -- Dữ liệu bổ sung
                                 Signature NVARCHAR(500), -- Chữ ký từ MoMo
                                 FOREIGN KEY (IdHD) REFERENCES HoaDon(IdHD)
);

/* ========================
   DỮ LIỆU MẪU ĐÃ SỬA CHỮA
   ======================== */

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
INSERT INTO SanPham (MaSP, TenSP, IdThuongHieu, IdDM, IdChatLieu, NgayTao, TongSoLuongSanPham, Img, MoTa, Gia, TrangThai) VALUES
                                                                                                                              ('SP001', N'Áo Thun Nam Basic', 1, 1, 1, GETDATE(), 100,
                                                                                                                               'https://images.pexels.com/photos/428340/pexels-photo-428340.jpeg',
                                                                                                                               N'Áo thun nam cotton cao cấp, co giãn 4 chiều, thấm hút mồ hôi tốt, kiểu dáng basic dễ phối đồ.', 199000, 1),
                                                                                                                              ('SP002', N'Quần Jogger Thể Thao', 2, 2, 2, GETDATE(), 80,
                                                                                                                               'https://images.pexels.com/photos/6311606/pexels-photo-6311606.jpeg',
                                                                                                                               N'Quần jogger thể thao chất polyester thoáng mát, bo gấu cá tính, phù hợp tập gym và đi chơi.', 299000, 1),
                                                                                                                              ('SP003', N'Áo Khoác Dù Nam', 3, 3, 3, GETDATE(), 60,
                                                                                                                               'https://images.pexels.com/photos/4041684/pexels-photo-4041684.jpeg',
                                                                                                                               N'Áo khoác dù chống nước, nhẹ, phù hợp cả nam và nữ, phong cách streetwear hiện đại.', 399000, 1);

-- Chi tiết sản phẩm
INSERT INTO SanPhamCT (MaSPCT, IdSP, IdSize, IdMauSac, SoLuong, Gia, TrangThai) VALUES
                                                                                    ('SPCT001', 1, 1, 1, 30, 199000, 1),
                                                                                    ('SPCT002', 2, 2, 2, 25, 299000, 1),
                                                                                    ('SPCT003', 3, 3, 3, 20, 399000, 1);

-- Ảnh sản phẩm chi tiết
INSERT INTO Img (IdSPCT, link) VALUES
                                   (1, 'https://images.pexels.com/photos/428340/pexels-photo-428340.jpeg'),
                                   (1, 'https://images.pexels.com/photos/994517/pexels-photo-994517.jpeg'),
                                   (1, 'https://images.pexels.com/photos/428338/pexels-photo-428338.jpeg'),
                                   (2, 'https://images.pexels.com/photos/6311606/pexels-photo-6311606.jpeg'),
                                   (2, 'https://images.pexels.com/photos/6311627/pexels-photo-6311627.jpeg'),
                                   (2, 'https://images.pexels.com/photos/6311637/pexels-photo-6311637.jpeg'),
                                   (3, 'https://images.pexels.com/photos/4041684/pexels-photo-4041684.jpeg'),
                                   (3, 'https://images.pexels.com/photos/6311376/pexels-photo-6311376.jpeg'),
                                   (3, 'https://images.pexels.com/photos/4041683/pexels-photo-4041683.jpeg');

-- Tài khoản (mật khẩu: 123456 - bcrypt mẫu)
INSERT INTO TaiKhoan (TaiKhoan, MatKhau, ChucVu, TrangThai) VALUES
                                                                ('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', 1),
                                                                ('customer1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'KHACH_HANG', 1),
                                                                ('customer2', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'KHACH_HANG', 1),
                                                                ('admin2', '$2y$10$d2tN83RdDo/p54rFv7WSjOFQveBGzHNq2mJbW6Y.c7wzof.QNNxn6', 'ADMIN', 1);

-- Nhân viên
INSERT INTO NhanVien (MaNV, TenNV, GioiTinh, NgaySinh, SDT, Email, DiaChi, IdTK, TrangThai) VALUES
    ('NV001', N'Nguyễn Văn Admin', N'Nam', '1990-01-01', '0123456789', 'admin@nexvibe.com', N'Hà Nội', 1, 1);

-- Khách hàng
INSERT INTO KhachHang (MaKH, TenKH, GioiTinh, NgaySinh, Email, SDT, IdTK, TrangThai) VALUES
                                                                                         ('KH001', N'Trần Thị Khách Hàng 1', N'Nữ', '2000-01-01', 'customer1@email.com', '0987654321', 2, 1),
                                                                                         ('KH002', N'Lê Văn Khách Hàng 2', N'Nam', '2001-02-02', 'customer2@email.com', '0123456780', 3, 1);

-- Địa chỉ KH
INSERT INTO DiaChiKhachHang (IdKH, DiaChiCuThe, TinhThanh, PhuongXa, SoDienThoai, GhiChu, TrangThai) VALUES
                                                                                                         (1, N'123 Trần Duy Hưng', N'Hà Nội', N'Cầu Giấy', '0987654321', NULL, 1),
                                                                                                         (2, N'45 Lê Lợi', N'Hồ Chí Minh', N'Quận 1', '0123456780', NULL, 1);

-- Phương thức thanh toán
INSERT INTO PhuongThucThanhToan (Ten) VALUES
                                          (N'THANH TOÁN KHI NHẬN HÀNG'),
                                          (N'CHUYỂN KHOẢN'),
                                          (N'TIỀN MẶT');

-- Khuyến mãi
INSERT INTO KhuyenMai (MaKM, TenKM, HinhThucGiam, MucGiam, GiaTriDonHangToiThieu, GiamToiDa, SoLuong, DaSuDung, NgayBatDau, NgayKetThuc, TrangThai) VALUES
                                                                                                                                                        ('KM001', N'Giảm giá mùa hè', 'PHAN_TRAM', 10.00, 500000, 50000, 100, 0, '2024-01-01', '2024-12-31', 1),
                                                                                                                                                        ('KM002', N'Khuyến mại sinh nhật', 'PHAN_TRAM', 15.00, 300000, 100000, 50, 0, '2024-01-01', '2024-12-31', 1);

-- Voucher
INSERT INTO Voucher (MaVoucher, TenVoucher, NgayBatDau, NgayKetThuc, SoLuong, HinhThucGiam, GiamToiDa, MucGiam, DonGiaKhiGiam, GiaGiam, TrangThai) VALUES
                                                                                                                                                       ('VC001', N'Voucher chào mừng', '2024-01-01', '2024-12-31', 100, 'PHAN_TRAM', 50000, 10.00, 500000, 50000, 1),
                                                                                                                                                       ('VC002', N'Voucher khách hàng VIP', '2024-01-01', '2024-12-31', 50, 'PHAN_TRAM', 100000, 15.00, 1000000, 100000, 1);

-- Voucher sản phẩm
INSERT INTO Voucher_SP (IdVoucher, IdSPCT, TrangThai) VALUES

                                                          (2, 2, 1),

                                                          (1, 2, 1),
                                                          (2, 3, 1);

-- Giỏ hàng
INSERT INTO GioHang (MaGH, IdKH, TrangThai, NgayTao, NgaySua, IdTK) VALUES
                                                                        ('GH001', 1, 1, GETDATE(), GETDATE(), 2),
                                                                        ('GH002', 2, 1, GETDATE(), GETDATE(), 3);

-- Chi tiết giỏ hàng
INSERT INTO GioHangCT (IdGH, IdSPCT, SoLuong, DonGia) VALUES
                                                          (1, 1, 2, 199000),
                                                          (1, 2, 1, 299000),
                                                          (2, 3, 1, 399000);

-- Hóa đơn
INSERT INTO HoaDon (MaHD, IdKH, IdNV, IdKM, IdPT, NgayTao, NgaySua, TongTien, LoaiHoaDon, TrangThai) VALUES
                                                                                                         ('HD001', 1, 1, NULL, 1, GETDATE(), GETDATE(), 597000, N'Tại quầy', N'Đã thanh toán'),
                                                                                                         ('HD002', 2, 1, NULL, 2, GETDATE(), GETDATE(), 399000, N'Tại quầy', N'Đã thanh toán'),
                                                                                                         ('HD003', 1, 1, NULL, 1, DATEADD(DAY, -10, GETDATE()), DATEADD(DAY, -10, GETDATE()), 1200000, N'Trực tuyến', N'Chờ xác nhận'),
                                                                                                         ('HD004', 2, 1, NULL, 2, DATEADD(DAY, -8, GETDATE()), DATEADD(DAY, -8, GETDATE()), 800000, N'Tại quầy', N'Đang vận chuyển'),
                                                                                                         ('HD005', 1, 1, NULL, 3, DATEADD(DAY, -6, GETDATE()), DATEADD(DAY, -6, GETDATE()), 500000, N'Trực tuyến', N'Đã giao hàng'),
                                                                                                         ('HD006', 2, 1, NULL, 1, DATEADD(DAY, -4, GETDATE()), DATEADD(DAY, -4, GETDATE()), 1500000, N'Tại quầy', N'Đã hủy'),
                                                                                                         ('HD007', 1, 1, NULL, 2, DATEADD(DAY, -2, GETDATE()), DATEADD(DAY, -2, GETDATE()), 2000000, N'Trực tuyến', N'Chờ thanh toán'),
                                                                                                         ('HD008', 2, 1, NULL, 3, DATEADD(DAY, -1, GETDATE()), DATEADD(DAY, -1, GETDATE()), 300000, N'Tại quầy', N'Hoàn thành');

-- Chi tiết hóa đơn
INSERT INTO HoaDonCT (IdSPCT, IdHD, SoLuong, DonGia, ThanhTien, NgayTao, NgaySua) VALUES
                                                                                      (1, 1, 2, 199000, 398000, GETDATE(), GETDATE()),
                                                                                      (2, 1, 1, 299000, 299000, GETDATE(), GETDATE()),
                                                                                      (3, 2, 1, 399000, 399000, GETDATE(), GETDATE()),
                                                                                      (1, 3, 4, 199000, 796000, DATEADD(DAY, -10, GETDATE()), DATEADD(DAY, -10, GETDATE())),
                                                                                      (2, 3, 2, 299000, 598000, DATEADD(DAY, -10, GETDATE()), DATEADD(DAY, -10, GETDATE())),
                                                                                      (3, 4, 2, 399000, 798000, DATEADD(DAY, -8, GETDATE()), DATEADD(DAY, -8, GETDATE())),
                                                                                      (1, 5, 1, 199000, 199000, DATEADD(DAY, -6, GETDATE()), DATEADD(DAY, -6, GETDATE())),
                                                                                      (2, 5, 1, 299000, 299000, DATEADD(DAY, -6, GETDATE()), DATEADD(DAY, -6, GETDATE())),
                                                                                      (3, 5, 1, 399000, 399000, DATEADD(DAY, -6, GETDATE()), DATEADD(DAY, -6, GETDATE())),
                                                                                      (1, 6, 2, 199000, 398000, DATEADD(DAY, -4, GETDATE()), DATEADD(DAY, -4, GETDATE())),
                                                                                      (2, 6, 1, 299000, 299000, DATEADD(DAY, -4, GETDATE()), DATEADD(DAY, -4, GETDATE())),
                                                                                      (3, 7, 5, 399000, 1995000, DATEADD(DAY, -2, GETDATE()), DATEADD(DAY, -2, GETDATE())),
                                                                                      (1, 8, 1, 199000, 199000, DATEADD(DAY, -1, GETDATE()), DATEADD(DAY, -1, GETDATE()));


PRINT 'Đã tạo CSDL và import dữ liệu thành công!';
PRINT 'Tài khoản đăng nhập:';
PRINT 'Admin: admin / 123456';
PRINT 'Customer1: customer1 / 123456';
PRINT 'Customer2: customer2 / 123456';

-- Thêm cột IdPT cho HoaDonCT nếu còn thiếu
-- 1) Thêm cột + FK (nếu chưa có)
IF COL_LENGTH('HoaDonCT', 'IdPT') IS NULL
    ALTER TABLE HoaDonCT ADD IdPT INT NULL;

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name='PhuongThucThanhToan')
BEGIN
    CREATE TABLE PhuongThucThanhToan (
        IdPT INT IDENTITY(1,1) PRIMARY KEY,
        Ten NVARCHAR(50) NOT NULL
    );
    INSERT INTO PhuongThucThanhToan(Ten)
    VALUES (N'TIỀN MẶT'), (N'CHUYỂN KHOẢN'), (N'MOMO');
END;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name='FK_HoaDonCT_PhuongThucThanhToan')
BEGIN
    ALTER TABLE HoaDonCT
      ADD CONSTRAINT FK_HoaDonCT_PhuongThucThanhToan
      FOREIGN KEY (IdPT) REFERENCES PhuongThucThanhToan(IdPT);
END;

-- 2) Gán giá trị mặc định cho các dòng đang NULL (KHÔNG dùng alias)
UPDATE HoaDonCT
SET IdPT = (SELECT TOP 1 IdPT FROM PhuongThucThanhToan ORDER BY IdPT)
WHERE IdPT IS NULL;

