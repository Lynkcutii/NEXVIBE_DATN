<template>
  <div class="momo-return-container" :class="{'success': isSuccess, 'error': isError}">
    <div v-if="isLoading">
      <h2>Đang xử lý thanh toán MoMo...</h2>
      <p>Vui lòng không đóng cửa sổ này.</p>
      <div class="spinner"></div>
    </div>
    <div v-else-if="isSuccess">
      <div class="success-icon">✓</div>
      <h2>Thanh toán thành công!</h2>
      <p>Đơn hàng đã được thanh toán qua MoMo.</p>
      <p class="small">Trang này sẽ tự đóng sau {{countdown}} giây...</p>
    </div>
    <div v-else-if="isError">
      <div class="error-icon">✗</div>
      <h2>Thanh toán thất bại</h2>
      <p>{{errorMessage || 'Có lỗi xảy ra trong quá trình thanh toán.'}}</p>
      <p class="small">Trang này sẽ tự đóng sau {{countdown}} giây...</p>
    </div>
  </div>
</template>

<script>
import { onMounted, ref, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth'; // Giả sử store auth của bạn ở đây

export default {
  name: 'MoMoReturn',
  setup() {
    const router = useRouter();
    const authStore = useAuthStore();
    const isLoading = ref(true);
    const isSuccess = ref(false);
    const isError = ref(false);
    const errorMessage = ref('');
    const countdown = ref(3);
    let countdownTimer = null;
    let closeTimer = null;

    // Hàm kiểm tra trạng thái thanh toán
    const checkPaymentStatus = async (orderId) => {
      try {
        const response = await fetch(`http://localhost:8080/admin/api/momo/check-status/${orderId}`, {
          method: 'GET',
          credentials: 'include'
        });
        
        if (!response.ok) {
          throw new Error('Không thể kiểm tra trạng thái thanh toán');
        }
        
        const data = await response.json();
        console.log('[MoMoReturn] Kết quả kiểm tra trạng thái:', data);
        
        if (data.transactionStatus === 'SUCCESS') {
          isSuccess.value = true;
          startCountdown();
        } else if (data.transactionStatus === 'FAILED') {
          isError.value = true;
          errorMessage.value = data.message || 'Thanh toán thất bại';
          startCountdown();
        } else {
          // Vẫn đang chờ kết quả, chuyển hướng về POS để tiếp tục polling
          redirectToPOS(orderId);
        }
      } catch (error) {
        console.error('[MoMoReturn] Lỗi kiểm tra trạng thái:', error);
        redirectToPOS(orderId);
      } finally {
        isLoading.value = false;
      }
    };
    
    // Hàm đếm ngược và đóng trang
    const startCountdown = () => {
      countdownTimer = setInterval(() => {
        countdown.value--;
        if (countdown.value <= 0) {
          clearInterval(countdownTimer);
          window.close();
          
          // Nếu window.close() không hoạt động (trình duyệt chặn), chuyển hướng về POS
          closeTimer = setTimeout(() => {
            redirectToPOS();
          }, 500);
        }
      }, 1000);
    };
    
    // Hàm chuyển hướng về trang POS
    const redirectToPOS = (orderId) => {
      if (orderId) {
        localStorage.setItem('momo_last_order_id', orderId);
      }
      
      // Lấy URL trở về từ localStorage hoặc mặc định về trang POS
      const returnUrl = localStorage.getItem('pos_return_url') || '/admin/pos';
      console.log(`[MoMoReturn] Chuyển về ${returnUrl}`);
      
      if (authStore.isAuthenticated && authStore.isAdmin()) {
        console.log('[MoMoReturn] Đã xác thực admin. Chuyển về trang quản lý.');
        window.location.href = returnUrl;
      } else {
        console.log('[MoMoReturn] Chưa xác thực. Chuyển về trang đăng nhập.');
        router.replace({ path: '/login', query: { redirect: returnUrl } });
      }
    };

    onMounted(() => {
      console.log('[MoMoReturn] Trang xử lý redirect được tải.');
      const urlParams = new URLSearchParams(window.location.search);
      const orderId = urlParams.get('orderId');
      const resultCode = urlParams.get('resultCode');
      const message = urlParams.get('message');
      
      console.log(`[MoMoReturn] Lấy được orderId: ${orderId}, resultCode: ${resultCode}, message: ${message}`);

      if (orderId) {
        // Kiểm tra kết quả từ URL trước
        if (resultCode === '0') {
          isSuccess.value = true;
          isLoading.value = false;
          startCountdown();
        } else if (resultCode && resultCode !== '0') {
          isError.value = true;
          errorMessage.value = message || 'Thanh toán thất bại';
          isLoading.value = false;
          startCountdown();
        } else {
          // Không có resultCode trong URL, kiểm tra từ API
          checkPaymentStatus(orderId);
        }

      } else {
        console.error('[MoMoReturn] Không tìm thấy orderId. Chuyển về trang chủ.');
        isError.value = true;
        errorMessage.value = 'Không tìm thấy thông tin đơn hàng';
        isLoading.value = false;
        startCountdown();
      }
    });
    
    // Dọn dẹp khi component bị hủy
    onUnmounted(() => {
      if (countdownTimer) clearInterval(countdownTimer);
      if (closeTimer) clearTimeout(closeTimer);
    });

    return {
      isLoading,
      isSuccess,
      isError,
      errorMessage,
      countdown
    };
  }
};
</script>

<style scoped>
.momo-return-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  text-align: center;
}
.spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border-left-color: #007bff;
  animation: spin 1s ease infinite;
  margin-top: 20px;
}
.success-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background-color: #28a745;
  color: white;
  font-size: 48px;
  line-height: 80px;
  margin: 0 auto 20px;
}
.error-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background-color: #dc3545;
  color: white;
  font-size: 48px;
  line-height: 80px;
  margin: 0 auto 20px;
}
.small {
  font-size: 14px;
  color: #6c757d;
  margin-top: 20px;
}
.momo-return-container.success {
  background-color: #d4edda;
}
.momo-return-container.error {
  background-color: #f8d7da;
}
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
