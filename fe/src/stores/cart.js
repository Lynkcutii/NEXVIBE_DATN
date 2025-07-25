import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'

export const useCartStore = defineStore('cart', () => {
  // 1. Khôi phục giỏ hàng từ localStorage khi store được khởi tạo
  const items = ref(JSON.parse(localStorage.getItem('nexvibe_cart')) || [])

  // 2. Theo dõi sự thay đổi của giỏ hàng và tự động lưu vào localStorage
  watch(items, (newCart) => {
    localStorage.setItem('nexvibe_cart', JSON.stringify(newCart))
  }, { deep: true }); // 'deep: true' để theo dõi cả thay đổi bên trong các object (ví dụ: số lượng)


  // --- CÁC HÀM VÀ COMPUTED PROPERTY GIỮ NGUYÊN ---
  
  const totalItems = computed(() => items.value.reduce((sum, item) => sum + item.quantity, 0))
  const subTotal = computed(() => items.value.reduce((sum, item) => sum + (item.price * item.quantity), 0))

  function addToCart(product, quantity = 1, variant = {}) {
    const uniqueId = product.id + '-' + (variant.size || '') + '-' + (variant.color || '');
    const existingItem = items.value.find(item => item.uniqueId === uniqueId);
    if (existingItem) {
      existingItem.quantity += quantity;
    } else {
      items.value.push({ ...product, quantity, variant, uniqueId });
    }
  }

  function removeFromCart(uniqueId) {
    items.value = items.value.filter(item => item.uniqueId !== uniqueId)
  }

  function clearCart() {
    items.value = []
  }

  return { items, totalItems, subTotal, addToCart, removeFromCart, clearCart }
})