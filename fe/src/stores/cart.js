import { defineStore } from 'pinia';

export const useCartStore = defineStore('cart', {
  state: () => ({
    cart: null,
    items: [],
    API_BASE_URL: 'http://localhost:8080/client/api',
  }),

  actions: {
    async fetchCart() {
      try {
        const response = await fetch(`${this.API_BASE_URL}/giohang/byTaiKhoan`, {
          headers: {
            'Content-Type': 'application/json',
          },
          credentials: 'include', // Gửi cookie JSESSIONID
        });
        if (!response.ok) {
          throw new Error('Không thể lấy giỏ hàng. Vui lòng đăng nhập.');
        }
        this.cart = await response.json();
        const itemsResponse = await fetch(`${this.API_BASE_URL}/giohangct/byGioHang/${this.cart.idGH}`, {
          headers: {
            'Content-Type': 'application/json',
          },
          credentials: 'include',
        });
        if (!itemsResponse.ok) {
          throw new Error('Không thể lấy chi tiết giỏ hàng');
        }
        this.items = await itemsResponse.json();
      } catch (error) {
        console.error('Error fetching cart:', error.message);
        throw new Error(error.message);
      }
    },

    async addToCart(product, quantity, variant) {
      try {
        const variantsResponse = await fetch(`${this.API_BASE_URL}/sanphamchitiet/bySanPham/${product.idSP}`, {
          headers: {
            'Content-Type': 'application/json',
          },
          credentials: 'include',
        });
        if (!variantsResponse.ok) {
          throw new Error('Không thể lấy biến thể sản phẩm');
        }
        const variants = await variantsResponse.json();
        const selectedVariant = variants.find(
          (v) => v.tenMauSac === variant.color && v.tenKichThuoc === variant.size
        );
        if (!selectedVariant) {
          throw new Error('Biến thể sản phẩm không tồn tại');
        }
        if (!selectedVariant.idSpct) {
          throw new Error('ID sản phẩm chi tiết không tồn tại trong biến thể');
        }
        console.log('Request body:', {
          idSpct: selectedVariant.idSpct,
          soLuong: quantity,
          mauSac: variant.color,
          kichThuoc: variant.size,
        });
        const response = await fetch(`${this.API_BASE_URL}/giohang/addToCart`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          credentials: 'include', // Gửi cookie JSESSIONID
          body: JSON.stringify({
            idSpct: selectedVariant.idSpct,
            soLuong: quantity,
            mauSac: variant.color,
            kichThuoc: variant.size,
          }),
        });
        if (!response.ok) {
          const errorData = await response.json();
          throw new Error(errorData.error || 'Không thể thêm sản phẩm vào giỏ hàng');
        }
        await this.fetchCart();
      } catch (error) {
        console.error('Error adding to cart:', error.message);
        throw new Error(error.message);
      }
    },
  },
});