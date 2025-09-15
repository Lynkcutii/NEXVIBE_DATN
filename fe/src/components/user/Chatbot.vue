<template>
  <div class="chatbot-container">
    <!-- Nút bubble để mở/đóng chat -->
    <div class="chat-bubble" @click="toggleChat">
      <transition name="icon-fade" mode="out-in">
        <i v-if="!isChatOpen" class="fas fa-comment-dots"></i>
        <i v-else class="fas fa-times"></i>
      </transition>
    </div>

    <!-- Cửa sổ chat -->
    <div class="chat-window" :class="{ 'open': isChatOpen }">
      <div class="chat-header">
        <div class="d-flex align-items-center">
          <img :src="botAvatarUrl" alt="Bot Avatar" class="bot-avatar-img">
          <div>
            <h5 class="mb-0">UIA NexVibe</h5>
            <small class="text-success"><i class="fas fa-circle me-1"></i> Online</small>
          </div>
        </div>
        <button @click="toggleChat" class="btn-close-chat">&times;</button>
      </div>

      <div class="chat-body" ref="chatBodyRef">
        <!-- Lặp qua các tin nhắn -->
        <div v-for="(message, index) in messages" :key="index" class="message" :class="`message-${message.sender}`">
          <!-- Render text bình thường -->
          <div v-if="message.text" class="message-content" v-html="formatMessage(message.text)"></div>

          <!-- Render thẻ sản phẩm nếu có -->
          <div v-if="message.products && message.products.length > 0" class="product-suggestions">
            <div v-for="product in message.products" :key="product.idSP" class="product-card">
              <img :src="product.imageUrl || 'https://placehold.co/150'" :alt="product.tenSP">
              <div class="product-info">
                <h6 class="product-name">{{ product.tenSP }}</h6>
                <p class="product-price">{{ formatPriceRange(product.minPrice, product.maxPrice) }}</p>
                <button class="btn btn-sm btn-primary" @click="viewProduct(product)">Xem SP</button>
              </div>
            </div>
          </div>
        </div>

        <!-- Hiển thị "Bot is typing..." -->
        <div v-if="isTyping" class="message message-bot">
          <div class="message-content typing-indicator"><span></span><span></span><span></span></div>
        </div>
      </div>

      <div class="chat-footer">
        <form @submit.prevent="sendMessage">
          <input type="text" v-model="userInput" placeholder="Nhập tin nhắn..." :disabled="isTyping" ref="inputRef" />
          <button type="submit" :disabled="isTyping || !userInput.trim()"><i class="fas fa-paper-plane"></i></button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';
import axios from 'axios';
import { marked } from 'marked';
const botAvatarUrl = ref('/img/logo/botavt.jpg');
const router = useRouter();
const toast = useToast();

const isChatOpen = ref(false);
const userInput = ref('');
const messages = ref([{ sender: 'bot', text: 'Chào bạn! Tôi có thể giúp gì cho bạn?' }]);
const isTyping = ref(false);
const chatBodyRef = ref(null);
const inputRef = ref(null);
const API_BASE_URL = 'http://localhost:8080';

const toggleChat = () => { isChatOpen.value = !isChatOpen.value; };

watch(isChatOpen, (newValue) => {
  if (newValue) {
    nextTick(() => {
      inputRef.value?.focus();
    });
  }
});

const scrollToBottom = () => {
  nextTick(() => {
    if (chatBodyRef.value) {
      chatBodyRef.value.scrollTop = chatBodyRef.value.scrollHeight;
    }
  });
};

const sendMessage = async () => {
  const userText = userInput.value.trim();
  if (!userText || isTyping.value) return;

  messages.value.push({ sender: 'user', text: userText });
  userInput.value = '';
  scrollToBottom();
  isTyping.value = true;

  try {
    const response = await axios.post(`${API_BASE_URL}/api/chatbot/ask`, { question: userText });
    const botResponse = response.data; // Response giờ là một object ChatbotResponseDTO

    setTimeout(() => {
      isTyping.value = false;
      // Thêm tin nhắn mới vào mảng messages, bao gồm cả text và products
      messages.value.push({
        sender: 'bot',
        text: botResponse.answer,
        products: botResponse.products
      });
      scrollToBottom();
    }, 800);

  } catch (error) {
    console.error("Lỗi khi gọi API chatbot:", error);
    isTyping.value = false;
    messages.value.push({ sender: 'bot', text: 'Xin lỗi, tôi đang gặp sự cố kết nối. Vui lòng thử lại sau.' });
    scrollToBottom();
  }
};

// Hàm để render Markdown (in đậm, gạch đầu dòng)
const formatMessage = (text) => {
  if (!text) return '';
  return marked(text, { breaks: true });
};

// Hàm định dạng khoảng giá
const formatPriceRange = (min, max) => {
  if (min === max) {
    return `${min.toLocaleString('vi-VN')}đ`;
  }
  return `Từ ${min.toLocaleString('vi-VN')}đ`;
};

// Hàm xử lý khi nhấn nút "Xem SP"
const viewProduct = (product) => {
  // Chuyển hướng người dùng đến trang chi tiết sản phẩm
  router.push(`/products/${product.idSP}`);
  toggleChat(); // Tự động đóng cửa sổ chat
};
</script>

<style scoped>
.chatbot-container {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 1050;
}

.chat-bubble {
  width: 60px;
  height: 60px;
  background-color: var(--bs-primary);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.chat-bubble:hover {
  transform: scale(1.1);
}

.chat-window {
  width: 370px;
  height: 550px;
  max-height: calc(100vh - 100px);
  background: white;
  border-radius: 15px;
  box-shadow: 0 5px 25px rgba(0, 0, 0, 0.2);
  position: absolute;
  bottom: 80px;
  right: 0;
  display: flex;
  flex-direction: column;
  transform: scale(0);
  opacity: 0;
  transform-origin: bottom right;
  transition: transform 0.3s ease, opacity 0.3s ease;
}

.chat-window.open {
  transform: scale(1);
  opacity: 1;
}

.chat-header {
  padding: 1rem;
  background: #f8f9fa;
  border-bottom: 1px solid #dee2e6;
  border-radius: 15px 15px 0 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-header h5 {
  margin: 0;
  font-weight: 600;
}

.bot-avatar-img {
  width: 40px;
  height: 40px;
  border-radius: 50%; /* Bo tròn ảnh */
  object-fit: cover;    /* Đảm bảo ảnh lấp đầy hình tròn mà không bị méo */
  margin-right: 10px;
  border: 2px solid #e9ecef; /* (Tùy chọn) Thêm viền cho đẹp */
}

.chat-header small {
  font-size: 0.75rem;
}

.chat-header small i {
  font-size: 0.5rem;
  vertical-align: middle;
}

.btn-close-chat {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #6c757d;
  padding: 0;
  line-height: 1;
}

.chat-body {
  flex-grow: 1;
  padding: 1rem;
  overflow-y: auto;
  background-color: #fff;
}

.message {
  margin-bottom: 1rem;
  display: flex;
  flex-direction: column;
  max-width: 90%;
}

.message-content {
  padding: 0.75rem 1rem;
  border-radius: 1.25rem;
  word-wrap: break-word;
}

.message-user {
  align-self: flex-end;
  margin-left: auto;
}

.message-user .message-content {
  background-color: var(--bs-primary);
  color: white;
  border-bottom-right-radius: 0.375rem;
}

.message-bot {
  align-self: flex-start;
}

.message-bot .message-content {
  background-color: #e9ecef;
  color: #212529;
  border-bottom-left-radius: 0.375rem;
}

.chat-footer {
  padding: 0.75rem;
  border-top: 1px solid #dee2e6;
  background-color: #f8f9fa;
}

.chat-footer form {
  display: flex;
}

.chat-footer input {
  flex-grow: 1;
  border: 1px solid #ced4da;
  border-radius: 20px;
  padding: 10px 15px;
  outline: none;
}

.chat-footer input:focus {
  border-color: var(--bs-primary);
  box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}

.chat-footer button {
  background: var(--bs-primary);
  color: white;
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  min-width: 40px;
  margin-left: 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-fade-enter-active,
.icon-fade-leave-active {
  transition: opacity 0.2s;
}

.icon-fade-enter-from,
.icon-fade-leave-to {
  opacity: 0;
}

.typing-indicator span {
  height: 8px;
  width: 8px;
  background-color: #9E9E9E;
  border-radius: 50%;
  display: inline-block;
  animation: wave 1.3s infinite;
}

.typing-indicator span:nth-of-type(2) {
  animation-delay: -1.1s;
}

.typing-indicator span:nth-of-type(3) {
  animation-delay: -0.9s;
}

@keyframes wave {

  0%,
  60%,
  100% {
    transform: initial;
  }

  30% {
    transform: translateY(-8px);
  }
}

/* CSS mới cho thẻ sản phẩm */
.product-suggestions {
  margin-top: 10px;
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding-bottom: 10px;
}

.product-card {
  flex: 0 0 150px;
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.product-card img {
  width: 100%;
  height: 120px;
  object-fit: cover;
}

.product-info {
  padding: 8px;
}

.product-name {
  font-size: 0.9rem;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 4px;
}

.product-price {
  font-size: 0.9rem;
  color: var(--bs-danger);
  font-weight: bold;
}

.product-info .btn {
  width: 100%;
  margin-top: 8px;
  font-size: 0.8rem;
}
</style>